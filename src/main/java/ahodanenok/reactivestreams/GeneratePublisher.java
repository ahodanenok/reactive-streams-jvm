package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class GeneratePublisher<T> implements Publisher<T> {

    private final T initial;
    private final BiConsumer<T, ValueCallback<T>> generator;

    public GeneratePublisher(T initial, BiConsumer<T, ValueCallback<T>> generator) {
        Objects.requireNonNull(generator, "generator");
        this.initial = initial;
        this.generator = generator;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new GeneratePublisherSubscription<>(subscriber, initial, generator));
    }

    static class GeneratePublisherSubscription<T> extends SyncPublisherSubscription<T> implements ValueCallback<T> {

        private final BiConsumer<T, ValueCallback<T>> generator;

        private volatile T prev;
        private volatile boolean allowNext;

        GeneratePublisherSubscription(Subscriber<? super T> subscriber, T initial, BiConsumer<T, ValueCallback<T>> generator) {
            super(subscriber);
            this.prev = initial;
            this.generator = generator;
        }

        @Override
        protected boolean handleRequest(long n) {
            try {
                while (n > 0 && !cancelled) {
                    allowNext = true;
                    generator.accept(prev, this);
                    n--;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                signalError(e);
            }

            return !cancelled;
        }

        @Override
        public void signalValue(T value) {
            if (cancelled) {
                return;
            }

            if (!allowNext) {
                downstream.onError(new IllegalStateException("Multiple signals aren't allowed"));
                return;
            }

            Objects.requireNonNull(value, "value");
            allowNext = false;
            prev = value;
            downstream.onNext(value);
        }

        @Override
        public void signalError(Throwable error) {
            if (cancelled) {
                return;
            }

            Objects.requireNonNull(error, "error");
            try {
                downstream.onError(error);
            } finally {
                cancel();
            }
        }

        @Override
        public void signalComplete() {
            if (cancelled) {
                return;
            }

            try {
                downstream.onComplete();
            } finally {
                cancel();
            }
        }
    }
}

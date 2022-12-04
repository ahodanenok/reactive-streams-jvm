package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.reactivestreams.Subscriber;

public class GeneratePublisher<T> extends AbstractPublisher<T> {

    private final T initial;
    private final BiConsumer<T, ValueCallback<T>> generator;

    public GeneratePublisher(T initial, BiConsumer<T, ValueCallback<T>> generator) {
        Objects.requireNonNull(generator, "generator");
        this.initial = initial;
        this.generator = generator;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new GeneratePublisherSubscription<>(subscriber, initial, generator));
    }

    static class GeneratePublisherSubscription<T> extends AbstractSubscription<T> implements ValueCallback<T> {

        private final BiConsumer<T, ValueCallback<T>> generator;
        private final RequestedSupport requestedSupport;

        private T prev;
        private boolean allowNext;

        GeneratePublisherSubscription(Subscriber<? super T> subscriber, T initial, BiConsumer<T, ValueCallback<T>> generator) {
            super(subscriber);
            this.prev = initial;
            this.generator = generator;
            this.requestedSupport = new RequestedSupport(this::next);
        }

        @Override
        protected void onRequest(long n) {
            requestedSupport.request(n);
        }

        @Override
        protected void onDispose() {
            requestedSupport.dispose();
        }

        @Override
        public void resolve(T value) {
            if (isCancelled()) {
                return;
            }

            if (!allowNext) {
                error(new IllegalStateException("Multiple resolve calls aren't allowed"));
                return;
            }

            allowNext = false;
            prev = value;
            value(value);
        }

        private void next() {
            allowNext = true;
            try {
                generator.accept(prev, this);
            } catch (Throwable e) {
                e.printStackTrace();
                error(e);
            }
        }
    }
}

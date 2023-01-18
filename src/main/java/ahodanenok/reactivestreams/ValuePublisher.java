package ahodanenok.reactivestreams;

import java.util.Objects;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class ValuePublisher<T> implements Publisher<T> {

    private final T value;

    public ValuePublisher(T value) {
        Objects.requireNonNull(value, "value");
        this.value = value;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new ValuePublisherSubscription<>(subscriber, value));
    }

    static class ValuePublisherSubscription<T> extends SyncPublisherSubscription<T> {

        private final T value;

        ValuePublisherSubscription(Subscriber<? super T> subscriber, T value) {
            super(subscriber);
            this.value = value;
        }

        @Override
        protected boolean handleRequest(long n) {
            downstream.onNext(value);
            downstream.onComplete();

            return false;
        }
    }
}

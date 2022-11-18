package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import org.reactivestreams.Subscriber;

public class ValuePublisher<T> extends AbstractPublisher<T> {

    private final T value;

    public ValuePublisher(T value) {
        Objects.requireNonNull(value, "value");
        this.value = value;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new ValuePublisherSubscription<>(subscriber, value));
    }

    static class ValuePublisherSubscription<T> extends AbstractSubscription<T> {

        private final T value;

        ValuePublisherSubscription(Subscriber<? super T> subscriber, T value) {
            super(subscriber);
            this.value = value;
        }

        @Override
        protected void onRequest(long n) {
            complete(value);
        }
    }
}

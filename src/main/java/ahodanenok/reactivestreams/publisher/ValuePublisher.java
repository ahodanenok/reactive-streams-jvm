package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import org.reactivestreams.Subscriber;

public class ValuePublisher<T> extends AbstractPublisherV2<T> {

    private final T value;
    private boolean signalled;

    public ValuePublisher(T value) {
        Objects.requireNonNull(value, "value");
        this.value = value;
    }

    @Override
    protected void onRequest(long n) {
        if (signalled) {
            return;
        }

        signalled = true;
        signalNext(value);
        signalComplete();
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new ValuePublisherSubscription<>(subscriber, value));
    }

    static class ValuePublisherSubscription<T> extends AbstractSubscription<T> {

        private final T value;
        private boolean signalled;

        ValuePublisherSubscription(Subscriber<? super T> subscriber, T value) {
            super(subscriber);
            this.value = value;
        }

        @Override
        protected void onRequest(long n) {
            if (!signalled) {
                signalled = true;
                complete(value);
            }
        }
    }*/
}

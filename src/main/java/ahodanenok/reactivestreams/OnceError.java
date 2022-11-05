package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceError<T> extends Once<T> {

    private Throwable throwable;

    public OnceError(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        OnceErrorSubscription<T> subscription = new OnceErrorSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        if (!subscription.isCancelled()) {
            try {
                subscriber.onError(throwable);
            } finally {
                subscription.cancel();
            }
        }
    }

    private static class OnceErrorSubscription<T> extends OnceSubscription<T> {

        OnceErrorSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public T requestValue() {
            throw new IllegalStateException("No value here!");
        }
    }
} 

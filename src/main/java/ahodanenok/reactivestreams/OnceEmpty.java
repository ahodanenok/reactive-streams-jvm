package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceEmpty<T> extends Once<T> {

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        OnceEmptySubscription<T> subscription = new OnceEmptySubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        if (!subscription.isCancelled()) {
            try {
                subscriber.onComplete();
            } finally {
                subscription.cancel();
            }
        }
    }

    private static class OnceEmptySubscription<T> extends OnceSubscription<T> {

        OnceEmptySubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public T requestValue() {
            throw new IllegalStateException("No value here!");
        }
    }
} 

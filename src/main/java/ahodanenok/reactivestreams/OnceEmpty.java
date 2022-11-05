package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceEmpty<T> extends Once<T> {

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        OnceEmptSubscription<T> subscription = new OnceEmptSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        if (!subscription.isCancelled()) {
            subscriber.onComplete();
        }
    }

    private static class OnceEmptSubscription<T> extends OnceSubscription<T> {

        OnceEmptSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public T requestValue() {
            throw new IllegalStateException("No value here!");
        }
    }
} 

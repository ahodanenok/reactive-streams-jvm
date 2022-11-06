package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceEmpty<T> extends Once<T> {

    @Override
    public void doSubscribe(Subscriber<? super T> subscriber) {
        OnceEmptySubscription<T> subscription = new OnceEmptySubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        subscription.complete();
    }

    private static class OnceEmptySubscription<T> extends OnceSubscription<T> {

        OnceEmptySubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
} 

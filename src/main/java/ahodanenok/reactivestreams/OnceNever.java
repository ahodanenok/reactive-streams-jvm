package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceNever<T> extends Once<T> {

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        OnceNeverSubscription<T> subscription = new OnceNeverSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        // todo: hmm, i shouldn't do it this way
        subscription.cancel(); // todo: finally?
    }

    private static class OnceNeverSubscription<T> extends OnceSubscription<T> {

        OnceNeverSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
} 

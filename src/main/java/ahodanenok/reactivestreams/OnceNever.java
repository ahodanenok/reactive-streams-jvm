package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceNever<T> extends Once<T> {

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new OnceNeverSubscription<>(subscriber));
    }

    private static class OnceNeverSubscription<T> extends OnceSubscription<T> {

        OnceNeverSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
} 

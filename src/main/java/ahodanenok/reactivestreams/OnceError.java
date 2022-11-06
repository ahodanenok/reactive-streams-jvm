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
        // todo: i can use subscription.complete(error) to singal onError
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
    }
} 

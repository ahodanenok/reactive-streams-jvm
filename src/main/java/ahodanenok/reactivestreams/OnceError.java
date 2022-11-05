package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceError<T> extends Once<T> {

    private Throwable throwable;

    public OnceError(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        OnceErrorSub<T> sub = new OnceErrorSub<>(s);
        s.onSubscribe(sub);
        if (!sub.cancelled) {
            s.onError(throwable);
        }
    }

    private static class OnceErrorSub<T> implements Subscription {

        Subscriber<? super T> subscriber;
        boolean cancelled;

        OnceErrorSub(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException());
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
        }
    }
} 

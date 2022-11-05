package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceEmpty<T> extends Once<T> {

    @Override
    public void subscribe(Subscriber<? super T> s) {
        OnceEmptSub<T> sub = new OnceEmptSub<>(s);
        s.onSubscribe(sub);
        if (!sub.cancelled) {
            s.onComplete();
        }
    }

    private static class OnceEmptSub<T> implements Subscription {

        Subscriber<? super T> subscriber;
        boolean cancelled;

        OnceEmptSub(Subscriber<? super T> subscriber) {
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

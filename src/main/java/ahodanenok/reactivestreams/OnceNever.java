package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceNever<T> extends Once<T> {

    @Override
    public void subscribe(Subscriber<? super T> s) {
        s.onSubscribe(new OnceNeverSub<T>(s));
    }

    private static class OnceNeverSub<T> implements Subscription {

        private Subscriber<? super T> subscriber;

        OnceNeverSub(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException());
            }
        }

        @Override
        public void cancel() { }
    }
} 

package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceNever<T> extends Once<T> {

    @Override
    public void subscribe(Subscriber<? super T> s) {
        s.onSubscribe(new OnceNeverSubscription<T>(s));
    }

    private static class OnceNeverSubscription<T> extends OnceSubscription<T> {

        OnceNeverSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public T requestValue() {
            throw new IllegalStateException("No value here!");
        }
    }
} 

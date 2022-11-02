package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceValue<T> extends Once<T> {

    private final T value;

    public OnceValue(T value) {
        this.value = value;
    }

    public void subscribe(Subscriber<? super T> s) {
        // 2.12, 1.10 - each subsriber must be different,
        // can't subscribe the same subscriber multiple times

        s.onSubscribe(new OnceValueSub(s));
    }

    class OnceValueSub implements Subscription {

        private Subscriber<? super T> subscriber;
        private volatile boolean cancelled;

        OnceValueSub(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException(n + ""));
                return;
            }

            if (!cancelled) {
                // todo: check 2.13
                // todo: if onNext failed should onError be signalled?
                subscriber.onNext(value);
            }

            if (!cancelled) {
                // todo: check 2.13
                // todo: if onComplete failed should onError be signalled?
                subscriber.onComplete();
                cancel();
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
        }
    }
}

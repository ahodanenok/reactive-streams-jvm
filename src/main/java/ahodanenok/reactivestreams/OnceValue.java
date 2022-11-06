package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OnceValue<T> extends Once<T> {

    private final T value;

    public OnceValue(T value) {
        this.value = value;
    }

    public void subscribe(Subscriber<? super T> subscriber) {
        // 2.12, 1.10 - each subsriber must be different,
        // can't subscribe the same subscriber multiple times

        subscriber.onSubscribe(new OnceValueSubscription(subscriber));
    }

    class OnceValueSubscription extends OnceSubscription<T> {

        OnceValueSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        protected void onRequest() {
            complete(value);
        }
    }
}

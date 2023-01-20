package ahodanenok.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EmptySubscription<T> implements Subscription {

    protected final Subscriber<? super T> downstream;
    protected volatile boolean cancelled;

    public EmptySubscription(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber is null");
        }

        this.downstream = subscriber;
    }

    @Override
    public void request(long n) {
        if (n <= 0) {
            cancel();
            downstream.onError(new IllegalArgumentException("requested amount must be > 0: " + n));
        }

        // no-op
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}

package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class AbstractSubscription<T> implements Subscription {

    private final Subscriber<? super T> subscriber;
    private volatile boolean cancelled;

    protected AbstractSubscription(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {
        if (cancelled) {
            return; // 3.6
        }

        if (n <= 0) { // 3.9
            error(new IllegalArgumentException("Requested amount must be positive: " + n));
            return;
        }

        try {
            onRequest(n);
        } catch (Throwable e) {
            e.printStackTrace();
            // 3.16
            cancel(); // 2.13
            // todo: throw?
        }
    }

    protected void onRequest(long n) { };

    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        // todo: thread safety
        if (!cancelled) { // 3.7
            cancelled = true;
            onCancel();
        }
    }

    protected void onCancel() { }

    public void value(T value) {
        if (cancelled) {
            return; // 1.7, 1.8
        }

        if (value == null) {
            // todo: are nulls allowed?
            throw new NullPointerException();
        }
        
        try {
            subscriber.onNext(value);
        } catch (Throwable e) {
            cancel();
            throw e; // todo: looks like wrong behavior, check it
        }
    }

    public void complete(T value) {
        value(value);
        complete();
    }

    public void complete() {
        if (cancelled) {
            return; // 1.7, 1.8
        }

        try {
            subscriber.onComplete();
        } finally {
            cancel(); // 2.4
        }
    }

    public final void error(Throwable e) {
        if (cancelled) {
            return; // 1.7, 1.8
        }

        try {
            subscriber.onError(e);
        } finally {
            cancel(); // 2.4
        }
    }
}

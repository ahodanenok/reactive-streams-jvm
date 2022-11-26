package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class AbstractSubscription<T> implements Subscription {

    private final Subscriber<? super T> subscriber;
    private volatile boolean cancelled;

    protected AbstractSubscription(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
    }

    public final void init() {
        subscriber.onSubscribe(this);

        try {
            onInit();
        } catch (Throwable e) {
            e.printStackTrace();
            error(e);
        }
    }

    protected void onInit() { }

    @Override
    public final void request(long n) {
        if (cancelled) {
            return;
        }

        if (n <= 0) {
            error(new IllegalArgumentException("Requested amount must be positive: " + n));
            return;
        }

        try {
            onRequest(n);
        } catch (Throwable e) {
            e.printStackTrace();
            cancel();
        }
    }

    protected void onRequest(long n) { };

    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void cancel() {
        // todo: thread safety
        if (!cancelled) {
            cancelled = true;
            try {
                onCancel();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected void onCancel() { }

    public final void value(T value) {
        if (cancelled) {
            return;
        }

        if (value == null) {
            cancel();
            throw new NullPointerException("value");
        }

        try {
            subscriber.onNext(value);
        } catch (Throwable e) {
            e.printStackTrace();
            cancel();
        }
    }

    public final void complete(T value) {
        value(value);
        complete();
    }

    public final void complete() {
        if (cancelled) {
            return;
        }

        try {
            subscriber.onComplete();
        } finally {
            cancel();
        }
    }

    public final void error(Throwable e) {
        if (cancelled) {
            return;
        }

        if (e == null) {
            cancel();
            throw new NullPointerException("value");
        }

        try {
            subscriber.onError(e);
        } finally {
            cancel();
        }
    }
}

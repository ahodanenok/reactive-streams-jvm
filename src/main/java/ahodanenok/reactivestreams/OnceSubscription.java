package ahodanenok.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class OnceSubscription<T> implements Subscription {

    private final Subscriber<? super T> subscriber;
    private boolean cancelled;

    protected OnceSubscription(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public final void request(long n) {
        if (cancelled) {
            return;
        }

        if (n <= 0) {
            complete(new IllegalArgumentException("Requested amount must be positive: " + n));
        }

        try {
            onRequest();
        } catch (Throwable e) {
            // 2.13 - if violated then this subscription will be cancelled
            cancel();
            throw e;
        }
    }

    protected void onRequest() { };

    protected final void complete(Throwable e) {
        if (cancelled) {
            return;
        }

        try {
            subscriber.onError(e);
        } finally {
            cancel();
        }
    }

    protected final void complete(T value) {
        if (cancelled) {
            return;
        }

        if (value == null) {
            // todo: are nulls allowed?
            throw new NullPointerException();
        }

        try {
            subscriber.onNext(value);
            complete();
        } finally {
            cancel();
        }
    }

    protected final void complete() {
        if (cancelled) {
            return;
        }

        try {
            subscriber.onComplete();
        } finally {
            cancel();
        }
    }

    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void cancel() {
        cancelled = true;
    }
}

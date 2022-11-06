package ahodanenok.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class OnceSubscription<T> implements Subscription {

    private final Subscriber<? super T> subscriber;
    private volatile boolean cancelled; // 3.5

    protected OnceSubscription(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public final void request(long n) {
        if (cancelled) {
            return; // 3.6
        }

        if (n <= 0) { // 3.9
            complete(new IllegalArgumentException("Requested amount must be positive: " + n));
        }

        try {
            onRequest();
        } catch (Throwable e) {
            // 3.16
            cancel(); // 2.13
        }
    }

    protected void onRequest() { };

    protected final void complete(Throwable e) {
        if (cancelled) {
            return; // 1.7, 1.8
        }

        try {
            subscriber.onError(e);
        } finally {
            cancel(); // 2.4
        }
    }

    protected final void complete(T value) {
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
            // it is expected that the subscriber will be wrapped by SubscriberWrapper
            // where the subscription will be cancelled in case of error, so maybe
            // there is no need to cancel it here
            cancel();
            throw e;
        }

        complete();
    }

    protected final void complete() {
        if (cancelled) {
            return; // 1.7, 1.8
        }

        try {
            subscriber.onComplete();
        } finally {
            cancel(); // 2.4
        }
    }

    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void cancel() {
        cancelled = true; // 3.7, 3.16
    }
}

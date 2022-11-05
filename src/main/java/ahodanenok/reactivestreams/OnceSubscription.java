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

        try {
            doRequest(n);
        } finally {
            // 2.13 - if violated then this subscription will be cancelled
            cancel();
        }
    }

    private void doRequest(long n) {
        if (n <= 0) {
            subscriber.onError(
                new IllegalArgumentException("Requested amount must be positive: " + n));
        } else {
            T value;
            try {
                value = requestValue();
            } catch (Throwable e) {
                e.printStackTrace(); // todo: log
                subscriber.onError(e);
                return;
            }

            if (value == null) {
                // todo: are nulls allowed?
                throw new NullPointerException();
            }

            subscriber.onNext(value);
            if (!cancelled) {
                subscriber.onComplete();
            }
        }
    }

    protected abstract T requestValue();

    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void cancel() {
        cancelled = true;
    }
}

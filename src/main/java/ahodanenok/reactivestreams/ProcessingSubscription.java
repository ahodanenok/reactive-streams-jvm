package ahodanenok.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class ProcessingSubscription<T, R> implements Subscriber<T> {

    protected final Subscriber<? super R> downstream;

    protected volatile Subscription upstream;
    protected volatile boolean cancelled;

    public ProcessingSubscription(Subscriber<? super R> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber is null");
        }

        this.downstream = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        if (subscription == null) {
            throw new NullPointerException("subscription is null");
        }

        this.upstream = subscription;
        downstream.onSubscribe(new Subscription() {

            @Override
            public void request(long n) {
                if (cancelled) {
                    return;
                }

                handleRequest(n);
            }

            @Override
            public void cancel() {
                ProcessingSubscription.this.cancel();
            }
        });
    }

    protected final void cancel() {
        cancelled = true;
        try {
            upstream.cancel();
        } finally {
            onCancel();
        }
    }

    protected void handleRequest(long n) {
        upstream.request(n);
    }

    protected void onCancel() {
        upstream.cancel();
    }

    @Override
    public final void onNext(T value) {
        if (cancelled) {
            return;
        }

        handleNext(value);
    }

    @Override
    public final void onError(Throwable error) {
        if (cancelled) {
            return;
        }

        handleError(error);
    }

    @Override
    public final void onComplete() {
        if (cancelled) {
            return;
        }

        handleComplete();
    }

    protected abstract void handleNext(T value);

    protected void handleError(Throwable error) {
        downstream.onError(error);
    }

    protected void handleComplete() {
        downstream.onComplete();
    }
}

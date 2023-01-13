package ahodanenok.reactivestreams.channel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Requests for the next items in batches of the configured size.
 * For the cases where the next items are sent on the same thread as requested.
 */
public class SyncIncrementalChannel<T> implements Channel<T> {

    private Subscription upstream;
    private Subscriber<? super T> downstream;

    private volatile boolean requesting;
    private volatile boolean cancelled;
    private volatile long requestedCount;
    private volatile long signalledCount;

    public SyncIncrementalChannel(Subscriber<? super T> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void connect(Subscription upstream) {
        this.upstream = upstream;
        handleRequest();
    }

    @Override
    public void activate() {
        downstream.onSubscribe(this);
    }

    @Override
    public void request(long n) {
        if (n <= 0) {
            signalError(new IllegalArgumentException("Requested amount must be positive: " + n));
            upstream.cancel();
            return;
        }

        if (cancelled) {
            return;
        }

        addRequested(n);
        if (requesting || upstream == null) {
            return;
        }

        handleRequest();
    }

    private void handleRequest() {
        requesting = true;
        try {
            while (signalledCount < requestedCount) {
                if (cancelled) {
                    break;
                }

                // here some signal* must be called
                upstream.request(1);
            }
        } finally {
            requesting = false;
        }
    }

    private void addRequested(long n) {
        if (n == Long.MAX_VALUE) {
            requestedCount = Long.MAX_VALUE;
            return;
        }

        requestedCount += n;
        if (requestedCount < 0) {
            requestedCount = Long.MAX_VALUE;
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
        upstream.cancel();
    }

    @Override
    public void signalNext(T value) {
        signalledCount++;
        downstream.onNext(value);
    }

    @Override
    public void signalError(Throwable error) {
        cancelled = true;
        downstream.onError(error);
    }

    @Override
    public void signalComplete() {
        cancelled = true;
        downstream.onComplete();
    }
}

package ahodanenok.reactivestreams.channel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Requests for the next items in batches of the configured size.
 * For the cases where the next items are sent on the same thread as requested.
 */
public class AsyncIncrementalChannel<T> implements Channel<T> {

    private Subscription upstream;
    private Subscriber<? super T> downstream;
    private final Executor executor;
    private final long batchSize;

    private volatile boolean cancelled;
    private volatile long requestedCount;
    private volatile long signalledCount;
    private volatile long batchRemainingCount;

    public AsyncIncrementalChannel(Subscriber<? super T> downstream) {
        this(downstream, 1);
    }

    public AsyncIncrementalChannel(Subscriber<? super T> downstream, long batchSize) {
        this.downstream = downstream;
        this.batchSize = batchSize;
        // todo: allow specifying Executor to make requests on
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void connect(Subscription upstream) {
        this.upstream = upstream;
        if (cancelled) {
            upstream.cancel();
        } else {
            handleRequest();
        }
    }

    @Override
    public void activate() {
        downstream.onSubscribe(this);
    }

    @Override
    public void request(long n) {
        if (n <= 0) {
            signalError(new IllegalArgumentException("Requested amount must be positive: " + n));
            if (upstream != null) upstream.cancel();
            return;
        }

        if (cancelled) {
            return;
        }

        addRequested(n);
        if (upstream == null) {
            return;
        }

        handleRequest();
    }

    private void handleRequest() {
        if (cancelled || batchRemainingCount > 0) {
            return;
        }

        if (signalledCount < requestedCount) {
            long n;
            if (batchSize == -1) {
                n = requestedCount - signalledCount;
            } else {
                n = Math.min(requestedCount - signalledCount, batchSize);
            }

            batchRemainingCount = n;
            executor.execute(() -> {
                upstream.request(n);
            });
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
        downstream.onNext(value);
        batchRemainingCount--;
        signalledCount++;
        handleRequest();
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

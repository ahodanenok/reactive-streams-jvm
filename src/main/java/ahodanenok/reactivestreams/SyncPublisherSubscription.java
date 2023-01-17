package ahodanenok.reactivestreams;

import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class SyncPublisherSubscription<T> implements Subscription {

    protected final Subscriber<? super T> subscriber;
    protected final AtomicLong requested = new AtomicLong(0);
    protected volatile boolean cancelled;

    public SyncPublisherSubscription(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber is null");
        }

        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {
        if (n <= 0) {
            cancel();
            subscriber.onError(new IllegalArgumentException("requested amount must be > 0: " + n));
            return;
        }

        if (cancelled) {
            return;
        }

        long requestedCurrent = requested.getAndAccumulate(n, (v, inc) -> {
             // nothing to add
            if (v == Long.MAX_VALUE) {
                return v;
            }

            long result = v + inc;
            if (result < 0) {
                return Long.MAX_VALUE;
            } else {
                return result;
            }
        });

        // already handling requests
        if (requestedCurrent > 0) {
            return;
        }

        requestedCurrent = requested.get();
        while (!cancelled) {
            if (!handleRequest(requestedCurrent)) {
                break;
            }

            requestedCurrent = requested.addAndGet(-requestedCurrent);
            if (requestedCurrent == 0) {
                break;
            }

            if (requestedCurrent < 0) {
                throw new IllegalStateException("Time to debug concurrency issues!");
            }
        }
    }

    protected abstract boolean handleRequest(long n);

    @Override
    public void cancel() {
        cancelled = true;
    }
}

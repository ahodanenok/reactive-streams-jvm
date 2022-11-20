package ahodanenok.reactivestreams.publisher;

public class RequestedSupport<T> {

    private final Runnable action;

    private volatile long requested;
    private long emitted;
    private volatile boolean emitting;
    private volatile boolean cancelled;

    public RequestedSupport(Runnable action) {
        this.action = action;
    }

    public void request(long n) {
        addRequested(n);
        if (emitting || cancelled) {
            return;
        }

        emitting = true;
        try {
            while (emitted < requested) {
                if (cancelled) {
                    break;
                }

                action.run();
                emitted++;
            }
        } finally {
            emitting = false;
        }
    }

    public void cancel() {
        cancelled = true;
    }

    private void addRequested(long n) {
        if (n == Long.MAX_VALUE) {
            requested = Long.MAX_VALUE;
            return;
        }

        requested += n;
        if (requested < 0) {
            requested = Long.MAX_VALUE;
        }
    }
}

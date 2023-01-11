package ahodanenok.reactivestreams.channel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ErrorChannel<T> implements Channel<T> {

    public static <T> void send(Subscriber<? super T> subscriber, Throwable error) {
        new ErrorChannel<>(subscriber, error).activate();
    }

    private final Subscriber<? super T> downstream;
    private final Throwable error;

    private volatile boolean cancelled;

    public ErrorChannel(Subscriber<? super T> downstream, Throwable error) {
        this.downstream = downstream;
        this.error = error;
    }

    @Override
    public void connect(Subscription upstream) {
        // no-op
    }

    @Override
    public void activate() {
        downstream.onSubscribe(this);
        if (!cancelled) {
            downstream.onError(error);
        }
    }

    @Override
    public void request(long n) {
        if (n <= 0) {
            signalError(new IllegalArgumentException("Requested amount must be positive: " + n));
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public void signalNext(T value) {
        // no-op
    }

    @Override
    public void signalError(Throwable error) {
        // no-op
    }

    @Override
    public void signalComplete() {
        // no-op
    }
}

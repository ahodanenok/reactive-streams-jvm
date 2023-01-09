package ahodanenok.reactivestreams.channel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SimpleChannel<T> implements Channel<T> {

    private Subscription upstream;
    private Subscriber<? super T> downstream;

    public SimpleChannel(Subscriber<? super T> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void connect(Subscription upstream) {
        this.upstream = upstream;
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

        upstream.request(n);
    }

    @Override
    public void cancel() {
        upstream.cancel();
    }

    @Override
    public void signalNext(T value) {
        downstream.onNext(value);
    }

    @Override
    public void signalError(Throwable error) {
        downstream.onError(error);
    }

    @Override
    public void signalComplete() {
        downstream.onComplete();
    }
}

package ahodanenok.reactivestreams.processor;

import java.util.Objects;

import org.reactivestreams.*;

import ahodanenok.reactivestreams.publisher.AbstractSubscription;
import ahodanenok.reactivestreams.publisher.Utils;

public abstract class AbstractProcessorSubscription<T> extends AbstractSubscription<T> implements Subscriber<T> {

    protected volatile Subscription upstream;
    protected volatile long pendingRequested;

    protected AbstractProcessorSubscription(Subscriber<? super T> subscriber) {
        super(subscriber);
    }

    @Override
    protected void onRequest(long n) {
        if (upstream != null) {
            upstream.request(n);
        } else {
            pendingRequested = Utils.addRequested(pendingRequested, n);
        }
    }

    @Override
    protected void onCancel() {
        if (upstream != null) {
            upstream.cancel();
        }
    }

    @Override
    public final void onSubscribe(Subscription upstream) {
        Objects.requireNonNull(upstream, "upstream");

        this.upstream = upstream;

        if (isCancelled()) {
            upstream.cancel();
        } else if (pendingRequested > 0) {
            upstream.request(pendingRequested);
            pendingRequested = 0;
        }
    }

    @Override
    public final void onNext(T value) {
        if (isCancelled()) {
            return;
        }

        try {
            value(value);
        } catch (Throwable e) {
            e.printStackTrace();
            dispose();
        }
    }

    @Override
    public final void onError(Throwable e) {
        if (isCancelled()) {
            return;
        }

        try {
            error(e);
        } catch (Throwable ex) {
            ex.printStackTrace();
            dispose();
        }
    }

    @Override
    public final void onComplete() {
        if (isCancelled()) {
            return;
        }

        try {
            complete();
        } catch (Throwable e) {
            e.printStackTrace();
            dispose();
        }
    }
}

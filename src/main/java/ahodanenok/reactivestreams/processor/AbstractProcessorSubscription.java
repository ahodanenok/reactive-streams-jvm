package ahodanenok.reactivestreams.processor;

import java.util.Objects;

import org.reactivestreams.*;

import ahodanenok.reactivestreams.publisher.AbstractSubscription;
import ahodanenok.reactivestreams.publisher.Utils;

public abstract class AbstractProcessorSubscription<T, R> extends AbstractSubscription<R> implements Subscriber<T> {

    protected volatile Subscription upstream;
    protected volatile long pendingRequested;

    protected AbstractProcessorSubscription(Subscriber<? super R> subscriber) {
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

        if (this.upstream != null) {
            // if already subscribed, then cancel incoming subscription
            upstream.cancel();
            return;
        }

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
            processNext(value);
        } catch (Throwable e) {
            e.printStackTrace();
            dispose();
        }
    }

    protected abstract void processNext(T value);

    @Override
    public final void onError(Throwable e) {
        if (isCancelled()) {
            return;
        }

        try {
            processError(e);
        } catch (Throwable ex) {
            ex.printStackTrace();
            dispose();
        }
    }

    protected void processError(Throwable e) {
        error(e);
    }

    @Override
    public final void onComplete() {
        if (isCancelled()) {
            return;
        }

        try {
            processComplete();
        } catch (Throwable e) {
            e.printStackTrace();
            dispose();
        }
    }

    protected void processComplete() {
        complete();
    }
}

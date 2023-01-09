package ahodanenok.reactivestreams.processor;

import java.util.Objects;
import org.reactivestreams.*;
import ahodanenok.reactivestreams.publisher.AbstractPublisher;

public abstract class AbstractTransformingProcessor<T, R> extends AbstractPublisher<R> implements Processor<T, R> {

    protected volatile Subscription upstream;
    protected volatile AbstractProcessorSubscription<R> downstream;

    protected Subscription pendingUpstream;
    protected Throwable pendingError;
    protected boolean pendingCompleted;

    @Override
    protected final void doSubscribe(Subscriber<? super R> subscriber) {
        AbstractProcessorSubscription<R> subscription = createSubscription(subscriber);
        subscription.init();

        downstream = subscription;
        if (upstream != null) {
            onSubscribe(upstream);
        }

        if (pendingError != null) {
            onError(pendingError);
            pendingError = null;
        } else if (pendingCompleted) {
            onComplete();
            pendingCompleted = false;
        }
    }

    protected abstract AbstractProcessorSubscription<R> createSubscription(Subscriber<? super R> subscriber);

    @Override
    public final void onSubscribe(Subscription subscription) {
        Objects.requireNonNull(subscription, "subscription");
        this.upstream = subscription;

        if (downstream != null) {
            if (this.downstream.upstream != null) {
                // if already subscribed, then cancel incoming subscription
                subscription.cancel();
                return;
            }

            downstream.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    upstream.request(n);
                }

                @Override
                public void cancel() {
                    try {
                        upstream.cancel();
                    } finally {
                        upstream = null;
                        downstream = null;
                    }
                }
            });
        }
    }

    @Override
    public final void onNext(T value) {
        Objects.requireNonNull(value, "value");
        if (downstream != null /*&& !downstream.isCancelled()*/) {
            //downstream.onNext(value);
            processNext(value);
        }
    }

    protected abstract void processNext(T value);

    @Override
    public final void onError(Throwable error) {
        Objects.requireNonNull(error, "error");
        if (downstream != null /*&& !downstream.isCancelled()*/) {
            processError(error);
        } else {
            pendingError = error;
        }
    }

    protected void processError(Throwable error) {
        downstream.error(error);
    }

    @Override
    public final void onComplete() {
        if (downstream != null /*&& !downstream.isCancelled()*/) {
            processComplete();
        } else {
            pendingCompleted = true;
        }
    }

    protected void processComplete() {
        downstream.complete();
    }
}

package ahodanenok.reactivestreams.processor;

import java.util.Objects;
import org.reactivestreams.*;
import ahodanenok.reactivestreams.publisher.AbstractPublisher;

public abstract class AbstractProcessor<T, R> extends AbstractPublisher<R> implements Processor<T, R> {

    protected volatile AbstractProcessorSubscription<T, R> processorSubscription;

    protected Subscription pendingUpstream;
    protected Throwable pendingError;
    protected boolean pendingCompleted;

    @Override
    protected final void doSubscribe(Subscriber<? super R> subscriber) {
        AbstractProcessorSubscription<T, R> subscription = createSubscription(subscriber);
        subscription.init();

        processorSubscription = subscription;

        if (pendingUpstream != null) {
            onSubscribe(pendingUpstream);
            pendingUpstream = null;
        }

        if (pendingError != null) {
            onError(pendingError);
            pendingError = null;
        } else if (pendingCompleted) {
            onComplete();
            pendingCompleted = false;
        }
    }

    protected abstract AbstractProcessorSubscription<T, R> createSubscription(Subscriber<? super R> subscriber);

    @Override
    public final void onSubscribe(final Subscription subscription) {
        Objects.requireNonNull(subscription, "subscription");
        if (processorSubscription != null) {
            processorSubscription.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    subscription.request(n);
                }

                @Override
                public void cancel() {
                    try {
                        subscription.cancel();
                    } finally {
                        processorSubscription = null;
                    }
                }
            });
        } else {
            pendingUpstream = subscription;
        }
    }

    @Override
    public final void onNext(T value) {
        Objects.requireNonNull(value, "value");
        if (processorSubscription != null /*&& !processorSubscription.isCancelled()*/) {
            processorSubscription.onNext(value);
        }
    }

    @Override
    public final void onError(Throwable error) {
        Objects.requireNonNull(error, "error");
        if (processorSubscription != null /*&& !processorSubscription.isCancelled()*/) {
            processorSubscription.onError(error);
        } else {
            pendingError = error;
        }
    }

    @Override
    public final void onComplete() {
        if (processorSubscription != null /*&& !processorSubscription.isCancelled()*/) {
            processorSubscription.onComplete();
        } else {
            pendingCompleted = true;
        }
    }
}

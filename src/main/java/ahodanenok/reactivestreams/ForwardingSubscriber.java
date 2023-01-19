package ahodanenok.reactivestreams;

import java.util.Objects;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class ForwardingSubscriber<T, R> implements Subscriber<R> {

    protected final Subscriber<? super R> downstream;
    protected volatile Subscription upstream;

    public ForwardingSubscriber(Subscriber<? super R> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        this.downstream = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        Objects.requireNonNull(subscription, "subscription");
        this.upstream = subscription;
        downstream.onSubscribe(subscription);
    }

    @Override
    public void onError(Throwable error) {
        downstream.onError(error);
    }

    @Override
    public void onComplete() {
        downstream.onComplete();
    }
}

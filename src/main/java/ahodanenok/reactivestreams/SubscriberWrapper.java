package ahodanenok.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

// todo: better name
public final class SubscriberWrapper<T> implements Subscriber<T> {

    private final Subscriber<? super T> subscriber;
    private Subscription subscription;

    public SubscriberWrapper(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription; // todo: 2.12?

        try {
            subscriber.onSubscribe(subscription);
        } catch (Throwable e) {
            subscription.cancel(); // 2.13
            throw e;
        }
    }

    @Override
    public void onNext(T value) {
        try {
            subscriber.onNext(value);
        } catch (Throwable e) {
            subscription.cancel(); // 2.13
            throw e;
        }
    }

    @Override
    public void onComplete() {
        try {
            subscriber.onComplete();
        } catch (Throwable e) {
            subscription.cancel(); // 2.13
            throw e;
        }
    }

    @Override
    public void onError(Throwable e) {
        try {
            subscriber.onError(e);
        } catch (Throwable unexpected) {
            subscription.cancel(); // 2.13
            throw unexpected;
        }
    }
}

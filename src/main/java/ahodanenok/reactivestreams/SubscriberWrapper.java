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
        if (subscription == null) {
            // 2.13
            subscription.cancel();
            throw new NullPointerException("onSubscribe: subscription is null"); 
        }

        this.subscription = subscription; // todo: 2.12?

        try {
            subscriber.onSubscribe(subscription);
        } catch (Throwable e) {
            e.printStackTrace(); // todo: log
            subscription.cancel(); // 2.13
        }
    }

    @Override
    public void onNext(T value) {
        if (value == null) {
            // 2.13
            subscription.cancel();
            throw new NullPointerException("onNext: value is null"); 
        }

        try {
            subscriber.onNext(value);
        } catch (Throwable e) {
            e.printStackTrace(); // todo: log
            subscription.cancel(); // 2.13
        }
    }

    @Override
    public void onComplete() {
        try {
            subscriber.onComplete();
        } catch (Throwable e) {
            e.printStackTrace(); // todo: log
            subscription.cancel(); // 2.13
        }
    }

    @Override
    public void onError(Throwable e) {
        try {
            subscriber.onError(e);
        } catch (Throwable unexpected) {
            unexpected.printStackTrace(); // todo: log
            subscription.cancel(); // 2.13
        }
    }
}

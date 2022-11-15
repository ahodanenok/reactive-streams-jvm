package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Subscriber;

public class EmptyPublisher<T> extends AbstractPublisher<T> {

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        EmptyPublisherSubscription<T> subscription = new EmptyPublisherSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        subscription.complete();
    }

    static class EmptyPublisherSubscription<T> extends AbstractSubscription<T> {

        EmptyPublisherSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
}

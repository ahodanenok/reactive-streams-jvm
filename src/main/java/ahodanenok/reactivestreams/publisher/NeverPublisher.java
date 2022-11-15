package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Subscriber;

public class NeverPublisher<T> extends AbstractPublisher<T> {

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        NeverPublisherSubscription<T> subscription = new NeverPublisherSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
    }

    static class NeverPublisherSubscription<T> extends AbstractSubscription<T> {

        NeverPublisherSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
}

package ahodanenok.reactivestreams.publisher;

public class EmptyPublisher<T> extends AbstractPublisherV2<T> {

    @Override
    protected void onActivate() {
        signalComplete();
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        EmptyPublisherSubscription<T> subscription = new EmptyPublisherSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        subscription.complete();
    }

    static class EmptyPublisherSubscription<T> extends AbstractSubscription<T> {

        EmptyPublisherSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }*/
}

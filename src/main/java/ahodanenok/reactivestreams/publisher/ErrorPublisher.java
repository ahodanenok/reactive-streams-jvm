package ahodanenok.reactivestreams;

import org.reactivestreams.Subscriber;

public class ErrorPublisher<T> extends AbstractPublisher<T> {

    private final Throwable e;

    public ErrorPublisher(Throwable e) {
        this.e = e;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        ErrorPublisherSubscription<T> subscription =
            new ErrorPublisherSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        subscription.error(e);
    }

    // todo: maybe create class for default subscription?
    static class ErrorPublisherSubscription<T> extends AbstractSubscription<T> {

        ErrorPublisherSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
}

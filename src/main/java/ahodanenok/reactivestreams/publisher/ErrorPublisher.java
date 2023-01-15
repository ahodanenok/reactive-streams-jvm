package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import org.reactivestreams.Subscriber;

public class ErrorPublisher<T> extends AbstractPublisherV2<T> {

    private final Throwable error;

    public ErrorPublisher(Throwable error) {
        Objects.requireNonNull(error, "error");
        this.error = error;
    }

    @Override
    protected void onActivate() {
        signalError(error);
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        ErrorPublisherSubscription<T> subscription = new ErrorPublisherSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        subscription.error(error);
    }

    static class ErrorPublisherSubscription<T> extends AbstractSubscription<T> {

        ErrorPublisherSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }*/
}

package ahodanenok.reactivestreams;

import java.util.Objects;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class ErrorPublisher<T> implements Publisher<T> {

    private final Throwable error;

    public ErrorPublisher(Throwable error) {
        Objects.requireNonNull(error, "error");
        this.error = error;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        EmptySubscription<T> subscription = new EmptySubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        if (!subscription.cancelled) {
            subscriber.onError(error);
        }
    }
}

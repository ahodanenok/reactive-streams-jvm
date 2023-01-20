package ahodanenok.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class EmptyPublisher<T> implements Publisher<T> {

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        EmptySubscription<T> subscription = new EmptySubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        if (!subscription.cancelled) {
            subscriber.onComplete();
        }
    }
}

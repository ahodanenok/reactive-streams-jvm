package ahodanenok.reactivestreams;

import java.util.Objects;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class NeverPublisher<T> implements Publisher<T> {

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        subscriber.onSubscribe(new Subscription() {

            @Override
            public void request(long n) {
                if (n <= 0) {
                    subscriber.onError(new IllegalArgumentException("requested amount must be > 0: " + n));
                }

                // no-op
            }

            @Override
            public void cancel() {
                // no-op
            }
        });
    }
}

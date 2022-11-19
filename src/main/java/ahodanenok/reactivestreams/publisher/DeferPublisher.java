package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class DeferPublisher<T> extends AbstractPublisher<T> {

    private final Supplier<? extends Publisher<T>> supplier;

    public DeferPublisher(Supplier<? extends Publisher<T>> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        this.supplier = supplier;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        Publisher<T> publisher = supplier.get();
        if (publisher != null) {
            publisher.subscribe(subscriber);
        } else {
            DeferPublisherSubscription<T> subscription = new DeferPublisherSubscription<>(subscriber);
            subscriber.onSubscribe(subscription);
            subscription.error(new NullPointerException("Supplied publisher is null"));
        }
    }

    static class DeferPublisherSubscription<T> extends AbstractSubscription<T> {

        DeferPublisherSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
}

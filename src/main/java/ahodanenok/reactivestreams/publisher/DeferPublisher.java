package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import ahodanenok.reactivestreams.channel.ErrorChannel;

public class DeferPublisher<T> implements Publisher<T> {

    private final Supplier<? extends Publisher<T>> supplier;

    public DeferPublisher(Supplier<? extends Publisher<T>> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        this.supplier = supplier;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");

        Publisher<T> publisher = supplier.get();
        if (publisher != null) {
            publisher.subscribe(subscriber);
        } else {
            ErrorChannel.send(subscriber, new NullPointerException("Supplied publisher is null"));
        }
    }
}

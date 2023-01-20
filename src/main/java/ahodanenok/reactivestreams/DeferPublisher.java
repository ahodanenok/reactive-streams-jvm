package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

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
        if (publisher == null) {
            publisher = new ErrorPublisher<>(new NullPointerException("Publisher supplier returned null"));
        }

        publisher.subscribe(subscriber);
    }
}

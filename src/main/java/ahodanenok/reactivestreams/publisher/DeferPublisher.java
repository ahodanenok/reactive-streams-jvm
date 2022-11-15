package ahodanenok.reactivestreams.publisher;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class DeferPublisher<T> extends AbstractPublisher<T> {

    private final Supplier<? extends Publisher<T>> supplier;

    public DeferPublisher(Supplier<? extends Publisher<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        Publisher<T> publisher = supplier.get();
        publisher.subscribe(subscriber);
    }
}

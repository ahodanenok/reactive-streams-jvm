package ahodanenok.reactivestreams;

import org.reactivestreams.Subscriber;
import java.util.function.Supplier;

public class OnceDefer<T> extends Once<T> {

    private final Supplier<? extends Once<T>> supplier;

    public OnceDefer(Supplier<? extends Once<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void doSubscribe(Subscriber<? super T> subscriber) {
        Once<T> publisher = supplier.get();
        // if null NPE would signalled to the caller
        publisher.subscribe(subscriber);
    }
} 

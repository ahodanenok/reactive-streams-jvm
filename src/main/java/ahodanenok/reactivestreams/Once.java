package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import java.util.function.Supplier;

public abstract class Once<T> implements Publisher<T> {

    /**
     * Create publisher that emits a single value and completes.
     */
    public static <T> Once<T> value(T value) {
        return new OnceValue<>(value);
    }

    /**
     * Create publisher that emits the value produced by supplier.
     */
    public static <T> Once<T> value(Supplier<T> supplier) {
        return new OnceValueSupplier<>(supplier);
    }

    /**
     * Crete publisher that never emits any signal except onSubscribe.
     */
    public static <T> Once<T> never() {
        return new OnceNever<>();
    }

    /**
     * Create publisher that immediately completes after subscribing to it
     * without emitting any values. 
     */
    public static <T> Once<T> empty() {
        return new OnceEmpty<>();
    }

    /**
     * Create publisher that immediately completes with error signal.
     */
    public static <T> Once<T> error(Throwable throwable) {
        return new OnceError<>(throwable);
    }

    /**
     * Create publisher that completes when the given publisher completes. 
     */
    public static <T> Once<T> publisherCompleted(Publisher<T> publisher) {
        return new OncePublisherCompleted<>(publisher);
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber"); // 1.9
        }

        // todo: enforce 1.10?
        doSubscribe(new SubscriberWrapper(subscriber));
    }

    protected void doSubscribe(Subscriber<? super T> subscriber) { };
}

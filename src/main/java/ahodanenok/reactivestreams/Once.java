package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import java.util.function.Supplier;

public abstract class Once<T> implements Publisher<T> {

    /**
     * Create publisher that emits a single value and completes.
     */
    public static <T> Once<T> value(T value) {
        return new OnceValue<T>(value);
    }

    /**
     * Create publisher that emits the value produced by supplier.
     */
    public static <T> Once<T> value(Supplier<T> supplier) {
        return new OnceValueSupplier<T>(supplier);
    }

    /**
     * Crete publisher that never emits any signal except onSubscribe.
     */
    public static <T> Once<T> never() {
        return new OnceNever<T>();
    }

    /**
     * Create publisher that immediately completes after subscribing to it
     * without emitting any values. 
     */
    public static <T> Once<T> empty() {
        return new OnceEmpty<T>();
    }

    /**
     * Create publisher that immediately completes with error signal.
     */
    public static <T> Once<T> error(Throwable throwable) {
        return new OnceError<T>(throwable);
    }
}

package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import java.util.function.Supplier;

public abstract class Once<T> implements Publisher<T> {

    /**
     * Create publisher that emits a single value and completes.
     */
    public static <T> Once<T> value(T value) {
        return new OnceValue(value);
    }

    /**
     * Crete publisher that never emits any signal except onSubscribe.
     */
    public static <T> Once<T> never() {
        return new OnceNever();
    }

    /**
     * Create publisher that emits the value produced by supplier.
     */
    public static <T> Once<T> completed(Supplier<T> supplier) {
        return new OnceSupplierCompleted(supplier);
    }
}

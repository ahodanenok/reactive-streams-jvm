package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public abstract class Once<T> implements Publisher<T> {

    /**
     * Create publisher that emits a single value and completes.
     */
    public static <T> Once<T> value(T value) {
        return new OnceValue(value);
    }
}

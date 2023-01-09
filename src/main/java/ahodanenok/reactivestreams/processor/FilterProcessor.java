package ahodanenok.reactivestreams.processor;

import java.util.Objects;
import java.util.function.Predicate;

import org.reactivestreams.*;

public class FilterProcessor<T> extends AbstractTransformingProcessor<T, T> {

    private final Predicate<T> predicate;

    public FilterProcessor(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.predicate = predicate;
    }

    @Override
    protected FilterProcessorSubscription<T> createSubscription(Subscriber<? super T> subscriber) {
        return new FilterProcessorSubscription<>(subscriber);
    }

    @Override
    protected void processNext(T value) {
        if (predicate.test(value)) {
            downstream.value(value);
        } else {
            upstream.request(1);
        }
    }

    static class FilterProcessorSubscription<T> extends AbstractProcessorSubscription<T> {

        FilterProcessorSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }
    }
}

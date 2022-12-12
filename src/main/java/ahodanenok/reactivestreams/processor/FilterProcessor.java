package ahodanenok.reactivestreams.processor;

import java.util.Objects;
import java.util.function.Predicate;

import org.reactivestreams.*;

public class FilterProcessor<T> extends AbstractProcessor<T, T> {

    private final Predicate<T> predicate;

    public FilterProcessor(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.predicate = predicate;
    }

    @Override
    protected FilterProcessorSubscription<T> createSubscription(Subscriber<? super T> subscriber) {
        return new FilterProcessorSubscription<>(subscriber, predicate);
    }

    static class FilterProcessorSubscription<T> extends AbstractProcessorSubscription<T, T> {

        private final Predicate<T> predicate;

        FilterProcessorSubscription(Subscriber<? super T> subscriber, Predicate<T> predicate) {
            super(subscriber);
            this.predicate = predicate;
        }

        @Override
        public void processNext(final T value) {
            if (predicate.test(value)) {
                value(value);
            } else {
                upstream.request(1);
            }
        }
    }
}

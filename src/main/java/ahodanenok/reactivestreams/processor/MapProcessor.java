package ahodanenok.reactivestreams.processor;

import java.util.Objects;
import java.util.function.Function;

import org.reactivestreams.*;

public class MapProcessor<T, R> extends AbstractTransformingProcessor<T, R> {

    private final Function<T, R> mapper;

    public MapProcessor(Function<T, R> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        this.mapper = mapper;
    }

    @Override
    protected MapProcessorSubscription<T, R> createSubscription(Subscriber<? super R> subscriber) {
        return new MapProcessorSubscription<>(subscriber, mapper);
    }

    static class MapProcessorSubscription<T, R> extends AbstractProcessorSubscription<T, R> {

        private final Function<T, R> mapper;

        MapProcessorSubscription(Subscriber<? super R> subscriber, Function<T, R> mapper) {
            super(subscriber);
            this.mapper = mapper;
        }

        @Override
        public void processNext(final T value) {
            value(mapper.apply(value));
        }
    }
}

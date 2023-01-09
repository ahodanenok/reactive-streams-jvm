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
    protected MapProcessorSubscription<R> createSubscription(Subscriber<? super R> subscriber) {
        return new MapProcessorSubscription<R>(subscriber);
    }

    @Override
    protected void processNext(T value) {
        downstream.value(mapper.apply(value));
    }

    static class MapProcessorSubscription<R> extends AbstractProcessorSubscription<R> {

        MapProcessorSubscription(Subscriber<? super R> subscriber) {
            super(subscriber);
        }
    }
}

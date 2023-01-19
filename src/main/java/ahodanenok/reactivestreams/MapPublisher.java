package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class MapPublisher<T, R> implements Publisher<R> {

    private final Publisher<T> publisher;
    private final Function<T, R> mapper;

    public MapPublisher(Publisher<T> publisher, Function<T, R> mapper) {
        Objects.requireNonNull(publisher, "publisher");
        Objects.requireNonNull(mapper, "mapper");
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Override
    public void subscribe(Subscriber<? super R> subscriber) {
        publisher.subscribe(new MapSubscriber<>(subscriber, mapper));
    }

    private static class MapSubscriber<T, R> extends ForwardingSubscriber<T, R> {

        private final Function<T, R> mapper;

        MapSubscriber(Subscriber<? super R> subscriber, Function<T, R> mapper) {
            super(subscriber);
            this.mapper = mapper;
        }

        @Override
        public void onNext(T value) {
            if (value == null) {
                throw new NullPointerException("value");
            }

            downstream.onNext(mapper.apply(value));
        }
    }
}

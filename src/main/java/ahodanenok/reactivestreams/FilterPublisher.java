package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class FilterPublisher<T> implements Publisher<T> {

    private final Publisher<T> publisher;
    private final Predicate<T> predicate;

    public FilterPublisher(Publisher<T> publisher, Predicate<T> predicate) {
        Objects.requireNonNull(publisher, "publisher");
        Objects.requireNonNull(predicate, "predicate");
        this.publisher = publisher;
        this.predicate = predicate;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        publisher.subscribe(new FilterSubscriber(subscriber, predicate));
    }

    private static class FilterSubscriber<T> extends ForwardingSubscriber<T, T> {

        private final Predicate<T> predicate;

        FilterSubscriber(Subscriber<? super T> subscriber, Predicate<T> predicate) {
            super(subscriber);
            this.predicate = predicate;
        }

        @Override
        public void onNext(T value) {
            if (value == null) {
                throw new NullPointerException("value");
            }

            if (predicate.test(value)) {
                downstream.onNext(value);
            } else {
                upstream.request(1);
            }
        }
    }
}

package ahodanenok.reactivestreams;

import java.util.function.Function;
import org.reactivestreams.*;;

public class MapPublisher<T, R> extends AbstractPublisher<R> {

    private final Publisher<T> upstream;
    private final Function<T, R> mapper;

    public MapPublisher(Publisher<T> upstream, Function<T, R> mapper) {
        this.upstream = upstream;
        this.mapper = mapper;
    }

    @Override
    protected void doSubscribe(Subscriber<? super R> downstream) {
        MapProcessor<T, R> processor = new MapProcessor<>(mapper);
        processor.subscribe(downstream);
        upstream.subscribe(processor);
    }

    static class MapProcessor<T, R> implements Processor<T, R>, Subscription {
        
        private final Function<T, R> mapper;
        private Subscriber<? super R> downstream;
        private Subscription subscription;

        public MapProcessor(Function<T, R> mapper) {
            this.mapper = mapper;
        }

        @Override
        public void request(long n) {
            subscription.request(n);
        }

        @Override
        public void cancel() {
            subscription.cancel();
        }

        @Override
        public void subscribe(Subscriber<? super R> downstream) {
            this.downstream = downstream;
            downstream.onSubscribe(this);
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            // todo: pending request/cancel
        }

        @Override
        public void onNext(T value) {
            downstream.onNext(mapper.apply(value));
        }

        @Override
        public void onComplete() {
            downstream.onComplete();
        }

        @Override
        public void onError(Throwable e) {
            downstream.onError(e);
        }
    }
}

package ahodanenok.reactivestreams.publisher;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import org.reactivestreams.*;;

public class WhenAnyCompletedPublisher<T> extends AbstractPublisher<T> {

    private final List<Publisher<?>> publishers;

    public WhenAnyCompletedPublisher(List<Publisher<?>> publishers) {
        this.publishers = publishers;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> downstream) {
        WhenAnyCompletedProcessor<T> processor = new WhenAnyCompletedProcessor<>(publishers);
        processor.subscribe(downstream);
        for (Publisher<?> upstream : publishers) {
            upstream.subscribe(processor); // violating rule 2.5!
        }
    }

    static class WhenAnyCompletedProcessor<T> implements Processor<Object, T>, Subscription {
        
        // todo: thread safety

        private final List<Publisher<?>> publishers;
        private Subscriber<? super T> downstream;
        private final List<Subscription> subscriptions = new ArrayList<>();

        WhenAnyCompletedProcessor(List<Publisher<?>> publishers) {
            this.publishers = publishers;
        }

        @Override
        public void request(long n) {
            for (Subscription s : subscriptions) {
                s.request(Long.MAX_VALUE);
            }
        }

        @Override
        public void cancel() {
            for (Subscription s : subscriptions) {
                s.cancel();
            }
        }

        @Override
        public void subscribe(Subscriber<? super T> downstream) {
            this.downstream = downstream;
            downstream.onSubscribe(this);
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscriptions.add(subscription);

            // todo: pending request/cancel
        }

        @Override
        public void onNext(Object value) {
            // skipping...
        }

        @Override
        public void onComplete() {
            // todo: prevent multiple concurrently
            for (Subscription s : subscriptions) {
                s.cancel();
            }

            downstream.onComplete();
        }

        @Override
        public void onError(Throwable e) {
            // todo: prevent multiple concurrently
            for (Subscription s : subscriptions) {
                s.cancel();
            }

            downstream.onError(e);
        }
    }
}

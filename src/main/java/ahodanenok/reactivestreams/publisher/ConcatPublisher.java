package ahodanenok.reactivestreams.publisher;

import java.util.List;
import java.util.ArrayList;

import org.reactivestreams.*;

public class ConcatPublisher<T> extends AbstractPublisher<T> {

    private final List<Publisher<T>> publishers;

    public ConcatPublisher(List<Publisher<T>> publishers) {
        this.publishers = publishers;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        new ConcatPublisherSubscription(publishers).subscribe(subscriber);
    }

    static class ConcatPublisherSubscription<T> implements Processor<T, T>, Subscription {

        // todo: thread safety

        private Subscriber<? super T> downstream;
        private Subscription subscription;

        private final List<Publisher<T>> remainingPublishers;
        private final List<Publisher<T>> completedPublishers;

        private volatile long requested;
        private volatile long emitted;

        ConcatPublisherSubscription(List<Publisher<T>> publishers) {
            this.remainingPublishers = new ArrayList<>(publishers);
            this.completedPublishers = new ArrayList<>();
        }

        @Override
        public void request(long n) {
            requested = Utils.addRequested(requested, n);
            if (subscription != null) {
                subscription.request(n);
            }
        }

        @Override
        public void cancel() {
            subscription.cancel();
        }

        @Override
        public void subscribe(Subscriber<? super T> downstream) {
            this.downstream = downstream;
            downstream.onSubscribe(this);
            remainingPublishers.get(0).subscribe(this);
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            // todo: pending request/cancel

            if (requested == Long.MAX_VALUE) {
                subscription.request(Long.MAX_VALUE);
            } else if (emitted < requested) {
                subscription.request(requested - emitted);
            }
        }

        @Override
        public void onNext(T value) {
            emitted++;
            downstream.onNext(value);
        }

        @Override
        public void onComplete() {            
            completedPublishers.add(remainingPublishers.remove(0));
            if (remainingPublishers.isEmpty()) {
                downstream.onComplete();
            } else {
                remainingPublishers.get(0).subscribe(this);
            }
        }

        @Override
        public void onError(Throwable e) {
            downstream.onError(e);
        }
    }
}

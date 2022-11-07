package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import java.util.List;

public class OnceEveryPublisherCompleted<T> extends Once<T> {

    private final List<Publisher<?>> publishers;

    public OnceEveryPublisherCompleted(List<Publisher<?>> publishers) {
        this.publishers = publishers;
    }

    protected void doSubscribe(Subscriber<? super T> subscriber) {
        DisposableAggregate disposable = new DisposableAggregate();

        OnceEveryPublisherCompletedSubscription subscription =
            new OnceEveryPublisherCompletedSubscription(subscriber, disposable);
        subscriber.onSubscribe(subscription);

        for (Publisher<?> p : publishers) {
            p.subscribe(new OnceUpstreamPublisherCompletedSubscriber(subscription, disposable));
        }       
    }

    class OnceEveryPublisherCompletedSubscription extends OnceSubscription<T> {

        private final Disposable disposable;

        OnceEveryPublisherCompletedSubscription(
                Subscriber<? super T> downstream, Disposable disposable) {
            super(downstream);
            this.disposable = disposable;
        }

        @Override
        protected void onCancel() {
            disposable.dispose();
        }
    }

    class OnceUpstreamPublisherCompletedSubscriber implements Subscriber<Object>, Disposable {

        // todo: thread-safety

        private Subscription upstream;
        private final OnceEveryPublisherCompletedSubscription downstream;
        private DisposableAggregate disposable;        
        private int completedCount;

        OnceUpstreamPublisherCompletedSubscriber(
                OnceEveryPublisherCompletedSubscription downstream,
                DisposableAggregate disposable) {
            this.downstream = downstream;
            this.disposable = disposable;
        }

        @Override
        public void onSubscribe(Subscription upstream) {
            this.upstream = upstream;
            disposable.add(this);
        }

        @Override
        public void onError(Throwable e) {
            downstream.complete(e);
        }

        @Override
        public void onNext(Object value) { /* no-op */ }

        @Override
        public void onComplete() {
            completedCount++;
            if (completedCount == publishers.size()) {
                downstream.complete();
            }
        }

        @Override
        public void dispose() {
            upstream.cancel();
        }
    }
}

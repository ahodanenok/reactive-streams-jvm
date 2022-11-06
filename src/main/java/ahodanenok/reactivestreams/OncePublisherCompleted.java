package ahodanenok.reactivestreams;

import org.reactivestreams.*;

public class OncePublisherCompleted<T> extends Once<T> {

    private final Publisher<T> upstream;

    public OncePublisherCompleted(Publisher<T> upstream) {
        this.upstream = upstream;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        OncePublisherCompletedSubscription<T> subscription =
            new OncePublisherCompletedSubscription<>(subscriber);
        // todo: some error handling needs to be done here
        subscriber.onSubscribe(subscription);
        upstream.subscribe(subscription);
    }

    static class OncePublisherCompletedSubscription<T> extends OnceSubscription<T> implements Subscriber<T> {

        OncePublisherCompletedSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public void onSubscribe(Subscription s) {
            // todo: cancel upstream subscription if downstream cancelled its (onCancel)
        }

        @Override
        public void onError(Throwable e) {
            complete(e);
        }

        @Override
        public void onNext(T value) { /* no-op */ }

        @Override
        public void onComplete() {
            complete();
        }
    }
}

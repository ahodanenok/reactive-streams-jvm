package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ConcatPublisher<T> implements Publisher<T> {

    private LinkedList<Publisher<T>> publishers;

    public ConcatPublisher(List<Publisher<T>> publishers) {
        Objects.requireNonNull(publishers, "publishers");
        if (publishers.isEmpty()) {
            throw new IllegalArgumentException("Publishers list is empty");
        }
        if (publishers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Publishers list contains null");
        }

        this.publishers = new LinkedList<>(publishers);
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        new ConcatPublisherSubscription<>(subscriber, publishers).subscribeNext();
    }

    static class ConcatPublisherSubscription<T> implements Subscription, Subscriber<T> {

        // package state to provide atomicity
        final class SubscriptionState {

            final Subscription upstream;
            final AtomicLong requested;
            final AtomicLong signalled;

            SubscriptionState(Subscription subscription) {
                this.upstream = subscription;
                this.requested = new AtomicLong(0);
                this.signalled = new AtomicLong(0);
            }
        }

        private final Subscriber<? super T> downstream;
        private final LinkedList<Publisher<T>> publishers;

        private volatile boolean subscribed;
        private volatile SubscriptionState subscriptionState;

        ConcatPublisherSubscription(Subscriber<? super T> subscriber, LinkedList<Publisher<T>> publishers) {
            this.downstream = subscriber;
            this.publishers = publishers;
        }

        void subscribeNext() {
            publishers.poll().subscribe(this);
        }

        @Override
        public void request(long n) {
            if (n > 0) {
                subscriptionState.requested.accumulateAndGet(n, (v, inc) -> {
                    // nothing to add
                    if (v == Long.MAX_VALUE) {
                        return v;
                    }

                    long result = v + inc;
                    if (result < 0) {
                        return Long.MAX_VALUE;
                    } else {
                        return result;
                    }
                });
            }

            subscriptionState.upstream.request(n);
        }

        @Override
        public void cancel() {
            subscriptionState.upstream.cancel();
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            Objects.requireNonNull(subscription, "subscription");

            SubscriptionState prevState = subscriptionState;
            subscriptionState = new SubscriptionState(subscription);
            if (!subscribed){
                subscribed = true;
                downstream.onSubscribe(this);
            }

            if (prevState != null) {
                long requested = prevState.requested.longValue();
                long signalled = prevState.signalled.longValue();
                if (requested == Long.MAX_VALUE) {
                    request(Long.MAX_VALUE);
                } else if (signalled < requested) {
                    request(requested - signalled);
                }
            }
        }

        @Override
        public void onNext(T value) {
            subscriptionState.signalled.incrementAndGet();
            downstream.onNext(value);
        }

        @Override
        public void onError(Throwable error) {
            downstream.onError(error);
        }

        @Override
        public void onComplete() {
            if (publishers.isEmpty()) {
                downstream.onComplete();
            } else {
                subscribeNext();
            }
        }
    }
}

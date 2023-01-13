package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Subscriber;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SyncIncrementalChannel;

public class IntRangePublisher extends AbstractPublisherV2<Integer> {

    private final int from;
    private final int to;

    private int current;

    public IntRangePublisher(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }

        this.from = from;
        this.to = to;
    }

    protected Channel<Integer> createChannel(Subscriber<? super Integer> subscriber) {
        return new SyncIncrementalChannel<>(subscriber);
    }

    @Override
    protected void onRequest(long n) {
        if (current < to) {
            signalNext(current);
            current++;
        }

        if (current == to) {
            signalComplete();
        }
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super Integer> subscriber) {
        IntRangePublisherSubscription subscription =
            new IntRangePublisherSubscription(subscriber, from, to);
        subscriber.onSubscribe(subscription);
        if (from == to) {
            subscription.complete();
        }
    }

    static class IntRangePublisherSubscription extends AbstractSubscription<Integer> {

        private int current;
        private final int to;
        private final RequestedSupport requestedSupport;

        IntRangePublisherSubscription(Subscriber<? super Integer> subscriber, int from, int to) {
            super(subscriber);
            this.current = current;
            this.to = to;
            this.requestedSupport = new RequestedSupport(this::next);
        }

        @Override
        protected void onRequest(long n) {
            requestedSupport.request(n);
        }

        @Override
        protected void onDispose() {
            requestedSupport.dispose();
        }

        private void next() {
            if (current < to) {
                value(current);
                current++;
            }

            if (current == to) {
                complete();
            }
        }
    }*/
}

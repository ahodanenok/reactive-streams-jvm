package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.Iterator;

import org.reactivestreams.Subscriber;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SyncIncrementalChannel;

public class IterablePublisher<T> extends AbstractPublisherV2<T> {

    private final Iterable<T> iterable;

    private volatile Iterator<T> iterator;

    public IterablePublisher(Iterable<T> iterable) {
        Objects.requireNonNull(iterable, "iterable");
        this.iterable = iterable;
    }

    protected Channel<T> createChannel(Subscriber<? super T> subscriber) {
        return new SyncIncrementalChannel<>(subscriber);
    }

    @Override
    protected void onRequest(long n) {
        if (iterator == null) {
            iterator = iterable.iterator();
        }

        if (iterator.hasNext()) {
            signalNext(iterator.next());
        } else {
            signalComplete();
        }
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new IterablePublisherSubscription<>(subscriber, iterable));
    }

    static class IterablePublisherSubscription<T> extends AbstractSubscription<T> {

        private final Iterable<T> iterable;
        private final RequestedSupport requestedSupport;

        private Iterator<T> iterator;

        IterablePublisherSubscription(Subscriber<? super T> subscriber, Iterable<T> iterable) {
            super(subscriber);
            this.iterable = iterable;
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

        public void next() {
            if (iterator == null) {
                iterator = iterable.iterator();
            }

            if (iterator.hasNext()) {
                value(iterator.next());
            } else {
                complete();
            }
        }
    }*/
}

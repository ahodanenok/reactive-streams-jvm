package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.Iterator;
import java.util.stream.Stream;

import org.reactivestreams.Subscriber;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SyncIncrementalChannel;

public class StreamPublisher<T> extends AbstractPublisherV2<T> {

    private final Stream<T> stream;

    private volatile Iterator<T> iterator;

    public StreamPublisher(Stream<T> stream) {
        Objects.requireNonNull(stream, "stream");
        this.stream = stream;
    }

    @Override
    protected Channel<T> createChannel(Subscriber<? super T> subscriber) {
        return new SyncIncrementalChannel(subscriber);
    }

    @Override
    protected void onRequest(long n) {
        if (iterator == null) {
            iterator = stream.iterator();
        }

        if (iterator.hasNext()) {
            signalNext(iterator.next());
        } else {
            signalComplete();
            stream.close();
        }
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new StreamPublisherSubscription<>(subscriber, stream));
    }

    static class StreamPublisherSubscription<T> extends AbstractSubscription<T> {

        private final Stream<T> stream;
        private final RequestedSupport requestedSupport;

        private Iterator<T> iterator;

        StreamPublisherSubscription(Subscriber<? super T> subscriber, Stream<T> stream) {
            super(subscriber);
            this.stream = stream;
            this.requestedSupport = new RequestedSupport(this::next);
        }

        @Override
        protected void onRequest(long n) {
            requestedSupport.request(n);
        }

        @Override
        protected void onDispose() {
            requestedSupport.dispose();
            stream.close();
        }

        public void next() {
            try {
                if (iterator == null) {
                    iterator = stream.iterator();
                }

                if (iterator.hasNext()) {
                    value(iterator.next());
                } else {
                    complete();
                    stream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                error(e);
            }
        }
    }*/
}

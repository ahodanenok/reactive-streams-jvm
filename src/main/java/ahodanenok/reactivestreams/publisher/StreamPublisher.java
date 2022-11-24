package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.Iterator;
import java.util.stream.Stream;

import org.reactivestreams.Subscriber;

public class StreamPublisher<T> extends AbstractPublisher<T> {

    private final Stream<T> stream;

    public StreamPublisher(Stream<T> stream) {
        Objects.requireNonNull(stream, "stream");
        this.stream = stream;
    }

    @Override
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
        protected void onCancel() {
            requestedSupport.cancel();
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
                stream.close();
                error(e);
            }
        }
    }
}

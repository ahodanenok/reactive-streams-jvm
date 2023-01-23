package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class StreamPublisher<T> implements Publisher<T> {

    private final Stream<T> stream;
    private final AtomicBoolean subscribed;

    public StreamPublisher(Stream<T> stream) {
        Objects.requireNonNull(stream, "stream");
        this.stream = stream;
        this.subscribed = new AtomicBoolean(false);
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        if (!subscribed.getAndSet(true)) {
            subscriber.onSubscribe(new StreamPublisherSubscription<>(subscriber, stream));
        } else {
            EmptySubscription<T> subscription = new EmptySubscription<>(subscriber);
            subscriber.onSubscribe(subscription);
            if (!subscription.cancelled) {
                subscriber.onError(new IllegalStateException("Publisher supports only a single subscriber"));
            }
        }
    }

    static class StreamPublisherSubscription<T> extends SyncPublisherSubscription<T> {

        private final Stream<T> stream;

        private volatile Iterator<T> iterator;

        StreamPublisherSubscription(Subscriber<? super T> subscriber, Stream<T> stream) {
            super(subscriber);
            this.stream = stream;
        }

        @Override
        protected boolean handleRequest(long n) {
            if (iterator == null) {
                iterator = stream.iterator();
            }

            Iterator<T> iteratorLocal = iterator;
            while (iteratorLocal.hasNext() && n > 0) {
                downstream.onNext(iteratorLocal.next());
                n--;
            }

            if (!iteratorLocal.hasNext()) {
                try {
                    downstream.onComplete();
                } finally {
                    stream.close();
                }

                return false;
            }

            return true;
        }
    }
}

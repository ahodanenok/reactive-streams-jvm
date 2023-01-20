package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.Iterator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class IterablePublisher<T> implements Publisher<T> {

    private final Iterable<T> iterable;

    public IterablePublisher(Iterable<T> iterable) {
        Objects.requireNonNull(iterable, "iterable");
        this.iterable = iterable;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new IterablePublisherSubscription<>(subscriber, iterable));
    }

    static class IterablePublisherSubscription<T> extends SyncPublisherSubscription<T> {

        private final Iterable<T> iterable;

        private volatile Iterator<T> iterator;

        IterablePublisherSubscription(Subscriber<? super T> subscriber, Iterable<T> iterable) {
            super(subscriber);
            this.iterable = iterable;
        }

        @Override
        protected boolean handleRequest(long n) {
            if (iterator == null) {
                try {
                    iterator = iterable.iterator();
                } catch (Exception e) {
                    downstream.onError(e);
                    return false;
                }
            }

            Iterator<T> iteratorLocal = iterator;
            while (iteratorLocal.hasNext() && n > 0) {
                downstream.onNext(iteratorLocal.next());
                n--;
            }

            if (!iteratorLocal.hasNext()) {
                downstream.onComplete();
                return false;
            }

            return true;
        }
    }
}

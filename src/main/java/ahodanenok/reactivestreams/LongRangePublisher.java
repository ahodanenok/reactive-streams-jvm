package ahodanenok.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class LongRangePublisher implements Publisher<Long> {

    private final long from;
    private final long to;

    public LongRangePublisher(long from, long to) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }

        this.from = from;
        this.to = to;
    }

    @Override
    public void subscribe(Subscriber<? super Long> subscriber) {
        subscriber.onSubscribe(new LongRangePublisherSubscription(subscriber, from, to));
    }

    static class LongRangePublisherSubscription extends SyncPublisherSubscription<Long> {

        private final long from;
        private final long to;

        private volatile long current;

        LongRangePublisherSubscription(Subscriber<? super Long> subscriber, long from, long to) {
            super(subscriber);
            this.from = from;
            this.to = to;
            this.current = from;
        }

        @Override
        protected boolean handleRequest(long n) {
            long next = current;
            while (n > 0 && next < to) {
                if (cancelled) {
                    break;
                }

                subscriber.onNext(next);
                next++;
                n--;
            }

            current = next;
            if (next == to && !cancelled) {
                subscriber.onComplete();
            }

            return next < to;
        }
    }
}

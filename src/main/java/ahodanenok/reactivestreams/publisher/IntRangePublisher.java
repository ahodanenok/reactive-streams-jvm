package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Subscriber;

public class IntRangePublisher extends AbstractPublisher<Integer> {

    private final int from;
    private final int to;

    public IntRangePublisher(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    protected void doSubscribe(Subscriber<? super Integer> subscriber) {
        subscriber.onSubscribe(new IntRangePublisherSubscription(subscriber, from, to));
    }

    static class IntRangePublisherSubscription extends AbstractSubscription<Integer> {

        private int current;
        private final int to;

        private volatile long requested;
        private long emitted;
        private boolean emitting;

        IntRangePublisherSubscription(Subscriber<? super Integer> subscriber, int from, int to) {
            super(subscriber);
            this.current = current;
            this.to = to;
        }

        @Override
        protected void onRequest(long n) {
            requested = Utils.addRequested(requested, n);
            if (emitting) {
                return;
            }

            emitting = true;
            try {
                while (current < to && emitted < requested) {
                    if (isCancelled()) {
                        break;
                    }

                    value(current);
                    current++;
                    emitted++;
                }
            } finally {
                emitting = false;
            }

            if (current == to) {
                complete();
            }
        }
    }
}

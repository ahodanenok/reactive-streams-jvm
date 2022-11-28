package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;

public class TicksPublisher extends AbstractPublisher<Long> {

    private final long count;
    private final long period;
    private final TimeUnit unit;

    public TicksPublisher(long count, long period, TimeUnit unit) {
        if (count <= 0) {
            throw new IllegalArgumentException("count <= 0");
        }

        if (period <= 0) {
            throw new IllegalArgumentException("period <= 0");
        }

        Objects.requireNonNull(unit, "unit");
        this.count = count;
        this.period = period;
        this.unit = unit;
    }

    @Override
    protected void doSubscribe(Subscriber<? super Long> subscriber) {
        new TicksPublisherSubscription(subscriber, count, period, unit).init();
    }

    static class TicksPublisherSubscription extends AbstractSubscription<Long> implements Runnable {

        private final long count;
        private final long period;
        private final TimeUnit unit;

        private ScheduledExecutorService executor;
        private volatile long tick;

        TicksPublisherSubscription(Subscriber<? super Long> subscriber, long count, long period, TimeUnit unit) {
            super(subscriber);
            this.count = count;
            this.period = period;
            this.unit = unit;
        }

        @Override
        protected void onInit() {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this, 0, period, unit);
        }

        @Override
        public void run() {
            value(tick);
            tick++;
            if (tick == count) {
                complete();
            }
        }

        @Override
        protected void onCancel() {
            executor.shutdown();
        }
    }
}

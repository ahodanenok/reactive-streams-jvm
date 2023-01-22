package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class TicksPublisher implements Publisher<Long> {

    private final long maxReceived;
    private final long period;
    private final TimeUnit unit;

    private ScheduledExecutorService executor;

    public TicksPublisher(long maxReceived, long period, TimeUnit unit) {
        if (maxReceived < 0) {
            throw new IllegalArgumentException("maxReceived < 0");
        }

        if (period <= 0) {
            throw new IllegalArgumentException("period <= 0");
        }

        Objects.requireNonNull(unit, "unit");
        this.maxReceived = maxReceived;
        this.period = period;
        this.unit = unit;
    }

    @Override
    public void subscribe(Subscriber<? super Long> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        new TicksPublisherSubscription(subscriber, maxReceived, period, unit).init();
    }

    static class TicksPublisherSubscription implements Subscription, Runnable {

        private final Subscriber<? super Long> downstream;
        private final long maxReceived;
        private final long period;
        private final TimeUnit unit;

        private ScheduledExecutorService executor;
        private volatile long tick;
        private volatile long signalled;
        private AtomicLong requested;
        private AtomicBoolean cancelled;

        TicksPublisherSubscription(Subscriber<? super Long> subscriber, long maxReceived, long period, TimeUnit unit) {
            this.downstream = subscriber;
            this.maxReceived = maxReceived;
            this.period = period;
            this.unit = unit;
            this.requested = new AtomicLong(0);
            this.cancelled = new AtomicBoolean(false);
        }

        void init() {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this, 0, period, unit);
            downstream.onSubscribe(this);
        }

        @Override
        public void run() {
            try {
                doRun();
            } catch (Throwable e) {
                try {
                    downstream.onError(e);
                } finally {
                    cancel();
                }
            }
        }

        void doRun() {
            if (signalled < requested.longValue() && signalled < maxReceived) {
                downstream.onNext(tick);
                signalled++;
            }

            tick++;
            if (signalled == maxReceived) {
                try {
                    downstream.onComplete();
                } finally {
                    cancel();
                }
            }
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                try {
                    downstream.onError(new IllegalArgumentException("requested amount must be > 0: " + n));
                } finally {
                    cancel();
                }
            }

            Fn.getAndAddRequested(requested, n);
        }

        @Override
        public void cancel() {
            if (!cancelled.getAndSet(true)) {
                executor.shutdown();
            }
        }
    }
}

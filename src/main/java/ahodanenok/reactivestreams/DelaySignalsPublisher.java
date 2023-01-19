package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class DelaySignalsPublisher<T> implements Publisher<T> {

    private final Publisher<T> publisher;
    private final long delay;
    private final TimeUnit unit;

    public DelaySignalsPublisher(Publisher<T> publisher, long delay, TimeUnit unit) {
        Objects.requireNonNull(publisher, "publisher");
        Objects.requireNonNull(unit, "unit");
        if (delay <= 0) {
            throw new IllegalArgumentException("delay <= 0");
        }

        this.publisher = publisher;
        this.delay = delay;
        this.unit = unit;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        publisher.subscribe(new DelaySignalsPublisherSubscription<>(subscriber, delay, unit));
    }

    class DelaySignalsPublisherSubscription<T> extends ProcessingSubscriber<T, T> {

        private final long delay;
        private final TimeUnit unit;

        private ScheduledExecutorService executor;
        private AtomicBoolean destroyed;

        DelaySignalsPublisherSubscription(Subscriber<? super T> subscriber, long delay, TimeUnit unit) {
            super(subscriber);
            this.delay = delay;
            this.unit = unit;
            this.executor = Executors.newSingleThreadScheduledExecutor();
            this.destroyed = new AtomicBoolean(false);
        }

        @Override
        protected void handleNext(final T value) {
            executor.schedule(() -> downstream.onNext(value), delay, unit);
        }

        @Override
        protected void handleError(final Throwable e) {
            executor.schedule(() -> {
                try {
                    downstream.onError(e);
                } finally {
                    cancel();
                }
            }, delay, unit);
        }

        @Override
        protected void handleComplete() {
            executor.schedule(() -> {
                try {
                    downstream.onComplete();
                } finally {
                    cancel();
                }
            }, delay, unit);
        }

        @Override
        protected void onCancel() {
            if (!destroyed.getAndSet(true)) {
                executor.shutdown();
                executor = null;
            }
        }
    }
}

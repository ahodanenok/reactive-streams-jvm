package ahodanenok.reactivestreams.processor;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.*;

public class DelayProcessor<T> extends AbstractTransformingProcessor<T, T> {

    private final long delay;
    private final TimeUnit unit;
    private ScheduledExecutorService executor;

    public DelayProcessor(long delay, TimeUnit unit) {
        if (delay <= 0) {
            throw new IllegalArgumentException("delay <= 0");
        }

        Objects.requireNonNull(unit, "unit");
        this.delay = delay;
        this.unit = unit;
    }

    @Override
    protected DelayProcessorSubscription<T> createSubscription(Subscriber<? super T> subscriber) {
        return new DelayProcessorSubscription<>(subscriber);
    }

    @Override
    protected void processNext(final T value) {
        executor.schedule(() -> downstream.value(value), delay, unit);
    }

    @Override
    protected void processError(final Throwable e) {
        executor.schedule(() -> downstream.error(e), delay, unit);
    }

    @Override
    protected void processComplete() {
        executor.schedule(() -> downstream.complete(), delay, unit);
    }

    class DelayProcessorSubscription<T> extends AbstractProcessorSubscription<T> {

        DelayProcessorSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        protected void onInit() {
            executor = Executors.newSingleThreadScheduledExecutor();
        }

        @Override
        protected void onDispose() {
            executor.shutdown();
            executor = null;
        }
    }
}

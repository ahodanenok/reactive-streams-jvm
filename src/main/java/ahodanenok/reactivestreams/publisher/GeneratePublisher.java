    package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.reactivestreams.Subscriber;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SyncIncrementalChannel;

public class GeneratePublisher<T> extends AbstractPublisherV2<T> {

    private final T initial;
    private final BiConsumer<T, ValueCallback<T>> generator;

    private T prev;
    private boolean allowNext;
    private ValueCallback<T> callback;

    public GeneratePublisher(T initial, BiConsumer<T, ValueCallback<T>> generator) {
        Objects.requireNonNull(generator, "generator");
        this.initial = initial;
        this.generator = generator;
    }

    @Override
    protected Channel<T> createChannel(Subscriber<? super T> subscriber) {
        return new SyncIncrementalChannel<>(subscriber);
    }

    @Override
    protected void onActivate() {
        prev = initial;
        callback = new ValueCallback<T>() {

            @Override
            public void signalValue(T value) {
                if (isDestroyed()) {
                    return;
                }

                if (!allowNext) {
                    GeneratePublisher.this.signalError(new IllegalStateException("Multiple resolve calls aren't allowed"));
                    return;
                }

                allowNext = false;
                prev = value;
                GeneratePublisher.this.signalNext(value);
            }

            @Override
            public void signalError(Throwable error) {
                GeneratePublisher.this.signalError(error);
            }

            @Override
            public void signalComplete() {
                GeneratePublisher.this.signalComplete();
            }
        };
    }

    @Override
    protected void onRequest(long n) {
        allowNext = true;
        try {
            generator.accept(prev, callback);
        } catch (Throwable e) {
            signalError(e);
        }
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new GeneratePublisherSubscription<>(subscriber, initial, generator));
    }

    static class GeneratePublisherSubscription<T> extends AbstractSubscription<T> implements ValueCallback<T> {

        private final BiConsumer<T, ValueCallback<T>> generator;
        private final RequestedSupport requestedSupport;

        private T prev;
        private boolean allowNext;

        GeneratePublisherSubscription(Subscriber<? super T> subscriber, T initial, BiConsumer<T, ValueCallback<T>> generator) {
            super(subscriber);
            this.prev = initial;
            this.generator = generator;
            this.requestedSupport = new RequestedSupport(this::next);
        }

        @Override
        protected void onRequest(long n) {
            requestedSupport.request(n);
        }

        @Override
        protected void onDispose() {
            requestedSupport.dispose();
        }

        @Override
        public void resolve(T value) {
            if (isCancelled()) {
                return;
            }

            if (!allowNext) {
                error(new IllegalStateException("Multiple resolve calls aren't allowed"));
                return;
            }

            allowNext = false;
            prev = value;
            value(value);
        }

        private void next() {
            allowNext = true;
            try {
                generator.accept(prev, this);
            } catch (Throwable e) {
                e.printStackTrace();
                error(e);
            }
        }
    }*/
}

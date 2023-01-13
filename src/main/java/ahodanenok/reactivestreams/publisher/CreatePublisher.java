package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import org.reactivestreams.Subscriber;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.AsyncIncrementalChannel;

public class CreatePublisher<T> extends AbstractPublisherV2<T> {

    private final Consumer<FlowCallback<T>> consumer;

    private FlowCallback<T> callback;
    private volatile long pendingRequested;
    private volatile LongConsumer onRequestAction;
    private volatile Action onCancelAction;

    public CreatePublisher(Consumer<FlowCallback<T>> consumer) {
        Objects.requireNonNull(consumer, "consumer");
        this.consumer = consumer;
    }

    @Override
    protected Channel<T> createChannel(Subscriber<? super T> subscriber) {
        return new AsyncIncrementalChannel<>(subscriber, -1);
    }

    @Override
    protected void onActivate() {
        consumer.accept(new FlowCallback<>() {

            @Override
            public void setOnCancel(Action action) {
                onCancelAction = action;
                if (isDestroyed()) {
                    onCancelAction.execute();
                }
            }

            @Override
            public void setOnRequest(LongConsumer action) {
                onRequestAction = action;
                if (pendingRequested > 0) {
                    long n = pendingRequested;
                    pendingRequested = 0;
                    onRequestAction.accept(n);
                }
            }

            @Override
            public boolean isCancelled() {
                return isDestroyed();
            }

            @Override
            public void signalNext(T value) {
                CreatePublisher.this.signalNext(value);
            }

            @Override
            public void signalError(Throwable error) {
                CreatePublisher.this.signalError(error);
            }

            @Override
            public void signalComplete() {
                CreatePublisher.this.signalComplete();
            }
        });
    }

    @Override
    protected void onRequest(long n) {
        if (onRequestAction != null) {
            onRequestAction.accept(n);
        } else {
            pendingRequested = Utils.addRequested(pendingRequested, n);
        }
    }

    @Override
    protected void onDestroy() {
        if (onCancelAction != null) {
            onCancelAction.execute();
        }
    }

    /*@Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        CreatePublisherSubscription<T> subscription = new CreatePublisherSubscription<>(subscriber);
        subscription.init();
        consumer.accept(subscription);
    }

    static class CreatePublisherSubscription<T> extends AbstractSubscription<T> implements FlowCallback<T> {

        private volatile long pendingRequested;
        private volatile LongConsumer onRequestAction;
        private volatile Action onCancelAction;

        CreatePublisherSubscription(Subscriber<? super T> downstream) {
            super(downstream);
        }

        @Override
        public void setOnRequest(LongConsumer action) {
            this.onRequestAction = action;
            if (pendingRequested > 0) {
                executeOnRequestAction(pendingRequested);
                pendingRequested = 0;
            }
        }

        @Override
        protected void onRequest(long n) {
            if (onRequestAction != null) {
                executeOnRequestAction(n);
            } else {
                pendingRequested = Utils.addRequested(pendingRequested, n);
            }
        }

        @Override
        public void setOnCancel(Action action) {
            this.onCancelAction = action;
            if (isCancelled()) {
                executeOnCancelAction();
            }
        }

        @Override
        protected void onCancel() {
            executeOnCancelAction();
        }

        private void executeOnRequestAction(long n) {
            if (onRequestAction != null) {
                try {
                    onRequestAction.accept(n);
                } catch (Throwable e) {
                    e.printStackTrace();
                    error(e);
                }
            }
        }

        private void executeOnCancelAction() {
            if (onCancelAction != null) {
                try {
                    onCancelAction.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}

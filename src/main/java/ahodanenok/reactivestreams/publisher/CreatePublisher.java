package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import org.reactivestreams.Subscriber;

public class CreatePublisher<T> extends AbstractPublisher<T> {

    private final Consumer<FlowCallback<T>> consumer;

    public CreatePublisher(Consumer<FlowCallback<T>> consumer) {
        Objects.requireNonNull(consumer, "consumer");
        this.consumer = consumer;
    }

    @Override
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
    }
}

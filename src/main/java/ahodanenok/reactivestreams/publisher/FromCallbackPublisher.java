package ahodanenok.reactivestreams.publisher;

import java.util.function.Consumer;
import java.util.function.LongConsumer;
import org.reactivestreams.Subscriber;

public class FromCallbackPublisher<T> extends AbstractPublisher<T> {

    private final Consumer<PublisherCallback<T>> callback;

    public FromCallbackPublisher(Consumer<PublisherCallback<T>> callback) {
        this.callback = callback;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        FromCallbackSubscription subscription = new FromCallbackSubscription<>(subscriber);
        subscriber.onSubscribe(subscription);
        callback.accept(subscription);
    }

    static class FromCallbackSubscription<T> extends AbstractSubscription<T> implements PublisherCallback<T> {

        private LongConsumer onRequestAction;
        private Action onCancelAction;

        FromCallbackSubscription(Subscriber<? super T> downstream) {
            super(downstream);
        }

        @Override
        public void setOnRequest(LongConsumer action) {
            this.onRequestAction = action;
        }

        @Override
        protected void onRequest(long n) {
            if (onRequestAction != null) {
                onRequestAction.accept(n);
            }
        }

        @Override
        public void setOnCancel(Action action) {
            this.onCancelAction = action;
        }

        @Override
        protected void onCancel() {
            if (onCancelAction != null) {
                onCancelAction.execute();
            }
        }
    }
}

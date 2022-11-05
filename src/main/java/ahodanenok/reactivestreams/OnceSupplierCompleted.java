package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import java.util.function.Supplier;

public class OnceSupplierCompleted<T> extends Once<T> {

    private final Supplier<T> supplier;

    OnceSupplierCompleted(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new OnceSupplierCompletedSub(subscriber));
    }

    class OnceSupplierCompletedSub implements Subscription {

        private Subscriber<? super T> subscriber;
        private boolean cancelled;

        OnceSupplierCompletedSub(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (cancelled) {
                return;
            }

            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException());
                return;
            }

            T value;
            try {
                value = supplier.get();
            } catch (Exception e) {
                e.printStackTrace(); // todo: log
                subscriber.onError(e);
                cancel();
                return;
            }

            if (value == null) {
                // todo: are nulls allowed?
                throw new NullPointerException();
            }

            if (!cancelled) {
                subscriber.onNext(value);
            }

            if (!cancelled) {
                subscriber.onComplete();
            }

            cancel();
        }

        @Override
        public void cancel() {
            cancelled = true;
        }
    }
}

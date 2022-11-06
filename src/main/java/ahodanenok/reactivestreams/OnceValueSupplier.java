package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import java.util.function.Supplier;

public class OnceValueSupplier<T> extends Once<T> {

    private final Supplier<T> supplier;

    OnceValueSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new OnceValueSupplierSubscription(subscriber));
    }

    class OnceValueSupplierSubscription extends OnceSubscription<T> {

        OnceValueSupplierSubscription(Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        protected void onRequest() {
            complete(supplier.get());
        }
    }
}

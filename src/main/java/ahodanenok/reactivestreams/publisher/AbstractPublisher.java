package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public abstract class AbstractPublisher<T> implements Publisher<T> {

    @Override
    public final void subscribe(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscribe: subscriber is null"); // 1.9
        }

        doSubscribe(subscriber);
    }

    protected abstract void doSubscribe(Subscriber<? super T> subscriber);
}

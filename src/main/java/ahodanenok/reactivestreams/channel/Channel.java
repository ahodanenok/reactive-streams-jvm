package ahodanenok.reactivestreams.channel;

import org.reactivestreams.Subscription;

/**
 * Point-to-point connection between a single publisher and a subscriber.
 */
public interface Channel<T> extends Subscription {

    void connect(Subscription upstream);

    void activate();

    void signalNext(T value);

    void signalError(Throwable error);

    void signalComplete();
}
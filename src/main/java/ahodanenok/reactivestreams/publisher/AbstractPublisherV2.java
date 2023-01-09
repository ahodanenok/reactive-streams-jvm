package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SimpleChannel;

public abstract class AbstractPublisherV2<T> implements Publisher<T> {

    private Channel channel;

    @Override
    public final void subscribe(Subscriber<? super T> subscriber) {
        channel = createChannel(subscriber);
        channel.connect(new Subscription() {
            @Override
            public void request(long n) {
                handleRequest(n);
            }

            @Override
            public void cancel() {
                handleCancel();
            }
        });
        channel.activate();
        onActivate();
    }

    private void handleRequest(long n) {
        onRequest(n);
    }

    private void handleCancel() {
        onCancel();
        handleDispose();
    }

    private void handleDispose() {
        onDispose();
    }

    protected final void signalNext(T value) {
        channel.signalNext(value);
    }

    protected final void signalError(Throwable error) {
        channel.signalError(error);
        handleDispose();
    }

    protected final void signalComplete() {
        channel.signalComplete();
        handleDispose();
    }

    protected Channel createChannel(Subscriber<? super T> subscriber) {
        return new SimpleChannel(subscriber);
    }

    protected void onActivate() {
        // no-op
    }

    protected void onRequest(long n) {
        // no-op
    }

    protected void onCancel() {
        // no-op
    }

    protected void onDispose() {
        // no-op
    }
}

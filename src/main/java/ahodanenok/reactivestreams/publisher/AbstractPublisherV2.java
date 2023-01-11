package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SimpleChannel;

public abstract class AbstractPublisherV2<T> implements Publisher<T> {

    private Channel channel;
    private boolean destroyed;

    protected final boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public final void subscribe(Subscriber<? super T> subscriber) {
        onInit();

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
        onDisconnect();
        handleDestroy();
    }

    private void handleDestroy() {
        if (destroyed) {
            return;
        }

        destroyed = true;
        onDestroy();
    }

    protected final void signalNext(T value) {
        channel.signalNext(value);
    }

    protected final void signalError(Throwable error) {
        channel.signalError(error);
        handleDestroy();
    }

    protected final void signalComplete() {
        channel.signalComplete();
        handleDestroy();
    }

    protected Channel createChannel(Subscriber<? super T> subscriber) {
        return new SimpleChannel(subscriber);
    }

    protected void onInit() {
        // no-op
    }

    protected void onActivate() {
        // no-op
    }

    protected void onRequest(long n) {
        // no-op
    }

    protected void onDisconnect() {
        // no-op
    }

    protected void onDestroy() {
        // no-op
    }
}

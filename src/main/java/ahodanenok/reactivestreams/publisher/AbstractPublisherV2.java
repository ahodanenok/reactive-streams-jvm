package ahodanenok.reactivestreams.publisher;

import java.util.Objects;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SimpleChannel;
import ahodanenok.reactivestreams.channel.ErrorChannel;

public abstract class AbstractPublisherV2<T> implements Publisher<T> {

    private Channel channel;
    private volatile boolean destroyed;

    protected final boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public final void subscribe(Subscriber<? super T> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");

        try {
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
        } catch (Throwable e) {
            ErrorChannel.send(subscriber, e);
            return;
        }

        channel.activate();

        try {
            onActivate();
        } catch (Throwable e) {
            signalError(e);
        }
    }

    private void handleRequest(long n) {
        if (destroyed) {
            return;
        }

        onRequest(n);
    }

    private void handleCancel() {
        if (destroyed) {
            return;
        }

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
        if (destroyed) {
            return;
        }

        channel.signalNext(value);
    }

    protected final void signalError(Throwable error) {
        if (destroyed) {
            return;
        }

        try {
            channel.signalError(error);
        } finally {
            handleDestroy();
        }
    }

    protected final void signalComplete() {
        if (destroyed) {
            return;
        }

        try {
            channel.signalComplete();
        } finally {
            handleDestroy();
        }
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

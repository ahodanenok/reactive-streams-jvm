package ahodanenok.reactivestreams.publisher;

import java.util.Objects;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import ahodanenok.reactivestreams.channel.Channel;
import ahodanenok.reactivestreams.channel.SimpleChannel;
import ahodanenok.reactivestreams.channel.ErrorChannel;

public abstract class AbstractPublisherV2<T> implements Publisher<T> {

    private final Object stateLock = new Object();

    protected enum State {

        CREATED, INIT, ACTIVE, DESTROYED;
    }

    private Channel channel;
    private volatile State state = State.CREATED;

    protected final boolean isDestroyed() {
        return state == State.DESTROYED;
    }

    @Override
    public final void subscribe(Subscriber<? super T> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");

        synchronized (stateLock) {
            if (state != State.CREATED) {
                ErrorChannel.send(subscriber, new IllegalStateException("Publisher already has a subscriber"));
                return;
            }

            state = State.INIT;
        }

        try {
            onInit();
        } catch (Throwable e) {
            e.printStackTrace();
            ErrorChannel.send(subscriber, e);
            return;
        }

        try {
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
            e.printStackTrace();
            ErrorChannel.send(subscriber, e);
            return;
        }

        try {
            channel.activate();
        } catch (Throwable e) {
            e.printStackTrace();
            handleDestroy();
            throw e;
        }

        handleActivate();
    }

    private void handleActivate() {
        // additional check to prevent acquiring lock on each onRequest call
        if (state != State.INIT) {
            return;
        }

        synchronized (stateLock) {
            if (state != State.INIT) {
                return;
            }

            state = State.ACTIVE;
        }

        try {
            onActivate();
        } catch (Throwable e) {
            e.printStackTrace();
            signalError(e);
        }
    }

    private void handleRequest(long n) {
        if (isDestroyed()) {
            return;
        }

        handleActivate();
        try {
            onRequest(n);
        } catch (Throwable e) {
            e.printStackTrace();
            signalError(e);
        }
    }

    private void handleCancel() {
        if (isDestroyed()) {
            return;
        }

        try {
            onDisconnect();
            handleDestroy();
        } catch (Throwable e) {
            e.printStackTrace();
            signalError(e);
        }
    }

    private void handleDestroy() {
        if (isDestroyed()) {
            return;
        }

        state = State.DESTROYED;
        channel = null;
        try {
            onDestroy();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected final void signalNext(T value) {
        if (isDestroyed()) {
            return;
        }

        try {
            channel.signalNext(value);
        } catch (Throwable e) {
            e.printStackTrace();
            signalError(e);
        }
    }

    protected final void signalError(Throwable error) {
        if (isDestroyed()) {
            return;
        }

        try {
            channel.signalError(error);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            handleDestroy();
        }
    }

    protected final void signalComplete() {
        if (isDestroyed()) {
            return;
        }

        try {
            channel.signalComplete();
        } catch (Throwable e) {
            e.printStackTrace();
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

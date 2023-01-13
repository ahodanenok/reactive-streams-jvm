package ahodanenok.reactivestreams.publisher;

import java.util.function.LongConsumer;

public interface FlowCallback<T> {

    void setOnRequest(LongConsumer action);

    void setOnCancel(Action action);

    boolean isCancelled();

    void signalNext(T value);

    void signalComplete();

    void signalError(Throwable e);
}

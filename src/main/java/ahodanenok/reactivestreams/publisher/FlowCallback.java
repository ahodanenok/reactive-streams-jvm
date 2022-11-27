package ahodanenok.reactivestreams.publisher;

import java.util.function.LongConsumer;

public interface FlowCallback<T> {

    void setOnRequest(LongConsumer action);

    void setOnCancel(Action action);

    void value(T value);

    void complete(T value);

    void complete();

    void error(Throwable e);

    boolean isCancelled();
}

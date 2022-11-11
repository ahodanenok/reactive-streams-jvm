package ahodanenok.reactivestreams.publisher;

import java.util.function.LongConsumer;

// todo: add ability to check if cancelled?
public interface PublisherCallback<T> {

    void setOnRequest(LongConsumer action);

    void setOnCancel(Action action);

    void value(T value);

    void complete(T value);

    void complete();

    void error(Throwable e);
}

package ahodanenok.reactivestreams;

import java.util.Objects;

public interface ValueCallback<T> {

    void signalValue(T value);

    void signalError(Throwable error);

    void signalComplete();
}

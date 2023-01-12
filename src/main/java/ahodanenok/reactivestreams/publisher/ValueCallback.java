package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import org.reactivestreams.Subscriber;

public interface ValueCallback<T> {

    void signalValue(T value);

    void signalError(Throwable error);

    void signalComplete();
}

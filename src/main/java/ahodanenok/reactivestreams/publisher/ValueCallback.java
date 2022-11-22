package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import org.reactivestreams.Subscriber;

public interface ValueCallback<T> {

    void resolve(T value);

    void error(Throwable error);

    void complete();
}

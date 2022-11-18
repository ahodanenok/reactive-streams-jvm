package ahodanenok.reactivestreams.publisher;

import java.io.IOException;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorPublisherTest {

    @Test
    public void shouldReturnError() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ErrorPublisher<String> publisher = new ErrorPublisher<>(new IOException("test"));
        publisher.subscribe(subscriber);
        subscriber.expectError(IOException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfNullValue() {
        assertThrows(NullPointerException.class, () -> new ErrorPublisher<String>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        ErrorPublisher<Integer> publisher = new ErrorPublisher<>(new IllegalArgumentException("test"));
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }
}

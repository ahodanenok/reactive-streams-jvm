package ahodanenok.reactivestreams;

import java.io.IOException;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class EmptyPublisherTest {

    @Test
    public void shouldComplete() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        EmptyPublisher<String> publisher = new EmptyPublisher<>();
        publisher.subscribe(subscriber);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        EmptyPublisher<Integer> publisher = new EmptyPublisher<>();
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }
}

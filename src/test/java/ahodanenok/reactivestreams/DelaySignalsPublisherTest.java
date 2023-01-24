package ahodanenok.reactivestreams;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import ahodanenok.reactivestreams.*;

public class DelaySignalsPublisherTest {

    @Test
    public void shouldDelayEmptyPublisher() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelaySignalsPublisher<String> processor =
            new DelaySignalsPublisher<>(new EmptyPublisher<>(), 50, TimeUnit.MILLISECONDS);
        processor.subscribe(subscriber);
        subscriber.expectNone(49);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldDelaySingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelaySignalsPublisher<String> processor =
            new DelaySignalsPublisher<>(new ValuePublisher<>("test"), 25, TimeUnit.MILLISECONDS);
        processor.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNone(24);
        subscriber.expectNext("test");
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldDelayMultipleValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelaySignalsPublisher<String> processor =
            new DelaySignalsPublisher<>(new IterablePublisher<>(List.of("a", "b", "c")), 30, TimeUnit.MILLISECONDS);
        processor.subscribe(subscriber);
        subscriber.request(5);
        subscriber.expectNone(29);
        subscriber.expectNext("a");
        subscriber.expectNext("b");
        subscriber.expectNext("c");
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldDelayError() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelaySignalsPublisher<String> processor =
            new DelaySignalsPublisher<>(new ErrorPublisher<String>(new RuntimeException("error!")), 35, TimeUnit.MILLISECONDS);
        processor.subscribe(subscriber);
        subscriber.expectNone(34);
        subscriber.expectError(RuntimeException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfNullUnit() {
        assertThrows(NullPointerException.class,
            () -> new DelaySignalsPublisher<>(new NeverPublisher<>(), 10, null));
    }

    @Test
    public void shouldThrowIllegalArgumentIfDelayInvalid() {
        assertThrows(IllegalArgumentException.class,
            () -> new DelaySignalsPublisher<>(new NeverPublisher<>(), 0, TimeUnit.MILLISECONDS));
        assertThrows(IllegalArgumentException.class,
            () -> new DelaySignalsPublisher<>(new NeverPublisher<>(), -10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        DelaySignalsPublisher<Integer> publisher = new DelaySignalsPublisher<>(new NeverPublisher<>(), 1, TimeUnit.MILLISECONDS);
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelaySignalsPublisher<String> publisher =
            new DelaySignalsPublisher<>(new ValuePublisher<>("test"), 1, TimeUnit.MILLISECONDS);
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

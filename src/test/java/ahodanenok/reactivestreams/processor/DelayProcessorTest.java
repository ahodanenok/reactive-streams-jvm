package ahodanenok.reactivestreams.processor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import ahodanenok.reactivestreams.*;
import ahodanenok.reactivestreams.publisher.*;

public class DelayProcessorTest {

    @Test
    public void shouldDelayEmptyPublisher() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelayProcessor<String> processor = new DelayProcessor<>(50, TimeUnit.MILLISECONDS);
        new EmptyPublisher<String>().subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.expectNone(49);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldDelaySingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelayProcessor<String> processor = new DelayProcessor<>(25, TimeUnit.MILLISECONDS);
        new ValuePublisher<>("test").subscribe(processor);
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
        DelayProcessor<String> processor = new DelayProcessor<>(30, TimeUnit.MILLISECONDS);
        new IterablePublisher<>(List.of("a", "b", "c")).subscribe(processor);
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
        DelayProcessor<String> processor = new DelayProcessor<>(35, TimeUnit.MILLISECONDS);
        new ErrorPublisher<String>(new RuntimeException("error!")).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.expectNone(34);
        subscriber.expectError(RuntimeException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfNullUnit() {
        assertThrows(NullPointerException.class, () -> new DelayProcessor<>(10, null));
    }

    @Test
    public void shouldThrowIllegalArgumentIfDelayInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new DelayProcessor<>(0, TimeUnit.MILLISECONDS));
        assertThrows(IllegalArgumentException.class, () -> new DelayProcessor<>(-10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        DelayProcessor<Integer> processor = new DelayProcessor<>(1, TimeUnit.MILLISECONDS);
        assertThrows(NullPointerException.class, () -> processor.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DelayProcessor<String> processor = new DelayProcessor<>(1, TimeUnit.MILLISECONDS);
        processor.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

package ahodanenok.reactivestreams;

import java.util.List;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import ahodanenok.reactivestreams.*;

public class FilterPublisherTest {

    @Test
    public void shouldFilterEmptyPublisher() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<Integer> publisher = new FilterPublisher<>(new EmptyPublisher<>(), n -> true);
        publisher.subscribe(subscriber);
        subscriber.expectNone();
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldAcceptSingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<Integer> publisher = new FilterPublisher<>(new ValuePublisher<>(10), n -> true);
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(10);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldRejectSingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<Integer> publisher = new FilterPublisher<>(new ValuePublisher<>(10), n -> false);
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }


    @Test
    public void shouldAcceptAllValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<Integer> publisher = new FilterPublisher<>(new IterablePublisher<>(List.of(1, 3, 5, 7)), n -> true);
        publisher.subscribe(subscriber);
        subscriber.request(5);
        subscriber.expectNext(1);
        subscriber.expectNext(3);
        subscriber.expectNext(5);
        subscriber.expectNext(7);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldRejectAllValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<Integer> publisher = new FilterPublisher<>(new IterablePublisher<>(List.of(1, 3, 5, 7)), n -> false);
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 3, 5, 7 })
    public void shouldAcceptOneValueFromMultiple(int allowed) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<Integer> publisher = new FilterPublisher<>(new IterablePublisher<>(List.of(1, 3, 5, 7)), n -> n == allowed);
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(allowed);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldAcceptSomeValuesFromMultiple() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<Integer> publisher = new FilterPublisher<>(new IterablePublisher<>(List.of(1, 3, 5, 7)), n -> n == 3 || n == 5);
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(3);
        subscriber.request(1);
        subscriber.expectNext(5);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldPassError() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<String> publisher = new FilterPublisher<>(new ErrorPublisher<>(new RuntimeException("error!")), n -> true);
        publisher.subscribe(subscriber);
        subscriber.expectError(RuntimeException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfPublisherNull() {
        assertThrows(NullPointerException.class, () -> new FilterPublisher<String>(null, n -> true));
    }

    @Test
    public void shouldThrowNpeIfPredicateNull() {
        assertThrows(NullPointerException.class, () -> new FilterPublisher<String>(new EmptyPublisher<>(), null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        FilterPublisher<String> publisher = new FilterPublisher<>(new EmptyPublisher<>(), n -> true);
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterPublisher<String> publisher = new FilterPublisher<>(new ValuePublisher("abc"), n -> true);
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

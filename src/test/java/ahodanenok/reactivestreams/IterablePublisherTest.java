package ahodanenok.reactivestreams;

import java.util.List;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class IterablePublisherTest {

    @Test
    public void shouldCompleteWithoutAnyValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        IterablePublisher<Integer> publisher = new IterablePublisher<>(List.of());
        publisher.subscribe(subscriber);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendValueAfterCompletion() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        IterablePublisher<Integer> publisher = new IterablePublisher<>(List.of(2, 3, 4));
        publisher.subscribe(subscriber);
        subscriber.request(3);
        subscriber.expectNext(2);
        subscriber.expectNext(3);
        subscriber.expectNext(4);
        subscriber.expectCompletion();
        subscriber.request(1);
        subscriber.expectNone();
    }

    @Test
    public void shouldSendLessThanRequested() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        IterablePublisher<Integer> publisher = new IterablePublisher<>(List.of(0, 1, 2, 3, 4));
        publisher.subscribe(subscriber);
        subscriber.request(10);
        subscriber.expectNext(0);
        subscriber.expectNext(1);
        subscriber.expectNext(2);
        subscriber.expectNext(3);
        subscriber.expectNext(4);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendMoreThanRequested() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        IterablePublisher<Integer> publisher = new IterablePublisher<>(List.of(7, 8, 9, 10, 11, 12, 13));
        publisher.subscribe(subscriber);
        subscriber.request(2);
        subscriber.expectNext(7);
        subscriber.expectNext(8);
        subscriber.expectNone();
        subscriber.request(1);
        subscriber.expectNext(9);
        subscriber.expectNone();
        subscriber.request(3);
        subscriber.expectNext(10);
        subscriber.expectNext(11);
        subscriber.expectNext(12);
        subscriber.expectNone();
        subscriber.cancel();
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfIterableNull() {
        assertThrows(NullPointerException.class, () -> new IterablePublisher(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        IterablePublisher<Integer> publisher = new IterablePublisher(List.of());
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        IterablePublisher<Integer> publisher = new IterablePublisher(List.of(1, 2, 3));
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

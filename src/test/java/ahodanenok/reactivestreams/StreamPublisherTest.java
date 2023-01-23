package ahodanenok.reactivestreams;

import java.util.stream.IntStream;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class StreamPublisherTest {

    @Test
    public void shouldCompleteWithoutAnyValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        StreamPublisher<Integer> publisher = new StreamPublisher<>(IntStream.empty().boxed());
        publisher.subscribe(subscriber);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendValueAfterCompletion() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        StreamPublisher<Integer> publisher = new StreamPublisher<>(IntStream.range(2, 5).boxed());
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
        StreamPublisher<Integer> publisher = new StreamPublisher<>(IntStream.range(0, 5).boxed());
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
        StreamPublisher<Integer> publisher = new StreamPublisher<>(IntStream.range(7, 15).boxed());
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
        assertThrows(NullPointerException.class, () -> new StreamPublisher<>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        StreamPublisher<Integer> publisher = new StreamPublisher<>(IntStream.range(2, 3).boxed());
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        StreamPublisher<Integer> publisher = new StreamPublisher<>(IntStream.range(2, 3).boxed());
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

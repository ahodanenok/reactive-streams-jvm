package ahodanenok.reactivestreams;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class LongRangePublisherTest {

    @Test
    public void shouldCompleteWithoutAnyValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        LongRangePublisher publisher = new LongRangePublisher(10, 10);
        publisher.subscribe(subscriber);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendValueAfterCompletion() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        LongRangePublisher publisher = new LongRangePublisher(2, 5);
        publisher.subscribe(subscriber);
        subscriber.request(3);
        subscriber.expectNext(2L);
        subscriber.expectNext(3L);
        subscriber.expectNext(4L);
        subscriber.expectCompletion();
        subscriber.request(1);
        subscriber.expectNone();
    }

    @Test
    public void shouldSendLessThanRequested() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        LongRangePublisher publisher = new LongRangePublisher(0, 5);
        publisher.subscribe(subscriber);
        subscriber.request(10);
        subscriber.expectNext(0L);
        subscriber.expectNext(1L);
        subscriber.expectNext(2L);
        subscriber.expectNext(3L);
        subscriber.expectNext(4L);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendMoreThanRequested() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        LongRangePublisher publisher = new LongRangePublisher(7, 20);
        publisher.subscribe(subscriber);
        subscriber.request(2);
        subscriber.expectNext(7L);
        subscriber.expectNext(8L);
        subscriber.expectNone();
        subscriber.request(1);
        subscriber.expectNext(9L);
        subscriber.expectNone();
        subscriber.request(3);
        subscriber.expectNext(10L);
        subscriber.expectNext(11L);
        subscriber.expectNext(12L);
        subscriber.expectNone();
        subscriber.cancel();
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowIllegalArgumentIfFromGtTo() {
        assertThrows(IllegalArgumentException.class, () -> new LongRangePublisher(5, 4));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        LongRangePublisher publisher = new LongRangePublisher(0, 1);
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        LongRangePublisher publisher = new LongRangePublisher(1, 5);
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

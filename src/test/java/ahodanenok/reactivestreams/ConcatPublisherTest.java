package ahodanenok.reactivestreams;

import java.util.List;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConcatPublisherTest {

    @Test
    public void shouldSignalFromPublishersSequentially() throws Exception  {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ConcatPublisher<String> publisher = new ConcatPublisher<>(List.of(
            new ValuePublisher<>("a"),
            new ValuePublisher<>("b"),
            new ValuePublisher<>("c"),
            new ValuePublisher<>("d"),
            new ValuePublisher<>("e")));
        publisher.subscribe(subscriber);
        subscriber.request(5);
        subscriber.expectNext("a");
        subscriber.expectNext("b");
        subscriber.expectNext("c");
        subscriber.expectNext("d");
        subscriber.expectNext("e");
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSignalMoreThanRequested() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ConcatPublisher<Long> publisher = new ConcatPublisher<>(List.of(
            new LongRangePublisher(0, 2),
            new LongRangePublisher(3, 6),
            new LongRangePublisher(7, 10)));
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(0L);
        subscriber.expectNone();
        subscriber.request(2);
        subscriber.expectNext(1L);
        subscriber.expectNext(3L);
        subscriber.expectNone();
        subscriber.request(4);
        subscriber.expectNext(4L);
        subscriber.expectNext(5L);
        subscriber.expectNext(7L);
        subscriber.expectNext(8L);
        subscriber.expectNone();
        subscriber.request(5);
        subscriber.expectNext(9L);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldRequestFromNextRemainingRequestsFromPrevious() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ConcatPublisher<Long> publisher = new ConcatPublisher<>(List.of(
            new LongRangePublisher(0, 2),
            new LongRangePublisher(3, 6),
            new LongRangePublisher(7, 10)));
        publisher.subscribe(subscriber);
        subscriber.request(4);
        subscriber.expectNext(0L);
        subscriber.expectNext(1L);
        subscriber.expectNext(3L);
        subscriber.expectNext(4L);
        subscriber.expectNone();
        subscriber.request(7);
        subscriber.expectNext(5L);
        subscriber.expectNext(7L);
        subscriber.expectNext(8L);
        subscriber.expectNext(9L);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldStopSignallingOnCancel() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ConcatPublisher<Long> publisher = new ConcatPublisher<>(List.of(
            new LongRangePublisher(0, 2), new LongRangePublisher(3, 5)));
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(0L);
        subscriber.cancel();
        subscriber.request(2);
        subscriber.expectNone();
    }

    @Test
    public void shouldStopSignallingOnError() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ConcatPublisher<String> publisher = new ConcatPublisher<>(List.of(
            new ValuePublisher<>("a"),
            new ValuePublisher<>("b"),
            new ErrorPublisher(new RuntimeException("error!")),
            new ValuePublisher<>("d"),
            new ValuePublisher<>("e")));
        publisher.subscribe(subscriber);
        subscriber.request(5);
        subscriber.expectNext("a");
        subscriber.expectNext("b");
        subscriber.expectError(RuntimeException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfPublishersNull() {
        assertThrows(NullPointerException.class, () -> new ConcatPublisher<String>(null));
    }

    @Test
    public void shouldThrowNpeIfPublishersEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new ConcatPublisher<String>(List.of()));
    }

    @Test
    public void shouldThrowNpeIfPublishersContainsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new ConcatPublisher<String>(java.util.Arrays.asList(new ValuePublisher<>("a"), null)));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        ConcatPublisher<Integer> publisher = new ConcatPublisher<>(List.of(new ValuePublisher<>(0)));
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ConcatPublisher<String> publisher = new ConcatPublisher<>(List.of(new ValuePublisher<>("abc")));
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

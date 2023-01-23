package ahodanenok.reactivestreams;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePublisherTest {

    @Test
    public void shouldCompleteWithoutAnyValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        GeneratePublisher<Integer> publisher = new GeneratePublisher<>(10, (prev, callback) -> callback.signalComplete());
        publisher.subscribe(subscriber);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendValueAfterCompletion() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        GeneratePublisher<Integer> publisher = new GeneratePublisher<>(2, (prev, callback) -> {
            if (prev < 5) {
                callback.signalValue(prev + 1);
            } else {
                callback.signalComplete();
            }
        });
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
        GeneratePublisher<Integer> publisher = new GeneratePublisher<>(0, (prev, callback) -> {
            if (prev < 5) {
                callback.signalValue(prev + 1);
            } else {
                callback.signalComplete();
            }
        });
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
        GeneratePublisher<Integer> publisher = new GeneratePublisher<Integer>(7, (prev, callback) -> callback.signalValue(prev + 1));
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
    public void shouldEndWithErrorIfGeneratorResolvesMultipleTimes() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        GeneratePublisher<Integer> publisher = new GeneratePublisher<Integer>(0, (prev, callback) -> {
            callback.signalValue(100);
            callback.signalValue(200);
        });
        publisher.subscribe(subscriber);
        subscriber.request(20);
        subscriber.expectError(IllegalStateException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldEndWithErrorIfGeneratorFails() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        GeneratePublisher<Integer> publisher = new GeneratePublisher<Integer>(1, (prev, callback) -> {
            if (prev == 1) {
                callback.signalValue(100);
            } else {
                callback.signalError(new ClassCastException());
            }
        });
        publisher.subscribe(subscriber);
        subscriber.request(2);
        subscriber.expectNext(100);
        subscriber.expectError(ClassCastException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowIllegalArgumentIfGeneratorNull() {
        assertThrows(NullPointerException.class, () -> new GeneratePublisher<>("123", null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        GeneratePublisher<Integer> publisher = new GeneratePublisher<>(1, (prev, callback) -> callback.signalValue(prev + 1));
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        GeneratePublisher<Integer> publisher = new GeneratePublisher<>(1, (prev, callback) -> callback.signalValue(prev + 1));
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

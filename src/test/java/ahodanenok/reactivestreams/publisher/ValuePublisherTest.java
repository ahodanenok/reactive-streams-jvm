package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ValuePublisherTest {

    @ParameterizedTest
    @ValueSource(longs = { 1, 5, Long.MAX_VALUE })
    public void shouldSendValue(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ValuePublisher<String> publisher = new ValuePublisher<>("test_" + count);
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectNext("test_" + count);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendValueAfterCompletion() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ValuePublisher<String> publisher = new ValuePublisher<>("abc");
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.request(1);
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendValueIfCancelled() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ValuePublisher<String> publisher = new ValuePublisher<>("abc");
        publisher.subscribe(subscriber);
        subscriber.cancel();
        subscriber.request(1);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfNullValue() {
        assertThrows(NullPointerException.class, () -> new ValuePublisher<String>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        ValuePublisher<Integer> publisher = new ValuePublisher<>(1);
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        ValuePublisher<String> publisher = new ValuePublisher<>("test_" + count);
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);      
        subscriber.expectNone();  
    }
}

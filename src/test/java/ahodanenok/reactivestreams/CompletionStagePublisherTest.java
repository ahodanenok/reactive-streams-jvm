package ahodanenok.reactivestreams;

import java.util.concurrent.CompletableFuture;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class CompletionStagePublisherTest {

    @ParameterizedTest
    @ValueSource(longs = { 1, 5, Long.MAX_VALUE })
    public void shouldSendValue(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CompletionStagePublisher<String> publisher = new CompletionStagePublisher<>(
            CompletableFuture.completedFuture("test_" + count));
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectNext("test_" + count);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldSendValueAsync() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CompletionStagePublisher<String> publisher = new CompletionStagePublisher<>(
            CompletableFuture.completedFuture("test").supplyAsync(() -> "async"));
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext("async");
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldNotSendValueAfterCompletion() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CompletionStagePublisher<String> publisher = new CompletionStagePublisher<>(
            CompletableFuture.completedFuture("abc"));
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
        CompletionStagePublisher<String> publisher = new CompletionStagePublisher<>(
            CompletableFuture.completedFuture("abc"));
        publisher.subscribe(subscriber);
        subscriber.cancel();
        subscriber.request(1);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfNullValue() {
        assertThrows(NullPointerException.class, () -> new CompletionStagePublisher<String>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        CompletionStagePublisher<Integer> publisher = new CompletionStagePublisher<>(
            CompletableFuture.completedFuture(1));
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CompletionStagePublisher<String> publisher = new CompletionStagePublisher<>(
            CompletableFuture.completedFuture("test_" + count));
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ahodanenok.reactivestreams.NeverPublisher;
import ahodanenok.reactivestreams.ValuePublisher;

import static org.junit.jupiter.api.Assertions.*;

public class DeferPublisherTest {

    @Test
    public void shouldCallSupplierForEachSubscriber() throws Exception {
        int[] supplierCalledCount = { 0 };
        DeferPublisher<String> publisher = new DeferPublisher<>(() -> {
            supplierCalledCount[0]++;
            return new ValuePublisher<>("test");
        });

        for (int i = 0; i < 3; i++) {
            ManualSubscriberWithSubscriptionSupport<String> subscriber =
                new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
            publisher.subscribe(subscriber);
            subscriber.request(1);
            subscriber.expectNext("test");
            subscriber.expectCompletion();
            subscriber.expectNone();
        }

        assertEquals(3, supplierCalledCount[0]);
    }

    @Test
    public void shouldReturnNPEIfSupplierReturnsNull() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
                new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        DeferPublisher<String> publisher = new DeferPublisher<>(() -> null);
        publisher.subscribe(subscriber);
        subscriber.expectError(NullPointerException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfNullSupplier() {
        assertThrows(NullPointerException.class, () -> new DeferPublisher<String>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        DeferPublisher<Integer> publisher = new DeferPublisher<>(() -> new NeverPublisher<>());
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }
}

package ahodanenok.reactivestreams;

import java.util.concurrent.TimeUnit;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TicksPublisherTest {

    @Test
    public void shouldGenerateOneTick() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        TicksPublisher publisher = new TicksPublisher(1, 5, TimeUnit.MILLISECONDS);
        publisher.subscribe(subscriber);
        subscriber.request(100);
        subscriber.expectNext(0L, 20L);
        subscriber.expectCompletion(20L);
        subscriber.expectNone(50L);
    }

    @Test
    public void shouldGenerateTwoTicks() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        TicksPublisher publisher = new TicksPublisher(2, 2, TimeUnit.MILLISECONDS);
        publisher.subscribe(subscriber);
        subscriber.request(10);
        subscriber.expectNext(0L, 20L);
        subscriber.expectNext(1L, 20L);
        subscriber.expectCompletion(20L);
        subscriber.expectNone(50L);
    }

    @Test
    public void shouldGenerateTenTicks() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        TicksPublisher publisher = new TicksPublisher(10, 3, TimeUnit.MILLISECONDS);
        publisher.subscribe(subscriber);
        subscriber.request(100);
        subscriber.expectNext(0L, 20L);
        subscriber.expectNext(1L, 20L);
        subscriber.expectNext(2L, 20L);
        subscriber.expectNext(3L, 20L);
        subscriber.expectNext(4L, 20L);
        subscriber.expectNext(5L, 20L);
        subscriber.expectNext(6L, 20L);
        subscriber.expectNext(7L, 20L);
        subscriber.expectNext(8L, 20L);
        subscriber.expectNext(9L, 20L);
        subscriber.expectCompletion(20L);
        subscriber.expectNone(50L);
    }

    @Test
    public void shouldGenerateNoTicksAfterCancel() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        TicksPublisher publisher = new TicksPublisher(100, 5, TimeUnit.MILLISECONDS);
        publisher.subscribe(subscriber);
        subscriber.request(50);
        Thread.sleep(26L);
        subscriber.cancel();
        subscriber.expectNext(0L, 1L);
        subscriber.expectNext(1L, 1L);
        subscriber.expectNext(2L, 1L);
        subscriber.expectNext(3L, 1L);
        subscriber.expectNext(4L, 1L);
        subscriber.expectNone(50L);
    }

    @Test
    public void shouldGenerateOnlyAfterRequested() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        TicksPublisher publisher = new TicksPublisher(100, 5, TimeUnit.MILLISECONDS);
        publisher.subscribe(subscriber);
        Thread.sleep(26L);
        subscriber.request(3);
        subscriber.nextElements(3, 20);
        subscriber.expectNone(50L);
        subscriber.request(5);
        subscriber.nextElements(5, 30);
        subscriber.expectNone(50L);
        subscriber.cancel();
    }

    @Test
    public void shouldThrowNpeIfTimeUnitNull() {
        assertThrows(NullPointerException.class, () -> new TicksPublisher(1, 1, null));
    }

    @Test
    public void shouldThrowIllegalArgumentIfMaxReceivedInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new TicksPublisher(-10, 1, TimeUnit.SECONDS));
        assertThrows(IllegalArgumentException.class, () -> new TicksPublisher(-1, 1, TimeUnit.SECONDS));
    }

    @Test
    public void shouldThrowIllegalArgumentIfPeriodInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new TicksPublisher(10, -10, TimeUnit.SECONDS));
        assertThrows(IllegalArgumentException.class, () -> new TicksPublisher(1, -1, TimeUnit.SECONDS));
        assertThrows(IllegalArgumentException.class, () -> new TicksPublisher(2, 0, TimeUnit.SECONDS));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        TicksPublisher publisher = new TicksPublisher(1, 1, TimeUnit.SECONDS);
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Long> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        TicksPublisher publisher = new TicksPublisher(10, 5, TimeUnit.MILLISECONDS);
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

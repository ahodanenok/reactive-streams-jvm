package ahodanenok.reactivestreams;

import java.util.List;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import ahodanenok.reactivestreams.*;
import ahodanenok.reactivestreams.publisher.*;

public class MapPublisherTest {

    @Test
    public void shouldMapEmptyPublisher() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapPublisher<String, Integer> publisher = new MapPublisher<>(new EmptyPublisher<>(), String::length);
        publisher.subscribe(subscriber);
        subscriber.expectNone();
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldMapSingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapPublisher<String, Integer> publisher = new MapPublisher<>(new ValuePublisher<>("test"), String::length);
        publisher.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(4);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }


    @Test
    public void shouldMapMultipleValues() throws Exception {        
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapPublisher<String, Integer> publisher =
            new MapPublisher<>(new IterablePublisher<>(List.of("aa", "bbbb", "c")), String::length);
        publisher.subscribe(subscriber);
        subscriber.request(5);
        subscriber.expectNext(2);
        subscriber.expectNext(4);
        subscriber.expectNext(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldPassError() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapPublisher<String, Integer> publisher =
            new MapPublisher<>(new ErrorPublisher<>(new RuntimeException("error!")), String::length);
        publisher.subscribe(subscriber);
        subscriber.expectError(RuntimeException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfPublisherNull() {
        assertThrows(NullPointerException.class, () -> new MapPublisher<>(null, String::length));
    }

    @Test
    public void shouldThrowNpeIfMapperNull() {
        assertThrows(NullPointerException.class, () -> new MapPublisher<>(new EmptyPublisher<>(), null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        MapPublisher<String, Integer> publisher = new MapPublisher<>(new ValuePublisher("test"), String::length);
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapPublisher<String, Integer> publisher = new MapPublisher<>(new ValuePublisher<>("test"), String::length);
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

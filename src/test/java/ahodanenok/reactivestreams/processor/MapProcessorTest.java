package ahodanenok.reactivestreams.processor;

import java.util.List;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import ahodanenok.reactivestreams.publisher.*;

public class MapProcessorTest {

    @Test
    public void shouldMapEmptyPublisher() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapProcessor<String, Integer> processor = new MapProcessor<>(String::length);
        new EmptyPublisher<String>().subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.expectNone();
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldMapSingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapProcessor<String, Integer> processor = new MapProcessor<>(String::length);
        new ValuePublisher<>("test").subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(4);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }


    @Test
    public void shouldMapMultipleValues() throws Exception {        
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapProcessor<String, Integer> processor = new MapProcessor<>(String::length);
        new IterablePublisher<>(List.of("aa", "bbbb", "c")).subscribe(processor);
        processor.subscribe(subscriber);
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
        MapProcessor<String, Integer> processor = new MapProcessor<>(String::length);
        new ErrorPublisher<String>(new RuntimeException("error!")).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.expectError(RuntimeException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfMapperNull() {
        assertThrows(NullPointerException.class, () -> new MapProcessor<String, Integer>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        MapProcessor<String, Integer> processor = new MapProcessor<>(String::length);
        assertThrows(NullPointerException.class, () -> processor.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        MapProcessor<String, Integer> processor = new MapProcessor<>(String::length);
        processor.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

package ahodanenok.reactivestreams.processor;

import java.util.List;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import ahodanenok.reactivestreams.publisher.*;

public class FilterProcessorTest {

    @Test
    public void shouldFilterEmptyPublisher() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<Integer> processor = new FilterProcessor<>(n -> true);
        new EmptyPublisher<Integer>().subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.expectNone();
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldAcceptSingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<Integer> processor = new FilterProcessor<>(n -> true);
        new ValuePublisher<>(10).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(10);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldRejectSingleValue() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<Integer> processor = new FilterProcessor<>(n -> false);
        new ValuePublisher<>(10).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }


    @Test
    public void shouldAcceptAllValues() throws Exception {        
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<Integer> processor = new FilterProcessor<>(n -> true);
        new IterablePublisher<>(List.of(1, 3, 5, 7)).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.request(5);
        subscriber.expectNext(1);
        subscriber.expectNext(3);
        subscriber.expectNext(5);
        subscriber.expectNext(7);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldRejectAllValues() throws Exception {        
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<Integer> processor = new FilterProcessor<>(n -> false);
        new IterablePublisher<>(List.of(1, 3, 5, 7)).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 3, 5, 7 })
    public void shouldAcceptOneValueFromMultiple(int allowed) throws Exception {        
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<Integer> processor = new FilterProcessor<>(n -> n == allowed);
        new IterablePublisher<>(List.of(1, 3, 5, 7)).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(allowed);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldAcceptSomeValuesFromMultiple() throws Exception {        
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<Integer> processor = new FilterProcessor<>(n -> n == 3 || n == 5);
        new IterablePublisher<>(List.of(1, 3, 5, 7)).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.request(1);
        subscriber.expectNext(3);
        subscriber.request(1);
        subscriber.expectNext(5);
        subscriber.request(1);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldPassError() throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<String> processor = new FilterProcessor<>(n -> true);
        new ErrorPublisher<String>(new RuntimeException("error!")).subscribe(processor);
        processor.subscribe(subscriber);
        subscriber.expectError(RuntimeException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowNpeIfPredicateNull() {
        assertThrows(NullPointerException.class, () -> new FilterProcessor<String>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        FilterProcessor<String> processor = new FilterProcessor<>(n -> true);
        assertThrows(NullPointerException.class, () -> processor.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<String> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        FilterProcessor<String> processor = new FilterProcessor<>(n -> true);
        processor.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

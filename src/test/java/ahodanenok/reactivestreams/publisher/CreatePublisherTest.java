package ahodanenok.reactivestreams.publisher;

import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;

import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.TestEnvironment.ManualSubscriberWithSubscriptionSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class CreatePublisherTest {

    @Test
    public void shouldCompleteWithoutAnyValues() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> f.signalComplete());
        publisher.subscribe(subscriber);
        subscriber.expectCompletion();
        subscriber.expectNone();
    }

    @Test
    public void shouldRequestPending() throws Exception {
        LinkedList<Integer> items = new LinkedList<>();
        items.add(0);
        items.add(1);
        items.add(2);
        items.add(3);

        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> {
            ForkJoinPool.commonPool().submit(() -> {
                try {
                    Thread.currentThread().sleep(20);
                    f.setOnRequest(n -> {
                        for (int i = 0; i < n && !items.isEmpty(); i++) {
                            f.signalNext(items.poll());
                        }

                        if (items.isEmpty()) {
                            f.signalComplete();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        publisher.subscribe(subscriber);
        subscriber.request(4);
        subscriber.expectNext(0);
        subscriber.expectNext(1);
        subscriber.expectNext(2);
        subscriber.expectNext(3);
        subscriber.expectCompletion();
        subscriber.expectNone();
        assertEquals(0, items.size());
    }

    @Test
    public void shouldCancelPending() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        boolean onCancelCalled[] = new boolean[] { false };
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> {
            ForkJoinPool.commonPool().submit(() -> {
                try {
                    Thread.currentThread().sleep(20);
                    f.setOnRequest(n -> {
                        f.signalNext(100);
                    });
                    f.setOnCancel(() -> onCancelCalled[0] = true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        publisher.subscribe(subscriber);
        subscriber.cancel();
        subscriber.request(1);
        subscriber.expectNone();
        assertTrue(onCancelCalled[0]);
    }

    @Test
    public void shouldNotSendValueAfterCompletion() throws Exception {
        LinkedList<Integer> items = new LinkedList<>();
        items.add(2);
        items.add(3);
        items.add(4);

        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> {
            f.setOnRequest(n -> {
                for (int i = 0; i < n && !items.isEmpty(); i++) {
                    f.signalNext(items.poll());
                }

                if (items.isEmpty()) {
                    f.signalComplete();
                }
            });
        });
        publisher.subscribe(subscriber);
        subscriber.request(3);
        subscriber.expectNext(2);
        subscriber.expectNext(3);
        subscriber.expectNext(4);
        subscriber.expectCompletion();
        subscriber.request(1);
        subscriber.expectNone();
        assertEquals(0, items.size());
    }

    @Test
    public void shouldSendLessThanRequested() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> {
            f.setOnRequest(n -> {
                for (int i = 0; i < 5; i++) {
                    f.signalNext(i);
                }

                f.signalComplete();
            });
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
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> {
            f.setOnRequest(n -> {
                for (int i = 0; i < n; i++) {
                    f.signalNext(i);
                }
            });
        });
        publisher.subscribe(subscriber);
        subscriber.request(2);
        subscriber.expectNext(0);
        subscriber.expectNext(1);
        subscriber.expectNone();
        subscriber.request(1);
        subscriber.expectNext(0);
        subscriber.expectNone();
        subscriber.request(3);
        subscriber.expectNext(0);
        subscriber.expectNext(1);
        subscriber.expectNext(2);
        subscriber.expectNone();
        subscriber.cancel();
        subscriber.expectNone();
    }

    @Test
    public void shouldEndWithErrorIfGeneratorFails() throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> {
            f.setOnRequest(n -> {
                f.signalNext(100);
                throw new ClassCastException();
            });
        });
        publisher.subscribe(subscriber);
        subscriber.request(2);
        subscriber.expectNext(100);
        subscriber.expectError(ClassCastException.class);
        subscriber.expectNone();
    }

    @Test
    public void shouldThrowIllegalArgumentIfGeneratorNull() {
        assertThrows(NullPointerException.class, () -> new CreatePublisher<>(null));
    }

    @Test
    public void shouldThrowNpeIfSubscriberNull() {
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> f.signalComplete());
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -10, Long.MIN_VALUE })
    public void shouldThrowIllegalArgumentIfRequestedAmountNotValid(long count) throws Exception {
        ManualSubscriberWithSubscriptionSupport<Integer> subscriber =
            new ManualSubscriberWithSubscriptionSupport<>(new TestEnvironment());
        CreatePublisher<Integer> publisher = new CreatePublisher<>(f -> {});
        publisher.subscribe(subscriber);
        subscriber.request(count);
        subscriber.expectError(IllegalArgumentException.class);
        subscriber.expectNone();
    }
}

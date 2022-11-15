package ahodanenok.reactivestreams.publisher.verification;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

import ahodanenok.reactivestreams.publisher.DeferPublisher;
import ahodanenok.reactivestreams.publisher.ValuePublisher;
import ahodanenok.reactivestreams.publisher.ErrorPublisher;

public class DeferPublisherVerificationTest extends PublisherVerification<Integer> {

    public DeferPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new DeferPublisher<>(() -> new ValuePublisher(5));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return new DeferPublisher<>(() -> new ErrorPublisher(new RuntimeException("test")));
    }

    @Override
    public long maxElementsFromPublisher() {
        return 1;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

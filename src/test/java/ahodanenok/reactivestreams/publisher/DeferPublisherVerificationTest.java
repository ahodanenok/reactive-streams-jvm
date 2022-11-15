package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

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

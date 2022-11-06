package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class OncePublisherCompletedVerificationTest extends PublisherVerification<Integer> {

    public OncePublisherCompletedVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new OncePublisherCompleted<Integer>(Once.value(10));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return new OncePublisherCompleted<Integer>(Once.error(new RuntimeException("error!")));
    }

    @Override
    public long maxElementsFromPublisher() {
        return 0;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

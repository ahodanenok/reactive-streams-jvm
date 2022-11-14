package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class EmptyPublisherVerificationTest extends PublisherVerification<Integer> {

    public EmptyPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new EmptyPublisher<>();
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
    }

    @Override public long maxElementsFromPublisher() {
        return 0;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

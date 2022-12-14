package ahodanenok.reactivestreams.publisher.verification;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.publisher.IntRangePublisher;

public class IntRangePublisherVerificationTest extends PublisherVerification<Integer> {

    public IntRangePublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new IntRangePublisher(0, (int) elements);
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

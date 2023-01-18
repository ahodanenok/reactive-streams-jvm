package ahodanenok.reactivestreams.verification;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.NeverPublisher;

public class NeverPublisherVerificationTest extends PublisherVerification<Integer> {

    public NeverPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new NeverPublisher<>();
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

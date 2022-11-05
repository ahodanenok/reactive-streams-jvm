package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class OnceEmptyVerificationTest extends PublisherVerification<Integer> {

    public OnceEmptyVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new OnceEmpty<Integer>();
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
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

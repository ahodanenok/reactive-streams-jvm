package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class OnceNeverVerificationTest extends PublisherVerification<Integer> {

    public OnceNeverVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new OnceNever<Integer>();
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

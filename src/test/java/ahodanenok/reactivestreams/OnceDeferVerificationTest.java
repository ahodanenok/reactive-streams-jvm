package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class OnceDeferVerificationTest extends PublisherVerification<Integer> {

    public OnceDeferVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new OnceDefer<Integer>(() -> Once.value(5));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
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

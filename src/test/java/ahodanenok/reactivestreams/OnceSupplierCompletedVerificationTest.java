package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class OnceSupplierCompletedVerificationTest extends PublisherVerification<String> {

    public OnceSupplierCompletedVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<String> createPublisher(long elements) {
        return new OnceSupplierCompleted<>(() -> "hello!");
    }

    @Override
    public Publisher<String> createFailedPublisher() {
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

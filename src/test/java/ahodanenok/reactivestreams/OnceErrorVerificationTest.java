package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class OnceErrorVerificationTest extends PublisherVerification<Integer> {

    public OnceErrorVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new OnceError<Integer>(new java.io.FileNotFoundException("no file"));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return new OnceError<Integer>(new IllegalStateException("error!"));
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

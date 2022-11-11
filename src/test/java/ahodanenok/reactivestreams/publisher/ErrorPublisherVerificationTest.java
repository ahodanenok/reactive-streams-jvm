package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class ErrorPublisherVerificationTest extends PublisherVerification<Integer> {

    public ErrorPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new ErrorPublisher<>(new RuntimeException("error 1"));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return new ErrorPublisher<>(new RuntimeException("error 2"));
    }

    @Override public long maxElementsFromPublisher() {
        return 0;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class MapPublisherVerificationTest extends PublisherVerification<String> {

    public MapPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<String> createPublisher(long elements) {
        return new MapPublisher<>(new ValuePublisher<>(0), v -> "num=" + v);
    }

    @Override
    public Publisher<String> createFailedPublisher() {
        return null;
    }

    @Override public long maxElementsFromPublisher() {
        return 1;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

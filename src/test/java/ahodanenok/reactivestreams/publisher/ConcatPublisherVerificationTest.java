package ahodanenok.reactivestreams.publisher;

import java.util.List;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class ConcatPublisherVerificationTest extends PublisherVerification<Integer> {

    public ConcatPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        long mid = elements / 2;
        return new ConcatPublisher<>(List.of(
            new IntRangePublisher(0, (int) mid),
            new IntRangePublisher(0, (int) (elements - mid))));
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

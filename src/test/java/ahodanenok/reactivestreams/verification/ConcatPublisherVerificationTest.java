package ahodanenok.reactivestreams.verification;

import java.util.List;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.ErrorPublisher;
import ahodanenok.reactivestreams.LongRangePublisher;
import ahodanenok.reactivestreams.ConcatPublisher;

public class ConcatPublisherVerificationTest extends PublisherVerification<Long> {

    public ConcatPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        long mid = elements / 2;
        return new ConcatPublisher<>(List.of(
            new LongRangePublisher(0, mid),
            new LongRangePublisher(0, elements - mid)));
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return new ConcatPublisher<>(List.of(
            new ErrorPublisher(new RuntimeException("error!")),
            new LongRangePublisher(0, 10)));
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

package ahodanenok.reactivestreams.verification;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.ErrorPublisher;
import ahodanenok.reactivestreams.FilterPublisher;
import ahodanenok.reactivestreams.LongRangePublisher;

public class FilterPublisherVerificationTest extends PublisherVerification<Long> {

    public FilterPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return new FilterPublisher<>(new LongRangePublisher(0, elements), n -> true);
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return new FilterPublisher<>(new ErrorPublisher<>(new RuntimeException("error!")), n -> true);
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

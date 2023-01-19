package ahodanenok.reactivestreams.verification;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.MapPublisher;
import ahodanenok.reactivestreams.LongRangePublisher;
import ahodanenok.reactivestreams.publisher.ErrorPublisher;

public class MapPublisherVerificationTest extends PublisherVerification<Long> {

    public MapPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return new MapPublisher<>(new LongRangePublisher(0, elements), n -> n + 1);
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return new MapPublisher<>(new ErrorPublisher<Long>(new RuntimeException("error!")), n -> n + 1);
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

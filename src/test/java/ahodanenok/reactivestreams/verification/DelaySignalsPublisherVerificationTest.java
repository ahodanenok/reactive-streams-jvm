package ahodanenok.reactivestreams.verification;

import java.util.concurrent.TimeUnit;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.DelaySignalsPublisher;
import ahodanenok.reactivestreams.LongRangePublisher;
import ahodanenok.reactivestreams.publisher.ErrorPublisher;

public class DelaySignalsPublisherVerificationTest extends PublisherVerification<Long> {

    public DelaySignalsPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return new DelaySignalsPublisher<>(new LongRangePublisher(0, elements), 5, TimeUnit.MILLISECONDS);
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return new DelaySignalsPublisher<>(new ErrorPublisher<>(new RuntimeException("error!")), 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

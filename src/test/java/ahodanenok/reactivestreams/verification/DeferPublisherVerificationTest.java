package ahodanenok.reactivestreams.verification;

import java.util.stream.LongStream;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

import ahodanenok.reactivestreams.DeferPublisher;
import ahodanenok.reactivestreams.ErrorPublisher;
import ahodanenok.reactivestreams.LongRangePublisher;

public class DeferPublisherVerificationTest extends PublisherVerification<Long> {

    public DeferPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return new DeferPublisher<>(() -> new LongRangePublisher(0, elements));
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return new DeferPublisher<>(() -> new ErrorPublisher(new RuntimeException("test")));
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

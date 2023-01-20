package ahodanenok.reactivestreams.publisher.verification;

import java.util.stream.LongStream;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

import ahodanenok.reactivestreams.publisher.DeferPublisher;
import ahodanenok.reactivestreams.publisher.IterablePublisher;
import ahodanenok.reactivestreams.ErrorPublisher;

public class DeferPublisherVerificationTest extends PublisherVerification<Long> {

    public DeferPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return new DeferPublisher<>(() -> new IterablePublisher(() -> LongStream.range(0, elements).iterator()));
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

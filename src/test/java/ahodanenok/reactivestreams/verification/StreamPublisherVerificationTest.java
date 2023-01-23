package ahodanenok.reactivestreams.verification;

import java.util.stream.LongStream;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.StreamPublisher;

public class StreamPublisherVerificationTest extends PublisherVerification<Long> {

    public StreamPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return new StreamPublisher<>(LongStream.range(0, elements).boxed());
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return null;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

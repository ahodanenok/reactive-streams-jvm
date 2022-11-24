package ahodanenok.reactivestreams.publisher.verification;

import java.util.stream.IntStream;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.publisher.StreamPublisher;

public class StreamPublisherVerificationTest extends PublisherVerification<Integer> {

    public StreamPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new StreamPublisher<>(IntStream.range(0, (int) elements).boxed());
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

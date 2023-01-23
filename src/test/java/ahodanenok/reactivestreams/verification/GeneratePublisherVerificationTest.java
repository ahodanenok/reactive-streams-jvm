package ahodanenok.reactivestreams.verification;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.GeneratePublisher;

public class GeneratePublisherVerificationTest extends PublisherVerification<Integer> {

    public GeneratePublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new GeneratePublisher<>(0, (prev, callback) -> {
            if (prev < elements) {
                callback.signalValue(prev + 1);
            } else {
                callback.signalComplete();
            }
        });
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

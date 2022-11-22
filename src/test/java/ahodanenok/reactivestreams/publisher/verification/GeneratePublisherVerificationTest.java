package ahodanenok.reactivestreams.publisher.verification;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.publisher.GeneratePublisher;

public class GeneratePublisherVerificationTest extends PublisherVerification<Integer> {

    public GeneratePublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new GeneratePublisher<>(0, (prev, callback) -> {
            if (prev < elements) {
                callback.resolve(prev + 1);
            } else {
                callback.complete();
            }
        });
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
    }

    @Override public long maxElementsFromPublisher() {
        return 1;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

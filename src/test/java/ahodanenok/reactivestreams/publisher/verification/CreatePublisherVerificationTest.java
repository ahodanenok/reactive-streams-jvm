package ahodanenok.reactivestreams.publisher.verification;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

import ahodanenok.reactivestreams.publisher.CreatePublisher;

public class CreatePublisherVerificationTest extends PublisherVerification<String> {

    public CreatePublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<String> createPublisher(long elements) {
        return new CreatePublisher<String>(callback -> callback.setOnRequest(n -> callback.complete("hello!")));
    }

    @Override
    public Publisher<String> createFailedPublisher() {
        return new CreatePublisher<String>(callback -> callback.error(new RuntimeException()));
    }

    @Override
    public long maxElementsFromPublisher() {
        return 1;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

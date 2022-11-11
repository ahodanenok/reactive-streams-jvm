package ahodanenok.reactivestreams.publisher;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class FromCallbackPublisherOneVerificationTest extends PublisherVerification<String> {

    public FromCallbackPublisherOneVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<String> createPublisher(long elements) {
        return new FromCallbackPublisher<String>(callback -> callback.setOnRequest(n -> callback.complete("hello!")));
    }

    @Override
    public Publisher<String> createFailedPublisher() {
        return new FromCallbackPublisher<String>(callback -> callback.error(new RuntimeException()));
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

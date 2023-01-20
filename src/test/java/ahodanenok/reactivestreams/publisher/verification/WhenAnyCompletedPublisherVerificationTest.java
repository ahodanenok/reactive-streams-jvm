package ahodanenok.reactivestreams.publisher.verification;

import java.util.List;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.publisher.WhenAnyCompletedPublisher;
import ahodanenok.reactivestreams.ValuePublisher;
import ahodanenok.reactivestreams.ErrorPublisher;

public class WhenAnyCompletedPublisherVerificationTest extends PublisherVerification<String> {

    public WhenAnyCompletedPublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<String> createPublisher(long elements) {
        return new WhenAnyCompletedPublisher<>(List.of(
            new ValuePublisher<>(0), 
            new ValuePublisher<>(5),
            new ValuePublisher<>(10)));
    }

    @Override
    public Publisher<String> createFailedPublisher() {
        return new WhenAnyCompletedPublisher<>(List.of(
            new ValuePublisher<>(0),
            new ErrorPublisher<>(new RuntimeException("error")),
            new ValuePublisher<>(10)));
    }

    @Override public long maxElementsFromPublisher() {
        return 0;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

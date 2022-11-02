package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

public class OnceValueVerificationTest extends PublisherVerification<Integer> {

    public OnceValueVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new OnceValue(0);
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
    }

    @Override public long maxElementsFromPublisher() {
        return 1;
    }
}
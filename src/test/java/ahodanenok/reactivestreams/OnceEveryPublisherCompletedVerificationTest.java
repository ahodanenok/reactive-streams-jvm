package ahodanenok.reactivestreams;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;
import java.util.Arrays;

public class OnceEveryPublisherCompletedVerificationTest extends PublisherVerification<Integer> {

    public OnceEveryPublisherCompletedVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new OnceEveryPublisherCompleted<Integer>(Arrays.asList(Once.value(10), Once.empty(), Once.value(1)));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return new OnceEveryPublisherCompleted<Integer>(Arrays.asList(Once.value(10), Once.error(new RuntimeException("error!"))));
    }

    @Override
    public long maxElementsFromPublisher() {
        return 0;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

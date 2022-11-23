package ahodanenok.reactivestreams.publisher.verification;

import java.util.List;
import java.util.ArrayList;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.publisher.IterablePublisher;

public class IterablePublisherVerificationTest extends PublisherVerification<Integer> {

    public IterablePublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < Math.min(elements, 10); i++) {
            list.add(i);
        }

        return new IterablePublisher<>(list);
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

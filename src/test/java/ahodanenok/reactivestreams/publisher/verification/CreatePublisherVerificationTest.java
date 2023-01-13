package ahodanenok.reactivestreams.publisher.verification;

import java.util.function.LongConsumer;

import org.reactivestreams.*;
import org.reactivestreams.tck.*;

import ahodanenok.reactivestreams.publisher.CreatePublisher;

public class CreatePublisherVerificationTest extends PublisherVerification<Long> {

    public CreatePublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long elements) {
        return new CreatePublisher<>(callback -> callback.setOnRequest(new LongConsumer() {

            private long totalCount = elements;
            private long signalledCount = 0;

            @Override
            public void accept(long n) {
                for (long i = 0; i < n && signalledCount < totalCount; i++) {
                    signalledCount++;
                    callback.signalNext(i);
                }

                if (signalledCount == totalCount) {
                    callback.signalComplete();
                }
            }
        }));
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return new CreatePublisher<>(callback -> callback.signalError(new RuntimeException()));
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

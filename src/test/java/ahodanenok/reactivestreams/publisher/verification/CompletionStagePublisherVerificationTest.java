package ahodanenok.reactivestreams.publisher.verification;

import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import ahodanenok.reactivestreams.publisher.CompletionStagePublisher;

public class CompletionStagePublisherVerificationTest extends PublisherVerification<Integer> {

    public CompletionStagePublisherVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new CompletionStagePublisher<>(CompletableFuture.completedFuture(1));
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        return new CompletionStagePublisher<>(future);
    }

    @Override public long maxElementsFromPublisher() {
        return 1;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}

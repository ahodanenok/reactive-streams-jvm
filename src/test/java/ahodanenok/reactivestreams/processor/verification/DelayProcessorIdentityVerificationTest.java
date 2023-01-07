package ahodanenok.reactivestreams.processor.verification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.tck.IdentityProcessorVerification;
import org.reactivestreams.tck.SubscriberWhiteboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import ahodanenok.reactivestreams.processor.DelayProcessor;
import ahodanenok.reactivestreams.publisher.IntRangePublisher;
import ahodanenok.reactivestreams.publisher.ErrorPublisher;

public class DelayProcessorIdentityVerificationTest extends IdentityProcessorVerification<Integer> {

    private ExecutorService executor;

    @BeforeClass
    public void before() { executor = Executors.newFixedThreadPool(4); }

    @AfterClass
    public void after() { if (executor != null) executor.shutdown(); }

    public DelayProcessorIdentityVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Processor<Integer, Integer> createIdentityProcessor(int bufferSize) {
        DelayProcessor<Integer> processor = new DelayProcessor<Integer>(20, TimeUnit.MILLISECONDS);
        return processor;
    }

    /*@Override
    public Publisher<Integer> createHelperPublisher(long elements) {
        return new IntRangePublisher(0, (int) elements);
    }*/

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return new ErrorPublisher<>(new RuntimeException("error!"));
    }

    @Override
    public ExecutorService publisherExecutorService() {
        return executor;
    }

    @Override
    public long maxElementsFromPublisher() {
        return super.maxElementsFromPublisher();
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }

    public long maxSupportedSubscribers() {
        return 1;
    }

    @Override
    public Integer createElement(int element) {
        return element;
    }
}

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

import ahodanenok.reactivestreams.processor.MapProcessor;
import ahodanenok.reactivestreams.publisher.ErrorPublisher;

public class MapProcessorIdentityVerificationTest extends IdentityProcessorVerification<Integer> {

    private ExecutorService executor;

    @BeforeClass
    public void before() { executor = Executors.newFixedThreadPool(4); }

    @AfterClass
    public void after() { if (executor != null) executor.shutdown(); }

    public MapProcessorIdentityVerificationTest() {
        super(new TestEnvironment());
    }

    @Override
    public Processor<Integer, Integer> createIdentityProcessor(int bufferSize) {
        return new MapProcessor<>(n -> n);       
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return new ErrorPublisher<>(new RuntimeException("error!"));
    }

    @Override
    public ExecutorService publisherExecutorService() {
        return executor;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }

    @Override
    public long maxSupportedSubscribers() {
        return 1;
    }

    @Override
    public Integer createElement(int element) {
        return element;
    }
}

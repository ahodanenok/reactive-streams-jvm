package ahodanenok.reactivestreams.publisher;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import org.reactivestreams.Subscriber;

public class CompletionStagePublisher<T> extends AbstractPublisher<T> {

    private final CompletionStage<T> stage;

    public CompletionStagePublisher(CompletionStage<T> stage) {
        Objects.requireNonNull(stage, "stage");
        this.stage = stage;
    }

    @Override
    protected void doSubscribe(Subscriber<? super T> subscriber) {
        new CompletionStagePublisherSubscription<>(subscriber, stage).init();
    }

    static class CompletionStagePublisherSubscription<T> extends AbstractSubscription<T> {

        private final CompletionStage<T> stage;
        private volatile boolean waitingForResult;

        CompletionStagePublisherSubscription(Subscriber<? super T> subscriber, CompletionStage<T> stage) {
            super(subscriber);
            this.stage = stage;
        }

        @Override
        protected void onInit() {
            stage.whenComplete((value, exception) -> {
                if (exception != null) {
                    error(exception);
                }
            });
        }

        @Override
        protected void onRequest(long n) {
            if (waitingForResult) {
                return;
            }

            waitingForResult = true;
            stage.whenComplete((result, exception) -> {
                if (exception == null) {
                    complete(result);
                }
            });
        }
    }
}
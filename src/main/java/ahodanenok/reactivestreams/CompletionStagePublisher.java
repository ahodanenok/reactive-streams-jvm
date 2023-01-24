package ahodanenok.reactivestreams;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class CompletionStagePublisher<T> implements Publisher<T> {

    private final CompletionStage<T> stage;

    private volatile T result;
    private volatile boolean resultReady;
    private volatile boolean requested;

    public CompletionStagePublisher(CompletionStage<T> stage) {
        Objects.requireNonNull(stage, "stage");
        this.stage = stage;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        new CompletionStagePublisherSubscription<>(subscriber, stage).init();
    }

    static class CompletionStagePublisherSubscription<T> implements Subscription {

        private final Subscriber<? super T> downstream;
        private final CompletionStage<T> stage;

        private volatile T result;
        private volatile boolean resultReady;
        private volatile boolean requested;
        private volatile boolean cancelled;

        CompletionStagePublisherSubscription(Subscriber<? super T> subscriber, CompletionStage<T> stage) {
            Objects.requireNonNull(subscriber, "subscriber");
            this.downstream = subscriber;
            this.stage = stage;
        }

        void init() {
            downstream.onSubscribe(this);
            stage.whenComplete((value, error) -> {
                if (cancelled) {
                    return;
                }

                if (error != null) {
                    downstream.onError(error);
                    return;
                }

                result = value;
                resultReady = true;
                if (requested) {
                    downstream.onNext(result);
                    downstream.onComplete();
                }
            });
        }

        @Override
        public void request(long n) {
            if (requested || cancelled) {
                return;
            }

            if (n <= 0) {
                cancelled = true;
                downstream.onError(new IllegalArgumentException("requested amount must be > 0: " + n));
                return;
            }

            requested = true;
            if (resultReady) {
                downstream.onNext(result);
                downstream.onComplete();
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
        }
    }
}

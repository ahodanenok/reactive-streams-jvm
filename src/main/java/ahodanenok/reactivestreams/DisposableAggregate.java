package ahodanenok.reactivestreams;

import java.util.List;
import java.util.ArrayList;

public class DisposableAggregate implements Disposable {

    // todo: thread safety

    private final List<Disposable> disposables = new ArrayList<>();

    public void add(Disposable disposable) {
        this.disposables.add(disposable);
    }

    @Override
    public void dispose() {
        for (Disposable d : disposables) {
            d.dispose();
        }
    }
}

package ahodanenok.reactivestreams;

import java.util.concurrent.atomic.AtomicLong;

public final class Fn {

    private Fn() { }

    public static long getAndAddRequested(AtomicLong requested, long n) {
        return requested.getAndAccumulate(n, (v, inc) -> {
             // nothing to add
            if (v == Long.MAX_VALUE) {
                return v;
            }

            long result = v + inc;
            if (result < 0) {
                return Long.MAX_VALUE;
            } else {
                return result;
            }
        });
    }
}

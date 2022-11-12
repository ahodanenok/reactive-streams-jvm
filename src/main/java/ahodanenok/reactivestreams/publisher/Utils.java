package ahodanenok.reactivestreams.publisher;

public final class Utils {

    private Utils() { }

    public static long addRequested(long requested, long n) {
        if (n == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }

        requested += n;
        if (requested < 0) {
            requested = Long.MAX_VALUE;
        }

        return requested;
    }
}

package team.tnt.collectorsalbum.util;

public final class MathUtil {

    public static int seqSum(int from, int to) {
        int n = to - from + 1;
        return n * (from + to) / 2;
    }
}

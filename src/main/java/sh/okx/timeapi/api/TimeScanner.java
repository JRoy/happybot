package sh.okx.timeapi.api;

import java.util.Arrays;
import java.util.function.Predicate;

class TimeScanner {
    private final char[] time;
    private int index = 0;

    public TimeScanner(String time) {
        this.time = time.toCharArray();
    }

    public boolean hasNext() {
        return index < time.length-1;
    }

    public long nextLong() {
        return Long.parseLong(String.valueOf(next(Character::isDigit)));
    }

    public String nextString() {
        return String.valueOf(next(Character::isAlphabetic));
    }

    private char[] next(Predicate<Character> whichSatisfies) {
        int startIndex = index;
        while(++index < time.length && whichSatisfies.test(time[index]));
        return Arrays.copyOfRange(time, startIndex, index);
    }
}
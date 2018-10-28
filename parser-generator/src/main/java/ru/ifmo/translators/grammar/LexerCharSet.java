package ru.ifmo.translators.grammar;

import java.util.HashSet;
import java.util.Set;

public class LexerCharSet extends Token {

    private final String charset;
    private final boolean inverse;

    private final Set<Character> set;

    public LexerCharSet(String charset, boolean inverse) {
        this.charset = charset;
        this.inverse = inverse;

        this.set = new HashSet<>(inverse ? 256 : 4);

        for (int c = 0; inverse && c < 256; c++) {
            set.add((char) c);
        }

        charset.chars().mapToObj(i -> (char) i).forEach(inverse ? set::remove : set::add);
    }

    public Set<Character> getSet() {
        return set;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isInverse() {
        return inverse;
    }

    @Override
    public String toString() {
        return (inverse ? "~" : "") + "[" + charset + "]";
    }
}

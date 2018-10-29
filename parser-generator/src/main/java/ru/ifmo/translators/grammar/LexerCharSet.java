package ru.ifmo.translators.grammar;

import java.util.regex.Pattern;

public class LexerCharSet implements LexerToken {

    private final String charset;
    private final boolean inverse;

    private final Pattern pattern;

    public LexerCharSet(String charset, boolean inverse) {
        this.charset = charset;
        this.inverse = inverse;
        this.pattern = Pattern.compile("[" + (inverse ? "^" : "") + charset + "]");
    }

    public String getCharset() {
        return "[" + (inverse ? "^" : "") + charset + "]";
    }

    public boolean isInverse() {
        return inverse;
    }

    public boolean test(char c) {
        return pattern.matcher(Character.toString(c)).find();
    }

    @Override
    public String toString() {
        return (inverse ? "~" : "") + "[" + charset + "]";
    }
}

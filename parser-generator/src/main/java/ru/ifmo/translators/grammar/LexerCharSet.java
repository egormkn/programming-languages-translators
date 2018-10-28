package ru.ifmo.translators.grammar;

import java.util.regex.Pattern;

public class LexerCharSet extends Token {

    private final String charset;
    private final boolean inverse;

    private final Pattern pattern;

    public LexerCharSet(String charset, boolean inverse) {
        this.charset = charset;
        this.inverse = inverse;

        this.pattern = Pattern.compile(charset);
    }

    public String getCharset() {
        return charset;
    }

    public boolean isInverse() {
        return inverse;
    }

    public boolean test(char c) {
        return inverse != pattern.matcher(Character.toString(c)).find();
    }

    @Override
    public String toString() {
        return (inverse ? "~" : "") + "[" + charset + "]";
    }
}

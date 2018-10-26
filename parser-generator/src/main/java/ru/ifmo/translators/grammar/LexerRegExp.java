package ru.ifmo.translators.grammar;

import java.util.regex.Pattern;

public class LexerRegExp extends Token implements LexerToken {

    private final Pattern pattern;

    public LexerRegExp(String regexp) {
        pattern = Pattern.compile(regexp);
    }

    public Pattern getPattern() {
        return pattern;
    }
}

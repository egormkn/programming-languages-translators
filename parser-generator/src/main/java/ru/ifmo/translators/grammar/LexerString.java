package ru.ifmo.translators.grammar;

public class LexerString extends Token {

    private final String string;

    public LexerString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return "'" + string + "'";
    }
}
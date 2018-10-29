package ru.ifmo.translators.grammar;

public class LexerCode implements LexerToken {

    private final String code;

    public LexerCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "{" + code + "}";
    }
}
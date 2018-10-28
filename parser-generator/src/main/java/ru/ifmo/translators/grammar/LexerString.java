package ru.ifmo.translators.grammar;

import java.util.HashMap;
import java.util.Map;

public class LexerString implements LexerToken, ParserToken {

    static final LexerString EMPTY = new LexerString("") {
        @Override
        public String toString() {
            return "";
        }
    };

    private static final Map<String, LexerString> cache = new HashMap<>();

    private final String string;

    private LexerString(String string) {
        this.string = string;
    }

    public static LexerString get(String string) {
        LexerString lexerString = cache.get(string);
        if (lexerString == null) {
            lexerString = new LexerString(string);
            cache.put(string, lexerString);
        }
        return lexerString;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return "'" + string + "'";
    }
}
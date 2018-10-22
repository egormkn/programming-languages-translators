package ru.ifmo.translators;

import java.util.HashMap;
import java.util.Map;

public class Token {

    private static Map<Integer, Token> pool = new HashMap<>();

    static {
        for (Type t : Type.values()) {
            if (t.isKeywordType()) pool.put(t.value, new Token(t, t.value));
        }

        for (int letter = 'a'; letter <= 'z'; letter++) {
            pool.put(letter, new Token(Type.LETTER, letter));
        }
    }

    private final Type type;
    private final int value;

    private Token(Type type, int value) {
        this.type = type;
        this.value = value;
    }

    public static Token get(int c) {
        return pool.get(c);
    }

    public Type getType() {
        return type;
    }

    public char getChar() {
        return (char) value;
    }

    public enum Type {
        LETTER,
        LPAREN('('),
        RPAREN(')'),
        OR('|'),
        STAR('*'),
        PLUS('+'),
        END(-1);

        private final int value;

        Type() {
            value = Integer.MIN_VALUE;
        }

        Type(int c) {
            value = c;
        }

        public boolean isKeywordType() {
            return value != Integer.MIN_VALUE;
        }
    }

}

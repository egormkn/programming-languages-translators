package ru.ifmo.translators;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class LexicalAnalyzer {

    private final InputStream stream;
    private int currentChar, currentPos = 0;
    private Token currentToken;

    public LexicalAnalyzer(InputStream is) {
        stream = is;
    }

    private void nextChar() throws ParseException {
        currentPos++;
        try {
            currentChar = stream.read();
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), currentPos);
        }
    }

    public void nextToken() throws ParseException {
        do {
            nextChar();
        } while (Character.isWhitespace(currentChar));
        currentToken = Token.get(currentChar);
        if (currentToken == null) {
            throw new ParseException("Illegal character: " + (char) currentChar, currentPos);
        }
    }

    public Token currentToken() {
        return currentToken;
    }

    public Token.Type currentType() {
        return currentToken.getType();
    }

    public int currentPos() {
        return currentPos;
    }
}

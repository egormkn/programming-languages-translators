package ru.ifmo.translators.lexer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class TemplateLexer {

    private static List<Token> pool = new ArrayList<>();

    static {
        pool.add(new TestToken("Test", "test"));
    }

    final Node START = new Node();
    private final InputStream is;

    public TemplateLexer(InputStream is) {
        this.is = is;
    }

    public static void main(String[] args) {
        TemplateLexer t = new TemplateLexer(new ByteArrayInputStream("testtesttest".getBytes(StandardCharsets.UTF_8)));
        try {
            t.getTokens().stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private List<Token> getTokens() throws IOException, ParseException {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = is.read()) != -1) builder.append((char) c);
        CharBuffer input = CharBuffer.wrap(builder.toString());
        int position = 0, length = input.length();

        List<Token> tokens = new ArrayList<>();
        while (position < length) {
            Token token = getToken(input.subSequence(position, length));
            if (token == null) throw new ParseException("Unknown token", position);
            position += token.name.length(); // FIXME o_O
            tokens.add(token);
        }
        if (position != length) {
            throw new AssertionError("Position > length");
        }
        return tokens;
    }

    private Token getToken(CharSequence input) {
        for (Token token : pool) {
            Token parsed = token.consume(input);
            if (parsed != null) return parsed;
        }
        return null;
    }

    public static abstract class Token {

        private final String name;

        public Token(String name) {
            this.name = name;
        }

        public abstract Token consume(CharSequence input);
    }

    public static class TestToken extends Token {

        public TestToken(String name, String text) {
            super(name);
        }

        public Token consume(CharSequence input) {
            if (input.subSequence(0, 4).toString().equals("test")) {
                return this;
            }
            return null;
        }
    }

    private class Node {

        Node[] transitions = new Node[256];

    }
}

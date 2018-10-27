package ru.ifmo.translators.lexer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class TemplateLexer {

    private static List<Class<? extends Token>> tokenClasses
            = Arrays.asList(TestWithSpaceToken.class, TestToken.class, SpaceToken.class);

    public static void main(String[] args) {
        String input = "test testtest  test test test  ";
        System.out.println("Input: \"" + input + "\"");
        TemplateLexer t = new TemplateLexer();
        try {
            InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            t.getTokens(is).forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Token> getTokens(InputStream is) throws IOException, ParseException {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = is.read()) != -1) builder.append((char) c);
        CharBuffer input = CharBuffer.wrap(builder.toString());

        int position = 0, length = input.length();

        List<Token> tokens = new ArrayList<>();
        while (position < length) {
            Token token = getToken(input.subSequence(position, length));
            if (token == null) throw new ParseException("Can't recognize token: " + input, position);
            position += token.length();
            tokens.add(token);
        }
        if (position != length) {
            throw new AssertionError("Position > length");
        }
        return tokens;
    }

    /* GENERATED */

    private Token getToken(CharSequence input) {
        for (Class<? extends Token> tokenClass : tokenClasses) {
            try {
                Token t = tokenClass.getConstructor().newInstance();
                if (t.consume(input) != null) return t;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public enum TokenType {
        TESTWITHSPACE,
        TEST,
        SPACE
    }

    public abstract static class Token {

        private final TokenType type;
        private final boolean skip;
        protected String text = "", data = "";

        protected Token(TokenType type) {
            this(type, false);
        }

        protected Token(TokenType type, boolean skip) {
            this.type = type;
            this.skip = skip;
        }

        public boolean isSkipped() {
            return skip;
        }

        public TokenType getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getData() {
            return data;
        }

        public int length() {
            return data.length();
        }

        public abstract Token consume(CharSequence input);

        @Override
        public String toString() {
            return new StringJoiner(", ", Token.class.getSimpleName() + "[", "]")
                    .add("type=" + type)
                    .add("skip=" + skip)
                    .add("text='" + text + "'")
                    .add("data='" + data + "'")
                    .toString();
        }
    }

    public static class TestWithSpaceToken extends Token {

        public TestWithSpaceToken() {
            super(TokenType.TESTWITHSPACE);
        }

        public Token consume(CharSequence input) {

            Token token1 = new TestToken();
            if (token1.consume(input) == null) {
                return null;
            } else {
                text += token1.text;
                data += token1.data;
                input = input.subSequence(token1.length(), input.length());
            }


            Token token = new SpaceToken();
            if (token.consume(input) == null) {
                return null;
            } else {
                text += token.text;
                data += token.data;
                input = input.subSequence(token.length(), input.length());
            }

            {
                setText("text was changed by testwithspace rule");
            }

            return this;
        }
    }

    public static class TestToken extends Token {

        public TestToken() {
            super(TokenType.TEST);
        }

        public Token consume(CharSequence input) {
            String str = "test";
            if (input.length() < str.length()) {
                return null;
            } else if (!input.subSequence(0, str.length()).toString().equals(str)) {
                return null;
            } else {
                text += str;
                data += str;
                input = input.subSequence(str.length(), input.length());
            }

            {
                setText("text was changed by test lexer rule");
            }

            return this;
        }
    }

    public static class SpaceToken extends Token {

        public SpaceToken() {
            super(TokenType.SPACE, true);
        }

        public Token consume(CharSequence input) {
            Matcher matcher = Pattern.compile("^(" + "[ ]" + ")").matcher(input);
            if (!matcher.find()) {
                return null;
            } else {
                text += matcher.group(0);
                data += matcher.group(0);
                input = input.subSequence(matcher.group(0).length(), input.length());
            }

            {
                setText("text was changed by space lexer rule");
            }

            return this;
        }
    }
}

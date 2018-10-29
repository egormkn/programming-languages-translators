
package ru.ifmo.translators.generated;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class RegexpLexer {


    // Non-fragment lexer rules
    private static List<Class<? extends Token>> nonFragmentClasses = Arrays.asList(
            KPrimeToken.class,
            BarToken.class,
            LparenToken.class,
            RparenToken.class,
            LetterToken.class
    );

    public static void main(String[] args) {
        Path inputFile = Paths.get(args[0]);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(inputFile.toFile()));
            new RegexpLexer().getTokens(is).forEach(System.out::println);
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
            if (token == null)
                throw new ParseException("Can't recognize token: " + input.subSequence(position, length), position);
            position += token.length();
            if (!token.isSkip()) tokens.add(token);
        }
        if (position != length) {
            throw new AssertionError("Position > length");
        }
        return tokens;
    }

    private Token getToken(CharSequence input) {
        for (Class<? extends Token> tokenClass : nonFragmentClasses) {
            try {
                Token t = tokenClass.getConstructor().newInstance();
                if (t.consume(input) != null) return t;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // All lexer token types
    public enum TokenType {
        KPRIME,
        BAR,
        LPAREN,
        RPAREN,
        LETTER,
        _NONE
    }

    public static abstract class Token {
        private final TokenType type;
        protected String text = "", data = "";
        private boolean skip;

        protected Token() {
            this.type = TokenType._NONE;
            this.skip = false;
        }

        protected Token(TokenType type, boolean skip) {
            this.type = type;
            this.skip = skip;
        }

        public TokenType getType() {
            return type;
        }

        public int getTypeId() {
            return type.ordinal();
        }

        public String getText() {
            return text;
        }

        void setText(String text) {
            this.text = text;
        }

        public boolean isSkip() {
            return skip;
        }

        void setSkip(boolean skip) {
            this.skip = skip;
        }

        public String getData() {
            return data;
        }

        public int length() {
            return data.length();
        }

        Token consume(CharSequence input) {
            for (Function<CharSequence, List<Token>> alternative : getAlternatives()) {
                List<Token> result = alternative.apply(input);
                if (result != null) {
                    result.forEach(t -> {
                        text += t.text;
                        data += t.data;
                    });
                    return this;
                }
            }
            return null;
        }

        abstract List<Function<CharSequence, List<Token>>> getAlternatives();

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

    private static class StringTokenHelper extends Token {

        private final String str;

        StringTokenHelper(String str) {
            this.str = str;
        }

        @Override
        public Token consume(CharSequence input) {
            if (input.length() < str.length()) {
                return null;
            } else if (!input.subSequence(0, str.length()).toString().equals(str)) {
                return null;
            }

            text += str;
            data += str;
            return this;
        }

        @Override
        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return null;
        }
    }

    private static class CharSetTokenHelper extends Token {

        private final String charset;
        private final Pattern pattern;

        CharSetTokenHelper(String charset) {
            this.charset = charset;
            this.pattern = Pattern.compile(charset);
        }

        @Override
        public Token consume(CharSequence input) {
            if (input.length() < 1) {
                return null;
            } else if (!pattern.matcher(input.subSequence(0, 1)).find()) {
                return null;
            }

            text += input.charAt(0);
            data += input.charAt(0);
            return this;
        }

        @Override
        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return null;
        }
    }

    public static class KPrimeToken extends Token {
        public KPrimeToken() {
            super(TokenType.KPRIME, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[+*]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    }
            );
        }
    }

    public static class BarToken extends Token {
        public BarToken() {
            super(TokenType.BAR, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("|");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    }
            );
        }
    }

    public static class LparenToken extends Token {
        public LparenToken() {
            super(TokenType.LPAREN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("(");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    }
            );
        }
    }

    public static class RparenToken extends Token {
        public RparenToken() {
            super(TokenType.RPAREN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(")");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    }
            );
        }
    }

    public static class LetterToken extends Token {
        public LetterToken() {
            super(TokenType.LETTER, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[a-z]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    }
            );
        }
    }
}
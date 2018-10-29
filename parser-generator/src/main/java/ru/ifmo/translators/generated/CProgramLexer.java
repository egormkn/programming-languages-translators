
package ru.ifmo.translators.generated;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class CProgramLexer {


    // Non-fragment lexer rules
    private static List<Class<? extends Token>> nonFragmentClasses = Arrays.asList(
            StringLiteralToken.class,
            ConstantToken.class,
            DirectiveTokenToken.class,
            AutoToken.class,
            BreakToken.class,
            CaseToken.class,
            CharToken.class,
            ConstToken.class,
            ContinueToken.class,
            DefaultToken.class,
            DoToken.class,
            DoubleToken.class,
            ElseToken.class,
            EnumToken.class,
            ExternToken.class,
            FloatToken.class,
            ForToken.class,
            GotoToken.class,
            IfToken.class,
            InlineToken.class,
            IntToken.class,
            LongToken.class,
            RegisterToken.class,
            RestrictToken.class,
            ReturnToken.class,
            ShortToken.class,
            SignedToken.class,
            SizeofToken.class,
            StaticToken.class,
            StructToken.class,
            SwitchToken.class,
            TypedefToken.class,
            UnionToken.class,
            UnsignedToken.class,
            VoidToken.class,
            VolatileToken.class,
            WhileToken.class,
            LeftParenToken.class,
            RightParenToken.class,
            LeftBracketToken.class,
            RightBracketToken.class,
            LeftBraceToken.class,
            RightBraceToken.class,
            LessToken.class,
            LessEqualToken.class,
            GreaterToken.class,
            GreaterEqualToken.class,
            LeftShiftToken.class,
            RightShiftToken.class,
            PlusToken.class,
            PlusPlusToken.class,
            MinusToken.class,
            MinusMinusToken.class,
            StarToken.class,
            DivToken.class,
            ModToken.class,
            AndToken.class,
            OrToken.class,
            AndAndToken.class,
            OrOrToken.class,
            CaretToken.class,
            NotToken.class,
            TildeToken.class,
            QuestionToken.class,
            ColonToken.class,
            SemicolonToken.class,
            CommaToken.class,
            SingleQuoteToken.class,
            DoubleQuoteToken.class,
            AssignToken.class,
            StarAssignToken.class,
            DivAssignToken.class,
            ModAssignToken.class,
            PlusAssignToken.class,
            MinusAssignToken.class,
            LeftShiftAssignToken.class,
            RightShiftAssignToken.class,
            AndAssignToken.class,
            XorAssignToken.class,
            OrAssignToken.class,
            EqualToken.class,
            NotEqualToken.class,
            ArrowToken.class,
            DotToken.class,
            EllipsisToken.class,
            IdentifierToken.class,
            StructOrUnionToken.class,
            StorageClassSpecifierToken.class,
            BasicTypeSpecifierToken.class,
            TypeQualifierToken.class,
            DigitSequenceToken.class,
            WhitespaceToken.class,
            NewlineToken.class,
            BlockCommentToken.class,
            LineCommentToken.class
    );

    static <T> String stringify(java.util.List<T> list, java.util.function.Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(java.util.stream.Collectors.joining(delim));
    }

    static String spaces(int length) {
        return new String(new char[length]).replace('\0', ' ');
    }

    static StringBuilder builder() {
        return new StringBuilder();
    }

    static FilteredStringJoiner joiner(String delim) {
        return new FilteredStringJoiner(delim);
    }

    static String nullable(String given, String def) {
        return given != null ? given : def;
    }

    static String nullable(String pref, String given, String suff, String def) {
        return given != null ? (pref + given + suff) : def;
    }

    public static void main(String[] args) {
        Path inputFile = Paths.get(args[0]);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(inputFile.toFile()));
            new CProgramLexer().getTokens(is).forEach(System.out::println);
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
        STRINGLITERAL,
        CONSTANT,
        DIRECTIVETOKEN,
        AUTO,
        BREAK,
        CASE,
        CHAR,
        CONST,
        CONTINUE,
        DEFAULT,
        DO,
        DOUBLE,
        ELSE,
        ENUM,
        EXTERN,
        FLOAT,
        FOR,
        GOTO,
        IF,
        INLINE,
        INT,
        LONG,
        REGISTER,
        RESTRICT,
        RETURN,
        SHORT,
        SIGNED,
        SIZEOF,
        STATIC,
        STRUCT,
        SWITCH,
        TYPEDEF,
        UNION,
        UNSIGNED,
        VOID,
        VOLATILE,
        WHILE,
        LEFTPAREN,
        RIGHTPAREN,
        LEFTBRACKET,
        RIGHTBRACKET,
        LEFTBRACE,
        RIGHTBRACE,
        LESS,
        LESSEQUAL,
        GREATER,
        GREATEREQUAL,
        LEFTSHIFT,
        RIGHTSHIFT,
        PLUS,
        PLUSPLUS,
        MINUS,
        MINUSMINUS,
        STAR,
        DIV,
        MOD,
        AND,
        OR,
        ANDAND,
        OROR,
        CARET,
        NOT,
        TILDE,
        QUESTION,
        COLON,
        SEMICOLON,
        COMMA,
        SINGLEQUOTE,
        DOUBLEQUOTE,
        ASSIGN,
        STARASSIGN,
        DIVASSIGN,
        MODASSIGN,
        PLUSASSIGN,
        MINUSASSIGN,
        LEFTSHIFTASSIGN,
        RIGHTSHIFTASSIGN,
        ANDASSIGN,
        XORASSIGN,
        ORASSIGN,
        EQUAL,
        NOTEQUAL,
        ARROW,
        DOT,
        ELLIPSIS,
        IDENTIFIER,
        STRUCTORUNION,
        STORAGECLASSSPECIFIER,
        BASICTYPESPECIFIER,
        TYPEQUALIFIER,
        IDENTIFIERNONDIGIT,
        NONDIGIT,
        DIGIT,
        UNIVERSALCHARACTERNAME,
        HEXQUAD,
        INTEGERCONSTANT,
        BINARYCONSTANT,
        DECIMALCONSTANT,
        OCTALCONSTANT,
        HEXADECIMALCONSTANT,
        HEXADECIMALPREFIX,
        NONZERODIGIT,
        OCTALDIGIT,
        HEXADECIMALDIGIT,
        INTEGERSUFFIX,
        UNSIGNEDSUFFIX,
        LONGSUFFIX,
        LONGLONGSUFFIX,
        FLOATINGCONSTANT,
        DECIMALFLOATINGCONSTANT,
        HEXADECIMALFLOATINGCONSTANT,
        FRACTIONALCONSTANT,
        EXPONENTPART,
        SIGN,
        DIGITSEQUENCE,
        HEXADECIMALFRACTIONALCONSTANT,
        BINARYEXPONENTPART,
        FLOATINGSUFFIX,
        CHARACTERCONSTANT,
        CCHAR,
        ESCAPESEQUENCE,
        SIMPLEESCAPESEQUENCE,
        OCTALESCAPESEQUENCE,
        HEXADECIMALESCAPESEQUENCE,
        ENCODINGPREFIX,
        SCHAR,
        WHITESPACE,
        NEWLINE,
        BLOCKCOMMENT,
        LINECOMMENT,
        _FLATTENIDENTIFIER0,
        _FLATTENNEWLINE0,
        _NONE
    }

    static class FilteredStringJoiner {
        private final String prefix;
        private final String delimiter;
        private final String suffix;

        private StringBuilder value;

        private String emptyValue;


        public FilteredStringJoiner(CharSequence delimiter) {
            this(delimiter, "", "");
        }

        public FilteredStringJoiner(CharSequence delimiter,
                                    CharSequence prefix,
                                    CharSequence suffix) {
            Objects.requireNonNull(prefix, "The prefix must not be null");
            Objects.requireNonNull(delimiter, "The delimiter must not be null");
            Objects.requireNonNull(suffix, "The suffix must not be null");
            // make defensive copies of arguments
            this.prefix = prefix.toString();
            this.delimiter = delimiter.toString();
            this.suffix = suffix.toString();
            this.emptyValue = this.prefix + this.suffix;
        }

        @Override
        public String toString() {
            if (value == null) {
                return emptyValue;
            } else {
                if (suffix.equals("")) {
                    return value.toString();
                } else {
                    int initialLength = value.length();
                    String result = value.append(suffix).toString();
                    // reset value to pre-append initialLength
                    value.setLength(initialLength);
                    return result;
                }
            }
        }

        public FilteredStringJoiner add(CharSequence newElement) {
            if (newElement.length() > 0) prepareBuilder().append(newElement);
            return this;
        }

        private StringBuilder prepareBuilder() {
            if (value != null) {
                value.append(delimiter);
            } else {
                value = new StringBuilder().append(prefix);
            }
            return value;
        }

        public int length() {
            return (value != null ? value.length() + suffix.length() :
                    emptyValue.length());
        }
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

    public static class StringLiteralToken extends Token {
        public StringLiteralToken() {
            super(TokenType.STRINGLITERAL, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new EncodingPrefixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        {
                            Token token = new DoubleQuoteToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new SCharToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new SCharToken();
                            }
                        }
                        {
                            Token token = new DoubleQuoteToken();
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

    public static class ConstantToken extends Token {
        public ConstantToken() {
            super(TokenType.CONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new IntegerConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new FloatingConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharacterConstantToken();
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

    public static class DirectiveTokenToken extends Token {
        public DirectiveTokenToken() {
            super(TokenType.DIRECTIVETOKEN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("#");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new WhitespaceToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new WhitespaceToken();
                            }
                        }
                        {
                            Token token = new CharSetTokenHelper("[a-z]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new CharSetTokenHelper("[a-z]");
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new CharSetTokenHelper("[a-z]");
                            }
                        }
                        {
                            Token token = new CharSetTokenHelper("[^\n]");
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new CharSetTokenHelper("[^\n]");
                            }
                        }
                        {
                            Token token = new NewlineToken();
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

    public static class AutoToken extends Token {
        public AutoToken() {
            super(TokenType.AUTO, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("auto");
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

    public static class BreakToken extends Token {
        public BreakToken() {
            super(TokenType.BREAK, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("break");
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

    public static class CaseToken extends Token {
        public CaseToken() {
            super(TokenType.CASE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("case");
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

    public static class CharToken extends Token {
        public CharToken() {
            super(TokenType.CHAR, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("char");
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

    public static class ConstToken extends Token {
        public ConstToken() {
            super(TokenType.CONST, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("const");
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

    public static class ContinueToken extends Token {
        public ContinueToken() {
            super(TokenType.CONTINUE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("continue");
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

    public static class DefaultToken extends Token {
        public DefaultToken() {
            super(TokenType.DEFAULT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("default");
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

    public static class DoToken extends Token {
        public DoToken() {
            super(TokenType.DO, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("do");
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

    public static class DoubleToken extends Token {
        public DoubleToken() {
            super(TokenType.DOUBLE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("double");
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

    public static class ElseToken extends Token {
        public ElseToken() {
            super(TokenType.ELSE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("else");
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

    public static class EnumToken extends Token {
        public EnumToken() {
            super(TokenType.ENUM, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("enum");
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

    public static class ExternToken extends Token {
        public ExternToken() {
            super(TokenType.EXTERN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("extern");
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

    public static class FloatToken extends Token {
        public FloatToken() {
            super(TokenType.FLOAT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("float");
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

    public static class ForToken extends Token {
        public ForToken() {
            super(TokenType.FOR, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("for");
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

    public static class GotoToken extends Token {
        public GotoToken() {
            super(TokenType.GOTO, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("goto");
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

    public static class IfToken extends Token {
        public IfToken() {
            super(TokenType.IF, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("if");
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

    public static class InlineToken extends Token {
        public InlineToken() {
            super(TokenType.INLINE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("inline");
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

    public static class IntToken extends Token {
        public IntToken() {
            super(TokenType.INT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("int");
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

    public static class LongToken extends Token {
        public LongToken() {
            super(TokenType.LONG, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("long");
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

    public static class RegisterToken extends Token {
        public RegisterToken() {
            super(TokenType.REGISTER, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("register");
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

    public static class RestrictToken extends Token {
        public RestrictToken() {
            super(TokenType.RESTRICT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("restrict");
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

    public static class ReturnToken extends Token {
        public ReturnToken() {
            super(TokenType.RETURN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("return");
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

    public static class ShortToken extends Token {
        public ShortToken() {
            super(TokenType.SHORT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("short");
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

    public static class SignedToken extends Token {
        public SignedToken() {
            super(TokenType.SIGNED, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("signed");
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

    public static class SizeofToken extends Token {
        public SizeofToken() {
            super(TokenType.SIZEOF, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("sizeof");
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

    public static class StaticToken extends Token {
        public StaticToken() {
            super(TokenType.STATIC, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("static");
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

    public static class StructToken extends Token {
        public StructToken() {
            super(TokenType.STRUCT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("struct");
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

    public static class SwitchToken extends Token {
        public SwitchToken() {
            super(TokenType.SWITCH, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("switch");
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

    public static class TypedefToken extends Token {
        public TypedefToken() {
            super(TokenType.TYPEDEF, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("typedef");
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

    public static class UnionToken extends Token {
        public UnionToken() {
            super(TokenType.UNION, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("union");
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

    public static class UnsignedToken extends Token {
        public UnsignedToken() {
            super(TokenType.UNSIGNED, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("unsigned");
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

    public static class VoidToken extends Token {
        public VoidToken() {
            super(TokenType.VOID, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("void");
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

    public static class VolatileToken extends Token {
        public VolatileToken() {
            super(TokenType.VOLATILE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("volatile");
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

    public static class WhileToken extends Token {
        public WhileToken() {
            super(TokenType.WHILE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("while");
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

    public static class LeftParenToken extends Token {
        public LeftParenToken() {
            super(TokenType.LEFTPAREN, false);
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

    public static class RightParenToken extends Token {
        public RightParenToken() {
            super(TokenType.RIGHTPAREN, false);
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

    public static class LeftBracketToken extends Token {
        public LeftBracketToken() {
            super(TokenType.LEFTBRACKET, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("[");
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

    public static class RightBracketToken extends Token {
        public RightBracketToken() {
            super(TokenType.RIGHTBRACKET, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("]");
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

    public static class LeftBraceToken extends Token {
        public LeftBraceToken() {
            super(TokenType.LEFTBRACE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("{");
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

    public static class RightBraceToken extends Token {
        public RightBraceToken() {
            super(TokenType.RIGHTBRACE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("}");
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

    public static class LessToken extends Token {
        public LessToken() {
            super(TokenType.LESS, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("<");
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

    public static class LessEqualToken extends Token {
        public LessEqualToken() {
            super(TokenType.LESSEQUAL, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("<=");
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

    public static class GreaterToken extends Token {
        public GreaterToken() {
            super(TokenType.GREATER, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(">");
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

    public static class GreaterEqualToken extends Token {
        public GreaterEqualToken() {
            super(TokenType.GREATEREQUAL, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(">=");
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

    public static class LeftShiftToken extends Token {
        public LeftShiftToken() {
            super(TokenType.LEFTSHIFT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("<<");
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

    public static class RightShiftToken extends Token {
        public RightShiftToken() {
            super(TokenType.RIGHTSHIFT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(">>");
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

    public static class PlusToken extends Token {
        public PlusToken() {
            super(TokenType.PLUS, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("+");
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

    public static class PlusPlusToken extends Token {
        public PlusPlusToken() {
            super(TokenType.PLUSPLUS, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("++");
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

    public static class MinusToken extends Token {
        public MinusToken() {
            super(TokenType.MINUS, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("-");
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

    public static class MinusMinusToken extends Token {
        public MinusMinusToken() {
            super(TokenType.MINUSMINUS, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("--");
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

    public static class StarToken extends Token {
        public StarToken() {
            super(TokenType.STAR, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("*");
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

    public static class DivToken extends Token {
        public DivToken() {
            super(TokenType.DIV, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("/");
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

    public static class ModToken extends Token {
        public ModToken() {
            super(TokenType.MOD, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("%");
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

    public static class AndToken extends Token {
        public AndToken() {
            super(TokenType.AND, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("&");
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

    public static class OrToken extends Token {
        public OrToken() {
            super(TokenType.OR, false);
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

    public static class AndAndToken extends Token {
        public AndAndToken() {
            super(TokenType.ANDAND, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("&&");
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

    public static class OrOrToken extends Token {
        public OrOrToken() {
            super(TokenType.OROR, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("||");
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

    public static class CaretToken extends Token {
        public CaretToken() {
            super(TokenType.CARET, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("^");
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

    public static class NotToken extends Token {
        public NotToken() {
            super(TokenType.NOT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("!");
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

    public static class TildeToken extends Token {
        public TildeToken() {
            super(TokenType.TILDE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("~");
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

    public static class QuestionToken extends Token {
        public QuestionToken() {
            super(TokenType.QUESTION, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("?");
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

    public static class ColonToken extends Token {
        public ColonToken() {
            super(TokenType.COLON, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(":");
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

    public static class SemicolonToken extends Token {
        public SemicolonToken() {
            super(TokenType.SEMICOLON, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(";");
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

    public static class CommaToken extends Token {
        public CommaToken() {
            super(TokenType.COMMA, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(",");
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

    public static class SingleQuoteToken extends Token {
        public SingleQuoteToken() {
            super(TokenType.SINGLEQUOTE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\'");
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

    public static class DoubleQuoteToken extends Token {
        public DoubleQuoteToken() {
            super(TokenType.DOUBLEQUOTE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\"");
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

    public static class AssignToken extends Token {
        public AssignToken() {
            super(TokenType.ASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("=");
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

    public static class StarAssignToken extends Token {
        public StarAssignToken() {
            super(TokenType.STARASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("*=");
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

    public static class DivAssignToken extends Token {
        public DivAssignToken() {
            super(TokenType.DIVASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("/=");
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

    public static class ModAssignToken extends Token {
        public ModAssignToken() {
            super(TokenType.MODASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("%=");
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

    public static class PlusAssignToken extends Token {
        public PlusAssignToken() {
            super(TokenType.PLUSASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("+=");
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

    public static class MinusAssignToken extends Token {
        public MinusAssignToken() {
            super(TokenType.MINUSASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("-=");
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

    public static class LeftShiftAssignToken extends Token {
        public LeftShiftAssignToken() {
            super(TokenType.LEFTSHIFTASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("<<=");
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

    public static class RightShiftAssignToken extends Token {
        public RightShiftAssignToken() {
            super(TokenType.RIGHTSHIFTASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(">>=");
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

    public static class AndAssignToken extends Token {
        public AndAssignToken() {
            super(TokenType.ANDASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("&=");
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

    public static class XorAssignToken extends Token {
        public XorAssignToken() {
            super(TokenType.XORASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("^=");
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

    public static class OrAssignToken extends Token {
        public OrAssignToken() {
            super(TokenType.ORASSIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("|=");
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

    public static class EqualToken extends Token {
        public EqualToken() {
            super(TokenType.EQUAL, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("==");
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

    public static class NotEqualToken extends Token {
        public NotEqualToken() {
            super(TokenType.NOTEQUAL, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("!=");
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

    public static class ArrowToken extends Token {
        public ArrowToken() {
            super(TokenType.ARROW, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("->");
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

    public static class DotToken extends Token {
        public DotToken() {
            super(TokenType.DOT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper(".");
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

    public static class EllipsisToken extends Token {
        public EllipsisToken() {
            super(TokenType.ELLIPSIS, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("...");
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

    public static class IdentifierToken extends Token {
        public IdentifierToken() {
            super(TokenType.IDENTIFIER, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new IdentifierNondigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new _FlattenIdentifier0Token();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new _FlattenIdentifier0Token();
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class StructOrUnionToken extends Token {
        public StructOrUnionToken() {
            super(TokenType.STRUCTORUNION, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StructToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new UnionToken();
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

    public static class StorageClassSpecifierToken extends Token {
        public StorageClassSpecifierToken() {
            super(TokenType.STORAGECLASSSPECIFIER, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new TypedefToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new ExternToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StaticToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new AutoToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new RegisterToken();
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

    public static class BasicTypeSpecifierToken extends Token {
        public BasicTypeSpecifierToken() {
            super(TokenType.BASICTYPESPECIFIER, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new VoidToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new ShortToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new IntToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new LongToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new FloatToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DoubleToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new SignedToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new UnsignedToken();
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

    public static class TypeQualifierToken extends Token {
        public TypeQualifierToken() {
            super(TokenType.TYPEQUALIFIER, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new ConstToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new VolatileToken();
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

    public static class IdentifierNondigitToken extends Token {
        public IdentifierNondigitToken() {
            super(TokenType.IDENTIFIERNONDIGIT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new NondigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new UniversalCharacterNameToken();
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

    public static class NondigitToken extends Token {
        public NondigitToken() {
            super(TokenType.NONDIGIT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[a-zA-Z_]");
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

    public static class DigitToken extends Token {
        public DigitToken() {
            super(TokenType.DIGIT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[0-9]");
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

    public static class UniversalCharacterNameToken extends Token {
        public UniversalCharacterNameToken() {
            super(TokenType.UNIVERSALCHARACTERNAME, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\u");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexQuadToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\U");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexQuadToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexQuadToken();
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

    public static class HexQuadToken extends Token {
        public HexQuadToken() {
            super(TokenType.HEXQUAD, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalDigitToken();
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

    public static class IntegerConstantToken extends Token {
        public IntegerConstantToken() {
            super(TokenType.INTEGERCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DecimalConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new IntegerSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new OctalConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new IntegerSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new IntegerSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new BinaryConstantToken();
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

    public static class BinaryConstantToken extends Token {
        public BinaryConstantToken() {
            super(TokenType.BINARYCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("0");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new CharSetTokenHelper("[bB]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new CharSetTokenHelper("[0-1]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new CharSetTokenHelper("[0-1]");
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new CharSetTokenHelper("[0-1]");
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class DecimalConstantToken extends Token {
        public DecimalConstantToken() {
            super(TokenType.DECIMALCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new NonzeroDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new DigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new DigitToken();
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class OctalConstantToken extends Token {
        public OctalConstantToken() {
            super(TokenType.OCTALCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("0");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new OctalDigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new OctalDigitToken();
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class HexadecimalConstantToken extends Token {
        public HexadecimalConstantToken() {
            super(TokenType.HEXADECIMALCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalPrefixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new HexadecimalDigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new HexadecimalDigitToken();
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class HexadecimalPrefixToken extends Token {
        public HexadecimalPrefixToken() {
            super(TokenType.HEXADECIMALPREFIX, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("0");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new CharSetTokenHelper("[xX]");
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

    public static class NonzeroDigitToken extends Token {
        public NonzeroDigitToken() {
            super(TokenType.NONZERODIGIT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[1-9]");
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

    public static class OctalDigitToken extends Token {
        public OctalDigitToken() {
            super(TokenType.OCTALDIGIT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[0-7]");
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

    public static class HexadecimalDigitToken extends Token {
        public HexadecimalDigitToken() {
            super(TokenType.HEXADECIMALDIGIT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[0-9a-fA-F]");
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

    public static class IntegerSuffixToken extends Token {
        public IntegerSuffixToken() {
            super(TokenType.INTEGERSUFFIX, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new UnsignedSuffixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new LongSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new UnsignedSuffixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new LongLongSuffixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new LongSuffixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new UnsignedSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new LongLongSuffixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new UnsignedSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class UnsignedSuffixToken extends Token {
        public UnsignedSuffixToken() {
            super(TokenType.UNSIGNEDSUFFIX, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[uU]");
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

    public static class LongSuffixToken extends Token {
        public LongSuffixToken() {
            super(TokenType.LONGSUFFIX, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[lL]");
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

    public static class LongLongSuffixToken extends Token {
        public LongLongSuffixToken() {
            super(TokenType.LONGLONGSUFFIX, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("ll");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("LL");
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

    public static class FloatingConstantToken extends Token {
        public FloatingConstantToken() {
            super(TokenType.FLOATINGCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DecimalFloatingConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalFloatingConstantToken();
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

    public static class DecimalFloatingConstantToken extends Token {
        public DecimalFloatingConstantToken() {
            super(TokenType.DECIMALFLOATINGCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new FractionalConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new ExponentPartToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        {
                            Token token = new FloatingSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DigitSequenceToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new ExponentPartToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new FloatingSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class HexadecimalFloatingConstantToken extends Token {
        public HexadecimalFloatingConstantToken() {
            super(TokenType.HEXADECIMALFLOATINGCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalPrefixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalFractionalConstantToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new BinaryExponentPartToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new FloatingSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalPrefixToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new HexadecimalDigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new HexadecimalDigitToken();
                            }
                        }
                        {
                            Token token = new BinaryExponentPartToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new FloatingSuffixToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class FractionalConstantToken extends Token {
        public FractionalConstantToken() {
            super(TokenType.FRACTIONALCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DigitSequenceToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        {
                            Token token = new DotToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new DigitSequenceToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DigitSequenceToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new DotToken();
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

    public static class ExponentPartToken extends Token {
        public ExponentPartToken() {
            super(TokenType.EXPONENTPART, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new SignToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        {
                            Token token = new DigitSequenceToken();
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

    public static class SignToken extends Token {
        public SignToken() {
            super(TokenType.SIGN, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[-+]");
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

    public static class DigitSequenceToken extends Token {
        public DigitSequenceToken() {
            super(TokenType.DIGITSEQUENCE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new DigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new DigitToken();
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class HexadecimalFractionalConstantToken extends Token {
        public HexadecimalFractionalConstantToken() {
            super(TokenType.HEXADECIMALFRACTIONALCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalDigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new HexadecimalDigitToken();
                            }
                        }
                        {
                            Token token = new DotToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new HexadecimalDigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new HexadecimalDigitToken();
                            }
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new HexadecimalDigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new HexadecimalDigitToken();
                            }
                        }
                        {
                            Token token = new DotToken();
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

    public static class BinaryExponentPartToken extends Token {
        public BinaryExponentPartToken() {
            super(TokenType.BINARYEXPONENTPART, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[pP]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new SignToken();
                            if (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                            }
                        }
                        {
                            Token token = new DigitSequenceToken();
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

    public static class FloatingSuffixToken extends Token {
        public FloatingSuffixToken() {
            super(TokenType.FLOATINGSUFFIX, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[fFlL]");
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

    public static class CharacterConstantToken extends Token {
        public CharacterConstantToken() {
            super(TokenType.CHARACTERCONSTANT, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[LuU]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new SingleQuoteToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new CCharToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new CCharToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new CCharToken();
                            }
                        }
                        {
                            Token token = new SingleQuoteToken();
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

    public static class CCharToken extends Token {
        public CCharToken() {
            super(TokenType.CCHAR, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[^'\\\r\n]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new EscapeSequenceToken();
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

    public static class EscapeSequenceToken extends Token {
        public EscapeSequenceToken() {
            super(TokenType.ESCAPESEQUENCE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new SimpleEscapeSequenceToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new OctalEscapeSequenceToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new HexadecimalEscapeSequenceToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new UniversalCharacterNameToken();
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

    public static class SimpleEscapeSequenceToken extends Token {
        public SimpleEscapeSequenceToken() {
            super(TokenType.SIMPLEESCAPESEQUENCE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new CharSetTokenHelper("['\"?abfnrtv0\\]");
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

    public static class OctalEscapeSequenceToken extends Token {
        public OctalEscapeSequenceToken() {
            super(TokenType.OCTALESCAPESEQUENCE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new OctalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new OctalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new OctalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new OctalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new OctalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new OctalDigitToken();
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

    public static class HexadecimalEscapeSequenceToken extends Token {
        public HexadecimalEscapeSequenceToken() {
            super(TokenType.HEXADECIMALESCAPESEQUENCE, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\x");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new HexadecimalDigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new HexadecimalDigitToken();
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new HexadecimalDigitToken();
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class EncodingPrefixToken extends Token {
        public EncodingPrefixToken() {
            super(TokenType.ENCODINGPREFIX, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("u8");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("u");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("U");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("L");
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

    public static class SCharToken extends Token {
        public SCharToken() {
            super(TokenType.SCHAR, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[^\"\\\r\n]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new EscapeSequenceToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\\");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new NewlineToken();
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

    public static class WhitespaceToken extends Token {
        public WhitespaceToken() {
            super(TokenType.WHITESPACE, true);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new CharSetTokenHelper("[ \t]");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                            token = new CharSetTokenHelper("[ \t]");
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new CharSetTokenHelper("[ \t]");
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class NewlineToken extends Token {
        public NewlineToken() {
            super(TokenType.NEWLINE, true);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new _FlattenNewline0Token();
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

    public static class BlockCommentToken extends Token {
        public BlockCommentToken() {
            super(TokenType.BLOCKCOMMENT, true);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("/*");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new CharSetTokenHelper("[^*]");
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new CharSetTokenHelper("[^*]");
                            }
                        }
                        {
                            Token token = new StringTokenHelper("*/");
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

    public static class LineCommentToken extends Token {
        public LineCommentToken() {
            super(TokenType.LINECOMMENT, true);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("//");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        {
                            Token token = new CharSetTokenHelper("[^\r\n]");
                            while (token.consume(input) != null) {
                                list.add(token);
                                input = input.subSequence(token.length(), input.length());
                                token = new CharSetTokenHelper("[^\r\n]");
                            }
                        }
                        return list;
                    }
            );
        }
    }

    public static class _FlattenIdentifier0Token extends Token {
        public _FlattenIdentifier0Token() {
            super(TokenType._FLATTENIDENTIFIER0, false);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new IdentifierNondigitToken();
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new DigitToken();
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

    public static class _FlattenNewline0Token extends Token {
        public _FlattenNewline0Token() {
            super(TokenType._FLATTENNEWLINE0, true);
        }

        List<Function<CharSequence, List<Token>>> getAlternatives() {
            return Arrays.asList(
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\r\n");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\r");
                            if (token.consume(input) == null) {
                                return null;
                            }
                            list.add(token);
                            input = input.subSequence(token.length(), input.length());
                        }
                        return list;
                    },
                    input -> {
                        List<Token> list = new ArrayList<>();

                        {
                            Token token = new StringTokenHelper("\n");
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



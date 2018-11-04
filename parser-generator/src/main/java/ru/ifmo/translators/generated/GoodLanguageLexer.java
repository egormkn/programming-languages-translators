
package ru.ifmo.translators.generated;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GoodLanguageLexer {


    static <T> String stringify(java.util.List<T> list, java.util.function.Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(java.util.stream.Collectors.joining(delim));
    }

    public static String initTuple(String tuple, String type, String assignment) {
        StringBuilder code = new StringBuilder();
        List<String> vars = Arrays.asList(tuple.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
        vars.stream().map(v -> type + " " + v + ";\n").forEach(code::append);
        if (assignment.indexOf("%SCANF%") > -1) {
            code.append(assignment.replace("%SCANF%", vars.stream().map(v -> "&" + v).collect(Collectors.joining(", "))));
        } else if (assignment.indexOf("%PRINTF%") > -1) {
            code.append(assignment.replace("%PRINTF%", vars.stream().collect(Collectors.joining(", "))));
        } else {
            String updateCode = updateTuple(tuple, type, assignment);
            code.append(updateCode.substring(2, updateCode.length() - 3));
        }
        return code.toString();
    }

    public static String updateTuple(String tuple, String type, String assignment) {
        StringBuilder code = new StringBuilder();
        List<String> vars = Arrays.asList(tuple.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
        if (assignment.indexOf("%SCANF%") > -1) {
            code.append(assignment.replace("%SCANF%", vars.stream().map(v -> "&" + v).collect(Collectors.joining(", "))));
        } else if (assignment.indexOf("%PRINTF%") > -1) {
            code.append(assignment.replace("%PRINTF%", vars.stream().collect(Collectors.joining(", "))));
        } else {
            List<String> assignments = Arrays.asList(assignment.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
            if (vars.size() != assignments.size()) {
                throw new AssertionError("Wrong tuple syntax: " + tuple + " " + assignment);
            }
            for (int i = 0; i < vars.size(); i++) {
                code.append(type + " __TEMP" + vars.get(i) + " = " + assignments.get(i) + ";\n");
            }
            for (int i = 0; i < vars.size(); i++) {
                code.append(vars.get(i) + " = __TEMP" + vars.get(i) + ";\n");
            }
        }
        return "{\n" + code.toString() + "\n}\n";
    }


    // Non-fragment lexer rules
    private static List<Class<? extends Token>> nonFragmentClasses = Arrays.asList(
            NewlineToken.class,
        BlockCommentToken.class,
        LineCommentToken.class,
        StringLiteralToken.class,
        BreakToken.class,
        CaseToken.class,
        CharToken.class,
        ConstToken.class,
        ContinueToken.class,
        DefaultToken.class,
        DoubleToken.class,
        DoToken.class,
        ElseToken.class,
        FloatToken.class,
        ForToken.class,
        FunctionToken.class,
        GotoToken.class,
        IfToken.class,
        IntToken.class,
        LongToken.class,
        PrintToken.class,
        ReadToken.class,
        ReturnToken.class,
        ShortToken.class,
        SignedToken.class,
        SizeofToken.class,
        StaticToken.class,
        StringToken.class,
        StructToken.class,
        SwitchToken.class,
        TypedefToken.class,
        UnionToken.class,
        UnsignedToken.class,
        VarToken.class,
        VoidToken.class,
        VolatileToken.class,
        WhileToken.class,
        LeftParenToken.class,
        RightParenToken.class,
        LeftBracketToken.class,
        RightBracketToken.class,
        LeftBraceToken.class,
        RightBraceToken.class,
        LeftShiftToken.class,
        LessEqualToken.class,
        LessToken.class,
        RightShiftToken.class,
        GreaterEqualToken.class,
        GreaterToken.class,
        ArrowToken.class,
        EqualToken.class,
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
        NotEqualToken.class,
        PlusPlusToken.class,
        PlusToken.class,
        MinusMinusToken.class,
        MinusToken.class,
        StarToken.class,
        DivToken.class,
        ModToken.class,
        AndAndToken.class,
        AndToken.class,
        OrOrToken.class,
        OrToken.class,
        CaretToken.class,
        NotToken.class,
        TildeToken.class,
        QuestionToken.class,
        ColonToken.class,
        SemicolonToken.class,
        CommaToken.class,
        EllipsisToken.class,
        DotToken.class,
        DirectiveToken.class,
        IdentifierToken.class,
        ConstantToken.class,
        DigitSequenceToken.class,
        WhitespaceToken.class
    );

    // All lexer token types
    public enum TokenType {
        NEWLINE,
        BLOCKCOMMENT,
        LINECOMMENT,
        STRINGLITERAL,
        BREAK,
        CASE,
        CHAR,
        CONST,
        CONTINUE,
        DEFAULT,
        DOUBLE,
        DO,
        ELSE,
        FLOAT,
        FOR,
        FUNCTION,
        GOTO,
        IF,
        INT,
        LONG,
        PRINT,
        READ,
        RETURN,
        SHORT,
        SIGNED,
        SIZEOF,
        STATIC,
        STRING,
        STRUCT,
        SWITCH,
        TYPEDEF,
        UNION,
        UNSIGNED,
        VAR,
        VOID,
        VOLATILE,
        WHILE,
        LEFTPAREN,
        RIGHTPAREN,
        LEFTBRACKET,
        RIGHTBRACKET,
        LEFTBRACE,
        RIGHTBRACE,
        LEFTSHIFT,
        LESSEQUAL,
        LESS,
        RIGHTSHIFT,
        GREATEREQUAL,
        GREATER,
        ARROW,
        EQUAL,
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
        NOTEQUAL,
        PLUSPLUS,
        PLUS,
        MINUSMINUS,
        MINUS,
        STAR,
        DIV,
        MOD,
        ANDAND,
        AND,
        OROR,
        OR,
        CARET,
        NOT,
        TILDE,
        QUESTION,
        COLON,
        SEMICOLON,
        COMMA,
        ELLIPSIS,
        DOT,
        DIRECTIVE,
        IDENTIFIER,
        IDENTIFIERNONDIGIT,
        NONDIGIT,
        DIGIT,
        UNIVERSALCHARACTERNAME,
        HEXQUAD,
        CONSTANT,
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
        HEXADECIMALDIGITSEQUENCE,
        FLOATINGSUFFIX,
        CHARACTERCONSTANT,
        CCHARSEQUENCE,
        CCHAR,
        ESCAPESEQUENCE,
        SIMPLEESCAPESEQUENCE,
        OCTALESCAPESEQUENCE,
        HEXADECIMALESCAPESEQUENCE,
        SCHARSEQUENCE,
        SCHAR,
        WHITESPACE,
        _EPS,
        BLOCKCOMMENT_FLATTEN0,
        DIRECTIVE_FLATTEN0,
        IDENTIFIER_FLATTEN0,
        _NONE
    }

    public static void main(String[] args) {
        Path inputFile = Paths.get(args[0]);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(inputFile.toFile()));
            new GoodLanguageLexer().getTokens(is).forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Token> getTokens(InputStream is) throws IOException, ParseException {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = is.read()) != -1) builder.append((char) c);
        CharBuffer input = CharBuffer.wrap(builder.toString());

        int position = 0, length = input.length();

        List<Token> tokens = new ArrayList<>();
        while (position < length) {
            Token token = getToken(input.subSequence(position, length));
            if (token == null) throw new ParseException("Can't recognize token: " + input.subSequence(position, length), position);
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

    public static abstract class Token {
        private final TokenType type;
        private boolean skip;
        protected String text = "", data = "";

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

        void setSkip(boolean skip) {
            this.skip = skip;
        }

        public boolean isSkip() {
            return skip;
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

public static class NewlineToken extends Token {
    public NewlineToken() {
        super(TokenType.NEWLINE, false);
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
Token token = new BlockComment_flatten0Token();
while (token.consume(input) != null) {
    list.add(token);
    input = input.subSequence(token.length(), input.length());
    token = new BlockComment_flatten0Token();
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

public static class StringLiteralToken extends Token {
    public StringLiteralToken() {
        super(TokenType.STRINGLITERAL, false);
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
{
Token token = new SCharSequenceToken();
if (token.consume(input) != null) {
    list.add(token);
    input = input.subSequence(token.length(), input.length());
}
}
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

public static class FunctionToken extends Token {
    public FunctionToken() {
        super(TokenType.FUNCTION, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("function");
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

public static class PrintToken extends Token {
    public PrintToken() {
        super(TokenType.PRINT, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("print");
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

public static class ReadToken extends Token {
    public ReadToken() {
        super(TokenType.READ, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("read");
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

public static class StringToken extends Token {
    public StringToken() {
        super(TokenType.STRING, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("string");
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

public static class VarToken extends Token {
    public VarToken() {
        super(TokenType.VAR, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("var");
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

public static class DirectiveToken extends Token {
    public DirectiveToken() {
        super(TokenType.DIRECTIVE, false);
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
Token token = new Directive_flatten0Token();
while (token.consume(input) != null) {
    list.add(token);
    input = input.subSequence(token.length(), input.length());
    token = new Directive_flatten0Token();
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
Token token = new Identifier_flatten0Token();
while (token.consume(input) != null) {
    list.add(token);
    input = input.subSequence(token.length(), input.length());
    token = new Identifier_flatten0Token();
}
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

public static class ConstantToken extends Token {
    public ConstantToken() {
        super(TokenType.CONSTANT, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
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
Token token = new HexadecimalDigitSequenceToken();
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
Token token = new StringTokenHelper("e");
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
},
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("E");
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

public static class SignToken extends Token {
    public SignToken() {
        super(TokenType.SIGN, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new PlusToken();
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
Token token = new MinusToken();
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
Token token = new HexadecimalDigitSequenceToken();
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
Token token = new HexadecimalDigitSequenceToken();
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
Token token = new HexadecimalDigitSequenceToken();
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

public static class BinaryExponentPartToken extends Token {
    public BinaryExponentPartToken() {
        super(TokenType.BINARYEXPONENTPART, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("p");
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
},
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("P");
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

public static class HexadecimalDigitSequenceToken extends Token {
    public HexadecimalDigitSequenceToken() {
        super(TokenType.HEXADECIMALDIGITSEQUENCE, false);
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

public static class FloatingSuffixToken extends Token {
    public FloatingSuffixToken() {
        super(TokenType.FLOATINGSUFFIX, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("f");
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
Token token = new StringTokenHelper("l");
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
Token token = new StringTokenHelper("F");
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

public static class CharacterConstantToken extends Token {
    public CharacterConstantToken() {
        super(TokenType.CHARACTERCONSTANT, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("'");
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new CCharSequenceToken();
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new StringTokenHelper("'");
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
Token token = new StringTokenHelper("L'");
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new CCharSequenceToken();
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new StringTokenHelper("'");
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
Token token = new StringTokenHelper("u'");
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new CCharSequenceToken();
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new StringTokenHelper("'");
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
Token token = new StringTokenHelper("U'");
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new CCharSequenceToken();
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new StringTokenHelper("'");
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

public static class CCharSequenceToken extends Token {
    public CCharSequenceToken() {
        super(TokenType.CCHARSEQUENCE, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

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
Token token = new CharSetTokenHelper("['\"?abf0nrtv\\]");
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

public static class SCharSequenceToken extends Token {
    public SCharSequenceToken() {
        super(TokenType.SCHARSEQUENCE, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new SCharToken();
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
token = new SCharToken();
while (token.consume(input) != null) {
    list.add(token);
    input = input.subSequence(token.length(), input.length());
    token = new SCharToken();
}
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
Token token = new StringTokenHelper("\\\n");
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
Token token = new StringTokenHelper("\\\r\n");
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

public static class _EPSToken extends Token {
    public _EPSToken() {
        super(TokenType._EPS, true);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new StringTokenHelper("");
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

public static class BlockComment_flatten0Token extends Token {
    public BlockComment_flatten0Token() {
        super(TokenType.BLOCKCOMMENT_FLATTEN0, true);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new CharSetTokenHelper("[^*]");
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
Token token = new CharSetTokenHelper("[*]");
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new CharSetTokenHelper("[^/]");
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

public static class Directive_flatten0Token extends Token {
    public Directive_flatten0Token() {
        super(TokenType.DIRECTIVE_FLATTEN0, false);
    }

    List<Function<CharSequence, List<Token>>> getAlternatives() {
        return Arrays.asList(
input -> {
    List<Token> list = new ArrayList<>();

{
Token token = new CharSetTokenHelper("[^\\\r\n]");
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
Token token = new CharSetTokenHelper("[\\]");
if (token.consume(input) == null) {
    return null;
}
list.add(token);
input = input.subSequence(token.length(), input.length());
}
{
Token token = new CharSetTokenHelper("[\r\n]");
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

public static class Identifier_flatten0Token extends Token {
    public Identifier_flatten0Token() {
        super(TokenType.IDENTIFIER_FLATTEN0, false);
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
}



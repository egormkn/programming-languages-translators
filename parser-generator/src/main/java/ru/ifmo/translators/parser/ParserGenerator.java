package ru.ifmo.translators.parser;

import ru.ifmo.translators.grammar.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ParserGenerator {

    private final Grammar grammar;

    public ParserGenerator(Grammar grammar) {
        this.grammar = grammar;
    }

    private static String upper(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public void generate(Path path) throws IOException {
        path = path.resolve(grammar.getName() + "Parser.java");
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path.toFile()));
        outputStream.write(generateParser().getBytes());
        outputStream.close();
    }

    private String generateParser() {
        return grammar.getHeader() +
                "\n" +
                "import java.io.*;\n" +
                "import java.nio.CharBuffer;\n" +
                "import java.nio.file.Path;\n" +
                "import java.nio.file.Paths;\n" +
                "import java.text.ParseException;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "import java.util.StringJoiner;\n" +
                "import java.util.function.Function;\n" +
                "import java.util.regex.Matcher;\n" +
                "import java.util.regex.Pattern;\n" +
                "\n" +
                "@SuppressWarnings({\"all\", \"warnings\", \"unchecked\", \"unused\", \"cast\"})\n" +
                "public class " + grammar.getName() + "Lexer {\n" +
                "\n" +
                grammar.getMembers() + "\n" +
                "\n" +
                "    // Non-fragment lexer rules\n" +
                "    private static List<Class<? extends Token>> nonFragmentClasses = Arrays.asList(\n" +
                "            " + grammar.getLexerRules().values().stream()
                .filter(r -> !r.isFragment())
                .map(r -> r.getName() + "Token.class")
                .collect(Collectors.joining(",\n        ")) + "\n" +
                "    );\n" +
                "\n" +
                "    // All lexer token types\n" +
                "    public enum TokenType {\n" +
                "        " + grammar.getLexerRules().values().stream()
                .map(LexerRule::getName)
                .map(String::toUpperCase)
                .collect(Collectors.joining(",\n        ")) + ",\n" +
                "        _NONE\n" +
                "    }\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        Path inputFile = Paths.get(args[0]);\n" +
                "        try {\n" +
                "            InputStream is = new BufferedInputStream(new FileInputStream(inputFile.toFile()));\n" +
                "            new " + grammar.getName() + "Lexer().getTokens(is).forEach(System.out::println);\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private List<Token> getTokens(InputStream is) throws IOException, ParseException {\n" +
                "        StringBuilder builder = new StringBuilder();\n" +
                "        int c;\n" +
                "        while ((c = is.read()) != -1) builder.append((char) c);\n" +
                "        CharBuffer input = CharBuffer.wrap(builder.toString());\n" +
                "\n" +
                "        int position = 0, length = input.length();\n" +
                "\n" +
                "        List<Token> tokens = new ArrayList<>();\n" +
                "        while (position < length) {\n" +
                "            Token token = getToken(input.subSequence(position, length));\n" +
                "            if (token == null) throw new ParseException(\"Can't recognize token: \" + input.subSequence(position, length), position);\n" +
                "            position += token.length();\n" +
                "            if (!token.isSkip()) tokens.add(token);\n" +
                "        }\n" +
                "        if (position != length) {\n" +
                "            throw new AssertionError(\"Position > length\");\n" +
                "        }\n" +
                "        return tokens;\n" +
                "    }\n" +
                "\n" +
                "    private Token getToken(CharSequence input) {\n" +
                "        for (Class<? extends Token> tokenClass : nonFragmentClasses) {\n" +
                "            try {\n" +
                "                Token t = tokenClass.getConstructor().newInstance();\n" +
                "                if (t.consume(input) != null) return t;\n" +
                "            } catch (Exception e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    public static abstract class Token {\n" +
                "        private final TokenType type;\n" +
                "        private boolean skip;\n" +
                "        protected String text = \"\", data = \"\";\n" +
                "\n" +
                "        protected Token() {\n" +
                "            this.type = TokenType._NONE;\n" +
                "            this.skip = false;\n" +
                "        }\n" +
                "\n" +
                "        protected Token(TokenType type, boolean skip) {\n" +
                "            this.type = type;\n" +
                "            this.skip = skip;\n" +
                "        }\n" +
                "\n" +
                "        public TokenType getType() {\n" +
                "            return type;\n" +
                "        }\n" +
                "\n" +
                "        public int getTypeId() {\n" +
                "            return type.ordinal();\n" +
                "        }\n" +
                "\n" +
                "        public String getText() {\n" +
                "            return text;\n" +
                "        }\n" +
                "\n" +
                "        void setText(String text) {\n" +
                "            this.text = text;\n" +
                "        }\n" +
                "\n" +
                "        void setSkip(boolean skip) {\n" +
                "            this.skip = skip;\n" +
                "        }\n" +
                "\n" +
                "        public boolean isSkip() {\n" +
                "            return skip;\n" +
                "        }\n" +
                "\n" +
                "        public String getData() {\n" +
                "            return data;\n" +
                "        }\n" +
                "\n" +
                "        public int length() {\n" +
                "            return data.length();\n" +
                "        }\n" +
                "\n" +
                "        Token consume(CharSequence input) {\n" +
                "            for (Function<CharSequence, List<Token>> alternative : getAlternatives()) {\n" +
                "                List<Token> result = alternative.apply(input);\n" +
                "                if (result != null) {\n" +
                "                    result.forEach(t -> {\n" +
                "                        text += t.text;\n" +
                "                        data += t.data;\n" +
                "                    });\n" +
                "                    return this;\n" +
                "                }\n" +
                "            }\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        abstract List<Function<CharSequence, List<Token>>> getAlternatives();\n" +
                "\n" +
                "        @Override\n" +
                "        public String toString() {\n" +
                "            return new StringJoiner(\", \", Token.class.getSimpleName() + \"[\", \"]\")\n" +
                "                    .add(\"type=\" + type)\n" +
                "                    .add(\"skip=\" + skip)\n" +
                "                    .add(\"text='\" + text + \"'\")\n" +
                "                    .add(\"data='\" + data + \"'\")\n" +
                "                    .toString();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static class StringTokenHelper extends Token {\n" +
                "\n" +
                "        private final String str;\n" +
                "\n" +
                "        StringTokenHelper(String str) {\n" +
                "            this.str = str;\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        public Token consume(CharSequence input) {\n" +
                "            if (input.length() < str.length()) {\n" +
                "                return null;\n" +
                "            } else if (!input.subSequence(0, str.length()).toString().equals(str)) {\n" +
                "                return null;\n" +
                "            }\n" +
                "\n" +
                "            text += str;\n" +
                "            data += str;\n" +
                "            return this;\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        List<Function<CharSequence, List<Token>>> getAlternatives() {\n" +
                "            return null;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static class CharSetTokenHelper extends Token {\n" +
                "\n" +
                "        private final String charset;\n" +
                "        private final Pattern pattern;\n" +
                "\n" +
                "        CharSetTokenHelper(String charset) {\n" +
                "            this.charset = charset;\n" +
                "            this.pattern = Pattern.compile(charset);\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        public Token consume(CharSequence input) {\n" +
                "            if (input.length() < 1) {\n" +
                "                return null;\n" +
                "            } else if (!pattern.matcher(input.subSequence(0, 1)).find()) {\n" +
                "                return null;\n" +
                "            }\n" +
                "\n" +
                "            text += input.charAt(0);\n" +
                "            data += input.charAt(0);\n" +
                "            return this;\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        List<Function<CharSequence, List<Token>>> getAlternatives() {\n" +
                "            return null;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                grammar.getParserRules().values().stream()
                        .map(this::generateRuleContext)
                        .collect(Collectors.joining("\n")) + "\n" +
                "}\n" +
                "\n" +
                grammar.getFooter() +
                "\n";
    }

    private String generateRuleContext(ParserRule rule) {
        String name = Character.toUpperCase(rule.getName().charAt(0)) + rule.getName().substring(1);
        StringJoiner joiner = new StringJoiner("\n")
                .add("    public static class " + name + "RuleContext extends RuleContext {")
                .add("        public " + name + "RuleContext() {")
                .add("            ")
                .add("        }")
                .add("    ")
                .add(generateRuleContextFields(rule))
                .add("    }")
                .add("    ");
        return joiner.toString();
    }

    private String generateRuleContextFields(ParserRule rule) {
        Map<String, Integer> lexerRules = new HashMap<>();
        Map<String, Integer> parserRules = new HashMap<>();
        countContextFields(rule.getAlternative(), lexerRules, parserRules);

        StringJoiner joiner = new StringJoiner("\n");
        lexerRules.forEach((String key, Integer value) -> {
            if (value == 1) {
                joiner.add("        public Token " + key + ";");
            } else {
                joiner.add("        public List<Token> " + key + " = new ArrayList<>();");
            }
        });
        parserRules.forEach((String key, Integer value) -> {
            String name = upper(key);
            if (value == 1) {
                joiner.add("        public " + name + "RuleContext " + key + ";");
            } else {
                joiner.add("        public List<" + name + "RuleContext> " + key + " = new ArrayList<>();");
            }
        });
        return joiner.toString();
    }

    private void countContextFields(ParserAlternative alternative,
                                    Map<String, Integer> lexerRules,
                                    Map<String, Integer> parserRules) {
        for (List<ParserAlternative.Wrapper> sequence : alternative.getAlternatives()) {
            for (ParserAlternative.Wrapper wrapper : sequence) {
                ParserToken token = wrapper.getToken();
                System.out.println(token.toString());
                if (token instanceof LexerRule) {
                    lexerRules.compute(
                            ((LexerRule) token).getName(),
                            (String key, Integer i) -> i == null ? 1 : i + 1);
                } else if (token instanceof ParserRule) {
                    parserRules.compute(
                            ((ParserRule) token).getName(),
                            (String key, Integer i) -> i == null ? 1 : i + 1);
                } else if (token instanceof ParserAlternative) {
                    countContextFields((ParserAlternative) token, lexerRules, parserRules);
                } else {
                    throw new AssertionError("Wrong token type");
                }
            }
        }
    }
}

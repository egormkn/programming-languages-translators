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
import java.util.stream.Stream;

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
                "public class " + grammar.getName() + "Parser {\n" +
                "\n" +
                grammar.getMembers() + "\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        Path inputFile = Paths.get(args[0]);\n" +
                "        try {\n" +
                "            InputStream is = new BufferedInputStream(new FileInputStream(inputFile.toFile()));\n" +
                "            List<" + grammar.getName() + "Lexer.Token> tokens = new " + grammar.getName() + "Lexer().getTokens(is);\n" +
                "            System.out.println(new " + grammar.getName() + "Parser()." + grammar.getParserRules().values().iterator().next().getName() + "(tokens).toString());\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public static abstract class RuleContext {\n" +
                "        private StringBuilder builder = new StringBuilder();\n" +
                "        \n" +
                "        protected void addText(String s) {\n" +
                "            builder.append(s);\n" +
                "        }\n" +
                "        \n" +
                "        public String getText() {\n" +
                "            return builder.toString();\n" +
                "        }\n" +
                "        \n" +
                "        public void setText(String s) {\n" +
                "            builder = new StringBuilder(s);\n" +
                "        }\n" +
                "        \n" +
                "        private int length = 0;\n" +
                "        \n" +
                "        public void addLength(int n) {\n" +
                "            length += n;\n" +
                "        }\n" +
                "        \n" +
                "        public int length() {\n" +
                "            return length;\n" +
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
        Map<String, Boolean> lexerRules = new HashMap<>();
        Map<String, Boolean> parserRules = new HashMap<>();
        System.err.println(rule.getName());
        countContextFields(rule.getAlternative(), lexerRules, parserRules);
        for (Map.Entry<String, Boolean> entry : lexerRules.entrySet()) {
            System.err.println(entry.getKey() + ": " + (entry.getValue() ? "list" : "single"));
        }
        for (Map.Entry<String, Boolean> entry : parserRules.entrySet()) {
            System.err.println(entry.getKey() + ": " + (entry.getValue() ? "list" : "single"));
        }
        System.err.println();

        StringJoiner ruleFields = new StringJoiner("\n");
        StringJoiner ruleMethods = new StringJoiner("\n");
        lexerRules.forEach((String key, Boolean isMany) -> {
            if (!isMany) {
                ruleFields
                        .add("        private " + grammar.getName() + "Lexer.Token " + key + ";");
                ruleMethods
                        .add("        public " + grammar.getName() + "Lexer.Token " + key + "() {")
                        .add("            return " + key + ";")
                        .add("        }")
                        .add("        ");
            } else {
                ruleFields
                        .add("        private List<" + grammar.getName() + "Lexer.Token> " + key + " = new ArrayList<>();");
                ruleMethods
                        .add("        public List<" + grammar.getName() + "Lexer.Token> " + key + "() {")
                        .add("            return " + key + ";")
                        .add("        }")
                        .add("        ");
            }
        });
        parserRules.forEach((String key, Boolean isMany) -> {
            String name = upper(key);
            if (!isMany) {
                ruleFields
                        .add("        private " + name + "RuleContext " + key + ";");
                ruleMethods
                        .add("        public " + name + "RuleContext " + key + "() {")
                        .add("            return " + key + ";")
                        .add("        }")
                        .add("        ");
            } else {
                ruleFields
                        .add("        private List<" + name + "RuleContext> " + key + " = new ArrayList<>();");
                ruleMethods
                        .add("        public List<" + name + "RuleContext> " + key + "() {")
                        .add("            return " + key + ";")
                        .add("        }")
                        .add("        ");
            }
        });


        String name = Character.toUpperCase(rule.getName().charAt(0)) + rule.getName().substring(1);
        StringJoiner joiner = new StringJoiner("\n")
                .add("    public static class " + name + "RuleContext extends RuleContext {")
                .add("        public " + name + "RuleContext() {")
                .add("            ")
                .add("        }")
                .add("        public void after() {")
                .add("            " + rule.getAfterCode().replaceAll("\\$ctx", "((" + name + "RuleContext) this)"))
                .add("        }")
                .add("    ")
                .add(ruleFields.toString())
                .add("    ")
                .add(ruleMethods.toString())
                .add("    ")
                .add(generateReturnFields(rule.getRet()))
                .add("    }")
                .add("    ")
                .add("    public " + name + "RuleContext " + rule.getName() + "(List<" + grammar.getName() + "Lexer.Token> tokens" + (!rule.getArgs().isEmpty() ? ", " + rule.getArgs() : "") + ") {")
                .add(rule.getAlternatives().stream().map(sequence -> generateAlternative(sequence, name, lexerRules, parserRules)).collect(Collectors.joining("\n\n")))
                .add("        // System.err.println(\"Can't build " + rule.getName() + " from input: \");")
                .add("        // tokens.forEach(System.out::println);")
                .add("        return null;")
                .add("    }")
                .add("    ");


        return joiner.toString();
    }

    private String generateAlternative(List<ParserAlternative.Wrapper> sequence, String name, Map<String, Boolean> lexerRules, Map<String, Boolean> parserRules) {
        return "        do {\n" +
                "            " + name + "RuleContext ctx = new " + name + "RuleContext();\n" +
                "            List<" + grammar.getName() + "Lexer.Token> input = tokens;\n" +
                "\n" +
                sequence.stream()
                        .map(w -> "            {\n" + generateConsumer(w, name, lexerRules, parserRules) + "\n            }\n")
                        .collect(Collectors.joining("\n")) +
                "\n" +
                "            if (ctx == null) break;\n" +
                "            ctx.after();\n" +
                "            return ctx;\n" +
                "        } while (false);\n";
    }

    private String generateConsumer(ParserAlternative.Wrapper wrapper, String name, Map<String, Boolean> lexerRules, Map<String, Boolean> parserRules) {
        ParserToken token = wrapper.getToken();
        if (token instanceof LexerRule) {
            return generateLexerConsumer(wrapper, (LexerRule) token, lexerRules, parserRules);
        } else if (token instanceof ParserRule) {
            return generateParserConsumer(wrapper, (ParserRule) token, lexerRules, parserRules);
        } else {
            throw new AssertionError("Inner alternatives are not supported");
        }
    }

    private String generateParserConsumer(ParserAlternative.Wrapper wrapper, ParserRule token, Map<String, Boolean> lexerRules, Map<String, Boolean> parserRules) {
        String name = Character.toUpperCase(token.getName().charAt(0)) + token.getName().substring(1);
        StringJoiner joiner = new StringJoiner("\n");
        // TODO: Support customname, customop
        switch (wrapper.getRepeat()) {
            case ONCE:
                joiner
                        .add("                " + name + "RuleContext token = " + token.getName() + "(input" + (wrapper.getArgs().isEmpty() ? "" : ", " + wrapper.getArgs()) + ");")
                        .add("                " + "if (token == null) {")
                        .add("                " + "    break;")
                        .add("                " + "}")
                        .add("                " + "ctx.addLength(token.length());")
                        .add("                " + "ctx.addText(token.getText());")
                        .add("                " + "ctx." + token.getName() + (parserRules.getOrDefault(token.getName(), false) ? ".add(token);" : " = token;"))
                        .add("                " + "input = input.subList(token.length(), input.size());");
                break;
            case ANY:
                joiner
                        .add("                " + name + "RuleContext token = " + token.getName() + "(input" + (wrapper.getArgs().isEmpty() ? "" : ", " + wrapper.getArgs()) + ");")
                        .add("                " + "while (token != null) {")
                        .add("                " + "    ctx.addLength(token.length());")
                        .add("                " + "    ctx.addText(token.getText());")
                        .add("                " + "    ctx." + token.getName() + ".add(token);")
                        .add("                " + "    input = input.subList(token.length(), input.size());")
                        .add("                " + "    token = " + token.getName() + "(input" + (wrapper.getArgs().isEmpty() ? "" : ", " + wrapper.getArgs()) + ");")
                        .add("                " + "}");
                break;
            case MAYBE:
                joiner
                        .add("                " + name + "RuleContext token = " + token.getName() + "(input" + (wrapper.getArgs().isEmpty() ? "" : ", " + wrapper.getArgs()) + ");")
                        .add("                " + "if (token != null) {")
                        .add("                " + "    ctx.addLength(token.length());")
                        .add("                " + "    ctx.addText(token.getText());")
                        .add("                " + "    ctx." + token.getName() + (parserRules.getOrDefault(token.getName(), false) ? ".add(token);" : " = token;"))
                        .add("                " + "    input = input.subList(token.length(), input.size());")
                        .add("                " + "}");
                break;
            case SOME:
                joiner
                        .add("                " + name + "RuleContext token = " + token.getName() + "(input" + (wrapper.getArgs().isEmpty() ? "" : ", " + wrapper.getArgs()) + ");")
                        .add("                " + "if (token == null) {")
                        .add("                " + "    break;")
                        .add("                " + "}")
                        .add("                " + "ctx.addLength(token.length());")
                        .add("                " + "ctx.addText(token.getText());")
                        .add("                " + "ctx." + token.getName() + ".add(token);")
                        .add("                " + "input = input.subList(token.length(), input.size());")
                        .add("                " + "token = " + token.getName() + "(input" + (wrapper.getArgs().isEmpty() ? "" : ", " + wrapper.getArgs()) + ");")
                        .add("                " + "while (token != null) {")
                        .add("                " + "    ctx.addLength(token.length());")
                        .add("                " + "    ctx.addText(token.getText());")
                        .add("                " + "    ctx." + token.getName() + ".add(token);")
                        .add("                " + "    input = input.subList(token.length(), input.size());")
                        .add("                " + "    token = " + token.getName() + "(input" + (wrapper.getArgs().isEmpty() ? "" : ", " + wrapper.getArgs()) + ");")
                        .add("                " + "}");
                break;
        }
        return joiner.toString();
    }


    private String generateLexerConsumer(ParserAlternative.Wrapper wrapper, LexerRule token, Map<String, Boolean> lexerRules, Map<String, Boolean> parserRules) {
        if (token.getName().equals("_EPS")) return "// Epsilon rule";

        String name = Character.toUpperCase(token.getName().charAt(0)) + token.getName().substring(1);
        StringJoiner joiner = new StringJoiner("\n");
        // TODO: Support customname, customop
        switch (wrapper.getRepeat()) {
            case ONCE:
                joiner
                        .add("                " + "if (input.isEmpty()) break;")
                        .add("                " + grammar.getName() + "Lexer.Token token = input.get(0);")
                        .add("                " + "if (token.getType() != " + grammar.getName() + "Lexer.TokenType." + token.getName().toUpperCase() + ") {")
                        .add("                " + "    break;")
                        .add("                " + "}")
                        .add("                " + "ctx.addText(token.getText());")
                        .add("                " + "ctx.addLength(1);")
                        .add("                " + "ctx." + token.getName() + (lexerRules.getOrDefault(token.getName(), false) ? ".add(token);" : " = token;"))
                        .add("                " + "input = input.subList(1, input.size());");
                break;
            case ANY:
                joiner
                        .add("                " + grammar.getName() + "Lexer.Token token = input.isEmpty() ? null : input.get(0);")
                        .add("                " + "while (!input.isEmpty() && token.getType() == " + grammar.getName() + "Lexer.TokenType." + token.getName().toUpperCase() + ") {")
                        .add("                " + "    ctx.addLength(1);")
                        .add("                " + "    ctx.addText(token.getText());")
                        .add("                " + "    ctx." + token.getName() + ".add(token);")
                        .add("                " + "    input = input.subList(1, input.size());")
                        .add("                " + "    token = input.isEmpty() ? null : input.get(0);")
                        .add("                " + "}");
                break;
            case MAYBE:
                joiner
                        .add("                " + grammar.getName() + "Lexer.Token token = input.isEmpty() ? null : input.get(0);")
                        .add("                " + "if (!input.isEmpty() && token.getType() == " + grammar.getName() + "Lexer.TokenType." + token.getName().toUpperCase() + ") {")
                        .add("                " + "    ctx.addLength(1);")
                        .add("                " + "    ctx.addText(token.getText());")
                        .add("                " + "    ctx." + token.getName() + (lexerRules.getOrDefault(token.getName(), false) ? ".add(token);" : " = token;"))
                        .add("                " + "    input = input.subList(1, input.size());")
                        .add("                " + "}");
                break;
            case SOME:
                joiner
                        .add("                " + "if (input.isEmpty()) break;")
                        .add("                " + grammar.getName() + "Lexer.Token token = input.get(0);")
                        .add("                " + "if (token.getType() != " + grammar.getName() + "Lexer.TokenType." + token.getName().toUpperCase() + ") {")
                        .add("                " + "    break;")
                        .add("                " + "}")
                        .add("                " + "ctx.addLength(1);")
                        .add("                " + "ctx.addText(token.getText());")
                        .add("                " + "    ctx." + token.getName() + ".add(token);")
                        .add("                " + "input = input.subList(1, input.size());")
                        .add("                " + "token = input.get(0);")
                        .add("                " + "while (!input.isEmpty() && token.getType() == " + grammar.getName() + "Lexer.TokenType." + token.getName().toUpperCase() + ") {")
                        .add("                " + "    ctx.addLength(1);")
                        .add("                " + "    ctx.addText(token.getText());")
                        .add("                " + "    ctx." + token.getName() + ".add(token);")
                        .add("                " + "    input = input.subList(1, input.size());")
                        .add("                " + "    token = input.isEmpty() ? null : input.get(0);")
                        .add("                " + "}");
                break;
        }
        return joiner.toString();
    }


    private void countContextFields(ParserAlternative alternative,
                                    Map<String, Boolean> lexerRules,
                                    Map<String, Boolean> parserRules) {
        for (List<ParserAlternative.Wrapper> sequence : alternative.getAlternatives()) {
            for (ParserAlternative.Wrapper wrapper : sequence) {
                ParserToken token = wrapper.getToken();
                Token.Repeat repeat = wrapper.getRepeat();
                boolean repeatMany = repeat == Token.Repeat.ANY || repeat == Token.Repeat.SOME;
                if (token instanceof LexerRule) {
                    lexerRules.compute(
                            ((LexerRule) token).getName(),
                            (String key, Boolean isMany) -> isMany != null || repeatMany);
                } else if (token instanceof ParserRule) {
                    parserRules.compute(
                            ((ParserRule) token).getName(),
                            (String key, Boolean isMany) -> isMany != null || repeatMany);
                    if (((ParserRule) token).getName().startsWith("_flatten_")) {
                        countContextFields(((ParserRule) token).getAlternative(), lexerRules, parserRules);
                    }
                } else if (token instanceof ParserAlternative) {
                    countContextFields((ParserAlternative) token, lexerRules, parserRules);
                } else {
                    throw new AssertionError("Wrong token type");
                }
            }
        }
    }


    private String generateReturnFields(String ret) {
        return ret.isEmpty()
                ? ""
                : Stream.of(ret.split(","))
                .map(String::trim)
                .map(f -> "        public " + f + ";")
                .collect(Collectors.joining("\n"));
    }
}

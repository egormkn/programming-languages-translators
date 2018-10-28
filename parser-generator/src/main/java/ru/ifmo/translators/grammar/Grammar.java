package ru.ifmo.translators.grammar;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import ru.ifmo.translators.GrammarLexer;
import ru.ifmo.translators.GrammarParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {

    final Map<String, Token> dictionary = new HashMap<>();

    final Map<String, LexerRule> lexerRules = new LinkedHashMap<>();
    final Map<String, ParserRule> parserRules = new LinkedHashMap<>();

    private final String name, header, members;

    public Grammar(GrammarParser.GrammarFileContext grammarFile) {
        this.name = grammarFile.name.getText();
        this.header = grammarFile.header == null ? "" : grammarFile.header.getText();
        this.members = grammarFile.members == null ? "" : grammarFile.members.getText();

        for (GrammarParser.GrammarLexerRuleContext rule : grammarFile.lexerRules) {
            LexerRule lexerRule = new LexerRule(rule);
            lexerRules.put(lexerRule.getName(), lexerRule);
        }
        lexerRules.forEach((String key, LexerRule rule) -> rule.bind(this));

        buildDictionary();

        for (GrammarParser.GrammarParserRuleContext rule : grammarFile.parserRules) {
            ParserRule parserRule = new ParserRule(rule);
            parserRules.put(parserRule.getName(), parserRule);
        }
        parserRules.forEach((String key, ParserRule rule) -> rule.bind(this));
    }

    public static Grammar parse(Path path) throws IOException {
        GrammarLexer lexer = new GrammarLexer(CharStreams.fromPath(path));
        GrammarParser parser = new GrammarParser(new CommonTokenStream(lexer));
        GrammarParser.GrammarFileContext grammarFile = parser.grammarFile();
        return new Grammar(grammarFile);
    }

    private void buildDictionary() {
        for (LexerRule rule : lexerRules.values()) {
            List<List<Token>> alternatives = rule.getAlternatives();
            if (alternatives.size() == 1 && alternatives.get(0).size() == 1) {
                Token str = alternatives.get(0).get(0);
                if (str instanceof LexerString) {
                    dictionary.put(((LexerString) str).getString(), rule);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getHeader() {
        return header;
    }

    public String getMembers() {
        return members;
    }

    public Map<String, LexerRule> getLexerRules() {
        return Collections.unmodifiableMap(lexerRules);
    }

    public Map<String, ParserRule> getParserRules() {
        return Collections.unmodifiableMap(parserRules);
    }

    @Override
    public String toString() {
        return new StringJoiner("\n")
                .add("grammar " + name + ";")
                .add("")
                .add("@header {")
                .add(header)
                .add("}")
                .add("")
                .add("@members {")
                .add(members)
                .add("}")
                .add("")
                .add(parserRules.values()
                        .stream()
                        .map(ParserRule::toString)
                        .collect(Collectors.joining("\n\n")))
                .add("")
                .add(lexerRules.values()
                        .stream()
                        .map(LexerRule::toString)
                        .collect(Collectors.joining("\n\n")))
                .toString();
    }
}

package ru.ifmo.translators.grammar;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import ru.ifmo.translators.GrammarLexer;
import ru.ifmo.translators.GrammarParser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {

    @SuppressWarnings("WeakerAccess")
    final Map<String, LexerRule> dictionary = new HashMap<>();
    @SuppressWarnings("WeakerAccess")
    final Map<String, LexerRule> lexerRules = new LinkedHashMap<>();
    @SuppressWarnings("WeakerAccess")
    final Map<String, ParserRule> parserRules = new LinkedHashMap<>();
    private final String header, members;

    public Grammar(GrammarParser.GrammarFileContext grammarFile) {
        this.header = grammarFile.header == null ? "" : grammarFile.header.getText();
        this.members = grammarFile.members == null ? "" : grammarFile.getText();

        for (GrammarParser.GrammarLexerRuleContext rule : grammarFile.lexerRules) {
            String name = rule.name.getText();
            LexerRule lexerRule = new LexerRule(rule);
            lexerRules.put(name, lexerRule);

            if (rule.alternative.sequences.size() == 1
                    && rule.alternative.sequences.get(0).parts.size() == 1) {
                Token str = rule.alternative.sequences.get(0).parts.get(0).token.string;
                if (str != null) dictionary.put(str.getText(), lexerRule);
            }
        }

        for (GrammarParser.GrammarParserRuleContext rule : grammarFile.parserRules) {
            String name = rule.name.getText();
            parserRules.put(name, new ParserRule(rule));
        }

        lexerRules.forEach((String key, LexerRule rule) -> rule.bind(this));
        parserRules.forEach((String key, ParserRule rule) -> rule.bind(this));
    }

    public static Grammar parse(String path) throws IOException {
        GrammarLexer lexer = new GrammarLexer(CharStreams.fromFileName(path));
        GrammarParser parser = new GrammarParser(new CommonTokenStream(lexer));

        GrammarParser.GrammarFileContext grammarFile = parser.grammarFile();

        return new Grammar(grammarFile);
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
                .add("@header {")
                .add(header)
                .add("}")
                .add("")
                .add("@members {")
                .add(members)
                .add("}")
                .add("")
                .add(lexerRules.values()
                        .stream()
                        .map(LexerRule::toString)
                        .collect(Collectors.joining("\n\n")))
                .toString();
    }


}

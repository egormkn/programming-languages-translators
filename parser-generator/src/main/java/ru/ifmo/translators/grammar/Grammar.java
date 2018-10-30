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

    private final LinkedHashMap<String, LexerRule> lexerRules = new LinkedHashMap<>();
    private final LinkedHashMap<String, ParserRule> parserRules = new LinkedHashMap<>();
    private final String name;
    private final String header;
    private final String members;
    private final String footer;
    private final Map<String, LexerRule> dictionary = new HashMap<>();

    public Grammar(GrammarParser.GrammarFileContext grammarFile) {
        this.name = grammarFile.name.getText();
        this.header = grammarFile.header == null ? "" : grammarFile.header.getText();
        this.members = grammarFile.members == null ? "" : grammarFile.members.getText();
        this.footer = grammarFile.footer == null ? "" : grammarFile.footer.getText();

        for (GrammarParser.GrammarLexerRuleContext rule : grammarFile.lexerRules) {
            LexerRule lexerRule = new LexerRule(rule);
            lexerRules.put(lexerRule.getName(), lexerRule);
        }
        lexerRules.forEach((String key, LexerRule rule) -> rule.bind(this));

        lexerRules.put("", LexerRule.EMPTY);

        flattenLexerAlternatives();

        for (GrammarParser.GrammarParserRuleContext rule : grammarFile.parserRules) {
            ParserRule parserRule = new ParserRule(rule);
            parserRules.put(parserRule.getName(), parserRule);
        }
        parserRules.forEach((String key, ParserRule rule) -> rule.bind(this));

        buildDictionary();
    }

    public static Grammar parse(Path path) throws IOException {
        GrammarLexer lexer = new GrammarLexer(CharStreams.fromPath(path));
        GrammarParser parser = new GrammarParser(new CommonTokenStream(lexer));
        GrammarParser.GrammarFileContext grammarFile = parser.grammarFile();
        return new Grammar(grammarFile);
    }

    private void buildDictionary() {
        for (LexerRule rule : lexerRules.values()) {
            List<List<LexerAlternative.Wrapper>> alternatives = rule.getAlternatives();
            if (alternatives.size() == 1 && alternatives.get(0).size() == 1) {
                LexerAlternative.Wrapper wrapper = alternatives.get(0).get(0);
                LexerToken token = wrapper.getToken();
                if (token instanceof LexerString) {
                    dictionary.put(((LexerString) token).getString(), rule);
                }
            }
        }

        for (LexerRule rule : lexerRules.values()) {
            for (List<LexerAlternative.Wrapper> wrappers : rule.getAlternatives()) {
                for (ListIterator<LexerAlternative.Wrapper> iter = wrappers.listIterator(); iter.hasNext(); ) {
                    LexerAlternative.Wrapper wrapper = iter.next();
                    LexerToken token = wrapper.getToken();
                    if (token instanceof LexerString) {
                        String str = ((LexerString) token).getString();
                        LexerRule fromDict = dictionary.get(str);
                        if (fromDict != null && fromDict != rule) {
                            iter.set(new LexerAlternative.Wrapper(fromDict, wrapper.getRepeat()));
                        }
                    }
                }
            }
        }

        for (ParserRule rule : parserRules.values()) {
            for (List<ParserAlternative.Wrapper> wrappers : rule.getAlternatives()) {
                for (ListIterator<ParserAlternative.Wrapper> iter = wrappers.listIterator(); iter.hasNext(); ) {
                    ParserAlternative.Wrapper wrapper = iter.next();
                    ParserToken token = wrapper.getToken();
                    if (token instanceof LexerString) {
                        String str = ((LexerString) token).getString();
                        LexerRule fromDict = dictionary.get(str);
                        if (fromDict != null) {
                            iter.set(new ParserAlternative.Wrapper(
                                    fromDict,
                                    wrapper.getRepeat(),
                                    wrapper.getCode(),
                                    wrapper.getArgs(),
                                    wrapper.getCustomName(),
                                    wrapper.getCustomOp()
                            ));
                        }
                    }
                }
            }
        }
    }

    private void flattenLexerAlternatives() {
        Queue<LexerRule> queue = new ArrayDeque<>(lexerRules.values());
        while (!queue.isEmpty()) {
            LexerRule rule = queue.poll();
            int counter = 0;
            for (List<LexerAlternative.Wrapper> wrappers : rule.getAlternatives()) {
                for (ListIterator<LexerAlternative.Wrapper> iter = wrappers.listIterator(); iter.hasNext(); ) {
                    LexerAlternative.Wrapper wrapper = iter.next();
                    LexerToken token = wrapper.getToken();
                    if (token instanceof LexerAlternative) {
                        String name = "_Flatten" + rule.getName() + String.valueOf(counter++);
                        LexerRule flattened = new LexerRule(name, true, rule.isSkip(), (LexerAlternative) token);
                        lexerRules.put(name, flattened);
                        queue.add(flattened);
                        iter.set(new LexerAlternative.Wrapper(flattened, wrapper.getRepeat()));
                    }
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

    public String getFooter() {
        return footer;
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

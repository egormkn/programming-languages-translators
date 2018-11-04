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

    private Map<String, Set<String>> first = new HashMap<>();
    private Map<String, Set<String>> follow = new HashMap<>();

    private final LinkedHashMap<String, LexerRule> lexerRules = new LinkedHashMap<>();
    private final LinkedHashMap<String, ParserRule> parserRules = new LinkedHashMap<>();
    private final String name;
    private final String header;
    private final String members;
    private final String footer;
    private final Map<String, LexerRule> lexerDictionary = new HashMap<>();
    private final Map<String, LexerRule> parserDictionary = new HashMap<>();

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


        for (GrammarParser.GrammarParserRuleContext rule : grammarFile.parserRules) {
            ParserRule parserRule = new ParserRule(rule);
            parserRules.put(parserRule.getName(), parserRule);
        }
        parserRules.forEach((String key, ParserRule rule) -> rule.bind(this));


        flattenLexerAlternatives();
        flattenParserAlternatives();
        buildDictionary();
        countFirst();
        countFollow();
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
                    lexerDictionary.putIfAbsent(((LexerString) token).getString(), rule);
                    if (!rule.isSkip() && !rule.isFragment()) {
                        parserDictionary.putIfAbsent(((LexerString) token).getString(), rule);
                    }
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
                        LexerRule fromDict = lexerDictionary.get(str);
                        if (fromDict != null && fromDict != rule && fromDict.isSkip() == rule.isSkip()) {
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
                        LexerRule fromDict = parserDictionary.get(str);
                        if (fromDict != null) {
                            iter.set(new ParserAlternative.Wrapper(
                                    fromDict,
                                    wrapper.getRepeat(),
                                    wrapper.getCode(),
                                    wrapper.getArgs(),
                                    wrapper.getCustomName(),
                                    wrapper.getCustomOp()
                            ));
                        } else {
                            System.err.println("Implicit token for string \"" + str + "\"");
                            throw new AssertionError();
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
                        String name = rule.getName() + "_flatten" + String.valueOf(counter++);
                        LexerRule flattened = new LexerRule(name, true, rule.isSkip(), (LexerAlternative) token);
                        lexerRules.put(name, flattened);
                        queue.add(flattened);
                        iter.set(new LexerAlternative.Wrapper(flattened, wrapper.getRepeat()));
                    }
                }
            }
        }
    }

    private void flattenParserAlternatives() {
        Queue<ParserRule> queue = new ArrayDeque<>(parserRules.values());
        while (!queue.isEmpty()) {
            ParserRule rule = queue.poll();
            int counter = 0;
            for (List<ParserAlternative.Wrapper> wrappers : rule.getAlternatives()) {
                for (ListIterator<ParserAlternative.Wrapper> iter = wrappers.listIterator(); iter.hasNext(); ) {
                    ParserAlternative.Wrapper wrapper = iter.next();
                    ParserToken token = wrapper.getToken();
                    if (token instanceof ParserAlternative) {
                        String name = rule.getName() + "_flatten" + String.valueOf(counter++);
                        ParserRule flattened = new ParserRule(name, rule.getArgs(), rule.getRet(), "", "", (ParserAlternative) token);
                        parserRules.put(name, flattened);
                        queue.add(flattened);
                        iter.set(new ParserAlternative.Wrapper(flattened, wrapper.getRepeat(), wrapper.getCode(), rule.getArgs() /* FIXME: _flatten_aaa[String code] -> _flatten_aaa[code] */, wrapper.getCustomName(), wrapper.getCustomOp()));
                    }
                }
            }
        }
    }

    private void countFirst() {
        for (ParserRule rule : parserRules.values()) {
            first.put(rule.getName(), new HashSet<>());
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (ParserRule rule : parserRules.values()) {
                for (List<ParserAlternative.Wrapper> sequence : rule.getAlternatives()) {
                    for (ParserAlternative.Wrapper wrapper : sequence) {
                        ParserToken token = wrapper.getToken();
                        if (token instanceof LexerRule) {
                            changed |= first.get(rule.getName()).add(((LexerRule) token).getName());
                        } else if (token instanceof ParserRule) {
                            changed |= first.get(rule.getName()).addAll(first.get(((ParserRule) token).getName()));
                        } else {
                            throw new AssertionError("Wrong parser rule part");
                        }
                        if (wrapper.getRepeat() == Token.Repeat.SOME || wrapper.getRepeat() == Token.Repeat.ONCE) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void countFollow() {
        for (ParserRule rule : parserRules.values()) {
            follow.put(rule.getName(), new HashSet<>());
        }
        ParserRule firstRule = parserRules.values().iterator().next();
        follow.get(firstRule.getName()).add("_END");

        boolean changed = true;
        while (changed) {
            changed = false;
            for (ParserRule rule : parserRules.values()) {
                for (List<ParserAlternative.Wrapper> sequence : rule.getAlternatives()) {
                    for (int i = 0; i < sequence.size(); i++) {
                        ParserAlternative.Wrapper wrapper = sequence.get(i);
                        ParserToken current = wrapper.getToken();
                        if (current instanceof ParserRule) {
                            if (i < sequence.size() - 1) { // Not last
                                for (int j = i + 1; j < sequence.size(); j++) {
                                    ParserAlternative.Wrapper nextWrapper = sequence.get(j);
                                    ParserToken nextToken = nextWrapper.getToken();
                                    if (nextToken instanceof ParserRule) {
                                        changed |= follow.get(((ParserRule) current).getName()).addAll(first.get(((ParserRule) nextToken).getName()));
                                    } else if (nextToken instanceof LexerRule) {
                                        changed |= follow.get(((ParserRule) current).getName()).add(((LexerRule) nextToken).getName());
                                    }
                                }
                            } else {
                                changed |= follow.get(((ParserRule) current).getName()).addAll(follow.get(rule.getName()));
                            }
                        }
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

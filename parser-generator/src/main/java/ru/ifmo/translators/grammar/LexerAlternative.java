package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LexerAlternative implements LexerToken {

    static final LexerAlternative EMPTY =
            new LexerAlternative(Collections.singletonList(Collections.singletonList(new Wrapper(LexerString.EMPTY, Repeat.ONCE))));

    private final List<List<Wrapper>> alternatives;

    public LexerAlternative(GrammarParser.LexerAlternativeContext alternativeContext, Grammar grammar) {
        alternatives = new ArrayList<>();

        for (GrammarParser.LexerSequenceContext sequenceContext : alternativeContext.sequences) {
            List<Wrapper> wrappers = new LinkedList<>();

            if (sequenceContext.wrappers.size() == 0) {
                wrappers.add(new Wrapper(LexerRule.EMPTY, Repeat.ONCE));
            }

            for (GrammarParser.LexerTokenWrapperContext wrapperContext : sequenceContext.wrappers) {
                GrammarParser.LexerTokenContext tokenContext = wrapperContext.token;

                LexerToken token;

                if (tokenContext == null) {
                    // TODO: Add support for code in lexer tokens
                    System.err.println("Code in lexer tokens is not supported yet");
                    continue;
                }

                if (tokenContext.lexerRuleName != null) {
                    String name = tokenContext.lexerRuleName.getText();
                    token = grammar.getLexerRules().get(name);
                    if (token == null) {
                        throw new AssertionError("Unknown lexer rule \"" + name + "\"");
                    }
                } else if (tokenContext.string != null) {
                    String str = tokenContext.string.getText();
                    token = LexerString.get(str);
                } else if (tokenContext.charset != null) {
                    String charset = tokenContext.charset.getText();
                    token = new LexerCharSet(charset, tokenContext.inverse != null);
                } else if (tokenContext.alternative != null) {
                    token = new LexerAlternative(tokenContext.alternative, grammar);
                } else {
                    throw new AssertionError("Unknown lexer token type");
                }

                Repeat repeat = wrapperContext.repeat == null
                        ? Repeat.ONCE
                        : Repeat.get(wrapperContext.repeat.getText());

                wrappers.add(new Wrapper(token, repeat));
            }
            alternatives.add(wrappers);
        }
    }

    public LexerAlternative(List<List<Wrapper>> alternatives) {
        this.alternatives = alternatives;
    }

    public List<List<Wrapper>> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        Function<List<Wrapper>, String> sequenceToString =
                list -> list.stream().map(Wrapper::toString).collect(Collectors.joining(" "));
        return alternatives.stream().map(sequenceToString).collect(Collectors.joining(" | "));
    }

    public static class Wrapper {

        private final LexerToken token;
        private final LexerToken.Repeat repeat;

        public Wrapper(LexerToken token, Repeat repeat) {
            this.token = token;
            this.repeat = repeat;
        }

        public LexerToken getToken() {
            return token;
        }

        public Repeat getRepeat() {
            return repeat;
        }

        @Override
        public String toString() {
            String tokenString;
            if (token instanceof LexerRule) {
                tokenString = ((LexerRule) token).getName();
            } else if (token instanceof LexerAlternative) {
                tokenString = "(" + token.toString() + ")";
            } else {
                tokenString = token.toString();
            }
            return tokenString + repeat.toString();
        }
    }
}

package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LexerAlternative extends Token {

    private final List<List<Token>> alternatives = new ArrayList<>();

    public LexerAlternative(GrammarParser.LexerAlternativeContext alternative, Grammar grammar) {
        for (GrammarParser.LexerSequenceContext sequence : alternative.sequences) {
            List<Token> tokens = new ArrayList<>();

            for (GrammarParser.LexerSequencePartContext part : sequence.parts) {
                GrammarParser.LexerRuleTokenContext tokenContext = part.token;

                Token token;

                if (tokenContext.lexerRuleName != null) {
                    String name = tokenContext.lexerRuleName.getText();
                    token = grammar.lexerRules.get(name);
                    if (token == null) {
                        throw new AssertionError("Unknown lexer rule \"" + name + "\"");
                    }
                } else if (tokenContext.string != null) {
                    String str = tokenContext.string.getText();
                    token = new LexerString(str);
                } else if (tokenContext.regexp != null) {
                    String pattern = tokenContext.regexp.getText();
                    token = new LexerRegExp(pattern);
                } else if (tokenContext.alternative != null) {
                    token = new LexerAlternative(tokenContext.alternative, grammar);
                } else {
                    throw new AssertionError("Unknown token type in lexer");
                }

                Repeat repeat = part.repeat == null
                        ? Repeat.ONCE
                        : Repeat.get(part.repeat.getText());
                token.setRepeat(repeat);

                tokens.add(token);
            }
            alternatives.add(tokens);
        }
    }

    public List<List<Token>> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        Function<List<Token>, String> seqToString = list -> list.stream().map(token -> {
            if (token instanceof LexerRule) {
                return ((LexerRule) token).getName();
            } else if (token instanceof LexerAlternative) {
                return "(" + token.toString() + ")";
            } else {
                return token.toString();
            }
        }).collect(Collectors.joining(" "));

        return alternatives.stream().map(seqToString).collect(Collectors.joining(" | "));
    }
}

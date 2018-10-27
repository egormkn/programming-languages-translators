package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexerAlternative extends Token implements LexerToken {

    private final List<List<LexerToken>> alternatives = new ArrayList<>();

    public LexerAlternative(GrammarParser.LexerAlternativeContext alternative, Grammar grammar) {
        Map<String, LexerRule> lexerRules = grammar.lexerRules;

        for (GrammarParser.LexerSequenceContext sequence : alternative.sequences) {
            List<LexerToken> tokens = new ArrayList<>();
            for (GrammarParser.LexerSequencePartContext part : sequence.parts) {
                org.antlr.v4.runtime.Token repeatText = part.repeat;
                Repeat repeat = repeatText == null ? Repeat.ONCE : Repeat.get(repeatText.getText());

                LexerToken token;

                org.antlr.v4.runtime.Token lexerRuleName = part.token.lexerRuleName;
                org.antlr.v4.runtime.Token stringText = part.token.string;
                org.antlr.v4.runtime.Token regexp = part.token.regexp;
                GrammarParser.LexerAlternativeContext alt = part.token.alternative;
                if (lexerRuleName != null) {
                    token = lexerRules.get(lexerRuleName.getText());
                    if (token == null) {
                        throw new AssertionError("No token with name \"" + lexerRuleName.getText() + "\"");
                    }
                } else if (stringText != null) {
                    token = grammar.dictionary.get(stringText.getText());
                    if (token == null) {
                        throw new AssertionError("No token for string literal \"" + stringText.getText() + "\"");
                    }
                } else if (regexp != null) {
                    token = new LexerRegExp(regexp.getText());
                } else if (alt.sequences != null) {
                    token = new LexerAlternative(alt, grammar);
                } else {
                    throw new AssertionError("Unknown token type in lexer");
                }
                ((ru.ifmo.translators.grammar.Token) token).setRepeat(repeat);
                tokens.add(token);
            }
            alternatives.add(tokens);
        }
    }

    public List<List<LexerToken>> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        return "str";//alternatives.toString();
    }
}

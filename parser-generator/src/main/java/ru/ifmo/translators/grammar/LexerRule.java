package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.List;

public class LexerRule extends Token {

    private final boolean isFragment, isSkip;
    private final String name;

    private final GrammarParser.LexerAlternativeContext alternativeContext;

    private LexerAlternative alternative;

    public LexerRule(GrammarParser.GrammarLexerRuleContext rule) {
        isFragment = rule.isFragment != null;
        isSkip = rule.isSkip != null;
        name = rule.name.getText();

        alternativeContext = rule.alternative;
        alternative = null;
    }

    public boolean isFragment() {
        return isFragment;
    }

    public boolean isSkip() {
        return isSkip;
    }

    public String getName() {
        return name;
    }

    public LexerAlternative getAlternative() {
        return alternative;
    }

    public List<List<Token>> getAlternatives() {
        return alternative.getAlternatives();
    }

    public void bind(Grammar grammar) {
        alternative = new LexerAlternative(alternativeContext, grammar);
    }

    @Override
    public String toString() {
        return ((isFragment ? "fragment\n" : "") + name + ": ") +
                alternative.toString() + (isSkip ? " -> skip" : "") + ";";
    }
}

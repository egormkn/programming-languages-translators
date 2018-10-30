package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.List;

public class LexerRule implements LexerToken, ParserToken {

    static final LexerRule EMPTY = new LexerRule("_EPS", true, true, LexerAlternative.EMPTY);

    private final boolean isFragment, isSkip;
    private final String name;

    private GrammarParser.LexerAlternativeContext alternativeContext;
    private LexerAlternative alternative;

    public LexerRule(GrammarParser.GrammarLexerRuleContext rule) {
        this.isFragment = rule.isFragment != null;
        this.isSkip = rule.isSkip != null;
        this.name = rule.name.getText();
        this.alternativeContext = rule.alternative;
        this.alternative = null;
    }

    LexerRule(String name, boolean isFragment, boolean isSkip, LexerAlternative alternative) {
        this.isFragment = isFragment;
        this.isSkip = isSkip;
        this.name = name;
        this.alternativeContext = null;
        this.alternative = alternative;
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

    private void setAlternative(LexerAlternative alternative) {
        this.alternative = alternative;
    }

    public List<List<LexerAlternative.Wrapper>> getAlternatives() {
        return alternative.getAlternatives();
    }

    public void bind(Grammar grammar) {
        if (alternativeContext != null) {
            setAlternative(new LexerAlternative(alternativeContext, grammar));
        } else {
            throw new AssertionError("Do not bind the grammar to manually constructed rule");
        }
    }

    @Override
    public String toString() {
        return ((isFragment ? "fragment\n" : "") + name + ": ") +
                alternative.toString() + (isSkip ? " -> skip" : "") + ";";
    }
}

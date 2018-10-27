package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.StringJoiner;

public class LexerRule extends Token implements LexerToken, ParserToken {

    private final GrammarParser.GrammarLexerRuleContext rule;

    private final boolean isFragment, isSkip;
    private final String name;
    private LexerAlternative alternative;

    public LexerRule(GrammarParser.GrammarLexerRuleContext rule) {
        this.rule = rule;
        isFragment = rule.isFragment != null;
        name = rule.name.getText();
        alternative = null;
        isSkip = rule.isSkip != null;
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

    public void bind(Grammar grammar) {
        alternative = new LexerAlternative(rule.alternative, grammar);
    }

    @Override
    public String toString() {
        return new StringJoiner("\n")
                .add(isFragment ? "fragment" : "")
                .add(name + ":")
                .add(alternative.toString() + (isSkip ? " -> skip" : ""))
                .toString();
    }
}

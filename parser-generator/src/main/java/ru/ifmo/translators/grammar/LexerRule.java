package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.StringJoiner;

public class LexerRule extends Token implements LexerToken, ParserToken {

    private final GrammarParser.GrammarLexerRuleContext rule;

    private final boolean isFragment, isSkip;
    private final String name, initCode, afterCode;
    private LexerAlternative alternative;

    public LexerRule(GrammarParser.GrammarLexerRuleContext rule) {
        this.rule = rule;
        isFragment = rule.isFragment != null;
        name = rule.name.getText();
        initCode = rule.init == null ? "" : rule.init.getText();
        afterCode = rule.after == null ? "" : rule.after.getText();
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

    public String getInitCode() {
        return initCode;
    }

    public String getAfterCode() {
        return afterCode;
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
                .add(name)
                .add("@init {")
                .add(initCode)
                .add("}")
                .add("@after {")
                .add(afterCode)
                .add("}")
                .add(":")
                .add(alternative.toString())
                .add(isSkip ? "-> skip" : "")
                .toString();
    }
}

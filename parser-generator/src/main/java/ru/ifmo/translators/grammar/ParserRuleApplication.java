package ru.ifmo.translators.grammar;

public class ParserRuleApplication extends Token {

    private final ParserRule rule;
    private final String args;

    public ParserRuleApplication(ParserRule rule, String args) {
        this.rule = rule;
        this.args = args;
    }

    public ParserRule getRule() {
        return rule;
    }

    public String getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return rule.getName() + "[" + args + "]";
    }
}

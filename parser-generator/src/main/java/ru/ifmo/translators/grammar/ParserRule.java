package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.StringJoiner;

public class ParserRule extends Token implements ParserToken {

    private final GrammarParser.GrammarParserRuleContext rule;
    private final String name, args, ret, initCode, afterCode;
    private ParserAlternative alternative;

    public ParserRule(GrammarParser.GrammarParserRuleContext rule) {
        this.rule = rule;
        name = rule.name.getText();
        args = rule.args == null ? "" : rule.args.getText();
        ret = rule.ret == null ? "" : rule.ret.getText();
        initCode = rule.init == null ? "" : rule.init.getText();
        afterCode = rule.after == null ? "" : rule.after.getText();
        alternative = null;
    }

    public String getName() {
        return name;
    }

    public String getArgs() {
        return args;
    }

    public String getRet() {
        return ret;
    }

    public String getInitCode() {
        return initCode;
    }

    public String getAfterCode() {
        return afterCode;
    }

    public ParserAlternative getAlternative() {
        return alternative;
    }

    public void bind(Grammar grammar) {
        alternative = new ParserAlternative(rule.alternative, grammar);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParserRule.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("args='" + args + "'")
                .add("ret='" + ret + "'")
                .add("initCode='" + initCode + "'")
                .add("afterCode='" + afterCode + "'")
                .add("alternative=" + alternative)
                .toString();
    }
}

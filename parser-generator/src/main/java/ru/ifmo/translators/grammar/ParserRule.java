package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.List;

public class ParserRule implements ParserToken {

    private final String name, args, ret, initCode, afterCode;

    private final GrammarParser.ParserAlternativeContext alternativeContext;

    private ParserAlternative alternative;

    public ParserRule(GrammarParser.GrammarParserRuleContext rule) {
        name = rule.name.getText();
        args = rule.args == null ? "" : rule.args.getText();
        ret = rule.ret == null ? "" : rule.ret.getText();
        initCode = rule.init == null ? "" : rule.init.getText();
        afterCode = rule.after == null ? "" : rule.after.getText();

        alternativeContext = rule.alternative;
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

    public List<List<ParserAlternative.Wrapper>> getAlternatives() {
        return alternative.getAlternatives();
    }

    public void bind(Grammar grammar) {
        alternative = new ParserAlternative(alternativeContext, grammar);
    }

    @Override
    public String toString() {
        return name
                + (args.equals("") ? "" : "[" + args + "]")
                + (ret.equals("") ? "" : " returns [" + ret + "]")
                + (initCode.equals("") ? "" : "\n@init {\n" + initCode + "\n}\n")
                + (afterCode.equals("") ? "" : "\n@init {\n" + afterCode + "\n}\n")
                + ": " + alternative.toString() + ";";
    }
}

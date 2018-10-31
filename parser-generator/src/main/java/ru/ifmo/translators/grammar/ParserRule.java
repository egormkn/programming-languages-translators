package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.List;

public class ParserRule implements ParserToken {

    private final String name, args, ret, initCode, afterCode;

    private final GrammarParser.ParserAlternativeContext alternativeContext;

    private ParserAlternative alternative;

    public ParserRule(GrammarParser.GrammarParserRuleContext rule) {
        this.name = rule.name.getText();
        this.args = rule.args == null ? "" : rule.args.getText();
        this.ret = rule.ret == null ? "" : rule.ret.getText();
        this.initCode = rule.init == null ? "" : rule.init.getText();
        this.afterCode = rule.after == null ? "" : rule.after.getText();

        this.alternativeContext = rule.alternative;
        this.alternative = null;
    }

    public ParserRule(String name, String args, String ret, String initCode, String afterCode, ParserAlternative alternative) {
        this.name = name;
        this.args = args;
        this.ret = ret;
        this.initCode = initCode;
        this.afterCode = afterCode;
        this.alternativeContext = null;
        this.alternative = alternative;
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
        if (alternativeContext != null) {
            alternative = new ParserAlternative(alternativeContext, grammar);
        }
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

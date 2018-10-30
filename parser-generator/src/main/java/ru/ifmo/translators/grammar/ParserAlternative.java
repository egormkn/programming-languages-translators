package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParserAlternative implements ParserToken {

    private final List<List<Wrapper>> alternatives;

    public ParserAlternative(GrammarParser.ParserAlternativeContext alternativeContext, Grammar grammar) {
        alternatives = new ArrayList<>();

        for (GrammarParser.ParserSequenceContext sequenceContext : alternativeContext.sequences) {
            List<Wrapper> wrappers = new LinkedList<>();

            if (sequenceContext.wrappers.size() == 0) {
                wrappers.add(new Wrapper(LexerRule.EMPTY, Repeat.ONCE, ""));
            }

            for (GrammarParser.ParserTokenWrapperContext wrapperContext : sequenceContext.wrappers) {
                GrammarParser.ParserTokenContext tokenContext = wrapperContext.token;

                ParserToken token;

                if (tokenContext == null) {
                    // TODO: Add support for code in parser tokens
                    System.err.println("Code in parser tokens is not supported yet");
                    continue;
                }

                String args = "";

                if (tokenContext.lexerRuleName != null) {
                    String name = tokenContext.lexerRuleName.getText();
                    token = grammar.getLexerRules().get(name);
                    if (token == null) {
                        throw new AssertionError("Unknown lexer rule \"" + name + "\"");
                    }
                } else if (tokenContext.parserRuleName != null) {
                    String name = tokenContext.parserRuleName.getText();
                    token = grammar.getParserRules().get(name);
                    if (token == null) {
                        throw new AssertionError("Unknown parser rule \"" + name + "\"");
                    }
                    if (tokenContext.args != null) {
                        args = tokenContext.args.getText();
                    }
                } else if (tokenContext.string != null) {
                    String str = tokenContext.string.getText();
                    token = LexerString.get(str);
                } else if (tokenContext.alternative != null) {
                    token = new ParserAlternative(tokenContext.alternative, grammar);
                } else {
                    throw new AssertionError("Unknown parser token type");
                }

                Repeat repeat = wrapperContext.repeat == null
                        ? Repeat.ONCE
                        : Repeat.get(wrapperContext.repeat.getText());
                String code = wrapperContext.code == null
                        ? "" : wrapperContext.code.getText();
                String customName = wrapperContext.customName == null
                        ? "" : wrapperContext.customName.getText();
                Operator customOp = wrapperContext.customOp == null
                        ? Operator.NONE : Operator.get(wrapperContext.customOp.getText());

                wrappers.add(new Wrapper(token, repeat, code, args, customName, customOp));
            }
            alternatives.add(wrappers);
        }
    }

    public List<List<Wrapper>> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        Function<List<Wrapper>, String> seqToString =
                list -> list.stream().map(Wrapper::toString).collect(Collectors.joining(" "));
        return alternatives.stream().map(seqToString).collect(Collectors.joining(" | "));
    }

    public static class Wrapper {

        private final ParserToken token;
        private final ParserToken.Repeat repeat;
        private final String code, args, customName;
        private final ParserToken.Operator customOp;

        public Wrapper(ParserToken token, Repeat repeat, String code) {
            this(token, repeat, code, "");
        }

        public Wrapper(ParserToken token, Repeat repeat, String code, String args) {
            this(token, repeat, code, args, "", Operator.NONE);
        }

        public Wrapper(ParserToken token, Repeat repeat, String code, String args, String customName, Operator customOp) {
            this.token = token;
            this.repeat = repeat;
            this.code = code;
            this.args = args;
            this.customName = customName;
            this.customOp = customOp;
        }

        public ParserToken getToken() {
            return token;
        }

        public Repeat getRepeat() {
            return repeat;
        }

        public String getCode() {
            return code;
        }

        public String getCustomName() {
            return customName;
        }

        public Operator getCustomOp() {
            return customOp;
        }

        @Override
        public String toString() {
            String tokenString;
            if (token instanceof LexerRule) {
                tokenString = ((LexerRule) token).getName();
            } else if (token instanceof ParserRule) {
                tokenString = ((ParserRule) token).getName();
            } else if (token instanceof ParserAlternative) {
                tokenString = "(" + token.toString() + ")";
            } else {
                tokenString = token.toString();
            }
            return customName + customOp.toString() + tokenString
                    + (args.isEmpty() ? "" : "[" + args + "]")
                    + repeat.toString()
                    + (!code.isEmpty() ? " {" + code + "}" : "");
        }

        public String getArgs() {
            return args;
        }
    }
}

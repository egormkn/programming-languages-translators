package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParserAlternative extends Token {

    private final List<List<Token>> alternatives = new ArrayList<>();

    public ParserAlternative(GrammarParser.ParserAlternativeContext alternative, Grammar grammar) {
        for (GrammarParser.ParserSequenceContext sequence : alternative.sequences) {
            List<Token> tokens = new ArrayList<>();

            for (GrammarParser.ParserSequencePartContext part : sequence.parts) {
                GrammarParser.ParserRuleTokenContext tokenContext = part.token;

                Token token;

                if (tokenContext.lexerRuleName != null) {
                    String name = tokenContext.lexerRuleName.getText();
                    token = grammar.lexerRules.get(name);
                    if (token == null) {
                        throw new AssertionError("Unknown lexer rule \"" + name + "\"");
                    }
                } else if (tokenContext.parserRuleName != null) {
                    String name = tokenContext.parserRuleName.getText();
                    token = grammar.parserRules.get(name);
                    if (token == null) {
                        throw new AssertionError("Unknown parser rule \"" + name + "\"");
                    }
                    if (tokenContext.args != null) {
                        String args = tokenContext.args.getText();
                        token = new ParserRuleApplication((ParserRule) token, args);
                    }
                } else if (tokenContext.string != null) {
                    String str = tokenContext.string.getText();
                    token = grammar.dictionary.get(str);
                    if (token == null) {
                        System.err.println("Implicit token created for string literal \"" + str + "\"");
                        token = new LexerString(str);
                        grammar.dictionary.put(str, token);
                    }
                } else if (tokenContext.alternative != null) {
                    token = new ParserAlternative(tokenContext.alternative, grammar);
                } else {
                    throw new AssertionError("Unknown token type in parser");
                }

                Repeat repeat = part.repeat == null ? Repeat.ONCE : Repeat.get(part.repeat.getText());
                String customNameText = part.customName == null ? "" : part.customName.getText();
                String customOpText = part.customOp == null ? "" : part.customOp.getText();
                String codeText = part.code == null ? "" : part.code.getText();

                token.setRepeat(repeat);
                token.setCustomName(customNameText);
                token.setCustomOp(customOpText);
                token.setCode(codeText);

                tokens.add(token);
            }
            alternatives.add(tokens);
        }
    }

    public List<List<Token>> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        Function<List<Token>, String> seqToString = list -> list.stream().map(token -> {
            if (token instanceof LexerRule) {
                return ((LexerRule) token).getName();
            } else if (token instanceof ParserRule) {
                return ((ParserRule) token).getName();
            } else if (token instanceof ParserAlternative) {
                return "(" + token.toString() + ")";
            } else {
                return token.toString();
            }
        }).collect(Collectors.joining(" "));

        return alternatives.stream().map(seqToString).collect(Collectors.joining(" | "));
    }
}

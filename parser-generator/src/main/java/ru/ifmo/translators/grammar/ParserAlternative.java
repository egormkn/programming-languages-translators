package ru.ifmo.translators.grammar;

import ru.ifmo.translators.GrammarParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParserAlternative extends Token implements ParserToken {

    private final List<List<ParserToken>> alternatives = new ArrayList<>();

    public ParserAlternative(GrammarParser.ParserAlternativeContext alternative, Grammar grammar) {
        Map<String, LexerRule> lexerRules = grammar.lexerRules;
        Map<String, ParserRule> parserRules = grammar.parserRules;

        for (GrammarParser.ParserSequenceContext sequence : alternative.sequences) {
            List<ParserToken> tokens = new ArrayList<>();
            for (GrammarParser.ParserSequencePartContext part : sequence.parts) {
                ParserToken token = null;

                org.antlr.v4.runtime.Token lexerRuleName = part.token.lexerRuleName;
                org.antlr.v4.runtime.Token parserRuleName = part.token.parserRuleName;

                // TODO: Inherited arguments !!!

                org.antlr.v4.runtime.Token stringText = part.token.string;
                GrammarParser.ParserAlternativeContext alt = part.token.alternative;
                if (lexerRuleName != null) {
                    token = lexerRules.get(lexerRuleName.getText());
                    if (token == null) {
                        throw new AssertionError("No token with name \"" + lexerRuleName.getText() + "\"");
                    }
                } else if (parserRuleName != null) {
                    token = parserRules.get(parserRuleName.getText());
                    if (token == null) {
                        throw new AssertionError("No token with name \"" + parserRuleName.getText() + "\"");
                    }
                } else if (stringText != null) {
                    token = grammar.dictionary.get(stringText.getText());
                    if (token == null) {
                        throw new AssertionError("No token for string literal \"" + stringText.getText() + "\"");
                    }
                } else if (alt.sequences != null) {
                    token = new ParserAlternative(alt, grammar);
                } else {
                    throw new AssertionError("Unknown token type in lexer");
                }
                org.antlr.v4.runtime.Token repeatText = part.repeat;
                Repeat repeat = repeatText == null ? Repeat.ONCE : Repeat.get(repeatText.getText());
                org.antlr.v4.runtime.Token customName = part.customName;
                String customNameText = customName == null ? "" : customName.getText();
                org.antlr.v4.runtime.Token customOp = part.customOp;
                String customOpText = customOp == null ? "" : customOp.getText();
                org.antlr.v4.runtime.Token code = part.code;
                String codeText = code == null ? "" : code.getText();

                ((ru.ifmo.translators.grammar.Token) token)
                        .setRepeat(repeat)
                        .setCustomName(customNameText)
                        .setCustomOp(customOpText)
                        .setCode(codeText);

                tokens.add(token);
            }
            alternatives.add(tokens);
        }
    }

    public List<List<ParserToken>> getAlternatives() {
        return alternatives;
    }
}

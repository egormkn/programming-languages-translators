package ru.ifmo.translators;

import ru.ifmo.translators.grammar.Grammar;
import ru.ifmo.translators.lexer.LexerGenerator;
import ru.ifmo.translators.parser.ParserGenerator;

import java.io.IOException;

public class Generator {

    public static void main(String[] args) {

        try {
            Grammar grammar = Grammar.parse("src/main/resources/Test.g4");
            System.out.println(grammar);

            new LexerGenerator(grammar).generate("src/test/java/TestLexer.java");
            new ParserGenerator(grammar).generate("src/test/java/TestParser.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

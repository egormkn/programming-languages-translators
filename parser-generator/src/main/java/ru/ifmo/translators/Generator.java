package ru.ifmo.translators;

import ru.ifmo.translators.grammar.Grammar;
import ru.ifmo.translators.lexer.LexerGenerator;
import ru.ifmo.translators.parser.ParserGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Generator {

    public static void main(String[] args) {

        if (args.length < 1) {
            throw new IllegalArgumentException("Please specify the grammar file");
        }

        Path grammarFile = Paths.get(args[0]);
        Path outputFolder = Paths.get(args.length > 1 ? args[1] : "");

        try {
            Grammar grammar = Grammar.parse(grammarFile);
            System.err.println(grammar);
            new LexerGenerator(grammar).generate(outputFolder);
            new ParserGenerator(grammar).generate(outputFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

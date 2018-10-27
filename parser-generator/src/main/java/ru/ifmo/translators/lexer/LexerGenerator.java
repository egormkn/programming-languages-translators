package ru.ifmo.translators.lexer;

import ru.ifmo.translators.grammar.Grammar;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LexerGenerator {

    private final Grammar grammar;

    public LexerGenerator(Grammar grammar) {
        this.grammar = grammar;
    }

    public void generate(String path) {
        generate(Paths.get(path));
    }

    public void generate(Path path) {

    }
}

package ru.ifmo.translators.parser;

import ru.ifmo.translators.grammar.Grammar;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ParserGenerator {

    private final Grammar grammar;

    public ParserGenerator(Grammar grammar) {
        this.grammar = grammar;
    }

    public void generate(String path) {
        generate(Paths.get(path));
    }

    public void generate(Path path) {

    }
}

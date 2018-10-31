package ru.ifmo.translators;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LanguageTranslator {

    static <T> String mapToString(List<T> list, Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(Collectors.joining(delim));
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Please specify the program file");
        }

        Path programFile = Paths.get(args[0]);

        try {
            GoodLanguageLexer lexer = new GoodLanguageLexer(CharStreams.fromPath(programFile));
            GoodLanguageParser parser = new GoodLanguageParser(new CommonTokenStream(lexer));
            GoodLanguageParser.ProgramContext program = parser.program();
            System.out.println(program.code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

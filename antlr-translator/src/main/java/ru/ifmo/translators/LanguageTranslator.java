package ru.ifmo.translators;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LanguageTranslator {

    static <T> String mapToString(List<T> list, Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(Collectors.joining(delim));
    }

    public static void main(String[] args) {
//        CharStream input = CharStreams.fromPath(Paths.get(args[0]));
//        ExprLexer lexer = new ExprLexer(input);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        ExprParser parser = new ExprParser(tokens);
//        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
//        try {
//            parser.stat();  // STAGE 1
//        }
//        catch (Exception ex) {
//            tokens.reset(); // rewind input stream
//            parser.reset();
//            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
//            parser.stat();  // STAGE 2
//            // if we parse ok, it's LL not SLL
//        }



        /*CharStream input = CharStreams.fromFileName ("program.pas");
        LanguageLexer lexer = new LanguageLexer (input);
        CommonTokenStream tokens = new CommonTokenStream (lexer);
        pascalParser parser = new pascalParser (tokens);
        parser.setBuildParseTree (false);
        //System.out.println (parser.program ().code);
        System.out.println (">> File parsed <<");

        PrintWriter pw = new PrintWriter ("program.c");
        pw.write (parser.program ().code);
        pw.close ();*/
    }
}

package ru.ifmo.translators;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.IOException;

public class ParserGenerator {

    public static void main(String[] args) {


    }


    public static MyGrammarParser getParser(final String fileName) {
        try {
            CharStream cs = CharStreams.fromFileName(fileName);
            MyGrammarLexer lexer = new MyGrammarLexer(cs);

            TokenStream ts = new CommonTokenStream(lexer);
            return new MyGrammarParser(ts);
        } catch (IOException ioe) {
            System.err.println("Exception occured:");
            System.err.println(ioe.toString());
        }

        return null;
    }
}

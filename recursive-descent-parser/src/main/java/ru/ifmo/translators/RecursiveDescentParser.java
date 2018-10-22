package ru.ifmo.translators;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;

import static ru.ifmo.translators.Token.Type.*;

public class RecursiveDescentParser {

    private LexicalAnalyzer lexer;

    public Tree parse(InputStream is) throws ParseException {
        lexer = new LexicalAnalyzer(is);
        lexer.nextToken();
        Tree root = E();
        if (lexer.currentType() != END) {
            throw new ParseException("Unexpected symbol: "
                        + lexer.currentToken().getChar(), lexer.currentPos());
        }
        return root;
    }

    private Tree E() throws ParseException {
        Tree c = C();
        Tree ePrime = EPrime();
        return new Tree("E", c, ePrime);
    }

    private Tree EPrime() throws ParseException {
        switch (lexer.currentType()) {
            case OR:
                lexer.nextToken();
                Tree c = C();
                Tree ePrime = EPrime();
                return new Tree("E'", new Tree("|"), c, ePrime);
            default:
                return new Tree("E'");
        }
    }

    private Tree C() throws ParseException {
        Tree k = K();
        Tree cPrime = CPrime();
        return new Tree("C", k, cPrime);
    }

    private Tree CPrime() throws ParseException {
        switch (lexer.currentType()) {
            case LPAREN:
            case LETTER:
                Tree k = K();
                Tree cPrime = CPrime();
                return new Tree("C'", k, cPrime);
            default:
                return new Tree("C'");
        }
    }

    private Tree K() throws ParseException {
        Tree t = T();
        Tree kPrime = KPrime();
        return new Tree("K", t, kPrime);
    }

    private Tree KPrime() throws ParseException {
        switch (lexer.currentType()) {
            case PLUS:
                lexer.nextToken();
                return new Tree("K'", new Tree("+"));
            case STAR:
                lexer.nextToken();
                return new Tree("K'", new Tree("*"));
            default:
                return new Tree("K'");
        }
    }

    private Tree T() throws ParseException {
        switch (lexer.currentType()) {
            case LPAREN:
                lexer.nextToken();
                Tree e = E();
                if (lexer.currentType() != RPAREN) {
                    if (lexer.currentType() == END) {
                        throw new ParseException("Unexpected end of line", lexer.currentPos());
                    } else {
                        throw new ParseException("Unexpected symbol: "
                                + lexer.currentToken().getChar(), lexer.currentPos());
                    }
                }
                lexer.nextToken();
                return new Tree("T", e);
            case LETTER:
                char letter = lexer.currentToken().getChar();
                lexer.nextToken();
                return new Tree("T", new Tree(Character.toString(letter)));
            default:
                if (lexer.currentType() == END) {
                    throw new ParseException("Unexpected end of line", lexer.currentPos());
                } else {
                    throw new ParseException("Unexpected symbol: "
                            + lexer.currentToken().getChar(), lexer.currentPos());
                }
        }
    }
}

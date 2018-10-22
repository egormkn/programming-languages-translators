package ru.ifmo.translators;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LexicalAnalyzerTest {

    private static LexicalAnalyzer getLexer(String s) {
        return new LexicalAnalyzer(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testLetters() {
        successTest("abc");
    }

    @Test
    public void testPlus() {
        successTest("+");
    }

    @Test
    public void testStar() {
        successTest("*");
    }

    @Test
    public void testParen() {
        successTest("()");
    }

    @Test
    public void testGroups() {
        successTest("(ab)*(cd)*");
    }

    @Test
    public void testSpaces() {
        successTest("(  a   b\n)*\t\t(c   d)  \r\n*");
    }

    @Test
    public void testLong() {
        successTest("abc*(cde|aaaaaaaa)*");
    }

    @Test
    public void testHard() {
        successTest("((abc*b|a)*ab(aa|b*)b)*");
    }

    @Test
    public void testHarder() {
        successTest("((abc*((abc*b|a)*ab(aa|b*)b)*|a)*((abc*b|a)*ab(aa|b*)b)*b(((abc*b|a)*ab(aa|b*)b)*a|b*)b)*");
    }

    @Test
    public void testDigit() {
        failTest("a1a");
    }

    @Test
    public void testCapital() {
        failTest("aAa");
    }

    private void successTest(String s) {
        assertTrue(test(s));
    }

    private void failTest(String s) {
        assertFalse(test(s));
    }

    private boolean test(String s) {
        LexicalAnalyzer lexer = getLexer(s);
        List<Token> tokens = s.chars()
                .filter(c -> !Character.isWhitespace(c))
                .mapToObj(Token::get)
                .collect(Collectors.toList());
        try {
            lexer.nextToken();
            for (int i = 0; lexer.currentType() != Token.Type.END; i++) {
                if (lexer.currentToken().getChar() != tokens.get(i).getChar()) return false;
                lexer.nextToken();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

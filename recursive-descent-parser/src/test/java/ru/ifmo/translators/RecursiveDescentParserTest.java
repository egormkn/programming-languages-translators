package ru.ifmo.translators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecursiveDescentParserTest {

    private static RecursiveDescentParser p;

    @BeforeAll
    public static void initParser() {
        p = new RecursiveDescentParser();
    }

    @AfterEach
    public void afterEachTest() {
        System.out.println();
    }

    @Test
    public void test() {
        successTest("ab");
    }

    @Test
    public void testBar() {
        successTest("a|b");
    }

    @Test
    public void testEmpty() {
        //failTest("");
    }

    @Test
    public void testStar() {
        successTest("a*b");
    }

    @Test
    public void testParens() {
        successTest("(ab)*");
    }

    @Test
    public void testTwoGroups() {
        failTest("(ab)**(cd)");
    }

    @Test
    public void testTwoGroups2() {
        successTest("(ab)|(cd)");
    }

    @Test
    public void testTwoGroups3() {
        successTest("(ab)(cd)");
    }

    @Test
    public void testLong() {
        successTest("abc*(cde|aaaaaaaa)*");
    }

    @Test
    public void hardTest() {
        successTest("((abc*b|a)*ab(aa|b*)b)*");
    }

    @Test
    public void testPlus() {
        successTest("(abc*b|a)+");
    }

    @Test
    public void testWrongChar() {
        failTest("a1a");
    }

    @Test
    public void testWrongStar() {
        failTest("*");
    }

    @Test
    public void testWrongStar2() {
        failTest("*aaaa");
    }

    @Test
    public void testNotBalancedOne() {
        failTest("(ab))");
    }

    @Test
    public void testNotBalancedTwo() {
        failTest("((ab)");
    }

    @Test
    public void testTwoBars() {
        failTest("a||b");
    }

    @Test
    public void testPrint() {
        try {
            InputStream is = new ByteArrayInputStream("((abc*b|a)*ab(aa|b*)b)*".getBytes(StandardCharsets.UTF_8));
            Tree t = p.parse(is);
            t.visualize();
            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    private void successTest(String s) {
        assertTrue(test(s));
    }

    private void failTest(String s) {
        assertFalse(test(s));
    }

    private boolean test(String s) {
        try {
            InputStream is = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
            Tree t = p.parse(is);
            return true;
        } catch (Exception e) {
            // System.err.println(e.getMessage() + " in " + s);
            // System.err.flush();
            return false;
        }
    }
}

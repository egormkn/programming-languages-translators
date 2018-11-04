
package ru.ifmo.translators.generated;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GoodLanguageParser {


    static <T> String stringify(java.util.List<T> list, java.util.function.Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(java.util.stream.Collectors.joining(delim));
    }

    public static String initTuple(String tuple, String type, String assignment) {
        StringBuilder code = new StringBuilder();
        List<String> vars = Arrays.asList(tuple.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
        vars.stream().map(v -> type + " " + v + ";\n").forEach(code::append);
        if (assignment.indexOf("%SCANF%") > -1) {
            code.append(assignment.replace("%SCANF%", vars.stream().map(v -> "&" + v).collect(Collectors.joining(", "))));
        } else if (assignment.indexOf("%PRINTF%") > -1) {
            code.append(assignment.replace("%PRINTF%", vars.stream().collect(Collectors.joining(", "))));
        } else {
            String updateCode = updateTuple(tuple, type, assignment);
            code.append(updateCode.substring(2, updateCode.length() - 3));
        }
        return code.toString();
    }

    public static String updateTuple(String tuple, String type, String assignment) {
        StringBuilder code = new StringBuilder();
        List<String> vars = Arrays.asList(tuple.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
        if (assignment.indexOf("%SCANF%") > -1) {
            code.append(assignment.replace("%SCANF%", vars.stream().map(v -> "&" + v).collect(Collectors.joining(", "))));
        } else if (assignment.indexOf("%PRINTF%") > -1) {
            code.append(assignment.replace("%PRINTF%", vars.stream().collect(Collectors.joining(", "))));
        } else {
            List<String> assignments = Arrays.asList(assignment.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
            if (vars.size() != assignments.size()) {
                throw new AssertionError("Wrong tuple syntax: " + tuple + " " + assignment);
            }
            for (int i = 0; i < vars.size(); i++) {
                code.append(type + " __TEMP" + vars.get(i) + " = " + assignments.get(i) + ";\n");
            }
            for (int i = 0; i < vars.size(); i++) {
                code.append(vars.get(i) + " = __TEMP" + vars.get(i) + ";\n");
            }
        }
        return "{\n" + code.toString() + "\n}\n";
    }


    public static void main(String[] args) {
        Path inputFile = Paths.get(args[0]);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(inputFile.toFile()));
            List<GoodLanguageLexer.Token> tokens = new GoodLanguageLexer().getTokens(is);
            System.out.println(new GoodLanguageParser().program(tokens).code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static abstract class RuleContext {
        private StringBuilder builder = new StringBuilder();
        
        protected void addText(String s) {
            builder.append(s);
        }
        
        public String getText() {
            return builder.toString();
        }
        
        public void setText(String s) {
            builder = new StringBuilder(s);
        }
        
        private int length = 0;
        
        public void addLength(int n) {
            length += n;
        }
        
        public int length() {
            return length;
        }
    }

    public static class ProgramRuleContext extends RuleContext {
        public ProgramRuleContext() {
            
        }
        public void after() {
            
        ((ProgramRuleContext) this).code = stringify(((ProgramRuleContext) this).unit(), u -> u.code, "\n\n");
    
        }
    
        private List<GoodLanguageLexer.Token> Newline = new ArrayList<>();
        private List<UnitRuleContext> unit = new ArrayList<>();
    
        public List<GoodLanguageLexer.Token> Newline() {
            return Newline;
        }
        
        public List<UnitRuleContext> unit() {
            return unit;
        }
        
    
        public String code;
    }
    
    public ProgramRuleContext program(List<GoodLanguageLexer.Token> tokens) {
        do {
            ProgramRuleContext ctx = new ProgramRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.NEWLINE) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Newline.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            {
                UnitRuleContext token = unit(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.unit.add(token);
                    input = input.subList(token.length(), input.size());
                    token = unit(input);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build program from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class UnitRuleContext extends RuleContext {
        public UnitRuleContext() {
            
        }
        public void after() {
            
        if (((UnitRuleContext) this).declaration() != null) {
            ((UnitRuleContext) this).code = ((UnitRuleContext) this).declaration().code + ";\n";
        } else if (((UnitRuleContext) this).function() != null) {
            ((UnitRuleContext) this).code = ((UnitRuleContext) this).function().code;
        } else if (((UnitRuleContext) this).Directive() != null) {
            ((UnitRuleContext) this).code = ((UnitRuleContext) this).Directive().getText();
        }
    
        }
    
        private List<GoodLanguageLexer.Token> Newline = new ArrayList<>();
        private GoodLanguageLexer.Token Directive;
        private FunctionRuleContext function;
        private DeclarationRuleContext declaration;
    
        public List<GoodLanguageLexer.Token> Newline() {
            return Newline;
        }
        
        public GoodLanguageLexer.Token Directive() {
            return Directive;
        }
        
        public FunctionRuleContext function() {
            return function;
        }
        
        public DeclarationRuleContext declaration() {
            return declaration;
        }
        
    
        public String code;
    }
    
    public UnitRuleContext unit(List<GoodLanguageLexer.Token> tokens) {
        do {
            UnitRuleContext ctx = new UnitRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                DeclarationRuleContext token = declaration(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.declaration = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addLength(1);
                ctx.addText(token.getText());
                    ctx.Newline.add(token);
                input = input.subList(1, input.size());
                token = input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.NEWLINE) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Newline.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnitRuleContext ctx = new UnitRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                FunctionRuleContext token = function(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.function = token;
                input = input.subList(token.length(), input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.NEWLINE) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Newline.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnitRuleContext ctx = new UnitRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DIRECTIVE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Directive = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.NEWLINE) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Newline.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build unit from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class DeclarationRuleContext extends RuleContext {
        public DeclarationRuleContext() {
            
        }
        public void after() {
            
        ((DeclarationRuleContext) this).code = "";
        if (((DeclarationRuleContext) this).lvalue().tuple() != null && ((DeclarationRuleContext) this).initializer().tupleAssignment() != null) {
            ((DeclarationRuleContext) this).code += initTuple(((DeclarationRuleContext) this).lvalue().tuple().code, ((DeclarationRuleContext) this).type().code, ((DeclarationRuleContext) this).initializer().tupleAssignment().code);
        } else if (((DeclarationRuleContext) this).lvalue().tuple() != null || ((DeclarationRuleContext) this).initializer().tupleAssignment() != null) {
            throw new AssertionError("Wrong tuple usage");
        } else if (((DeclarationRuleContext) this).lvalue().Identifier() != null) {
            if (((DeclarationRuleContext) this).Const() != null) {
                ((DeclarationRuleContext) this).code += "const ";
            }
            ((DeclarationRuleContext) this).code += ((DeclarationRuleContext) this).type().code + " " + ((DeclarationRuleContext) this).lvalue().Identifier().getText() + " = " + ((DeclarationRuleContext) this).initializer().code;
        }
    
        }
    
        private GoodLanguageLexer.Token Var;
        private GoodLanguageLexer.Token Const;
        private GoodLanguageLexer.Token Assign;
        private GoodLanguageLexer.Token Colon;
        private LvalueRuleContext lvalue;
        private TypeRuleContext type;
        private InitializerRuleContext initializer;
    
        public GoodLanguageLexer.Token Var() {
            return Var;
        }
        
        public GoodLanguageLexer.Token Const() {
            return Const;
        }
        
        public GoodLanguageLexer.Token Assign() {
            return Assign;
        }
        
        public GoodLanguageLexer.Token Colon() {
            return Colon;
        }
        
        public LvalueRuleContext lvalue() {
            return lvalue;
        }
        
        public TypeRuleContext type() {
            return type;
        }
        
        public InitializerRuleContext initializer() {
            return initializer;
        }
        
    
        public String code;
    }
    
    public DeclarationRuleContext declaration(List<GoodLanguageLexer.Token> tokens) {
        do {
            DeclarationRuleContext ctx = new DeclarationRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.VAR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Var = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                if (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.CONST) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Const = token;
                    input = input.subList(1, input.size());
                }
            }

            {
                LvalueRuleContext token = lvalue(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.lvalue = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon = token;
                input = input.subList(1, input.size());
            }

            {
                TypeRuleContext token = type(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.type = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Assign = token;
                input = input.subList(1, input.size());
            }

            {
                InitializerRuleContext token = initializer(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.initializer = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build declaration from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class FunctionRuleContext extends RuleContext {
        public FunctionRuleContext() {
            
        }
        public void after() {
            
        ((FunctionRuleContext) this).code = ((FunctionRuleContext) this).type().code + " " + ((FunctionRuleContext) this).Identifier().getText() + "(";
        if (((FunctionRuleContext) this).arguments() != null) {
            ((FunctionRuleContext) this).code += ((FunctionRuleContext) this).arguments().code;
        }
        ((FunctionRuleContext) this).code += ") " + ((FunctionRuleContext) this).compoundStatement().code;
    
        }
    
        private GoodLanguageLexer.Token Function;
        private GoodLanguageLexer.Token Identifier;
        private GoodLanguageLexer.Token LeftParen;
        private GoodLanguageLexer.Token Colon;
        private GoodLanguageLexer.Token RightParen;
        private CompoundStatementRuleContext compoundStatement;
        private ArgumentsRuleContext arguments;
        private TypeRuleContext type;
    
        public GoodLanguageLexer.Token Function() {
            return Function;
        }
        
        public GoodLanguageLexer.Token Identifier() {
            return Identifier;
        }
        
        public GoodLanguageLexer.Token LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token Colon() {
            return Colon;
        }
        
        public GoodLanguageLexer.Token RightParen() {
            return RightParen;
        }
        
        public CompoundStatementRuleContext compoundStatement() {
            return compoundStatement;
        }
        
        public ArgumentsRuleContext arguments() {
            return arguments;
        }
        
        public TypeRuleContext type() {
            return type;
        }
        
    
        public String code;
    }
    
    public FunctionRuleContext function(List<GoodLanguageLexer.Token> tokens) {
        do {
            FunctionRuleContext ctx = new FunctionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.FUNCTION) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Function = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen = token;
                input = input.subList(1, input.size());
            }

            {
                ArgumentsRuleContext token = arguments(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.arguments = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon = token;
                input = input.subList(1, input.size());
            }

            {
                TypeRuleContext token = type(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.type = token;
                input = input.subList(token.length(), input.size());
            }

            {
                CompoundStatementRuleContext token = compoundStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.compoundStatement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build function from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class TypeRuleContext extends RuleContext {
        public TypeRuleContext() {
            
        }
        public void after() {
            
        if (((TypeRuleContext) this).Void() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Void().getText();
        }
        if (((TypeRuleContext) this).Char() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Char().getText();
        }
        if (((TypeRuleContext) this).Short() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Short().getText();
        }
        if (((TypeRuleContext) this).Int() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Int().getText();
        }
        if (((TypeRuleContext) this).Long() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Long().getText();
        }
        if (((TypeRuleContext) this).Float() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Float().getText();
        }
        if (((TypeRuleContext) this).Double() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Double().getText();
        }
        if (((TypeRuleContext) this).Signed() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Signed().getText();
        }
        if (((TypeRuleContext) this).Unsigned() != null) {
            ((TypeRuleContext) this).code = ((TypeRuleContext) this).Unsigned().getText();
        }
        if (((TypeRuleContext) this).String() != null) {
            ((TypeRuleContext) this).code = "char*";
        }
        ((TypeRuleContext) this).code += stringify(((TypeRuleContext) this).Star(), s -> s.getText(), "");
    
        }
    
        private GoodLanguageLexer.Token Float;
        private GoodLanguageLexer.Token Unsigned;
        private List<GoodLanguageLexer.Token> Star = new ArrayList<>();
        private GoodLanguageLexer.Token Signed;
        private GoodLanguageLexer.Token Char;
        private GoodLanguageLexer.Token Long;
        private GoodLanguageLexer.Token String;
        private GoodLanguageLexer.Token Void;
        private GoodLanguageLexer.Token Double;
        private GoodLanguageLexer.Token Int;
        private GoodLanguageLexer.Token Short;
    
        public GoodLanguageLexer.Token Float() {
            return Float;
        }
        
        public GoodLanguageLexer.Token Unsigned() {
            return Unsigned;
        }
        
        public List<GoodLanguageLexer.Token> Star() {
            return Star;
        }
        
        public GoodLanguageLexer.Token Signed() {
            return Signed;
        }
        
        public GoodLanguageLexer.Token Char() {
            return Char;
        }
        
        public GoodLanguageLexer.Token Long() {
            return Long;
        }
        
        public GoodLanguageLexer.Token String() {
            return String;
        }
        
        public GoodLanguageLexer.Token Void() {
            return Void;
        }
        
        public GoodLanguageLexer.Token Double() {
            return Double;
        }
        
        public GoodLanguageLexer.Token Int() {
            return Int;
        }
        
        public GoodLanguageLexer.Token Short() {
            return Short;
        }
        
    
        public String code;
    }
    
    public TypeRuleContext type(List<GoodLanguageLexer.Token> tokens) {
        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.VOID) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Void = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.CHAR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Char = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SHORT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Short = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.INT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Int = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LONG) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Long = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.FLOAT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Float = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DOUBLE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Double = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SIGNED) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Signed = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.UNSIGNED) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Unsigned = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TypeRuleContext ctx = new TypeRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STRING) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.String = token;
                input = input.subList(1, input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.STAR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Star.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build type from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class InitializerRuleContext extends RuleContext {
        public InitializerRuleContext() {
            
        }
        public void after() {
            
        if (((InitializerRuleContext) this).assignmentExpression() != null) {
            ((InitializerRuleContext) this).code = ((InitializerRuleContext) this).assignmentExpression().code;
        } else if (((InitializerRuleContext) this).initializer() != null) {
            ((InitializerRuleContext) this).code = "{" + ((InitializerRuleContext) this).initializer().code + stringify(((InitializerRuleContext) this).initializerCont(), i -> i.code, "") + "}";
        }
    
        }
    
        private GoodLanguageLexer.Token LeftBrace;
        private GoodLanguageLexer.Token RightBrace;
        private TupleAssignmentRuleContext tupleAssignment;
        private AssignmentExpressionRuleContext assignmentExpression;
        private List<InitializerContRuleContext> initializerCont = new ArrayList<>();
        private InitializerRuleContext initializer;
    
        public GoodLanguageLexer.Token LeftBrace() {
            return LeftBrace;
        }
        
        public GoodLanguageLexer.Token RightBrace() {
            return RightBrace;
        }
        
        public TupleAssignmentRuleContext tupleAssignment() {
            return tupleAssignment;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
        public List<InitializerContRuleContext> initializerCont() {
            return initializerCont;
        }
        
        public InitializerRuleContext initializer() {
            return initializer;
        }
        
    
        public String code;
    }
    
    public InitializerRuleContext initializer(List<GoodLanguageLexer.Token> tokens) {
        do {
            InitializerRuleContext ctx = new InitializerRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                TupleAssignmentRuleContext token = tupleAssignment(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.tupleAssignment = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            InitializerRuleContext ctx = new InitializerRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTBRACE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftBrace = token;
                input = input.subList(1, input.size());
            }

            {
                InitializerRuleContext token = initializer(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.initializer = token;
                input = input.subList(token.length(), input.size());
            }

            {
                InitializerContRuleContext token = initializerCont(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.initializerCont.add(token);
                    input = input.subList(token.length(), input.size());
                    token = initializerCont(input);
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTBRACE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightBrace = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            InitializerRuleContext ctx = new InitializerRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build initializer from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class InitializerContRuleContext extends RuleContext {
        public InitializerContRuleContext() {
            
        }
        public void after() {
            
        ((InitializerContRuleContext) this).code = ", " + ((InitializerContRuleContext) this).initializer().code;
    
        }
    
        private GoodLanguageLexer.Token Comma;
        private InitializerRuleContext initializer;
    
        public GoodLanguageLexer.Token Comma() {
            return Comma;
        }
        
        public InitializerRuleContext initializer() {
            return initializer;
        }
        
    
        public String code;
    }
    
    public InitializerContRuleContext initializerCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            InitializerContRuleContext ctx = new InitializerContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COMMA) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Comma = token;
                input = input.subList(1, input.size());
            }

            {
                InitializerRuleContext token = initializer(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.initializer = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build initializerCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ArgumentsRuleContext extends RuleContext {
        public ArgumentsRuleContext() {
            
        }
        public void after() {
            
        ((ArgumentsRuleContext) this).code = ((ArgumentsRuleContext) this).argument().code + stringify(((ArgumentsRuleContext) this).argumentCont(), a -> a.code, "");
    
        }
    
        private ArgumentRuleContext argument;
        private List<ArgumentContRuleContext> argumentCont = new ArrayList<>();
    
        public ArgumentRuleContext argument() {
            return argument;
        }
        
        public List<ArgumentContRuleContext> argumentCont() {
            return argumentCont;
        }
        
    
        public String code;
    }
    
    public ArgumentsRuleContext arguments(List<GoodLanguageLexer.Token> tokens) {
        do {
            ArgumentsRuleContext ctx = new ArgumentsRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ArgumentRuleContext token = argument(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.argument = token;
                input = input.subList(token.length(), input.size());
            }

            {
                ArgumentContRuleContext token = argumentCont(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.argumentCont.add(token);
                    input = input.subList(token.length(), input.size());
                    token = argumentCont(input);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build arguments from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ArgumentContRuleContext extends RuleContext {
        public ArgumentContRuleContext() {
            
        }
        public void after() {
            
        ((ArgumentContRuleContext) this).code = ", " + ((ArgumentContRuleContext) this).argument().code;
    
        }
    
        private GoodLanguageLexer.Token Comma;
        private ArgumentRuleContext argument;
    
        public GoodLanguageLexer.Token Comma() {
            return Comma;
        }
        
        public ArgumentRuleContext argument() {
            return argument;
        }
        
    
        public String code;
    }
    
    public ArgumentContRuleContext argumentCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            ArgumentContRuleContext ctx = new ArgumentContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COMMA) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Comma = token;
                input = input.subList(1, input.size());
            }

            {
                ArgumentRuleContext token = argument(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.argument = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build argumentCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ArgumentRuleContext extends RuleContext {
        public ArgumentRuleContext() {
            
        }
        public void after() {
            
        ((ArgumentRuleContext) this).code = ((ArgumentRuleContext) this).type().code + " " + ((ArgumentRuleContext) this).Identifier().getText();
    
        }
    
        private GoodLanguageLexer.Token Identifier;
        private GoodLanguageLexer.Token Colon;
        private TypeRuleContext type;
    
        public GoodLanguageLexer.Token Identifier() {
            return Identifier;
        }
        
        public GoodLanguageLexer.Token Colon() {
            return Colon;
        }
        
        public TypeRuleContext type() {
            return type;
        }
        
    
        public String code;
    }
    
    public ArgumentRuleContext argument(List<GoodLanguageLexer.Token> tokens) {
        do {
            ArgumentRuleContext ctx = new ArgumentRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon = token;
                input = input.subList(1, input.size());
            }

            {
                TypeRuleContext token = type(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.type = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build argument from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class LvalueRuleContext extends RuleContext {
        public LvalueRuleContext() {
            
        }
        public void after() {
            
        if (((LvalueRuleContext) this).tuple() != null) {
            ((LvalueRuleContext) this).code = ((LvalueRuleContext) this).tuple().code;
        } else {
            ((LvalueRuleContext) this).code = ((LvalueRuleContext) this).Identifier().getText();
        }
    
        }
    
        private GoodLanguageLexer.Token Identifier;
        private TupleRuleContext tuple;
    
        public GoodLanguageLexer.Token Identifier() {
            return Identifier;
        }
        
        public TupleRuleContext tuple() {
            return tuple;
        }
        
    
        public String code;
    }
    
    public LvalueRuleContext lvalue(List<GoodLanguageLexer.Token> tokens) {
        do {
            LvalueRuleContext ctx = new LvalueRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                TupleRuleContext token = tuple(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.tuple = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            LvalueRuleContext ctx = new LvalueRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build lvalue from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class TupleRuleContext extends RuleContext {
        public TupleRuleContext() {
            
        }
        public void after() {
            
        ((TupleRuleContext) this).code = "[" + ((TupleRuleContext) this).assignmentExpression().code + stringify(((TupleRuleContext) this).tupleCont(), i -> i.code, "") + "]";
    
        }
    
        private GoodLanguageLexer.Token RightBracket;
        private GoodLanguageLexer.Token LeftBracket;
        private AssignmentExpressionRuleContext assignmentExpression;
        private List<TupleContRuleContext> tupleCont = new ArrayList<>();
    
        public GoodLanguageLexer.Token RightBracket() {
            return RightBracket;
        }
        
        public GoodLanguageLexer.Token LeftBracket() {
            return LeftBracket;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
        public List<TupleContRuleContext> tupleCont() {
            return tupleCont;
        }
        
    
        public String code;
    }
    
    public TupleRuleContext tuple(List<GoodLanguageLexer.Token> tokens) {
        do {
            TupleRuleContext ctx = new TupleRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTBRACKET) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftBracket = token;
                input = input.subList(1, input.size());
            }

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                TupleContRuleContext token = tupleCont(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.tupleCont.add(token);
                    input = input.subList(token.length(), input.size());
                    token = tupleCont(input);
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTBRACKET) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightBracket = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build tuple from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class TupleContRuleContext extends RuleContext {
        public TupleContRuleContext() {
            
        }
        public void after() {
            
        ((TupleContRuleContext) this).code = ", " + ((TupleContRuleContext) this).assignmentExpression().code;
    
        }
    
        private GoodLanguageLexer.Token Comma;
        private AssignmentExpressionRuleContext assignmentExpression;
    
        public GoodLanguageLexer.Token Comma() {
            return Comma;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
    
        public String code;
    }
    
    public TupleContRuleContext tupleCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            TupleContRuleContext ctx = new TupleContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COMMA) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Comma = token;
                input = input.subList(1, input.size());
            }

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build tupleCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class CompoundStatementRuleContext extends RuleContext {
        public CompoundStatementRuleContext() {
            
        }
        public void after() {
            
        ((CompoundStatementRuleContext) this).code = "{\n" + stringify(((CompoundStatementRuleContext) this).item(), i -> i.code, "\n") + "\n}\n";
    
        }
    
        private GoodLanguageLexer.Token LeftBrace;
        private GoodLanguageLexer.Token RightBrace;
        private List<ItemRuleContext> item = new ArrayList<>();
    
        public GoodLanguageLexer.Token LeftBrace() {
            return LeftBrace;
        }
        
        public GoodLanguageLexer.Token RightBrace() {
            return RightBrace;
        }
        
        public List<ItemRuleContext> item() {
            return item;
        }
        
    
        public String code;
    }
    
    public CompoundStatementRuleContext compoundStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            CompoundStatementRuleContext ctx = new CompoundStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTBRACE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftBrace = token;
                input = input.subList(1, input.size());
            }

            {
                ItemRuleContext token = item(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.item.add(token);
                    input = input.subList(token.length(), input.size());
                    token = item(input);
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTBRACE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightBrace = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build compoundStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ItemRuleContext extends RuleContext {
        public ItemRuleContext() {
            
        }
        public void after() {
            
        if (((ItemRuleContext) this).statement() != null) {
            ((ItemRuleContext) this).code = ((ItemRuleContext) this).statement().code;
        } else if (((ItemRuleContext) this).declaration() != null) {
            ((ItemRuleContext) this).code = ((ItemRuleContext) this).declaration().code + ";\n";
        }
    
        }
    
        private List<GoodLanguageLexer.Token> Newline = new ArrayList<>();
        private StatementRuleContext statement;
        private DeclarationRuleContext declaration;
    
        public List<GoodLanguageLexer.Token> Newline() {
            return Newline;
        }
        
        public StatementRuleContext statement() {
            return statement;
        }
        
        public DeclarationRuleContext declaration() {
            return declaration;
        }
        
    
        public String code;
    }
    
    public ItemRuleContext item(List<GoodLanguageLexer.Token> tokens) {
        do {
            ItemRuleContext ctx = new ItemRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                DeclarationRuleContext token = declaration(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.declaration = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addLength(1);
                ctx.addText(token.getText());
                    ctx.Newline.add(token);
                input = input.subList(1, input.size());
                token = input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.NEWLINE) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Newline.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ItemRuleContext ctx = new ItemRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            {
                GoodLanguageLexer.Token token = input.isEmpty() ? null : input.get(0);
                while (!input.isEmpty() && token.getType() == GoodLanguageLexer.TokenType.NEWLINE) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Newline.add(token);
                    input = input.subList(1, input.size());
                    token = input.isEmpty() ? null : input.get(0);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build item from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class StatementRuleContext extends RuleContext {
        public StatementRuleContext() {
            
        }
        public void after() {
            
        if (((StatementRuleContext) this).labeledStatement() != null) {
            ((StatementRuleContext) this).code = ((StatementRuleContext) this).labeledStatement().code;
        } else if (((StatementRuleContext) this).compoundStatement() != null) {
            ((StatementRuleContext) this).code = ((StatementRuleContext) this).compoundStatement().code;
        } else if (((StatementRuleContext) this).expression() != null) {
            ((StatementRuleContext) this).code = ((StatementRuleContext) this).expression().code + ";";
        } else if (((StatementRuleContext) this).selectionStatement() != null) {
            ((StatementRuleContext) this).code = ((StatementRuleContext) this).selectionStatement().code;
        } else if (((StatementRuleContext) this).iterationStatement() != null) {
            ((StatementRuleContext) this).code = ((StatementRuleContext) this).iterationStatement().code;
        } else if (((StatementRuleContext) this).jumpStatement() != null) {
            ((StatementRuleContext) this).code = ((StatementRuleContext) this).jumpStatement().code;
        } else if (((StatementRuleContext) this).tupleExpression() != null) {
            ((StatementRuleContext) this).code = ((StatementRuleContext) this).tupleExpression().code;
        } else {
            ((StatementRuleContext) this).code = "\n";
        }
    
        }
    
        private GoodLanguageLexer.Token Newline;
        private ExpressionRuleContext expression;
        private IterationStatementRuleContext iterationStatement;
        private TupleExpressionRuleContext tupleExpression;
        private CompoundStatementRuleContext compoundStatement;
        private LabeledStatementRuleContext labeledStatement;
        private SelectionStatementRuleContext selectionStatement;
        private JumpStatementRuleContext jumpStatement;
    
        public GoodLanguageLexer.Token Newline() {
            return Newline;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
        public IterationStatementRuleContext iterationStatement() {
            return iterationStatement;
        }
        
        public TupleExpressionRuleContext tupleExpression() {
            return tupleExpression;
        }
        
        public CompoundStatementRuleContext compoundStatement() {
            return compoundStatement;
        }
        
        public LabeledStatementRuleContext labeledStatement() {
            return labeledStatement;
        }
        
        public SelectionStatementRuleContext selectionStatement() {
            return selectionStatement;
        }
        
        public JumpStatementRuleContext jumpStatement() {
            return jumpStatement;
        }
        
    
        public String code;
    }
    
    public StatementRuleContext statement(List<GoodLanguageLexer.Token> tokens) {
        do {
            StatementRuleContext ctx = new StatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                LabeledStatementRuleContext token = labeledStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.labeledStatement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            StatementRuleContext ctx = new StatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                CompoundStatementRuleContext token = compoundStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.compoundStatement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            StatementRuleContext ctx = new StatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                SelectionStatementRuleContext token = selectionStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.selectionStatement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            StatementRuleContext ctx = new StatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                IterationStatementRuleContext token = iterationStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.iterationStatement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            StatementRuleContext ctx = new StatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                JumpStatementRuleContext token = jumpStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.jumpStatement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            StatementRuleContext ctx = new StatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                TupleExpressionRuleContext token = tupleExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.tupleExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            StatementRuleContext ctx = new StatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ExpressionRuleContext token = expression(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.expression = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Newline = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build statement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class LabeledStatementRuleContext extends RuleContext {
        public LabeledStatementRuleContext() {
            
        }
        public void after() {
            
        ((LabeledStatementRuleContext) this).code = ((LabeledStatementRuleContext) this).labeledStatementPrefix().code + ((LabeledStatementRuleContext) this).statement().code;
    
        }
    
        private LabeledStatementPrefixRuleContext labeledStatementPrefix;
        private StatementRuleContext statement;
    
        public LabeledStatementPrefixRuleContext labeledStatementPrefix() {
            return labeledStatementPrefix;
        }
        
        public StatementRuleContext statement() {
            return statement;
        }
        
    
        public String code;
    }
    
    public LabeledStatementRuleContext labeledStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            LabeledStatementRuleContext ctx = new LabeledStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                LabeledStatementPrefixRuleContext token = labeledStatementPrefix(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.labeledStatementPrefix = token;
                input = input.subList(token.length(), input.size());
            }

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build labeledStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class LabeledStatementPrefixRuleContext extends RuleContext {
        public LabeledStatementPrefixRuleContext() {
            
        }
        public void after() {
            
        if (((LabeledStatementPrefixRuleContext) this).Default() != null) {
            ((LabeledStatementPrefixRuleContext) this).code = "default: ";
        } else if (((LabeledStatementPrefixRuleContext) this).Case() != null) {
            ((LabeledStatementPrefixRuleContext) this).code = "case " + ((LabeledStatementPrefixRuleContext) this).conditionalExpression().code + ": ";
        } else if (((LabeledStatementPrefixRuleContext) this).Identifier() != null) {
            ((LabeledStatementPrefixRuleContext) this).code = ((LabeledStatementPrefixRuleContext) this).Identifier().getText() + ": ";
        }
    
        }
    
        private GoodLanguageLexer.Token Identifier;
        private List<GoodLanguageLexer.Token> Colon = new ArrayList<>();
        private GoodLanguageLexer.Token Default;
        private GoodLanguageLexer.Token Case;
        private ConditionalExpressionRuleContext conditionalExpression;
    
        public GoodLanguageLexer.Token Identifier() {
            return Identifier;
        }
        
        public List<GoodLanguageLexer.Token> Colon() {
            return Colon;
        }
        
        public GoodLanguageLexer.Token Default() {
            return Default;
        }
        
        public GoodLanguageLexer.Token Case() {
            return Case;
        }
        
        public ConditionalExpressionRuleContext conditionalExpression() {
            return conditionalExpression;
        }
        
    
        public String code;
    }
    
    public LabeledStatementPrefixRuleContext labeledStatementPrefix(List<GoodLanguageLexer.Token> tokens) {
        do {
            LabeledStatementPrefixRuleContext ctx = new LabeledStatementPrefixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DEFAULT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Default = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            LabeledStatementPrefixRuleContext ctx = new LabeledStatementPrefixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.CASE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Case = token;
                input = input.subList(1, input.size());
            }

            {
                ConditionalExpressionRuleContext token = conditionalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.conditionalExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            LabeledStatementPrefixRuleContext ctx = new LabeledStatementPrefixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build labeledStatementPrefix from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class SelectionStatementRuleContext extends RuleContext {
        public SelectionStatementRuleContext() {
            
        }
        public void after() {
            
        if (((SelectionStatementRuleContext) this).If() != null) {
            ((SelectionStatementRuleContext) this).code = "if " + ((SelectionStatementRuleContext) this).selectionStatementContentIf().code;
            if (((SelectionStatementRuleContext) this).elseStatement() != null) {
                ((SelectionStatementRuleContext) this).code += " " + ((SelectionStatementRuleContext) this).elseStatement().code;
            }
        } else if (((SelectionStatementRuleContext) this).Switch() != null) {
            ((SelectionStatementRuleContext) this).code = "switch " + ((SelectionStatementRuleContext) this).selectionStatementContentSwitch().code;
        }
    
        }
    
        private GoodLanguageLexer.Token Switch;
        private GoodLanguageLexer.Token If;
        private ElseStatementRuleContext elseStatement;
        private SelectionStatementContentSwitchRuleContext selectionStatementContentSwitch;
        private SelectionStatementContentIfRuleContext selectionStatementContentIf;
    
        public GoodLanguageLexer.Token Switch() {
            return Switch;
        }
        
        public GoodLanguageLexer.Token If() {
            return If;
        }
        
        public ElseStatementRuleContext elseStatement() {
            return elseStatement;
        }
        
        public SelectionStatementContentSwitchRuleContext selectionStatementContentSwitch() {
            return selectionStatementContentSwitch;
        }
        
        public SelectionStatementContentIfRuleContext selectionStatementContentIf() {
            return selectionStatementContentIf;
        }
        
    
        public String code;
    }
    
    public SelectionStatementRuleContext selectionStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            SelectionStatementRuleContext ctx = new SelectionStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IF) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.If = token;
                input = input.subList(1, input.size());
            }

            {
                SelectionStatementContentIfRuleContext token = selectionStatementContentIf(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.selectionStatementContentIf = token;
                input = input.subList(token.length(), input.size());
            }

            {
                ElseStatementRuleContext token = elseStatement(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.elseStatement = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            SelectionStatementRuleContext ctx = new SelectionStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SWITCH) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Switch = token;
                input = input.subList(1, input.size());
            }

            {
                SelectionStatementContentSwitchRuleContext token = selectionStatementContentSwitch(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.selectionStatementContentSwitch = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build selectionStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class SelectionStatementContentIfRuleContext extends RuleContext {
        public SelectionStatementContentIfRuleContext() {
            
        }
        public void after() {
            
        ((SelectionStatementContentIfRuleContext) this).code = "(" + ((SelectionStatementContentIfRuleContext) this).expression().code + ") " + ((SelectionStatementContentIfRuleContext) this).statement().code;
    
        }
    
        private GoodLanguageLexer.Token LeftParen;
        private GoodLanguageLexer.Token RightParen;
        private ExpressionRuleContext expression;
        private StatementRuleContext statement;
    
        public GoodLanguageLexer.Token LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token RightParen() {
            return RightParen;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
        public StatementRuleContext statement() {
            return statement;
        }
        
    
        public String code;
    }
    
    public SelectionStatementContentIfRuleContext selectionStatementContentIf(List<GoodLanguageLexer.Token> tokens) {
        do {
            SelectionStatementContentIfRuleContext ctx = new SelectionStatementContentIfRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen = token;
                input = input.subList(1, input.size());
            }

            {
                ExpressionRuleContext token = expression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen = token;
                input = input.subList(1, input.size());
            }

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build selectionStatementContentIf from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class SelectionStatementContentSwitchRuleContext extends RuleContext {
        public SelectionStatementContentSwitchRuleContext() {
            
        }
        public void after() {
            
        ((SelectionStatementContentSwitchRuleContext) this).code = "(" + ((SelectionStatementContentSwitchRuleContext) this).expression().code + ") " + ((SelectionStatementContentSwitchRuleContext) this).statement().code;
    
        }
    
        private GoodLanguageLexer.Token LeftParen;
        private GoodLanguageLexer.Token RightParen;
        private ExpressionRuleContext expression;
        private StatementRuleContext statement;
    
        public GoodLanguageLexer.Token LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token RightParen() {
            return RightParen;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
        public StatementRuleContext statement() {
            return statement;
        }
        
    
        public String code;
    }
    
    public SelectionStatementContentSwitchRuleContext selectionStatementContentSwitch(List<GoodLanguageLexer.Token> tokens) {
        do {
            SelectionStatementContentSwitchRuleContext ctx = new SelectionStatementContentSwitchRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen = token;
                input = input.subList(1, input.size());
            }

            {
                ExpressionRuleContext token = expression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen = token;
                input = input.subList(1, input.size());
            }

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build selectionStatementContentSwitch from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ElseStatementRuleContext extends RuleContext {
        public ElseStatementRuleContext() {
            
        }
        public void after() {
            
        ((ElseStatementRuleContext) this).code = "else " + ((ElseStatementRuleContext) this).statement().code;
    
        }
    
        private GoodLanguageLexer.Token Else;
        private StatementRuleContext statement;
    
        public GoodLanguageLexer.Token Else() {
            return Else;
        }
        
        public StatementRuleContext statement() {
            return statement;
        }
        
    
        public String code;
    }
    
    public ElseStatementRuleContext elseStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            ElseStatementRuleContext ctx = new ElseStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ELSE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Else = token;
                input = input.subList(1, input.size());
            }

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build elseStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class IterationStatementRuleContext extends RuleContext {
        public IterationStatementRuleContext() {
            
        }
        public void after() {
            
        if (((IterationStatementRuleContext) this).Do() != null) {
            ((IterationStatementRuleContext) this).code = "do " + ((IterationStatementRuleContext) this).doStatement().code + " while (" + ((IterationStatementRuleContext) this).doExpression().code + ");\n";
        } else if (((IterationStatementRuleContext) this).For() != null) {
            ((IterationStatementRuleContext) this).code = "for (" + ((IterationStatementRuleContext) this).forCondition().code + ") " + ((IterationStatementRuleContext) this).forStatement().code;
        } else if (((IterationStatementRuleContext) this).While() != null) {
            ((IterationStatementRuleContext) this).code = "while (" + ((IterationStatementRuleContext) this).expression().code + ") " + ((IterationStatementRuleContext) this).statement().code;
        }
    
        }
    
        private GoodLanguageLexer.Token For;
        private List<GoodLanguageLexer.Token> LeftParen = new ArrayList<>();
        private GoodLanguageLexer.Token Newline;
        private List<GoodLanguageLexer.Token> While = new ArrayList<>();
        private GoodLanguageLexer.Token Do;
        private List<GoodLanguageLexer.Token> RightParen = new ArrayList<>();
        private ExpressionRuleContext expression;
        private ForConditionRuleContext forCondition;
        private DoStatementRuleContext doStatement;
        private StatementRuleContext statement;
        private DoExpressionRuleContext doExpression;
        private ForStatementRuleContext forStatement;
    
        public GoodLanguageLexer.Token For() {
            return For;
        }
        
        public List<GoodLanguageLexer.Token> LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token Newline() {
            return Newline;
        }
        
        public List<GoodLanguageLexer.Token> While() {
            return While;
        }
        
        public GoodLanguageLexer.Token Do() {
            return Do;
        }
        
        public List<GoodLanguageLexer.Token> RightParen() {
            return RightParen;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
        public ForConditionRuleContext forCondition() {
            return forCondition;
        }
        
        public DoStatementRuleContext doStatement() {
            return doStatement;
        }
        
        public StatementRuleContext statement() {
            return statement;
        }
        
        public DoExpressionRuleContext doExpression() {
            return doExpression;
        }
        
        public ForStatementRuleContext forStatement() {
            return forStatement;
        }
        
    
        public String code;
    }
    
    public IterationStatementRuleContext iterationStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            IterationStatementRuleContext ctx = new IterationStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DO) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Do = token;
                input = input.subList(1, input.size());
            }

            {
                DoStatementRuleContext token = doStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.doStatement = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.WHILE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.While.add(token);
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                DoExpressionRuleContext token = doExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.doExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Newline = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            IterationStatementRuleContext ctx = new IterationStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.FOR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.For = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                ForConditionRuleContext token = forCondition(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.forCondition = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                ForStatementRuleContext token = forStatement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.forStatement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            IterationStatementRuleContext ctx = new IterationStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.WHILE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.While.add(token);
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                ExpressionRuleContext token = expression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build iterationStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class DoStatementRuleContext extends RuleContext {
        public DoStatementRuleContext() {
            
        }
        public void after() {
            
        ((DoStatementRuleContext) this).code = ((DoStatementRuleContext) this).statement().code;
    
        }
    
        private StatementRuleContext statement;
    
        public StatementRuleContext statement() {
            return statement;
        }
        
    
        public String code;
    }
    
    public DoStatementRuleContext doStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            DoStatementRuleContext ctx = new DoStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build doStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class DoExpressionRuleContext extends RuleContext {
        public DoExpressionRuleContext() {
            
        }
        public void after() {
            
        ((DoExpressionRuleContext) this).code = ((DoExpressionRuleContext) this).expression().code;
    
        }
    
        private ExpressionRuleContext expression;
    
        public ExpressionRuleContext expression() {
            return expression;
        }
        
    
        public String code;
    }
    
    public DoExpressionRuleContext doExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            DoExpressionRuleContext ctx = new DoExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ExpressionRuleContext token = expression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build doExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ForStatementRuleContext extends RuleContext {
        public ForStatementRuleContext() {
            
        }
        public void after() {
            
        ((ForStatementRuleContext) this).code = ((ForStatementRuleContext) this).statement().code;
    
        }
    
        private StatementRuleContext statement;
    
        public StatementRuleContext statement() {
            return statement;
        }
        
    
        public String code;
    }
    
    public ForStatementRuleContext forStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            ForStatementRuleContext ctx = new ForStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                StatementRuleContext token = statement(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.statement = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build forStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ForConditionRuleContext extends RuleContext {
        public ForConditionRuleContext() {
            
        }
        public void after() {
            
        ((ForConditionRuleContext) this).code = ((ForConditionRuleContext) this).forConditionPrefix().code;
        if (((ForConditionRuleContext) this).assignmentExpression() != null) {
            ((ForConditionRuleContext) this).code += ((ForConditionRuleContext) this).assignmentExpression().code;
        }
        ((ForConditionRuleContext) this).code += "; ";
        if (((ForConditionRuleContext) this).assignmentExpression2() != null) {
            ((ForConditionRuleContext) this).code += ((ForConditionRuleContext) this).assignmentExpression2().code;
        }
    
        }
    
        private GoodLanguageLexer.Token Semicolon;
        private ForConditionPrefixRuleContext forConditionPrefix;
        private AssignmentExpressionRuleContext assignmentExpression;
        private AssignmentExpression2RuleContext assignmentExpression2;
    
        public GoodLanguageLexer.Token Semicolon() {
            return Semicolon;
        }
        
        public ForConditionPrefixRuleContext forConditionPrefix() {
            return forConditionPrefix;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
        public AssignmentExpression2RuleContext assignmentExpression2() {
            return assignmentExpression2;
        }
        
    
        public String code;
    }
    
    public ForConditionRuleContext forCondition(List<GoodLanguageLexer.Token> tokens) {
        do {
            ForConditionRuleContext ctx = new ForConditionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ForConditionPrefixRuleContext token = forConditionPrefix(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.forConditionPrefix = token;
                input = input.subList(token.length(), input.size());
            }

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.assignmentExpression = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SEMICOLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Semicolon = token;
                input = input.subList(1, input.size());
            }

            {
                AssignmentExpression2RuleContext token = assignmentExpression2(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.assignmentExpression2 = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build forCondition from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ForConditionPrefixRuleContext extends RuleContext {
        public ForConditionPrefixRuleContext() {
            
        }
        public void after() {
            
        ((ForConditionPrefixRuleContext) this).code = "";
        if (((ForConditionPrefixRuleContext) this).declaration() != null) {
            ((ForConditionPrefixRuleContext) this).code += ((ForConditionPrefixRuleContext) this).declaration().code;
        } else if (((ForConditionPrefixRuleContext) this).expression() != null) {
            ((ForConditionPrefixRuleContext) this).code += ((ForConditionPrefixRuleContext) this).expression().code;
        }
        ((ForConditionPrefixRuleContext) this).code += ";";
    
        }
    
        private List<GoodLanguageLexer.Token> Semicolon = new ArrayList<>();
        private ExpressionRuleContext expression;
        private DeclarationRuleContext declaration;
    
        public List<GoodLanguageLexer.Token> Semicolon() {
            return Semicolon;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
        public DeclarationRuleContext declaration() {
            return declaration;
        }
        
    
        public String code;
    }
    
    public ForConditionPrefixRuleContext forConditionPrefix(List<GoodLanguageLexer.Token> tokens) {
        do {
            ForConditionPrefixRuleContext ctx = new ForConditionPrefixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                DeclarationRuleContext token = declaration(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.declaration = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SEMICOLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Semicolon.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ForConditionPrefixRuleContext ctx = new ForConditionPrefixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ExpressionRuleContext token = expression(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.expression = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SEMICOLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Semicolon.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build forConditionPrefix from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class JumpStatementRuleContext extends RuleContext {
        public JumpStatementRuleContext() {
            
        }
        public void after() {
            
        if (((JumpStatementRuleContext) this).Goto() != null) {
            ((JumpStatementRuleContext) this).code = "goto " + ((JumpStatementRuleContext) this).Identifier().getText() + ";\n";
        } else if (((JumpStatementRuleContext) this).Continue() != null) {
            ((JumpStatementRuleContext) this).code = "continue;\n";
        } else if (((JumpStatementRuleContext) this).Break() != null) {
            ((JumpStatementRuleContext) this).code = "break;\n";
        } else if (((JumpStatementRuleContext) this).Return() != null) {
            if (((JumpStatementRuleContext) this).expression() != null) {
                ((JumpStatementRuleContext) this).code = "return " + ((JumpStatementRuleContext) this).expression().code + ";\n";
            } else {
                ((JumpStatementRuleContext) this).code = "return;\n";
            }
        }
    
        }
    
        private GoodLanguageLexer.Token Goto;
        private GoodLanguageLexer.Token Return;
        private GoodLanguageLexer.Token Identifier;
        private GoodLanguageLexer.Token Break;
        private List<GoodLanguageLexer.Token> Newline = new ArrayList<>();
        private GoodLanguageLexer.Token Continue;
        private ExpressionRuleContext expression;
    
        public GoodLanguageLexer.Token Goto() {
            return Goto;
        }
        
        public GoodLanguageLexer.Token Return() {
            return Return;
        }
        
        public GoodLanguageLexer.Token Identifier() {
            return Identifier;
        }
        
        public GoodLanguageLexer.Token Break() {
            return Break;
        }
        
        public List<GoodLanguageLexer.Token> Newline() {
            return Newline;
        }
        
        public GoodLanguageLexer.Token Continue() {
            return Continue;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
    
        public String code;
    }
    
    public JumpStatementRuleContext jumpStatement(List<GoodLanguageLexer.Token> tokens) {
        do {
            JumpStatementRuleContext ctx = new JumpStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.GOTO) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Goto = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Newline.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            JumpStatementRuleContext ctx = new JumpStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.CONTINUE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Continue = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Newline.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            JumpStatementRuleContext ctx = new JumpStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.BREAK) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Break = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Newline.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            JumpStatementRuleContext ctx = new JumpStatementRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RETURN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Return = token;
                input = input.subList(1, input.size());
            }

            {
                ExpressionRuleContext token = expression(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.expression = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NEWLINE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Newline.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build jumpStatement from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class PrimaryExpressionRuleContext extends RuleContext {
        public PrimaryExpressionRuleContext() {
            
        }
        public void after() {
            
        if (((PrimaryExpressionRuleContext) this).Identifier() != null) {
            ((PrimaryExpressionRuleContext) this).code = ((PrimaryExpressionRuleContext) this).Identifier().getText();
        } else if (((PrimaryExpressionRuleContext) this).Constant() != null) {
            ((PrimaryExpressionRuleContext) this).code = ((PrimaryExpressionRuleContext) this).Constant().getText();
        } else if (((PrimaryExpressionRuleContext) this).StringLiteral() != null) {
            ((PrimaryExpressionRuleContext) this).code = ((PrimaryExpressionRuleContext) this).StringLiteral().getText();
        } else if (((PrimaryExpressionRuleContext) this).expression() != null) {
            ((PrimaryExpressionRuleContext) this).code = "(" + ((PrimaryExpressionRuleContext) this).expression().code + ")";
        }
    
        }
    
        private GoodLanguageLexer.Token Identifier;
        private GoodLanguageLexer.Token StringLiteral;
        private GoodLanguageLexer.Token Constant;
        private GoodLanguageLexer.Token LeftParen;
        private GoodLanguageLexer.Token RightParen;
        private ExpressionRuleContext expression;
    
        public GoodLanguageLexer.Token Identifier() {
            return Identifier;
        }
        
        public GoodLanguageLexer.Token StringLiteral() {
            return StringLiteral;
        }
        
        public GoodLanguageLexer.Token Constant() {
            return Constant;
        }
        
        public GoodLanguageLexer.Token LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token RightParen() {
            return RightParen;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
    
        public String code;
    }
    
    public PrimaryExpressionRuleContext primaryExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            PrimaryExpressionRuleContext ctx = new PrimaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PrimaryExpressionRuleContext ctx = new PrimaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.CONSTANT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Constant = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PrimaryExpressionRuleContext ctx = new PrimaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STRINGLITERAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.StringLiteral = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PrimaryExpressionRuleContext ctx = new PrimaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen = token;
                input = input.subList(1, input.size());
            }

            {
                ExpressionRuleContext token = expression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build primaryExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class PostfixExpressionRuleContext extends RuleContext {
        public PostfixExpressionRuleContext() {
            
        }
        public void after() {
            
        ((PostfixExpressionRuleContext) this).code = ((PostfixExpressionRuleContext) this).primaryExpression().code + stringify(((PostfixExpressionRuleContext) this).postfix(), p -> p.code, "");
    
        }
    
        private PrimaryExpressionRuleContext primaryExpression;
        private List<PostfixRuleContext> postfix = new ArrayList<>();
    
        public PrimaryExpressionRuleContext primaryExpression() {
            return primaryExpression;
        }
        
        public List<PostfixRuleContext> postfix() {
            return postfix;
        }
        
    
        public String code;
    }
    
    public PostfixExpressionRuleContext postfixExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            PostfixExpressionRuleContext ctx = new PostfixExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                PrimaryExpressionRuleContext token = primaryExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.primaryExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                PostfixRuleContext token = postfix(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.postfix.add(token);
                    input = input.subList(token.length(), input.size());
                    token = postfix(input);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build postfixExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class PostfixRuleContext extends RuleContext {
        public PostfixRuleContext() {
            
        }
        public void after() {
            
        if (((PostfixRuleContext) this).expression() != null) {
            ((PostfixRuleContext) this).code = "[" + ((PostfixRuleContext) this).expression().code + "]";
        } else {
            ((PostfixRuleContext) this).code = ((PostfixRuleContext) this).getText();
        }
    
        }
    
        private GoodLanguageLexer.Token Arrow;
        private List<GoodLanguageLexer.Token> Identifier = new ArrayList<>();
        private GoodLanguageLexer.Token RightBracket;
        private GoodLanguageLexer.Token PlusPlus;
        private GoodLanguageLexer.Token LeftBracket;
        private GoodLanguageLexer.Token Dot;
        private GoodLanguageLexer.Token LeftParen;
        private GoodLanguageLexer.Token MinusMinus;
        private GoodLanguageLexer.Token RightParen;
        private ExpressionRuleContext expression;
        private ArgumentExpressionListRuleContext argumentExpressionList;
    
        public GoodLanguageLexer.Token Arrow() {
            return Arrow;
        }
        
        public List<GoodLanguageLexer.Token> Identifier() {
            return Identifier;
        }
        
        public GoodLanguageLexer.Token RightBracket() {
            return RightBracket;
        }
        
        public GoodLanguageLexer.Token PlusPlus() {
            return PlusPlus;
        }
        
        public GoodLanguageLexer.Token LeftBracket() {
            return LeftBracket;
        }
        
        public GoodLanguageLexer.Token Dot() {
            return Dot;
        }
        
        public GoodLanguageLexer.Token LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token MinusMinus() {
            return MinusMinus;
        }
        
        public GoodLanguageLexer.Token RightParen() {
            return RightParen;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
        public ArgumentExpressionListRuleContext argumentExpressionList() {
            return argumentExpressionList;
        }
        
    
        public String code;
    }
    
    public PostfixRuleContext postfix(List<GoodLanguageLexer.Token> tokens) {
        do {
            PostfixRuleContext ctx = new PostfixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTBRACKET) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftBracket = token;
                input = input.subList(1, input.size());
            }

            {
                ExpressionRuleContext token = expression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTBRACKET) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightBracket = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PostfixRuleContext ctx = new PostfixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen = token;
                input = input.subList(1, input.size());
            }

            {
                ArgumentExpressionListRuleContext token = argumentExpressionList(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.argumentExpressionList = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PostfixRuleContext ctx = new PostfixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DOT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Dot = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PostfixRuleContext ctx = new PostfixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ARROW) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Arrow = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.IDENTIFIER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Identifier.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PostfixRuleContext ctx = new PostfixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.PLUSPLUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.PlusPlus = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            PostfixRuleContext ctx = new PostfixRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MINUSMINUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.MinusMinus = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build postfix from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ArgumentExpressionListRuleContext extends RuleContext {
        public ArgumentExpressionListRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<ArgumentExpressionListContRuleContext> argumentExpressionListCont = new ArrayList<>();
        private AssignmentExpressionRuleContext assignmentExpression;
    
        public List<ArgumentExpressionListContRuleContext> argumentExpressionListCont() {
            return argumentExpressionListCont;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
    

    }
    
    public ArgumentExpressionListRuleContext argumentExpressionList(List<GoodLanguageLexer.Token> tokens) {
        do {
            ArgumentExpressionListRuleContext ctx = new ArgumentExpressionListRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                ArgumentExpressionListContRuleContext token = argumentExpressionListCont(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.argumentExpressionListCont.add(token);
                    input = input.subList(token.length(), input.size());
                    token = argumentExpressionListCont(input);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build argumentExpressionList from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ArgumentExpressionListContRuleContext extends RuleContext {
        public ArgumentExpressionListContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private GoodLanguageLexer.Token Comma;
        private AssignmentExpressionRuleContext assignmentExpression;
    
        public GoodLanguageLexer.Token Comma() {
            return Comma;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
    

    }
    
    public ArgumentExpressionListContRuleContext argumentExpressionListCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            ArgumentExpressionListContRuleContext ctx = new ArgumentExpressionListContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COMMA) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Comma = token;
                input = input.subList(1, input.size());
            }

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build argumentExpressionListCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class UnaryExpressionRuleContext extends RuleContext {
        public UnaryExpressionRuleContext() {
            
        }
        public void after() {
            
        ((UnaryExpressionRuleContext) this).code = ((UnaryExpressionRuleContext) this).getText();
    
        }
    
        private GoodLanguageLexer.Token Not;
        private GoodLanguageLexer.Token PlusPlus;
        private GoodLanguageLexer.Token Star;
        private GoodLanguageLexer.Token And;
        private GoodLanguageLexer.Token LeftParen;
        private GoodLanguageLexer.Token Tilde;
        private List<GoodLanguageLexer.Token> Sizeof = new ArrayList<>();
        private GoodLanguageLexer.Token Plus;
        private GoodLanguageLexer.Token RightParen;
        private GoodLanguageLexer.Token Minus;
        private GoodLanguageLexer.Token MinusMinus;
        private List<CastExpressionRuleContext> castExpression = new ArrayList<>();
        private TypeRuleContext type;
        private List<UnaryExpressionRuleContext> unaryExpression = new ArrayList<>();
        private PostfixExpressionRuleContext postfixExpression;
    
        public GoodLanguageLexer.Token Not() {
            return Not;
        }
        
        public GoodLanguageLexer.Token PlusPlus() {
            return PlusPlus;
        }
        
        public GoodLanguageLexer.Token Star() {
            return Star;
        }
        
        public GoodLanguageLexer.Token And() {
            return And;
        }
        
        public GoodLanguageLexer.Token LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token Tilde() {
            return Tilde;
        }
        
        public List<GoodLanguageLexer.Token> Sizeof() {
            return Sizeof;
        }
        
        public GoodLanguageLexer.Token Plus() {
            return Plus;
        }
        
        public GoodLanguageLexer.Token RightParen() {
            return RightParen;
        }
        
        public GoodLanguageLexer.Token Minus() {
            return Minus;
        }
        
        public GoodLanguageLexer.Token MinusMinus() {
            return MinusMinus;
        }
        
        public List<CastExpressionRuleContext> castExpression() {
            return castExpression;
        }
        
        public TypeRuleContext type() {
            return type;
        }
        
        public List<UnaryExpressionRuleContext> unaryExpression() {
            return unaryExpression;
        }
        
        public PostfixExpressionRuleContext postfixExpression() {
            return postfixExpression;
        }
        
    
        public String code;
    }
    
    public UnaryExpressionRuleContext unaryExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                PostfixExpressionRuleContext token = postfixExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.postfixExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.PLUSPLUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.PlusPlus = token;
                input = input.subList(1, input.size());
            }

            {
                UnaryExpressionRuleContext token = unaryExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.unaryExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MINUSMINUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.MinusMinus = token;
                input = input.subList(1, input.size());
            }

            {
                UnaryExpressionRuleContext token = unaryExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.unaryExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.AND) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.And = token;
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STAR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Star = token;
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.PLUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Plus = token;
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MINUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Minus = token;
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.TILDE) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Tilde = token;
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NOT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Not = token;
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SIZEOF) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Sizeof.add(token);
                input = input.subList(1, input.size());
            }

            {
                UnaryExpressionRuleContext token = unaryExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.unaryExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            UnaryExpressionRuleContext ctx = new UnaryExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.SIZEOF) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Sizeof.add(token);
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen = token;
                input = input.subList(1, input.size());
            }

            {
                TypeRuleContext token = type(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.type = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build unaryExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class CastExpressionRuleContext extends RuleContext {
        public CastExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private GoodLanguageLexer.Token LeftParen;
        private GoodLanguageLexer.Token RightParen;
        private CastExpressionRuleContext castExpression;
        private UnaryExpressionRuleContext unaryExpression;
        private TypeRuleContext type;
    
        public GoodLanguageLexer.Token LeftParen() {
            return LeftParen;
        }
        
        public GoodLanguageLexer.Token RightParen() {
            return RightParen;
        }
        
        public CastExpressionRuleContext castExpression() {
            return castExpression;
        }
        
        public UnaryExpressionRuleContext unaryExpression() {
            return unaryExpression;
        }
        
        public TypeRuleContext type() {
            return type;
        }
        
    

    }
    
    public CastExpressionRuleContext castExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            CastExpressionRuleContext ctx = new CastExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen = token;
                input = input.subList(1, input.size());
            }

            {
                TypeRuleContext token = type(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.type = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen = token;
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            CastExpressionRuleContext ctx = new CastExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                UnaryExpressionRuleContext token = unaryExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.unaryExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build castExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class MultiplicativeExpressionRuleContext extends RuleContext {
        public MultiplicativeExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<CastExpressionRuleContext> castExpression = new ArrayList<>();
        private MultiplicativeExpressionContRuleContext multiplicativeExpressionCont;
    
        public List<CastExpressionRuleContext> castExpression() {
            return castExpression;
        }
        
        public MultiplicativeExpressionContRuleContext multiplicativeExpressionCont() {
            return multiplicativeExpressionCont;
        }
        
    

    }
    
    public MultiplicativeExpressionRuleContext multiplicativeExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            MultiplicativeExpressionRuleContext ctx = new MultiplicativeExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                MultiplicativeExpressionContRuleContext token = multiplicativeExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            MultiplicativeExpressionRuleContext ctx = new MultiplicativeExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build multiplicativeExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class MultiplicativeExpressionContRuleContext extends RuleContext {
        public MultiplicativeExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> Div = new ArrayList<>();
        private List<GoodLanguageLexer.Token> Mod = new ArrayList<>();
        private List<GoodLanguageLexer.Token> Star = new ArrayList<>();
        private List<CastExpressionRuleContext> castExpression = new ArrayList<>();
        private List<MultiplicativeExpressionContRuleContext> multiplicativeExpressionCont = new ArrayList<>();
    
        public List<GoodLanguageLexer.Token> Div() {
            return Div;
        }
        
        public List<GoodLanguageLexer.Token> Mod() {
            return Mod;
        }
        
        public List<GoodLanguageLexer.Token> Star() {
            return Star;
        }
        
        public List<CastExpressionRuleContext> castExpression() {
            return castExpression;
        }
        
        public List<MultiplicativeExpressionContRuleContext> multiplicativeExpressionCont() {
            return multiplicativeExpressionCont;
        }
        
    

    }
    
    public MultiplicativeExpressionContRuleContext multiplicativeExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            MultiplicativeExpressionContRuleContext ctx = new MultiplicativeExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STAR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Star.add(token);
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                MultiplicativeExpressionContRuleContext token = multiplicativeExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            MultiplicativeExpressionContRuleContext ctx = new MultiplicativeExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DIV) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Div.add(token);
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                MultiplicativeExpressionContRuleContext token = multiplicativeExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            MultiplicativeExpressionContRuleContext ctx = new MultiplicativeExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MOD) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Mod.add(token);
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                MultiplicativeExpressionContRuleContext token = multiplicativeExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            MultiplicativeExpressionContRuleContext ctx = new MultiplicativeExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STAR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Star.add(token);
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            MultiplicativeExpressionContRuleContext ctx = new MultiplicativeExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DIV) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Div.add(token);
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            MultiplicativeExpressionContRuleContext ctx = new MultiplicativeExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MOD) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Mod.add(token);
                input = input.subList(1, input.size());
            }

            {
                CastExpressionRuleContext token = castExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.castExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build multiplicativeExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class AdditiveExpressionRuleContext extends RuleContext {
        public AdditiveExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private AdditiveExpressionContRuleContext additiveExpressionCont;
        private List<MultiplicativeExpressionRuleContext> multiplicativeExpression = new ArrayList<>();
    
        public AdditiveExpressionContRuleContext additiveExpressionCont() {
            return additiveExpressionCont;
        }
        
        public List<MultiplicativeExpressionRuleContext> multiplicativeExpression() {
            return multiplicativeExpression;
        }
        
    

    }
    
    public AdditiveExpressionRuleContext additiveExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            AdditiveExpressionRuleContext ctx = new AdditiveExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                MultiplicativeExpressionRuleContext token = multiplicativeExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                AdditiveExpressionContRuleContext token = additiveExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AdditiveExpressionRuleContext ctx = new AdditiveExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                MultiplicativeExpressionRuleContext token = multiplicativeExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build additiveExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class AdditiveExpressionContRuleContext extends RuleContext {
        public AdditiveExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> Plus = new ArrayList<>();
        private List<GoodLanguageLexer.Token> Minus = new ArrayList<>();
        private List<AdditiveExpressionContRuleContext> additiveExpressionCont = new ArrayList<>();
        private List<MultiplicativeExpressionRuleContext> multiplicativeExpression = new ArrayList<>();
    
        public List<GoodLanguageLexer.Token> Plus() {
            return Plus;
        }
        
        public List<GoodLanguageLexer.Token> Minus() {
            return Minus;
        }
        
        public List<AdditiveExpressionContRuleContext> additiveExpressionCont() {
            return additiveExpressionCont;
        }
        
        public List<MultiplicativeExpressionRuleContext> multiplicativeExpression() {
            return multiplicativeExpression;
        }
        
    

    }
    
    public AdditiveExpressionContRuleContext additiveExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            AdditiveExpressionContRuleContext ctx = new AdditiveExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.PLUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Plus.add(token);
                input = input.subList(1, input.size());
            }

            {
                MultiplicativeExpressionRuleContext token = multiplicativeExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                AdditiveExpressionContRuleContext token = additiveExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AdditiveExpressionContRuleContext ctx = new AdditiveExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MINUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Minus.add(token);
                input = input.subList(1, input.size());
            }

            {
                MultiplicativeExpressionRuleContext token = multiplicativeExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                AdditiveExpressionContRuleContext token = additiveExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AdditiveExpressionContRuleContext ctx = new AdditiveExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.PLUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Plus.add(token);
                input = input.subList(1, input.size());
            }

            {
                MultiplicativeExpressionRuleContext token = multiplicativeExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AdditiveExpressionContRuleContext ctx = new AdditiveExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MINUS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Minus.add(token);
                input = input.subList(1, input.size());
            }

            {
                MultiplicativeExpressionRuleContext token = multiplicativeExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.multiplicativeExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build additiveExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ShiftExpressionRuleContext extends RuleContext {
        public ShiftExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<AdditiveExpressionRuleContext> additiveExpression = new ArrayList<>();
        private ShiftExpressionContRuleContext shiftExpressionCont;
    
        public List<AdditiveExpressionRuleContext> additiveExpression() {
            return additiveExpression;
        }
        
        public ShiftExpressionContRuleContext shiftExpressionCont() {
            return shiftExpressionCont;
        }
        
    

    }
    
    public ShiftExpressionRuleContext shiftExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            ShiftExpressionRuleContext ctx = new ShiftExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                AdditiveExpressionRuleContext token = additiveExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                ShiftExpressionContRuleContext token = shiftExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ShiftExpressionRuleContext ctx = new ShiftExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                AdditiveExpressionRuleContext token = additiveExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build shiftExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ShiftExpressionContRuleContext extends RuleContext {
        public ShiftExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> RightShift = new ArrayList<>();
        private List<GoodLanguageLexer.Token> LeftShift = new ArrayList<>();
        private List<AdditiveExpressionRuleContext> additiveExpression = new ArrayList<>();
        private List<ShiftExpressionContRuleContext> shiftExpressionCont = new ArrayList<>();
    
        public List<GoodLanguageLexer.Token> RightShift() {
            return RightShift;
        }
        
        public List<GoodLanguageLexer.Token> LeftShift() {
            return LeftShift;
        }
        
        public List<AdditiveExpressionRuleContext> additiveExpression() {
            return additiveExpression;
        }
        
        public List<ShiftExpressionContRuleContext> shiftExpressionCont() {
            return shiftExpressionCont;
        }
        
    

    }
    
    public ShiftExpressionContRuleContext shiftExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            ShiftExpressionContRuleContext ctx = new ShiftExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTSHIFT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftShift.add(token);
                input = input.subList(1, input.size());
            }

            {
                AdditiveExpressionRuleContext token = additiveExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                ShiftExpressionContRuleContext token = shiftExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ShiftExpressionContRuleContext ctx = new ShiftExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTSHIFT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightShift.add(token);
                input = input.subList(1, input.size());
            }

            {
                AdditiveExpressionRuleContext token = additiveExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                ShiftExpressionContRuleContext token = shiftExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ShiftExpressionContRuleContext ctx = new ShiftExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTSHIFT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftShift.add(token);
                input = input.subList(1, input.size());
            }

            {
                AdditiveExpressionRuleContext token = additiveExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ShiftExpressionContRuleContext ctx = new ShiftExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTSHIFT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightShift.add(token);
                input = input.subList(1, input.size());
            }

            {
                AdditiveExpressionRuleContext token = additiveExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.additiveExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build shiftExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class RelationalExpressionRuleContext extends RuleContext {
        public RelationalExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private RelationalExpressionContRuleContext relationalExpressionCont;
        private List<ShiftExpressionRuleContext> shiftExpression = new ArrayList<>();
    
        public RelationalExpressionContRuleContext relationalExpressionCont() {
            return relationalExpressionCont;
        }
        
        public List<ShiftExpressionRuleContext> shiftExpression() {
            return shiftExpression;
        }
        
    

    }
    
    public RelationalExpressionRuleContext relationalExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            RelationalExpressionRuleContext ctx = new RelationalExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                RelationalExpressionContRuleContext token = relationalExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionRuleContext ctx = new RelationalExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build relationalExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class RelationalExpressionContRuleContext extends RuleContext {
        public RelationalExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> GreaterEqual = new ArrayList<>();
        private List<GoodLanguageLexer.Token> LessEqual = new ArrayList<>();
        private List<GoodLanguageLexer.Token> Greater = new ArrayList<>();
        private List<GoodLanguageLexer.Token> Less = new ArrayList<>();
        private List<RelationalExpressionContRuleContext> relationalExpressionCont = new ArrayList<>();
        private List<ShiftExpressionRuleContext> shiftExpression = new ArrayList<>();
    
        public List<GoodLanguageLexer.Token> GreaterEqual() {
            return GreaterEqual;
        }
        
        public List<GoodLanguageLexer.Token> LessEqual() {
            return LessEqual;
        }
        
        public List<GoodLanguageLexer.Token> Greater() {
            return Greater;
        }
        
        public List<GoodLanguageLexer.Token> Less() {
            return Less;
        }
        
        public List<RelationalExpressionContRuleContext> relationalExpressionCont() {
            return relationalExpressionCont;
        }
        
        public List<ShiftExpressionRuleContext> shiftExpression() {
            return shiftExpression;
        }
        
    

    }
    
    public RelationalExpressionContRuleContext relationalExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LESSEQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LessEqual.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                RelationalExpressionContRuleContext token = relationalExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.GREATEREQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.GreaterEqual.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                RelationalExpressionContRuleContext token = relationalExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LESS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Less.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                RelationalExpressionContRuleContext token = relationalExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.GREATER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Greater.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                RelationalExpressionContRuleContext token = relationalExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LESS) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Less.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.GREATER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Greater.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LESSEQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LessEqual.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            RelationalExpressionContRuleContext ctx = new RelationalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.GREATEREQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.GreaterEqual.add(token);
                input = input.subList(1, input.size());
            }

            {
                ShiftExpressionRuleContext token = shiftExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.shiftExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build relationalExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class EqualityExpressionRuleContext extends RuleContext {
        public EqualityExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<RelationalExpressionRuleContext> relationalExpression = new ArrayList<>();
        private EqualityExpressionContRuleContext equalityExpressionCont;
    
        public List<RelationalExpressionRuleContext> relationalExpression() {
            return relationalExpression;
        }
        
        public EqualityExpressionContRuleContext equalityExpressionCont() {
            return equalityExpressionCont;
        }
        
    

    }
    
    public EqualityExpressionRuleContext equalityExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            EqualityExpressionRuleContext ctx = new EqualityExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                RelationalExpressionRuleContext token = relationalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                EqualityExpressionContRuleContext token = equalityExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.equalityExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            EqualityExpressionRuleContext ctx = new EqualityExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                RelationalExpressionRuleContext token = relationalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build equalityExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class EqualityExpressionContRuleContext extends RuleContext {
        public EqualityExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> NotEqual = new ArrayList<>();
        private List<GoodLanguageLexer.Token> Equal = new ArrayList<>();
        private List<RelationalExpressionRuleContext> relationalExpression = new ArrayList<>();
        private List<EqualityExpressionContRuleContext> equalityExpressionCont = new ArrayList<>();
    
        public List<GoodLanguageLexer.Token> NotEqual() {
            return NotEqual;
        }
        
        public List<GoodLanguageLexer.Token> Equal() {
            return Equal;
        }
        
        public List<RelationalExpressionRuleContext> relationalExpression() {
            return relationalExpression;
        }
        
        public List<EqualityExpressionContRuleContext> equalityExpressionCont() {
            return equalityExpressionCont;
        }
        
    

    }
    
    public EqualityExpressionContRuleContext equalityExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            EqualityExpressionContRuleContext ctx = new EqualityExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.EQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Equal.add(token);
                input = input.subList(1, input.size());
            }

            {
                RelationalExpressionRuleContext token = relationalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                EqualityExpressionContRuleContext token = equalityExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.equalityExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            EqualityExpressionContRuleContext ctx = new EqualityExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NOTEQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.NotEqual.add(token);
                input = input.subList(1, input.size());
            }

            {
                RelationalExpressionRuleContext token = relationalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                EqualityExpressionContRuleContext token = equalityExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.equalityExpressionCont.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            EqualityExpressionContRuleContext ctx = new EqualityExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.EQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Equal.add(token);
                input = input.subList(1, input.size());
            }

            {
                RelationalExpressionRuleContext token = relationalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            EqualityExpressionContRuleContext ctx = new EqualityExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.NOTEQUAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.NotEqual.add(token);
                input = input.subList(1, input.size());
            }

            {
                RelationalExpressionRuleContext token = relationalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.relationalExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build equalityExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class AndExpressionRuleContext extends RuleContext {
        public AndExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private AndExpressionContRuleContext andExpressionCont;
        private List<EqualityExpressionRuleContext> equalityExpression = new ArrayList<>();
    
        public AndExpressionContRuleContext andExpressionCont() {
            return andExpressionCont;
        }
        
        public List<EqualityExpressionRuleContext> equalityExpression() {
            return equalityExpression;
        }
        
    

    }
    
    public AndExpressionRuleContext andExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            AndExpressionRuleContext ctx = new AndExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                EqualityExpressionRuleContext token = equalityExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.equalityExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                AndExpressionContRuleContext token = andExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.andExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AndExpressionRuleContext ctx = new AndExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                EqualityExpressionRuleContext token = equalityExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.equalityExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build andExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class AndExpressionContRuleContext extends RuleContext {
        public AndExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> And = new ArrayList<>();
        private AndExpressionContRuleContext andExpressionCont;
        private List<EqualityExpressionRuleContext> equalityExpression = new ArrayList<>();
    
        public List<GoodLanguageLexer.Token> And() {
            return And;
        }
        
        public AndExpressionContRuleContext andExpressionCont() {
            return andExpressionCont;
        }
        
        public List<EqualityExpressionRuleContext> equalityExpression() {
            return equalityExpression;
        }
        
    

    }
    
    public AndExpressionContRuleContext andExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            AndExpressionContRuleContext ctx = new AndExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.AND) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.And.add(token);
                input = input.subList(1, input.size());
            }

            {
                EqualityExpressionRuleContext token = equalityExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.equalityExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                AndExpressionContRuleContext token = andExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.andExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AndExpressionContRuleContext ctx = new AndExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.AND) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.And.add(token);
                input = input.subList(1, input.size());
            }

            {
                EqualityExpressionRuleContext token = equalityExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.equalityExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build andExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ExclusiveOrExpressionRuleContext extends RuleContext {
        public ExclusiveOrExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<AndExpressionRuleContext> andExpression = new ArrayList<>();
        private ExclusiveOrExpressionContRuleContext exclusiveOrExpressionCont;
    
        public List<AndExpressionRuleContext> andExpression() {
            return andExpression;
        }
        
        public ExclusiveOrExpressionContRuleContext exclusiveOrExpressionCont() {
            return exclusiveOrExpressionCont;
        }
        
    

    }
    
    public ExclusiveOrExpressionRuleContext exclusiveOrExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            ExclusiveOrExpressionRuleContext ctx = new ExclusiveOrExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                AndExpressionRuleContext token = andExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.andExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                ExclusiveOrExpressionContRuleContext token = exclusiveOrExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.exclusiveOrExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ExclusiveOrExpressionRuleContext ctx = new ExclusiveOrExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                AndExpressionRuleContext token = andExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.andExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build exclusiveOrExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ExclusiveOrExpressionContRuleContext extends RuleContext {
        public ExclusiveOrExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> Caret = new ArrayList<>();
        private List<AndExpressionRuleContext> andExpression = new ArrayList<>();
        private ExclusiveOrExpressionContRuleContext exclusiveOrExpressionCont;
    
        public List<GoodLanguageLexer.Token> Caret() {
            return Caret;
        }
        
        public List<AndExpressionRuleContext> andExpression() {
            return andExpression;
        }
        
        public ExclusiveOrExpressionContRuleContext exclusiveOrExpressionCont() {
            return exclusiveOrExpressionCont;
        }
        
    

    }
    
    public ExclusiveOrExpressionContRuleContext exclusiveOrExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            ExclusiveOrExpressionContRuleContext ctx = new ExclusiveOrExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.CARET) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Caret.add(token);
                input = input.subList(1, input.size());
            }

            {
                AndExpressionRuleContext token = andExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.andExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                ExclusiveOrExpressionContRuleContext token = exclusiveOrExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.exclusiveOrExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ExclusiveOrExpressionContRuleContext ctx = new ExclusiveOrExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.CARET) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Caret.add(token);
                input = input.subList(1, input.size());
            }

            {
                AndExpressionRuleContext token = andExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.andExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build exclusiveOrExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class InclusiveOrExpressionRuleContext extends RuleContext {
        public InclusiveOrExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<ExclusiveOrExpressionRuleContext> exclusiveOrExpression = new ArrayList<>();
        private InclusiveOrExpressionContRuleContext inclusiveOrExpressionCont;
    
        public List<ExclusiveOrExpressionRuleContext> exclusiveOrExpression() {
            return exclusiveOrExpression;
        }
        
        public InclusiveOrExpressionContRuleContext inclusiveOrExpressionCont() {
            return inclusiveOrExpressionCont;
        }
        
    

    }
    
    public InclusiveOrExpressionRuleContext inclusiveOrExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            InclusiveOrExpressionRuleContext ctx = new InclusiveOrExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ExclusiveOrExpressionRuleContext token = exclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.exclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                InclusiveOrExpressionContRuleContext token = inclusiveOrExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.inclusiveOrExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            InclusiveOrExpressionRuleContext ctx = new InclusiveOrExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ExclusiveOrExpressionRuleContext token = exclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.exclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build inclusiveOrExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class InclusiveOrExpressionContRuleContext extends RuleContext {
        public InclusiveOrExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> Or = new ArrayList<>();
        private List<ExclusiveOrExpressionRuleContext> exclusiveOrExpression = new ArrayList<>();
        private InclusiveOrExpressionContRuleContext inclusiveOrExpressionCont;
    
        public List<GoodLanguageLexer.Token> Or() {
            return Or;
        }
        
        public List<ExclusiveOrExpressionRuleContext> exclusiveOrExpression() {
            return exclusiveOrExpression;
        }
        
        public InclusiveOrExpressionContRuleContext inclusiveOrExpressionCont() {
            return inclusiveOrExpressionCont;
        }
        
    

    }
    
    public InclusiveOrExpressionContRuleContext inclusiveOrExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            InclusiveOrExpressionContRuleContext ctx = new InclusiveOrExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.OR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Or.add(token);
                input = input.subList(1, input.size());
            }

            {
                ExclusiveOrExpressionRuleContext token = exclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.exclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                InclusiveOrExpressionContRuleContext token = inclusiveOrExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.inclusiveOrExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            InclusiveOrExpressionContRuleContext ctx = new InclusiveOrExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.OR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Or.add(token);
                input = input.subList(1, input.size());
            }

            {
                ExclusiveOrExpressionRuleContext token = exclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.exclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build inclusiveOrExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class LogicalAndExpressionRuleContext extends RuleContext {
        public LogicalAndExpressionRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<InclusiveOrExpressionRuleContext> inclusiveOrExpression = new ArrayList<>();
        private LogicalAndExpressionContRuleContext logicalAndExpressionCont;
    
        public List<InclusiveOrExpressionRuleContext> inclusiveOrExpression() {
            return inclusiveOrExpression;
        }
        
        public LogicalAndExpressionContRuleContext logicalAndExpressionCont() {
            return logicalAndExpressionCont;
        }
        
    

    }
    
    public LogicalAndExpressionRuleContext logicalAndExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            LogicalAndExpressionRuleContext ctx = new LogicalAndExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                InclusiveOrExpressionRuleContext token = inclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.inclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                LogicalAndExpressionContRuleContext token = logicalAndExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalAndExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            LogicalAndExpressionRuleContext ctx = new LogicalAndExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                InclusiveOrExpressionRuleContext token = inclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.inclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build logicalAndExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class LogicalAndExpressionContRuleContext extends RuleContext {
        public LogicalAndExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> AndAnd = new ArrayList<>();
        private List<InclusiveOrExpressionRuleContext> inclusiveOrExpression = new ArrayList<>();
        private LogicalAndExpressionContRuleContext logicalAndExpressionCont;
    
        public List<GoodLanguageLexer.Token> AndAnd() {
            return AndAnd;
        }
        
        public List<InclusiveOrExpressionRuleContext> inclusiveOrExpression() {
            return inclusiveOrExpression;
        }
        
        public LogicalAndExpressionContRuleContext logicalAndExpressionCont() {
            return logicalAndExpressionCont;
        }
        
    

    }
    
    public LogicalAndExpressionContRuleContext logicalAndExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            LogicalAndExpressionContRuleContext ctx = new LogicalAndExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ANDAND) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.AndAnd.add(token);
                input = input.subList(1, input.size());
            }

            {
                InclusiveOrExpressionRuleContext token = inclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.inclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                LogicalAndExpressionContRuleContext token = logicalAndExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalAndExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            LogicalAndExpressionContRuleContext ctx = new LogicalAndExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ANDAND) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.AndAnd.add(token);
                input = input.subList(1, input.size());
            }

            {
                InclusiveOrExpressionRuleContext token = inclusiveOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.inclusiveOrExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build logicalAndExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class LogicalOrExpressionRuleContext extends RuleContext {
        public LogicalOrExpressionRuleContext() {
            
        }
        public void after() {
            
        ((LogicalOrExpressionRuleContext) this).code = ((LogicalOrExpressionRuleContext) this).getText();
    
        }
    
        private List<LogicalAndExpressionRuleContext> logicalAndExpression = new ArrayList<>();
        private LogicalOrExpressionContRuleContext logicalOrExpressionCont;
    
        public List<LogicalAndExpressionRuleContext> logicalAndExpression() {
            return logicalAndExpression;
        }
        
        public LogicalOrExpressionContRuleContext logicalOrExpressionCont() {
            return logicalOrExpressionCont;
        }
        
    
        public String code;
    }
    
    public LogicalOrExpressionRuleContext logicalOrExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            LogicalOrExpressionRuleContext ctx = new LogicalOrExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                LogicalAndExpressionRuleContext token = logicalAndExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalAndExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                LogicalOrExpressionContRuleContext token = logicalOrExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalOrExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            LogicalOrExpressionRuleContext ctx = new LogicalOrExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                LogicalAndExpressionRuleContext token = logicalAndExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalAndExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build logicalOrExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class LogicalOrExpressionContRuleContext extends RuleContext {
        public LogicalOrExpressionContRuleContext() {
            
        }
        public void after() {
            
        }
    
        private List<GoodLanguageLexer.Token> OrOr = new ArrayList<>();
        private List<LogicalAndExpressionRuleContext> logicalAndExpression = new ArrayList<>();
        private LogicalOrExpressionContRuleContext logicalOrExpressionCont;
    
        public List<GoodLanguageLexer.Token> OrOr() {
            return OrOr;
        }
        
        public List<LogicalAndExpressionRuleContext> logicalAndExpression() {
            return logicalAndExpression;
        }
        
        public LogicalOrExpressionContRuleContext logicalOrExpressionCont() {
            return logicalOrExpressionCont;
        }
        
    

    }
    
    public LogicalOrExpressionContRuleContext logicalOrExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            LogicalOrExpressionContRuleContext ctx = new LogicalOrExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.OROR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.OrOr.add(token);
                input = input.subList(1, input.size());
            }

            {
                LogicalAndExpressionRuleContext token = logicalAndExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalAndExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            {
                LogicalOrExpressionContRuleContext token = logicalOrExpressionCont(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalOrExpressionCont = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            LogicalOrExpressionContRuleContext ctx = new LogicalOrExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.OROR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.OrOr.add(token);
                input = input.subList(1, input.size());
            }

            {
                LogicalAndExpressionRuleContext token = logicalAndExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalAndExpression.add(token);
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build logicalOrExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ConditionalExpressionRuleContext extends RuleContext {
        public ConditionalExpressionRuleContext() {
            
        }
        public void after() {
            
        ((ConditionalExpressionRuleContext) this).code = ((ConditionalExpressionRuleContext) this).logicalOrExpression().code;
        if (((ConditionalExpressionRuleContext) this).conditionalExpressionCont() != null) {
            ((ConditionalExpressionRuleContext) this).code += conditionalExpressionCont().code;
        }
    
        }
    
        private ConditionalExpressionContRuleContext conditionalExpressionCont;
        private LogicalOrExpressionRuleContext logicalOrExpression;
    
        public ConditionalExpressionContRuleContext conditionalExpressionCont() {
            return conditionalExpressionCont;
        }
        
        public LogicalOrExpressionRuleContext logicalOrExpression() {
            return logicalOrExpression;
        }
        
    
        public String code;
    }
    
    public ConditionalExpressionRuleContext conditionalExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            ConditionalExpressionRuleContext ctx = new ConditionalExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                LogicalOrExpressionRuleContext token = logicalOrExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.logicalOrExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                ConditionalExpressionContRuleContext token = conditionalExpressionCont(input);
                if (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.conditionalExpressionCont = token;
                    input = input.subList(token.length(), input.size());
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build conditionalExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ConditionalExpressionContRuleContext extends RuleContext {
        public ConditionalExpressionContRuleContext() {
            
        }
        public void after() {
            
        ((ConditionalExpressionContRuleContext) this).code = " ? " + ((ConditionalExpressionContRuleContext) this).expression().code + " : " + ((ConditionalExpressionContRuleContext) this).conditionalExpression().code;
    
        }
    
        private GoodLanguageLexer.Token Question;
        private GoodLanguageLexer.Token Colon;
        private ExpressionRuleContext expression;
        private ConditionalExpressionRuleContext conditionalExpression;
    
        public GoodLanguageLexer.Token Question() {
            return Question;
        }
        
        public GoodLanguageLexer.Token Colon() {
            return Colon;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
        public ConditionalExpressionRuleContext conditionalExpression() {
            return conditionalExpression;
        }
        
    
        public String code;
    }
    
    public ConditionalExpressionContRuleContext conditionalExpressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            ConditionalExpressionContRuleContext ctx = new ConditionalExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.QUESTION) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Question = token;
                input = input.subList(1, input.size());
            }

            {
                ExpressionRuleContext token = expression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon = token;
                input = input.subList(1, input.size());
            }

            {
                ConditionalExpressionRuleContext token = conditionalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.conditionalExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build conditionalExpressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class AssignmentExpressionRuleContext extends RuleContext {
        public AssignmentExpressionRuleContext() {
            
        }
        public void after() {
            
        if (((AssignmentExpressionRuleContext) this).conditionalExpression() != null) {
            ((AssignmentExpressionRuleContext) this).code = ((AssignmentExpressionRuleContext) this).conditionalExpression().code;
        } else if (((AssignmentExpressionRuleContext) this).unaryExpression() != null && ((AssignmentExpressionRuleContext) this).assignmentOperator() != null && ((AssignmentExpressionRuleContext) this).assignmentExpression() != null) {
            ((AssignmentExpressionRuleContext) this).code = ((AssignmentExpressionRuleContext) this).unaryExpression().code + " " + ((AssignmentExpressionRuleContext) this).assignmentOperator().code + " " + ((AssignmentExpressionRuleContext) this).assignmentExpression().code;
        } else {
            ((AssignmentExpressionRuleContext) this).code = ((AssignmentExpressionRuleContext) this).getText();
        }
    
        }
    
        private ConditionalExpressionRuleContext conditionalExpression;
        private AssignmentOperatorRuleContext assignmentOperator;
        private AssignmentExpressionRuleContext assignmentExpression;
        private UnaryExpressionRuleContext unaryExpression;
    
        public ConditionalExpressionRuleContext conditionalExpression() {
            return conditionalExpression;
        }
        
        public AssignmentOperatorRuleContext assignmentOperator() {
            return assignmentOperator;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
        public UnaryExpressionRuleContext unaryExpression() {
            return unaryExpression;
        }
        
    
        public String code;
    }
    
    public AssignmentExpressionRuleContext assignmentExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            AssignmentExpressionRuleContext ctx = new AssignmentExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ConditionalExpressionRuleContext token = conditionalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.conditionalExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentExpressionRuleContext ctx = new AssignmentExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                UnaryExpressionRuleContext token = unaryExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.unaryExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                AssignmentOperatorRuleContext token = assignmentOperator(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentOperator = token;
                input = input.subList(token.length(), input.size());
            }

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build assignmentExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class AssignmentExpression2RuleContext extends RuleContext {
        public AssignmentExpression2RuleContext() {
            
        }
        public void after() {
            
        if (((AssignmentExpression2RuleContext) this).conditionalExpression() != null) {
            ((AssignmentExpression2RuleContext) this).code = ((AssignmentExpression2RuleContext) this).conditionalExpression().code;
        } else if (((AssignmentExpression2RuleContext) this).unaryExpression() != null && ((AssignmentExpression2RuleContext) this).assignmentOperator() != null && ((AssignmentExpression2RuleContext) this).assignmentExpression() != null) {
            ((AssignmentExpression2RuleContext) this).code = ((AssignmentExpression2RuleContext) this).unaryExpression().code + " " + ((AssignmentExpression2RuleContext) this).assignmentOperator().code + " " + ((AssignmentExpression2RuleContext) this).assignmentExpression().code;
        } else {
            ((AssignmentExpression2RuleContext) this).code = ((AssignmentExpression2RuleContext) this).getText();
        }
    
        }
    
        private ConditionalExpressionRuleContext conditionalExpression;
        private AssignmentOperatorRuleContext assignmentOperator;
        private AssignmentExpressionRuleContext assignmentExpression;
        private UnaryExpressionRuleContext unaryExpression;
    
        public ConditionalExpressionRuleContext conditionalExpression() {
            return conditionalExpression;
        }
        
        public AssignmentOperatorRuleContext assignmentOperator() {
            return assignmentOperator;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
        public UnaryExpressionRuleContext unaryExpression() {
            return unaryExpression;
        }
        
    
        public String code;
    }
    
    public AssignmentExpression2RuleContext assignmentExpression2(List<GoodLanguageLexer.Token> tokens) {
        do {
            AssignmentExpression2RuleContext ctx = new AssignmentExpression2RuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                ConditionalExpressionRuleContext token = conditionalExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.conditionalExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentExpression2RuleContext ctx = new AssignmentExpression2RuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                UnaryExpressionRuleContext token = unaryExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.unaryExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                AssignmentOperatorRuleContext token = assignmentOperator(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentOperator = token;
                input = input.subList(token.length(), input.size());
            }

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build assignmentExpression2 from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class AssignmentOperatorRuleContext extends RuleContext {
        public AssignmentOperatorRuleContext() {
            
        }
        public void after() {
            
        if (((AssignmentOperatorRuleContext) this).Assign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).Assign().getText();
        if (((AssignmentOperatorRuleContext) this).StarAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).StarAssign().getText();
        if (((AssignmentOperatorRuleContext) this).DivAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).DivAssign().getText();
        if (((AssignmentOperatorRuleContext) this).ModAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).ModAssign().getText();
        if (((AssignmentOperatorRuleContext) this).PlusAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).PlusAssign().getText();
        if (((AssignmentOperatorRuleContext) this).MinusAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).MinusAssign().getText();
        if (((AssignmentOperatorRuleContext) this).LeftShiftAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).LeftShiftAssign().getText();
        if (((AssignmentOperatorRuleContext) this).RightShiftAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).RightShiftAssign().getText();
        if (((AssignmentOperatorRuleContext) this).AndAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).AndAssign().getText();
        if (((AssignmentOperatorRuleContext) this).XorAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).XorAssign().getText();
        if (((AssignmentOperatorRuleContext) this).OrAssign() != null) ((AssignmentOperatorRuleContext) this).code = ((AssignmentOperatorRuleContext) this).OrAssign().getText();
    
        }
    
        private GoodLanguageLexer.Token AndAssign;
        private GoodLanguageLexer.Token MinusAssign;
        private GoodLanguageLexer.Token PlusAssign;
        private GoodLanguageLexer.Token OrAssign;
        private GoodLanguageLexer.Token Assign;
        private GoodLanguageLexer.Token ModAssign;
        private GoodLanguageLexer.Token DivAssign;
        private GoodLanguageLexer.Token XorAssign;
        private GoodLanguageLexer.Token StarAssign;
        private GoodLanguageLexer.Token RightShiftAssign;
        private GoodLanguageLexer.Token LeftShiftAssign;
    
        public GoodLanguageLexer.Token AndAssign() {
            return AndAssign;
        }
        
        public GoodLanguageLexer.Token MinusAssign() {
            return MinusAssign;
        }
        
        public GoodLanguageLexer.Token PlusAssign() {
            return PlusAssign;
        }
        
        public GoodLanguageLexer.Token OrAssign() {
            return OrAssign;
        }
        
        public GoodLanguageLexer.Token Assign() {
            return Assign;
        }
        
        public GoodLanguageLexer.Token ModAssign() {
            return ModAssign;
        }
        
        public GoodLanguageLexer.Token DivAssign() {
            return DivAssign;
        }
        
        public GoodLanguageLexer.Token XorAssign() {
            return XorAssign;
        }
        
        public GoodLanguageLexer.Token StarAssign() {
            return StarAssign;
        }
        
        public GoodLanguageLexer.Token RightShiftAssign() {
            return RightShiftAssign;
        }
        
        public GoodLanguageLexer.Token LeftShiftAssign() {
            return LeftShiftAssign;
        }
        
    
        public String code;
    }
    
    public AssignmentOperatorRuleContext assignmentOperator(List<GoodLanguageLexer.Token> tokens) {
        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Assign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STARASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.StarAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.DIVASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.DivAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MODASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.ModAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.PLUSASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.PlusAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.MINUSASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.MinusAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTSHIFTASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftShiftAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTSHIFTASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightShiftAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ANDASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.AndAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.XORASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.XorAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            AssignmentOperatorRuleContext ctx = new AssignmentOperatorRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ORASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.OrAssign = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build assignmentOperator from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ExpressionRuleContext extends RuleContext {
        public ExpressionRuleContext() {
            
        }
        public void after() {
            
        ((ExpressionRuleContext) this).code = ((ExpressionRuleContext) this).assignmentExpression().code + stringify(((ExpressionRuleContext) this).expressionCont(), c -> c.code, "");
    
        }
    
        private AssignmentExpressionRuleContext assignmentExpression;
        private List<ExpressionContRuleContext> expressionCont = new ArrayList<>();
    
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
        public List<ExpressionContRuleContext> expressionCont() {
            return expressionCont;
        }
        
    
        public String code;
    }
    
    public ExpressionRuleContext expression(List<GoodLanguageLexer.Token> tokens) {
        do {
            ExpressionRuleContext ctx = new ExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            {
                ExpressionContRuleContext token = expressionCont(input);
                while (token != null) {
                    ctx.addLength(token.length());
                    ctx.addText(token.getText());
                    ctx.expressionCont.add(token);
                    input = input.subList(token.length(), input.size());
                    token = expressionCont(input);
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build expression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ExpressionContRuleContext extends RuleContext {
        public ExpressionContRuleContext() {
            
        }
        public void after() {
            
        ((ExpressionContRuleContext) this).code = ", " + ((ExpressionContRuleContext) this).assignmentExpression().code;
    
        }
    
        private GoodLanguageLexer.Token Comma;
        private AssignmentExpressionRuleContext assignmentExpression;
    
        public GoodLanguageLexer.Token Comma() {
            return Comma;
        }
        
        public AssignmentExpressionRuleContext assignmentExpression() {
            return assignmentExpression;
        }
        
    
        public String code;
    }
    
    public ExpressionContRuleContext expressionCont(List<GoodLanguageLexer.Token> tokens) {
        do {
            ExpressionContRuleContext ctx = new ExpressionContRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COMMA) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Comma = token;
                input = input.subList(1, input.size());
            }

            {
                AssignmentExpressionRuleContext token = assignmentExpression(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.assignmentExpression = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build expressionCont from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class TupleExpressionRuleContext extends RuleContext {
        public TupleExpressionRuleContext() {
            
        }
        public void after() {
            
        ((TupleExpressionRuleContext) this).code = updateTuple(((TupleExpressionRuleContext) this).tuple().code, ((TupleExpressionRuleContext) this).type().code, ((TupleExpressionRuleContext) this).tupleAssignment().code);
    
        }
    
        private GoodLanguageLexer.Token Assign;
        private GoodLanguageLexer.Token Colon;
        private TupleAssignmentRuleContext tupleAssignment;
        private TupleRuleContext tuple;
        private TypeRuleContext type;
    
        public GoodLanguageLexer.Token Assign() {
            return Assign;
        }
        
        public GoodLanguageLexer.Token Colon() {
            return Colon;
        }
        
        public TupleAssignmentRuleContext tupleAssignment() {
            return tupleAssignment;
        }
        
        public TupleRuleContext tuple() {
            return tuple;
        }
        
        public TypeRuleContext type() {
            return type;
        }
        
    
        public String code;
    }
    
    public TupleExpressionRuleContext tupleExpression(List<GoodLanguageLexer.Token> tokens) {
        do {
            TupleExpressionRuleContext ctx = new TupleExpressionRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                TupleRuleContext token = tuple(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.tuple = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.COLON) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Colon = token;
                input = input.subList(1, input.size());
            }

            {
                TypeRuleContext token = type(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.type = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.ASSIGN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Assign = token;
                input = input.subList(1, input.size());
            }

            {
                TupleAssignmentRuleContext token = tupleAssignment(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.tupleAssignment = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build tupleExpression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class TupleAssignmentRuleContext extends RuleContext {
        public TupleAssignmentRuleContext() {
            
        }
        public void after() {
            
        if (((TupleAssignmentRuleContext) this).Read() != null) {
            ((TupleAssignmentRuleContext) this).code = "scanf(" + ((TupleAssignmentRuleContext) this).readLiteral().code + ", %SCANF%);";
        } else if (((TupleAssignmentRuleContext) this).Print() != null) {
            ((TupleAssignmentRuleContext) this).code = "printf(" + ((TupleAssignmentRuleContext) this).StringLiteral().getText() + ", %PRINTF%);";
        } else if (((TupleAssignmentRuleContext) this).tuple() != null) {
            ((TupleAssignmentRuleContext) this).code = ((TupleAssignmentRuleContext) this).tuple().code;
        }
    
        }
    
        private GoodLanguageLexer.Token Read;
        private GoodLanguageLexer.Token Print;
        private GoodLanguageLexer.Token StringLiteral;
        private List<GoodLanguageLexer.Token> LeftParen = new ArrayList<>();
        private List<GoodLanguageLexer.Token> RightParen = new ArrayList<>();
        private TupleRuleContext tuple;
        private ReadLiteralRuleContext readLiteral;
    
        public GoodLanguageLexer.Token Read() {
            return Read;
        }
        
        public GoodLanguageLexer.Token Print() {
            return Print;
        }
        
        public GoodLanguageLexer.Token StringLiteral() {
            return StringLiteral;
        }
        
        public List<GoodLanguageLexer.Token> LeftParen() {
            return LeftParen;
        }
        
        public List<GoodLanguageLexer.Token> RightParen() {
            return RightParen;
        }
        
        public TupleRuleContext tuple() {
            return tuple;
        }
        
        public ReadLiteralRuleContext readLiteral() {
            return readLiteral;
        }
        
    
        public String code;
    }
    
    public TupleAssignmentRuleContext tupleAssignment(List<GoodLanguageLexer.Token> tokens) {
        do {
            TupleAssignmentRuleContext ctx = new TupleAssignmentRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.READ) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Read = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                ReadLiteralRuleContext token = readLiteral(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.readLiteral = token;
                input = input.subList(token.length(), input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TupleAssignmentRuleContext ctx = new TupleAssignmentRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.PRINT) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Print = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.LEFTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.LeftParen.add(token);
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STRINGLITERAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.StringLiteral = token;
                input = input.subList(1, input.size());
            }

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.RIGHTPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.RightParen.add(token);
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TupleAssignmentRuleContext ctx = new TupleAssignmentRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                TupleRuleContext token = tuple(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.tuple = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build tupleAssignment from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ReadLiteralRuleContext extends RuleContext {
        public ReadLiteralRuleContext() {
            
        }
        public void after() {
            
        ((ReadLiteralRuleContext) this).code = ((ReadLiteralRuleContext) this).StringLiteral().getText();
    
        }
    
        private GoodLanguageLexer.Token StringLiteral;
    
        public GoodLanguageLexer.Token StringLiteral() {
            return StringLiteral;
        }
        
    
        public String code;
    }
    
    public ReadLiteralRuleContext readLiteral(List<GoodLanguageLexer.Token> tokens) {
        do {
            ReadLiteralRuleContext ctx = new ReadLiteralRuleContext();
            List<GoodLanguageLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                GoodLanguageLexer.Token token = input.get(0);
                if (token.getType() != GoodLanguageLexer.TokenType.STRINGLITERAL) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.StringLiteral = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build readLiteral from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
}



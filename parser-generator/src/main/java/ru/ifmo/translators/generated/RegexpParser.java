
package ru.ifmo.translators.generated;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.List;

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
public class RegexpParser {


    public static class Tree {

        private final String name;
        private final List<Tree> children;

        public Tree(String name, Tree... children) {
            this(name, Arrays.asList(children));
        }

        public Tree(String name, List<Tree> children) {
            this.name = name;
            this.children = children;
        }

        public String getName() {
            return name;
        }

        public List<Tree> getChildren() {
            return children;
        }

        @Override
        public String toString() {
            return name;
        }

        public void visualize() {
            DelegateTree<Tree, Integer> delegateTree = new DelegateTree<>(new DirectedOrderedSparseMultigraph<>());
            delegateTree.addVertex(this);
            Queue<Tree> queue = new LinkedList<>(Collections.singleton(this));

            int edge = 0;
            while (!queue.isEmpty()) {
                Tree parent = queue.poll();
                for (Tree child : parent.children) {
                    delegateTree.addChild(edge++, parent, child);
                    queue.add(child);
                }
            }

            TreeLayout<Tree, Integer> layout = new TreeLayout<>(delegateTree);
            VisualizationViewer<Tree, Integer> viewer = new VisualizationViewer<>(layout);

            RenderContext<Tree, Integer> renderContext = viewer.getRenderContext();

            renderContext.setVertexShapeTransformer(n -> {
                double width = n == null ? 25 : Math.max(n.toString().length() * 10, 25);
                return new Ellipse2D.Double(-(width / 2), -12.5, width, 25.0);
            });
            renderContext.setEdgeShapeTransformer(EdgeShape.line(delegateTree));
            renderContext.setVertexLabelTransformer(new ToStringLabeller());
            renderContext.setVertexFillPaintTransformer(t -> Color.WHITE);
            renderContext.setVertexDrawPaintTransformer(t -> Color.GRAY);

            viewer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

            viewer.setPreferredSize(new Dimension(1280, 720));

            DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
            gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
            viewer.setGraphMouse(gm);

            JFrame frame = new JFrame("JUNG2 Graph Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(viewer);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }


    public static void main(String[] args) {
        Path inputFile = Paths.get(args[0]);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(inputFile.toFile()));
            List<RegexpLexer.Token> tokens = new RegexpLexer().getTokens(is);
            System.out.println(new RegexpParser().expression(tokens).toString());
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

    public static class ExpressionRuleContext extends RuleContext {
        public ExpressionRuleContext() {
            
        }
        public void after() {
            
        ((ExpressionRuleContext) this).tree = new Tree("E", ((ExpressionRuleContext) this).composition().tree, ((ExpressionRuleContext) this).expressionOr().tree);
    
        }
    
        private CompositionRuleContext composition;
        private ExpressionOrRuleContext expressionOr;
    
        public CompositionRuleContext composition() {
            return composition;
        }
        
        public ExpressionOrRuleContext expressionOr() {
            return expressionOr;
        }
        
    
        public Tree tree;
    }
    
    public ExpressionRuleContext expression(List<RegexpLexer.Token> tokens) {
        do {
            ExpressionRuleContext ctx = new ExpressionRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
                CompositionRuleContext token = composition(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.composition = token;
                input = input.subList(token.length(), input.size());
            }

            {
                ExpressionOrRuleContext token = expressionOr(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expressionOr = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build expression from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class ExpressionOrRuleContext extends RuleContext {
        public ExpressionOrRuleContext() {
            
        }
        public void after() {
            
        if (((ExpressionOrRuleContext) this).composition() != null && ((ExpressionOrRuleContext) this).expressionOr() != null) {
            ((ExpressionOrRuleContext) this).tree = new Tree("E'", new Tree("|"), ((ExpressionOrRuleContext) this).composition().tree, ((ExpressionOrRuleContext) this).expressionOr().tree);
        } else {
            ((ExpressionOrRuleContext) this).tree = new Tree("E'");
        }
    
        }
    
        private RegexpLexer.Token Bar;
        private RegexpLexer.Token _EPS;
        private CompositionRuleContext composition;
        private ExpressionOrRuleContext expressionOr;
    
        public RegexpLexer.Token Bar() {
            return Bar;
        }
        
        public RegexpLexer.Token _EPS() {
            return _EPS;
        }
        
        public CompositionRuleContext composition() {
            return composition;
        }
        
        public ExpressionOrRuleContext expressionOr() {
            return expressionOr;
        }
        
    
        public Tree tree;
    }
    
    public ExpressionOrRuleContext expressionOr(List<RegexpLexer.Token> tokens) {
        do {
            ExpressionOrRuleContext ctx = new ExpressionOrRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                RegexpLexer.Token token = input.get(0);
                if (token.getType() != RegexpLexer.TokenType.BAR) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Bar = token;
                input = input.subList(1, input.size());
            }

            {
                CompositionRuleContext token = composition(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.composition = token;
                input = input.subList(token.length(), input.size());
            }

            {
                ExpressionOrRuleContext token = expressionOr(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.expressionOr = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            ExpressionOrRuleContext ctx = new ExpressionOrRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
// Epsilon rule
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build expressionOr from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class CompositionRuleContext extends RuleContext {
        public CompositionRuleContext() {
            
        }
        public void after() {
            
        ((CompositionRuleContext) this).tree = new Tree("C", ((CompositionRuleContext) this).kleene().tree, ((CompositionRuleContext) this).compositionAnd().tree);
    
        }
    
        private CompositionAndRuleContext compositionAnd;
        private KleeneRuleContext kleene;
    
        public CompositionAndRuleContext compositionAnd() {
            return compositionAnd;
        }
        
        public KleeneRuleContext kleene() {
            return kleene;
        }
        
    
        public Tree tree;
    }
    
    public CompositionRuleContext composition(List<RegexpLexer.Token> tokens) {
        do {
            CompositionRuleContext ctx = new CompositionRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
                KleeneRuleContext token = kleene(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.kleene = token;
                input = input.subList(token.length(), input.size());
            }

            {
                CompositionAndRuleContext token = compositionAnd(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.compositionAnd = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build composition from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class CompositionAndRuleContext extends RuleContext {
        public CompositionAndRuleContext() {
            
        }
        public void after() {
            
        if (((CompositionAndRuleContext) this).kleene() != null && ((CompositionAndRuleContext) this).compositionAnd() != null) {
            ((CompositionAndRuleContext) this).tree = new Tree("C'", ((CompositionAndRuleContext) this).kleene().tree, ((CompositionAndRuleContext) this).compositionAnd().tree);
        } else {
            ((CompositionAndRuleContext) this).tree = new Tree("C'");
        }
    
        }
    
        private RegexpLexer.Token _EPS;
        private CompositionAndRuleContext compositionAnd;
        private KleeneRuleContext kleene;
    
        public RegexpLexer.Token _EPS() {
            return _EPS;
        }
        
        public CompositionAndRuleContext compositionAnd() {
            return compositionAnd;
        }
        
        public KleeneRuleContext kleene() {
            return kleene;
        }
        
    
        public Tree tree;
    }
    
    public CompositionAndRuleContext compositionAnd(List<RegexpLexer.Token> tokens) {
        do {
            CompositionAndRuleContext ctx = new CompositionAndRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
                KleeneRuleContext token = kleene(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.kleene = token;
                input = input.subList(token.length(), input.size());
            }

            {
                CompositionAndRuleContext token = compositionAnd(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.compositionAnd = token;
                input = input.subList(token.length(), input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            CompositionAndRuleContext ctx = new CompositionAndRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
// Epsilon rule
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build compositionAnd from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class KleeneRuleContext extends RuleContext {
        public KleeneRuleContext() {
            
        }
        public void after() {
            
        if (((KleeneRuleContext) this).Operator() != null) {
            ((KleeneRuleContext) this).tree = new Tree("K", ((KleeneRuleContext) this).token().tree, new Tree("K'", new Tree(((KleeneRuleContext) this).Operator().getText())));
        } else {
            ((KleeneRuleContext) this).tree = new Tree("K'", ((KleeneRuleContext) this).token().tree, new Tree("K'"));
        }
    
        }
    
        private RegexpLexer.Token Operator;
        private TokenRuleContext token;
    
        public RegexpLexer.Token Operator() {
            return Operator;
        }
        
        public TokenRuleContext token() {
            return token;
        }
        
    
        public Tree tree;
    }
    
    public KleeneRuleContext kleene(List<RegexpLexer.Token> tokens) {
        do {
            KleeneRuleContext ctx = new KleeneRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
                TokenRuleContext token = token(input);
                if (token == null) {
                    break;
                }
                ctx.addLength(token.length());
                ctx.addText(token.getText());
                ctx.token = token;
                input = input.subList(token.length(), input.size());
            }

            {
                RegexpLexer.Token token = input.isEmpty() ? null : input.get(0);
                if (!input.isEmpty() && token.getType() == RegexpLexer.TokenType.OPERATOR) {
                    ctx.addLength(1);
                    ctx.addText(token.getText());
                    ctx.Operator = token;
                    input = input.subList(1, input.size());
                }
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build kleene from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
    public static class TokenRuleContext extends RuleContext {
        public TokenRuleContext() {
            
        }
        public void after() {
            
        if (((TokenRuleContext) this).expression() != null) {
            ((TokenRuleContext) this).tree = new Tree("T", ((TokenRuleContext) this).expression().tree);
        } else if (((TokenRuleContext) this).Letter() != null) {
            ((TokenRuleContext) this).tree = new Tree("T", new Tree(((TokenRuleContext) this).Letter().getText()));
        }
    
        }
    
        private RegexpLexer.Token Letter;
        private RegexpLexer.Token Lparen;
        private RegexpLexer.Token Rparen;
        private ExpressionRuleContext expression;
    
        public RegexpLexer.Token Letter() {
            return Letter;
        }
        
        public RegexpLexer.Token Lparen() {
            return Lparen;
        }
        
        public RegexpLexer.Token Rparen() {
            return Rparen;
        }
        
        public ExpressionRuleContext expression() {
            return expression;
        }
        
    
        public Tree tree;
    }
    
    public TokenRuleContext token(List<RegexpLexer.Token> tokens) {
        do {
            TokenRuleContext ctx = new TokenRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                RegexpLexer.Token token = input.get(0);
                if (token.getType() != RegexpLexer.TokenType.LPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Lparen = token;
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
                RegexpLexer.Token token = input.get(0);
                if (token.getType() != RegexpLexer.TokenType.RPAREN) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Rparen = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);


        do {
            TokenRuleContext ctx = new TokenRuleContext();
            List<RegexpLexer.Token> input = tokens;

            {
                if (input.isEmpty()) break;
                RegexpLexer.Token token = input.get(0);
                if (token.getType() != RegexpLexer.TokenType.LETTER) {
                    break;
                }
                ctx.addText(token.getText());
                ctx.addLength(1);
                ctx.Letter = token;
                input = input.subList(1, input.size());
            }

            if (ctx == null) break;
            ctx.after();
            return ctx;
        } while (false);

        // System.err.println("Can't build token from input: ");
        // tokens.forEach(System.out::println);
        return null;
    }
    
}



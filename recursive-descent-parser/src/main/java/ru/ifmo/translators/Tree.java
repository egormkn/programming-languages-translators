package ru.ifmo.translators;

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
import java.util.Arrays;
import java.util.List;

public class Tree {

    private static int edgeNumber = 0;
    private final String name;
    private final List<Tree> children;

    public Tree(String name, Tree... children) {
        this.name = name;
        this.children = Arrays.asList(children);
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

    public void print() {
        DelegateTree<Tree, Integer> delegateTree = new DelegateTree<>(new DirectedOrderedSparseMultigraph<>());
        delegateTree.addVertex(this);
        edgeNumber = 0;
        for (Tree child : children) {
            child.addToTree(delegateTree, this);
        }

        TreeLayout<Tree, Integer> layout = new TreeLayout<>(delegateTree);
        VisualizationViewer<Tree, Integer> vv = new VisualizationViewer<>(layout);

        RenderContext<Tree, Integer> renderContext = vv.getRenderContext();

        renderContext.setVertexShapeTransformer(n -> {
            double width = n == null ? 25 : Math.max(n.toString().length() * 10, 25);
            return new Ellipse2D.Double(-(width / 2), -12.5, width, 25.0);
        });
        renderContext.setEdgeShapeTransformer(EdgeShape.line(delegateTree));
        renderContext.setVertexLabelTransformer(new ToStringLabeller());
        renderContext.setVertexFillPaintTransformer(t -> Color.WHITE);
        renderContext.setVertexDrawPaintTransformer(t -> Color.GRAY);

        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        vv.setPreferredSize(new Dimension(1280, 720));

        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);

        JFrame frame = new JFrame("JUNG2 Graph Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addToTree(DelegateTree<Tree, Integer> delegateTree, Tree parent) {
        delegateTree.addChild(edgeNumber++, parent, this);
        for (Tree child : children) {
            child.addToTree(delegateTree, this);
        }
    }

    private void print(int level) {
        StringBuilder builder = new StringBuilder(level * 4);
        for (int i = 0; i < (level - 1) * 4; i++) {
            builder.append(i % 4 == 0 ? '│' : ' ');
        }

        String padding = builder.toString();
        System.out.print(padding);
        if (level > 0) System.out.print("└───");
        System.out.println(name);
        for (Tree t : children) {
            t.print(level + 1);
        }
    }
}
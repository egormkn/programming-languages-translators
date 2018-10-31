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
import java.util.*;
import java.util.List;
import java.util.Queue;

public class Tree {

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
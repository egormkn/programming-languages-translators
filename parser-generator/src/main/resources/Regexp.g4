grammar Regexp;

@header {
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
}

@members {

}

e
    returns [String code]
    @after {
        $ctx.code = $ctx.c.code;
    }
    : c ePrime
    ;

ePrime: '|' c ePrime | ;

c
    returns [String code]
    @after {
        $ctx.code = "c";
    }
    : k cPrime;

cPrime: k cPrime | ;

k: t KPrime?;

KPrime: [+*];

t: '(' e ')' | Letter;

Bar: '|';
Lparen: '(';
Rparen: ')';

Letter: [a-z];

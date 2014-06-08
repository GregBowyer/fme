/**
 * Project: sve
 */

package sve.layout.graph;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import sve.engine.MainDisplay;
import sve.layout.abstraction.AbstractGraphLayout;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;

import com.xerox.VTM.engine.LongPoint;

/**
 * This layout arrages the nodes like a tree. It searches for the best top down
 * (like in flowcharts) starting with the nodes which are named as "start"
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class TreeLayout
    extends AbstractGraphLayout {

    /**
     * Horizontal distance between the nodes
     */
    private int distX = 100;

    /**
     * Vertical distance between the nodes
     */
    private int distY = 400;

    /**
     * Applies the layout algorithm to nodes of a graph
     * 
     * @param ag
     *            The graph to layout
     * @param md
     *            The MainDisplay which presents the graph
     */
    public void doLayoutNodes(AbstractGraph ag, MainDisplay md) {

        AbstractNode n, startNode;
        int notVisited, x, c, callBack, toSearch;
        LongPoint r;
        Vector<AbstractNode> startNodes = new Vector<AbstractNode>();
        Iterator<AbstractNode> i;

        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
            "Doing the TreeLayout");

        toSearch = ag.getNodes().size();
        if (100 < toSearch)
            toSearch = 100;

        // Find the most promising nodes
        i = ag.getNodes().iterator();
        while (i.hasNext() && startNodes.size() < toSearch) {
            n = i.next();
            if (n.getEdgesIncoming().size() <= 5)
                startNodes.add(n);
        }
        if (startNodes.size() != 0)
            startNodes.add(ag.getNodes().firstElement());
        else
            return;

        // Compute the CallBack count of each promising node and see which one
        // can
        // reach the most nodes with a minimum of call backs
        startNode = null;
        notVisited = Integer.MAX_VALUE;
        callBack = Integer.MAX_VALUE;
        i = startNodes.iterator();
        while (i.hasNext()) {
            n = i.next();
            c = testCallBack(n);
            x = deleteMarks(ag);
            if ((notVisited != x || callBack != c)
                && ((notVisited >= x && callBack >= c) || (notVisited + callBack >= x
                    + c))) {
                startNode = n;
                notVisited = x;
                callBack = c;

            }
        }
        Logger.getLogger(this.getClass().getCanonicalName()).log(
            Level.INFO,
            "BestNode:" + startNode.getName() + " Call Back's:" + callBack
                + " Not visited nodes:" + notVisited + "/" + ag.getNodes().size()
                + " (Searched nodes:" + toSearch + ")");

        r = layoutNode(startNode, 0, 0);

        // Layout the Rest
        i = ag.getNodes().iterator();
        while (i.hasNext()) {
            n = i.next();

            if (n.getFellow() == null)
                r = layoutNode(n, 0, r.y - 50);
        }

    }

    /**
     * Applies the layout algorithm to edges of a graph
     * 
     * @param ag
     *            The graph to layout
     * @param md
     *            The MainDisplay which presents the graph
     */
    public void doLayoutEdges(AbstractGraph ag, MainDisplay md) {

        AbstractEdge e;
        Iterator<AbstractEdge> i;

        // Layout the Rest
        i = ag.getEdges().iterator();
        while (i.hasNext()) {
            e = i.next();
            if (e.getFellow() != null) {

                if ((e.getEnd2().getX() - e.getEnd2().getWidth() == e
                    .getEnd2ConnectionPoint().getX())
                    || (e.getEnd2().getX() + e.getEnd2().getWidth() == e
                        .getEnd2ConnectionPoint().getX())) {
                    e.getEnd2ConnectionPoint().fixTranslation(
                        e.getEnd2ConnectionPoint().getX(),
                        e.getEnd2ConnectionPoint().getY() - 20);
                }

                else if ((e.getEnd2().getY() - e.getEnd2().getHeight() == e
                    .getEnd2ConnectionPoint().getY())
                    || (e.getEnd2().getY() + e.getEnd2().getHeight() == e
                        .getEnd2ConnectionPoint().getY())) {
                    e.getEnd2ConnectionPoint().fixTranslation(
                        e.getEnd2ConnectionPoint().getX() + 20,
                        e.getEnd2ConnectionPoint().getY());
                }

                e.updateGlyph();
                e.setFellow(null);
            }
        }
    }

    /**
     * Tests a node and all it's children for call backs
     * 
     * @param n
     *            The start node
     * @return The number of identified call backs
     */
    private int testCallBack(AbstractNode n) {
        int ret = 0;
        AbstractEdge e;
        Iterator<AbstractEdge> i;

        n.setFellow("mark");

        i = n.getEdgesOutgoing().iterator();
        while (i.hasNext()) {
            e = i.next();
            if (e.getEnd2().getFellow() != null)
                ret++;
        }

        i = n.getEdgesOutgoing().iterator();
        while (i.hasNext()) {
            e = i.next();
            if (e.getEnd2().getFellow() == null)
                ret += testCallBack(e.getEnd2());
        }

        return ret;
    }

    /**
     * Layout a node
     * 
     * @param n
     *            The node
     * @param x
     *            The upper left X coordinate
     * @param y
     *            The upper left Y coordinate
     * @return A point which holds the lower right X and Y coordinates including
     *         all children
     */
    private LongPoint layoutNode(AbstractNode n, long x, long y) {
        AbstractNode nn;
        AbstractEdge e;
        long nx, ny;
        LongPoint ret, rp;
        Iterator<AbstractEdge> i;

        // Compute the returning point when the nodes is not drawn
        ret = new LongPoint(x, y);

        if (n.getFellow() != null)
            return ret;
        else
            n.setFellow("mark");

        // Compute the returning point when no child nodes are drawn
        ret.x = x + n.getWidth() * 2 + this.distX;
        ret.y = y - n.getHeight() * 2;

        nx = x;
        ny = ret.y - distY;

        // Layout all child nodes
        i = n.getEdgesOutgoing().iterator();
        while (i.hasNext()) {
            e = i.next();
            nn = e.getEnd2();

            rp = layoutNode(nn, nx, ny);
            if (rp.x == nx && rp.y == ny) {
                e.setFellow("CallBack");
            }

            // Update the returning point for the child nodes
            if (rp.x > ret.x)
                ret.x = rp.x;
            if (rp.y < ret.y)
                ret.y = rp.y;
            nx = rp.x;
        }

        n.fixTranslation(x + n.getWidth() + ret.x / 2, y - n.getHeight());

        return ret;
    }

    /**
     * Delete the marks of all nodes in a graph
     * 
     * @param ag
     *            The graph to work on
     * @return The number of unmarked nodes
     */
    private int deleteMarks(AbstractGraph ag) {
        AbstractNode n;
        Iterator<AbstractNode> i;
        int ret;

        ret = 0;
        i = ag.getNodes().iterator();
        while (i.hasNext()) {
            n = i.next();
            if (n.getFellow() != null)
                n.setFellow(null);
            else
                ret++;
        }
        return ret;
    }
}

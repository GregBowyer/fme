/**
 * Project: sve
 */

package sve.structures.graphs;

import java.util.Iterator;
import java.util.Vector;

import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;
import sve.structures.graphs.popupmenu.DirectedGraphMenu;

import com.xerox.VTM.glyphs.VPath;

/**
 * This class represents a directed graph
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class DirectedGraph
    extends AbstractGraph {

    /**
     * A list of all paths in this diagram
     */
    private Vector<VPath> paths = new Vector<VPath>();

    /**
     * All nodes of the graph
     */
    protected Vector<AbstractNode> nodes = new Vector<AbstractNode>();

    /**
     * All edges of the graph
     */
    protected Vector<AbstractEdge> edges = new Vector<AbstractEdge>();

    public void initNodes() {

        AbstractNode t;
        Iterator<AbstractNode> i;

        // Init all components
        i = nodes.iterator();
        while (i.hasNext()) {
            t = i.next();
            t.setActionListener(getActionListener());
            t.init();
        }
    }

    public void initEdges() {

        AbstractEdge t;
        Iterator<AbstractEdge> i;

        i = edges.iterator();
        while (i.hasNext()) {
            t = i.next();
            t.setActionListener(getActionListener());
            t.init();
        }

        computeLevels();

        // Add a popup Menu
        this.popupMenu = new DirectedGraphMenu(this);
        this.hasPopupMenu = true;
    }

    /**
     * Add a node to the graph
     * 
     * @param n
     *            The node to add
     */
    public void addNode(AbstractNode n) {
        nodes.add(n);
    }

    /**
     * Add a edge to the graph
     * 
     * @param n
     *            The node to add
     */
    public void addEdge(AbstractEdge n) {
        edges.add(n);
        paths.add((VPath) n.getLine().getGlyphRepresentation());
    }

    /**
     * (non-Javadoc)
     * 
     * @see sve.structures.abstraction.AbstractGraph#computeLevels()
     */
    protected void computeLevels() {
        Iterator<AbstractEdge> i;
        Iterator<AbstractNode> i2;

        i = edges.iterator();
        while (i.hasNext())
            i.next().onTop();

        i2 = nodes.iterator();
        while (i2.hasNext())
            i2.next().onTop();
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractUMLDiagram#getPaths()
     */
    public Vector<VPath> getPaths() {
        return paths;
    }

    /**
     * (non-Javadoc)
     * 
     * @see sve.structures.abstraction.AbstractGraph#getEdges()
     */
    public Vector<AbstractEdge> getEdges() {
        return edges;
    }

    /**
     * (non-Javadoc)
     * 
     * @see sve.structures.abstraction.AbstractGraph#getNodes()
     */
    public Vector<AbstractNode> getNodes() {
        return nodes;
    }

    public void clear() {
        paths.clear();
        nodes.clear();
        edges.clear();
    }
}

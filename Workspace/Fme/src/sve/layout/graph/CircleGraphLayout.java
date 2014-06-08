/**
 * STRL Visualization Engine Circle Graph Layout
 * 
 * Stefan Natelberg De Montfort University Software Technology Research
 * Laboratory Leicester - United Kingdom
 */

package sve.layout.graph;

import java.util.Iterator;

import sve.engine.MainDisplay;
import sve.layout.abstraction.AbstractGraphLayout;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;
import sve.structures.general.ConnectionPoint;

/**
 * This layout arranges the nodes in a circle layout.
 */
public class CircleGraphLayout
    extends AbstractGraphLayout {
    /**
     * This final variable is the name of the algorithm.
     */
    private final String name = "CircleGraphLayout Algorithm";

    /**
     * This final variable is the version number of the algorithm.
     */
    private final double version = 1.0;

    /**
     * This is the abstract graph.
     */
    private AbstractGraph abstractGraph;

    /**
     * This is the distance between the nodes.
     */
    private final int distanceMultiplier = 80;

    /**
     * This is a counter of the self calls in the abstract graph.
     */
    private int selfCallCounter;

    /**
     * This is the time consumption of the last run of the node layout.
     */
    private long nodeLayoutTime;

    /**
     * This is the time consumption of the last run of the edge layout.
     */
    private long edgeLayoutTime;

    /**
     * (non-Javadoc)
     * 
     * @see sve.layout.abstraction.AbstractGraphLayout#doLayoutNodes(sve.structures.abstraction.AbstractGraph,
     *      sve.engine.MainDisplay)
     */
    public void doLayoutNodes(AbstractGraph ag, MainDisplay md) {

        this.abstractGraph = ag;
        // save the current time
        nodeLayoutTime = System.nanoTime();

        int nodeCount[] = new int[2];
        AbstractNode node;
        double pos_x;
        double pos_y;
        double temp;
        double degree;
        double sinDegree;
        double cosDegree;
        int counter;
        long offset;

        // initialise the node count
        nodeCount[0] = 0;
        nodeCount[1] = 0;
        for (int i = 0; i < abstractGraph.getNodes().size(); i++)
            // increase node count #0 if the node has edges
            if (((AbstractNode) abstractGraph.getNodes().get(i)).getEdges().size() != 0)
                nodeCount[0]++;
            // increase node count #1 if the node has no edges
            else
                nodeCount[1]++;

        // initialise the variables to layout the nodes with edges
        pos_x = nodeCount[0] * distanceMultiplier;
        pos_y = 0;
        temp = 0;
        degree = 2 * Math.PI / nodeCount[0];
        sinDegree = Math.sin(degree);
        cosDegree = Math.cos(degree);

        // Layout the nodes with edges
        counter = 0;
        for (int i = 0; counter < nodeCount[0]; i++) {
            // get the node
            node = abstractGraph.getNodes().get(i);

            // process the next node if the node has no edges
            if (node.getEdges().size() == 0)
                continue;
            // increase the counter if the node has edges
            else
                counter++;

            // save the old x position
            temp = pos_x;
            // calculate the new x position
            pos_x = pos_x * cosDegree - pos_y * sinDegree;
            // calculate the new y position
            pos_y = temp * sinDegree + pos_y * cosDegree;
            // fix the node
            node.fixTranslation((long) pos_x, (long) pos_y);
        } // for

        // initialise the variables to layout the nodes without edges
        pos_x = nodeCount[1] * distanceMultiplier;
        pos_y = 0;
        temp = 0;
        degree = 2 * Math.PI / nodeCount[1];
        sinDegree = Math.sin(degree);
        cosDegree = Math.cos(degree);

        // initialise the offset
        offset = (nodeCount[0] + nodeCount[1] + 5) * distanceMultiplier;

        // Layout the nodes without edges
        counter = 0;
        for (int i = 0; counter < nodeCount[1]; i++) {
            // get the node
            node = abstractGraph.getNodes().get(i);

            // process the next node if the node has edges
            if (node.getEdges().size() != 0)
                continue;
            // increase the counter if the node has no edges
            else
                counter++;

            // save the old x position
            temp = pos_x;
            // calculate the new x position
            pos_x = pos_x * cosDegree - pos_y * sinDegree;
            // calculate the new y position
            pos_y = temp * sinDegree + pos_y * cosDegree;
            // fix the node
            node.fixTranslation((long) pos_x, (long) pos_y + offset);
        } // for

        // calculate the time consumption
        nodeLayoutTime = System.nanoTime() - nodeLayoutTime;
    }

    /**
     * (non-Javadoc)
     * 
     * @see sve.layout.abstraction.AbstractGraphLayout#doLayoutEdges(sve.structures.abstraction.AbstractGraph,
     *      sve.engine.MainDisplay)
     */
    public void doLayoutEdges(AbstractGraph ag, MainDisplay md) {

        if (abstractGraph == null)
            this.abstractGraph = ag;

        // save the current time
        edgeLayoutTime = System.nanoTime();

        Iterator<AbstractEdge> edges;
        AbstractEdge edge;

        // reset the self call counter
        selfCallCounter = 0;

        // initialise the iterator for the loop
        edges = abstractGraph.getEdges().iterator();
        // sets the connection point of the edges
        while (edges.hasNext()) {
            edge = edges.next();

            // if the edge is a self call
            if (edge.getEnd1() == edge.getEnd2()) {
                // increase the self call counter
                selfCallCounter++;
                // set the connection points to the north of the node
                edge.setEnd1ConnectionPoint(AbstractEdge.NORTH);
                edge.setEnd2ConnectionPoint(AbstractEdge.NORTH);
                // fix the connection points
                // the distance between the connection points is 80
                edge.getEnd1ConnectionPoint().fixTranslation(
                    edge.getEnd1ConnectionPoint().getX() - 40,
                    edge.getEnd1ConnectionPoint().getY());
                edge.getEnd2ConnectionPoint().fixTranslation(
                    edge.getEnd2ConnectionPoint().getX() + 40,
                    edge.getEnd2ConnectionPoint().getY());

                // add two new connection points
                ConnectionPoint point_1 = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());
                ConnectionPoint point_2 = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // fix the connection points
                point_1.fixTranslation(point_1.getX() + 10, point_1.getY() + 50);
                point_2.fixTranslation(point_2.getX() - 10, point_2.getY() + 50);

                // hide the added points
                point_1.hide();
                point_2.hide();
            } // if
        } // while

        // update the abstract graph
        update();

        // calculate the time consumption
        edgeLayoutTime = System.nanoTime() - edgeLayoutTime;
    } // method applyEdgeLayout

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        // calculate the time
        double totalTimeMs = (nodeLayoutTime + edgeLayoutTime) / 1000000d;
        double nodeLayoutTimeMs = nodeLayoutTime / 1000000d;
        double edgeLayoutTimeMs = edgeLayoutTime / 1000000d;

        // create the return string
        String returnString = "\n"
            + "\n --------------------------------------------------" + "\n "
            + name + " v" + version
            + "\n --------------------------------------------------\n"
            + "\n Number of Nodes: " + abstractGraph.getNodes().size()
            + "\n Number of Edges: " + abstractGraph.getEdges().size() + "\n"
            + "\n Distance Multiplier: " + distanceMultiplier + "\n"
            + "\n Number of Self Calls: " + selfCallCounter
            + "\n Time Consumption (Node Layout): " + nodeLayoutTimeMs + "ms"
            + "\n Time Consumption (Edge Layout): " + edgeLayoutTimeMs + "ms"
            + "\n Time Consumption (Total): " + totalTimeMs + "ms\n"
            + "\n --------------------------------------------------\n" + "\n";

        return returnString;
    } // method toString

    /**
     * This method updates the abstract graph.
     */
    public void update() {
        Iterator<AbstractNode> nodes = abstractGraph.getNodes().iterator();
        Iterator<AbstractEdge> edges = abstractGraph.getEdges().iterator();

        // update nodes
        while (nodes.hasNext())
            nodes.next().updateGlyph();

        // update edges
        while (edges.hasNext())
            edges.next().updateGlyph();
    } // method update
} // class CircleGraphLayout

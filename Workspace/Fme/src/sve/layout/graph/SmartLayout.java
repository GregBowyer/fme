/**
 * STRL Visualization Engine Raster Graph Layout
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
 * This layout algorithm arranges the nodes of an abstract graph in an
 * orthogonal raster.
 */
public class SmartLayout
    extends AbstractGraphLayout {
    /**
     * This final variable is the name of the algorithm.
     */
    private final String name = "RasterGraphLayout Algorithm";

    /**
     * This final variable is the version number of the algorithm.
     */
    private final double version = 1.0;

    /**
     * This variable sets the adaptive edge mode of the algorithm.
     */
    private boolean adaptiveEdges;

    /**
     * This variable sets the uncompress mode of the algorithm.
     */
    private boolean uncompress;

    /**
     * This is the abstract graph.
     */
    private AbstractGraph abstractGraph;

    /**
     * This is the number of nodes without edges.
     */
    private int nodeWithoutEdgesNumber;

    /**
     * This is the distance ratio (dimension X / dimension Y).
     */
    private double distanceRatio;

    /**
     * This is the distance multiplier.
     */
    private double distanceMultiplier;

    /**
     * This is the x distance between the nodes.
     */
    private int x_distance;

    /**
     * This is the half x distance between the nodes.
     */
    private int x_distance_2;

    /**
     * This is the eighth x distance between the nodes.
     */
    private int x_distance_8;

    /**
     * This is the y distance between the nodes.
     */
    private int y_distance;

    /**
     * This is the half y distance between the nodes.
     */
    private int y_distance_2;

    /**
     * This is the eighth y distance between the nodes.
     */
    private int y_distance_8;

    /**
     * This is the distance between two edges.
     */
    private int edge_distance;

    /**
     * This is the dimension x of the area.
     */
    private int dim_x;

    /**
     * This is the dimension y of the area.
     */
    private int dim_y;

    /**
     * This is the value X0 in the area.
     */
    private int X0;

    /**
     * This is the value Y0 in the area.
     */
    private int Y0;

    /**
     * This is the element map which stores the position of the nodes.
     */
    private int elementMap[][];

    /**
     * This array saves the allocated lines in the x dimension.
     */
    private boolean x_allocated[];

    /**
     * This array saves the allocated lines in the y dimension.
     */
    private boolean y_allocated[];

    /**
     * This array saves the number of edges in the x dimension.
     */
    private int edgeNumber_x[];

    /**
     * This array saves the number of edges in the y dimension.
     */
    private int edgeNumber_y[];

    /**
     * This is the int code for the dimension x.
     */
    private final int x = 0;

    /**
     * This is the int code for the dimension y.
     */
    private final int y = 1;

    /**
     * This is the int code for a free element.
     */
    private final int free = -1;

    /**
     * This is the int code for a reserved element.
     */
    private final int reserved = Integer.MIN_VALUE;

    /**
     * This is the String for the keyword 'west'.
     */
    private final String west = "W";

    /**
     * This is the String for the keyword 'east'.
     */
    private final String east = "E";

    /**
     * This is the String for the keyword 'south'.
     */
    private final String south = "S";

    /**
     * This is the String for the keyword 'north'.
     */
    private final String north = "N";

    /**
     * This is a counter of the self calls in the abstract graph.
     */
    private int selfCallCounter;

    /**
     * This is a counter of the inner diversions in the abstract graph.
     */
    private int innerDiversionCounter;

    /**
     * This is a counter of the outer diversions in the abstract graph.
     */
    private int outerDiversionCounter;

    /**
     * This is the time consumption of the last run of the node layout.
     */
    private long nodeLayoutTime;

    /**
     * This is the time consumption of the last run of the edge layout.
     */
    private long edgeLayoutTime;

    /**
     * The Constructor
     */
    public SmartLayout() {
    }

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

        Iterator<AbstractNode> nodes;
        AbstractNode node;
        long largestNodeSize[] = new long[2];

        // get the size of the edge vector
        int edgeSize = abstractGraph.getEdges().size();
        // get the size of the node vector
        int nodeSize = abstractGraph.getNodes().size();

        // initialise the distance ratio
        if (edgeSize > 5 * nodeSize)
            distanceRatio = 0.5D;
        else if (edgeSize > 4 * nodeSize)
            distanceRatio = 0.6D;
        else if (edgeSize > 3 * nodeSize)
            distanceRatio = 0.7D;
        else if (edgeSize > 2 * nodeSize)
            distanceRatio = 0.8D;
        else if (edgeSize > nodeSize)
            distanceRatio = 0.9D;
        else
            distanceRatio = 1.0D;

        // initialise the distance multiplier
        if (edgeSize < 10)
            distanceMultiplier = 250;
        else if (edgeSize < 50)
            distanceMultiplier = 300;
        else if (edgeSize < 100)
            distanceMultiplier = 350;
        else if (edgeSize < 150)
            distanceMultiplier = 400;
        else if (edgeSize < 200)
            distanceMultiplier = 450;
        else if (edgeSize < 250)
            distanceMultiplier = 500;
        else if (edgeSize < 300)
            distanceMultiplier = 550;
        else if (edgeSize < 350)
            distanceMultiplier = 600;
        else if (edgeSize < 400)
            distanceMultiplier = 650;
        else if (edgeSize < 450)
            distanceMultiplier = 700;
        else if (edgeSize < 500)
            distanceMultiplier = 750;
        else
            distanceMultiplier = 800;

        // initialise the iterator for the loop
        nodes = abstractGraph.getNodes().iterator();
        // calculate the largest node size
        while (nodes.hasNext()) {
            node = nodes.next();
            // set the x dimension (width)
            largestNodeSize[x] = Math.max(largestNodeSize[x], node.getWidth());
            // set the y dimension (height)
            largestNodeSize[y] = Math.max(largestNodeSize[y], node.getHeight());
        } // while

        // calculate the x distance between the nodes
        x_distance = (int) (distanceMultiplier * distanceRatio);
        // the x distance has to be longer than the width of every single node
        x_distance = (int) Math.max(x_distance, 2 * largestNodeSize[x] + 10);
        // calculate the half x distance between the nodes
        x_distance_2 = x_distance / 2;
        // calculate the eighth x distance between the nodes
        x_distance_8 = x_distance / 8;
        // calculate the y distance between the nodes
        y_distance = (int) (distanceMultiplier / distanceRatio);
        // the y distance has to be longer than the height of every single node
        y_distance = (int) Math.max(y_distance, 2 * largestNodeSize[y] + 10);
        // calculate the half y distance between the nodes
        y_distance_2 = y_distance / 2;
        // calculate the eighth x distance between the nodes
        y_distance_8 = y_distance / 8;
        // calculate the distance between two edges
        edge_distance = x_distance / 15;

        // set the boolean of the adaptiveEdges mode
        adaptiveEdges = edgeSize <= 1000;

        // set the boolean of the uncompress mode
        uncompress = edgeSize <= 500;

        // allocate elements for the uncompress mode
        if (uncompress) {
            // initialise the size of the x and y dimensions
            dim_x = Math.max(nodeSize * 5, 10);
            dim_y = Math.max(nodeSize * 3, 10);
        } // if
          // allocate elements for the normal mode
        else {
            // initialise the size of the x and y dimensions
            dim_x = Math.max(nodeSize * 2, 10);
            dim_y = Math.max(nodeSize * 2, 10);
        } // else

        // initialise the X0 and Y0 coordinates
        X0 = dim_x / 2;
        Y0 = dim_y / 2;

        // initialise the size of the arrays
        elementMap = new int[dim_x][dim_y];
        x_allocated = new boolean[dim_x];
        y_allocated = new boolean[dim_y];
        edgeNumber_x = new int[dim_x];
        edgeNumber_y = new int[dim_y];

        // initialise the values of the element map
        for (int i = 0; i < dim_x; i++)
            for (int j = 0; j < dim_y; j++)
                elementMap[i][j] = free;

        // initialise the values of the allocated lines in the x dimension
        for (int i = 0; i < dim_x; i++)
            x_allocated[i] = false;

        // initialise the values of the allocated lines in the y dimension
        for (int i = 0; i < dim_y; i++)
            y_allocated[i] = false;

        // initialise the values of the number of edges in the x dimension
        for (int i = 0; i < dim_x; i++)
            edgeNumber_x[i] = 0;

        // initialise the values of the number of edges in the y dimension
        for (int i = 0; i < dim_y; i++)
            edgeNumber_y[i] = 0;

        // if there are nodes
        if (nodeSize != 0) {
            // process the nodes
            markNodesWithoutEdges();
            processOtherNodes();
            processNodesWithoutEdges();
        } // if

        // calculate the time consumption
        nodeLayoutTime = System.nanoTime() - nodeLayoutTime;
    } // method applyNodeLayout

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

        // return if the adaptive edges are not enabled
        if (!adaptiveEdges) {
            // calculate the time consumption
            edgeLayoutTime = System.nanoTime() - edgeLayoutTime;
            return;
        } // if

        Iterator<AbstractEdge> edges;
        AbstractEdge edge;

        long xy_1[] = new long[2];
        long xy_2[] = new long[2];

        // prepare the diversion
        prepareDiversion();

        // initialise the iterator for the loop
        edges = abstractGraph.getEdges().iterator();
        // sets the connection point of the edges
        while (edges.hasNext()) {
            edge = edges.next();

            // if the edge has two connection points
            if (edge.getFellow() == null) {
                // get the x distance between the connection points
                xy_1[x] = edge.getEnd2().getX() - edge.getEnd1().getX();
                // get the y distance between the connection points
                xy_1[y] = edge.getEnd2().getY() - edge.getEnd1().getY();
                // get the x distance between the connection points
                xy_2[x] = xy_1[x];
                // get the y distance between the connection points
                xy_2[y] = xy_1[y];
            } // if
              // if the edge has four connection points and the edge is a self
              // call
              // (fellow mode 'self call')
            else if (((Fellow) edge.getFellow()).mode == Fellow.SELF_CALL) {
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
                // process the next edge
                continue;
            } // else if
              // if the edge has four connection points and the edge is an inner
              // diversion
              // (fellow mode 'inner diversion')
            else if (((Fellow) edge.getFellow()).mode == Fellow.INNER_DIVERSION) {
                // set the connection points to the north of the node
                edge.setEnd1ConnectionPoint(AbstractEdge.NORTH);
                edge.setEnd2ConnectionPoint(AbstractEdge.NORTH);

                // if the edge goes to the east
                if (edge.getEnd2().getX() > edge.getEnd1().getX()) {
                    edge.getEnd1ConnectionPoint().fixTranslation(
                        edge.getEnd1ConnectionPoint().getX() + 25,
                        edge.getEnd1ConnectionPoint().getY());
                    edge.getEnd2ConnectionPoint().fixTranslation(
                        edge.getEnd2ConnectionPoint().getX() - 25,
                        edge.getEnd2ConnectionPoint().getY());
                } // if
                  // if the edge goes to the west
                else {
                    edge.getEnd1ConnectionPoint().fixTranslation(
                        edge.getEnd1ConnectionPoint().getX() - 25,
                        edge.getEnd1ConnectionPoint().getY());
                    edge.getEnd2ConnectionPoint().fixTranslation(
                        edge.getEnd2ConnectionPoint().getX() + 25,
                        edge.getEnd2ConnectionPoint().getY());
                } // else if
                  // process the next edge
                continue;
            } // else if
              // if the edge has four connection points and the edge is an outer
              // diversion
              // (fellow mode 'outer diversion')
            else if (((Fellow) edge.getFellow()).mode == Fellow.OUTER_DIVERSION) {
                // get the x distance between the connection points
                xy_1[x] = ((Fellow) edge.getFellow()).x1 - edge.getEnd1().getX();
                // get the y distance between the connection points
                xy_1[y] = ((Fellow) edge.getFellow()).y1 - edge.getEnd1().getY();
                // get the x distance between the connection points
                xy_2[x] = edge.getEnd2().getX() - ((Fellow) edge.getFellow()).x2;
                // get the y distance between the connection points
                xy_2[y] = edge.getEnd2().getY() - ((Fellow) edge.getFellow()).y2;
            } // else if
              // if the edge has three connection points and the edge is
              // incoming
              // (fellow mode 'incoming')
            else if (((Fellow) edge.getFellow()).mode == Fellow.INCOMING) {
                // get the x distance between the connection points
                xy_1[x] = ((Fellow) edge.getFellow()).x - edge.getEnd1().getX();
                // get the y distance between the connection points
                xy_1[y] = ((Fellow) edge.getFellow()).y - edge.getEnd1().getY();
                // get the x distance between the connection points
                xy_2[x] = edge.getEnd2().getX() - ((Fellow) edge.getFellow()).x;
                // get the y distance between the connection points
                xy_2[y] = edge.getEnd2().getY() - ((Fellow) edge.getFellow()).y;
            } // else if
              // if the edge has three connection points and the edge is
              // outgoing
              // (fellow mode 'outgoing')
            else if (((Fellow) edge.getFellow()).mode == Fellow.OUTGOING) {
                // get the x distance between the connection points
                xy_1[x] = ((Fellow) edge.getFellow()).x - edge.getEnd1().getX();
                // get the y distance between the connection points
                xy_1[y] = ((Fellow) edge.getFellow()).y - edge.getEnd1().getY();
                // get the x distance between the connection points
                xy_2[x] = edge.getEnd2().getX() - ((Fellow) edge.getFellow()).x;
                // get the y distance between the connection points
                xy_2[y] = edge.getEnd2().getY() - ((Fellow) edge.getFellow()).y;
            } // else if

            /*
             * The following code is to arrange the end 1 connection points of
             * the edges. This should be done to seperate the end 1 connection
             * points from the end 2 connection points in the final
             * visualization.
             */

            // the edge is connected with the left side of a node
            if (-xy_1[x] > Math.abs(xy_1[y])) {
                // set the connection point to the west of the node
                edge.setEnd1ConnectionPoint(AbstractEdge.WEST);
                // if the edge goes to the south
                if (edge.getEnd1().getY() > edge.getEnd2().getY())
                    edge
                        .getEnd1ConnectionPoint().fixTranslation(
                            edge.getEnd1ConnectionPoint().getX(),
                            edge.getEnd1ConnectionPoint().getY() - 5);
                // if the edge goes to the north
                else
                    edge.getEnd1ConnectionPoint().fixTranslation(
                        edge.getEnd1ConnectionPoint().getX(),
                        edge.getEnd1ConnectionPoint().getY() + 5);
            } // if
              // the edge is connected with the right side of a node
            else if (xy_1[x] > Math.abs(xy_1[y])) {
                // set the connection point to the east of the node
                edge.setEnd1ConnectionPoint(AbstractEdge.EAST);
                // if the edge goes to the south
                if (edge.getEnd1().getY() > edge.getEnd2().getY())
                    edge
                        .getEnd1ConnectionPoint().fixTranslation(
                            edge.getEnd1ConnectionPoint().getX(),
                            edge.getEnd1ConnectionPoint().getY() - 5);
                // if the edge goes to the north
                else
                    edge.getEnd1ConnectionPoint().fixTranslation(
                        edge.getEnd1ConnectionPoint().getX(),
                        edge.getEnd1ConnectionPoint().getY() + 5);
            } // else if
              // the edge is connected with the bottom of a node
            else if (Math.abs(xy_1[x]) <= -xy_1[y]) {
                // set the connection point to the south of the node
                edge.setEnd1ConnectionPoint(AbstractEdge.SOUTH);
                // if the edge goes to the west
                if (edge.getEnd2().getX() < edge.getEnd1().getX())
                    edge
                        .getEnd1ConnectionPoint().fixTranslation(
                            edge.getEnd1ConnectionPoint().getX() - 5,
                            edge.getEnd1ConnectionPoint().getY());
                // if the edge goes to the east
                else
                    edge.getEnd1ConnectionPoint().fixTranslation(
                        edge.getEnd1ConnectionPoint().getX() + 5,
                        edge.getEnd1ConnectionPoint().getY());
            } // else if
              // the edge is connected with the top of a node
            else if (Math.abs(xy_1[x]) <= xy_1[y]) {
                // set the connection point to the north of the node
                edge.setEnd1ConnectionPoint(AbstractEdge.NORTH);
                // if the edge goes to the west
                if (edge.getEnd2().getX() < edge.getEnd1().getX())
                    edge
                        .getEnd1ConnectionPoint().fixTranslation(
                            edge.getEnd1ConnectionPoint().getX() - 5,
                            edge.getEnd1ConnectionPoint().getY());
                // if the edge goes to the east
                else
                    edge.getEnd1ConnectionPoint().fixTranslation(
                        edge.getEnd1ConnectionPoint().getX() + 5,
                        edge.getEnd1ConnectionPoint().getY());
            } // else if

            /*
             * The following code is to arrange the end 2 connection points of
             * the edges. This should be done to seperate the end 1 connection
             * points from the end 2 connection points in the final
             * visualization.
             */

            // the edge is connected with the left side of a node
            if (xy_2[x] > Math.abs(xy_2[y])) {
                // set the connection point to the west of the node
                edge.setEnd2ConnectionPoint(AbstractEdge.WEST);
                // if the edge comes from the south
                if (edge.getEnd1().getY() < edge.getEnd2().getY())
                    edge
                        .getEnd2ConnectionPoint().fixTranslation(
                            edge.getEnd2ConnectionPoint().getX(),
                            edge.getEnd2ConnectionPoint().getY() - 20);
                // if the edge comes from the north
                else
                    edge.getEnd2ConnectionPoint().fixTranslation(
                        edge.getEnd2ConnectionPoint().getX(),
                        edge.getEnd2ConnectionPoint().getY() + 20);
            } // if
              // the edge is connected with the right side of a node
            else if (-xy_2[x] > Math.abs(xy_2[y])) {
                // set the connection point to the east of the node
                edge.setEnd2ConnectionPoint(AbstractEdge.EAST);
                // if the edge comes from the south
                if (edge.getEnd1().getY() < edge.getEnd2().getY())
                    edge
                        .getEnd2ConnectionPoint().fixTranslation(
                            edge.getEnd2ConnectionPoint().getX(),
                            edge.getEnd2ConnectionPoint().getY() - 20);
                // if the edge comes from the north
                else
                    edge.getEnd2ConnectionPoint().fixTranslation(
                        edge.getEnd2ConnectionPoint().getX(),
                        edge.getEnd2ConnectionPoint().getY() + 20);
            } // else if
              // the edge is connected with the bottom of a node
            else if (Math.abs(xy_2[x]) <= xy_2[y]) {
                // set the connection point to the south of the node
                edge.setEnd2ConnectionPoint(AbstractEdge.SOUTH);
                // if the edge comes from the west
                if (edge.getEnd2().getX() > edge.getEnd1().getX())
                    edge
                        .getEnd2ConnectionPoint().fixTranslation(
                            edge.getEnd2ConnectionPoint().getX() - 20,
                            edge.getEnd2ConnectionPoint().getY());
                // if the edge comes from the east
                else
                    edge.getEnd2ConnectionPoint().fixTranslation(
                        edge.getEnd2ConnectionPoint().getX() + 20,
                        edge.getEnd2ConnectionPoint().getY());
            } // else if
              // the edge is connected with the top of a node
            else if (Math.abs(xy_2[x]) <= -xy_2[y]) {
                // set the connection point to the north of the node
                edge.setEnd2ConnectionPoint(AbstractEdge.NORTH);
                // if the edge comes from the west
                if (edge.getEnd2().getX() > edge.getEnd1().getX())
                    edge
                        .getEnd2ConnectionPoint().fixTranslation(
                            edge.getEnd2ConnectionPoint().getX() - 20,
                            edge.getEnd2ConnectionPoint().getY());
                // if the edge comes from the east
                else
                    edge.getEnd2ConnectionPoint().fixTranslation(
                        edge.getEnd2ConnectionPoint().getX() + 20,
                        edge.getEnd2ConnectionPoint().getY());
            } // else if
        } // while

        // reset the counter
        selfCallCounter = 0;
        innerDiversionCounter = 0;
        outerDiversionCounter = 0;

        // process the diversion
        processDiversion();

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

        // prepare the strings for the adaptive edge mode
        String adaptiveEdges = "disabled";
        if (this.adaptiveEdges)
            adaptiveEdges = "enabled";

        // prepare the strings for the uncompress mode
        String uncompress = "disabled";
        if (this.uncompress)
            uncompress = "enabled";

        // create the return string
        String returnString = "\n"
            + "\n --------------------------------------------------" + "\n "
            + name + " v" + version
            + "\n --------------------------------------------------\n"
            + "\n Number of Nodes: " + abstractGraph.getNodes().size()
            + "\n Number of Edges: " + abstractGraph.getEdges().size() + "\n"
            + "\n Distance Ratio: " + distanceRatio + "\n Distance Multiplier: "
            + distanceMultiplier + "\n" + "\n Node X-Distance: " + x_distance
            + "\n Node Y-Distance: " + y_distance + "\n Edge Distance: "
            + edge_distance + "\n" + "\n Array Dimension: x = " + dim_x + ", y = "
            + dim_y + "\n" + "\n Adaptive Edges: " + adaptiveEdges
            + "\n Uncompress Graph: " + uncompress + "\n"
            + "\n Number of Self Calls: " + selfCallCounter
            + "\n Number of Inner Diversions: " + innerDiversionCounter
            + "\n Number of Outer Diversions: " + outerDiversionCounter + "\n"
            + "\n Time Consumption (Node Layout): " + nodeLayoutTimeMs + "ms"
            + "\n Time Consumption (Edge Layout): " + edgeLayoutTimeMs + "ms"
            + "\n Time Consumption (Total): " + totalTimeMs + "ms\n"
            + "\n --------------------------------------------------\n" + "\n";

        return returnString;
    } // method toString

    /**
     * This method marks and counts the nodes without edges.
     */
    private void markNodesWithoutEdges() {
        Iterator<AbstractNode> nodes;
        AbstractNode node;
        int counter = 0;

        // initialise the iterator for the loop
        nodes = abstractGraph.getNodes().iterator();
        // analyse the nodes without edges
        while (nodes.hasNext()) {
            node = nodes.next();

            // if the node has no edges
            if (node.getEdges().size() == 0) {
                node.setFellow(new Fellow(Fellow.MARKED));
                counter++;
            } // if
        } // while

        // set the global variable
        nodeWithoutEdgesNumber = counter;
    } // method markNodesWithoutEdges

    /**
     * This method processes the nodes without edges.
     */
    private void processNodesWithoutEdges() {
        Iterator<AbstractNode> nodes;
        AbstractNode node;

        // calculate the offset
        int offset = 0;
        while (y_allocated[Y0 + offset])
            offset++;
        offset++;

        int pos_x = (int) (Math.sqrt((int) nodeWithoutEdgesNumber)) / 2;
        int yy = offset;
        int xx = 0;

        // initialise the iterator for the loop
        nodes = abstractGraph.getNodes().iterator();
        // process the nodes without edges
        while (nodes.hasNext()) {
            node = nodes.next();

            // continue if this node has edges
            if (node.getEdges().size() != 0)
                continue;

            // reset the fellow
            node.setFellow(null);

            // place this node
            placeSingleNode(node, xx, yy);

            // decrease the x position if it is less than 0
            if (xx <= 0)
                xx--;

            // alternate sign
            xx = -xx;

            // if the row is full
            if (xx <= -pos_x) {
                // jump to the next row in the area
                yy++;
                // jump to the x position 0 in the area
                xx = 0;
            } // if
        } // while
    } // method processNodesWithoutEdges

    /**
     * This method processes all other nodes.
     */
    private void processOtherNodes() {
        AbstractNode node = null;
        int offset = 0;

        // get the best start-node
        node = findBestStartNode();

        // process the other nodes
        while (node != null) {
            // place the node
            placeSingleNode(node, 0, offset);

            // calculate the offset
            while (y_allocated[Y0 + offset])
                offset--;
            offset--;

            // get the best start-node
            node = findBestStartNode();
        } // while
    } // method processOtherNodes

    /**
     * This method returns for the best start node or null.
     * 
     * @return AbstractNode
     */
    private AbstractNode findBestStartNode() {
        Iterator<AbstractNode> nodes;
        AbstractNode node;
        AbstractNode startNode = null;

        // initialise the iterator for the loop
        nodes = abstractGraph.getNodes().iterator();
        // search for the best start node
        while (nodes.hasNext()) {
            // get the next node
            node = nodes.next();

            // continue if the node has a fellow
            if (node.getFellow() != null)
                // process the next node
                continue;

            // if this is the first possible start-node which has been found
            if (startNode == null)
                startNode = node;
            // if there is already a start-node which has been found,
            // compare the start-nodes and select the best one
            else if (node.getEdges().size() >= startNode.getEdges().size())
                startNode = node;
        } // while

        // return the best start node
        return startNode;
    } // method findBestStartNode

    /**
     * This method places a node and creates the layout for a single graph.
     * 
     * @param node
     * @param pos_x
     * @param pos_y
     */
    private void placeSingleNode(AbstractNode node, int pos_x, int pos_y) {
        // if the node has already been processed
        if (node.getFellow() != null)
            return;

        long vx;
        long vy;
        int offset;

        // allocate the line in the y dimension
        y_allocated[Y0 + pos_y] = true;

        // this variable shows if the node has more than one edge
        boolean singleNode = node.getEdges().size() == 1;

        // if the selected element is free
        if (elementMap[X0 + pos_x][Y0 + pos_y] == free
            || (singleNode && elementMap[X0 + pos_x][Y0 + pos_y] == reserved)) {
            // allocate the line in the x dimension
            x_allocated[X0 + pos_x] = true;

            // calculate the position of the node
            vx = (long) pos_x * (long) x_distance;
            vy = (long) pos_y * (long) y_distance;

            // process the interlace mode
            if (pos_y % 2 != 0)
                // shift the node to the left
                node.fixTranslation(vx - x_distance_2, vy);
            else
                node.fixTranslation(vx, vy);

            // set the fellow
            node.setFellow(new Fellow(pos_x, pos_y));

            // allocate the elements
            allocateElements(node, X0 + pos_x, Y0 + pos_y);

            // process the parent nodes
            processParentNodes(node, pos_x, pos_y);

            // process the child nodes
            processChildNodes(node, pos_x, pos_y);

            // return if the selected element is free
            return;
        } // if

        // go on here if the selected element is not free
        offset = 1;
        while (true) {
            // test the right side if it is free
            if (elementMap[X0 + pos_x + offset][Y0 + pos_y] == free
                || (singleNode && elementMap[X0 + pos_x + offset][Y0 + pos_y] == reserved)) {
                // calculate the new x position in the area
                pos_x = pos_x + offset;

                // allocate the line in the x dimension
                x_allocated[X0 + pos_x] = true;

                // calculate the position of the node
                vx = (long) pos_x * (long) x_distance;
                vy = (long) pos_y * (long) y_distance;

                // process the interlace mode
                if (pos_y % 2 != 0)
                    // shift the node to the left
                    node.fixTranslation(vx - x_distance_2, vy);
                else
                    node.fixTranslation(vx, vy);

                // set the fellow
                node.setFellow(new Fellow(pos_x, pos_y));

                // allocate the elements
                allocateElements(node, X0 + pos_x, Y0 + pos_y);

                // stop the loop
                break;
            } // if
              // test the left side if it is free
            else if (elementMap[X0 + pos_x - offset][Y0 + pos_y] == free
                || (singleNode && elementMap[X0 + pos_x - offset][Y0 + pos_y] == reserved)) {
                // calculate the new x position in the area
                pos_x = pos_x - offset;

                // allocate the line in the x dimension
                x_allocated[X0 + pos_x] = true;

                // calculate the position of the node
                vx = (long) pos_x * (long) x_distance;
                vy = (long) pos_y * (long) y_distance;

                // process the interlace mode
                if (pos_y % 2 != 0)
                    // shift the node to the left
                    node.fixTranslation(vx - x_distance_2, vy);
                else
                    node.fixTranslation(vx, vy);

                // set the fellow
                node.setFellow(new Fellow(pos_x, pos_y));

                // allocate the elements
                allocateElements(node, X0 + pos_x, Y0 + pos_y);

                // stop the loop
                break;
            } // else if
              // increase the index offset
            offset++;
        } // while

        // process the parent nodes
        processParentNodes(node, pos_x, pos_y);

        // process the child nodes
        processChildNodes(node, pos_x, pos_y);
    } // method placeSingleNode

    /**
     * This method processes the parent nodes of a node.
     * 
     * @param node
     * @param pos_x
     * @param pos_y
     */
    private void processParentNodes(AbstractNode node, int pos_x, int pos_y) {
        Iterator<AbstractEdge> incoming;

        AbstractEdge edge;
        long interlace = 0;

        // initialise the iterator for the loop
        incoming = node.getEdgesIncoming().iterator();
        // process the incoming edges of the node
        while (incoming.hasNext()) {
            // get the next edge
            edge = incoming.next();

            // if the parent node has more than 100 outgoing edges
            if (edge.getEnd1().getEdgesOutgoing().size() > 100) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 3);
                y_allocated[Y0 + pos_y + 1] = true;
                y_allocated[Y0 + pos_y + 2] = true;
            } // if
              // if the parent node has more than five outgoing edges
            else if (edge.getEnd1().getEdgesOutgoing().size() > 5) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 2);
                y_allocated[Y0 + pos_y + 1] = true;
            } // else if
              // if the node has more than 250 incoming edges
            else if (node.getEdgesIncoming().size() > 250) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 3
                    + (int) (interlace % 6));
                y_allocated[Y0 + pos_y + 1] = true;
                y_allocated[Y0 + pos_y + 2] = true;
            } // else if
              // if the node has more than 100 incoming edges
            else if (node.getEdgesIncoming().size() > 100) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 3
                    + (int) (interlace % 4));
                y_allocated[Y0 + pos_y + 1] = true;
                y_allocated[Y0 + pos_y + 2] = true;
            } // else if
              // if the node has more than 50 incoming edges
            else if (node.getEdgesIncoming().size() > 50) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 2
                    + (int) (interlace % 3));
                y_allocated[Y0 + pos_y + 1] = true;
            } // else if
              // if the node has more than 25 incoming edges
            else if (node.getEdgesIncoming().size() > 25) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 2
                    + (int) (interlace % 2));
                y_allocated[Y0 + pos_y + 1] = true;
            } // else if
              // if the node has more than five incoming edges
            else if (node.getEdgesIncoming().size() > 5) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 2);
                y_allocated[Y0 + pos_y + 1] = true;
            } // else if
              // if the node has more than 100 outgoing edges
            else if (node.getEdgesOutgoing().size() > 100) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 3);
                y_allocated[Y0 + pos_y + 1] = true;
                y_allocated[Y0 + pos_y + 2] = true;
            } // else if
              // if the node has more than five outgoing edges
            else if (node.getEdgesOutgoing().size() > 5) {
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 2);
                y_allocated[Y0 + pos_y + 1] = true;
            } // else if
              // place the node in every case
            else
                placeSingleNode(edge.getEnd1(), pos_x, pos_y + 1);

            interlace++;
        } // while
    } // method processParentNodes

    /**
     * This method processes the child nodes of a node.
     * 
     * @param node
     * @param pos_x
     * @param pos_y
     */
    private void processChildNodes(AbstractNode node, int pos_x, int pos_y) {
        Iterator<AbstractEdge> outgoing;

        AbstractEdge edge;
        long interlace = 0;

        // initialise the iterator for the loop
        outgoing = node.getEdgesOutgoing().iterator();
        // process the outgoing edges of the node
        while (outgoing.hasNext()) {
            // get the next edge
            edge = outgoing.next();

            // if the child node has more than 100 incoming edges
            if (edge.getEnd2().getEdgesIncoming().size() > 100) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 3);
                y_allocated[Y0 + pos_y - 1] = true;
                y_allocated[Y0 + pos_y - 2] = true;
            } // if
              // if the child node has more than five incoming edges
            else if (edge.getEnd2().getEdgesIncoming().size() > 5) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 2);
                y_allocated[Y0 + pos_y - 1] = true;
            } // else if
              // if the node has more than 250 outgoing edges
            else if (node.getEdgesOutgoing().size() > 250) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 3
                    - (int) (interlace % 6));
                y_allocated[Y0 + pos_y - 1] = true;
                y_allocated[Y0 + pos_y - 2] = true;
            } // else if
              // if the node has more than 100 outgoing edges
            else if (node.getEdgesOutgoing().size() > 100) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 3
                    - (int) (interlace % 4));
                y_allocated[Y0 + pos_y - 1] = true;
                y_allocated[Y0 + pos_y - 2] = true;
            } // else if
              // if the node has more than 50 outgoing edges
            else if (node.getEdgesOutgoing().size() > 50) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 2
                    - (int) (interlace % 3));
                y_allocated[Y0 + pos_y - 1] = true;
            } // else if
              // if the node has more than 25 outgoing edges
            else if (node.getEdgesOutgoing().size() > 25) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 2
                    - (int) (interlace % 2));
                y_allocated[Y0 + pos_y - 1] = true;
            } // else if
              // if the node has more than five outgoing edges
            else if (node.getEdgesOutgoing().size() > 5) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 2);
                y_allocated[Y0 + pos_y - 1] = true;
            } // else if
              // if the node has more than 100 incoming edges
            else if (node.getEdgesIncoming().size() > 100) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 3);
                y_allocated[Y0 + pos_y - 1] = true;
                y_allocated[Y0 + pos_y - 2] = true;
            } // else if
              // if the node has more than five incoming edges
            else if (node.getEdgesIncoming().size() > 5) {
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 2);
                y_allocated[Y0 + pos_y - 1] = true;
            } // else if
            else
                // place the node in every case
                placeSingleNode(edge.getEnd2(), pos_x, pos_y - 1);

            interlace++;
        } // while
    } // method processChildNodes

    /**
     * This method allocates the elements for node and the child nodes.
     * 
     * @param node
     * @param pos_x
     * @param pos_y
     */
    private void allocateElements(AbstractNode node, int pos_x, int pos_y) {
        // allocate the element for the node
        elementMap[pos_x][pos_y] = abstractGraph.getNodes().indexOf(node);

        // allocate more elements if the uncompress mode is enabled
        if (!uncompress)
            return;

        // calculate the number of allocated elements
        int allocationNumber = Math.max(node.getEdgesIncoming().size(), node
            .getEdgesOutgoing().size());
        allocationNumber--;

        // process allocation
        int offset = 0;
        for (int i = 0; i < allocationNumber; i++) {
            if (offset >= 0)
                offset++;

            // allocate the element if it is free
            if (elementMap[pos_x + offset][pos_y] == free)
                elementMap[pos_x + offset][pos_y] = reserved;

            // change sign
            offset = -offset;
        } // for
    } // method allocateElements

    /**
     * This method analyses if the node is the root of a simple tree.
     * 
     * @param node
     * @param incoming
     * @return boolean
     */
    private boolean isSimpleTree(AbstractNode node, boolean incoming) {
        Iterator<AbstractEdge> edges;
        AbstractEdge edge;
        int counter;
        int otherEdges;

        // initialise the maximal number of other edges
        if (node.getEdges().size() < 10)
            otherEdges = 3;
        else if (node.getEdges().size() < 25)
            otherEdges = 5;
        else if (node.getEdges().size() < 50)
            otherEdges = 10;
        else
            otherEdges = 25;

        // incoming edges will be processed
        if (incoming) {
            // reset the counter
            counter = 0;

            // initialise the iterator for the loop
            edges = node.getEdgesIncoming().iterator();
            // process the edges
            while (edges.hasNext()) {
                // get the next edge
                edge = edges.next();
                // count the child nodes which have also child nodes
                if (edge.getEnd1().getEdges().size() > 1)
                    counter++;
                // return false if more than the maximal number of child nodes
                // have also
                // child nodes
                if (counter > otherEdges)
                    return false;
            } // while
        } // if
          // outgoing edges will be processed
        else {
            // reset the counter
            counter = 0;

            // initialise the iterator for the loop
            edges = node.getEdgesOutgoing().iterator();
            // process the edges
            while (edges.hasNext()) {
                // get the next edge
                edge = edges.next();
                // count the child nodes which have also child nodes
                if (edge.getEnd2().getEdges().size() > 1)
                    counter++;
                // return false if more than the maximal number of child nodes
                // have also
                // child nodes
                if (counter > otherEdges)
                    return false;
            } // while
        } // else

        return true;
    } // method isSimpleTree

    /**
     * This method calculates the coordinates of an added connection point.
     * After the processing a fellow will be attached to the edges which
     * contains the coordinates of the connection point. The connection points
     * will not be added to the edges. The existing fellows of the edges will be
     * deleted.
     */
    private void prepareDiversion() {
        Iterator<AbstractEdge> edges;
        AbstractEdge edge;
        Fellow fellow;

        // mark the edges with a fellow
        unmarkEdges();
        markEdges();

        // initialise the iterator for the loop
        edges = abstractGraph.getEdges().iterator();
        // sets the connection point of the edges
        while (edges.hasNext()) {
            // get the next edge
            edge = edges.next();

            // get the fellow
            fellow = (Fellow) edge.getFellow();

            // check for null pointer
            if (fellow == null)
                continue;

            // create the connection points for an edge with a self call
            // (fellow mode 'self call')
            if (fellow.mode == Fellow.SELF_CALL)
                ; // do not process
                  // create the connection points for an edge with an inner
                  // diversion
                  // (fellow mode 'inner diversion')
            else if (fellow.mode == Fellow.INNER_DIVERSION)
                ; // do not process
                  // create the connection points for an edge with an outer
                  // diversion on the
                  // west side
                  // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(west)) {
                // search for the outer index on the west side
                int index = 0;
                while (!x_allocated[index])
                    index++;
                // decrease index
                index--;

                // set the coordinates of the connection points
                fellow.x1 = (index - X0) * x_distance;
                fellow.y1 = edge.getEnd1ConnectionPoint().getY();
                fellow.x2 = fellow.x1;
                fellow.y2 = edge.getEnd2ConnectionPoint().getY();
            } // else if
              // create the connection points for an edge with an outer
              // diversion on the
              // east side
              // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(east)) {
                // search for the outer index on the east side
                int index = dim_x - 1;
                while (!x_allocated[index])
                    index--;
                // increase index
                index++;

                // set the coordinates of the connection points
                fellow.x1 = (index - X0) * x_distance;
                fellow.y1 = edge.getEnd1ConnectionPoint().getY();
                fellow.x2 = fellow.x1;
                fellow.y2 = edge.getEnd2ConnectionPoint().getY();
            } // else if
              // create the connection points for an edge with an outer
              // diversion on the
              // south side
              // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(south)) {
                // search for the outer index on the south side
                int index = 0;
                while (!y_allocated[index])
                    index++;
                // decrease index
                index--;

                // set the coordinates of the connection points
                fellow.x1 = edge.getEnd1ConnectionPoint().getX();
                fellow.y1 = (index - Y0) * y_distance;
                fellow.x2 = edge.getEnd2ConnectionPoint().getX();
                fellow.y2 = fellow.y1;
            } // else if
              // create the connection points for an edge with an outer
              // diversion on the
              // north side
              // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(north)) {
                // search for the outer index on the north side
                int index = dim_y - 1;
                while (!y_allocated[index])
                    index--;
                // increase index
                index++;

                // set the coordinates of the connection points
                fellow.x1 = edge.getEnd1ConnectionPoint().getX();
                fellow.y1 = (index - Y0) * y_distance;
                fellow.x2 = edge.getEnd2ConnectionPoint().getX();
                fellow.y2 = fellow.y1;
            } // else if
              // create a fellow for an incoming edge
            else if (fellow.mode == Fellow.INCOMING) {
                // set the coordinates of the connection points
                fellow.x = edge.getEnd1ConnectionPoint().getX();
                fellow.y = fellow.y * y_distance;
            } // else if
              // create a fellow for an outgoing edge
            else if (fellow.mode == Fellow.OUTGOING) {
                // set the coordinates of the connection points
                fellow.x = edge.getEnd2ConnectionPoint().getX();
                fellow.y = fellow.y * y_distance;
            } // else if
        } // while
    } // method prepareDiversion

    /**
     * This method calculates the coordinates of an added connection point.
     * After the processing a fellow will be attached to the edges which
     * contains the connection point. The existing fellows of the edges will be
     * deleted.
     */
    private void processDiversion() {
        Iterator<AbstractEdge> edges;
        AbstractEdge edge;
        Fellow fellow;
        int pos_x;
        int pos_y;
        int pos_X0;
        int pos_Y0;
        int pos_X0_offset;
        int pos_Y0_offset;
        int offset;
        int elementOffset;
        int elementNumber;
        long pos_offset;
        long pos;

        // mark the edges with a fellow
        unmarkEdges();
        markEdges();

        // initialise the iterator for the loop
        edges = abstractGraph.getEdges().iterator();
        // sets the connection point of the edges
        while (edges.hasNext()) {
            // get the next edge
            edge = edges.next();

            // get the fellow
            fellow = (Fellow) edge.getFellow();

            // check for null pointer
            if (fellow == null)
                continue;

            // create the connection points for an edge with a self call
            // (fellow mode 'self call')
            if (fellow.mode == Fellow.SELF_CALL) {
                // increase the self call counter
                selfCallCounter++;

                // add two new connection points
                ConnectionPoint point_1 = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());
                ConnectionPoint point_2 = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // fix the connection points
                point_1.fixTranslation(point_1.getX() + 10, point_1.getY()
                    + y_distance_2);
                point_2.fixTranslation(point_2.getX() - 10, point_2.getY()
                    + y_distance_2);

                // clear the vector and put the points into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point_1);
                fellow.connectionPoints.add(point_2);

                // hide the added points
                point_1.hide();
                point_2.hide();
            } // if
              // create the connection points for an edge with an inner
              // diversion
              // (fellow mode 'inner diversion')
            else if (fellow.mode == Fellow.INNER_DIVERSION) {
                // increase the inner diversion counter
                innerDiversionCounter++;

                pos_Y0 = Y0 + (int) ((Fellow) edge.getEnd1().getFellow()).y;
                // add two new connection points
                ConnectionPoint point_1 = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());
                ConnectionPoint point_2 = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // set the offset of the connection point
                offset = edgeNumber_y[pos_Y0] * edge_distance;

                // select the top position of the edge
                if (edgeNumber_y[pos_Y0] >= 0) {
                    // increase the counter of the element
                    edgeNumber_y[pos_Y0]++;
                    // invert the counter
                    edgeNumber_y[pos_Y0] = -edgeNumber_y[pos_Y0];
                } // if
                  // select the bottom position of the edge
                else
                    // invert the counter
                    edgeNumber_y[pos_Y0] = -edgeNumber_y[pos_Y0];

                // fix the connection points
                if (point_1.getX() < point_2.getX()) {
                    point_1.fixTranslation(point_1.getX() + x_distance_8, point_1.getY()
                        + y_distance_2 + offset);
                    point_2.fixTranslation(point_2.getX() - x_distance_8, point_2.getY()
                        + y_distance_2 + offset);
                } // if
                else {
                    point_1.fixTranslation(point_1.getX() - x_distance_8, point_1.getY()
                        + y_distance_2 + offset);
                    point_2.fixTranslation(point_2.getX() + x_distance_8, point_2.getY()
                        + y_distance_2 + offset);
                } // else

                // clear the vector and put the points into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point_1);
                fellow.connectionPoints.add(point_2);

                // hide the added points
                point_1.hide();
                point_2.hide();
            } // else if
              // create the connection points for an edge with an outer
              // diversion on the
              // west side
              // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(west)) {
                // increase the outer diversion counter
                outerDiversionCounter++;

                // get the most western end of the edge
                pos_x = Math.min((int) ((Fellow) edge.getEnd1().getFellow()).x,
                    (int) ((Fellow) edge.getEnd2().getFellow()).x);

                // initialise the sum
                pos_X0 = X0 + pos_x;

                // add two new connection points
                ConnectionPoint point_1 = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());
                ConnectionPoint point_2 = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // get the lowest position of the edge
                pos_Y0 = Y0
                    + (int) Math.min(((Fellow) (edge.getEnd1().getFellow())).y,
                        ((Fellow) (edge.getEnd2().getFellow())).y);

                // get the number of elements between the nodes
                elementNumber = (int) Math
                    .abs(((Fellow) (edge.getEnd1().getFellow())).y
                        - ((Fellow) (edge.getEnd2().getFellow())).y);

                boolean isFree = true;
                elementOffset = -1;
                // set the outer position of the edge
                while (true) {
                    // initialise the sum
                    pos_X0_offset = pos_X0 + elementOffset;
                    // check if the elements are free or reserved
                    for (int i = 0; i <= elementNumber; i++)
                        // check the next column if an element has been found
                        // which is not
                        // free or reserved
                        if (elementMap[pos_X0_offset][pos_Y0 + i] != free
                            && elementMap[pos_X0_offset][pos_Y0 + i] != reserved) {
                            // decrease the element offset
                            elementOffset--;
                            // set the boolean
                            isFree = false;
                            // break the loop
                            break;
                        } // if

                    // break the loop if a free column has been found
                    if (isFree)
                        break;
                    isFree = true;
                } // while

                // initialise the sum
                pos_offset = pos_x + elementOffset;

                // initialise the sum
                pos_X0_offset = pos_X0 + elementOffset;

                // set the offset of the connection point
                offset = edgeNumber_x[pos_X0_offset] * edge_distance;

                // select the right position of the edge
                if (edgeNumber_x[pos_X0_offset] >= 0) {
                    // increase the counter of the element
                    edgeNumber_x[pos_X0_offset]++;
                    // invert the counter
                    edgeNumber_x[pos_X0_offset] = -edgeNumber_x[pos_X0_offset];
                } // if
                  // select the left position of the edge
                else
                    // invert the counter
                    edgeNumber_x[pos_X0_offset] = -edgeNumber_x[pos_X0_offset];

                // fix the connection points
                pos = pos_offset * x_distance + offset - x_distance_2;
                if (point_1.getY() < point_2.getY()) {
                    point_1.fixTranslation(pos, point_1.getY() + y_distance_8);
                    point_2.fixTranslation(pos, point_2.getY() - y_distance_8);
                } // if
                else {
                    point_1.fixTranslation(pos, point_1.getY() - y_distance_8);
                    point_2.fixTranslation(pos, point_2.getY() + y_distance_8);
                } // else

                // clear the vector and put the points into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point_1);
                fellow.connectionPoints.add(point_2);

                // hide the added points
                point_1.hide();
                point_2.hide();
            } // else if
              // create the connection points for an edge with an outer
              // diversion on the
              // east side
              // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(east)) {
                // increase the outer diversion counter
                outerDiversionCounter++;

                // get the most eastern end of the edge
                pos_x = Math.max((int) ((Fellow) edge.getEnd1().getFellow()).x,
                    (int) ((Fellow) edge.getEnd2().getFellow()).x);

                // initialise the sum
                pos_X0 = X0 + pos_x;

                // add two new connection points
                ConnectionPoint point_1 = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());
                ConnectionPoint point_2 = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // get the lowest position of the edge
                pos_Y0 = Y0
                    + (int) Math.min(((Fellow) (edge.getEnd1().getFellow())).y,
                        ((Fellow) (edge.getEnd2().getFellow())).y);

                // get the number of elements between the nodes
                elementNumber = (int) Math
                    .abs(((Fellow) (edge.getEnd1().getFellow())).y
                        - ((Fellow) (edge.getEnd2().getFellow())).y);

                boolean isFree = true;
                elementOffset = 1;
                // set the outer position of the edge
                while (true) {
                    // initialise the sum
                    pos_X0_offset = pos_X0 + elementOffset;
                    // check if the elements are free or reserved
                    for (int i = 0; i <= elementNumber; i++)
                        // check the next column if an element has been found
                        // which is not
                        // free or reserved
                        if (elementMap[pos_X0_offset][pos_Y0 + i] != free
                            && elementMap[pos_X0_offset][pos_Y0 + i] != reserved) {
                            // increase the element offset
                            elementOffset++;
                            // set the boolean
                            isFree = false;
                            // break the loop
                            break;
                        } // if

                    // break the loop if a free column has been found
                    if (isFree)
                        break;
                    isFree = true;
                } // while

                // initialise the sum
                pos_offset = pos_x + elementOffset;

                // initialise the sum
                pos_X0_offset = pos_X0 + elementOffset;

                // set the offset of the connection point
                offset = edgeNumber_x[pos_X0_offset] * edge_distance;

                // select the right position of the edge
                if (edgeNumber_x[pos_X0_offset] >= 0) {
                    // increase the counter of the element
                    edgeNumber_x[pos_X0_offset]++;
                    // invert the counter
                    edgeNumber_x[pos_X0_offset] = -edgeNumber_x[pos_X0_offset];
                } // if
                  // select the left position of the edge
                else
                    // invert the counter
                    edgeNumber_x[pos_X0_offset] = -edgeNumber_x[pos_X0_offset];

                // fix the connection points
                pos = pos_offset * x_distance + offset;
                if (point_1.getY() < point_2.getY()) {
                    point_1.fixTranslation(pos, point_1.getY() + y_distance_8);
                    point_2.fixTranslation(pos, point_2.getY() - y_distance_8);
                } // if
                else {
                    point_1.fixTranslation(pos, point_1.getY() - y_distance_8);
                    point_2.fixTranslation(pos, point_2.getY() + y_distance_8);
                } // else

                // clear the vector and put the points into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point_1);
                fellow.connectionPoints.add(point_2);

                // hide the added points
                point_1.hide();
                point_2.hide();
            } // else if
              // create the connection points for an edge with an outer
              // diversion on the
              // south side
              // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(south)) {
                // increase the outer diversion counter
                outerDiversionCounter++;

                // get the most southern end of the edge
                pos_y = Math.min((int) ((Fellow) edge.getEnd1().getFellow()).y,
                    (int) ((Fellow) edge.getEnd2().getFellow()).y);

                // initialise the sum
                pos_Y0 = Y0 + pos_y;

                // add two new connection points
                ConnectionPoint point_1 = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());
                ConnectionPoint point_2 = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // get the leftmost position of the edge
                pos_X0 = X0
                    + (int) Math.min(((Fellow) (edge.getEnd1().getFellow())).x,
                        ((Fellow) (edge.getEnd2().getFellow())).x);

                // get the number of elements between the nodes
                elementNumber = (int) Math
                    .abs(((Fellow) (edge.getEnd1().getFellow())).x
                        - ((Fellow) (edge.getEnd2().getFellow())).x);

                boolean isFree = true;
                elementOffset = -1;
                // set the outer position of the edge
                while (true) {
                    // initialise the sum
                    pos_Y0_offset = pos_Y0 + elementOffset;
                    // check if the elements are free or reserved
                    for (int i = 0; i <= elementNumber; i++)
                        // check the next column if an element has been found
                        // which is not
                        // free or reserved
                        if (elementMap[pos_X0 + i][pos_Y0_offset] != free
                            && elementMap[pos_X0 + i][pos_Y0_offset] != reserved) {
                            // decrease the element offset
                            elementOffset--;
                            // set the boolean
                            isFree = false;
                            // break the loop
                            break;
                        } // if

                    // break the loop if a free column has been found
                    if (isFree)
                        break;
                    isFree = true;
                } // while

                // initialise the sum
                pos_offset = pos_y + elementOffset;

                // initialise the sum
                pos_Y0_offset = pos_Y0 + elementOffset;

                // set the offset of the connection point
                offset = edgeNumber_y[pos_Y0_offset] * edge_distance;

                // select the right position of the edge
                if (edgeNumber_y[pos_Y0_offset] >= 0) {
                    // increase the counter of the element
                    edgeNumber_y[pos_Y0_offset]++;
                    // invert the counter
                    edgeNumber_y[pos_Y0_offset] = -edgeNumber_y[pos_Y0_offset];
                } // if
                  // select the left position of the edge
                else
                    // invert the counter
                    edgeNumber_y[pos_Y0_offset] = -edgeNumber_y[pos_Y0_offset];

                // fix the connection points
                pos = pos_offset * y_distance + offset;
                if (point_1.getX() < point_2.getX()) {
                    point_1.fixTranslation(point_1.getX() + x_distance_8, pos);
                    point_2.fixTranslation(point_2.getX() - x_distance_8, pos);
                } // if
                else {
                    point_1.fixTranslation(point_1.getX() - x_distance_8, pos);
                    point_2.fixTranslation(point_2.getX() + x_distance_8, pos);
                } // else

                // clear the vector and put the points into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point_1);
                fellow.connectionPoints.add(point_2);

                // hide the added points
                point_1.hide();
                point_2.hide();
            } // else if
              // create the connection points for an edge with an outer
              // diversion on the
              // north side
              // (fellow mode 'outer diversion')
            else if (fellow.mode == Fellow.OUTER_DIVERSION
                && fellow.annotation.equals(north)) {
                // increase the outer diversion counter
                outerDiversionCounter++;

                // get the most northern end of the edge
                pos_y = Math.max((int) ((Fellow) edge.getEnd1().getFellow()).y,
                    (int) ((Fellow) edge.getEnd2().getFellow()).y);

                // initialise the sum
                pos_Y0 = Y0 + pos_y;

                // add two new connection points
                ConnectionPoint point_1 = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());
                ConnectionPoint point_2 = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // get the leftmost position of the edge
                pos_X0 = X0
                    + (int) Math.min(((Fellow) (edge.getEnd1().getFellow())).x,
                        ((Fellow) (edge.getEnd2().getFellow())).x);

                // get the number of elements between the nodes
                elementNumber = (int) Math
                    .abs(((Fellow) (edge.getEnd1().getFellow())).x
                        - ((Fellow) (edge.getEnd2().getFellow())).x);

                boolean isFree = true;
                elementOffset = 1;
                // set the outer position of the edge
                while (true) {
                    // initialise the sum
                    pos_Y0_offset = pos_Y0 + elementOffset;
                    // check if the elements are free or reserved
                    for (int i = 0; i <= elementNumber; i++)
                        // check the next column if an element has been found
                        // which is not
                        // free or reserved
                        if (elementMap[pos_X0 + i][pos_Y0_offset] != free
                            && elementMap[pos_X0 + i][pos_Y0_offset] != reserved) {
                            // increase the element offset
                            elementOffset++;
                            // set the boolean
                            isFree = false;
                            // break the loop
                            break;
                        } // if

                    // break the loop if a free column has been found
                    if (isFree)
                        break;
                    isFree = true;
                } // while

                // initialise the sum
                pos_offset = pos_y + elementOffset;

                // initialise the sum
                pos_Y0_offset = pos_Y0 + elementOffset;

                // set the offset of the connection point
                offset = edgeNumber_y[pos_Y0_offset] * edge_distance;

                // select the right position of the edge
                if (edgeNumber_y[pos_Y0_offset] >= 0) {
                    // increase the counter of the element
                    edgeNumber_y[pos_Y0_offset]++;
                    // invert the counter
                    edgeNumber_y[pos_Y0_offset] = -edgeNumber_y[pos_Y0_offset];
                } // if
                  // select the left position of the edge
                else
                    // invert the counter
                    edgeNumber_y[pos_Y0_offset] = -edgeNumber_y[pos_Y0_offset];

                // fix the connection points
                pos = pos_offset * y_distance + offset;
                if (point_1.getX() < point_2.getX()) {
                    point_1.fixTranslation(point_1.getX() + x_distance_8, pos);
                    point_2.fixTranslation(point_2.getX() - x_distance_8, pos);
                } // if
                else {
                    point_1.fixTranslation(point_1.getX() - x_distance_8, pos);
                    point_2.fixTranslation(point_2.getX() + x_distance_8, pos);
                } // else

                // clear the vector and put the points into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point_1);
                fellow.connectionPoints.add(point_2);

                // hide the added points
                point_1.hide();
                point_2.hide();
            } // else if
              // create a connection point for an incoming edge
              // (fellow mode 'incoming')
            else if (fellow.mode == Fellow.INCOMING) {
                // read the fellow
                pos_X0 = X0 + (int) fellow.x;
                pos_y = (int) fellow.y;

                // add a new connection point
                ConnectionPoint point = edge.addConnectionPoint(edge
                    .getEnd2ConnectionPoint().getX(), edge.getEnd2ConnectionPoint()
                    .getY());

                // set the offset of the connection point
                offset = edgeNumber_x[pos_X0] * edge_distance;

                // select the right position of the edge
                if (edgeNumber_x[pos_X0] >= 0) {
                    // increase the counter of the element
                    edgeNumber_x[pos_X0]++;
                    // invert the counter
                    edgeNumber_x[pos_X0] = -edgeNumber_x[pos_X0];
                } // if
                  // select the left position of the edge
                else
                    // invert the counter
                    edgeNumber_x[pos_X0] = -edgeNumber_x[pos_X0];

                // fix the connection point
                point.fixTranslation(edge.getEnd1ConnectionPoint().getX() + offset,
                    pos_y * y_distance);

                // clear the vector and put the point into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point);

                // hide the added point
                point.hide();
            } // else if
              // create a connection point for an outgoing edge
              // (fellow mode 'outgoing')
            else if (fellow.mode == Fellow.OUTGOING) {
                // read the fellow
                pos_X0 = X0 + (int) fellow.x;
                pos_y = (int) fellow.y;

                // add a new connection point
                ConnectionPoint point = edge.addConnectionPoint(edge
                    .getEnd1ConnectionPoint().getX(), edge.getEnd1ConnectionPoint()
                    .getY());

                // set the offset of the connection point
                offset = edgeNumber_x[pos_X0] * edge_distance;

                // select the right position of the edge
                if (edgeNumber_x[pos_X0] >= 0) {
                    // increase the counter of the element
                    edgeNumber_x[pos_X0]++;
                    // invert the counter
                    edgeNumber_x[pos_X0] = -edgeNumber_x[pos_X0];
                } // if
                  // select the left position of the edge
                else
                    // invert the counter
                    edgeNumber_x[pos_X0] = -edgeNumber_x[pos_X0];

                // fix the connection point
                point.fixTranslation(edge.getEnd2ConnectionPoint().getX() + offset,
                    pos_y * y_distance);

                // clear the vector and put the point into the empty vector
                fellow.connectionPoints.clear();
                fellow.connectionPoints.add(point);

                // hide the added point
                point.hide();
            } // else if
        } // while
    } // method processDiversion

    /**
     * This method marks all edges for the edge layout.
     */
    private void markEdges() {
        Iterator<AbstractEdge> edges;
        AbstractEdge edge;
        AbstractNode parentNode;
        AbstractNode childNode;
        String isOuterNode;
        int edge_x;
        int edge_y;

        int pos_y[] = new int[2];

        // initialise the iterator for the loop
        edges = abstractGraph.getEdges().iterator();
        // process all edges
        while (edges.hasNext()) {
            edge = (AbstractEdge) edges.next();

            // get the parent node and the child node
            parentNode = edge.getEnd1();
            childNode = edge.getEnd2();

            // get the y positions of the nodes
            pos_y[0] = (int) ((Fellow) parentNode.getFellow()).y;
            pos_y[1] = (int) ((Fellow) childNode.getFellow()).y;

            // if the edge makes a self call
            if (parentNode == childNode)
                edge.setFellow(new Fellow(Fellow.SELF_CALL));
            // if the parent node and the child node are on the same level
            else if (pos_y[0] == pos_y[1])
                edge.setFellow(new Fellow(
                    Fellow.INNER_DIVERSION));
            // if the parent node and the child node are outer nodes on the west
            // side
            // and the edge is a call back or
            // if the parent node and the child node are outer nodes on the west
            // side
            // and part of a complex graph
            else if ((isOuterNode = isOuterNode(parentNode)) != null
                && isOuterNode.contains(west)
                && (isOuterNode = isOuterNode(childNode)) != null
                && isOuterNode.contains(west)
                && (parentNode.getY() < childNode.getY() || (!isSimpleTree(
                    parentNode, false) || !isSimpleTree(childNode, true)))) {
                // create a new fellow
                Fellow fellow = new Fellow(Fellow.OUTER_DIVERSION);
                // set the annotation of the fellow
                fellow.annotation = west;
                // put the new fellow to the edge
                edge.setFellow(fellow);
            } // else if
              // if the parent node and the child node are outer nodes on the
              // east side
              // and the edge is a call back or
              // if the parent node and the child node are outer nodes on the
              // east side
              // and part of a complex graph
            else if ((isOuterNode = isOuterNode(parentNode)) != null
                && isOuterNode.contains(east)
                && (isOuterNode = isOuterNode(childNode)) != null
                && isOuterNode.contains(east)
                && (parentNode.getY() < childNode.getY() || (!isSimpleTree(
                    parentNode, false) || !isSimpleTree(childNode, true)))) {
                // create a new fellow
                Fellow fellow = new Fellow(Fellow.OUTER_DIVERSION);
                // set the annotation of the fellow
                fellow.annotation = east;
                // put the new fellow to the edge
                edge.setFellow(fellow);
            } // else if
              // if the parent node and the child node are outer nodes on the
              // south side
              // and the edge is a call back or
              // if the parent node and the child node are outer nodes on the
              // south side
              // and part of a complex graph
            else if ((isOuterNode = isOuterNode(parentNode)) != null
                && isOuterNode.contains(south)
                && (isOuterNode = isOuterNode(childNode)) != null
                && isOuterNode.contains(south)
                && (parentNode.getY() < childNode.getY() || (!isSimpleTree(
                    parentNode, false) || !isSimpleTree(childNode, true)))) {
                // create a new fellow
                Fellow fellow = new Fellow(Fellow.OUTER_DIVERSION);
                // set the annotation of the fellow
                fellow.annotation = south;
                // put the new fellow to the edge
                edge.setFellow(fellow);
            } // else if
              // if the parent node and the child node are outer nodes on the
              // north side
              // and the edge is a call back or
              // if the parent node and the child node are outer nodes on the
              // north side
              // and part of a complex graph
            else if ((isOuterNode = isOuterNode(parentNode)) != null
                && isOuterNode.contains(north)
                && (isOuterNode = isOuterNode(childNode)) != null
                && isOuterNode.contains(north)
                && (parentNode.getY() < childNode.getY() || (!isSimpleTree(
                    parentNode, false) || !isSimpleTree(childNode, true)))) {
                // create a new fellow
                Fellow fellow = new Fellow(Fellow.OUTER_DIVERSION);
                // set the annotation of the fellow
                fellow.annotation = north;
                // put the new fellow to the edge
                edge.setFellow(fellow);
            } // else if
              // if the edge should be handled as incoming
            else if (parentNode.getEdgesOutgoing().size() <= childNode
                .getEdgesIncoming().size()) {
                // if the edge jumps over not more than one row
                if (Math.abs(pos_y[0] - pos_y[1]) <= 1)
                    // process the next edge
                    continue;
                // if the edge is a simple tree
                if (isSimpleTree(childNode, true))
                    // process the next edge
                    continue;
                // if the edge jumps over two or more rows and the edge is
                // directed to the north
                if (childNode.getY() > parentNode.getY()) {
                    edge_x = (int) ((Fellow) parentNode.getFellow()).x;
                    edge_y = (int) ((Fellow) childNode.getFellow()).y - 1;
                    edge.setFellow(new Fellow(Fellow.INCOMING, edge_x, edge_y));
                } // if
                  // if the edge jumps over two or more rows and the edge is
                  // directed to the south
                else {
                    edge_x = (int) ((Fellow) parentNode.getFellow()).x;
                    edge_y = (int) ((Fellow) childNode.getFellow()).y + 1;
                    edge.setFellow(new Fellow(Fellow.INCOMING, edge_x, edge_y));
                } // else
            } // else if
              // if the edge should be handled as outgoing
            else if (parentNode.getEdgesOutgoing().size() > childNode
                .getEdgesIncoming().size()) {
                // if the edge jumps over not more than one row
                if (Math.abs(pos_y[0] - pos_y[1]) <= 1)
                    // process the next edge
                    continue;
                // if the edge is a simple tree
                if (isSimpleTree(parentNode, false))
                    // process the next edge
                    continue;
                // if the edge jumps over two or more rows and the edge is
                // directed to the north
                if (childNode.getY() > parentNode.getY()) {
                    edge_x = (int) ((Fellow) childNode.getFellow()).x;
                    edge_y = (int) ((Fellow) parentNode.getFellow()).y + 1;
                    edge.setFellow(new Fellow(Fellow.OUTGOING, edge_x, edge_y));
                } // if
                  // if the edge jumps over two or more rows and the edge is
                  // directed to the south
                else {
                    edge_x = (int) ((Fellow) childNode.getFellow()).x;
                    edge_y = (int) ((Fellow) parentNode.getFellow()).y - 1;
                    edge.setFellow(new Fellow(Fellow.OUTGOING, edge_x, edge_y));
                } // else
            } // else if
        } // while
    } // method markEdges

    /**
     * This method unmarks all edges.
     */
    private void unmarkEdges() {
        // initialise the iterator for the loop
        Iterator<AbstractEdge> edges = abstractGraph.getEdges().iterator();
        // unmark all edges
        while (edges.hasNext())
            // set the fellow of the next edge
            edges.next().setFellow(null);
    } // method unmarkEdges

    /**
     * This method returns if the node is an outer node or not.
     * 
     * @param node
     * @return String
     */
    private String isOuterNode(AbstractNode node) {
        Fellow fellow = (Fellow) node.getFellow();

        // create a local dim_x
        int dim_x = this.dim_x - 1;
        // create a local dim_y
        int dim_y = this.dim_y - 1;
        // initialise the result String
        String result = west + east + south + north;

        // check the left side
        for (int i = X0 + (int) fellow.x - 1; i > 0; i--)
            // replace the keyword in the result String and break the loop
            // if the current element is not free or reserved
            if (elementMap[i][Y0 + (int) fellow.y] != free
                && elementMap[i][Y0 + (int) fellow.y] != reserved) {
                result = result.replace(west, "");
                break;
            } // if

        // check the right side
        for (int i = X0 + (int) fellow.x + 1; i < dim_x; i++)
            // replace the keyword in the result String and break the loop
            // if the current element is not free or reserved
            if (elementMap[i][Y0 + (int) fellow.y] != free
                && elementMap[i][Y0 + (int) fellow.y] != reserved) {
                result = result.replace(east, "");
                break;
            } // if

        // check the lower side
        for (int i = Y0 + (int) fellow.y - 1; i > 0; i--)
            // replace the keyword in the result String and break the loop
            // if the current element is not free or reserved
            if (elementMap[X0 + (int) fellow.x][i] != free
                && elementMap[X0 + (int) fellow.x][i] != reserved) {
                result = result.replace(south, "");
                break;
            } // if

        // check the upper side
        for (int i = Y0 + (int) fellow.y + 1; i < dim_y; i++)
            // replace the keyword in the result String and break the loop
            // if the current element is not free or reserved
            if (elementMap[X0 + (int) fellow.x][i] != free
                && elementMap[X0 + (int) fellow.x][i] != reserved) {
                result = result.replace(north, "");
                break;
            } // if

        // return null if the node is not an outer node
        if (result.equals(""))
            return null;

        // return the result if the node is an outer node
        return result;
    } // method isOuterNode

    /**
     * This method updates the abstract graph.
     */
    public void update() {
        Iterator<AbstractNode> nodes = abstractGraph.getNodes().iterator();
        Iterator<AbstractEdge> edges = abstractGraph.getEdges().iterator();

        // update nodes
        while (nodes.hasNext())
            ((AbstractNode) nodes.next()).updateGlyph();

        // update edges
        while (edges.hasNext())
            ((AbstractEdge) edges.next()).updateGlyph();
    } // method update
} // class RasterGraphLayout

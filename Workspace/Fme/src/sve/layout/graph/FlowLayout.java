/**
 * STRL Visualization Engine Raster Chart Layout
 * 
 * Stefan Natelberg De Montfort University Software Technology Research
 * Laboratory Leicester - United Kingdom
 */

package sve.layout.graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import sve.engine.MainDisplay;
import sve.layout.abstraction.AbstractGraphLayout;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;
import sve.structures.general.ConnectionPoint;

/**
 * This layout algorithm arranges the nodes of an abstract graph in an
 * orthogonal raster based on the rules of a flow chart.
 */
public class FlowLayout
    extends AbstractGraphLayout {

    /**
     * This final variable is the name of the algorithm.
     */
    public final String name = "RasterChartLayout Algorithm";

    /**
     * This final variable is the company which uses the algorithm.
     */
    public final String company = "Software Technology Research Laboratory";

    /**
     * This final variable is a weblink.
     */
    public final String weblink = "www.dmu.ac.uk";

    /**
     * This final variable is the version number of the algorithm.
     */
    public final double version = 0.4;

    /**
     * This string buffer contains the protocol of the algorithm.
     */
    public StringBuffer protocol = new StringBuffer();

    /**
     * This is the abstract graph.
     */
    private AbstractGraph abstractGraph;

    /**
     * These are the start nodes of the graph.
     */
    private Vector<AbstractNode> startNodes;

    /**
     * This is the distance x between the nodes.
     */
    private long distance_x;

    /**
     * This is the distance y between the nodes.
     */
    private long distance_y;

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
     * This is the value Y0 in the area.
     */
    private int Y0;

    /**
     * This is the element map which stores the position of the nodes.
     */
    private int elementMap[][];

    /**
     * This array saves the allocated lines in the dimension x.
     */
    private boolean allocated_x[];

    /**
     * This array saves the allocated lines in the dimension y.
     */
    private boolean allocated_y[];

    /**
     * This array saves the number of edges in the dimension x.
     */
    private int edgeNumber_x[];

    /**
     * This array saves the number of edges in the dimension y.
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
    private final int reserved = -2;

    /**
     * This is the int code for an engaged element.
     */
    private final int engaged = -3;

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
    public FlowLayout() {
        protocol.append(new Date().toString() + "\n\n" + name + " v" + version
            + "\n" + company + "\n" + weblink + "\n\n");
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
        int nodeSize;
        long largestNodeSize[] = new long[2];

        // initialise the start nodes
        initialiseStartNodes();

        // get the size of the node vector
        nodeSize = abstractGraph.getNodes().size();

        // initialise the iterator for the loop
        nodes = abstractGraph.getNodes().iterator();
        // calculate the largest node size
        while (nodes.hasNext()) {
            node = nodes.next();
            // set the x dimension (width)
            largestNodeSize[x] = Math.max(largestNodeSize[x], node.getWidth());
            // set the y dimension (height)
            largestNodeSize[y] = Math.max(largestNodeSize[y], node.getHeight());
        }

        // calculate the x distance between the nodes
        distance_x = 2 * largestNodeSize[x] + 100;
        // calculate the y distance between the nodes
        distance_y = 2 * largestNodeSize[y] + 100;
        // calculate the distance between two edges
        edge_distance = 15;

        // initialise the size of the dimensions x and y
        dim_x = Math.max(nodeSize * 2, 10);
        dim_y = Math.max(nodeSize * 2, 10);

        // initialise the coordinate Y0
        Y0 = dim_y / 2;

        // initialise the size of the arrays
        elementMap = new int[dim_x][dim_y];
        allocated_x = new boolean[dim_x];
        allocated_y = new boolean[dim_y];
        edgeNumber_x = new int[dim_x];
        edgeNumber_y = new int[dim_y];

        // initialise the values of the element map
        for (int i = 0; i < dim_x; i++)
            for (int j = 0; j < dim_y; j++)
                elementMap[i][j] = free;

        // initialise the values of the allocated lines in the dimension x
        for (int i = 0; i < dim_x; i++)
            allocated_x[i] = false;

        // initialise the values of the allocated lines in the dimension y
        for (int i = 0; i < dim_y; i++)
            allocated_y[i] = false;

        // initialise the values of the number of edges in the dimension x
        for (int i = 0; i < dim_x; i++)
            edgeNumber_x[i] = 0;

        // initialise the values of the number of edges in the dimension y
        for (int i = 0; i < dim_y; i++)
            edgeNumber_y[i] = 0;

        // if there are nodes
        if (nodeSize != 0) {
            processNodes();
            nodePostOptimizer();
        }

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

        // <TEMP>
        AbstractEdge e;
        ConnectionPoint p;
        Iterator<AbstractEdge> i;

        // Find Start Nodes
        i = abstractGraph.getEdges().iterator();
        while (i.hasNext()) {
            e = i.next();

            // Case1:
            // Node1 is above Node2
            // if (e.getEnd1().getX() == e.getEnd2().getX()
            // && e.getEnd1().getY() > e.getEnd2().getY()) {
            // // Do nothing
            // }
            // Case2:
            // Node1 is the N edge of a decision
            if (e.getEnd1().getX() < e.getEnd2().getX()
                && e.getEnd1().getY() > e.getEnd2().getY()) {

                e.setEnd1ConnectionPoint(AbstractEdge.EAST);
                e.setEnd2ConnectionPoint(AbstractEdge.NORTH);

                p = e.addConnectionPoint(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd1ConnectionPoint().getY());
                p.fixTranslation(e.getEnd2().getX(), e.getEnd1().getY());
                e.updateGlyph();
                e.hideLinePoints();
            }
            // Case3:
            // Node2 is above Node1
            else if ((e.getEnd1().getX() == e.getEnd2().getX() && e.getEnd1().getY() < e
                .getEnd2().getY())
                || (e.getEnd1().getX() < e.getEnd2().getX() && e.getEnd1().getY() < e
                    .getEnd2().getY())) {

                long xcp;

                e.setEnd1ConnectionPoint(AbstractEdge.WEST);
                e.setEnd2ConnectionPoint(AbstractEdge.WEST);

                if (e.getEnd2ConnectionPoint().getX() > e.getEnd1ConnectionPoint()
                    .getX())
                    xcp = e.getEnd1ConnectionPoint().getX();
                else
                    xcp = e.getEnd2ConnectionPoint().getX();

                p = e.addConnectionPoint(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd1ConnectionPoint().getY());
                p.fixTranslation(xcp - 100, e.getEnd2ConnectionPoint().getY());
                p = e.addConnectionPoint(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd1ConnectionPoint().getY());
                p.fixTranslation(xcp - 100, e.getEnd1ConnectionPoint().getY());
                e.updateGlyph();
                e.hideLinePoints();
            }
            // Case4:
            // Node2 is above and right to Node1
            else if (e.getEnd1().getX() > e.getEnd2().getX()
                && e.getEnd1().getY() < e.getEnd2().getY()) {

                e.setEnd1ConnectionPoint(AbstractEdge.EAST);
                e.setEnd2ConnectionPoint(AbstractEdge.NORTH);

                p = e.addConnectionPoint(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd1ConnectionPoint().getY());
                p.fixTranslation(e.getEnd2ConnectionPoint().getX(), e
                    .getEnd2ConnectionPoint().getY() + 50);

                p = e.addConnectionPoint(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd1ConnectionPoint().getY());
                p.fixTranslation(e.getEnd1ConnectionPoint().getX() + 100, e
                    .getEnd2ConnectionPoint().getY() + 50);

                p = e.addConnectionPoint(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd1ConnectionPoint().getY());
                p.fixTranslation(e.getEnd1ConnectionPoint().getX() + 100, e
                    .getEnd1ConnectionPoint().getY());

                e.updateGlyph();
                e.hideLinePoints();
            }
            // Case5:
            // Node2 is under and left to Node1
            else if (e.getEnd1().getX() > e.getEnd2().getX()
                && e.getEnd1().getY() > e.getEnd2().getY()) {

                e.setEnd1ConnectionPoint(AbstractEdge.SOUTH);
                e.setEnd2ConnectionPoint(AbstractEdge.EAST);

                p = e.addConnectionPoint(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd1ConnectionPoint().getY());
                p.fixTranslation(e.getEnd1ConnectionPoint().getX(), e
                    .getEnd2ConnectionPoint().getY());

                e.updateGlyph();
                e.hideLinePoints();
            } else {
                Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.WARNING,
                    "Can't estimate how to draw the edge from node:"
                        + e.getEnd1().getName());
            }
        }
        // </TEMP>

        // update the abstract graph
        update();

        // calculate the time consumption
        edgeLayoutTime = System.nanoTime() - edgeLayoutTime;
    }

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
            + name + " v" + version + "\n" + "\n " + company + "\n " + weblink
            + "\n --------------------------------------------------\n" + "\n "
            + new Date().toString() + "\n" + "\n Number of Nodes: "
            + abstractGraph.getNodes().size() + "\n Number of Start Nodes: "
            + startNodes.size() + "\n Number of Edges: "
            + abstractGraph.getEdges().size() + "\n"
            + "\n Distance Ratio (X / Y): "
            + ((double) distance_x / (double) distance_y) + "\n Node X-Distance: "
            + distance_x + "\n Node Y-Distance: " + distance_y
            + "\n Edge Distance: " + edge_distance + "\n"
            + "\n Array Dimension: x = " + dim_x + ", y = " + dim_y
            + "\n Allocated Elements: x = " + countAllocatedX() + ", y = "
            + countAllocatedY() + "\n" + "\n Time Consumption (Node Layout): "
            + nodeLayoutTimeMs + "ms" + "\n Time Consumption (Edge Layout): "
            + edgeLayoutTimeMs + "ms" + "\n Time Consumption (Total): "
            + totalTimeMs + "ms\n"
            + "\n --------------------------------------------------\n" + "\n";

        return returnString;
    }

    /**
     * This method writes the protocol into the file.
     * 
     * @param file
     */
    public void createProtocol(File file) {
        try {
            // create a new file output stream
            OutputStream fos = new FileOutputStream(file);
            // write the string into the file
            fos.write(protocol.toString().getBytes());
            // close the file output stream
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Internal Methods
    // ================

    private int getElement(int pos_x, int pos_y) {
        return elementMap[pos_x][Y0 + pos_y];
    }

    private void setElement(int pos_x, int pos_y, int value) {
        elementMap[pos_x][Y0 + pos_y] = value;
    }

    private boolean isAllocatedX(int pos_x) {
        return allocated_x[pos_x];
    }

    private void setAllocatedX(int pos_x) {
        allocated_x[pos_x] = true;
    }

    private boolean isAllocatedY(int pos_y) {
        return allocated_y[Y0 + pos_y];
    }

    private void setAllocatedY(int pos_y) {
        allocated_y[Y0 + pos_y] = true;
    }

    private int countAllocatedX() {
        int counter;

        counter = 0;
        for (int i = 0; i < dim_x; i++)
            if (isAllocatedX(i))
                counter++;

        return counter;
    }

    private int countAllocatedY() {
        int counter;

        counter = 0;
        for (int i = -Y0; i < dim_y - Y0; i++)
            if (isAllocatedY(i))
                counter++;

        return counter;
    }

    /**
     * This method initialises the vector which contains the start nodes.
     */
    private void initialiseStartNodes() {
        Iterator<AbstractNode> nodes;
        AbstractNode node;

        // create a new vector
        startNodes = new Vector<AbstractNode>();

        // initialise the iterator for the loop
        nodes = abstractGraph.getNodes().iterator();
        // search for start nodes
        while (nodes.hasNext()) {
            // get the next node
            node = nodes.next();

            // process the next node if the node name is null
            if (node.getName() == null)
                continue;

            // add the node to the start nodes if the name equals 'start'
            if (node.getName().equals("start"))
                startNodes.add(node);

            // add the node to the start nodes if the name starts with '-'
            if (node.getName().startsWith("-"))
                startNodes.add(node);

            // add the node to the start nodes if there are no incoming edges
            if (node.getEdgesIncoming().size() == 0)
                startNodes.add(node);
        }
    }

    /**
     * This method returns the next start node or null.
     * 
     * @return AbstractNode
     */
    private AbstractNode getNextStartNode() {
        Iterator<AbstractNode> nodes;
        AbstractNode node;

        // initialise the iterator for the loop
        nodes = startNodes.iterator();
        // search for the next start node
        while (nodes.hasNext()) {
            // get the next node
            node = nodes.next();

            // return the node if the fellow is null
            if (node.getFellow() == null)
                return node;
        }

        // return null if no start node has been found
        return null;
    }

    /**
     * This method returns the next node or null.
     * 
     * @return AbstractNode
     */
    private AbstractNode getNextNode() {
        Iterator<AbstractNode> nodes;
        AbstractNode node;

        // initialise the iterator for the loop
        nodes = abstractGraph.getNodes().iterator();
        // search for the next node
        while (nodes.hasNext()) {
            // get the next node
            node = nodes.next();

            // return the node if the fellow is null
            if (node.getFellow() == null)
                return node;
        }

        // return null if no node has been found
        return null;
    }

    /**
     * This method processes the nodes.
     */
    private void processNodes() {
        AbstractNode node;
        int startPosition;
        Vector<AbstractNode> linkedNodes;

        // get the next start node
        node = getNextStartNode();

        // process the nodes
        while (node != null) {
            startPosition = getNextStartPosition();

            // process the graph
            linkedNodes = processGraph(node, true, startPosition, 0);

            while (!linkedNodes.isEmpty())
                linkedNodes = processLinkedNodes(linkedNodes);

            // get the next start node
            node = getNextStartNode();
        }

        // get the next node
        node = getNextNode();

        // process the nodes which have no defined start node
        while (node != null) {
            startPosition = getNextStartPosition();

            // process the graph
            linkedNodes = processGraph(node, true, startPosition, 0);

            while (!linkedNodes.isEmpty())
                linkedNodes = processLinkedNodes(linkedNodes);

            // get the next node
            node = getNextNode();
        }
    }

    private int getNextStartPosition() {
        int nextStartPosition = 0;

        while (isAllocatedX(nextStartPosition))
            nextStartPosition++;

        if (nextStartPosition != 0) {
            setAllocatedX(nextStartPosition);
            nextStartPosition++;
        }

        return nextStartPosition;
    }

    /**
     * This method places a node and creates the layout for a single graph.
     * 
     * @param node
     * @param placeNode
     * @param pos_x
     * @param pos_y
     */
    private Vector<AbstractNode> processGraph(AbstractNode node,
        boolean placeNode, int pos_x, int pos_y) {
        Iterator<AbstractEdge> edgesOutgoing;
        Iterator<AbstractEdge> edgesIncoming;
        Vector<AbstractNode> linkedNodes;

        if (placeNode)
            placeNode(node, pos_x, pos_y);

        linkedNodes = new Vector<AbstractNode>();

        edgesOutgoing = node.getEdgesOutgoing().iterator();
        while (edgesOutgoing.hasNext())
            placeNode(edgesOutgoing.next().getEnd2(), pos_x,
                pos_y - 1);

        edgesOutgoing = node.getEdgesOutgoing().iterator();
        while (edgesOutgoing.hasNext())
            linkedNodes.addAll(processNode(edgesOutgoing.next().getEnd2(), node));

        edgesIncoming = node.getEdgesIncoming().iterator();
        while (edgesIncoming.hasNext())
            linkedNodes.add(edgesIncoming.next().getEnd1());

        return linkedNodes;
    }

    private void placeNode(AbstractNode node, int pos_x, int pos_y) {
        int element;

        // if the node has already been placed
        if (node.getFellow() != null
            && ((Fellow) node.getFellow()).mode == Fellow.PLACED)
            return;

        // if the node has already been processed
        if (node.getFellow() != null
            && ((Fellow) node.getFellow()).mode == Fellow.PROCESSED)
            return;

        // go on here if the selected element is not free
        int offset = 0;
        // do the loop until a free place has been found
        while (true) {
            // start from the origin and test the right side if it is free
            if (isFreeTEMP(node, pos_x + offset, pos_y)) {
                // calculate the new x position in the area
                pos_x = pos_x + offset;

                // fix the node
                node.fixTranslation((long) pos_x * (long) distance_x, (long) pos_y
                    * (long) distance_y);

                // allocate the elements
                allocateElements(node, pos_x, pos_y, offset);

                // set the fellow
                node.setFellow(new Fellow(Fellow.PLACED, pos_x, pos_y));

                element = getElement(pos_x, pos_y + 1);
                if (offset != 0 && element != free && element != reserved
                    && element != engaged) {
                    node = abstractGraph.getNodes().get(element);
                    setElement(pos_x, pos_y + 1, engaged);
                    node.setFellow(null);
                    placeNode(node, pos_x, pos_y + 1);
                }

                return;
            }

            // increase the index offset
            offset++;
        }
    }

    private Vector<AbstractNode> processNode(AbstractNode node,
        AbstractNode parentNode) {
        // if the node has already been processed
        if (node.getFellow() != null
            && ((Fellow) node.getFellow()).mode == Fellow.PROCESSED)
            return new Vector<AbstractNode>();

        int pos_x = (int) ((Fellow) node.getFellow()).x;
        int pos_y = (int) ((Fellow) node.getFellow()).y;

        // set the fellow
        ((Fellow) node.getFellow()).mode = Fellow.PROCESSED;

        return processGraph(node, false, pos_x, pos_y);
    }

    private Vector<AbstractNode> processLinkedNodes(
        Vector<AbstractNode> linkedNodes) {
        Iterator<AbstractNode> nodes;
        Iterator<AbstractNode> reachableNodes;
        AbstractNode node;

        // initialise the iterator for the loop
        nodes = linkedNodes.iterator();

        // create a new vector
        // WARNING: The original vector will not be cleared
        linkedNodes = new Vector<AbstractNode>();

        // search for start nodes
        while (nodes.hasNext()) {
            // get the next node
            node = nodes.next();

            if (node.getFellow() != null)
                continue;

            reachableNodes = getReachableNodes(node).iterator();
            resetReachedNodes();
            while (reachableNodes.hasNext()) {
                node = reachableNodes.next();

                // process the next node if the node name is null
                if (node.getName() == null)
                    continue;

                if (node.getFellow() != null)
                    continue;

                // add the node to the start nodes if the name equals 'start'
                if (node.getName().equals("start"))
                    linkedNodes.addAll(processGraph(
                        node, true, getNextStartPosition(), 0));

                // add the node to the start nodes if the name starts with '-'
                if (node.getName().startsWith("-"))
                    linkedNodes.addAll(processGraph(
                        node, true, getNextStartPosition(), 0));

                // add the node to the start nodes if there are no incoming
                // edges
                if (node.getEdgesIncoming().size() == 0)
                    linkedNodes
                        .addAll(processGraph(node, true, getNextStartPosition(), 0));
            }
        }

        return linkedNodes;
    }

    // THIS METHOD NEEDS TO BE CHECKED!
    private void nodePostOptimizer() {
        Iterator<AbstractNode> nodes = abstractGraph.getNodes().iterator();
        Iterator<AbstractEdge> edges;
        AbstractNode node;
        AbstractEdge edge;
        int pos_x;
        int pos_y;
        boolean optimal;

        while (nodes.hasNext()) {
            node = nodes.next();

            if (node.getEdgesOutgoing().size() > 1) {
                optimal = false;
                edges = node.getEdgesOutgoing().iterator();
                while (edges.hasNext()) {
                    edge = edges.next();

                    if (((Fellow) edge.getEnd2().getFellow()).x == ((Fellow) node
                        .getFellow()).x) {
                        optimal = true;
                        break;
                    } // if
                }

                if (!optimal) {
                    edges = node.getEdgesOutgoing().iterator();
                    while (edges.hasNext()) {
                        edge = edges.next();

                        pos_x = (int) ((Fellow) edge.getEnd2().getFellow()).x;
                        pos_y = (int) ((Fellow) node.getFellow()).y;
                        if (getElement(pos_x, pos_y) == free
                            || getElement(pos_x, pos_y) == reserved
                            || getElement(pos_x, pos_y) == engaged) {
                            node.fixTranslation((long) pos_x * (long) distance_x,
                                (long) pos_y * (long) distance_y);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * This method allocates the elements for the node.
     * 
     * @param node
     * @param pos_x
     * @param pos_y
     * @param offset
     */
    private void allocateElements(AbstractNode node, int pos_x, int pos_y,
        int allocationNumber) {
        // allocate the element for the node
        setElement(pos_x, pos_y, abstractGraph.getNodes().indexOf(node));

        protocol.append("node: pos_x = " + pos_x + ", pos_y = " + pos_y + "\n");

        // allocate the line in the dimension x
        setAllocatedX(pos_x);

        // allocate the line in the dimension y
        setAllocatedY(pos_y);

        // get the number of allocated elements
        allocationNumber = node.getEdgesOutgoing().size();

        // process allocation
        for (int i = 1; i < allocationNumber; i++) {
            // allocate the element if it is free
            if (getElement(pos_x + i, pos_y) == free) {
                setElement(pos_x + i, pos_y, reserved);
                setAllocatedX(pos_x + i);
            }
        }
    }

    private Vector<AbstractNode> getReachableNodes(AbstractNode node) {
        Vector<AbstractNode> reachableNodes = new Vector<AbstractNode>();
        Iterator<AbstractEdge> edgesIncoming;
        Iterator<AbstractEdge> edgesOutgoing;

        if (isReached(node))
            return new Vector<AbstractNode>();

        setReached(node);

        reachableNodes.add(node);

        edgesIncoming = node.getEdgesIncoming().iterator();
        while (edgesIncoming.hasNext())
            reachableNodes.addAll(getReachableNodes(edgesIncoming.next().getEnd1()));

        edgesOutgoing = node.getEdgesOutgoing().iterator();
        while (edgesOutgoing.hasNext())
            reachableNodes.addAll(getReachableNodes(edgesOutgoing.next().getEnd2()));

        return reachableNodes;
    }

    private boolean isReached(AbstractNode node) {
        if (node.getFellow() != null && ((Fellow) node.getFellow()).reached)
            return true;
        return false;
    }

    private void setReached(AbstractNode node) {
        if (node.getFellow() == null) {
            Fellow fellow = new Fellow(Fellow.UNDEFINED);
            fellow.reached = true;
            node.setFellow(fellow);
        } else
            ((Fellow) node.getFellow()).reached = true;
    }

    private void resetReachedNodes() {
        Iterator<AbstractNode> nodes = abstractGraph.getNodes().iterator();
        AbstractNode node;

        while (nodes.hasNext()) {

            node = nodes.next();

            if (node.getFellow() == null)
                continue;

            if (((Fellow) node.getFellow()).mode == Fellow.UNDEFINED)
                node
                    .setFellow(null);
            else
                ((Fellow) node.getFellow()).reached = false;
        }
    }

    private boolean isFreeTEMP(AbstractNode node, int pos_x, int pos_y) {
        int depth = getDepthTEMP(node);

        for (int i = 0; i < depth; i++)
            if (getElement(pos_x, pos_y - i) != free)
                return false;

        return true;
    }

    private int getDepthTEMP(AbstractNode node) {

        int counter = 2;

        while (node.getFellow() == null && !node.getEdgesOutgoing().isEmpty()
            && !isReached(node)) {
            setReached(node);
            counter++;
            node = ((AbstractEdge) node.getEdgesOutgoing().firstElement()).getEnd2();
        }

        resetReachedNodes();

        return counter;
    }

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
    }
}

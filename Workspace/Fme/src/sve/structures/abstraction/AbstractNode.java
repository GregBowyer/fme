/**
 * Project: sve
 */

package sve.structures.abstraction;

import java.util.*;

/**
 * This class represents a general node
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

/**
 * @todo Description
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */
public abstract class AbstractNode
    extends AbstractGraphElement {

  /**
   * A list of all edges
   */
  private Vector<AbstractEdge> edges = new Vector<AbstractEdge>();

  /**
   * A list of all edges
   */
  private Vector<AbstractEdge> edgesIncoming = new Vector<AbstractEdge>();

  /**
   * A list of all edges
   */
  private Vector<AbstractEdge> edgesOutgoing = new Vector<AbstractEdge>();

  /**
   * The Constructor
   * 
   * @param owner The owner of this element (The UML Diagram)
   */
  public AbstractNode(AbstractGraph owner) {
    super(owner);
  }

  /**
   * Gets the with of this UML Element
   * 
   * @return Returns the width of this object
   */
  public abstract long getWidth();

  /**
   * Gets the height of this UML Element
   * 
   * @return Returns the height of this object
   */
  public abstract long getHeight();

  /**
   * Get a set of all edges of this node
   * 
   * @return A list of all edges
   */
  public Vector<AbstractEdge> getEdges() {
    return edges;
  }

  /**
   * Adds an edge to this node
   * 
   * @param a A edge which is connected to this node
   */
  public void addEdge(AbstractEdge a) {
//  edges.insertElementAt(a,edges.size());
    edges.insertElementAt(a,edges.size());
    if (a.getEnd2().equals(this)) edgesIncoming.insertElementAt(a,edgesIncoming.size());
    else edgesOutgoing.insertElementAt(a,edgesOutgoing.size());
  }

  /**
   * Get all incoming edges
   * 
   * @return A list of all incoming edges
   */
  public Vector<AbstractEdge> getEdgesIncoming() {
    return edgesIncoming;
  }

  /**
   * Get all outgoing edges
   * 
   * @return A list of all outgoing edges
   */
  public Vector<AbstractEdge> getEdgesOutgoing() {
    return edgesOutgoing;
  }
}

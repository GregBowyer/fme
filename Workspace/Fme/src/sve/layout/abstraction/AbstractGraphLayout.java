/**
 * STRL Visualization Engine
 * Abstract Graph Layout
 * 
 * Stefan Natelberg
 * De Montfort University
 * Software Technology Research Laboratory
 * Leicester - United Kingdom
 */

package sve.layout.abstraction;

import java.util.*;

import sve.engine.*;
import sve.structures.abstraction.*;
import sve.structures.general.*;

/**
 * This class represents an abstract layout algorithm
 */
public abstract class AbstractGraphLayout {
  /**
   * Applies the layout algorithm to nodes of a graph.
   * 
   * @param ag The graph to layout
   * @param md The MainDisplay which presents the graph
   */
  public abstract void doLayoutNodes(AbstractGraph ag, MainDisplay md);

  /**
   * Applies the layout algorithm to edges of a graph.
   * 
   * @param ag The graph to layout
   * @param md The MainDisplay which presents the graph
   */
  public abstract void doLayoutEdges(AbstractGraph ag, MainDisplay md);
  
  /**
   * This is a fellow which can be attached to a glyph.
   */
  public class Fellow {
    /**
     * This is the int code for the mode 'undefined'.
     */
    public static final int UNDEFINED = Integer.MIN_VALUE;

    /**
     * This is the int code for the mode 'self call'.
     */
    public static final int SELF_CALL = 10;

    /**
     * This is the int code for the mode 'inner diversion'.
     */
    public static final int INNER_DIVERSION = 100;

    /**
     * This is the int code for the mode 'outer diversion'.
     */
    public static final int OUTER_DIVERSION = 200;

    /**
     * This is the int code for the mode 'incoming'.
     */
    public static final int INCOMING = 1000;

    /**
     * This is the int code for the mode 'outgoing'.
     */
    public static final int OUTGOING = 2000;

    /**
     * This is the int code for the mode 'placed'.
     */
    public static final int PLACED = 10000;

    /**
     * This is the int code for the mode 'processed'.
     */
    public static final int PROCESSED = 20000;

    /**
     * This is the int code for the mode 'marked'.
     */
    public static final int MARKED = Integer.MAX_VALUE;

    /**
     * This is a public int variable.
     */
    public int mode;

    /**
     * This is a public long variable.
     */
    public long x;

    /**
     * This is a public long variable.
     */
    public long y;

    /**
     * This is a public long variable.
     */
    public long x1;

    /**
     * This is a public long variable.
     */
    public long y1;

    /**
     * This is a public long variable.
     */
    public long x2;

    /**
     * This is a public long variable.
     */
    public long y2;

    /**
     * This is a public boolean variable.
     */
    public boolean reached;

    /**
     * This is a public String variable.
     */
    public String annotation;

    /**
     * This public vector contains the connection points.
     */
    public Vector <ConnectionPoint>connectionPoints =
        new Vector<ConnectionPoint>();

    /**
     * Constructor.
     * 
     * @param mode
     */
    public Fellow(int mode) {
        this.mode = mode;
        x = UNDEFINED;
        y = UNDEFINED;
        x1 = UNDEFINED;
        y1 = UNDEFINED;
        x2 = UNDEFINED;
        y2 = UNDEFINED;
        reached = false;
        annotation = null;
    } 

    /**
     * Constructor.
     * 
     * @param x
     * @param y
     */
    public Fellow(int x, int y) {
        mode = UNDEFINED;
        this.x = x;
        this.y = y;
        x1 = UNDEFINED;
        y1 = UNDEFINED;
        x2 = UNDEFINED;
        y2 = UNDEFINED;
        reached = false;
        annotation = null;
    }

    /**
     * Constructor.
     * 
     * @param x
     * @param y
     */
    public Fellow(long x, long y) {
        mode = UNDEFINED;
        this.x = x;
        this.y = y;
        x1 = UNDEFINED;
        y1 = UNDEFINED;
        x2 = UNDEFINED;
        y2 = UNDEFINED;
        reached = false;
        annotation = null;
    } 

    /**
     * Constructor.
     * 
     * @param mode
     * @param x
     * @param y
     */
    public Fellow(int mode, int x, int y) {
        this.mode = mode;
        this.x = x;
        this.y = y;
        x1 = UNDEFINED;
        y1 = UNDEFINED;
        x2 = UNDEFINED;
        y2 = UNDEFINED;
        reached = false;
        annotation = null;
    } 

    /**
     * Constructor.
     * 
     * @param mode
     * @param x
     * @param y
     */
    public Fellow(int mode, long x, long y) {
        this.mode = mode;
        this.x = x;
        this.y = y;
        x1 = UNDEFINED;
        y1 = UNDEFINED;
        x2 = UNDEFINED;
        y2 = UNDEFINED;
        reached = false;
        annotation = null;
    } 

    /**
     * Constructor.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Fellow(int x1, int y1, int x2, int y2) {
        mode = UNDEFINED;
        x = UNDEFINED;
        y = UNDEFINED;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        reached = false;
        annotation = null;
    } 

    /**
     * Constructor.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Fellow(long x1, long y1, long x2, long y2) {
        mode = UNDEFINED;
        x = UNDEFINED;
        y = UNDEFINED;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        reached = false;
        annotation = null;
    } 

    /**
     * Constructor.
     * 
     * @param mode
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Fellow(int mode, int x1, int y1, int x2, int y2) {
        this.mode = mode;
        x = UNDEFINED;
        y = UNDEFINED;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        reached = false;
        annotation = null;
    } 

    /**
     * Constructor.
     * 
     * @param mode
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Fellow(int mode, long x1, long y1, long x2, long y2) {
        this.mode = mode;
        x = UNDEFINED;
        y = UNDEFINED;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        reached = false;
        annotation = null;
    } 
  }  
}
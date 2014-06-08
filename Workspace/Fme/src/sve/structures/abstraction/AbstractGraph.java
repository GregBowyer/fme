/**
 * Project: sve
 */

package sve.structures.abstraction;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.xerox.VTM.glyphs.*;

import sve.engine.*;

/**
 * This class represents a general graph
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public abstract class AbstractGraph {

  /**
   * The event pump of this diagram
   */
  protected ActionListener actionListener;

  /**
   * True if this diagram has a popup menu
   */
  protected boolean hasPopupMenu = false;

  /**
   * The popup menu of this diagram
   */
  protected JPopupMenu popupMenu;

  /**
   * The display which currently displays the graph (used for FontMetrics
   * operations)
   */
  private MainDisplay activeDisplay;

  /**
   * Init the nodes of the graph
   */
  public abstract void initNodes();

  /**
   * Init the edges of the graph
   */
  public abstract void initEdges();

  /**
   * Get the ActionListener for this diagram
   * 
   * @return Returns the actionListener.
   */
  public ActionListener getActionListener() {
    return actionListener;
  }

  /**
   * Set the ActionListener for this diagram
   * 
   * @param actionListener The actionListener to set.
   */
  public void setActionListener(ActionListener actionListener) {
    this.actionListener = actionListener;
  }

  /**
   * This Method shows the diagram's popup menu (if existing)
   * 
   * @param c The invoker
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void showPopupMenu(Component c, int x, int y) {
    if (hasPopupMenu) popupMenu.show(c, x, y);
  }

  /**
   * Get all the path glyphs of a diagram (for hit detection)
   * 
   * @return Returns a list of all paths
   */
  public abstract Vector<VPath> getPaths();

  /**
   * Update all glyphs of this diagram
   */
  public void updateGlyphs() {
  }

  /**
   * Computes the right level for all elements of this diagram
   */
  protected abstract void computeLevels();

  /**
   * Get the current displaying MainDisplay
   * 
   * @return The active display
   */
  public MainDisplay getActiveDisplay() {
    return activeDisplay;
  }

  /**
   * Set the current displaying MainDisplay (used for FontMetrics operations)
   * 
   * @param activeDisplay The MainDisplay which displays the graph
   */
  public void setActiveDisplay(MainDisplay activeDisplay) {
    this.activeDisplay = activeDisplay;
  }

  /**
   * Get all edges
   * 
   * @return A list of all edges
   */
  public abstract Vector<AbstractEdge> getEdges();

  /**
   * Get all nodes
   * 
   * @return A list of all nodes
   */
  public abstract Vector<AbstractNode> getNodes();
}

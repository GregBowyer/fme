/**
 * Project: sve
 */

package sve.structures.abstraction;

import java.util.*;

import sve.structures.general.*;

/**
 * This class represents a general edge
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class AbstractEdge
    extends AbstractGraphElement {

  /**
   * Constant for moving the ConnectionPoint
   */
  public static final int NORTH = 1;

  /**
   * Constant for moving the ConnectionPoint
   */
  public static final int WEST = 2;

  /**
   * Constant for moving the ConnectionPoint
   */
  public static final int EAST = 3;

  /**
   * Constant for moving the ConnectionPoint
   */
  public static final int SOUTH = 4;

  /**
   * One end of the edge
   */
  protected AbstractNode end1;

  /**
   * The other end of the edge
   */
  protected AbstractNode end2;

  /**
   * The connection line of this edge
   */
  protected ConnectionLine line;

  /**
   * The Constructor
   * 
   * @param owner The owner of this element
   */
  public AbstractEdge(AbstractGraph owner) {
    super(owner);
  }

  /**
   * (non-Javadoc)
   * 
   * @see sve.structures.abstraction.AbstractGraphElement#updateGlyph()
   */
  public void updateGlyph() {
    if (line != null) line.updateGlyph();
  }

  /**
   * Get the ConnectionLine of this edge
   * 
   * @return The ConnectionLine of this edge
   */
  public ConnectionLine getLine() {
    return line;
  }

  /**
   * Gets the 1st end of the edge
   * 
   * @return Returns the end1.
   */
  public AbstractNode getEnd1() {
    return end1;
  }

  /**
   * Gets the 1st connection point of the edge
   * 
   * @return Returns the end1 ConnectionPoint.
   */
  public ConnectionPoint getEnd1ConnectionPoint() {
    return line.get1stConnectionPoint();
  }

  /**
   * Gets the 2st end of the edge
   * 
   * @return Returns the end2.
   */
  public AbstractNode getEnd2() {
    return end2;
  }

  /**
   * Gets the 2st connection point of the edge
   * 
   * @return Returns the end1 ConnectionPoint.
   */
  public ConnectionPoint getEnd2ConnectionPoint() {
    return line.get2ndConnectionPoint();
  }

  /**
   * Sets the 1st connection point of the edge either (NORTH,WEST,EAST or SOUTH)
   * 
   * @return Returns the end1 ConnectionPoint.
   */
  public void setEnd1ConnectionPoint(int direction) {
    if (direction == NORTH) line.get1stConnectionPoint().fixTranslation(
        end1.getX(), end1.getY() + end1.getHeight());
    else if (direction == WEST) line.get1stConnectionPoint().fixTranslation(
        end1.getX() - end1.getWidth(), end1.getY());
    else if (direction == EAST) line.get1stConnectionPoint().fixTranslation(
        end1.getX() + end1.getWidth(), end1.getY());
    else if (direction == SOUTH) line.get1stConnectionPoint().fixTranslation(
        end1.getX(), end1.getY() - end1.getHeight());
    updateGlyph();
  }

  /**
   * Sets the 2st connection point of the edge either (NORTH,WEST,EAST or SOUTH)
   * 
   * @return Returns the end1 ConnectionPoint.
   */
  public void setEnd2ConnectionPoint(int direction) {
    if (direction == NORTH) line.get2ndConnectionPoint().fixTranslation(
        end2.getX(), end2.getY() + end2.getHeight());
    else if (direction == WEST) line.get2ndConnectionPoint().fixTranslation(
        end2.getX() - end2.getWidth(), end2.getY());
    else if (direction == EAST) line.get2ndConnectionPoint().fixTranslation(
        end2.getX() + end2.getWidth(), end2.getY());
    else if (direction == SOUTH) line.get2ndConnectionPoint().fixTranslation(
        end2.getX(), end2.getY() - end2.getHeight());
    updateGlyph();
  }

  /**
   * Show the line points of this edge
   */
  public void showLinePoints() {
    line.showLinePoints();
    updateGlyph();
  }

  /**
   * Hide the line points of this edge
   */
  public void hideLinePoints() {
    line.hideLinePoints();
    updateGlyph();
  }

  /**
   * Add a connection point to the line of the edge
   * 
   * @param x The X coordinate
   * @param y The Y coordinate
   */
  public ConnectionPoint addConnectionPoint(long x, long y) {
    return line.addConnectionPoint(x, y);
  }

  /**
   * Detects if the point is on this edge
   * 
   * @param x The x coordinate of the point
   * @param y The y coordinate of the point
   * @param tolerance The max. gap between the line and the point
   * @return A long array: (x and y coordinate of closest line point and the gap
   * between line and point)
   */
  public long[] edgeHit(long x, long y, long tolerance) {
    ConnectionPoint p1, p2;
    Iterator<ConnectionPoint> i;

    // The vectors or points
    DMathVector2D v, w, pvw, gap;

    // These variables store the length of a vector
    double lv, lvw, lgap, minlgap = Double.MAX_VALUE;
    double[] rec = new double[4];
    long[] ret = null;

    i = line.getPoints().iterator();
    p1 = i.next();
    while (i.hasNext()) {
      p2 = i.next();
      
      // Compute the rectangle points for the line (point must be within the
      // rectangle)
      if (p1.getX() > p2.getX()) {
        rec[0] = p2.getX();
        rec[2] = p1.getX();
      }
      else {
        rec[0] = p1.getX();
        rec[2] = p2.getX();
      }

      if (p1.getY() > p2.getY()) {
        rec[1] = p2.getY();
        rec[3] = p1.getY();
      }
      else {
        rec[1] = p1.getY();
        rec[3] = p2.getY();
      }

      // Compute the v and w vector
      v = new DMathVector2D(p2.getX() - p1.getX(), p2.getY() - p1.getY());
      w = new DMathVector2D(x - p1.getX(), y - p1.getY());

      // Compute the length of the v vector
      lv = Math.sqrt(v.x * v.x + v.y * v.y);

      // Compute the length of the w vector component in direction of the v
      // vector
      lvw = (v.x * w.x + v.y * w.y) / lv;

      // Compute the Point of the v vector which is closest to point
      pvw = new DMathVector2D(p1.getX() + (v.x / lv) * lvw, p1.getY()
          + (v.y / lv) * lvw);

      // Compute the gap between the line and the point
      gap = new DMathVector2D(x - pvw.x, y - pvw.y);

      // Compute the length of the gap
      lgap = Math.sqrt(gap.x * gap.x + gap.y * gap.y);

      if (lgap < tolerance && pvw.x >= rec[0] && pvw.x <= rec[2]
          && pvw.y >= rec[1] && pvw.y <= rec[3]) {

        if (lgap < minlgap) {
          ret = new long[3];
          ret[0] = Math.round(pvw.x);
          ret[1] = Math.round(pvw.y);
          ret[2] = Math.round(lgap);
          minlgap = lgap;
        }
      }

      p1 = p2;
    }
    return ret;
  }

  /**
   * This small class forms a mathematical 2D vector
   * 
   * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
   */
  private class DMathVector2D {
    public double x, y;

    public DMathVector2D(double x, double y) {
      this.x = x;
      this.y = y;
    }

    public String toString() {
      return "X:" + x + " Y:" + y;
    }
  }
}

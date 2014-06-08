/**
 * Project: fuml
 */

package sve.structures.general;

import java.awt.*;

import sve.*;
import sve.structures.abstraction.*;
import sve.structures.general.popupmenu.*;

import com.xerox.VTM.glyphs.*;

/**
 * This class represents a connection point (which is moveable)
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */
public class ConnectionPoint
    extends AbstractNode {

  /**
   * The model element this point belongs to
   */
  private AbstractNode element;

  /**
   * Glyph coordiantes (needed to detect changes)
   */
  private long gx, gy, gw, gh;

  /**
   * The connection line this point belongs to
   */
  private ConnectionLine line;

  /**
   * Constructor for static point
   * 
   * @param line The connection line this point is part of
   * @param owner The UML diagram this point belongs to
   * @param x Initial x point
   * @param y Initial y point
   * @param v Indicates if this point is visible
   */
  public ConnectionPoint(ConnectionLine line, AbstractGraph owner, long x,
      long y, boolean v) {
    super(owner);
    this.line = line;
    element = null;
    fixTranslation(x, y);
    line.addDependent(this);
  }

  /**
   * Constructor for point of a model element
   * 
   * @param e The model element this point belongs to
   * @param owner The UML diagram this point belongs to
   * @param x Initial x point
   * @param y Initial y point
   * @param v Indicates if this point is visible
   */
  public ConnectionPoint(AbstractNode e, ConnectionLine line,
      AbstractGraph owner, boolean v) {
    super(owner);
    this.line = line;
    element = e;
    line.addDependent(this);
  }

  /**
   * (non-Javadoc)
   * 
   * @see fuml.graphic.model.abstraction.AbstractModelElement#updateGlyph()
   */
  public void updateGlyph() {
    long offsetX, offsetY;

    if (glyph == null) {
      Color bc, fc;
      bc = (Color) CM.getConfig().get(
          "graph.ConnectionPoint.BackgroundColor");
      fc = (Color) CM.getConfig().get(
          "graph.ConnectionPoint.FrameColor");
      glyph = new VRectangle(0, 0, 0, 10, 10, bc);
      glyph.setBorderColor(fc);
      glyph.setOwner(this);
      gx = 0;
      gy = 0;
      gw = 0;
      gh = 0;
    }
    else {
      owner.getActiveDisplay().getVirtualSpace().onTop(glyph);

      if (element != null) {
        if (element.getX() != gx || element.getY() != gy
            || element.getWidth() != gw || element.getHeight() != gh) {
          offsetX = glyph.vx - gx;
          offsetY = glyph.vy - gy;
          fixTranslation(element.getX() + offsetX, element.getY() + offsetY);
        }
      }
    }
  }

  /**
   * (non-Javadoc)
   * 
   * @see fuml.graphic.model.abstraction.AbstractModelElement#draw(java.awt.Graphics2D)
   */
  public void addGlyphs() {
    super.addGlyphs();
  }

  /**
   * (non-Javadoc)
   * 
   * @see fuml.graphic.model.abstraction.AbstractModelElement#init()
   */
  public void init() {
    super.init();
    // Add a popup Menu
    this.popupMenu = new ConnectionPointMenu(this);
    this.hasPopupMenu = true;
    updateGlyph();
  }

  /**
   * (non-Javadoc)
   * 
   * @see fuml.graphic.model.abstraction.AbstractModelElement#getWidth()
   */
  public long getWidth() {
    return 5;
  }

  /**
   * (non-Javadoc)
   * 
   * @see fuml.graphic.model.abstraction.AbstractModelElement#getHeight()
   */
  public long getHeight() {
    return 5;
  }

  /**
   * (non-Javadoc)
   * 
   * @see fuml.graphic.model.abstraction.AbstractModelElement#fixTranslation(int,
   * int)
   */
  public void fixTranslation(long x, long y) {
    long eX, eY, eW, eH, dtop, dbottom, dright, dleft, xmin, ymin, min;

    if (element == null) {
      super.fixTranslation(x, y);
    }
    // When this point belongs to an element we have to check several things
    else {
      // Coordinates for range checking
      eX = element.getX() - element.getWidth();
      eY = element.getY() - element.getHeight();
      eH = element.getHeight() * 2;
      eW = element.getWidth() * 2;

      if (x < eX) x = eX;
      else if (x > eX + eW) x = eX + eW;
      if (y < eY) y = eY;
      else if (y > eY + eH) y = eY + eH;

      // Compute the closest edge

      // Distance to edges
      dtop = y - eY;
      dbottom = eY + eH - y;
      dright = eX + eW - x;
      dleft = x - eX;

      // Minimum
      xmin = Math.min(dright, dleft);
      ymin = Math.min(dtop, dbottom);
      min = Math.min(xmin, ymin);

      // Set the point to closest edge
      if (min == xmin) {
        if (xmin == dleft) x = eX;
        else x = eX + eW;
      }
      else {
        if (ymin == dtop) y = eY;
        else y = eY + eH;
      }

      // Store the glyph coordinates, so updateGlyph can detect a change
      gx = element.getGlyphRepresentation().vx;
      gy = element.getGlyphRepresentation().vy;
      gw = element.getWidth();
      gh = element.getHeight();

      super.fixTranslation(x, y);
    }
  }

  /**
   * Removes this point if possible
   */
  public void remove() {
    // Only static points can be removed)
    if (element == null) {
      ((ConnectionLine) line).removeConnectionPoint(this);
      owner.getActiveDisplay().getVirtualSpace().destroyGlyph(glyph);
    }
  }

  /**
   * Get the line this point belongs to
   * 
   * @return The ConnectionLine this point belongs to
   */
  public ConnectionLine getLine() {
    return line;
  }

  /**
   * (non-Javadoc)
   * 
   * @see fuml.graphic.model.abstraction.AbstractModelElement#onTop()
   */
  public void onTop() {
    super.onTop();
  }
}

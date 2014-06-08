/**
 * Project: fuml
 */

package sve.structures.general;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.Vector;

import sve.CM;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractGraphElement;
import sve.structures.abstraction.AbstractNode;

import com.xerox.VTM.engine.LongPoint;
import com.xerox.VTM.glyphs.VPath;

/**
 * This class represents a line (which may have many points)
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class ConnectionLine
    extends AbstractGraphElement {

    /**
     * The start of the line
     */
    private AbstractNode end1;

    /**
     * The end of the line
     */
    private AbstractNode end2;

    /**
     * The element which aggregates this line
     */
    private AbstractEdge parent;

    /**
     * The points when a symbol is drawn at the end
     */
    private boolean cutLineToPoint1, cutLineToPoint2;

    /**
     * The middle of the line
     */
    private LongPoint mid = new LongPoint();

    /**
     * The cutpoints (used when symbols should be drawn at one end)
     */
    private LongPoint cutPoint1 = new LongPoint(), cutPoint2 = new LongPoint();

    /**
     * All points of this line
     */
    private Vector<ConnectionPoint> points;

    /**
     * The line color
     */
    private Color lineColor;

    /**
     * Indicates if the line is marked
     */
    private boolean marked;

    /**
     * The dependents of this line
     */
    private Vector<AbstractGraphElement> dependents;

    /**
     * The Constructor
     * 
     * @param owner
     *            The owner of this element
     * @param el1
     *            The first element
     * @param el2
     *            The second element
     */
    public ConnectionLine(AbstractGraph owner, AbstractEdge parent,
        AbstractNode el1, AbstractNode el2) {
        super(owner);
        this.parent = parent;
        this.end1 = el1;
        this.end2 = el2;
        points = new Vector<ConnectionPoint>();
        cutLineToPoint1 = false;
        cutLineToPoint2 = false;
        marked = false;
        dependents = new Vector<AbstractGraphElement>();
        dependents.add(this);
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#updateGlyph()
     */
    public void updateGlyph() {
        Iterator<ConnectionPoint> it;

        if (glyph == null) {
            Color lc = (Color) CM.getConfig().get(
                "graph.ConnectionLine.Color");
            glyph = new VPath(0, lc, "");
            glyph.setOwner(this);
        } else {
            // Update Points
            it = points.iterator();
            while (it.hasNext()) {
                it.next().updateGlyph();
            }

            // Compute the CutPoints and the middle
            computePoints();

            // Now draw the line between the points
            long x = 0, y = 0;
            VPath v = (VPath) glyph;

            v.resetPath();
            if (marked) {
                owner.getActiveDisplay().getVirtualSpace().onTop(glyph);
                Color mc = (Color) CM.getConfig().get(
                    "graph.ConnectionLine.MarkedColor");
                v.setColor(mc);
            } else {
                owner.getActiveDisplay().getVirtualSpace().atBottom(glyph);
                if (lineColor != null)
                    v.setColor(lineColor);
                else {
                    Color lc = (Color) CM.getConfig().get(
                        "graph.ConnectionLine.Color");
                    v.setColor(lc);
                }
            }

            if (cutLineToPoint1) {
                x = cutPoint1.x;
                y = cutPoint1.y;
            } else {
                x = ((ConnectionPoint) points.firstElement()).getX();
                y = ((ConnectionPoint) points.firstElement()).getY();
            }

            v.jump(x, y, true);

            for (int i = 1; i < points.size(); i++) {
                if (i == points.size() - 1 && cutLineToPoint2) {
                    x = cutPoint2.x;
                    y = cutPoint2.y;
                } else {
                    x = ((ConnectionPoint) points.get(i)).getX();
                    y = ((ConnectionPoint) points.get(i)).getY();
                }
                v.addSegment(x, y, true);
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

        // Make sure that points is empty
        points.removeAllElements();

        // Add the first end points
        computeEndPoints();

        // Update the glyph
        updateGlyph();
        // Hide initial line points
        hideLinePoints();
    }

    /**
     * Returns the first connection point
     * 
     * @return The first connection point
     */
    public ConnectionPoint get1stConnectionPoint() {
        return (ConnectionPoint) points.firstElement();
    }

    /**
     * Returns the last connection point
     * 
     * @return The last connection point
     */
    public ConnectionPoint get2ndConnectionPoint() {
        return (ConnectionPoint) points.lastElement();
    }

    /**
     * Returns all connection points of this line
     * 
     * @return Returns the points.
     */
    public Vector<ConnectionPoint> getPoints() {
        return points;
    }

    /**
     * Returns the Y coordinate of the point in the middle of the line
     * 
     * @return The Y coordinate
     */
    public LongPoint getMiddle() {
        return mid;
    }

    /**
     * Add a connection point to the line of the association
     * 
     * @return Returns the end1 ConnectionPoint.
     */
    public ConnectionPoint addConnectionPoint(long x, long y) {
        ConnectionPoint p = (ConnectionPoint) points.firstElement(), p2;
        Line2D line;

        // Search the segment
        for (int i = 1; i < points.size(); i++) {
            p2 = (ConnectionPoint) points.get(i);
            line = new Line2D.Double(p.getX(), p.getY(), p2.getX(), p2.getY());
            if (line.intersects(x - 8, y - 8, 16, 16)) {
                p = new ConnectionPoint(this, owner, x, y, true);
                p.setActionListener(getActionListener());
                p.init();
                points.insertElementAt(p, i);
                break;
            }
            p = p2;
        }

        return p;
    }

    /**
     * Remove a connection point from the line of the association
     * 
     * @param p
     *            The ConnectionPoint.
     */
    public void removeConnectionPoint(ConnectionPoint p) {
        points.remove(p);
    }

    /**
     * Show the line points of this association
     */
    public void showLinePoints() {
        Iterator<ConnectionPoint> i;

        if (isHidden())
            return;

        i = points.iterator();
        while (i.hasNext()) {
            i.next().show();
        }
        marked = true;
    }

    /**
     * Hide the line points of this association
     */
    public void hideLinePoints() {
        Iterator<ConnectionPoint> i;

        i = points.iterator();
        while (i.hasNext()) {
            i.next().hide();
        }
        marked = false;
    }

    /**
     * Toggle if the line to ConnectionPoint1 should be cut (to draw a triangle
     * or lozenge)
     * 
     * @param cutLineToPoint1
     *            The cutLineToPoint1 to set.
     */
    public void setCutLineToPoint1(boolean cutLineToPoint1) {
        this.cutLineToPoint1 = cutLineToPoint1;
        updateGlyph();
    }

    /**
     * Toggle if the line to ConnectionPoint2 should be cut (to draw a triangle
     * or lozenge)
     * 
     * @param cutLineToPoint2
     *            The cutLineToPoint2 to set.
     */
    public void setCutLineToPoint2(boolean cutLineToPoint2) {
        this.cutLineToPoint2 = cutLineToPoint2;
        updateGlyph();
    }

    /**
     * Indicates if the line to ConnectionPoint1 should be cut (to draw a
     * triangle or lozenge)
     * 
     * @return True if the end of the line is cut
     */
    public boolean isCutLineToPoint1() {
        return this.cutLineToPoint1;
    }

    /**
     * Indicates if the line to ConnectionPoint2 should be cut (to draw a
     * triangle or lozenge)
     * 
     * @return True if the end of the line is cut
     */
    public boolean isCutLineToPoint2() {
        return this.cutLineToPoint2;
    }

    /**
     * Get the cut point 1 (if the end of the line was cut)
     * 
     * @return Returns the cutPoint1.
     */
    public LongPoint getCutPoint1() {
        return cutPoint1;
    }

    /**
     * Get the cut point 2 (if the end of the line was cut)
     * 
     * @return Returns the cutPoint2.
     */
    public LongPoint getCutPoint2() {
        return cutPoint2;
    }

    /**
     * Sets the Line Color
     * 
     * @param lineColor
     *            The color to set
     */
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * Get all dependents of this line
     * 
     * @return A Vector of all dependents
     */
    public Vector<AbstractGraphElement> getDependents() {
        return dependents;
    }

    /**
     * Get the element which holds this line
     * 
     * @return
     */
    public AbstractEdge getParent() {
        return parent;
    }

    /**
     * Set the element which holds this line
     * 
     * @return
     */
    public void setParent(AbstractEdge parent) {
        this.parent = parent;
    }

    /**
     * Add a dependent to this line
     * 
     * @param The
     *            dependent to add
     */
    public void addDependent(AbstractGraphElement dependent) {
        dependents.add(dependent);
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#onTop()
     */
    public void onTop() {
        super.onTop();

        Iterator<ConnectionPoint> i;

        i = points.iterator();
        while (i.hasNext()) {
            i.next().onTop();
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#hide()
     */
    public void hide() {
        super.hide();
        hideLinePoints();
    }

    /**
     * Indicates if the line is marked
     * 
     * @return True if the line is marked otherwise false
     */
    public boolean isMarked() {
        return marked;
    }

    // Internal Routines
    // =================

    /**
     * Arrange the endpoints of this line
     */
    private void computeEndPoints() {
        long fromX, fromY, toX, toY, vx, vy;
        double angle;
        ConnectionPoint e1, e2;

        // Compute the points of the line

        // Compute the 2D vector between the classes
        vx = end2.getX() - end1.getX();
        vy = end2.getY() - end1.getY();

        angle = Math.toDegrees(Math.atan2(vx, vy));
        // Points are on the bottom of end1 and on the top of end2
        if ((angle >= 135 && angle <= 180) || (angle >= -180 && angle <= -135)) {
            fromX = end1.getX();
            fromY = end1.getY() - end1.getHeight();
            toX = end2.getX();
            toY = end2.getY() + end2.getHeight();
        }
        // Points are on the left of end1 and on the right of end2
        else if (angle > -135 && angle < -45) {
            fromX = end1.getX() - end1.getWidth();
            fromY = end1.getY();
            toX = end2.getX() + end2.getWidth();
            toY = end2.getY();
        }
        // Point are on the top of end1 and on the bottom of end2
        else if (angle >= -45 && angle <= 45) {
            fromX = end1.getX();
            fromY = end1.getY() + end1.getHeight();
            toX = end2.getX();
            toY = end2.getY() - end2.getHeight();
        }
        // Points are on the right of end1 and on the left of end2
        else {
            fromX = end1.getX() + end1.getWidth();
            fromY = end1.getY();
            toX = end2.getX() - end2.getWidth();
            toY = end2.getY();
        }

        // Layout the end points
        if (points.size() == 0) {
            e1 = new ConnectionPoint(end1, this, owner, false);
            points.add(e1);
            e2 = new ConnectionPoint(end2, this, owner, false);
            points.add(e2);
            e1.init();
            e2.init();
        }

        e1 = (ConnectionPoint) points.firstElement();
        e2 = (ConnectionPoint) points.lastElement();
        e1.fixTranslation(fromX, fromY);
        e2.fixTranslation(toX, toY);
    }

    /**
     * Computes the middle and the cutLinePoints
     */
    private void computePoints() {

        long vx, vy;
        double len, clen;
        ConnectionPoint p1, p2;

        // Cut the length of the line to point 1
        if (cutLineToPoint1) {
            p1 = (ConnectionPoint) points.firstElement();
            p2 = (ConnectionPoint) points.get(1);
            vx = p1.getX() - p2.getX();
            vy = p1.getY() - p2.getY();

            len = 1 - 20 / Math.sqrt(vx * vx + vy * vy);

            cutPoint1.x = p2.getX() + Math.round(Math.round(vx * len));
            cutPoint1.y = p2.getY() + Math.round(Math.round(vy * len));
        }

        // Cut the length of the line to point 2
        if (cutLineToPoint2) {
            p1 = (ConnectionPoint) points.lastElement();
            p2 = (ConnectionPoint) points.get(points.size() - 2);
            vx = p1.getX() - p2.getX();
            vy = p1.getY() - p2.getY();

            len = 1 - 20 / Math.sqrt(vx * vx + vy * vy);

            cutPoint2.x = p2.getX() + Math.round(Math.round(vx * len));
            cutPoint2.y = p2.getY() + Math.round(Math.round(vy * len));
        }

        // Compute the middle of the line
        len = 0;
        p1 = (ConnectionPoint) points.firstElement();
        for (int i = 1; i < points.size(); i++) {
            p2 = (ConnectionPoint) points.get(i);
            len += Math.sqrt(Math.pow(Math.abs(p1.getX() - p2.getX()), 2)
                + Math.pow(Math.abs(p1.getY() - p2.getY()), 2));
            p1 = p2;
        }

        clen = len / 2;
        len = 0;

        p1 = (ConnectionPoint) points.firstElement();
        for (int i = 1; i < points.size(); i++) {
            p2 = (ConnectionPoint) points.get(i);
            len += Math.sqrt(Math.pow(Math.abs(p1.getX() - p2.getX()), 2)
                + Math.pow(Math.abs(p1.getY() - p2.getY()), 2));
            if (clen - len <= 0) {
                clen = len - clen;
                vx = p2.getX() - p1.getX();
                vy = p2.getY() - p1.getY();
                len = clen / Math.sqrt(vx * vx + vy * vy);
                mid = new LongPoint();
                mid.x = p2.getX() - Math.round(Math.round(vx * len));
                mid.y = p2.getY() - Math.round(Math.round(vy * len));
                break;
            }
            p1 = p2;
        }
    }
}

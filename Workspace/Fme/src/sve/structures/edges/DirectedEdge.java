/**
 * Project: sve
 */

package sve.structures.edges;

import java.awt.Color;
import java.awt.Font;

import sve.CM;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;
import sve.structures.edges.popupmenu.DirectedEdgeMenu;
import sve.structures.general.ConnectionLine;
import sve.structures.general.ConnectionPoint;

import com.xerox.VTM.engine.LongPoint;
import com.xerox.VTM.glyphs.VPolygon;
import com.xerox.VTM.glyphs.VRectangleST;
import com.xerox.VTM.glyphs.VText;

/**
 * This class represents a simple edge which connects two nodes
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class DirectedEdge
    extends AbstractEdge {

    /**
     * The caption of this edge
     */
    private String caption;

    // Sub-Glyphs
    private VText gName;

    private VRectangleST rect;

    /**
     * The Constructor
     * 
     * @param owner
     *            The UML diagram this generalization belongs to
     * @param fromNode
     *            The parent of the generalization
     * @param toNode
     *            The child of the generalization
     */
    public DirectedEdge(AbstractGraph owner, AbstractNode fromNode,
            AbstractNode toNode) {
        super(owner);
        this.end1 = fromNode;
        this.end2 = toNode;
        this.owner = owner;
        fromNode.addEdge(this);
        toNode.addEdge(this);

        line = new ConnectionLine(owner, this, fromNode, toNode);
        line.addDependent(this);
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#init()
     */
    public void init() {
        super.init();
        // Add a popup Menu
        this.popupMenu = new DirectedEdgeMenu(this);
        this.hasPopupMenu = true;
        line.setActionListener(getActionListener());
        line.init();

        line.setCutLineToPoint2(true);
        updateGlyph();
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#updateGlyph()
     */
    public void updateGlyph() {
        super.updateGlyph();
        Font f;
        Color fc;
        long fHeight;

        // Get some important values
        fc = (Color) CM.getConfig().get(
                "graph.DirectedEdge.CaptionColor");
        f = Font.decode((String) CM.getConfig().get(
                "graph.DirectedEdge.Font"));
        fHeight = owner.getActiveDisplay().getFontHeight(f);

        if (line == null) {
            LongPoint[] lp1 = new LongPoint[4];
            for (int i = 0; i < lp1.length; i++)
                lp1[i] = new LongPoint(0, 0);
            glyph = new VPolygon(lp1, Color.WHITE);
            glyph.setVisible(false);
        }
        // Delegation for the line
        else {

            // Draw the caption
            if (caption != null) {
                try {
                    drawCaption(f, fc, fHeight);
                } catch (Exception e) {

                }
            }

            drawArrow(line.getCutPoint2().x, line.getCutPoint2().y, line
                    .get2ndConnectionPoint().getX(), line
                    .get2ndConnectionPoint().getY());
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
     * @see fuml.graphic.model.abstraction.AbstractModelElement#hide()
     */
    public void hide() {
        super.hide();
        line.hide();
        if (gName != null)
            gName.setVisible(false);
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#show()
     */
    public void show() {
        super.show();
        line.show();
        if (gName != null)
            gName.setVisible(true);
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#onTop()
     */
    public void onTop() {
        super.onTop();
        owner.getActiveDisplay().getVirtualSpace().onTop(glyph);
        line.onTop();
    }

    /**
     * Get the caption of this edge
     * 
     * @return The caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Set the caption of this edge
     * 
     * @param caption
     *            The new caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    // Internal Methods
    // ================

    /**
     * Draw the caption of this association
     * 
     * @param f
     *            The font to use
     * @param fc
     *            The font color to draw with
     * @param fheight
     *            The height of the font
     */
    private void drawCaption(Font f, Color fc, long fheight) {
        long w;
        double vx, vy, len;

        // Remove old text glyphs
        if (gName != null)
            owner.getActiveDisplay().getVirtualSpace().destroyGlyph(gName);
        if (rect != null)
            owner.getActiveDisplay().getVirtualSpace().destroyGlyph(rect);

        gName = new VText(caption);
        gName.setSpecialFont(f);
        gName.setColor(fc);

        w = owner.getActiveDisplay().getFontWidth(f, caption);

        // Compute Vector
        vx = ((ConnectionPoint) line.getPoints().get(1)).getX()
                - ((ConnectionPoint) line.getPoints().get(0)).getX();
        vy = ((ConnectionPoint) line.getPoints().get(1)).getY()
                - ((ConnectionPoint) line.getPoints().get(0)).getY();

        // Compute length of Vector
        len = Math.sqrt(vx * vx + vy * vy);

        // Normalize Vector
        vx = vx / len;
        vy = vy / len;

        // For the beginning of the Line
        // if (line != null) gName.moveTo(x
        // + Math.round(Math.round(vx * xoff + px * 20 - w / 2)), y
        // + Math.round(Math.round(vy * 40 + py * 20 - fheight / 2)));
        if (line != null) {
            gName.moveTo(line.getMiddle().x - w / 2, line.getMiddle().y);

            // Draw underlying rectangle
            rect = new VRectangleST(line.getMiddle().x, Math.round(line
                    .getMiddle().y + 8), 0, w / 2, 12, Color.WHITE);
            rect.setPaintBorder(false);
            rect.setTransparencyValue((float) 0.6);

            owner.getActiveDisplay().getVirtualSpaceManager().addGlyph(rect,
                    owner.getActiveDisplay().getVirtualSpace());
        }

        owner.getActiveDisplay().getVirtualSpaceManager().addGlyph(gName,
                owner.getActiveDisplay().getVirtualSpace());
    }

    /**
     * Draws an Triangle
     * 
     * @param xFrom
     *            The x position to start
     * @param yFrom
     *            The y position to start
     * @param xTo
     *            The x position to end
     * @param yTo
     *            The y position to end
     */
    private void drawArrow(long xFrom, long yFrom, long xTo, long yTo) {
        double vx, vy, px, py, len;
        Color c;
        LongPoint[] p = new LongPoint[3];

        // Note: This solution constructs every time a new Polygon.
        // This was necessery because the points of a Polygon can't be changed
        // and the reorientation of VTriangle is not good enough

        for (int i = 0; i < p.length; i++)
            p[i] = new LongPoint(0, 0);

        // Destroy the old polygon
        owner.getActiveDisplay().getVirtualSpace().destroyGlyph(glyph);

        // Compute Vector
        vx = xTo - xFrom;
        vy = yTo - yFrom;

        // Compute length of Vector
        len = Math.sqrt(vx * vx + vy * vy);

        // Compute normalized perpendicular Vector
        px = -vy / len;
        py = vx / len;

        p[0].setLocation(xFrom + Math.round(Math.round(px * 7)), yFrom
                + Math.round(Math.round(py * 7)));
        p[1].setLocation(xTo, yTo);
        p[2].setLocation(xFrom - Math.round(Math.round(px * 7)), yFrom
                - Math.round(Math.round(py * 7)));

        // Construct a new polygon on specified end
        if (line.isMarked())
            c = (Color) CM.getConfig().get(
                    "graph.ConnectionLine.MarkedColor");
        else
            c = (Color) CM.getConfig().get(
                    "graph.ConnectionLine.Color");

        glyph = new VPolygon(p, c);
        glyph.setBorderColor(c);
        glyph.setVisible(true);
        glyph.setSensitivity(false);
        owner.getActiveDisplay().getVirtualSpaceManager().addGlyph(glyph,
                owner.getActiveDisplay().main.getVirtualSpaceName());
    }
}

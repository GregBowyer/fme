/**
 * Project: sve
 */

package sve.structures.nodes;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import net.claribole.zvtm.glyphs.CGlyph;
import sve.CM;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;

import com.xerox.VTM.engine.LongPoint;
import com.xerox.VTM.glyphs.Glyph;
import com.xerox.VTM.glyphs.VEllipse;
import com.xerox.VTM.glyphs.VPolygon;
import com.xerox.VTM.glyphs.VRectangle;
import com.xerox.VTM.glyphs.VText;

/**
 * This class represents a node with text which can be marked
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class TextNode
    extends AbstractNode {

    /*
     * Possible shapes
     */
    public static final int RectangularShape = 1;

    public static final int TrapezeShape = 2;

    public static final int OvalShape = 3;

    public static final int DiamondShape = 4;

    public static final int BoxedShape = 5;

    /**
     * The horizontal space between text and border of this node
     */
    private final int borderX = 80;

    /**
     * The vertical space between text and border of this node
     */
    private final int borderY = 50;

    /**
     * The content of this node
     */
    private String[] content;

    /**
     * The shape of this node
     */
    private int shape;

    /**
     * The background color of this node
     */
    private Color bc;

    /**
     * The text color of this node
     */
    private Vector<Color> tc;

    /**
     * The color when the node is marked
     */
    private static Color markedColor = Color.YELLOW;

    /**
     * Indecates if this node is marked
     */
    private boolean marked;

    // Sub Glyphs
    private VRectangle recNode;

    private Glyph recNodeVisible;

    private Vector<VText> gContent;

    /**
     * Sets the caption of this node
     * 
     * @param c
     *            The caption
     */
    public void setCaption(String[] c) {
        this.content = c;
    }

    /**
     * Sets the caption of this node
     * 
     * @param c
     *            The caption
     */
    public void setCaption(String c) {
        this.content = c.split("\n");
    }

    /**
     * Sets the color of this node (null means default)
     * 
     * @param c
     *            The caption
     */
    public void setBackgroundColor(Color bc) {
        this.bc = bc;
    }

    /**
     * Sets the color of this node (null means default)
     * 
     * @param c
     *            The caption
     */
    public void setTextColor(Color c) {
        if (tc == null)
            tc = new Vector<Color>();

        if (c == null && tc.size() != 0)
            tc.remove(tc.lastElement());
        else if (c != null)
            tc.add(c);
    }

    /**
     * Gets the color of this node (null means default)
     * 
     * @return The node color
     */
    public Color getBackgroundColor() {
        return bc;
    }

    /**
     * Gets the color of this node (null means default)
     * 
     * @return The node color
     */
    public Color getTextColor() {
        if (tc == null)
            tc = new Vector<Color>();

        if (tc.size() > 0)
            return tc.lastElement();
        else
            return null;
    }

    /**
     * Gets the caption of this node
     * 
     * @return The caption (each line is a String in the array)
     */
    public String[] getCaption() {
        return content;
    }

    /**
     * The Constructor
     * 
     * @param owner
     *            The UML diagram which shows this class
     */
    public TextNode(AbstractGraph owner, int shape) {
        super(owner);
        this.shape = shape;
    }

    public void init() {
        super.init();

        updateGlyph();
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#updateGlyph()
     */
    public void updateGlyph() {
        long fHeight, fw, fwMax, h, w;
        Font f;
        Color fc;
        CGlyph cg;
        HashMap<String, Object> config = CM.getConfig();

        // Get some important values

        if (getTextColor() == null)
            fc = (Color) config
                .get("graph.Textnode.FontColor");
        else
            fc = getTextColor();

        f = Font.decode((String) config.get("graph.Textnode.Font"));
        fHeight = owner.getActiveDisplay().getFontHeight(f);

        if (glyph == null) {
            recNode = new VRectangle(0, 0, 0, 50, 50, Color.WHITE);
            LongPoint[] lp1 = new LongPoint[4];
            for (int i = 0; i < lp1.length; i++)
                lp1[i] = new LongPoint(0, 0);
            recNodeVisible = new VPolygon(lp1, Color.WHITE);
            recNodeVisible.setVisible(false);

            gContent = new Vector<VText>();
            content = new String[1];
            content[0] = "";
            cg = new CGlyph(recNode, null);
            cg.addSecondaryGlyph(recNodeVisible, 0, 0);
            cg.setOwner(this);
            cg.setSensitivity(CGlyph.PRIMARY_GLYPH_ONLY);
            glyph = cg;
        } else {
            cg = (CGlyph) glyph;
        }

        fwMax = 0;
        for (int i = 0; i < content.length; i++) {
            fw = owner.getActiveDisplay().getFontWidth(f, content[i]);
            if (fw > fwMax)
                fwMax = fw;
        }

        if (shape == DiamondShape) {
            w = (fwMax + borderX) / 2 + 90;
            h = (fHeight * content.length + borderY) / 2 + 60;
        } else {
            w = (fwMax + borderX) / 2;
            h = (fHeight * content.length + borderY) / 2;
        }

        recNode.setWidth(w);
        recNode.setHeight(h);
        recNode.setFill(false);
        recNode.setDashed(true);
        recNode.setBorderColor(Color.WHITE);

        drawShape(w, h);

        if (shape == DiamondShape)
            drawText(65, recNode.getHeight() - 70, f, fc,
                fHeight);
        else
            drawText(0, recNode.getHeight() - 10, f, fc, fHeight);

    }

    /**
     * (non-Javadoc)
     * 
     * @see sve.structures.abstraction.AbstractGraphElement#addGlyphs()
     */
    public void addGlyphs() {
        super.addGlyphs();
        // Add the Glyphs to the virtual space
        owner.getActiveDisplay().getVirtualSpaceManager().addGlyph(recNode,
            owner.getActiveDisplay().getVirtualSpace());
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#getWidth()
     */
    public long getWidth() {
        return recNode.getWidth();
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#getHeight()
     */
    public long getHeight() {
        return recNode.getHeight();
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#onTop()
     */
    public void onTop() {
        super.onTop();

        Iterator<VText> i;

        owner.getActiveDisplay().getVirtualSpace().onTop(recNode);
        owner.getActiveDisplay().getVirtualSpace().onTop(recNodeVisible);
        i = gContent.iterator();
        while (i.hasNext())
            owner.getActiveDisplay().getVirtualSpace().onTop(i.next());
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#hide()
     */
    public void hide() {
        super.hide();

        recNodeVisible.setVisible(false);

        Iterator<VText> i = gContent.iterator();
        while (i.hasNext()) {
            i.next().setVisible(false);
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see fuml.graphic.model.abstraction.AbstractModelElement#show()
     */
    public void show() {
        super.show();

        recNodeVisible.setVisible(true);

        Iterator<VText> i = gContent.iterator();
        while (i.hasNext()) {
            i.next().setVisible(true);
        }
    }

    /**
     * Returns the shape of this node
     * 
     * @return The shape
     */
    public int getShape() {
        return shape;
    }

    /**
     * Indicates if this node is marked
     * 
     * @return True if this node is marked otherwise false
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * Mark this node
     */
    public void markNode() {
        this.marked = true;
    }

    /**
     * Unmark this node
     */
    public void unmarkNode() {
        this.marked = false;
    }

    // Internal Methods
    // ================

    /**
     * Compute the composite glyph of the text
     */
    private void drawText(long xoffset, long yoffset, Font f, Color fc,
        long fheight) {
        CGlyph cg = (CGlyph) glyph;
        VText t;
        Iterator<VText> i;

        // Remove old text glyphs
        i = gContent.iterator();
        while (i.hasNext()) {
            t = i.next();
            cg.removeSecondaryGlyph(t);
            owner.getActiveDisplay().getVirtualSpace().destroyGlyph(t);
        }
        gContent.removeAllElements();

        for (int j = content.length - 1; j >= 0; j--) {
            t = new VText(content[j]);
            // Text is always OnTop
            owner.getActiveDisplay().getVirtualSpace().onTop(t);
            t.setSpecialFont(f);
            t.setColor(fc);

            gContent.add(t);

            owner.getActiveDisplay().getVirtualSpaceManager().addGlyph(t,
                owner.getActiveDisplay().getVirtualSpace());
            cg.addSecondaryGlyph(t, xoffset - recNode.getWidth() + borderX / 2,
                -yoffset + (content.length - 1 - j) * fheight + borderY / 2);
        }
    }

    private void drawShape(long w, long h) {
        LongPoint[] p;
        CGlyph cg = (CGlyph) glyph;
        Color c;
        Color framec = (Color) CM.getConfig().get(
            "graph.Textnode.FrameColor");

        if (marked)
            c = markedColor;
        else if (bc == null)
            c = (Color) CM.getConfig().get(
                "graph.Textnode.BackgroundColor");
        else
            c = bc;

        // Destroy the old glyph
        cg.removeSecondaryGlyph(recNodeVisible);
        owner.getActiveDisplay().getVirtualSpace().destroyGlyph(recNodeVisible);

        // Note: This solution is constructs every time a new Polygon.
        // This was necessery because the points of a Polygon can't be changed

        if (shape == OvalShape) {
            recNodeVisible = new VEllipse(0, 0, 0, w, h, c);
        } else {
            if (shape == TrapezeShape)
                p = constructTrapeze(w, h);
            else if (shape == DiamondShape)
                p = constructDiamond(w, h);
            else if (shape == BoxedShape)
                p = constructBoxed(w, h);
            else
                p = constructRectangle(w, h);
            // Construct a new polygon on specified end
            recNodeVisible = new VPolygon(p, c);
        }

        recNodeVisible.setBorderColor(framec);
        recNodeVisible.setVisible(true);
        recNodeVisible.setSensitivity(false);
        owner.getActiveDisplay().getVirtualSpaceManager().addGlyph(recNodeVisible,
            owner.getActiveDisplay().getVirtualSpace());
        cg.addSecondaryGlyph(recNodeVisible, 0, 0);

    }

    private LongPoint[] constructRectangle(long w, long h) {
        LongPoint[] p = new LongPoint[4];
        for (int i = 0; i < p.length; i++)
            p[i] = new LongPoint(0, 0);

        p[0].setLocation(0, 0);
        p[1].setLocation(w * 2, 0);
        p[2].setLocation(w * 2, h * 2);
        p[3].setLocation(0, h * 2);

        return p;
    }

    private LongPoint[] constructTrapeze(long w, long h) {
        LongPoint[] p = new LongPoint[4];
        for (int i = 0; i < p.length; i++)
            p[i] = new LongPoint(0, 0);

        p[0].setLocation(0, 0);
        p[1].setLocation(w * 2 - 40, 0);
        p[2].setLocation(w * 2, h * 2);
        p[3].setLocation(40, h * 2);

        return p;
    }

    private LongPoint[] constructDiamond(long w, long h) {
        LongPoint[] p = new LongPoint[4];
        for (int i = 0; i < p.length; i++)
            p[i] = new LongPoint(0, 0);

        p[0].setLocation(w, 0);
        p[1].setLocation(w * 2, h);
        p[2].setLocation(w, h * 2);
        p[3].setLocation(0, h);

        return p;
    }

    private LongPoint[] constructBoxed(long w, long h) {
        LongPoint[] p = new LongPoint[10];
        for (int i = 0; i < p.length; i++)
            p[i] = new LongPoint(0, 0);

        p[0].setLocation(0, 0);
        p[1].setLocation(w * 2, 0);
        p[2].setLocation(w * 2, h * 2);

        p[3].setLocation(w * 2 - 25, h * 2);
        p[4].setLocation(w * 2 - 25, 0);
        p[5].setLocation(w * 2 - 25, h * 2);
        p[6].setLocation(25, h * 2);
        p[7].setLocation(25, 0);
        p[8].setLocation(25, h * 2);

        p[9].setLocation(0, h * 2);

        return p;
    }
}

/**
 * Project: fuml
 */

package sve.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import sve.CM;
import sve.SVEMain;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;
import sve.structures.general.ConnectionPoint;

import com.xerox.VTM.engine.Camera;
import com.xerox.VTM.engine.View;
import com.xerox.VTM.engine.VirtualSpace;
import com.xerox.VTM.engine.VirtualSpaceManager;
import com.xerox.VTM.glyphs.Glyph;

/**
 * This class represents a display for a diagram. For the drawing it uses a
 * special threaded double buffering technique.
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class MainDisplay {

    /**
     * SVE Main class
     */
    public SVEMain main;

    /**
     * Field for identifying a class
     */
    private static final long serialVersionUID = 3748121431174617317L;

    /**
     * The VirtualSpaceManager of this application
     */
    private VirtualSpaceManager vsm = null;

    /**
     * The Threads with the double buffering
     */
    private Vector<View> viewPanels = new Vector<View>();

    /**
     * The display as JPanel
     */
    private JPanel panel;

    /**
     * The display with scroll bars
     */
    private JPanel scrollPanel;

    /**
     * The scrollbars
     */
    private JScrollBar sbx, sby;

    /**
     * The event listener
     */
    private EventPump eventPump;

    /**
     * The camera of this MainDisplay
     */
    private Camera camera;

    /**
     * The View of the camera
     */
    private View cameraView;

    /**
     * Dummy image for FontMetrics
     */
    private BufferedImage bufferedImage = new BufferedImage(2, 2,
            BufferedImage.TYPE_INT_RGB);

    /**
     * The ToolBar of this MainDisplay
     */
    private MainDisplayToolBar toolBar;

    /**
     * The graph which is displayed by this MainDisplay
     */
    private AbstractGraph graph;

    /**
     * The Constructor
     * 
     * @param p
     *            The project with is displayed with this display
     * @param d
     *            The diagram to draw
     */
    public MainDisplay(AbstractGraph graph, SVEMain main) {

        this.graph = graph;
        this.main = main;

        Vector<Camera> cameras = new Vector<Camera>();

        toolBar = new MainDisplayToolBar();

        vsm = new VirtualSpaceManager();

        vsm.addVirtualSpace(main.getVirtualSpaceName());
        vsm.setZoomLimit(0);

        // Add a camera to the virtual space
        vsm.addCamera(main.getVirtualSpaceName());
        camera = vsm.getVirtualSpace(main.getVirtualSpaceName()).getCamera(0);
        cameras.add(camera);

        // Create the panel
        panel = vsm.addPanelView(cameras, "MainView", 800, 600);

        // Get the view of the camera
        // FIXME The ViewPanels must be stopped manual
        cameraView = vsm.getView("MainView");
        cameraView.setNotifyMouseMoved(true);
        cameraView.setBackgroundColor((Color) CM.getConfig().get(
                "gui.DesktopPaneBackground"));
        cameraView.setCursorIcon(Cursor.HAND_CURSOR);
        viewPanels.add(cameraView);

        // Scroll Bars
        sbx = new JScrollBar();
        sbx.setOrientation(JScrollBar.HORIZONTAL);
        sby = new JScrollBar();
        sby.setOrientation(JScrollBar.VERTICAL);

        scrollPanel = new JPanel();
        scrollPanel.setLayout(new BorderLayout());
        scrollPanel.add(panel);
        scrollPanel.add(sbx, BorderLayout.SOUTH);
        scrollPanel.add(sby, BorderLayout.EAST);
    }

    public void clearVirtualSpace() {
        while (!vsm.getVirtualSpace(main.getVirtualSpaceName()).getAllGlyphs()
                .isEmpty()) {
            vsm.getVirtualSpace(main.getVirtualSpaceName()).destroyGlyph(
                    (Glyph) vsm.getVirtualSpace(main.getVirtualSpaceName())
                            .getAllGlyphs().firstElement());
        }
    }

    /**
     * Gets the instance of the VirtualSpaceManager of this application
     * 
     * @return Returns the VirtualSpaceManager.
     */
    public VirtualSpaceManager getVirtualSpaceManager() {
        return vsm;
    }

    /**
     * Get the display as JPanel
     * 
     * @return Returns the panel.
     */
    public JPanel getJPanel() {
        return scrollPanel;
    }

    /**
     * Gets the view of the camera
     * 
     * @return Returns the cameraView.
     */
    public View getCameraView() {
        return cameraView;
    }

    /**
     * Gets the camera of this display
     * 
     * @return Returns the camera.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Get the height of a font
     * 
     * f The font to process
     */
    public int getFontHeight(Font f) {
        try {
            return bufferedImage.createGraphics().getFontMetrics(f).getHeight() + 4;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Get the width of a String
     * 
     * f The font to process s The string to process
     */
    public int getFontWidth(Font f, String s) {
        try {
            return bufferedImage.createGraphics().getFontMetrics(f)
                    .stringWidth(s);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Get the ToolBar of this MainDisplay
     * 
     * @return The ToolBar of this MainDisplay
     */
    public MainDisplayToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Get the virtual space of this MainDisplay
     * 
     * @return The VirtualSpace of this MainDisplay
     */
    public VirtualSpace getVirtualSpace() {
        return vsm.getVirtualSpace(main.getVirtualSpaceName());
    }

    /**
     * Returns the graph which is displayed by this MainDisplay
     * 
     * @return The displayed graph
     */
    public AbstractGraph getGraph() {
        return graph;
    }

    /**
     * Set the listener for this mainDisplay
     * 
     * @param eventPump
     */
    public void setListener(EventPump eventPump) {
        if (eventPump != null) {
            panel.removeMouseWheelListener(eventPump);
        }
        panel.addMouseWheelListener(eventPump);
        panel.addKeyListener(eventPump);
        scrollPanel.addKeyListener(eventPump);
        if (this.eventPump == null)
            toolBar.setListener(eventPump);
        panel.addComponentListener(eventPump);
        cameraView.setEventHandler(eventPump);
        cameraView.activate();
        sbx.addAdjustmentListener(eventPump);
        sby.addAdjustmentListener(eventPump);
        getVirtualSpaceManager().animator.setAnimationListener(eventPump);
        this.eventPump = eventPump;
    }

    /**
     * Centers the camera around an area
     * 
     * @param x1
     *            The x coordinate of the upper left corner
     * @param y1
     *            The y coordinate of the upper left corner
     * @param x2
     *            The x coordinate of the lower right corner
     * @param y2
     *            The y coordinate of the lower right corner
     */
    public void centerCamera(long x1, long y1, long x2, long y2) {

        // Follow the move of the camera
        getVirtualSpaceManager().animator.setAnimationListener(eventPump);
        getVirtualSpaceManager().centerOnRegion(camera, 500, x1, y1, x2, y2);
    }

    /**
     * Set the zoom to a special level
     * 
     * @param zoomLevel
     *            The new zoom level
     */
    public void setZoomLevel(long zoomLevel) {
        // Follow the move of the camera
        camera.setAltitude(zoomLevel);
        updateSpinner();
    }

    /**
     * Get the zoomlevel
     * 
     * @param zoomLevel
     *            The new zoom level
     */
    public long getZoomLevel() {
        return Math.round(camera.getAltitude());
    }

    /**
     * Forces the display to zoom out or in until the whole graph is displayed
     */
    public void fitInWindow() {
        long fg[] = findFarmostGlyphCoords();
        /*
         * long fg[] = vsm.getVirtualSpace( main.getVirtualSpaceName())
         * .findFarmostGlyphCoords();
         */

        centerCamera(fg[0], fg[1], fg[2], fg[3]);
    }

    /**
     * Toggles if this zooming mode should be set or not
     * 
     * @param v
     *            Sets the zooming mode
     */
    public void setZoomingMode(boolean v) {
        eventPump.setZoomCursor(v);
    }

    /**
     * Update the scrollbars
     */
    public void updateScrollBars() {
        int vx, vy, h, w, posx, posy;
        long[] vReg, vG;

        vReg = cameraView.getVisibleRegion(camera);
        /*
         * vG = vsm.getVirtualSpace(main.getVirtualSpaceName())
         * .findFarmostGlyphCoords();
         */

        vG = findFarmostGlyphCoords();

        if (vG[2] - vG[0] > Integer.MAX_VALUE || vG[2] - vG[0] < 0)
            w = Integer.MAX_VALUE;
        else
            w = (int) (vG[2] - vG[0]);
        if (vG[1] - vG[3] > Integer.MAX_VALUE || vG[1] - vG[3] < 0)
            h = Integer.MAX_VALUE;
        else
            h = (int) (vG[1] - vG[3]);
        if (vReg[2] - vReg[0] > Integer.MAX_VALUE || vReg[2] - vReg[0] < 0)
            vx = Integer.MAX_VALUE;
        else
            vx = (int) (vReg[2] - vReg[0]);
        if (vReg[1] - vReg[3] > Integer.MAX_VALUE || vReg[1] - vReg[3] < 0)
            vy = Integer.MAX_VALUE;
        else
            vy = (int) (vReg[1] - vReg[3]);

        // No questioning here because every value will be handled correctly
        posx = ((int) (vReg[0] - vG[0]));
        posy = ((int) (vG[1] - vReg[1]));

        eventPump.disableAdjustment();

        sbx.setMinimum(0);
        sbx.setMaximum(w);
        sbx.setVisibleAmount(vx);
        sbx.setValue(posx);
        sbx.validate();

        sby.setMinimum(0);
        sby.setMaximum(h);
        sby.setVisibleAmount(vy);
        sby.setValue(posy);
        sby.validate();

        eventPump.enableAdjustment();

        scrollPanel.grabFocus();
    }

    public long[] findFarmostGlyphCoords() {
        long ret[] = new long[4];

        AbstractNode node;
        Iterator<AbstractNode> it = graph.getNodes().iterator();
        if (it.hasNext())
            node = it.next();
        else
            return null;

        ret[0] = node.getX() - node.getWidth();
        ret[1] = node.getY() + node.getWidth();
        ret[2] = node.getX() + node.getWidth();
        ret[3] = node.getY() - node.getWidth();

        while (it.hasNext()) {
            node = it.next();
            if (node.getX() - node.getWidth() < ret[0])
                ret[0] = node.getX() - node.getWidth();
            if (node.getY() + node.getHeight() > ret[1])
                ret[1] = node.getY() + node.getHeight();
            if (node.getX() + node.getWidth() > ret[2])
                ret[2] = node.getX() + node.getWidth();
            if (node.getY() - node.getHeight() < ret[3])
                ret[3] = node.getY() - node.getHeight();
        }

        Iterator<ConnectionPoint> it3;
        Iterator<AbstractEdge> it2 = graph.getEdges().iterator();
        while (it2.hasNext()) {
            it3 = it2.next().getLine().getPoints().iterator();
            while (it3.hasNext()) {
                node = it3.next();
                if (node.getX() - node.getWidth() < ret[0])
                    ret[0] = node.getX() - node.getWidth();
                if (node.getY() + node.getHeight() > ret[1])
                    ret[1] = node.getY() + node.getHeight();
                if (node.getX() + node.getWidth() > ret[2])
                    ret[2] = node.getX() + node.getWidth();
                if (node.getY() - node.getHeight() < ret[3])
                    ret[3] = node.getY() - node.getHeight();
            }
        }

        return ret;
    }

    // Internal Methods
    // ================

    /**
     * Update the spinner from toolbar
     */
    private void updateSpinner() {
        toolBar.setSpinnerValue(new Integer(Math.round(camera.getAltitude())));
    }

}

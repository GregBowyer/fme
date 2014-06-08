/**
 * Project: fuml
 */

package sve.engine;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.claribole.zvtm.engine.AnimationListener;
import sve.CM;
import sve.SVEMain;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraphElement;
import sve.structures.abstraction.AbstractNode;
import sve.structures.edges.BendableDirectedEdge;
import sve.structures.general.ConnectionLine;
import sve.structures.general.ConnectionPoint;
import sve.structures.nodes.CollapsableTextNode;
import sve.structures.nodes.TextNode;

import com.xerox.VTM.engine.AnimManager;
import com.xerox.VTM.engine.AppEventHandler;
import com.xerox.VTM.engine.Camera;
import com.xerox.VTM.engine.LongPoint;
import com.xerox.VTM.engine.ViewPanel;
import com.xerox.VTM.glyphs.Glyph;
import com.xerox.VTM.glyphs.VRectangle;

/**
 * This class represents an event pump for a diagram (all events from this
 * diagram are handled)
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class EventPump
    extends AppEventHandler implements MouseWheelListener,
        ActionListener, ChangeListener, AnimationListener, KeyListener,
        ComponentListener, AdjustmentListener {

    /**
     * SVE Main class
     */
    public SVEMain main;

    /**
     * The last selected point on a line
     */
    private long[] linePoint;

    /**
     * The current dragged element
     */
    private AbstractGraphElement dragged;

    /**
     * The highlighted connection element
     */
    private AbstractEdge connectionElement;

    /**
     * The element of which the popupmenu is called
     */
    private AbstractGraphElement popupmenuElement;

    /**
     * True if an area should be marked
     */
    private boolean zoomCursor;

    /**
     * True if an area is being marked
     */
    private boolean marked;

    /**
     * True if the user marks several nodes (CTRL pressed)
     */
    private boolean marking;

    /**
     * Number of times the mouse have been dragged
     */
    private int drag;

    /**
     * Indicates if the camera is moved by the mouse
     */
    private boolean cameraMove = false;

    /**
     * Speed of mouse controlled camera movement
     */
    private double moveRate = 1.5;

    /**
     * The area which is being marked (as VirtualSpace coordinates)
     */
    private long vsCoordinates[] = new long[2];

    /**
     * The Rectangle to mark things
     */
    private VRectangle recMarker;

    /**
     * The MainDisplay of this EventPump
     */
    private MainDisplay mainDisplay;

    /**
     * Forwarder for events
     */
    private ActionListener forwarder = null;

    /**
     * Indicates if the user can adjust the zoom level with the JSpinner
     */
    private boolean adjustmentEnabled;

    /**
     * The current hidden nodes
     */
    private Vector<AbstractNode> hiddenNodes;

    /**
     * The marked lines
     */
    private Vector<AbstractEdge> markedLines;

    /**
     * The marked nodes
     */
    private Vector<TextNode> markedNodes;

    /**
     * Indicates if the marking is enabled
     */
    private boolean markingEnabled = false;

    /**
     * The Constructor
     * 
     * @param md
     *            The MainDisplay of this EventPump
     */
    public EventPump(MainDisplay md, SVEMain main) {
        this.main = main;
        this.forwarder = null;
        dragged = null;
        marked = false;
        drag = -1;
        zoomCursor = true;
        adjustmentEnabled = true;
        mainDisplay = md;
        linePoint = null;
        marking = false;
        hiddenNodes = new Vector<AbstractNode>();
        markedLines = new Vector<AbstractEdge>();
        markedNodes = new Vector<TextNode>();
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
        if (!marking && e.getKeyCode() == KeyEvent.VK_CONTROL) {
            Logger.getLogger(this.getClass().getCanonicalName());
            marking = true;
            if (forwarder != null) {
                forwarder.actionPerformed(new ActionEvent(this, 1,
                        "EnableMarking"));
            }
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
        LongPoint lp = null;

        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            Logger.getLogger(this.getClass().getCanonicalName());
            marking = false;
            if (forwarder != null) {
                forwarder.actionPerformed(new ActionEvent(this, 1,
                        "DisableMarking"));
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_UP)
            lp = new LongPoint(0, 1000);
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            lp = new LongPoint(0, -1000);
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            lp = new LongPoint(-1000, 0);
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            lp = new LongPoint(1000, 0);
        else
            lp = new LongPoint(0, 0);

        main.getMainDisplay().getVirtualSpaceManager().animator
                .createCameraAnimation(500, AnimManager.CA_TRANS_SIG, lp,
                        mainDisplay.getCamera().getID());

        mainDisplay.updateScrollBars();
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.xerox.VTM.engine.AppEventHandler#press1(com.xerox.VTM.engine.ViewPanel
     * , int, int, int)
     */
    public void click1(ViewPanel v, int mod, int jpx, int jpy, int clickNumber) {

        if (clickNumber == 2) {
            detectClickOnElement(v, jpx, jpy, false);

            if (dragged != null && forwarder != null) {
                forwarder.actionPerformed(new ActionEvent(dragged, 1,
                        "DoubleClickOnElement"));
            } else {
                LongPoint lp = new LongPoint(v.getMouse().vx - v.cams[0].posx,
                        v.getMouse().vy - v.cams[0].posy);
                main.getMainDisplay().getVirtualSpaceManager().animator
                        .createCameraAnimation(500, AnimManager.CA_TRANS_SIG,
                                lp, v.cams[0].getID());
                mainDisplay.updateScrollBars();
            }
            dragged = null;

            mainDisplay.getCameraView().mouse.setSensitivity(true);
            main.getMainDisplay().getVirtualSpaceManager().unstickFromMouse();
        } else if (mainDisplay.getCameraView().mouse.isSensitive()) {

            // In the zooming mode no glyph is sticked to the mouse
            if (zoomCursor)
                detectClickOnElement(v, jpx, jpy, false);
            else
                detectClickOnElement(v, jpx, jpy, true);

            // Forward marking events
            if (dragged != null && forwarder != null && marking) {
                forwarder.actionPerformed(new ActionEvent(dragged, 1,
                        "ElementMarked"));
                return;
            }

            if (!(dragged instanceof ConnectionPoint)
                    && connectionElement != null) {
                connectionElement.hideLinePoints();
                connectionElement = null;
            }

            if (dragged != null) {
                if (dragged instanceof AbstractEdge) {
                    connectionElement = (AbstractEdge) dragged;
                    connectionElement.showLinePoints();
                } else {
                    mainDisplay.getCameraView().mouse.setSensitivity(false);
                }
            } else if (forwarder != null) {
                forwarder.actionPerformed(new ActionEvent(this, 1,
                        "NoClickOnElement"));
            }
        } else {
            dragged = null;

            mainDisplay.getCameraView().mouse.setSensitivity(true);
            main.getMainDisplay().getVirtualSpaceManager().unstickFromMouse();
        }

        // The JPanel should always have the focus so it can receive the key
        // events
        mainDisplay.getJPanel().grabFocus();
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#click2(com.xerox.VTM.engine.ViewPanel,
     *      int, int, int, int)
     */
    public void click2(ViewPanel v, int mod, int jpx, int jpy, int clickNumber) {

        /*
         * For Center Camera on Point:
         * 
         * LongPoint lp = new LongPoint(v.getMouse().vx - v.cams[0].posx, v
         * 
         * .getMouse().vy - v.cams[0].posy);
         * 
         * main.getMainDisplay().getVirtualSpaceManager().animator
         * .createCameraAnimation(500, AnimManager.CA_TRANS_SIG, lp,
         * v.cams[0].getID()); mainDisplay.updateScrollBars();
         */
        main.getMainDisplay().fitInWindow();
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#click3(com.xerox.VTM.engine.ViewPanel,
     *      int, int, int, int)
     */
    public void click3(ViewPanel v, int mod, int jpx, int jpy, int clickNumber) {

        detectClickOnElement(v, jpx, jpy, false);

        if (!zoomCursor) {

            if (dragged != null) {

                popupmenuElement = dragged;

                dragged.showPopupMenu(mainDisplay.getJPanel(), jpx, jpy);

                // FIXME: The last must be deleted because if the action of the
                // popupMenu causes the component to shrink, the mouseExit is
                // not recognized
                v.parent.mouse.lastGlyphEntered = null;
                v.parent.mouse.glyphsUnderMouse[0] = null;
            } else {
                mainDisplay.getGraph().showPopupMenu(mainDisplay.getJPanel(),
                        jpx, jpy);
                popupmenuElement = null;
            }
        }
        dragged = null;

    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#press1(com.xerox.VTM.engine.ViewPanel,
     *      int, int, int)
     */
    public void press1(ViewPanel v, int mod, int jpx, int jpy) {

        // No Action if not the marker is being used
        if (!zoomCursor)
            return;

        marked = true;
        vsCoordinates[0] = v.parent.mouse.vx;
        vsCoordinates[1] = v.parent.mouse.vy;

        recMarker = new VRectangle();
        recMarker.setSensitivity(false);
        recMarker.setFill(false);
        main.getMainDisplay().getVirtualSpaceManager().addGlyph(recMarker,
                main.getVirtualSpaceName());
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#press1(com.xerox.VTM.engine.ViewPanel,
     *      int, int, int)
     */
    public void press3(ViewPanel v, int mod, int jpx, int jpy) {

        // No Action if not the marker is being used
        if (!zoomCursor)
            return;

        cameraMove = true;
        vsCoordinates[0] = jpx;
        vsCoordinates[1] = jpy;
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#mouseDragged(com.xerox.VTM.engine.ViewPanel,
     *      int, int, int, int)
     */
    public void mouseDragged(ViewPanel v, int mod, int buttonNumber, int jpx,
            int jpy) {
        long vx, vy, h, w;

        drag++;

        if (marked) {
            vx = v.parent.mouse.vx;
            vy = v.parent.mouse.vy;
            w = (vx - vsCoordinates[0]) / 2;
            h = (vsCoordinates[1] - vy) / 2;

            if (h <= 0 && w <= 0)
                return;

            vx = vsCoordinates[0] + w;
            vy = vsCoordinates[1] - h;

            recMarker.moveTo(vx, vy);
            recMarker.setWidth(w);
            recMarker.setHeight(h);
        }

        if (zoomCursor && cameraMove) {
            main.getMainDisplay().getCamera().move(
                    Math.round((vsCoordinates[0] - jpx) * moveRate),
                    Math.round((jpy - vsCoordinates[1]) * moveRate));
            vsCoordinates[0] = jpx;
            vsCoordinates[1] = jpy;
            mainDisplay.updateScrollBars();
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#release1(com.xerox.VTM.engine.ViewPanel,
     *      int, int, int)
     */
    public void release1(ViewPanel v, int mod, int jpx, int jpy) {
        // No Action if not the marker is being used

        if (!zoomCursor)
            return;

        if (marked) {
            if (drag > 10) {
                mainDisplay.centerCamera(vsCoordinates[0], vsCoordinates[1],
                        v.parent.mouse.vx, v.parent.mouse.vy);
            }
            marked = false;
            mainDisplay.getVirtualSpace().destroyGlyph(recMarker);
            recMarker = null;
        }
        drag = -1;
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#release3(com.xerox.VTM.engine.ViewPanel,
     *      int, int, int)
     */
    public void release3(ViewPanel v, int mod, int jpx, int jpy) {

        if (!zoomCursor)
            return;

        cameraMove = false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.xerox.VTM.engine.AppEventHandler#mouseMoved(com.xerox.VTM.engine.ViewPanel,
     *      int, int)
     */
    public void mouseMoved(ViewPanel v, int jpx, int jpy) {

        ConnectionPoint cp;
        Iterator<AbstractGraphElement> i;
        AbstractGraphElement e;
        if (dragged instanceof ConnectionPoint) {
            cp = (ConnectionPoint) dragged;
            cp.fixTranslation(v.parent.mouse.vx, v.parent.mouse.vy);
            i = cp.getLine().getDependents().iterator();
            while (i.hasNext()) {
                e = i.next();
                e.updateGlyph();
            }
        } else if (dragged instanceof AbstractNode) {
            updateAbstractNode((AbstractNode) dragged);
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e) {

        // long x, y;
        int w, h;
        float a;

        // x = mainDisplay.getCameraView().mouse.vx;
        // y = mainDisplay.getCameraView().mouse.vy;

        Camera c = mainDisplay.getCamera();
        a = (c.focal + Math.abs(c.altitude)) / c.focal;
        if (e.getWheelRotation() > 0) {
            c.altitudeOffset(a * 7);
        } else {
            c.altitudeOffset(-a * 7);
            // If the zooming should follow the mouse
            // c.moveTo(x, y);
            h = mainDisplay.getJPanel().getHeight();
            w = mainDisplay.getJPanel().getWidth();
            mainDisplay.getCameraView().mouse.moveTo(w / 2, h / 2);
        }

        mainDisplay.getToolBar().setSpinnerValue(
                new Integer(Math.round(mainDisplay.getCamera().getAltitude())));

        main.getMainDisplay().getVirtualSpaceManager().repaintNow();
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        mainDisplay.updateScrollBars();
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
        mainDisplay.updateScrollBars();
    }

    /**
     * (non-Javadoc)
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        mainDisplay.getCamera().setAltitude(
                Math.round(mainDisplay.getToolBar().getSpinnerValue()));
        mainDisplay.updateScrollBars();
        main.getMainDisplay().getVirtualSpaceManager().repaintNow();
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {

        if (adjustmentEnabled) {

            long[] vReg, vG;
            long x, y;

            vReg = mainDisplay.getCameraView().getVisibleRegion(
                    mainDisplay.getCamera());
            // vG = mainDisplay.getVirtualSpace().findFarmostGlyphCoords();
            vG = mainDisplay.findFarmostGlyphCoords();

            if ((((JScrollBar) e.getSource()).getOrientation()) == JScrollBar.HORIZONTAL) {
                x = vG[0] + e.getValue() + (vReg[2] - vReg[0]) / 2;
                mainDisplay.getCamera().moveTo(x, mainDisplay.getCamera().posy);
            } else {
                y = vG[1] - e.getValue() - (vReg[1] - vReg[3]) / 2;
                mainDisplay.getCamera().moveTo(mainDisplay.getCamera().posx, y);
            }
        }
    }

    /**
     * Disables the event logging of the scrollbars
     */
    public void disableAdjustment() {
        adjustmentEnabled = false;
    }

    /**
     * Enables the event logging of the scrollbars
     */
    public void enableAdjustment() {
        adjustmentEnabled = true;
    }

    /**
     * (non-Javadoc)
     * 
     * @see net.claribole.zvtm.engine.AnimationListener#cameraMoved()
     */
    public void cameraMoved() {
        mainDisplay.getToolBar().setSpinnerValue(
                new Integer(Math.round(mainDisplay.getCamera().getAltitude())));
        mainDisplay.updateScrollBars();
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
                "Command: " + e.getActionCommand());

        // Actions of graph elements
        if (e.getActionCommand().equals("DirectedEdge:AddConnectionPoint")
                && popupmenuElement instanceof BendableDirectedEdge) {

            BendableDirectedEdge d = (BendableDirectedEdge) popupmenuElement;

            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Adding connection point to DirectedEdge:" + d);

            d.addConnectionPoint(linePoint[0], linePoint[1]);
        } else if (e.getActionCommand().equals(
                "ConnectionPoint:RemoveConnectionPoint")
                && popupmenuElement instanceof ConnectionPoint) {

            ConnectionPoint p = (ConnectionPoint) popupmenuElement;

            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Removing connection point");

            p.remove();
            p.getLine().getParent().updateGlyph();
        }
        // Expand and Collapse
        else if (e.getActionCommand().equals("TextNode:CollapseAll")) {
            CollapsableTextNode node = ((CollapsableTextNode) popupmenuElement);

            if (!node.isCollapsed()) {
                Iterator<AbstractEdge> i = node.getEdgesOutgoing().iterator();
                while (i.hasNext()) {
                    AbstractNode cn = i.next().getEnd2();
                    if (!cn.equals(node) && !cn.isHidden()) {
                        node.addCollapsedNode(cn);
                        collapseSubNodes(node, cn);
                    }
                }
                node.bendEdges();
            }
        } else if (e.getActionCommand().equals("TextNode:Collapse")) {
            CollapsableTextNode node = ((CollapsableTextNode) popupmenuElement);

            if (!node.isCollapsed()) {
                Iterator<AbstractEdge> i = node.getEdgesOutgoing().iterator();
                while (i.hasNext()) {
                    AbstractNode cn = i.next().getEnd2();
                    if (!cn.equals(node) && !cn.isHidden()) {
                        node.addCollapsedNode(cn);
                    }
                }
                node.bendEdges();
            }
        } else if (e.getActionCommand().equals("TextNode:Expand")) {
            CollapsableTextNode node = ((CollapsableTextNode) popupmenuElement);
            if (node.isCollapsed())
                node.releaseCollapsedNodes();
        }
        // Show / Hide
        else if (e.getActionCommand().equals("TextNode:Hide")) {
            CollapsableTextNode node = ((CollapsableTextNode) popupmenuElement);

            node.hide();
            hideEdges(node.getEdges());
            hiddenNodes.add(node);
            main.getMainDisplay().getVirtualSpaceManager().repaintNow();

            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Hiding:" + node.getName().trim());
        } else if (e.getActionCommand().equals("DirectedGraph:ShowLastHidden")) {

            if (hiddenNodes.size() > 0) {
                CollapsableTextNode node = (CollapsableTextNode) hiddenNodes
                        .lastElement();

                node.show();
                showEdges(node.getEdges());
                hiddenNodes.remove(node);
                main.getMainDisplay().getVirtualSpaceManager().repaintNow();
                Logger.getLogger(this.getClass().getCanonicalName()).log(
                        Level.INFO,
                        "Showing last hidden:" + node.getName().trim());
            }
        } else if (e.getActionCommand().equals("DirectedGraph:ShowAll")) {
            CollapsableTextNode node;
            while (hiddenNodes.size() > 0) {
                node = (CollapsableTextNode) hiddenNodes.lastElement();
                node.show();
                showEdges(node.getEdges());
                hiddenNodes.remove(node);
            }
            main.getMainDisplay().getVirtualSpaceManager().repaintNow();
            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Showing all hidden ...");
        }
        // Mark incoming and outgoing edges
        else if (e.getActionCommand().equals("TextNode:MarkEdges")) {
            AbstractNode node = ((AbstractNode) popupmenuElement);
            AbstractEdge edge;
            Iterator<AbstractEdge> i;
            markedLines.addAll(node.getEdgesIncoming());
            i = node.getEdgesIncoming().iterator();
            while (i.hasNext()) {
                edge = i.next();
                edge.getLine().setLineColor(
                        (Color) CM.getConfig().get(
                                "graph.Textnode.MarkedColorIncomingEdges"));
                edge.updateGlyph();
            }
            markedLines.addAll(node.getEdgesOutgoing());
            i = node.getEdgesOutgoing().iterator();
            while (i.hasNext()) {
                edge = i.next();
                edge.getLine().setLineColor(
                        (Color) CM.getConfig().get(
                                "graph.Textnode.MarkedColorOutgoingEdges"));
                edge.updateGlyph();
            }
        } else if (e.getActionCommand().equals("TextNode:UnmarkEdges")) {
            AbstractNode node = ((AbstractNode) popupmenuElement);
            AbstractEdge edge;
            Iterator<AbstractEdge> i;

            markedLines.removeAll(node.getEdgesIncoming());
            i = node.getEdgesIncoming().iterator();
            while (i.hasNext()) {
                edge = i.next();
                edge.getLine().setLineColor(null);
                edge.updateGlyph();
            }
            markedLines.removeAll(node.getEdgesOutgoing());
            i = node.getEdgesOutgoing().iterator();
            while (i.hasNext()) {
                edge = i.next();
                edge.getLine().setLineColor(null);
                edge.updateGlyph();
            }
        } else if (e.getActionCommand().equals("TextNode:UnmarkAllEdges")) {
            AbstractEdge edge;
            Iterator<AbstractEdge> i;
            i = markedLines.iterator();
            while (i.hasNext()) {
                edge = i.next();
                edge.getLine().setLineColor(null);
                edge.updateGlyph();
            }
            markedLines.clear();
        }
        // Marking multiple Nodes
        else if (e.getActionCommand().equals("EnableMarking")) {
            markingEnabled = true;
        } else if (e.getActionCommand().equals("ElementMarked")) {
            TextNode n = (TextNode) popupmenuElement;

            n.setTextColor((Color) CM.getConfig().get(
                    "graph.Textnode.MarkedColor"));
            n.updateGlyph();
            markedNodes.add(n);

            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Marking Node:" + n.getName());
        } else if (e.getActionCommand().equals("DisableMarking")) {
            markingEnabled = false;
        } else if (e.getActionCommand().equals("NoClickOnElement")) {
            TextNode n;
            Iterator<TextNode> i;

            if (!markingEnabled) {
                i = markedNodes.iterator();
                while (i.hasNext()) {
                    n = i.next();
                    n.setTextColor(null);
                    n.updateGlyph();
                }
                markedNodes.clear();
            }
        }
        // Actions of the ToolBar
        else if (e.getActionCommand().equals("MainDisplayMenu:AlignView")) {

            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Align the view ...");

            mainDisplay.fitInWindow();
        } else if (e.getActionCommand().equals("MainDisplayMenu:SelectView")) {

            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Select view ...");

            mainDisplay.getCameraView().setCursorIcon(Cursor.HAND_CURSOR);

            zoomCursor = true;
        } else if (e.getActionCommand().equals("MainDisplayMenu:Curser")) {

            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.INFO, "Normal cursor selected");

            mainDisplay.getCameraView().setCursorIcon(Cursor.CROSSHAIR_CURSOR);

            zoomCursor = false;

        }
        // Delegate to forwarder if necessary
        else if (forwarder != null) {
            e.setSource(popupmenuElement);
            forwarder.actionPerformed(e);
        }

        // Update the diagram
        mainDisplay.getGraph().updateGlyphs();

        // The JPanel should always have the focus so it can receive the key
        // events
        mainDisplay.getJPanel().grabFocus();
    }

    /**
     * Indicates if the zooming cursor is active are not
     * 
     * @return True if the zooming curser is active / otherwise false
     */
    public boolean isZoomCursor() {
        return zoomCursor;
    }

    /**
     * Set the zooming cursor
     * 
     * @param zoomCurser
     *            New status of the zooming cursor
     */
    public void setZoomCursor(boolean zoomCurser) {
        if (zoomCursor) {
            mainDisplay.getCameraView().setCursorIcon(Cursor.HAND_CURSOR);
        } else {
            mainDisplay.getCameraView().setCursorIcon(Cursor.CROSSHAIR_CURSOR);
        }

        mainDisplay.getGraph().updateGlyphs();

        this.zoomCursor = zoomCurser;
    }

    /**
     * Set a forwarder for ActionEvents
     * 
     * @param forwarder
     *            The forwarder to delegate the events to
     */
    public void setForwarder(ActionListener forwarder) {
        this.forwarder = forwarder;
    }

    // Internal Routines
    // =================

    /**
     * Detects a click on an element of this UML Diagram
     * 
     * @param v
     *            The ViewPanel of the diagram
     * @param jpx
     *            The x coordinate of the click on the panel
     * @param jpy
     *            The y coordinate of the click on the panel
     * @param stickToMouse
     *            Indicates if the clicked glyph should be sticked to the mouse
     */
    private void detectClickOnElement(ViewPanel v, int jpx, int jpy,
            boolean stickToMouse) {

        Glyph g = v.lastGlyphEntered();
        Glyph[] gi = mainDisplay.getVirtualSpace().getVisibleGlyphList();
        Iterator<AbstractEdge> it;
        long maxgap = 25;
        long[] lp = null;

        dragged = null;

        // Check for path
        if (g == null) {
            it = mainDisplay.getGraph().getEdges().iterator();
            while (it.hasNext()) {
                AbstractEdge e = it.next();
                lp = e.edgeHit(v.parent.mouse.vx, v.parent.mouse.vy, maxgap);
                if (lp != null) {
                    dragged = ((ConnectionLine) e.getLine()).getParent();
                    linePoint = lp;
                    maxgap = lp[2];
                }
            }
            return;
        }

        // Check for important glyphs in selection
        for (int i = 0; i < gi.length; i++) {
            if (!(gi[i] instanceof VRectangle) || !gi[i].isVisible())
                continue;
            if (!gi[i].coordInside(jpx, jpy, 0))
                continue;
            if (gi[i].getOwner() instanceof ConnectionPoint) {
                g = gi[i];
                break;
            }
        }

        // Connection Points are not selectable when in zooming mode
        if (zoomCursor && g.getOwner() instanceof ConnectionPoint)
            return;

        if (g != null) {
            if (g.getCGlyph() != null) {
                dragged = (AbstractGraphElement) g.getCGlyph().getOwner();
                if (stickToMouse) {

                    if (g.getCGlyph().getSecondaryGlyphs() != null) {
                        for (int i = 0; i < g.getCGlyph().getSecondaryGlyphs().length; i++) {
                            mainDisplay.getVirtualSpace().onTop(
                                    g.getCGlyph().getSecondaryGlyphs()[i]
                                            .getGlyph());
                        }
                    }
                    main.getMainDisplay().getVirtualSpaceManager()
                            .stickToMouse(g.getCGlyph());
                }
            } else {
                dragged = (AbstractGraphElement) g.getOwner();
                if (stickToMouse) {
                    main.getMainDisplay().getVirtualSpaceManager()
                            .stickToMouse(g);
                }
            }
        }
    }

    /**
     * Handles the movement of an AbstractSurfaceElement (Updates all related
     * Objects)
     * 
     * @param e
     *            The AbstractSurfaceElement to update
     */
    private void updateAbstractNode(AbstractNode e) {
        Iterator<AbstractEdge> i;
        Iterator<AbstractGraphElement> i2;
        AbstractGraphElement el;
        AbstractEdge ec;

        i = e.getEdges().iterator();
        while (i.hasNext()) {
            ec = i.next();
            if (!ec.isHidden()) {
                i2 = ec.getLine().getDependents().iterator();
                while (i2.hasNext()) {
                    el = (AbstractGraphElement) i2.next();
                    el.updateGlyph();
                }
            }
        }
    }

    /**
     * Hide all edges of a node
     * 
     * @param e
     *            The edges of a node
     */
    private void hideEdges(Vector<AbstractEdge> e) {
        Iterator<AbstractEdge> i = e.iterator();
        while (i.hasNext()) {
            AbstractEdge ed = i.next();
            ed.hide();
        }
    }

    /**
     * Show all edges of a node
     * 
     * @param e
     *            The edges of a node
     */
    private void showEdges(Vector<AbstractEdge> e) {
        Iterator<AbstractEdge> i = e.iterator();
        while (i.hasNext()) {
            AbstractEdge ed = i.next();
            if (!ed.getEnd1().isHidden() && !ed.getEnd2().isHidden()) {
                ed.show();
            }
        }
    }

    /**
     * Collapse all sub nodes from a given node
     * 
     * @param c
     *            The node to collapse in
     * @param n
     *            The node whose sub nodes should be collapsed
     */
    private void collapseSubNodes(CollapsableTextNode c, AbstractNode n) {
        Iterator<AbstractEdge> i = n.getEdgesOutgoing().iterator();

        while (i.hasNext()) {
            AbstractNode cn = i.next().getEnd2();
            if (!cn.equals(c) && !cn.isHidden()) {
                c.addCollapsedNode(cn);
                collapseSubNodes(c, cn);
            }
        }
    }

}

/**
 * Project: sve
 */

package sve.structures.nodes;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import net.claribole.zvtm.glyphs.CGlyph;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;
import sve.structures.edges.BendableDirectedEdge;
import sve.structures.nodes.popupmenu.CollapsableTextNodeMenu;

import com.xerox.VTM.engine.LongPoint;
import com.xerox.VTM.glyphs.VCircle;

/**
 * This class represents a TextNode which can contain other nodes
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class CollapsableTextNode extends TextNode {

	private HashSet<AbstractNode> collapsedNodesLookupTable;

	private Vector<AbstractNode> collapsedNodes;

	private Vector<BendableDirectedEdge> bendedEdges;

	private Vector<LongPoint> collapsedNodesOffsets;

	private boolean collapsed;

	private VCircle collapsedCircle;

	/**
	 * The Constructor
	 * 
	 * @param owner
	 *            The UML diagram which shows this class
	 */
	public CollapsableTextNode(AbstractGraph owner, int shape) {
		super(owner, shape);
		collapsedNodesLookupTable = new HashSet<AbstractNode>();
		collapsedNodes = new Vector<AbstractNode>();
		bendedEdges = new Vector<BendableDirectedEdge>();
		collapsedNodesOffsets = new Vector<LongPoint>();
		collapsed = false;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see sve.structures.abstraction.AbstractGraphElement#init()
	 */
	public void init() {
		super.init();

		// Add a popup Menu
		this.popupMenu = new CollapsableTextNodeMenu(this);
		this.hasPopupMenu = true;
	}

	/**
	 * Indicates if this node is collapsed
	 * 
	 * @return True if this node is collapsed otherwise false
	 */
	public boolean isCollapsed() {
		return collapsed;
	}

	public void addCollapsedNode(AbstractNode n) {
		LongPoint off;

		if (n.equals(this))
			return;

		if (!collapsedNodes.contains(n)) {
			collapsedNodes.add(n);
			collapsedNodesLookupTable.add(n);
			off = new LongPoint(n.getX() - getX(), n.getY() - getY());
			collapsedNodesOffsets.add(off);
			n.hide();
			hideEdges(n.getEdges());

			if (!collapsed)
				addCollapsedGlyph();
		}
	}

	public void bendEdges() {
		AbstractEdge e;
		Iterator<AbstractNode> i = collapsedNodes.iterator();
		Iterator<AbstractEdge> i2;

		while (i.hasNext()) {
			i2 = i.next().getEdges().iterator();
			while (i2.hasNext()) {
				e = i2.next();

				// This node is the target
				if (!e.getEnd1().equals(this) && !e.getEnd1().isHidden()
						&& !collapsedNodesLookupTable.contains(e.getEnd1())) {

					((BendableDirectedEdge) e).bendEdgeEnd2(this);
					bendedEdges.add((BendableDirectedEdge) e);
					e.show();
				}
				// This node is the source
				else if (!e.getEnd2().equals(this) && !e.getEnd2().isHidden()
						&& !collapsedNodesLookupTable.contains(e.getEnd2())) {

					((BendableDirectedEdge) e).bendEdgeEnd1(this);
					bendedEdges.add((BendableDirectedEdge) e);
					e.show();
				}
			}
		}

		i = owner.getNodes().iterator();
		while (i.hasNext()) {
			i.next().onTop();
		}
	}

	public void releaseCollapsedNodes() {

		BendableDirectedEdge ae;
		Iterator<BendableDirectedEdge> it;

		it = bendedEdges.iterator();
		while (it.hasNext()) {
			ae = it.next();
			ae.straightenEdge();
		}

		for (int i = 0; i < collapsedNodes.size(); i++) {
			collapsedNodes.get(i).fixTranslation(
					getX() + collapsedNodesOffsets.get(i).x,
					getY() + collapsedNodesOffsets.get(i).y);
			collapsedNodes.get(i).show();
		}

		for (int i = 0; i < collapsedNodes.size(); i++) {
			showEdges(collapsedNodes.get(i).getEdges());
		}

		collapsedNodes.clear();
		bendedEdges.clear();
		collapsedNodesLookupTable.clear();
		collapsedNodesOffsets.clear();
		removeCollapsedGlyph();
	}

	private void addCollapsedGlyph() {

		if (collapsed)
			return;

		CGlyph cg = (CGlyph) glyph;

		collapsedCircle = new VCircle();
		collapsedCircle.setColor(Color.WHITE);
		collapsedCircle.sizeTo(8);

		owner.getActiveDisplay().getVirtualSpaceManager().addGlyph(collapsedCircle,
				owner.getActiveDisplay().getVirtualSpace());

		cg
				.addSecondaryGlyph(collapsedCircle, getWidth() + 10,
						getHeight() + 10);

		collapsed = true;

		updateGlyph();
	}

	private void removeCollapsedGlyph() {

		if (!collapsed)
			return;

		CGlyph cg = (CGlyph) glyph;
		cg.removeSecondaryGlyph(collapsedCircle);
		owner.getActiveDisplay().getVirtualSpace()
				.destroyGlyph(collapsedCircle);
		collapsedCircle = null;

		collapsed = false;

		updateGlyph();
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
				ed.updateGlyph();
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see sve.structures.abstraction.AbstractGraphElement#hide()
	 */
	public void hide() {
		super.hide();
		if (collapsedCircle != null)
			collapsedCircle.setVisible(false);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see sve.structures.abstraction.AbstractGraphElement#show()
	 */
	public void show() {
		super.show();
		if (collapsedCircle != null)
			collapsedCircle.setVisible(true);
	}
}

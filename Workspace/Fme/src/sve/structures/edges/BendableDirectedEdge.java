/**
 * Project: sve
 */

package sve.structures.edges;

import java.util.Vector;

import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;
import sve.structures.general.ConnectionLine;

import com.xerox.VTM.glyphs.VPath;

/**
 * This class represents a simple edge which connects two nodes
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class BendableDirectedEdge extends DirectedEdge {

	private Vector<ConnectionLine> oldLines;

	private Vector<AbstractNode> newNodesEnd1;
	
	private Vector<AbstractNode> newNodesEnd2;
	
	/**
	 * The Constructor
	 * 
	 * @param owner
	 *            The diagram this edge belongs to
	 * @param fromNode
	 *            The source of the edge
	 * @param toNode
	 *            The destination of the edge
	 */
	public BendableDirectedEdge(AbstractGraph owner, AbstractNode fromNode,
			AbstractNode toNode) {
		super(owner, fromNode, toNode);
		oldLines = new Vector<ConnectionLine>();

		newNodesEnd1 = new Vector<AbstractNode>();
		newNodesEnd2 = new Vector<AbstractNode>();
	}

	/**
	 * Changes the source of this edge
	 * 
	 * @param newToNode
	 *            The new source node of this edge
	 */
	public void bendEdgeEnd1(AbstractNode newFromNode) {

		oldLines.add(line);
		line.hide();

		if (!newFromNode.getEdges().contains(this)) {
			newFromNode.addEdge(this);
			newNodesEnd1.add(newFromNode);
		}

		line = new ConnectionLine(owner, this, newFromNode, this.end2);
		bendEdge();
	}

	/**
	 * Changes the target of this edge
	 * 
	 * @param newToNode
	 *            The new target node of this edge
	 */
	public void bendEdgeEnd2(AbstractNode newToNode) {

		oldLines.add(line);
		line.hide();

		if (!newToNode.getEdges().contains(this)) {
			newToNode.addEdge(this);
			newNodesEnd2.add(newToNode);
		}

		line = new ConnectionLine(owner, this, this.end1, newToNode);
		line.setName("created");
		bendEdge();
	}

	/**
	 * Move this edge back to it's previous source/target
	 */
	public void straightenEdge() {

		this.owner.getPaths().remove((VPath) line.getGlyphRepresentation());
		owner.getActiveDisplay().getVirtualSpace().destroyGlyph(
				line.getGlyphRepresentation());

		if (newNodesEnd1.size() != 0) {
			newNodesEnd1.lastElement().getEdges().remove(this);
			newNodesEnd1.lastElement().getEdgesIncoming().remove(this);
			newNodesEnd1.lastElement().getEdgesOutgoing().remove(this);
			newNodesEnd1.remove(newNodesEnd1.size() - 1);
		}
		if (newNodesEnd2.size() != 0) {
			newNodesEnd2.lastElement().getEdges().remove(this);
			newNodesEnd2.lastElement().getEdgesIncoming().remove(this);
			newNodesEnd2.lastElement().getEdgesOutgoing().remove(this);
			newNodesEnd2.remove(newNodesEnd2.size() - 1);
		}
		line = oldLines.lastElement();
		oldLines.remove(oldLines.size() - 1);
	}

	public AbstractNode getEnd1() {
		if (newNodesEnd1 != null && newNodesEnd1.size() != 0) return newNodesEnd1.lastElement();
		else return super.getEnd1();
	}

	public AbstractNode getEnd2() {
		if (newNodesEnd2 != null && newNodesEnd2.size() != 0) return newNodesEnd2.lastElement();
		else return super.getEnd2();
	}

	// Internal Methods
	// ================

	private void bendEdge() {
		line.addDependent(this);
		line.getDependents().addAll(oldLines.lastElement().getDependents());

		this.owner.getPaths().add((VPath) line.getGlyphRepresentation());

		line.setActionListener(getActionListener());
		line.init();

		line.setCutLineToPoint2(true);
		updateGlyph();

		if (isHidden())
			hide();
	}
}

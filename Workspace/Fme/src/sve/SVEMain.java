/**
 * Project: sve
 */

package sve;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import sve.engine.EventPump;
import sve.engine.MainDisplay;
import sve.layout.abstraction.AbstractGraphLayout;
import sve.logging.SVELogger;
import sve.structures.abstraction.AbstractGraph;

/**
 * Main class of the SVE project
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class SVEMain {

	/**
	 * Numbering for VirtualSpace
	 */
	private static int num = 0;

	/**
	 * Current name of VirtualSpace
	 */
	private String virtualSpaceName;

	/*
	 * The graph model
	 */
	private AbstractGraph graph;

	/*
	 * The view component of the SVE
	 */
	private MainDisplay mainDisplay;

	/*
	 * The controller component of the SVE
	 */
	private EventPump eventPump;

	/**
	 * The user defined node and edge menu extentions
	 */
	private Vector<String> nodeCaption, nodeEvents, edgeCaption, edgeEvents;

	public SVEMain() {
		// Enable logging
		SVELogger.enableSVELogging();
		num++;
		virtualSpaceName = "VirtualSpace" + num;
	}

	public void initGraph(AbstractGraph graph) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
				"Starting SVE");

		this.graph = graph;
		mainDisplay = new MainDisplay(graph, this);
		eventPump = new EventPump(mainDisplay, this);

		graph.setActionListener(eventPump);
		graph.setActiveDisplay(mainDisplay);
	}

	/**
	 * Display the graph
	 */
	public void layoutGraph(AbstractGraphLayout l) {

		// Destroy all glyphs from previous graphs
		mainDisplay.getVirtualSpaceManager().destroyGlyphsInSpace(
				mainDisplay.getVirtualSpace().getName());

		// Init the nodes directed
		graph.initNodes();
		l.doLayoutNodes(graph, mainDisplay);
		// Init the edges
		graph.initEdges();
		l.doLayoutEdges(graph, mainDisplay);

		mainDisplay.setListener(eventPump);
	}

	/**
	 * Menue Extention for nodes
	 * 
	 * @param caption
	 *            The labels to add
	 * @param events
	 *            The action events to add
	 */
	public void nodeMenuExtention(Vector<String> caption, Vector<String> events) {
		nodeCaption = caption;
		nodeEvents = events;
	}

	/**
	 * Menue Extention for edges
	 * 
	 * @param caption
	 *            The labels to add
	 * @param events
	 *            The action events to add
	 */
	public void edgeMenuExtention(Vector<String> caption, Vector<String> events) {
		edgeCaption = caption;
		edgeEvents = events;
	}

	/**
	 * Get the maindisplay
	 * 
	 * @return The maindisplay
	 */
	public MainDisplay getMainDisplay() {
		return mainDisplay;
	}

	/**
	 * Get the event pump
	 * 
	 * @return The event pump
	 */
	public EventPump getEventPump() {
		return eventPump;
	}

	/**
	 * Get user defined edge menu extention (Captions)
	 * 
	 * @return A Vector containing menu extention
	 */
	public Vector<String> getEdgeCaption() {
		return edgeCaption;
	}

	/**
	 * Get user defined edge menu extention (ActionEvents)
	 * 
	 * @return A Vector containing menu extention
	 */
	public Vector<String> getEdgeEvents() {
		return edgeEvents;
	}

	/**
	 * Get user defined node menu extention (Captions)
	 * 
	 * @return A Vector containing menu extention
	 */
	public Vector<String> getNodeCaption() {
		return nodeCaption;
	}

	/**
	 * Get user defined node menu extention (ActionEvents)
	 * 
	 * @return A Vector containing menu extention
	 */
	public Vector<String> getNodeEvents() {
		return nodeEvents;
	}

	public String getVirtualSpaceName() {
		return virtualSpaceName;
	}
}
package sve;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import sve.engine.ExportManager;
import sve.engine.PrintingManager;
import sve.gui.SimpleFrame;
import sve.layout.graph.CircleGraphLayout;
import sve.layout.graph.FlowLayout;
import sve.layout.graph.GridLayout;
import sve.layout.graph.SmartLayout;
import sve.layout.graph.TreeLayout;
import sve.structures.abstraction.AbstractNode;
import sve.structures.edges.BendableDirectedEdge;
import sve.structures.graphs.DirectedGraph;
import sve.structures.nodes.CollapsableTextNode;
import sve.structures.nodes.TextNode;

import com.digitprop.tonic.TonicLookAndFeel;

public class Main implements ActionListener {

	private static SVEMain main;

	private static DirectedGraph directedGraph;

	private static HashMap<String, CollapsableTextNode> nodes;

	public static void main(String args[]) {

		String file;

		// Create SVE instance
		main = new SVEMain();

		// Set Look and Feel
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		try {
			boolean lfFound = false;
			// First try special Look and Feel
			try {
				if (((String) CM.getConfig().get("gui.LookAndFeel"))
						.equals("Tonic")) {
					UIManager.setLookAndFeel(new TonicLookAndFeel());
					lfFound = true;
					Logger.getLogger(Main.class.getCanonicalName()).log(Level.INFO,
							"Using Tonic as LaF");					
				}
			} catch (Exception e) {

			}

			// Then try windows Look and Feel
			if (!lfFound) {
				for (int i = 0; i < lafs.length; i++) {
					if (lafs[i].getName().toLowerCase().contains("windows")) {
						UIManager.setLookAndFeel(lafs[i].getClassName());
						lfFound = true;
						Logger.getLogger(Main.class.getCanonicalName()).log(Level.INFO,
						"Using Windows Standard as LaF");					
						break;
					}
				}
			}
			// Then try motif Look and Feel
			if (!lfFound) {
				for (int i = 0; i < lafs.length; i++) {
					if (lafs[i].getName().toLowerCase().contains("motif")) {
						UIManager.setLookAndFeel(lafs[i].getClassName());
						lfFound = true;
						Logger.getLogger(Main.class.getCanonicalName()).log(Level.INFO,
						"Using Motif as LaF");					
						break;
					}
				}
			}
			if (!lfFound)
				throw new Exception();
		} catch (Exception e) {
			Logger
					.getLogger(Main.class.getCanonicalName())
					.log(Level.WARNING,
							"Can't find a Look&Feel Manager within the JVM on this machine");
		}

		if (args.length > 0) {
			file = args[0];
		} else {
			file = "graph.xml";
		}

		// Create an abstract graph
		directedGraph = new DirectedGraph();

		// Create HashMap for nodes
		nodes = new HashMap<String, CollapsableTextNode>();

		// Initialise the graph
		main.initGraph(directedGraph);

		Logger.getLogger(Main.class.getCanonicalName()).log(Level.INFO,
				"Parsing graph definition from: " + file);

		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			DefaultHandler handler = new ConfigFileParser();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(file);
		} catch (SAXException e) {
			Logger.getLogger(Main.class.getCanonicalName()).log(Level.SEVERE,
					"SAX: XML Parser Error");
		} catch (IOException e) {
			Logger.getLogger(Main.class.getCanonicalName()).log(Level.SEVERE,
					"SAX: IO Error");
		}

		// Layout the graph
		main.layoutGraph(new SmartLayout());

		// Display the graph
		SimpleFrame mainFrame = new SimpleFrame(main.getMainDisplay(), false);

		// Set the MenuBar
		mainFrame.setJMenuBar(new MainMenu(new Main()));

		// Graphic display start
		mainFrame.setVisible(true);
		mainFrame.repaint();
	}

	/**
	 * Add a TextNode to a DirectedGraph
	 * 
	 * @param g
	 *            The DirectedGraph
	 * @param name
	 *            The name of the TextNode
	 * @param shape
	 *            The shape of the node (@see sve.structures.nodes.TextNode)
	 * @return The added TextNode
	 */
	private static CollapsableTextNode addTextNode(DirectedGraph g,
			String name, int shape) {
		CollapsableTextNode tn = new CollapsableTextNode(g, shape);
		tn.setName(name);
		tn.setCaption(name);
		g.addNode(tn);
		return tn;
	}

	/**
	 * Add a DirectedEdge to a DirectedGraph
	 * 
	 * @param g
	 *            The DirectedGraph
	 * @param name
	 *            The name of the DirectedEdge
	 * @param n1
	 *            The source of the edge
	 * @param n2
	 *            The destination of the edge
	 * @return The added DirectedEdge
	 */
	private static BendableDirectedEdge addDirectedEdge(DirectedGraph g,
			String name, AbstractNode n1, AbstractNode n2) {
		BendableDirectedEdge de = new BendableDirectedEdge(g, n1, n2);
		de.setName(name);
		de.setCaption(name);
		g.addEdge(de);
		return de;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Menu:ExportScreenshot")) {
			ExportManager.exportScreenshotToPicture(main.getMainDisplay());
		} else if (e.getActionCommand().equals("Menu:ExportDiagram")) {
			ExportManager.exportDiagramToPicture(main.getMainDisplay());
		} else if (e.getActionCommand().equals("Menu:Print")) {
			PrintingManager.printDiagramWithSetup(main.getMainDisplay());
		} else if (e.getActionCommand().equals("Menu:LayoutSmart")) {
			try {
				main.layoutGraph(new SmartLayout());
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Can't execute layout algorithm on present graph.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getActionCommand().equals("Menu:LayoutTree")) {
			try {
				main.layoutGraph(new TreeLayout());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"Can't execute layout algorithm on present graph.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getActionCommand().equals("Menu:LayoutCircle")) {
			try {
				main.layoutGraph(new CircleGraphLayout());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"Can't execute layout algorithm on present graph.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getActionCommand().equals("Menu:LayoutGrid")) {
			try {
				main.layoutGraph(new GridLayout());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"Can't execute layout algorithm on present graph.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getActionCommand().equals("Menu:LayoutFlow")) {
			try {
				main.layoutGraph(new FlowLayout());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"Can't execute layout algorithm on present graph.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Internal Class
	// ==============

	private static class ConfigFileParser extends DefaultHandler {

		public ConfigFileParser() {
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			CollapsableTextNode tn;

			try {
				if (qName.equals("Node")) {
					if (attributes.getValue(1).equals("BoxedShape"))
						tn = addTextNode(directedGraph, attributes.getValue(0),
								TextNode.BoxedShape);
					else if (attributes.getValue(1).equals("DiamondShape"))
						tn = addTextNode(directedGraph, attributes.getValue(0),
								TextNode.DiamondShape);
					else if (attributes.getValue(1).equals("OvalShape"))
						tn = addTextNode(directedGraph, attributes.getValue(0),
								TextNode.OvalShape);
					else if (attributes.getValue(1).equals("TrapezeShape"))
						tn = addTextNode(directedGraph, attributes.getValue(0),
								TextNode.TrapezeShape);
					else
						tn = addTextNode(directedGraph, attributes.getValue(0),
								TextNode.RectangularShape);

					nodes.put(attributes.getValue(0), tn);
				} else if (qName.equals("Edge")) {
					addDirectedEdge(directedGraph, attributes.getValue(0),
							nodes.get(attributes.getValue(1)), nodes
									.get(attributes.getValue(2)));
				}
			} catch (Exception ex) {
				Logger.getLogger(CM.class.getCanonicalName()).log(
						Level.WARNING,
						"Error in creating " + qName.toLowerCase() + " :"
								+ attributes.getValue(0));
			}
		}
	}
}

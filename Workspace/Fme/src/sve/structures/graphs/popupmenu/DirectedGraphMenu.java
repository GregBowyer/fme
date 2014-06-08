package sve.structures.graphs.popupmenu;

import javax.swing.*;

import sve.structures.graphs.*;

/**
 * This class draws a popup menu on the screen
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class DirectedGraphMenu
    extends JPopupMenu {

  /**
   * Field for identifying a class
   */
  private static final long serialVersionUID = -5360531926125446737L;

  /**
   * The names of the menu entries
   */
  private String name[] = { "Show last hidden node", "Show all hidden nodes",
      "Unmark all edges" };

  /**
   * The name of the events
   */
  private String event[] = { "DirectedGraph:ShowLastHidden",
      "DirectedGraph:ShowAll","TextNode:UnmarkAllEdges" };

  /**
   * The Constructor
   * 
   * @param owner The owner of this PopupMenu
   */
  public DirectedGraphMenu(DirectedGraph owner) {
    super("Menu");

    JLabel title = new JLabel("Menu");
    title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(title);
    addSeparator();

    int i;
    JMenuItem item;

    // Menu items
    for (i = 0; i < name.length; i++) {
      item = new JMenuItem(name[i]);
      item.setActionCommand(event[i]);
      item.addActionListener(owner.getActionListener());
      add(item);
    }
  }
}

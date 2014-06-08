/**
 * Project: fuml
 */

package sve.structures.general.popupmenu;

import javax.swing.*;

import sve.structures.general.*;

/**
 * This class draws a popup menu on the screen
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class ConnectionPointMenu
    extends JPopupMenu {

  /**
   * Field for identifying a class
   */
  private static final long serialVersionUID = -6376445825845032304L;

  /**
   * The names of the menu entries
   */
  private String name[] = { "Remove connection point" };

  /**
   * The name of the events
   */
  private String event[] = { "ConnectionPoint:RemoveConnectionPoint" };

  /**
   * The Constructor
   * 
   * @param owner The owner of this PopupMenu
   */
  public ConnectionPointMenu(ConnectionPoint owner) {
    super("Connection Point Menu");

    JLabel title = new JLabel("Connection Point Menu");
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

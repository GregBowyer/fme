package sve.structures.edges.popupmenu;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sve.structures.edges.DirectedEdge;

/**
 * This class draws a popup menu on the screen
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class DirectedEdgeMenu extends JPopupMenu {

    /**
     * Field for identifying a class
     */
    private static final long serialVersionUID = -5360531926125446737L;

    /**
     * The names of the menu entries
     */
    private String name[] = { "Add connection point" };

    /**
     * The name of the events
     */
    private String event[] = { "DirectedEdge:AddConnectionPoint" };

    /**
     * The Constructor
     * 
     * @param owner
     *            The owner of this PopupMenu
     */
    public DirectedEdgeMenu(DirectedEdge owner) {
        super("Directed edge menu");

        JLabel title = new JLabel("Directed edge menu");
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        add(title);
        addSeparator();

        int i;
        JMenuItem item;
        Vector<String> caption, events;

        if (owner.getOwner().getActiveDisplay().main.getEdgeCaption() != null) {
            caption = owner.getOwner().getActiveDisplay().main.getEdgeCaption();
            events = owner.getOwner().getActiveDisplay().main.getEdgeEvents();
            for (i = 0; i < caption.size(); i++) {
                item = new JMenuItem(caption.get(i));
                item.setActionCommand(events.get(i));
                item.addActionListener(owner.getActionListener());
                add(item);
            }
            addSeparator();
        }

        // Menu items
        for (i = 0; i < name.length; i++) {
            item = new JMenuItem(name[i]);
            item.setActionCommand(event[i]);
            item.addActionListener(owner.getActionListener());
            add(item);
        }
    }
}

package sve.structures.nodes.popupmenu;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sve.structures.nodes.CollapsableTextNode;

/**
 * This class draws a popup menu on the screen
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class CollapsableTextNodeMenu extends JPopupMenu {

    /**
     * Field for identifying a class
     */
    private static final long serialVersionUID = -5360531926125446737L;

    /**
     * The names of the menu entries
     */
    private String name[] = { "Hide", "Collapse First Level",
        "Collapse All Levels", "Expand", "Mark Edges", "Unmark Edges" };

    /**
     * The name of the events
     */
    private String event[] = { "TextNode:Hide", "TextNode:Collapse",
        "TextNode:CollapseAll", "TextNode:Expand", "TextNode:MarkEdges",
        "TextNode:UnmarkEdges" };

    /**
     * The Constructor
     * 
     * @param owner
     *            The owner of this PopupMenu
     */
    public CollapsableTextNodeMenu(CollapsableTextNode owner) {
        super("Menu");

        JLabel title = new JLabel("Menu");
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        add(title);
        addSeparator();

        int i;
        JMenuItem item;
        Vector<String> caption, events;

        if (owner.getOwner().getActiveDisplay().main.getNodeCaption() != null) {
            caption = owner.getOwner().getActiveDisplay().main.getNodeCaption();
            events = owner.getOwner().getActiveDisplay().main.getNodeEvents();
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
            if (i == 2)
                add(new Separator());
            else if (i == 5)
                add(new Separator());
            item = new JMenuItem(name[i]);
            item.setActionCommand(event[i]);
            item.addActionListener(owner.getActionListener());
            add(item);
        }

    }
}

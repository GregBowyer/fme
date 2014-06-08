/**
 * Project: sve
 */

package sve.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JFrame;

import sve.CM;
import sve.engine.MainDisplay;

/**
 * This class implements a simple frame to display the MainDisplay
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */
public class SimpleFrame
    extends JFrame {

    private static final long serialVersionUID = 1L;

    private MainDisplay mainDisplay;

    private static boolean dispose = false;

    /**
     * The Constructor
     * 
     * @param md
     *            The MainDisplay to display
     * @param dispose
     *            True if the window should be disposed on closing / False if
     *            the window should call System.exit(0)
     */
    public SimpleFrame(MainDisplay md, boolean dispose) {
        // Set the title
        super("STRL Visualisation Engine");

        SimpleFrame.dispose = dispose;

        HashMap<String, Object> c = CM.getConfig();

        mainDisplay = md;

        // Handling for window closing event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (SimpleFrame.dispose) {
                    ((SimpleFrame) e.getSource()).dispose();
                } else
                    System.exit(0);
            }
        });

        // Set initial size
        setSize(((Integer) c.get("gui.MainFrameInitWidth")).intValue(),
            ((Integer) c.get("gui.MainFrameInitHeight")).intValue());
        setLocation(((Integer) c.get("gui.MainFrameInitXPos")).intValue(),
            ((Integer) c.get("gui.MainFrameInitYPos")).intValue());

        getContentPane().add(mainDisplay.getJPanel());
        getContentPane().add(mainDisplay.getToolBar(), BorderLayout.PAGE_START);
    }

    public MainDisplay getMainDisplay() {
        return mainDisplay;
    }
}

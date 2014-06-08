/**
 * Project: sve
 */

package sve.layout.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import sve.engine.MainDisplay;
import sve.layout.abstraction.AbstractGraphLayout;
import sve.structures.abstraction.AbstractEdge;
import sve.structures.abstraction.AbstractGraph;
import sve.structures.abstraction.AbstractNode;

/**
 * This layout arrages the nodes in a grid starting with the nodes with the most
 * outgoing calls
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class GridLayout
    extends AbstractGraphLayout {

    /**
     * (non-Javadoc)
     * 
     * @see sve.layout.abstraction.AbstractGraphLayout#doLayout(sve.structures.abstraction.AbstractGraph)
     */
    public void doLayoutNodes(AbstractGraph ag, MainDisplay md) {
        long n = 0, nh = 0, maxheight = 0;
        Iterator<AbstractEdge> i;
        Iterator<AbstractNode> i2, i3;
        AbstractNode c;
        Vector<AbstractNode> v;

        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
            "Doing the GridLayout");

        // HashMap count outgoing calls of each node
        HashMap<AbstractNode, Integer> fc_out = new HashMap<AbstractNode, Integer>();
        i = ag.getEdges().iterator();
        while (i.hasNext()) {
            c = (AbstractNode) i.next().getEnd1();
            if (fc_out.get(c) == null)
                fc_out.put(c, new Integer(1));
            else
                fc_out.put(c, new Integer(((Integer) fc_out.get(c)).intValue() + 1));
        }
        i2 = ag.getNodes().iterator();
        while (i.hasNext()) {
            c = i2.next();
            if (fc_out.get(c) == null)
                fc_out.put(c, new Integer(0));
        }

        // Sort the nodes according to the outgoing calls
        HashMap<Integer, Vector<AbstractNode>> classMap = new HashMap<Integer, Vector<AbstractNode>>();
        i2 = fc_out.keySet().iterator();
        while (i.hasNext()) {
            c = i2.next();
            if (classMap.get(fc_out.get(c)) == null) {
                v = new Vector<AbstractNode>();
                v.add(c);
                classMap.put(fc_out.get(c), v);
            } else {
                v = (Vector<AbstractNode>) classMap.get(fc_out.get(c));
                v.add(c);
            }
        }

        // Sort the KeySet of the HashMap
        ArrayList<Integer> l = Collections.list(Collections.enumeration(classMap
            .keySet()));
        Comparator<Integer> comparator = new Comparator<Integer>() {
            public int compare(Integer o, Integer o2) {
                if (o.intValue() > o2.intValue())
                    return -1;
                else if (o.intValue() == o2.intValue())
                    return 0;
                else
                    return 1;
            }
        };
        Collections.sort(l, comparator);

        // Arrange the classes according to their outgoing associations
        Iterator<Integer> ii = l.iterator();
        while (ii.hasNext()) {
            i3 = ((Vector<AbstractNode>) classMap.get(ii.next())).iterator();
            while (i3.hasNext()) {
                c = (AbstractNode) i3.next();

                // IMPORTANT: The getWidth() method of the glyph returns only
                // half the height in case of a rectangle
                if (n != 0)
                    n += c.getWidth();

                if (nh == 0)
                    c.fixTranslation(n, -nh);
                else
                    c.fixTranslation(n, -nh - c.getHeight());

                // X Offset for next class
                n += c.getWidth() + 150;

                // Compute maxheight
                if (maxheight < c.getHeight() && nh == 0)
                    maxheight = c.getHeight();
                else if (maxheight < c.getHeight() * 2 && nh != 0)
                    maxheight = c.getHeight() * 2;

                // Next Line
                if (n > md.getJPanel().getWidth()) {
                    n = 0;
                    nh += maxheight + 150;
                    maxheight = -1;
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see sve.layout.abstraction.AbstractGraphLayout#doLayoutEdges(sve.structures.abstraction.AbstractGraph,
     *      sve.engine.MainDisplay)
     */
    public void doLayoutEdges(AbstractGraph ag, MainDisplay md) {
    }

}

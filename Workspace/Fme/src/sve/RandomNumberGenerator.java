/**
 * Project: fuml
 */

package sve;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class generates random numbers which are unique in a specified group
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public class RandomNumberGenerator {

    /**
     * This HashMap holds the groups of Numbers
     */
    private static Map<String, Set<Integer>> groups = new HashMap<String, Set<Integer>>();

    /**
     * The Constructor
     */
    private RandomNumberGenerator() {

    }

    /**
     * Generates a random number which is unique in a given group (except the
     * group is full or the maxTry is reached)
     * 
     * @param group
     *            A group of numbers
     * @param from
     *            Lowest possible number
     * @param to
     *            Highest possible number
     * @param maxTry
     *            Maximum of tries to find a random number
     */
    public static int genRandomNumber(String group, int from, int to, int maxTry) {

        Integer num;
        int i = 0;

        if (groups.get(group) == null)
            groups.put(group, new HashSet<Integer>());

        num = new Integer(Math.round(Math.round(from + Math.random() * (to - from))));
        while (groups.get(group).contains(num) && i <= maxTry) {
            num = new Integer(Math.round(Math.round(from + Math.random() * (to - from))));
            i++;
        }

        groups.get(group).add(num);

        return num.intValue();
    }
}

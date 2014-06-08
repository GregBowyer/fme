/**
 * Project: fuml
 */

package sve.logging;

import java.io.*;
import java.util.logging.*;

/**
 * This class represents the logging subsystem of SVE
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class SVELogger {

    /**
     * Enables the SVE logging subsystem
     */
    public static void enableSVELogging() {

        (new File("SVE_log.txt")).delete();

        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setUseParentHandlers(false);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).addHandler(
            new GlobalHandler());

        Logger.getLogger("sve").setUseParentHandlers(false);
        Logger.getLogger("sve").addHandler(new DefaultHandler());
    }

    /**
     * Disables the SVE logging subsystem
     */
    public static void disableSVELogging() {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setUseParentHandlers(true);
        Logger.getLogger("sve").setUseParentHandlers(true);
    }
}

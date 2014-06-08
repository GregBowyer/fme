/**
 * Project: fuml
 */

package sve.logging;

import java.io.*;
import java.util.logging.*;

/**
 * The default handler for the logging
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class DefaultHandler
    extends Handler {

  /**
   * (non-Javadoc)
   * 
   * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
   */
  public void publish(LogRecord record) {

    String logMSG = record.getLevel().getName() + " " + record.getLoggerName()
        + "." + record.getSourceMethodName() + "(): " + record.getMessage();

    System.out.println(logMSG);
    
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(
          "SVE_log" + ".txt", true));
      out.write(logMSG + "\r\n");
      out.close();
    }
    catch (Exception e) {
      System.out.println("Logging system failure ...");
      e.printStackTrace();
    }
  }

  /**
   * (non-Javadoc)
   * 
   * @see java.util.logging.Handler#flush()
   */
  public void flush() {
  }

  /**
   * (non-Javadoc)
   * 
   * @see java.util.logging.Handler#close()
   */
  public void close() {
  }
}

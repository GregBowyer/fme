/**
 * Project: sve
 */

package sve.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JOptionPane;

public class PrintingManager {

  /**
   * The Constructor
   */
  private PrintingManager() {
  }

  /**
   * Prints a diagram
   * 
   * @param md The MainDisplay which displays the diagram
   */
  public static void printDiagram(MainDisplay md) {

    Logger.getLogger(PrintingManager.class.getCanonicalName()).log(Level.INFO,
        "Printing diagram");

    md.getCameraView().mouse.setVisibility(false);
    md.getVirtualSpaceManager().repaintNow();

    try {
      PrintService service = PrintServiceLookup.lookupDefaultPrintService();
      if (service != null) {
        DocPrintJob job = service.createPrintJob();
        BufferedImage screenShot;
        screenShot = md.getCameraView().getImage();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write(screenShot, "png", buffer);
        Doc doc = new SimpleDoc(buffer.toByteArray(), DocFlavor.BYTE_ARRAY.PNG,
            null);
        job.print(doc, null);
      }
      else {
        JOptionPane.showMessageDialog(md.getJPanel(),
            "Can't find suitable printers", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    catch (PrintException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    md.getCameraView().mouse.setVisibility(true);
  }

  /**
   * Prints a diagram and asks for printer properties
   * 
   * @param md The MainDisplay which displays the diagram
   */
  public static void printDiagramWithSetup(MainDisplay md) {

    Logger.getLogger(PrintingManager.class.getCanonicalName()).log(Level.INFO,
        "Printing diagram");

    md.getCameraView().mouse.setVisibility(false);
    md.getVirtualSpaceManager().repaintNow();

    try {
      PrintService[] services = PrintServiceLookup.lookupPrintServices(
          DocFlavor.INPUT_STREAM.PNG, null);
      PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
      if (services.length > 0) {
        PrintService service = ServiceUI.printDialog(null, 50, 50, services,
            services[0], DocFlavor.INPUT_STREAM.PNG, attributes);
        if (service != null) {
          DocPrintJob job = service.createPrintJob();
          BufferedImage screenShot;
          screenShot = md.getCameraView().getImage();
          ByteArrayOutputStream buffer = new ByteArrayOutputStream();
          ImageIO.write(screenShot, "png", buffer);
          Doc doc = new SimpleDoc(buffer.toByteArray(),
              DocFlavor.BYTE_ARRAY.PNG, null);
          job.print(doc, attributes);
        }
      }
    }
    catch (PrintException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    md.getCameraView().mouse.setVisibility(true);    
  }
}

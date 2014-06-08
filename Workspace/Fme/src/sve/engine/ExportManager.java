/**
 * Project: sve
 */

package sve.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import com.xerox.VTM.svg.SVGWriter;

public class ExportManager {

  private static boolean exportScreenshot = false;

  private static ExportManager singleton;

  private ExportManager() {
  }

  /**
   * Exports a screenshot from a diagram to a file (export only what is
   * currently visible)
   * 
   * @param md The MainDisplay which displays the diagram
   */
  public static void exportScreenshotToPicture(MainDisplay md) {
    exportScreenshot = true;
    exportDiagramToPicture(md);
    exportScreenshot = false;
  }

  /**
   * Exports a diagram to a file
   * 
   * @param md The MainDisplay which displays the diagram
   */
  public static void exportDiagramToPicture(MainDisplay md) {

    singleton = new ExportManager();

    JFileChooser chooser = new JFileChooser(new File(System
        .getProperty("user.dir")));

    chooser.setLocale(Locale.UK);
    if (!exportScreenshot) chooser.setFileFilter(new SVGFileFilter());
    else {
      chooser.addChoosableFileFilter(new BMPFileFilter());
      chooser.addChoosableFileFilter(new PNGFileFilter());
      chooser.addChoosableFileFilter(new JPGFileFilter());
    }

    md.getCameraView().mouse.setVisibility(false);
    md.main.getMainDisplay().getVirtualSpaceManager().repaintNow();

    if (chooser.showSaveDialog(md.getJPanel()) == JFileChooser.APPROVE_OPTION) {
      try {

        File f = chooser.getSelectedFile();

        // Add extension if necessary
        if (chooser.getFileFilter() instanceof JPGFileFilter
            && !f.getAbsolutePath().endsWith(".jpg")) {
          f = new File(f.getAbsolutePath() + ".jpg");
        }
        if (chooser.getFileFilter() instanceof BMPFileFilter
            && !f.getAbsolutePath().endsWith(".bmp")) {
          f = new File(f.getAbsolutePath() + ".bmp");
        }
        else if (chooser.getFileFilter() instanceof PNGFileFilter
            && !f.getAbsolutePath().endsWith(".png")) {
          f = new File(f.getAbsolutePath() + ".png");
        }
        else if (chooser.getFileFilter() instanceof SVGFileFilter
            && !f.getAbsolutePath().endsWith(".svg")) {
          f = new File(f.getAbsolutePath() + ".svg");
        }

        if (f.exists()) {
          if (JOptionPane.showConfirmDialog(md.getJPanel(), "Overwrite "
              + f.getName() + " ?", "Question", JOptionPane.YES_NO_OPTION) != 0) return;
        }

        if (f.getAbsolutePath().endsWith(".jpg")) singleton.exportToJPG(f, md);
        else if (f.getAbsolutePath().endsWith(".bmp")) singleton.exportToBMP(f,
            md);
        else if (f.getAbsolutePath().endsWith(".png")) singleton.exportToPNG(f,
            md);
        else if (f.getAbsolutePath().endsWith(".svg")) singleton.exportToSVG(f,
            md);
      }
      catch (IOException e) {
        Logger.getLogger(ExportManager.class.getCanonicalName()).log(
            Level.INFO,
            "IO Exception while saving exporting as picture. Filter:"
                + chooser.getFileFilter().getClass().toString()
                + " Screenshot:" + exportScreenshot);
      }
    }

    md.getCameraView().mouse.setVisibility(true);

  }

  /**
   * Exports a diagram to JPG (only the visible area)
   * 
   * @param file The file to write
   * @param diagram The diagram to export
   */
  private void exportToJPG(File file, MainDisplay md) throws IOException {

    BufferedImage screenShot;

    screenShot = md.getCameraView().getImage();

    Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
        "Export to JPG Picture:" + file.getName());

    if (!ImageIO.write(screenShot, "jpg", file)) {
      JOptionPane.showMessageDialog(md.getJPanel(),
          "Can't find suitable encoder plugin", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Exports a diagram to PNG (only the visible area)
   * 
   * @param file The file to write
   * @param diagram The diagram to export
   */
  private void exportToPNG(File file, MainDisplay md) throws IOException {

    BufferedImage screenShot;

    screenShot = md.getCameraView().getImage();

    Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
        "Export to PNG Picture:" + file.getName());

    if (!ImageIO.write(screenShot, "png", file)) {
      JOptionPane.showMessageDialog(md.getJPanel(),
          "Can't find suitable encoder plugin", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Exports a diagram to BMP (only the visible area)
   * 
   * @param file The file to write
   * @param diagram The diagram to export
   */
  private void exportToBMP(File file, MainDisplay md) throws IOException {

    BufferedImage screenShot;

    screenShot = md.getCameraView().getImage();

    Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
        "Export to BMP Picture:" + file.getName());

    if (!ImageIO.write(screenShot, "bmp", file)) {
      JOptionPane.showMessageDialog(md.getJPanel(),
          "Can't find suitable encoder plugin", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Exports a diagram to SVG
   * 
   * @param file The file to write
   * @param diagram The diagram to export
   */
  private void exportToSVG(File file, MainDisplay md) throws IOException {
    Document image;

    image = new SVGWriter().exportVirtualSpace(md.getVirtualSpace(),
        new DOMImplementationImpl(), file);

    OutputFormat format = new OutputFormat(image);
    format.setIndenting(true);
    format.setLineWidth(0);
    format.setPreserveSpace(true);

    XMLSerializer serializer = new XMLSerializer(new FileWriter(file), format);
    serializer.asDOMSerializer();
    serializer.serialize(image);

    Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,
        "Export to SVG Picture:" + file.getName());
  }

  /**
   * File Filter Class for JPG Files
   */
  private static class JPGFileFilter
      extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
      if (f.getName().endsWith(".jpg")) return true;
      else if (f.isDirectory()) return true;
      else return false;
    }

    public String getDescription() {
      return "JPG raster images";
    }
  }

  /**
   * File Filter Class for PNG Files
   */
  private static class PNGFileFilter
      extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
      if (f.getName().endsWith(".png")) return true;
      else if (f.isDirectory()) return true;
      else return false;
    }

    public String getDescription() {
      return "PNG raster images";
    }
  }

  /**
   * File Filter Class for BMP Files
   */
  private static class BMPFileFilter
      extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
      if (f.getName().endsWith(".bmp")) return true;
      else if (f.isDirectory()) return true;
      else return false;
    }

    public String getDescription() {
      return "BMP raster images";
    }
  }

  /**
   * File Filter Class for SVG Files
   */
  private static class SVGFileFilter
      extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
      if (f.getName().endsWith(".svg")) return true;
      else if (f.isDirectory()) return true;
      else return false;
    }

    public String getDescription() {
      return "SVG vector images";
    }
  }
}

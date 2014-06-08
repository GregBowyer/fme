/**
 * Project: fuml
 */

package sve;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class Manages the configurations
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau</A>
 */

public class CM {

  /**
   * The static reference to the singleton of this class
   */
  private static CM singleton = new CM();

  /**
   * The current configuration
   */
  private HashMap<String,Object> config;

  /**
   * The Constructor
   */
  private CM() {

    config = new HashMap<String,Object>();

    config.put("gui.DesktopPaneBackground", Color.WHITE);
    config.put("gui.MainFrameInitWidth", new Integer(800));
    config.put("gui.MainFrameInitHeight", new Integer(600));
    config.put("gui.MainFrameInitXPos", new Integer(50));
    config.put("gui.MainFrameInitYPos", new Integer(50));

    config.put("graph.Textnode.Font", "Arial-BOLD-24");
    config.put("graph.Textnode.FontColor",Color.BLACK);
    config.put("graph.Textnode.BackgroundColor", Color.WHITE);
    config.put("graph.Textnode.FrameColor",Color.BLACK);
    config.put("graph.Textnode.MarkedColor",Color.CYAN);
    config.put("graph.Textnode.MarkedColorIncomingEdges",Color.BLUE);
    config.put("graph.Textnode.MarkedColorOutgoingEdges",Color.GREEN);

    config.put("graph.ConnectionPoint.BackgroundColor", Color.RED);
    config.put("graph.ConnectionPoint.FrameColor",Color.BLACK);

    config.put("graph.ConnectionLine.Color",Color.BLACK);
    config.put("graph.ConnectionLine.MarkedColor",Color.RED);
    
    config.put("graph.DirectedEdge.CaptionColor", Color.RED);
    config.put("graph.DirectedEdge.Font", "Arial-BOLD-24");
    
    if (new File("sve-config.xml").exists()) {

        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            DefaultHandler handler = new ConfigFileParser();
            xmlReader.setContentHandler(handler);
            xmlReader.parse("sve-config.xml");
        } catch (SAXException e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(
                Level.SEVERE, "SAX: XML Parser Error");
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(
                Level.SEVERE, "SAX: IO Error");
        }
    }
    
  }

  /**
   * Gets the current configuration
   * 
   * @return Contains all configurationparameters
   */
    public static HashMap<String, Object> getConfig() {
    return singleton.config;
  }

  // Internal Class
  // ==============

  private class ConfigFileParser extends DefaultHandler {

      public ConfigFileParser() {
      }

      public void startElement(String uri, String localName, String qName,
          Attributes attributes) throws SAXException {
          try {
              if (qName.equals("Config")) {

                  if (attributes.getValue(1).equals("String")) {
                      config.put(attributes.getValue(0), attributes
                          .getValue(2));
                  } else if (attributes.getValue(1).equals("Integer")) {
                      config.put(attributes.getValue(0), Integer
                          .parseInt(attributes.getValue(2)));
                  } else if (attributes.getValue(1).equals("Color")) {
                      int r, g, b;
                      r = Integer
                          .parseInt(attributes.getValue(2).split("#")[0]);
                      g = Integer
                          .parseInt(attributes.getValue(2).split("#")[1]);
                      b = Integer
                          .parseInt(attributes.getValue(2).split("#")[2]);
                      config.put(attributes.getValue(0), new Color(r, g, b));
                  }
              }
          } catch (Exception ex) {
              Logger.getLogger(CM.class.getCanonicalName()).log(
                  Level.WARNING,
                  "Couldn't read config:" + attributes.getValue(1));
          }
      }
  }
}

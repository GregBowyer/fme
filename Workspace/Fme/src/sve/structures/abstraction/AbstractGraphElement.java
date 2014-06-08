/**
 * Project: sve
 */

package sve.structures.abstraction;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;

import sve.RandomNumberGenerator;

import com.xerox.VTM.glyphs.Glyph;

/**
 * This class represents a element in a graph
 * 
 * @author <A href="http://www.ladkau.de" target=newframe>M. Ladkau </A>
 */

public abstract class AbstractGraphElement {

	/**
	 * Owner of this element
	 */
	protected AbstractGraph owner;

	/**
	 * A unique ID of this Element
	 */
	protected long id;

	/**
	 * The glyph representation of this component
	 */
	protected Glyph glyph;

	/**
	 * The event pump of the diagram this element belongs to
	 */
	protected ActionListener actionListener;

	/**
	 * True if it has a popup menu
	 */
	protected boolean hasPopupMenu = false;

	/**
	 * The popupmenu of this element
	 */
	protected JPopupMenu popupMenu;

	/**
	 * Indicates if this element is hidden
	 */
	private boolean hidden;

	/**
	 * The name of this node
	 */
	private String name;

	/**
	 * An object which is tied to this node
	 */
	private Object fellow;

	/**
	 * The Constructor
	 * 
	 * @param owner
	 *            The owner of this element 
	 */
	public AbstractGraphElement(AbstractGraph owner) {
		this.owner = owner;
		this.id = RandomNumberGenerator.genRandomNumber("ObjectID", 0,
				Integer.MAX_VALUE, 10);
		hidden = false;
		popupMenu = null;
		updateGlyph();
		glyph.setStrokeWidth(2);
	}

	/**
	 * Builds the element according to it's current status
	 */
	public abstract void updateGlyph();

	/**
	 * Adds the element to the Virtual Space
	 */
	public void addGlyphs() {
		// Add the Glyph to the virtual space
		owner.getActiveDisplay().getVirtualSpaceManager()
				.addGlyph(glyph, owner.getActiveDisplay().main.getVirtualSpaceName());
	}

	/**
	 * Init the element
	 */
	public void init() {
		setFellow(null);
		addGlyphs();
	}

	/**
	 * Changes the position of the element
	 * 
	 * @param newX
	 *            The new X coordinate for the element
	 * @param newY
	 *            The new Y coordinate for the element
	 */
	public void fixTranslation(long newX, long newY) {
		glyph.moveTo(newX, newY);
	}

	/**
	 * Gets the x position of this UML Element (upper left corner)
	 * 
	 * @return Returns the x position of this object
	 */
	public long getX() {
		return glyph.vx;
	}

	/**
	 * Gets the y position of this UML Element (upper left corner)
	 * 
	 * @return Returns the y position of this object
	 */
	public long getY() {
		return glyph.vy;
	}

	/**
	 * Gets the glyph representation of this UML Element
	 * 
	 * @return Returns the glyph representation of this object
	 */
	public Glyph getGlyphRepresentation() {
		return this.glyph;
	}

	/**
	 * This Method shows the element's popup menu (if existing)
	 * 
	 * @param c
	 *            The invoker
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 */
	public void showPopupMenu(Component c, int x, int y) {
		if (hasPopupMenu)
			popupMenu.show(c, x, y);
	}

	/**
	 * Returns the popup menu of the element (if existing)
	 * 
	 * @return The popup menu
	 */
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	/**
	 * Get the ActionListener for this diagram
	 * 
	 * @return Returns the actionListener.
	 */
	public ActionListener getActionListener() {
		return actionListener;
	}

	/**
	 * Set the ActionListener for this diagram
	 * 
	 * @param actionListener
	 *            The actionListener to set.
	 */
	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	/**
	 * Gets the owner of this element
	 * 
	 * @return Returns the owner.
	 */
	public AbstractGraph getOwner() {
		return owner;
	}

	/**
	 * Returns an unique ID of this element
	 * 
	 * @return The ID of this element
	 */
	public long getID() {
		return id;
	}

	/**
	 * Draws the glyph and all sub components on the top of the VirtualSpace
	 */
	public void onTop() {
		owner.getActiveDisplay().getVirtualSpace().onTop(glyph);
	}

	/**
	 * Hides this element
	 */
	public void hide() {
		glyph.setVisible(false);
		hidden = true;
	}

	/**
	 * Shows this element
	 */
	public void show() {
		glyph.setVisible(true);
		hidden = false;
	}

	/**
	 * Indicates if this element is hidden
	 * 
	 * @return True if this element is hidden otherwise false.
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Get the name of this node
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this node
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return an object which was tied to this node
	 * 
	 * @return The tied object
	 */
	public Object getFellow() {
		return fellow;
	}

	/**
	 * Tie an object to this node
	 * 
	 * @param fellow
	 *            The object which should be tied to this node
	 */
	public void setFellow(Object fellow) {
		this.fellow = fellow;
	}
}

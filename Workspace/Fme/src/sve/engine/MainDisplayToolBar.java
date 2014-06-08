/**
 * Project: fuml
 */

package sve.engine;

import java.awt.*;

import javax.swing.*;

public class MainDisplayToolBar extends JToolBar {

	/**
	 * Field for identifying a class
	 */
	private static final long serialVersionUID = 2919158343769786933L;

	/**
	 * The button "Align view" of this ToolBar
	 */
	private JButton buttonAlignView;

	/**
	 * The button "Select view" of this ToolBar
	 */
	private JButton buttonSelectView;

	/**
	 * The button "Cursor" of this ToolBar
	 */
	private JButton buttonCurser;

	/**
	 * The JSpinner of this ToolBar
	 */
	private SpinnerModel spinnerModel;

	/**
	 * The Constructor
	 */
	public MainDisplayToolBar() {
		JSpinner spinner;
		JLabel s;

		this.setFloatable(false);
		this.setMinimumSize(new Dimension(100, 200));

		s = new JLabel("    ");
		add(s);

		buttonSelectView = new JButton();
		buttonSelectView.setToolTipText("Explorer Tool");
		buttonSelectView.setIcon(new ImageIcon("icons/select_view.gif"));
		buttonSelectView.setActionCommand("MainDisplayMenu:SelectView");
		add(buttonSelectView);

		s = new JLabel("    ");
		add(s);

		buttonAlignView = new JButton();
		buttonAlignView.setToolTipText("Allign view");
		buttonAlignView.setIcon(new ImageIcon("icons/align_view.gif"));
		buttonAlignView.setActionCommand("MainDisplayMenu:AlignView");
		add(buttonAlignView);

		s = new JLabel("    ");
		add(s);

		buttonCurser = new JButton();
		buttonCurser.setToolTipText("Modification Tool");
		buttonCurser.setIcon(new ImageIcon("icons/curser.gif"));
		buttonCurser.setActionCommand("MainDisplayMenu:Curser");
		add(buttonCurser);

		s = new JLabel("    ");
		add(s);

		s = new JLabel(" Altitude:");
		add(s);

		spinnerModel = new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 20);
		spinner = new JSpinner(spinnerModel);
		spinner.setToolTipText("Zoom Level");
		spinner.setMaximumSize(new Dimension(70, 30));
		add(spinner);

		s = new JLabel("    ");
		add(s);
	}

	/**
	 * Set the lister for this components (ActionListener and ChangeListener)
	 * 
	 * @param ep
	 *            The listener which listens for events
	 */
	public void setListener(EventPump ep) {
		buttonCurser.addActionListener(ep);
		buttonAlignView.addActionListener(ep);
		buttonSelectView.addActionListener(ep);
		spinnerModel.addChangeListener(ep);
	}

	/**
	 * Change the value of the spinner
	 * 
	 * @param value
	 *            The new value for the spinner
	 */
	public void setSpinnerValue(float value) {
		float v;
		v = value;
		spinnerModel.setValue(new Float(v));
	}

	/**
	 * Change the value of the spinner
	 * 
	 * @param value
	 *            The new value for the spinner
	 */
	public float getSpinnerValue() {
		return (Float.parseFloat("" + spinnerModel.getValue()));
	}
}

/* CVS: CobaltVault SERVER:\\halo TREE:\abode_mainline
 *       _    ____   ___  ____  _____ 
 *      / \  | __ ) / _ \|  _ \| ____|      Advanced
 *     / _ \ |  _ \| | | | | | |  _|        Behavior
 *    / ___ \| |_) | |_| | |_| | |___       Oriented
 *   /_/   \_\____/ \___/|____/|_____|      Design
 *         www.cobaltsoftware.net           Environment
 *
 * PRODUCED FOR:      University of Bath / Boeing
 * PAYMENT:           On Delivery   
 * LICENSING MODEL:   Unrestricted distribution (Post Delivery)
 * COPYRIGHT:         Client retains copyright.
 *
 * This program and all the software components herein are
 * released as-is, without warranties regarding function,
 * correctness or any other aspect of the components.
 * Steven Gray, Cobalt Software, it's subcontractors and
 * successors may not be held liable for any damage caused 
 * to computers, business or other property through use of 
 * or misuse of this software.
 *
 * Upon redistribution of the program, all notices of
 * copyrights, both of the software provider and the 
 * client must be retained.
 */
package abode.visual;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import abode.Configuration;

/**
 * A simple GUI class for the selection of colours. Nothing fancy.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class JColorSelectButton extends JButton {
	// Added to get rid of warnings and properly implement Serializable
	private static final long serialVersionUID = 1;

	// Our key in the configuration hierarchy
	private String key = "";

	// Frame invoking us
	private JOptionsScreen parentFrame = null;

	/**
	 * Initialize the colour scelection button
	 * 
	 * @param keyName
	 *            Name of the configuration path
	 * @param optionsFrame
	 *            Options display we're bound to
	 */
	public JColorSelectButton(String keyName, JOptionsScreen optionsFrame) {
		parentFrame = optionsFrame;
		key = "colours/" + keyName;
		setBackground(Configuration.getRGB(key));

		// Start listening for when anything happens to this (i.e. clicked)
		this.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				colorButtonActionPerformed(evt);
			}
		});
	}

	/**
	 * When an action has been peformed, do our voodoo.
	 */
	private void colorButtonActionPerformed(ActionEvent evt) {
		JColorSelectForm colForm = new JColorSelectForm(parentFrame, this);
		Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
		colForm.setLocation(SCREEN_SIZE.width / 2 - colForm.getWidth() / 2, SCREEN_SIZE.height / 2 - colForm.getHeight() / 2);
		parentFrame.setEnabled(false);
		colForm.show();
	}

	/**
	 * Get the configuration path we're representing
	 */
	public String getKey() {
		return key;
	}
}

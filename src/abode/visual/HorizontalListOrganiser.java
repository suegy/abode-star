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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.IEditableElement;
import abode.JAbode;
import abode.control.AbodeActionHandler;

/**
 * The horizontal list organiser is used for arranging elements within action
 * patterns, triggers and goals.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class HorizontalListOrganiser extends ListOrganiser {
	/**
	 * Populate the options panel on the right of the screen with the relevent
	 * list re-arrangement buttons for a list that is being re-arranged
	 * horizontally.
	 * 
	 * @param mainGui
	 *            The outer IDE reference
	 * @param internal
	 *            Our internal frame
	 * @param diagram
	 *            The diagram we are being rendered on
	 * @param subject
	 *            The tree-node that was clicked to produce this action
	 */
	@Override
	public void populateOptionsPanel(final JAbode mainGui, final JEditorWindow internal, final JDiagram diagram, final JTreeNode subject) {
		// Store some references to make things easier to read
		final JPanel panel = mainGui.getCommandsPanel();
		final ArrayList myGroup = subject.getGroup();
		final IEditableElement element = subject.getValue();

		JButton bttnMoveLeft = new JButton("Move left", new ImageIcon(getClass().getResource("/image/icon/arrowLeft.gif")));
		bttnMoveLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbodeActionHandler.getActionHandler().moveUpInGroupAction(diagram, internal, subject);
			}
		});
		bttnMoveLeft.setHorizontalAlignment(SwingConstants.LEFT);
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnMoveLeft.setToolTipText("Moves an element left in hierarchy" +
				" (Alt + Left)");
		bttnMoveLeft.setMnemonic(KeyEvent.VK_LEFT);
		
		panel.add(bttnMoveLeft);
			
		// Can this element be moved left?
		
		if (!(myGroup.indexOf(element) > 0)) {
				bttnMoveLeft.setEnabled(false);
		}

		addDeleteButton(mainGui.getEditPanel(), internal, subject, diagram);

		JButton bttnMoveRight = new JButton("Move right", new ImageIcon(getClass().getResource("/image/icon/arrowRight.gif")));
		bttnMoveRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbodeActionHandler.getActionHandler().moveDownInGroupAction(diagram, internal, subject);
			}
		});
		bttnMoveRight.setHorizontalAlignment(SwingConstants.LEFT);
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnMoveRight.setToolTipText("Moves an element right in hierarchy" +
				" (Alt + Right)");
		bttnMoveRight.setMnemonic(KeyEvent.VK_RIGHT);
		
		panel.add(bttnMoveRight);
		
		// Can this element be moved right?
		// If it can't, then disable the button
		if (!(myGroup.indexOf(subject.getValue()) < (myGroup.size() - 1))) {
			bttnMoveRight.setEnabled(false);
		}
	}
}

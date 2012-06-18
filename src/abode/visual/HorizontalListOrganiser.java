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
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.IEditableElement;

/**
 * The horizontal list organiser is used for arranging elements within action
 * patterns, triggers and goals.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class HorizontalListOrganiser implements IListOrganiser {
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
	public void populateOptionsPanel(final JAbode mainGui, final JEditorWindow internal, final JDiagram diagram, final JTreeNode subject) {
		// Store some references to make things easier to read
		final JPanel panel = mainGui.getCommandsPanel();
		final ArrayList myGroup = subject.getGroup();
		final IEditableElement element = subject.getValue();

		// Can this element be moved left?
		if (myGroup.indexOf(element) > 0) {
			JButton bttnMoveUp = new JButton("Move To Left", new ImageIcon(getClass().getResource("/image/icon/arrowLeft.gif")));
			bttnMoveUp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					int index = myGroup.indexOf(element);
					myGroup.add(index - 1, myGroup.remove(index));
					internal.updateDiagrams(diagram, element);
				}
			});
			bttnMoveUp.setHorizontalAlignment(JButton.LEFT);
			panel.add(bttnMoveUp);
		}

		// Crush, kill destroy
		JButton bttnDelete = new JButton("Delete Element", new ImageIcon(getClass().getResource("/image/icon/delete.gif")));
		bttnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (JOptionPane.showConfirmDialog(diagram, "Are you sure you want to delete this item?") == JOptionPane.YES_OPTION) {
					myGroup.remove(subject.getValue());
					internal.updateDiagrams(diagram, subject.getParentNode().getValue());
				}
			}
		});
		bttnDelete.setHorizontalAlignment(JButton.LEFT);
		panel.add(bttnDelete);

		// Can this element be moved right?
		if (myGroup.indexOf(subject.getValue()) < (myGroup.size() - 1)) {
			JButton bttnMoveDown = new JButton("Move To Right.", new ImageIcon(getClass().getResource("/image/icon/arrowRight.gif")));
			bttnMoveDown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					int index = myGroup.indexOf(subject.getValue());
					myGroup.add(index, myGroup.remove(index + 1));

					internal.updateDiagrams(diagram, subject.getValue());
				}
			});
			bttnMoveDown.setHorizontalAlignment(JButton.LEFT);
			panel.add(bttnMoveDown);
		}
	}
}

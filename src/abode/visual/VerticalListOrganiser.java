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
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditEvent;

import abode.AbodeUndoManager;
import abode.editing.PositionEdit;

import model.IEditableElement;

/**
 * When arraylists of arraylists of anonymous elements are laid out as a single
 * vertical list, we use this class as the list organiser in order to facilitate
 * the rearranging.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class VerticalListOrganiser extends ListOrganiser {
	/**
	 * This option populates the options/commands panel of the main GUI display
	 * with a list of relevent actions based on the currently selected tree
	 * node;
	 */
	public void populateOptionsPanel(final JAbode mainGui, final JEditorWindow internal, final JDiagram diagram, final JTreeNode subject) {
		// If we've got no node, the node isn't in a group, the node has no
		// parent or the
		// parent isn't in a group, we can't continue. We also need a
		// grandparent
		if ((subject == null) || (subject.getGroup() == null) || (subject.getValue() == null) || (subject.getParentNode() == null) || (subject.getParentNode().getGroup() == null))
			return;

		// Store some references to make things easier to read
		final JPanel panel = mainGui.getCommandsPanel();
		final ArrayList myGroup = subject.getGroup();
		final ArrayList groupGroup = subject.getParentNode().getGroup();
		final IEditableElement element = subject.getValue();

		String type = (myGroup.size() > 1) ? "group" : "element";

		// Move element/group up the list of lists
		JButton bttnMoveUp = new JButton("Move " + type + " up", new ImageIcon(getClass().getResource("/image/icon/group-up.gif")));
		bttnMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				int index = groupGroup.indexOf(myGroup);
				groupGroup.add(index - 1, groupGroup.remove(index));
				AbodeUndoManager.getUndoListener().undoableEditHappened(new UndoableEditEvent(this, new PositionEdit(myGroup, index, index-1, groupGroup)));
				internal.updateDiagrams(diagram, subject.getValue());
			}
		});
		bttnMoveUp.setHorizontalAlignment(JButton.LEFT);
		panel.add(bttnMoveUp);

		// If not possible to move up, disable the button
		if (!(groupGroup.indexOf(myGroup) > 0)) {
			bttnMoveUp.setEnabled(false);
		}

		JButton bttnMergeUp = new JButton("Merge with group above", new ImageIcon(getClass().getResource("/image/icon/merge-group-up.gif")));
		bttnMergeUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// Get the group above us
				ArrayList groupAbove = (ArrayList) groupGroup.get(groupGroup.indexOf(myGroup) - 1);

				// Remove us from the list of lists
				groupGroup.remove(myGroup);

				// Add each element to the end of the list above
				Iterator it = myGroup.iterator();
				while (it.hasNext())
					groupAbove.add(it.next());

				internal.updateDiagrams(diagram, subject.getValue());

			}
		});
		bttnMergeUp.setHorizontalAlignment(JButton.LEFT);
		panel.add(bttnMergeUp);
		
		// If there's no group above us disable the button
		if (!(groupGroup.indexOf(myGroup) > 0)) {
			bttnMergeUp.setEnabled(false);
		}

		// Are we in a multi-item group? If so present the option to ungroup
		if (myGroup.size() > 1) {
			// Move up in group
			JButton bttnMoveupGroup = new JButton("Move up in group", new ImageIcon(getClass().getResource("/image/icon/upingroup.gif")));
			bttnMoveupGroup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					ArrayList newGroup = new ArrayList();
					Object before = myGroup.get(myGroup.indexOf(subject.getValue()) - 1);
					Iterator it = myGroup.iterator();
					while (it.hasNext()) {
						Object n = it.next();
						if (n == before) {
							newGroup.add(subject.getValue());
							newGroup.add(before);
						} else if (n == subject.getValue()) {
							// Dont add ourself
						} else {
							newGroup.add(n);
						}
					}

					groupGroup.set(groupGroup.indexOf(myGroup), newGroup);
					internal.updateDiagrams(diagram, subject.getValue());
				}
			});
			bttnMoveupGroup.setHorizontalAlignment(JButton.LEFT);
			panel.add(bttnMoveupGroup);
			
			// If not possible to move up, disable the button
			if (!(myGroup.indexOf(element) > 0)) {
				bttnMoveupGroup.setEnabled(false);
			}

			addDeleteButton(mainGui.getEditPanel(), internal, subject, diagram);

			addDeleteGroupButton(mainGui.getEditPanel(), internal, subject, diagram);

			// Dissolve the group
			JButton bttnUngroup = new JButton("Ungroup Elements", new ImageIcon(getClass().getResource("/image/icon/ungroup.gif")));
			bttnUngroup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					Iterator it = myGroup.iterator();
					int done = 0;
					while (it.hasNext()) {
						ArrayList newGroup = new ArrayList();
						newGroup.add(it.next());
						groupGroup.add(groupGroup.indexOf(myGroup) + (done++), newGroup);
					}
					groupGroup.remove(myGroup);
					internal.updateDiagrams(diagram, subject.getValue());

				}
			});
			bttnUngroup.setHorizontalAlignment(JButton.LEFT);
			panel.add(bttnUngroup);

			// Move down inside group
			JButton bttnMoveDownGroup = new JButton("Move down in group", new ImageIcon(getClass().getResource("/image/icon/downingroup.gif")));
			bttnMoveDownGroup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					
					int index = myGroup.indexOf(subject.getValue());
					Object elem = myGroup.remove(index);
					myGroup.add(index+1, elem);
					
					AbodeUndoManager.getUndoListener().undoableEditHappened(new UndoableEditEvent(this, new PositionEdit(subject.getValue(), index, index+1, myGroup)));
					
					
					
//					ArrayList newGroup = new ArrayList();
//					Object after = myGroup.get(myGroup.indexOf(subject.getValue()) + 1);
//					
//					for (Object object : myGroup) {
//						if (object == after) {
//							newGroup.add(after);
//							newGroup.add(subject.getValue());
//						} else if (object == subject.getValue()) {
//							// Dont add ourself twice
//						} else {
//							newGroup.add(object);
//						}
//					}
//					
//					groupGroup.set(groupGroup.indexOf(myGroup), newGroup);
					internal.updateDiagrams(diagram, subject.getValue());
				}
			});
			bttnMoveDownGroup.setHorizontalAlignment(JButton.LEFT);
			panel.add(bttnMoveDownGroup);
			
			// if not possible to move down within the group, disable the button
			if (!(myGroup.indexOf(element) < (myGroup.size() - 1))) {
				bttnMoveDownGroup.setEnabled(false);
			}
		} else {
			addDeleteButton(mainGui.getEditPanel(), internal, subject, diagram);
		}

		// Button for merging with group below
		JButton bttnMergeDown = new JButton("Merge with group below", new ImageIcon(getClass().getResource("/image/icon/merge-group-down.gif")));
		bttnMergeDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// Get the group above us
				ArrayList groupBelow = (ArrayList) groupGroup.get(groupGroup.indexOf(myGroup) + 1);

				// Remove us from the list of lists
				groupGroup.remove(myGroup);

				// Add each element to the end of the list above
				Iterator it = myGroup.iterator();
				while (it.hasNext())
					groupBelow.add(it.next());

				internal.updateDiagrams(diagram, subject.getValue());
			}
		});
		bttnMergeDown.setHorizontalAlignment(JButton.LEFT);
		panel.add(bttnMergeDown);
			
		// If there's no group beneath us disable button
		if (!(groupGroup.indexOf(myGroup) < (groupGroup.size() - 1))) {
			bttnMergeDown.setEnabled(false);
		}

		// Move element/group down the list of lists
		JButton bttnMoveDown = new JButton("Move " + type + " down", new ImageIcon(getClass().getResource("/image/icon/group-down.gif")));
		bttnMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				int index = groupGroup.indexOf(myGroup);
				
				groupGroup.add(index, groupGroup.remove(index + 1));

				AbodeUndoManager.getUndoListener().undoableEditHappened(new UndoableEditEvent(this, new PositionEdit(myGroup, index, index+1, groupGroup)));
				
				internal.updateDiagrams(diagram, subject.getValue());
				
			}
		});
		bttnMoveDown.setHorizontalAlignment(JButton.LEFT);
		panel.add(bttnMoveDown);
		
		//Disable if the group / element cannot be moved down
		if (!(groupGroup.indexOf(myGroup) < (groupGroup.size() - 1))) {
			bttnMoveDown.setEnabled(false);
		}
	}
}

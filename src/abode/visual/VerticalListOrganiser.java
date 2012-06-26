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
import javax.swing.event.UndoableEditEvent;

import model.IEditableElement;
import abode.JAbode;
import abode.control.AbodeActionHandler;
import abode.editing.MergeGroupsEdit;
import abode.editing.UnGroupEdit;

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
	@Override
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
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbodeActionHandler.getActionHandler().moveUpAction(diagram, internal, subject);
			}
		});
		bttnMoveUp.setHorizontalAlignment(SwingConstants.LEFT);
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnMoveUp.setToolTipText("Moves an element up the hierarchy" +
				" (Alt + Up)");
		bttnMoveUp.setMnemonic(KeyEvent.VK_UP);
		
		panel.add(bttnMoveUp);

		// If not possible to move up, disable the button
		if (!(groupGroup.indexOf(myGroup) > 0)) {
			bttnMoveUp.setEnabled(false);
		}

		

		// Are we in a multi-item group? If so present the option to ungroup
		if (myGroup.size() > 1) {
			// Move up in group
			JButton bttnMoveupGroup = new JButton("Move up in group", new ImageIcon(getClass().getResource("/image/icon/upingroup.gif")));
			bttnMoveupGroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					AbodeActionHandler.getActionHandler().moveUpInGroupAction(diagram, internal, subject);
				}
			});
			bttnMoveupGroup.setHorizontalAlignment(SwingConstants.LEFT);
			
			// Set tooltip and shortcut key (Mnemonic)
			bttnMoveupGroup.setToolTipText("Moves an element up within a group" +
					" (Alt + })");
			bttnMoveupGroup.setMnemonic(KeyEvent.VK_BRACERIGHT);
			
			panel.add(bttnMoveupGroup);
			
			// If not possible to move up, disable the button
			if (!(myGroup.indexOf(element) > 0)) {
				bttnMoveupGroup.setEnabled(false);
			}



			// Move down inside group
			JButton bttnMoveDownGroup = new JButton("Move down in group", new ImageIcon(getClass().getResource("/image/icon/downingroup.gif")));
			bttnMoveDownGroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					AbodeActionHandler.getActionHandler().moveDownInGroupAction(diagram, internal, subject);
		}
			});
			bttnMoveDownGroup.setHorizontalAlignment(SwingConstants.LEFT);
			// Set tooltip and shortcut key (Mnemonic)
			bttnMoveDownGroup.setToolTipText("Moves an element down within a group" +
					" (Alt + {)");
			bttnMoveDownGroup.setMnemonic(KeyEvent.VK_BRACELEFT);
			
			panel.add(bttnMoveDownGroup);
			
			// if not possible to move down within the group, disable the button
			if (!(myGroup.indexOf(element) < (myGroup.size() - 1))) {
				bttnMoveDownGroup.setEnabled(false);
			}
		}



		// Move element/group down the list of lists
		JButton bttnMoveDown = new JButton("Move " + type + " down", new ImageIcon(getClass().getResource("/image/icon/group-down.gif")));
		bttnMoveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbodeActionHandler.getActionHandler().moveDownAction(diagram, internal, subject);
			}
		});
		bttnMoveDown.setHorizontalAlignment(SwingConstants.LEFT);
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnMoveDown.setToolTipText("Moves an element down the hierarchy" +
				" (Alt + Down)");
		bttnMoveDown.setMnemonic(KeyEvent.VK_DOWN);
		
		panel.add(bttnMoveDown);
		
		//Disable if the group / element cannot be moved down
		if (!(groupGroup.indexOf(myGroup) < (groupGroup.size() - 1))) {
			bttnMoveDown.setEnabled(false);
		}
		
		
		
		JButton bttnMergeUp = new JButton("Merge with group above", new ImageIcon(getClass().getResource("/image/icon/merge-group-up.gif")));
		bttnMergeUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				// Get the group above us
				ArrayList groupAbove = (ArrayList) groupGroup.get(groupGroup.indexOf(myGroup) - 1);
				_undoListener.undoableEditHappened(new UndoableEditEvent(groupGroup, new MergeGroupsEdit(diagram, internal, myGroup, groupGroup.indexOf(myGroup) , groupAbove, groupGroup, subject)));
				// Remove us from the list of lists
				groupGroup.remove(myGroup);

				// Add each element to the end of the list above
				for (Object object : myGroup)
					groupAbove.add(object);


				internal.updateDiagrams(diagram, subject.getValue());

			}
		});
		bttnMergeUp.setHorizontalAlignment(SwingConstants.LEFT);
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnMergeUp.setToolTipText("Merges an element with the element above it in the hierarchy.");
		
		panel.add(bttnMergeUp);
		
		// If there's no group above us disable the button
		if (!(groupGroup.indexOf(myGroup) > 0)) {
			bttnMergeUp.setEnabled(false);
		}
		
		// Button for merging with group below
		JButton bttnMergeDown = new JButton("Merge with group below", new ImageIcon(getClass().getResource("/image/icon/merge-group-down.gif")));
		bttnMergeDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				// Get the group above us
				ArrayList groupBelow = (ArrayList) groupGroup.get(groupGroup.indexOf(myGroup) + 1);
				_undoListener.undoableEditHappened(new UndoableEditEvent(groupGroup, new MergeGroupsEdit(diagram, internal, myGroup, groupGroup.indexOf(myGroup) , groupBelow, groupGroup, subject)));
				// Remove us from the list of lists
				groupGroup.remove(myGroup);

				// Add each element to the end of the list above
				for (Object object : myGroup)
					groupBelow.add(object);

				internal.updateDiagrams(diagram, subject.getValue());
			}
		});
		bttnMergeDown.setHorizontalAlignment(SwingConstants.LEFT);
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnMergeDown.setToolTipText("Merges an element with the element above it in the hierarchy.");
		
		panel.add(bttnMergeDown);
			
		// If there's no group beneath us disable button
		if (!(groupGroup.indexOf(myGroup) < (groupGroup.size() - 1))) {
			bttnMergeDown.setEnabled(false);
		}


		// Are we in a multi-item group? If so present the option to ungroup
		if (myGroup.size() > 1) {
			// Dissolve the group
			JButton bttnUngroup = new JButton("Ungroup Elements", new ImageIcon(getClass().getResource("/image/icon/ungroup.gif")));
			bttnUngroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					int index= groupGroup.indexOf(myGroup);
					ArrayList<IEditableElement> [] unGrouped=new ArrayList[myGroup.size()];
					for (Object item : myGroup) {
						unGrouped[myGroup.indexOf(item)]=new ArrayList<IEditableElement>();
						unGrouped[myGroup.indexOf(item)].add((IEditableElement)item);
					}
					groupGroup.remove(myGroup);
					for (Object object : unGrouped) {
						groupGroup.add(index,object);
					}
					
					_undoListener.undoableEditHappened(new UndoableEditEvent(groupGroup, new UnGroupEdit(diagram, internal, myGroup, index, unGrouped, groupGroup, subject)));
					
					
					internal.updateDiagrams(diagram, subject.getValue());
	
				}
			});
			bttnUngroup.setHorizontalAlignment(SwingConstants.LEFT);
			bttnUngroup.setToolTipText("Dissolves a group. All of the elements will return to being singular elements.");
			
			panel.add(bttnUngroup);
		}
		
		
		addDeleteButton(mainGui.getEditPanel(), internal, subject, diagram);

		addDeleteGroupButton(mainGui.getEditPanel(), internal, subject, diagram);
	}
}

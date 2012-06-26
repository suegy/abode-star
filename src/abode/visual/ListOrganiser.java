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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import abode.JAbode;
import abode.control.AbodeActionHandler;

/**
 * The super list organiser object for providing simple "delete me" functionality
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class ListOrganiser implements IListOrganiser {
	/**
	 * Populate the options panel on the right of the screen with the buttons
	 * for deleting constructs
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
		addDeleteButton(mainGui.getEditPanel(), internal, subject, diagram);
	}
	
	public void addDeleteButton(final JPanel panel, final JEditorWindow internal, final JTreeNode subject,final JDiagram diagram)
	{
		JButton bttnDelete = new JButton("Delete element", new ImageIcon(getClass().getResource("/image/icon/delete.gif")));
		bttnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbodeActionHandler.getActionHandler().deleteElementAction(diagram, internal, subject);
			}
		});
		bttnDelete.setHorizontalAlignment(JButton.LEFT);
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnDelete.setToolTipText("Deletes an element from the hierarchy" +
				" (Alt + Delete)");
		bttnDelete.setMnemonic(KeyEvent.VK_DELETE);
		
		panel.add(bttnDelete);
	}
	
	public void addDeleteGroupButton(final JPanel panel, final JEditorWindow internal, final JTreeNode subject,final JDiagram diagram)
	{
		JButton bttnDelete = new JButton("Delete group", new ImageIcon(getClass().getResource("/image/icon/delete.gif")));
		bttnDelete.setHorizontalAlignment(JButton.LEFT);
		bttnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbodeActionHandler.getActionHandler().deleteGroupAction(diagram, internal, subject);
			}
		});
		
		// Set tooltip and shortcut key (Mnemonic)
		bttnDelete.setToolTipText("Deletes an entire group from the hierarchy");
		
		panel.add(bttnDelete);
	}
}
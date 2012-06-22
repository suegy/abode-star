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
package model;


import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import abode.AbodeUndoManager;
import abode.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;
import model.posh.LearnableActionPattern;

/**
 * the IEditableElement interface defines methods for use when visually editing
 * an object, so each construct in our .lap files is represented as some object
 * that implements this interface, so when it is selected in the IDE the right
 * sequence of actions to present this on the GUI is taken.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public interface IEditableElement {
	
	public static UndoableEditListener _undoListener = AbodeUndoManager.getUndoListener();
	public static AbodeUndoManager _undo = AbodeUndoManager.getUndoManager();
	

	
	/**
	 * Sets enabled
	 */
	public void setEnabled(boolean newValue);
	
	/**
	 * Sets the element documentation
	 */
	public void setDocumentation(String newDocumentation);
	public String getElementDocumentation();
	/**
	 * Sets the element do
	 */
	
	/**
	 * When we click this Action Element in the GUI populate the properties
	 * panel with the various attributes and setup listeners to catch
	 * modifications that are made.
	 * 
	 * @param mainGui
	 *            The reference to the outer GUI
	 * @param subGui
	 *            The internal frame we're referring to
	 * @param diagram
	 *            The diagram we're being select on.
	 */
	public void onSelect(JAbode mainGui, JEditorWindow subGui, JDiagram diagram);

	/**
	 * Build the tree structure of the file
	 * 
	 * @root Root node this tree attatches to
	 * @lap File we're mapping to
	 * @detailed Show detailing nodes (i.e. trigger lists)
	 * @expanded Recursive expansion of sub-nodes
	 * 
	 * @return Tree node representing this node and the relevent sub-tree for
	 *         the specified diagram rendering settings
	 */
	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap, boolean detailed, boolean expanded);

	/**
	 * Produce and show a context menu for this object
	 * 
	 * @param showOn
	 *            The tree node invoking us
	 * @param lap
	 *            The file we're a part of
	 * @param window
	 *            The window we're being dispalyed in
	 * @param diagram
	 *            The diagram in the window we'return being shown on
	 */
	public void showContextMenu(JTreeNode showOn, LearnableActionPattern lap, JEditorWindow window, JDiagram diagram);
}

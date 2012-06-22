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

import abode.JAbode;

/**
 * The IListOrganiser interface is intended to encapsulate the rearranging
 * functionality that comes with constructs in the rendered tree that make use
 * of the grouping abilitiy in the code.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public interface IListOrganiser {
	/**
	 * Populate the options panel on the right of the screen with the relevent
	 * list re-arrangement buttons for this type of object.
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
	void populateOptionsPanel(final JAbode mainGui, final JEditorWindow internal, final JDiagram diagram, final JTreeNode subject);
}

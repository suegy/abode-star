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

import abode.visual.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;
import model.posh.LearnableActionPattern;

/**
 * A TimeUnit is a measurement of time with two attributes, the interval and the
 * unit of measurement. For example, 10 seconds, 5 hz, and so forth.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class TimeUnit implements IEditableElement {
	// Frequency, delay or Interval this time unit represents
	private double dInterval = 0;

	// Name of this unit
	private String strUnitName = null;
	
	
	//Not really relevant but easier
	public void setDocumentation(String newDocumentation) {}
	public String getElementDocumentation() { return ""; }

	/**
	 * Initialize this unit time
	 */
	public TimeUnit(String unit, double value) {
		strUnitName = unit;
		dInterval = value;
	}

	/**
	 * We've been selected on the diagram for editing, so populate the
	 * properties grid with editors allowing the user to change our interval and
	 * unit.
	 */
	public void onSelect(JAbode mainGui, JEditorWindow subGui, JDiagram diagram) {

	}

	/**
	 * Get the name of the unit involved
	 */
	public String getUnitName() {
		return strUnitName;
	}

	/**
	 * Get our interval value for this unit time
	 */
	public double getUnitValue() {
		return dInterval;
	}
	
	/**
	 * Set enabled. Doesn't really do a lot but needed for interface implementation
	 */
	public void setEnabled(boolean newValue) {}

	/**
	 * Set the name of our unit
	 */
	public void setUnitName(String unitName) {
		strUnitName = unitName;
	}

	/**
	 * Set the value of this unit of time
	 */
	public void setUnitValue(double value) {
		dInterval = value;
	}

	public void setDebuggerIdentifier(int debuggerIdentifier) {}
	
	/**
	 * Produce and show a context menu for this object
	 */
	public void showContextMenu(JTreeNode showOn, LearnableActionPattern lap, JEditorWindow window, JDiagram diagram) {

	}

	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap, boolean detailed, boolean expanded) {
		return null;
	}
}

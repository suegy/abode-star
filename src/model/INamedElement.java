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

/**
 * Certain elements in our LearnableActionPattern object have names that are
 * referenced by other objects for the purposes of inter-dependance/invocation.
 * What we use this interface for is to give those elements a common subtype and
 * a standardised way to retrieve the value for the name.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public interface INamedElement {
	/**
	 * Set the name of this element
	 * 
	 * @param name
	 *            Updated name of the element
	 */
	public void setName(String name);

	/**
	 * Get the name of this element
	 * 
	 * @return Name of the element
	 */
	public String getName();
}

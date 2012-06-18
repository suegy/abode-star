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
package abode.control;

import java.util.*;

/**
 * A Primitive Reader is an object that can produce a list of actions or senses
 * from an input file. This is used for validating diagrams elsewhere.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public interface IPrimitiveReader {
	/**
	 * Can this reader process the given file?
	 * 
	 * @param strFile
	 *            Path to file to read
	 * @return True if file contains primitives, false if not
	 */
	public boolean canRead(String strFile);

	/**
	 * Get the list of action primitives file
	 * 
	 * @param strFile
	 *            Path to file to read.
	 * @return Arraylist of actions found in the file
	 */
	public ArrayList getActions(String strFile);

	/**
	 * Get the description of file
	 * 
	 * @return Description of the types of file this parser can read.
	 */
	public String getDescription();

	/**
	 * Get the list of sense primitives from this file
	 * 
	 * @param strFile
	 *            Path to file to read.
	 * @return Arraylist of sense primitives found in the file
	 */
	public ArrayList getSenses(String strFile);
}
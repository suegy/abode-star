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


import java.io.FileNotFoundException;
import java.io.IOException;

import model.posh.LearnableActionPattern;

/**
 * The ILapReader interface defines methods that all classes that read in BOD
 * behaviour specifications must implement. This allows abstraction from the
 * underlying file formats, providing the classes are capable of producing the
 * LearnableActionPattern object at the end.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public interface ILAPReader {
	/**
	 * Is the specified file readable to this class?
	 * 
	 * @param fileName
	 *            Path to the file to read
	 * @return True if readable, false if not
	 */
	public boolean canRead(String fileName);

	/**
	 * Load the specified file and get an action pattern
	 * 
	 * @param fileName
	 *            File to load
	 * @return Loaded file in the form of an object model
	 */
	public LearnableActionPattern load(String fileName) throws FileNotFoundException, IOException, Exception;
}

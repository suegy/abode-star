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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A PrimitiveManager is an object that provides an abstraction to interface
 * with a list of objects that produce primitive lists
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class PrimitiveManager {
	// Where we store our list of primitive generators
	private static ArrayList alPrimReaders = null;

	static {
		alPrimReaders = new ArrayList();
		alPrimReaders.add(new PythonPrimitivesParser());
		alPrimReaders.add(new LispPrimitiveParser());
	}

	/**
	 * Register a new primitive reading object with the list.
	 * 
	 * @param ipr
	 *            Object that can handle parsing of primitives
	 */
	public static void register(IPrimitiveReader ipr) {
		alPrimReaders.add(ipr);
	}

	/**
	 * Get the relevent primitive reader for a specific file
	 * 
	 * @param strFile
	 *            Path to file to read
	 * @return Primitive reading object for this file.
	 */
	public static IPrimitiveReader getPrimitiveReader(String strFile) {
		// For each registered primitive reader
		Iterator iterator = alPrimReaders.iterator();
		while (iterator.hasNext()) {
			IPrimitiveReader reader = (IPrimitiveReader) iterator.next();

			// If we can read the file, we're done
			if (reader.canRead(strFile))
				return reader;
		}

		// No such primitive reader to read this file
		return null;
	}
}
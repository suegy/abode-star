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


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import abode.editing.LispBlob;


/**
 * Reads Primitives from .lisp files
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class LispPrimitiveParser implements IPrimitiveReader {
	/**
	 * Read the contents of a text file
	 * 
	 * @param strFile
	 *            File to load
	 * @return Contents of file
	 */
	private String getFileContents(String strFile) {
		try {
			BufferedReader brInputFile = new BufferedReader(new FileReader(strFile));
			String strFileContent = "", temp = "";
			while ((temp = brInputFile.readLine()) != null)
				strFileContent += temp + "\n";
			brInputFile.close();
			return "(" + strFileContent + ")";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Can this reader process the given file?
	 * 
	 * @param strFile
	 *            Path to file to read
	 * @return True if file contains primitives, false if not
	 */
	public boolean canRead(String strFile) {
		try {
			// This appears to be a check to see if a file exists. No. No.
			// Sort this out when the API is available
			new LispBlob(getFileContents(strFile));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the list of action primitives file
	 * 
	 * @param strFile
	 *            Path to file to read.
	 * @return Arraylist of actions found in the file
	 */
	public ArrayList getActions(String strFile) {
		ArrayList results = new ArrayList();
		LispBlob blob = new LispBlob(getFileContents(strFile));
		scanFor(blob, results, "add-act");
		return results;
	}

	/**
	 * Get the description of file
	 * 
	 * @return Description of the types of file this parser can read.
	 */
	public String getDescription() {
		return "Primitive Parser for .lisp files";
	}

	/**
	 * Get the list of sense primitives from this file
	 * 
	 * @param strFile
	 *            Path to file to read.
	 * @return Arraylist of sense primitives found in the file
	 */
	public ArrayList getSenses(String strFile) {
		ArrayList results = new ArrayList();
		LispBlob blob = new LispBlob(getFileContents(strFile));
		scanFor(blob, results, "add-sense");
		return results;
	}

	/**
	 * Scan a blob of lisp for primitives of certain types
	 * 
	 * @param blob
	 *            Blob of lisp we're working with
	 * @param storeTo
	 *            Arraylist we're storing primitives to
	 * @param scanText
	 *            Text that indicates the desired type.
	 */
	private void scanFor(LispBlob blob, ArrayList storeTo, String scanText) {
		if (blob.isList()) {
			ArrayList subList = blob.toList();
			// If there are more than one items in this list, it's a candidate
			if (subList.size() > 1) {
				LispBlob first = (LispBlob) subList.get(0);

				// If the first item is the prologue we'return looking for
				if (first.getText().equals(scanText)) {
					LispBlob second = (LispBlob) subList.get(1);

					// Remove the leading ' - It seems people just can't make up
					// their minds about whether or not identifiers need these!
					String name = second.getText().replaceAll("'", "");
					storeTo.remove(name);
					storeTo.add(name);
				}
			}

			Iterator iterator = subList.iterator();
			while (iterator.hasNext())
				scanFor((LispBlob) iterator.next(), storeTo, scanText);
		}
	}
}

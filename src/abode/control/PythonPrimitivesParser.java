/* CVS: CobaltVault SERVER:\\halo TREE:\abode_mainline
 *       _    ____   ___  ____  _____ 
 *      / \  | __ ) / _ \|  _ \| ____|      Advanced
 *     / _ \ |  _ \| | | | | | |  _|        Behavior
 *    / ___ \| |_) | |_| | |_| | |___       Oriented
 *   /_/   \_\____/ \___/|____/|_____|      Design
 *         www.cobaltsoftware.net           Environment
 
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

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Provides a simple abstraction for loading primitives from a .py file.
 */
public class PythonPrimitivesParser implements IPrimitiveReader {
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

	public boolean canRead(String fileName) {
		String text = getFileContents(fileName);
		if ((text.indexOf("add_sense(") > 0) || (text.indexOf("add_act(") > 0))
			return true;
		return false;
	}

	public ArrayList getSenses(String fileName) {
		return Scan("\\Qadd_sense(\"\\E(.*)\\Q\"\\E", getFileContents(fileName));
	}

	public ArrayList getActions(String fileName) {
		return Scan("\\Qadd_act(\"\\E(.*)\\Q\"\\E", getFileContents(fileName));
	}

	private ArrayList Scan(String patternText, String text) {
		Pattern pattern = Pattern.compile(patternText);
		Matcher matcher = pattern.matcher(text);

		ArrayList results = new ArrayList();
		while (matcher.find())
			results.add(matcher.group(1));
		return results;
	}

	/**
	 * Get a description of this parser
	 */
	public String getDescription() {
		return "Simple .py Primitives Reader";
	}
}
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
package abode;



import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import abode.editing.LispBlob;




/**
 * The configuration class encapsulates the abilities to modify
 * program settings and attributes and store them to file.
 *
 * @author  CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class Configuration {
	/**
	 * Private constructor to prevent instanciation
	 **/
	private Configuration() {
	}

	// Where we store the configuration file sections
	private static ArrayList sections = null;

	/**
	 * One-time initialization
	 **/
	static {
		sections = new ArrayList();
		try {

			BufferedReader brInputFile = null;
			try {
				brInputFile = new BufferedReader(new FileReader("/cfg/Configuration.lisp"));
			} catch (Exception e) {
				System.out.println("Loading default configuration!");
				InputStream is = Main.class.getResourceAsStream("/cfg/DefaultConfiguration.lisp");
				InputStreamReader isr = new InputStreamReader(is);
				brInputFile = new BufferedReader(isr);
			}

			String strFileContent = "", temp = "";
			while ((temp = brInputFile.readLine()) != null)
				strFileContent += temp + "\n";

			LispBlob blob = new LispBlob(strFileContent);
			blob.remove(0);
			sections = recurse(blob, 0);
			brInputFile.close();
		} catch (Exception e) {
			System.out.println("Could not read configuration file.");
			e.printStackTrace();
		}
	}

	/**
	 * Recursively load elements
	 * 
	 * @param input Array of LispBlob objects that must be traversed
	 * @param index Recusive depth inside lists
	 */
	private static ArrayList recurse(LispBlob input, int index) {
		ArrayList output = new ArrayList();

		Iterator it = input.iterator();
		while (it.hasNext()) {
			LispBlob next = (LispBlob) it.next();
			if (!next.isList())
				output.add(next.getText());
			else
				output.add(recurse(next, index + 1));
		}

		return output;
	}

	/**
	 * Look up a configuration file section by a slash
	 * delimited path
	 *
	 * @param path Path in configuration to load
	 * @return Arraylist of elements at this point
	 **/
	public static ArrayList getByKey(String path) {
		// Split the path string by slashes
		String[] bits = path.split("/");
		if (bits.length < 1)
			return null;

		// Start at the root of our lists
		ArrayList current = sections;

		// For each part of our path
		for (int x = 0; x < bits.length; x++) {
			// We havent matched this path yet
			boolean done = false;

			// Iterate over the lists at this level
			Iterator it = current.iterator();
			while (it.hasNext()) {
				Object object = it.next();
				if (!(object instanceof ArrayList))
					continue;

				ArrayList list = ((ArrayList) object);
				if (list.get(0).toString().equals(bits[x])) {
					current = list;
					done = true;
					break;
				}
			}

			if (!done)
				return null;
		}

		return current;
	}

	/**
	 * Update configuration file and save changes to disk
	 **/
	public static void update() {
		String buildString = "";
		buildString += recurseList(true, sections, 1);

		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("Configuration.lisp")));
			writer.println(buildString);
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR SAVING CONFIGURATION: " + e.toString());
		}
	}

	/**
	 * Recurse the list and generate output
	 *
	 * @param first Is this the first level element?
	 * @param list List to recursively build from
	 * @param depth Recursive depth in lists
	 * @return Composited lisp syntax representing contents of arraylist
	 **/
	private static String recurseList(boolean first, ArrayList list, int depth) {
		String result = "";

		// Open list
		for (int x = 0; x < depth; x++)
			result += "\t";
		if (first)
			result += "(configuration\r\n";
		else
			result += "(\r\n";

		// For each item in the list
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (next instanceof ArrayList) {
				// If this is a sub-list then recurse
				ArrayList subList = (ArrayList) next;
				result += recurseList(false, subList, depth + 1);
			} else {
				String text = next.toString();
				for (int x = 0; x < depth + 1; x++)
					result += "\t";
				result += text + "\r\n";
			}
		}

		// Close list
		for (int x = 0; x < depth; x++)
			result += "\t";
		result += ")\r\n";

		return result;
	}

	/**
	 * Load a colour value from the configuration
	 *
	 * @param Path to find the colour at
	 * @return Configuration setting in the form of a colour
	 **/
	public static Color getRGB(String key) {
		ArrayList setting = getByKey(key);
		Color rgb = new Color(Integer.parseInt(setting.get(1).toString()), Integer.parseInt(setting.get(2).toString()), Integer.parseInt(setting.get(3).toString()));
		return rgb;
	}
}
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

import abode.editing.LispBlob;




/**
 * The configuration class encapsulates the abilities to modify
 * program settings and attributes and store them to file.
 *
 * @author  CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 * 
 * modified additional casts to correct issue with Generics
 * @author  Gaudl, Swen
 * @version 1.1
 */
public class Configuration {
	/**
	 * Private constructor to prevent instanciation
	 **/
	private Configuration() {
	}

	// Where we store the configuration file sections
	private static ArrayList<Object> sections = null;

	/**
	 * One-time initialization
	 **/
	static {
		sections = new ArrayList<Object>();
		try {

			BufferedReader brInputFile = null;
			try {
				brInputFile = new BufferedReader(new FileReader("Abode-star.cfg"));
			} catch (Exception e) {
				System.out.println("Loading default configuration!");
				InputStream is = Configuration.class.getResourceAsStream("/cfg/DefaultConfiguration.lisp");
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
	private static ArrayList<Object> recurse(LispBlob input, int index) {
		ArrayList<Object> output = new ArrayList<Object>();

		for (LispBlob next : input) {
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
	public static ArrayList<Object> getByKey(String path) {
		// Split the path string by slashes
		String[] bits = path.split("/");
		ArrayList<Object> current = sections;
		if (bits.length < 1)
			return null;

		
		// For each part of our path
		for (int x = 0; x < bits.length; x++) {
			// We have not matched this path yet
			boolean done = false;

			
			// Iterate over the lists at this level
			for (Object obj : current) {
				if (!(obj instanceof ArrayList))
					continue;

				@SuppressWarnings("unchecked")
				ArrayList<Object> arrayList = (ArrayList<Object>)obj;
				
				if (arrayList.get(0).toString().equals(bits[x])) {
					current = arrayList;
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
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("Abode-star.cfg")));
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
	private static String recurseList(boolean first, ArrayList<Object> list, int depth) {
		String result = "";

		// Open list
		for (int x = 0; x < depth; x++)
			result += "\t";
		if (first)
			result += "(configuration\r\n";
		else
			result += "(\r\n";

		// For each item in the list
		for (Object next : list) {
			if (next instanceof ArrayList) {
				// If this is a sub-list then recurse
				@SuppressWarnings("unchecked")
				ArrayList<Object> subList = (ArrayList<Object>) next;
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
		ArrayList<Object> setting = getByKey(key);
		Color rgb = new Color(Integer.parseInt(setting.get(1).toString()), Integer.parseInt(setting.get(2).toString()), Integer.parseInt(setting.get(3).toString()));
		return rgb;
	}
}
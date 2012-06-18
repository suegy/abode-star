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

import abode.visual.JEditorWindow;

/**
 * The console writer class encapsulates a single function to make accesing 
 * the console text panel for the file easier. In future more methods could go
 * here to expand the functionality of the console.
 *
 * @author  CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class ConsoleWriter {
	/**
	 * Write a line to the console on the specified window
	 *
	 * @param text The text to display
	 * @param window The window for which the console is being updated
	 **/
	public static void writeLine(String text, JEditorWindow window) {
		window.getOutput().setText(text);
		System.out.println(text);
	}

}

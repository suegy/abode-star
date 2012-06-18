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
package abode.editing;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides simple API for dealing with lisp code in a string and enables us to
 * readily break the lists down into items.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class LispBlob {
	// Do we want the parser to write out information to System.out.println?
	private static boolean DEBUG_PARSER = false;

	// Raw text of this lisp blob
	private String strText = null;

	// Child blobs of this node
	private ArrayList alChildren = null;

	/**
	 * Initialize a lisp blob from a given string, so we can process the list
	 * and/or it's children.
	 * 
	 * @param lispString
	 *            Lisp code in string form
	 */
	public LispBlob(String lispString) {
		// Copy the text to the instance for safe keeping
		strText = lispString;

		// Parse and validate
		updateDOM();
	}

	/**
	 * Get an iterator over our children
	 * 
	 * @return Iterator over children
	 */
	public Iterator getIterator() {
		return alChildren.iterator();
	}

	/**
	 * Is this instance a list?
	 * 
	 * @return True if this lisp blob is a list
	 */
	public boolean isList() {
		if (alChildren == null)
			return false;
		return true;
	}

	/**
	 * Get the text this blob is comprised of
	 */
	public String getText() {
		return strText;
	}

	/**
	 * Set the text this blob is comprised of.
	 * 
	 * @param strNewText
	 */
	public void setText(String strNewText) {
		// If we're the same text, don't bother changing
		if (strNewText.equals(strText))
			return;

		// Save to instance then update the DOM so our children
		// are correctly regenerated.
		strText = strNewText;
		updateDOM();
	}

	/**
	 * Does this blob have list items?
	 */
	public boolean hasChildren() {
		// If we're not a list, or the child list is empty, we
		// clearly don't have children.
		if ((!isList()) || (alChildren.isEmpty()))
			return false;

		return true;
	}

	/**
	 * Clear the DOM ready for a re-parsing
	 */
	private void clearDOM() {
		alChildren = null;
	}

	/**
	 * Update the child lists or the node text for this lisp string
	 */
	private void updateDOM() {
		// Clear the DOM and remove existing children
		clearDOM();

		String child = ""; // For building up a child object
		int bracketDepth = 0; // How deep we are in the sub-lists
		boolean openQuote = false; // We're not inside a string literal either
		boolean skipQuote = false; // Are we skipping a quote?

		// Remove whitespace from the string start and end
		String currentString = stripWhiteSpace(stripComments(strText.trim()));

		if (DEBUG_PARSER)
			System.out.println("PARSING: " + currentString);

		// Are we dealing with a list? If not, we're actually done and this
		// is a non-lisp text blob.
		if (!(currentString.charAt(0) == '(')) {
			if (DEBUG_PARSER)
				System.out.println("FINISHED PARSING (CONSTANT)");
			return;
		}

		// We need to be at least two characters long to be a list, a ( and a )
		if (currentString.length() < 2) {
			if (DEBUG_PARSER) {
				System.out.println("Error Encounted in: ");
				System.out.println(strText);
			}
		}

		// Create a list for our children
		alChildren = new ArrayList();

		// For each character in between
		for (int index = 1; index < currentString.length() - 1; index++) {
			// Get the character at this point
			char c = currentString.charAt(index);

			// If this is a comment start, and we're not in a string literal
			if ((c == ';') && (c=='#') && (!openQuote))
				skipQuote = true;

			if ((c == '\n') && (skipQuote == true))
				skipQuote = false;

			if (skipQuote == true)
				continue;

			// If we're a ( and we're not in a list then we need to increase
			// nesting level
			if ((c == '(') && (!openQuote)) {
				// If we've hit a bracket at level 0, it means what's before us
				// may be a sibling
				if (bracketDepth == 0) {
					// Trim the text so far so we remove trailing/leading space
					// and can see clearly what we have....
					child = child.trim();

					// If there's any text here, we have a child
					if (child.length() > 0) {
						if (DEBUG_PARSER)
							System.out.println("Adding child node:- " + child);

						// Add to list and reset for next child
						alChildren.add(new LispBlob(child));
						child = "";
					}
				}

				bracketDepth++;
			}

			// Add to our potential child string
			child = child + c;

			// If we're a " then we need to flag our state so we keep track of
			// quote marks
			if (c == '"')
				openQuote = !openQuote;

			// If we're a ) and we're not in a quote then we need to decrease
			// nesting level
			if ((c == ')') && (!openQuote)) {
				bracketDepth--;
				if (bracketDepth < 0) {
					if (DEBUG_PARSER) {
						System.out.println("Error Encounted in: ");
						System.out.println(strText);
					}

					throw new RuntimeException("No opener for closing ) in expression. Check your lisp syntax");
				}
			}

			// Is this character a space or a closer bracket? If we're a
			// space/closing bracket and now at zero
			// depth, then we can bud off a new child object and add them to the
			// list of children.
			if ((Character.isWhitespace(c) || (c == ')')) && (bracketDepth == 0) && (!openQuote)) {
				// Trim the text so far so we remove trailing/leading space
				// and can see clearly what we have....
				child = child.trim();

				// If there's any text here, we have a child
				if (child.length() > 0) {
					if (DEBUG_PARSER)
						System.out.println("Adding child node:- " + child);

					// Add to list and reset for next child
					alChildren.add(new LispBlob(child));
					child = "";
				}
			}
		}

		// Now that we've finished parsing, there might be some text left
		child = child.trim();
		if (child.length() > 0) {
			alChildren.add(new LispBlob(child));
			if (DEBUG_PARSER)
				System.out.println("Added child " + child);
		}

		if (DEBUG_PARSER)
			System.out.println("FINISHED PARSING (LIST)");
	}

	/**
	 * Remove the comments from a lisp string. May have problems with strings,
	 * but you really shouldn't be putting such things into an AI definition
	 * file anyway.
	 */
	public static String stripComments(String lisp) {
		// Where we store our string we're building
		String result = "";

		// Are we in a comment block?
		boolean comment = false;

		// For each character of the string, move across one at a time
		for (int index = 0; index < lisp.length(); index++) {
			char c = lisp.charAt(index);

			// Start a comment block
			if (c == ';')
				comment = true;

			// We're not a comment, so add result to output
			if (!comment)
				result += c;

			// All lisp comments are terminated by newlines anyway
			if (c == '\n')
				comment = false;
		}

		// Show the effects numerically
		if (DEBUG_PARSER) {
			System.out.println("Stripping comments from input string....");
			System.out.println("BEFORE:- " + lisp.length());
			System.out.println("AFTER:-" + result.length());
		}

		return result;
	}

	/**
	 * Remove redundant whitespace from a lisp string. This can make it a lot
	 * easier to parse things. You must remove all comments before performing
	 * this action, otherwise behaviour may be undefined.
	 */
	public static String stripWhiteSpace(String lisp) {
		// Local Variables
		String result = ""; // Output string we are building
		boolean inQuote = false; // We're not in a quote yet
		boolean lastSpace = false; // Was the last character whitspace too?

		// Trim start and finish whitespace the old fashioned way
		lisp = lisp.trim();

		// Move across the characters one at a time
		for (int index = 0; index < lisp.length(); index++) {
			// Get the character at this point in the string
			char c = lisp.charAt(index);

			// Toggle in and out of quote contexts for string literals
			if (c == '"') {
				inQuote = !inQuote;
			}
			
			// If we're in a quote, we add this string regardless
			if (inQuote) {
				result = result + c;
				lastSpace = false;
			} else {
				// Are we adding a space to the string?
				if (Character.isWhitespace(c)) {
					// if the last character wasnt a space, then we can add this
					// to the string
					if (!lastSpace) {
						result = result + c;
					}
						
					// The last character (i.e. this one) was a space
					lastSpace = true;
				} else {
					// Add a normal character to the string
					result = result + c;
					lastSpace = false;
				}
			}
		}

		return result;
	}

	/**
	 * Convert this list to an arraylist
	 */
	public ArrayList toList() {
		ArrayList children = new ArrayList();
		Iterator it = getIterator();
		while (it.hasNext()) {
			children.add(it.next());
		}
		return children;
	}
}
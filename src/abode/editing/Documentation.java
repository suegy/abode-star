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

/**
 * The Documentation construct exists to hold version information that is held
 * in a standard structure at the start of the file.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class Documentation {
	// File Title
	private String strTitle = null;

	// Author information
	private String strAuthor = null;

	// Memo
	private String strMemo = null;

	/**
	 * Initialize a blank documentation construct
	 */
	public Documentation() {
		strTitle = "Your Title";
		strAuthor = "Your Name";
		strMemo = "Your file comments";
	}

	/**
	 * Initialize a documentation construct with all of the various options set.
	 * 
	 * @param title
	 *            Title of the file
	 * @param authorInfo
	 *            Who wrote the file
	 * @param memo
	 *            Some notes about the file
	 */
	public Documentation(String title, String authorInfo, String memo) {
		strTitle = title;
		strAuthor = authorInfo;
		strMemo = memo;
	}

	/**
	 * Get the title of the file
	 * 
	 * @return Title of the file
	 */
	public String getTitle() {
		return strTitle;
	}

	/**
	 * Set the title of the file
	 * 
	 * @param title
	 *            Title of the file
	 */
	public void setTitle(String title) {
		strTitle = title;
	}

	/**
	 * Get the author of the file
	 * 
	 * @return Author of the file
	 */
	public String getAuthor() {
		return strAuthor;
	}

	/**
	 * Set the author of the file
	 * 
	 * @param author
	 *            Author of the file
	 */
	public void setAuthor(String author) {
		strAuthor = author;
	}

	/**
	 * Get file memo
	 * 
	 * @return Memo/notes for this file.
	 */
	public String getMemo() {
		return strMemo;
	}

	/**
	 * Set file memo
	 * 
	 * @param memo
	 *            New memo/notes for this file.
	 */
	public void setMemo(String memo) {
		strMemo = memo;
	}
}
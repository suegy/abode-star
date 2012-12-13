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
/*
 * This is work in progress and needs serious optimisation 
 */

package abode.control;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import model.IEditableElement;
import model.TimeUnit;
import model.posh.ActionElement;
import model.posh.ActionPattern;
import model.posh.Competence;
import model.posh.CompetenceElement;
import model.posh.DriveCollection;
import model.posh.DriveElement;
import model.posh.LearnableActionPattern;
import abode.JAbode;
import abode.editing.CommentScraper;
import abode.editing.Documentation;
import abode.editing.LispBlob;

/**
 * The DotLapReader reads in lisp-formatted .lap files and then produces a
 * completed LearnableActionPattern object representing the contents of the
 * file.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class DotLapReader implements ILAPReader {
	
	private boolean debug;
	
	/*	*
	 * Is the specified file readable to this class?
	 * @param fileName
	 *            Path to the file to read
	 * @return True if readable, false if not
	 */
	
	public DotLapReader()
	{
		debug = false;
	}
	
	@Override
	public boolean canRead(String strFileName) {
		File f = new File (strFileName);
		
		if (!f.exists()) {
			return false;
		}
		
		return true;
	}
	
	protected void setDebug(boolean value)
	{
		this.debug = value;
	}
	
	/**
	 * Read the contents of a text file
	 * 
	 * @param strFile
	 *            File to load
	 * @return Contents of file
	 */
	public String getFileContents(String strFileName) {
		try {
			BufferedReader brInputFile = new BufferedReader(new FileReader(strFileName));
			String strFileContent = "", temp = "";
			while ((temp = brInputFile.readLine()) != null) {
				strFileContent += temp + "\n";
			}
			brInputFile.close();
			return strFileContent;
		} catch (Exception e) {
			return "Unable to load file.";
		}
	}

	@Override
	public LearnableActionPattern load(String strFileName) throws FileNotFoundException, IOException, Exception {
		String planFileContents = this.getFileContents(strFileName);
		return lapFromPlan(planFileContents);
	}
	
	/**
	 * Load the specified file and get an action pattern
	 * 
	 * @param fileName
	 *            File to load
	 * @return Loaded file in the form of an object model
	 */
	public LearnableActionPattern lapFromPlan(String strFileContent) throws FileNotFoundException, IOException, Exception {
		
		// Parse the Lisp document object model
		LispBlob blob = new LispBlob(strFileContent);

		ArrayList elements = new ArrayList();
		if (!blob.isList()) {
			throw new Exception("Invalid file structure (Initial element not a list.). Cannot parse this file.");
		}
		
		// Default to blank documentation
		Documentation documentation = new Documentation();

		
		// Get the comments in readable format
		CommentScraper topLevelComments = new CommentScraper(strFileContent, 1);
		
		// The count / index of the elements that are currently being added to the tree.
		// Used to get the associated comments.
		int elementIndex = 1;

		// Carry on scanning
		for (LispBlob child : blob) {
			// Get this child of the list
			
			// Add it to our children list after parsing it
			Object element = parseElement(child, topLevelComments.getCommentString(elementIndex));
			if (element instanceof Documentation) {
				documentation = (Documentation) element;
			} else {
				elements.add(element);
				elementIndex++;
			}
		}

		return new LearnableActionPattern(elements, documentation);
	}

	/**
	 * Parse a construct from the file
	 */
	private Object parseElement(LispBlob block, String comments) throws Exception {
		// All of the various constructs must be lists, so we exception
		// if there is no list inside this list.
		if (!block.isList()) {
			throw new Exception("Invalid file structure (Elements must be composed of lists!). Cannot parse this file.");
		}
		
		// Copy out each of the children into an arraylist for processing as an
		// array
		ArrayList children = block.toList();

		// The first item of the first child will be a constant
		LispBlob first = (LispBlob) children.get(0);

		// This constant is our type identifier
		String strType = first.getText();
		
		IEditableElement returnedElement =  null;
		
		if (strType.equals("documentation")) {
			return parseDocumentation(children);
		}
		if (strType.equals("AP")) {
			returnedElement = parseActionPattern(children, false);
			returnedElement.setDocumentation(comments);
		}
		if (strType.equals("C")) {
			returnedElement = parseCompetence(children, false);
			returnedElement.setDocumentation(comments);
		}
		if (strType.equals("DC")) {
			returnedElement = parseDriveCollection(children, false, false);
			returnedElement.setDocumentation(comments);
		}
		if (strType.equals("RDC")) {
			returnedElement = parseDriveCollection(children, true, false);
			returnedElement.setDocumentation(comments);
		}
		return returnedElement;
	}

	/**
	 * Parse documentation
	 * 
	 * @param elements
	 *            Arraylist containg documentation elements
	 * @return Documentation construct
	 */
	private Documentation parseDocumentation(ArrayList elements) {
		return new Documentation(((LispBlob) elements.get(1)).getText().replaceAll("\"", ""), ((LispBlob) elements.get(2)).getText().replaceAll("\"", ""), ((LispBlob) elements.get(3)).getText().replaceAll("\"", ""));
	}

	/**
	 * Parse a DC or (R) DC block
	 */
	private DriveCollection parseDriveCollection(ArrayList elements, boolean realTime, boolean wasCommented)  {
		// Get the name, parse the goal and the get the list of drive lists
		String strName = ((LispBlob) elements.get(1)).getText();
		ArrayList goal;
		try {
			goal = parseGoal((LispBlob) elements.get(2));
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList lists = ((LispBlob) elements.get(3)).toList();

		// Remove the constant "Drives" which is at the start of the list
		lists.remove(0);

		// For each list of drive elements
		ArrayList elementLists = new ArrayList();
		Iterator listIterator = lists.iterator();
		while (listIterator.hasNext()) {
			ArrayList subList = new ArrayList();
			Iterator subListIterator = ((LispBlob) listIterator.next()).toList().iterator();
			while (subListIterator.hasNext())
				subList.add(parseDriveElement((LispBlob) subListIterator.next()));
			elementLists.add(subList);
		}

		if (wasCommented) {
			JAbode.writeEnvironmentLine("Parsed commented drive collection (" + strName + ")");
			return new DriveCollection(strName, realTime, goal, elementLists, false);
		} else {
			JAbode.writeEnvironmentLine("Parsed drive collection (" + strName + ")");
			return new DriveCollection(strName, realTime, goal, elementLists);
		}
	}

	/**
	 * Parse a single drive element
	 */
	private DriveElement parseDriveElement(LispBlob blob) throws Exception {
		// Convert to list, extract name, trigger, action name and whether or
		// not there's a timeout
		ArrayList list = blob.toList();

		String strName = ((LispBlob) list.get(0)).getText();
		ArrayList trigger = parseTrigger((LispBlob) list.get(1));
		String strAction = ((LispBlob) list.get(2)).getText();

		JAbode.writeEnvironmentLine("Parsed Drive Element (" + strName + ")");

		// DriveElement(String name, ArrayList trigger, String action)
		if (list.size() == 4) {
			TimeUnit time = parseTimeUnit((LispBlob) list.get(3));
			return new DriveElement(strName, trigger, strAction, time);
		}

		return new DriveElement(strName, trigger, strAction);
	}

	/**
	 * Parse a competence block
	 */
	private Competence parseCompetence(ArrayList elements, boolean commented) throws Exception {
		// Extract some infomration from the list
		String strName = ((LispBlob) elements.get(1)).getText();
		TimeUnit timeUnit = parseTimeUnit((LispBlob) elements.get(2));
		ArrayList goal = parseGoal((LispBlob) elements.get(3));
		LispBlob elementLists = (LispBlob) elements.get(4);

		// Recurse the lists of lists of competence elements and build our
		// structure
		ArrayList compElements = new ArrayList();
		Iterator it = elementLists.getIterator();

		// Skip over constant value "elements"
		if (it.hasNext())
			it.next();
		// For each list of lists
		while (it.hasNext()) {
			// Iterate over the list inside
			LispBlob list = (LispBlob) it.next();
			Iterator subList = list.toList().iterator();

			ArrayList buildList = new ArrayList();
			while (subList.hasNext()) {
				// Produce the competence
				CompetenceElement ce = parseCompetenceElement((LispBlob) subList.next());
				buildList.add(ce);
			}
			compElements.add(buildList);
		}
		if (commented) {
			JAbode.writeEnvironmentLine("Parsed commented competence (" + strName + ")");
			return new Competence(strName, timeUnit, goal, compElements, false);
		} else {
			JAbode.writeEnvironmentLine("Parsed competence (" + strName + ")");
			return new Competence(strName, timeUnit, goal, compElements);
		}

	}

	/**
	 * Parse a competence element
	 */
	private CompetenceElement parseCompetenceElement(LispBlob blob) throws Exception {
		ArrayList list = blob.toList();
		// Get the name, trigger list and resulting action
		String strName = ((LispBlob) list.get(0)).getText();
		ArrayList alTrigger = parseTrigger((LispBlob) list.get(1));
		String action = ((LispBlob) list.get(2)).getText();

		JAbode.writeEnvironmentLine("Parsed competence element (" + strName + ")");

		if (list.size() == 3)
			return new CompetenceElement(strName, alTrigger, action);

		if (list.size() == 4) {
			int iRet = Integer.parseInt(((LispBlob) list.get(3)).getText());
			return new CompetenceElement(strName, alTrigger, action, iRet);
		}

		throw new Exception("Invalid number of elements for competence element!");
	}

	/**
	 * Parse a trigger block
	 */
	private ArrayList parseTrigger(LispBlob blob) throws Exception {
		ArrayList source = blob.toList();
		LispBlob blob2 = (LispBlob) source.get(1);
		if (!blob2.isList())
			return new ArrayList(); // "nil"
		return parseActionElementList(blob2);
	}

	/**
	 * Parse a goal block - This is essentially an actionelement list with a bit
	 * of wrapping
	 */
	private ArrayList parseGoal(LispBlob blob) throws Exception {
		ArrayList list = blob.toList();
		return parseActionElementList((LispBlob) list.get(1));
	}

	/**
	 * Parse an arraylist of elements that represents an action pattern
	 */
	private ActionPattern parseActionPattern(ArrayList elements, boolean wasCommented) throws Exception {
		// Our name is the second element of the list
		String strName = ((LispBlob) elements.get(1)).getText();

		// Our timeout can be parsed from the third element of the list
		TimeUnit timeOut = parseTimeUnit((LispBlob) elements.get(2));

		// Our list of ActionElements is the fourth element of the list
		LispBlob ael = (LispBlob) elements.get(3);

		// Parse each element of this list
		ArrayList children = parseActionElementList(ael);

		if (wasCommented) {
			JAbode.writeEnvironmentLine("Parsed commented action pattern named: " + strName);
			return new ActionPattern(strName, timeOut, children, false);
		} else {
			JAbode.writeEnvironmentLine("Parsed action pattern named :" + strName);
			return new ActionPattern(strName, timeOut, children);
		}
	}

	/**
	 * Parse a list of action elements
	 */
	private ArrayList parseActionElementList(LispBlob blob) throws Exception {
		ArrayList children = new ArrayList();
		Iterator it = blob.getIterator();
		while (it.hasNext())
			children.add(parseActionElement((LispBlob) it.next()));
		return children;
	}

	/**
	 * Parse an action element from a blob of lisp
	 */
	private ActionElement parseActionElement(LispBlob blob) throws Exception {
		// Simple case, we're just a name
		if (!blob.isList())
			return new ActionElement(false, blob.getText());

		ArrayList list = blob.toList();
		String name = ((LispBlob) list.get(0)).getText();
		switch (list.size()) {
		case 3:
			String pred = ((LispBlob) list.get(2)).getText();
			String val = ((LispBlob) list.get(1)).getText();
			return new ActionElement(name, val, pred);
		case 2:
			String val2 = ((LispBlob) list.get(1)).getText();
			return new ActionElement(name, val2);
		default:
			return new ActionElement(true, name);
		}
	}

	/**
	 * Parse a unit of time from a lisp blob
	 */
	private TimeUnit parseTimeUnit(LispBlob blob) throws Exception {
		ArrayList list = blob.toList();
		String unit = ((LispBlob) list.get(0)).getText();
		String val = ((LispBlob) list.get(1)).getText();
		return new TimeUnit(unit, Double.parseDouble(val));
	}
}
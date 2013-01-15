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

import exception.AbodeException;

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
	public LearnableActionPattern load(String strFileName) throws FileNotFoundException, IOException, AbodeException {
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
	public LearnableActionPattern lapFromPlan(String strFileContent) throws FileNotFoundException, IOException,AbodeException {
		
		// Parse the Lisp document object model
		LispBlob blob = new LispBlob(strFileContent);

		ArrayList elements = new ArrayList();
		if (!blob.isList()) {
			throw new AbodeException("Invalid file structure (Initial element not a list.). Cannot parse this file.");
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
	private Object parseElement(LispBlob block, String comments) throws AbodeException{
		// All of the various constructs must be lists, so we exception
		// if there is no list inside this list.
		if (!block.isList()) {
			throw new AbodeException("Invalid file structure (Elements must be composed of lists!). Cannot parse this file.");
		}
		
		
		// The first item of the first child will be a constant
		LispBlob first = block.getChild(0);

		// This constant is our type identifier
		String strType = first.getText();
		
		IEditableElement returnedElement =  null;
		
		if (strType.equals("documentation")) {
			return parseDocumentation(block);
		}
		if (strType.equals("AP")) {
			returnedElement = parseActionPattern(block, false);
			returnedElement.setDocumentation(comments);
		}
		if (strType.equals("C")) {
			returnedElement = parseCompetence(block, false);
			returnedElement.setDocumentation(comments);
		}
		if (strType.equals("DC")) {
			returnedElement = parseDriveCollection(block, false, false);
			returnedElement.setDocumentation(comments);
		}
		if (strType.equals("RDC")) {
			returnedElement = parseDriveCollection(block, true, false);
			returnedElement.setDocumentation(comments);
		}
		if (returnedElement == null) {
			throw new AbodeException("File does not contain POSH elements!");
		}
		return returnedElement;
	}

	/**
	 * Parse documentation
	 * 
	 * @param elements
	 *            Arraylist containg documentation elements
	 * @return Documentation construct
	 * @throws AbodeException 
	 */
	private Documentation parseDocumentation(LispBlob elements) throws AbodeException {
		String title = "";
		String author= "";
		String memo = "";
		
		try
		{
			title =  elements.getChild(1).getText().replaceAll("\"", "");
			author = elements.getChild(2).getText().replaceAll("\"", "");
			memo = elements.getChild(3).getText().replaceAll("\"", "");
		}
		catch (AbodeException e)
		{
			JAbode.writeEnvironmentLine("Error while parsing documentation "+title+"!");
			
			if (debug)
				throw new AbodeException("Error while parsing documentation "+title+"!",e);
		}
		return new Documentation(title, author, memo);
	}

	/**
	 * Parse a DC or (R) DC block
	 * @throws AbodeException 
	 */
	private DriveCollection parseDriveCollection(LispBlob elements, boolean realTime, boolean wasCommented) throws AbodeException  {
		// Get the name, parse the goal and the get the list of drive lists
		String strName = "";
		ArrayList goal = new ArrayList();
		try 
		{
			strName = elements.getChild(1).getText();
			goal 	= parseGoal( elements.getChild(2));
		} 
		catch (AbodeException e) {
			JAbode.writeEnvironmentLine("A parsing error has ocured while reading the DriveCollection "+strName+"!");
			if(debug)
				e.printStackTrace();
		} catch (Exception e) {
			JAbode.writeEnvironmentLine("An internal error has ocured while parsing the DriveCollection "+strName+"!");
			if(debug)
				e.printStackTrace();
		}
		

		// Remove the constant "Drives" which is at the start of the list
		elements = elements.getChild(3);
		elements.remove(0);

		// For each list of drive elements
		ArrayList<Object> elementLists = new ArrayList<Object>();
		for (LispBlob element : elements) {
			ArrayList subList = new ArrayList();
			for (LispBlob subElem : element) {
				subList.add(parseDriveElement(subElem));
			}
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
	 * @throws AbodeException 
	 */
	private DriveElement parseDriveElement(LispBlob blob) throws AbodeException {
		// extract name, trigger, action name and whether or
		// not there's a timeout
		
		String strName = blob.getChild(0).getText();
		ArrayList trigger = parseTrigger(blob.getChild(1));
		String strAction = blob.getChild(2).getText();

		JAbode.writeEnvironmentLine("Parsed Drive Element (" + strName + ")");

		// DriveElement(String name, ArrayList trigger, String action)
		if (blob.size() == 4) {
			TimeUnit time = parseTimeUnit(blob.getChild(3));
			return new DriveElement(strName, trigger, strAction, time);
		}

		return new DriveElement(strName, trigger, strAction);
	}

	/**
	 * Parse a competence block
	 * @throws AbodeException 
	 */
	private Competence parseCompetence(LispBlob elements, boolean commented) throws AbodeException {
		String strName = "";
		TimeUnit timeUnit = null;
		ArrayList goal = null;
		LispBlob elementLists =  null;

		// Extract some information from the list
		try
		{
			strName = elements.getChild(1).getText();
			timeUnit = parseTimeUnit( elements.getChild(2));
			goal = parseGoal( elements.getChild(3));
			elementLists =  elements.getChild(4);
		}
		catch (AbodeException e)
		{
			if (debug) 
				System.out.println(new AbodeException("Error while parsing competence "+strName+"!",e));
			JAbode.writeEnvironmentLine("Error while parsing input file! \n Problem encountered at competence "+strName+"!");
		}
		// Recurse the lists of lists of competence elements and build our
		// structure
		ArrayList compElements = new ArrayList();
		Iterator<LispBlob> it = elementLists.iterator();

		// Skip over constant value "elements"
		if (it.hasNext())
			it.next();
		// For each list of lists
		while (it.hasNext()) {
			// Iterate over the list inside
			LispBlob list = it.next();
			
			ArrayList<CompetenceElement> buildList = new ArrayList<CompetenceElement>();
			for (LispBlob subList : list) {
				// Produce the competence
				CompetenceElement ce = parseCompetenceElement(subList);
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
	 * @throws AbodeException 
	 */
	private CompetenceElement parseCompetenceElement(LispBlob blob) throws AbodeException {
		// Get the name, trigger list and resulting action
		String strName = blob.getChild(0).getText();
		ArrayList alTrigger = parseTrigger(blob.getChild(1));
		String action = blob.getChild(2).getText();

		JAbode.writeEnvironmentLine("Parsed competence element (" + strName + ")");

		if (blob.size() == 3)
			return new CompetenceElement(strName, alTrigger, action);

		if (blob.size() == 4) {
			int iRet = Integer.parseInt( blob.getChild(3).getText());
			return new CompetenceElement(strName, alTrigger, action, iRet);
		}

		throw new AbodeException("Invalid number of elements for competence element!");
	}

	/**
	 * Parse a trigger block
	 * @throws AbodeException 
	 */
	private ArrayList parseTrigger(LispBlob blob) throws AbodeException {

		LispBlob blob2 = blob.getChild(1);
		if (!blob2.isList())
			return new ArrayList<ActionElement>(); // "nil"
		return parseActionElementList(blob2);
	}

	/**
	 * Parse a goal block - This is essentially an actionelement list with a bit
	 * of wrapping
	 * @throws AbodeException 
	 */
	private ArrayList parseGoal(LispBlob blob) throws AbodeException {
		try {
			return parseActionElementList(blob.getChild(1));
		} catch (AbodeException e) {
			throw new AbodeException("Error while parsing goal!", e);
		}
	}

	/**
	 * Parse an LispBlob of elements that represents an action pattern
	 * @throws AbodeException 
	 */
	private ActionPattern parseActionPattern(LispBlob elements, boolean wasCommented) throws AbodeException {
		String strName = "";
		TimeUnit timeOut = null;
		LispBlob ael = null;
		try
		{
		// Our name is the second element of the list
		strName = (elements.getChild(1)).getText();

		// Our timeout can be parsed from the third element of the list
		timeOut = parseTimeUnit(elements.getChild(2));

		// Our list of ActionElements is the fourth element of the list
		ael = elements.getChild(3);
		}
		catch (AbodeException e)
		{
			throw new AbodeException("Error while parsing ActionPattern "+strName+" !", e);
		}
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
	 * @throws AbodeException 
	 */
	private ArrayList<ActionElement> parseActionElementList(LispBlob blob) throws AbodeException {
		ArrayList<ActionElement> children = new ArrayList<ActionElement>();

		for (LispBlob child : blob) {
			
		children.add(parseActionElement(child));
		}
		return children;
	}

	/**
	 * Parse an action element from a blob of lisp
	 * @throws AbodeException 
	 */
	private ActionElement parseActionElement(LispBlob blob) throws AbodeException {
		// Simple case, we're just a name
		if (!blob.isList())
			return new ActionElement(false, blob.getText());
		String name = "";
		try
		{
			name = (blob.getChild(0)).getText();
			switch (blob.size()) {
			case 3:
				String pred = (blob.getChild(2)).getText();
				String val =  (blob.getChild(1)).getText();
				return new ActionElement(name, val, pred);
			case 2:
				String val2 = (blob.getChild(1)).getText();
				return new ActionElement(name, val2);
			default:
				return new ActionElement(true, name);
			}
		}
		catch (AbodeException e)
		{
			JAbode.writeEnvironmentLine("Error while parsing action element named :" + name);
			throw new AbodeException("Error while parsing ActionElement "+name+"!");
		}
	}

	/**
	 * Parse a unit of time from a lisp blob
	 */
	private TimeUnit parseTimeUnit(LispBlob blob) throws AbodeException{

		String unit = "";
		String val = "";
		try
		{
			unit = ( blob.getChild(0)).getText();
			val =  ( blob.getChild(1)).getText();
		}
		catch (AbodeException e)
		{
			throw new AbodeException("Error while parsing TimeUnit!");
		}
		
		return new TimeUnit(unit, Double.parseDouble(val));


	}
	
}
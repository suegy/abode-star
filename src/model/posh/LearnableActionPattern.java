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
package model.posh;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import model.IEditableElement;
import model.INamedElement;
import model.TimeUnit;
import abode.Configuration;
import abode.editing.Documentation;
import abode.visual.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;


/**
 * A LearnableActionPattern encapsulates the various constructs within the LAP
 * file as an arraylist of elements which are drive collections, action patterns
 * and competances
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class LearnableActionPattern implements IEditableElement {
	// The elements are stored into this arraylist
	private ArrayList alElements = null;

	// Comments & Documentation object
	private Documentation documentation = null;
	
	//Not really relevant but easier
	public void setDocumentation(String newDocumentation) {}
	public String getElementDocumentation() { return ""; }

	/**
	 * Initialize this BOD object with an empty set of definitions.
	 */
	public LearnableActionPattern() {
		alElements = new ArrayList();
		documentation = new Documentation();
	}

	/**
	 * Initialize this BOD file with a pre-loaded arraylist of elements.
	 */
	public LearnableActionPattern(ArrayList elements) {
		if (elements == null)
			throw new NullPointerException("The pre-loaded list of elements cannot be null.");
		if (elements.isEmpty())
			throw new IllegalArgumentException("The pre-loaded list of elements cannot be empty.");

		alElements = elements;
		documentation = new Documentation();
	}

	/**
	 * Initialize this BOD file with a pre-loaded list of elements and some
	 * documentation
	 */
	public LearnableActionPattern(ArrayList elements, Documentation d) {
		this(elements);
		setDocumentation(d);
	}
	
	/**
	 * THIS MUST BE OVERRIDDEN!!!!!
	 * @return
	 */
	public void setEnabled(boolean newValue) {}
	//So must this...
	public void setDebuggerIdentifier(int debuggerIdentifier) {}
	
	public Documentation getDocumentation() {
		return documentation;
	}

	public void setDocumentation(Documentation d) {
		documentation = d;
	}

	/**
	 * Expose the arraylist to other classes
	 */
	public ArrayList getElements() {
		return alElements;
	}

	/**
	 * Do we contain an element with the name given?
	 */
	public boolean containsElementNamed(String name) {
		Iterator it = alElements.iterator();
		while (it.hasNext()) {
			IEditableElement element = (IEditableElement) it.next();
			if (element instanceof Competence) {
				Competence comp = (Competence) element;
				if (comp.getName().equals(name))
					return true;
			}
			if (element instanceof ActionPattern) {
				ActionPattern ap = (ActionPattern) element;
				if (ap.getName().equals(name))
					return true;
			}
		}

		return false;
	}

	public void printElements() {
		Iterator it = alElements.iterator();
		System.out.println("-------------");
		while (it.hasNext()) {
			System.out.println((IEditableElement)it.next());
		}
		System.out.println("-------------");
	}
	
	/**
	 * Get an element with the name given
	 */
	public IEditableElement getElementNamed(String name) {
		Iterator it = alElements.iterator();
		this.printElements();
		while (it.hasNext()) {
			IEditableElement element = (IEditableElement) it.next();
			if (element instanceof Competence) {
				Competence comp = (Competence) element;
				if (comp.getName().equals(name))
					return comp;
			}
			if (element instanceof ActionPattern) {
				ActionPattern ap = (ActionPattern) element;
				if (ap.getName().equals(name))
					return ap;
			}
			if (element instanceof DriveElement) {
				DriveElement de = (DriveElement) element;
				if (de.getName().equals(name))
					return de;
			}
		}
		return null;
	}
	
	/* public IEditableElement getDriveElementNamed(String name) {
		Iterator it = alElements.iterator();
		ArrayList driveCollections = new ArrayList();
		while (it.hasNext()) {
			IEditableElement element = (IEditableElement) it.next();
			if (element instanceof DriveCollection) {
				driveCollections.add(element);
			}
		}
		
		//Iterate through drive collections and find the named element
		Iterator driveIterator = driveCollections.iterator();
		while (driveIterator.hasNext()) {
			DriveCollection dc = (DriveCollection)driveIterator.next();
			Iterator driveIt2 = ((ArrayList)dc.getDriveElements()).iterator();
			while (driveIt2.hasNext()) {
				Object ee = driveIt2.next();
				if (ee instanceof DriveElement) {
					if (((DriveElement)ee).getName().equals(name)) {
						return (IEditableElement)ee;
					}
				}
			}
		}
		return null;
	} */

	/**
	 * Produce an overview tree
	 */
	public JTreeNode toOverviewTree() {
		JTreeNode root = new JTreeNode("Drive Collections", "", Configuration.getRGB("colours/rootNode"), this, null);
		root.setRendered(false);

		Iterator items = alElements.iterator();
		while (items.hasNext()) {
			IEditableElement item = (IEditableElement) items.next();
			if (!(item instanceof DriveCollection))
				continue;

			// Add the drive collection to the tree
			DriveCollection driveCollection = (DriveCollection) item;
			driveCollection.buildTree(root, this, false, true);
		}
		return root;
	}

	/**
	 * Produce a logic tree
	 */
	public JTreeNode toLogicTree() {
		JTreeNode root = new JTreeNode("Drive Collections", "", Configuration.getRGB("colours/rootNode"), this, null);
		root.setRendered(false);

		Iterator items = alElements.iterator();
		while (items.hasNext()) {
			IEditableElement item = (IEditableElement) items.next();
			if (!(item instanceof DriveCollection))
				continue;

			// Add the drive collection to the tree
			DriveCollection driveCollection = (DriveCollection) item;
			driveCollection.buildTree(root, this, true, true);
		}
		return root;
	}

	/**
	 * Add the specified node summary beneath the current node (i.e in overview
	 * mode, use to add the sub-tree for the action pattern or competence in
	 * question)
	 */
	public void scanActionTree(JTreeNode deNode, String action, boolean detailed, boolean expanded) {
		// Scan the file structure again looking for action patterns or
		// competences
		// that have the same name as our action, then add them to the tree.
		Iterator actionItems = alElements.iterator();
		while (actionItems.hasNext()) {
			// Look for named constructs
			IEditableElement search = (IEditableElement) actionItems.next();
			if (!(search instanceof INamedElement))
				continue;

			// If the name doesnt match, carry on
			INamedElement named = (INamedElement) search;
			if (!named.getName().equals(action))
				continue;

			search.buildTree(deNode, this, detailed, expanded);
		}
	}

	/**
	 * Produce a tree hierarchy of competence elements
	 */
	public JTreeNode toCompetenceTree() {
		JTreeNode root = new JTreeNode("Competences", "", Configuration.getRGB("colours/rootNode"), this, null);
		// root.setRendered(false);

		Iterator items = alElements.iterator();
		while (items.hasNext()) {
			IEditableElement item = (IEditableElement) items.next();
			if (!(item instanceof Competence))
				continue;
			Competence comp = (Competence) item;
			comp.buildTree(root, this, true, false);
		}

		return root;
	}

	/**
	 * Produce a diagram tree hierarchy of action pattern elements
	 */
	public JTreeNode toActionTree() {
		JTreeNode root = new JTreeNode("Action Patterns", "", Configuration.getRGB("colours/rootNode"), this, null);
		// root.setRendered(false);

		Iterator items = alElements.iterator();
		while (items.hasNext()) {
			IEditableElement item = (IEditableElement) items.next();

			if (!(item instanceof ActionPattern))
				continue;

			item.buildTree(root, this, true, false);
		}

		return root;
	}

	/**
	 * Produce a tree hierarchy of drive elements
	 */
	public JTreeNode toDriveTree() {
		JTreeNode root = new JTreeNode("Drive Collections", "", Configuration.getRGB("colours/rootNode"), this, null);
		// root.setRendered(false);

		Iterator items = alElements.iterator();
		while (items.hasNext()) {
			IEditableElement item = (IEditableElement) items.next();
			if (item instanceof DriveCollection) {
				DriveCollection dc = (DriveCollection) item;
				dc.buildTree(root, this, true, false);
			}
		}
		return root;
	}

	/**
	 * Don't call this on ourselves
	 */
	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap, boolean detailed, boolean expanded) {
		return null;
	}

	/**
	 * Populate the property grid
	 */
	public void onSelect(JAbode mainGui, JEditorWindow subGui, JDiagram diagram) {
		// Depopulate the property grid, so that the previous
		// menu options are no longer present
		mainGui.clearProperties();
	}

	// Used for creating new names
	private static int elementsMade = 1;

	/**
	 * Produce and show a context menu for this object
	 */
	public void showContextMenu(final JTreeNode showOn, final LearnableActionPattern lap, final JEditorWindow window, final JDiagram diagram) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("Learnable Action Pattern"));
		menu.addSeparator();

		JMenuItem addDriveColl = new JMenuItem("Add New Drive Collection");
		addDriveColl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = "Some Name";
				while (name.indexOf(" ") >= 0) {
					name = JOptionPane.showInputDialog(showOn.getParent(), "What would you like to name this new drive collection?", "DriveCollection" + elementsMade++).trim();

					if (name.indexOf(" ") >= 0)
						JOptionPane.showMessageDialog(showOn.getParent(), "Drive Collection Names can not contain spaces!");
				}

				DriveCollection dc = new DriveCollection(name, true, new ArrayList(), new ArrayList());
				lap.getElements().add(dc);
				window.updateDiagrams(diagram, dc);
			}
		});

		JMenuItem addComp = new JMenuItem("Add New Competence");
		addComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = "Some Name";
				while (name.indexOf(" ") >= 0) {
					name = JOptionPane.showInputDialog(showOn.getParent(), "What would you like to name this new competence?", "Competence" + elementsMade++).trim();

					if (name.indexOf(" ") >= 0)
						JOptionPane.showMessageDialog(showOn.getParent(), "Competence Names can not contain spaces!");
				}

				Competence dc = new Competence(name, new TimeUnit("seconds", 1));

				lap.getElements().add(dc);
				window.updateDiagrams(diagram, dc);
			}
		});

		JMenuItem addAp = new JMenuItem("Add New Action Pattern");
		addAp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = "Some Name";
				while (name.indexOf(" ") >= 0) {
					name = JOptionPane.showInputDialog(showOn.getParent(), "What would you like to name this new Action Pattern?", "ActionPattern" + elementsMade++).trim();

					if (name.indexOf(" ") >= 0)
						JOptionPane.showMessageDialog(showOn.getParent(), "Action Pattern names can not contain spaces!");
				}

				ActionPattern ap = new ActionPattern(name, new TimeUnit("seconds", 1));

				lap.getElements().add(ap);
				window.updateDiagrams(diagram, ap);
			}
		});

		menu.add(addDriveColl);
		menu.add(addComp);
		menu.add(addAp);
		menu.show(showOn, showOn.getX(), showOn.getY());
	}
}

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

package model.posh;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import model.IEditableElement;


import abode.Configuration;
import abode.visual.HorizontalListOrganiser;
import abode.visual.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;

/**
 * An action element is either an action primitive or composite name, or alternatively
 * is a boolean sense primitive, possibly with a value and predicate.
 *
 * @author  CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class ActionElement implements IEditableElement {
	// Is this a sense?
	private boolean bIsSense = false;

	// Name of the action, sense or composite we refer to
	private String strElementName = null;

	// Value input for the sense for comparison
	private String strValue = null;

	// Comparator / Predicate
	private String strComparator = null;
	
	// Enabled or otherwise
	private boolean enabled = true;
	
	//Docs
	private String documentation;

	/**
	 * Create an  action element with a comp/prim name only
	 *
	 * @param isSense       Is this a sense primitive or not?
	 * @param elementName   The name of this comp
	 **/
	public ActionElement(boolean isSense, String elementName) {
		bIsSense = isSense;
		strElementName = elementName;
	}

	/**
	 * Create an action element with a sense name and a value.
	 * @param strSense  Name of the sense we're constructing
	 * @param strVal    Value of the element
	 **/
	public ActionElement(String strSense, String strVal) {
		this(true, strSense);
		strValue = strVal;
	}

	/**
	 * Create an action element with a sense name, a value and a predicate
	 * @param strSense      Name of the sense we're constructing
	 * @param strVal        Value comparing against
	 * @param pred          Predicate used for comparison (i.e. < or >)
	 **/
	public ActionElement(String strSense, String strVal, String pred) {
		this(strSense, strVal);
		strComparator = pred;
	}
	
	public void setDocumentation(String newDocumentation) {
		this.documentation = newDocumentation;
	}
	
	public String getElementDocumentation() {
		return this.documentation;
	}

	/**
	 * Return whether or not this element should be enabled
	 * @return true if element is enabled, false otherwise
	 */
	public boolean isEnabled() {
		return this.enabled;
	}
	
	/**
	 * Sets whether the element is enabled
	 * @param Boolean indicating whether or not element should be enabled
	 */
	public void setEnabled(boolean value) {
		this.enabled = value;
	}
	
	/**
	 * Get the name of the element or sense we'return referring to 
	 *
	 * @return Name of this action or sense
	 * Create an action element with a sense name, value and predicate
	 **/
	public String getElementName() {
		return strElementName;
	}

	/**
	 * Return whether or not we represent a sense
	 *
	 * @return  True if we're a sense, false if we're an action or composite.
	 **/
	public boolean getIsSense() {
		return bIsSense;
	}

	/**
	 * Get the predicate we're using
	 *
	 * @return  The predicate used for comparisons or null if none is specified.
	 **/
	public String getPredicate() {
		return strComparator;
	}

	/**
	 * Get the value of the argument of this element
	 * 
	 * @return  Value we're being compared against
	 **/
	public String getValue() {
		return strValue;
	}

	/**
	 * Set the name of the element, action primitive or composite we refer to
	 *
	 * @param val   Name to set our element to
	 **/
	public void setElementName(String val) {
		strElementName = val;
	}

	/**
	 * Set whether or not we're a sense
	 **/
	public void setIsSense(boolean isSense) {
		bIsSense = isSense;
	}

	/**
	 * Set the predicate this action element uses
	 **/
	public void setPredicate(String pred) {
		strComparator = pred;
	}

	/**
	 * Set the value to compare against
	 **/
	public void setValue(String val) {
		strValue = val;
	}

	/**
	 * For accesing local references via inner class
	 **/
	public ActionElement getSelf() {
		return this;
	}
	
	/**
	 * When we click this Action Element in the GUI populate the properties
	 * panel with the various attributes and setup listeners to catch modifications
	 * that are made.
	 *
	 * @param mainGui  The reference to the outer GUI
	 * @param subGui   The internal frame we're referring to
	 * @param diagram  The diagram we're being select on.
	 **/
	public void onSelect(JAbode mainGui, final JEditorWindow subGui, final JDiagram diagram) {
		// Refresh the diagram and make the right hand side menus come out
		mainGui.popOutProperties();
		diagram.repaint();

		mainGui.setDocumentationField(this);
		
		// Create our new properties panel

		// Add name label
		JLabel namelabel;
		if(getIsSense()){
			namelabel = new JLabel("Sense Name");
		}
		else{
			namelabel = new JLabel("Action Name");
		}

		int vNamefieldSize = 15;
		final JTextField namefield = new JTextField(getElementName(), vNamefieldSize);

		// Action listener to update the actual data when the field is updated
		namefield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setElementName(namefield.getText());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});

		JPanel namePanel = new JPanel();
		
		namePanel.add(namelabel);
		namePanel.add(namefield);
	
		JPanel panel = new JPanel();
		
		JLabel typeLabel;
		if(getIsSense()){
			typeLabel = new JLabel(" - Sense - ");
		}
		else{
			typeLabel = new JLabel(" - Action - ");
		}
		// Add each panel
		// Seperate panels are used to keep labels adjacent to text fields
		panel.add(typeLabel);
		panel.add(namePanel);

		mainGui.setPropertiesPanel(panel);
	}

	/**
	 * Produce and show a context menu for this object
	 * 
	 * @param showOn    The tree node invoking us
	 * @param lap       The file we're a part of
	 * @param window    The window we're being dispalyed in
	 * @param diagram   The diagram in the window we'return being shown on
	 **/
	public void showContextMenu(final JTreeNode showOn, final LearnableActionPattern lap, final JEditorWindow window, final JDiagram diagram) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("Rearrange Elements"));
		menu.addSeparator();

		final ArrayList group = showOn.getGroup();
		if (group.size() > 1) {
			if (group.indexOf(showOn.getValue()) > 0) {
				JMenuItem moveLeft = new JMenuItem("Move Left");
				moveLeft.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						group.add(group.indexOf(showOn.getValue()) - 1, group.remove(group.indexOf(showOn.getValue())));
						window.updateDiagrams(diagram, showOn.getValue());
					}
				});
				menu.add(moveLeft);
			}

			if (group.indexOf(showOn.getValue()) < (group.size() - 1)) {
				JMenuItem moveRight = new JMenuItem("Move Right");
				moveRight.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						group.add(group.indexOf(showOn.getValue()) + 1, group.remove(group.indexOf(showOn.getValue())));
						window.updateDiagrams(diagram, showOn.getValue());
					}
				});
				menu.add(moveRight);
			}
		}

		JMenuItem deleteElement = new JMenuItem("Delete Action/Sense Element");
		deleteElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(diagram, "Are you sure you want to delete this element?") == JOptionPane.YES_OPTION) {
					showOn.getGroup().remove(getSelf());
					window.updateDiagrams(diagram, showOn.getParentNode().getValue());
				}
			}
		});
		
		JMenuItem disableThis = null;
		if (this.isEnabled()) {
			disableThis = new JMenuItem("Disable element");
		} else {
			disableThis = new JMenuItem("Enable element");
		}
		disableThis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (isEnabled()) {
					setEnabled(false);
				} else {
					setEnabled(true);
				}
				window.updateDiagrams(diagram, null);
			}
		});
		
		menu.add(disableThis);
		menu.addSeparator();
		menu.add(deleteElement);

		menu.show(showOn, 0, 0);
	}

	/**
	 * Convert an arraylist of actionelements into a tree representation.
	 * 
	 * @param text Text to show on this list's header node
	 * @param subText Text to show as the subtitle of lists header node
	 * @param triggerList List of elements comprising this trigger/ap 
	 * @param start The point of the tree we'return attatching ourselves to.
	 * @param bindTo The editable widget we're linked to
	 * @return A constructed subtree showing the specified arraylist of action elements in some desirable form
	 **/
	public static JTreeNode actionListToTree(String text, String subText, ArrayList triggerList, JTreeNode start, IEditableElement bindTo, boolean enabled) {
		Color colorToDraw;
		if (enabled) {
			colorToDraw = Configuration.getRGB("colours/actionElement");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		} 
		JTreeNode first = new JTreeNode(text, subText, colorToDraw, bindTo, start);
		JTreeNode prev = first;
		Iterator apIterator = triggerList.iterator();
		while (apIterator.hasNext()) {
			ActionElement ae = (ActionElement) apIterator.next();
			//TODO: Support lessthan/greaterthan equal, not equal as well
			//      Convert from textual display to symbolic
			prev = new JTreeNode(ae.getElementName(), (ae.getIsSense() ? "Sense" : "Action/Event") + (ae.getPredicate() == null ? " " : ":- If " + ae.getPredicate().replaceAll("<", "less than ").replaceAll(">", "greater than ").replaceAll("=", "equal-to ") + " ")
					+ (ae.getValue() == null ? "" : ae.getValue()), colorToDraw, ae, prev);
			prev.setOrganiser(new HorizontalListOrganiser());
			prev.setGroup(triggerList);
		}
		return first;
	}

	/**
	 * Convert this element into a tree branch
	 * 
	 * @param root The root of this sub-tree
	 * @param lap The learnable action pattern file we're part of.
	 * @param detailed Is the diagram detailed (i.e. decorative nodes)
	 * @param expanded Is this diagram expanded (i.e. show sub-tree chained elements)
	 * @return A constructed sub-tree showing this element and any sub-elements that need to be demonstrated.
	 **/
	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap, boolean detailed, boolean expanded) {
		Color colorToDraw;
		if (this.isEnabled()) {
			colorToDraw = Configuration.getRGB("colours/actionElement");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		}
		return new JTreeNode(getElementName(), (getIsSense() ? "Sense" : "Action/Event") + (getPredicate() == null ? " " : ":- If " + getPredicate().replaceAll("<", "less than ").replaceAll(">", "greater than ").replaceAll("=", "equal-to ") + " ") + (getValue() == null ? "" : getValue()),
				colorToDraw, this, root);
	}
}

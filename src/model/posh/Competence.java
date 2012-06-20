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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import model.IEditableElement;
import model.INamedElement;
import model.TimeUnit;
import abode.Configuration;
import abode.visual.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;

/**
 * A competence is a named list of lists of competence elements with a goal and an interval
 * 
 * @author  CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class Competence implements IEditableElement, INamedElement {
	// name of this competence
	private String strName = null;

	// Our list of element lists that comprise the competence
	private ArrayList alElementLists = null;

	// The goal is an arraylist of ActionElements
	private ArrayList alGoal = null;

	// Timeout (Ymir like, Ymir want breadsticks, me likey breadsticks!)
	private TimeUnit tTimeout = null;

	/**
	 * @version = 1.3
	 * Variable to track whether or not to comment this out in the lisp file
	 */
	private boolean enabled = true;

	//docs
	private String documentation;

	/**
	 * Create an empty competence with only a name and a time
	 * unit, and put some default values for our goal and element lists
	 *
	 * @param name Name of this competence
	 * @param timeUnit Timeout
	 **/
	public Competence(String name, TimeUnit timeUnit) {
		alElementLists = new ArrayList();
		alGoal = new ArrayList();
		strName = name;
		tTimeout = timeUnit;
	}

	/**
	 * Create this competence with a name, ymir-like timeout and a goal (Arraylist of actionelements)
	 * as well as an arraylist of arraylists of CompetenceElements comprising the competence itself.
	 *
	 * @param name Name of the competence
	 * @param timeUnit Timeout
	 * @param goal Goal to try and obtain
	 * @param elementLists Lists of lists of competence elements that comprise the actions etc.
	 **/
	public Competence(String name, TimeUnit timeUnit, ArrayList goal, ArrayList elementLists) {
		this(name, timeUnit);
		alGoal = goal;
		alElementLists = elementLists;
	}

	public Competence(String name, TimeUnit timeUnit, ArrayList goal, ArrayList elementLists, boolean shouldBeEnabled) {
		this(name, timeUnit, goal, elementLists);
		this.setEnabled(shouldBeEnabled);
	}
	
	public void setDocumentation(String newDocumentation) {
		this.documentation = newDocumentation;
	}
	
	public String getElementDocumentation() {
		return this.documentation;
	}

	/**
	 * Return whether or not this element is enabled
	 * @return Boolean, true if enabled, false otherwise
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Sets whether this element is enabled
	 */
	public void setEnabled(boolean newValue) {
		this.enabled = newValue;
		Iterator childrenIterator = this.getElementLists().iterator();
		while(childrenIterator.hasNext()) {
			Iterator thisChild = ((ArrayList)childrenIterator.next()).iterator();
			while (thisChild.hasNext()) {
				((IEditableElement)thisChild.next()).setEnabled(newValue);
			}
		}
	}

	/**
	 * Get our list of element lists
	 *
	 * @return Arraylist of arraylists of competence element objects.
	 **/
	public ArrayList getElementLists() {
		return alElementLists;
	}

	/**
	 * Get the goal list
	 *
	 * @return Arraylist of actionelements that comprise the goal
	 **/
	public ArrayList getGoal() {
		return alGoal;
	}

	/**
	 * Get the name of this element
	 * 
	 * @return Name of this competence
	 **/
	public String getName() {
		return strName;
	}

	/**
	 * Get the timeout of this competence
	 *
	 * @return Timeout of this competence
	 **/
	public TimeUnit getTimeout() {
		return tTimeout;
	}

	/**
	 * Set our arraylist of element lists
	 *
	 * @param lists List of lists of competence elements
	 **/
	public void setElementLists(ArrayList lists) {
		alElementLists = lists;
	}

	/**
	 * Set the goal
	 *
	 * @param goal Arraylist of actionelements that comprises the goal
	 **/
	public void setGoal(ArrayList goal) {
		alGoal = goal;
	}

	/**
	 * Set the name of this element
	 *
	 * @param name Name of the object
	 **/
	public void setName(String name) {
		strName = name;
	}

	/**
	 * Set the timeout of this competence.
	 *
	 * @param timeout Timeout value
	 **/
	public void setTimeout(TimeUnit timeout) {
		tTimeout = timeout;
	}

	/**
	 * When we click this Competence in the GUI populate the properties
	 * panel with the various attributes and setup listeners to catch modifications
	 * that are made.
	 *
	 * @param mainGui Reference to the MDI
	 * @param subGui Reference to the editor window
	 * @param diagram Diagram refernece on our editing window
	 **/
	public void onSelect(JAbode mainGui, final JEditorWindow subGui, final JDiagram diagram) {
		mainGui.popOutProperties();
		diagram.repaint();
		
		mainGui.setDocumentationField(this);
		
		// Add name label
		JLabel namelabel = new JLabel("Name");

		int vTextFieldSize = 15;
		final JTextField namefield = new JTextField(getName(), vTextFieldSize);

		// Action listener to update the actual data when the field is updated
		namefield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setName(namefield.getText());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});

		JPanel namePanel = new JPanel();
		
		namePanel.add(namelabel);
		namePanel.add(namefield);

		// Add name label for the timeout of the competence
		JLabel timeoutLabel = new JLabel("Timeout Length");

		// Setup spinner
		double startingValue = 1;
		
		if(getTimeout() != null){
			startingValue = getTimeout().getUnitValue();
		}
		final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(startingValue,
				0, 9999, 1);
		
		// Action listener to update the actual data when the field is updated
		spinnerModel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double value = (Double) spinnerModel.getValue();
				if (Double.toString(value).length() < 1) {
					setTimeout(null);
				} else {
					String strTimeUnit;
					// If no previous time unit set, use seconds
					if (getTimeout() == null){
						strTimeUnit = "seconds";
					}
					else{
						// Get the previous unit of time
						strTimeUnit = getTimeout().getUnitName();
					}
					//Set the new frequency
					setTimeout(new TimeUnit(strTimeUnit, value));
				}
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
		JSpinner timeoutSpinner = new JSpinner(spinnerModel);

		JPanel timeoutPanel = new JPanel();
		
		timeoutPanel.add(timeoutLabel);
		timeoutPanel.add(timeoutSpinner);
		
		// Add name label for the frequency of a drive
		String[] unitStrings = {"seconds","minutes","hours"};
		
		final JComboBox timeoutUnit = new JComboBox(unitStrings);
		
		String strCurrentTimeoutUnit;
		if(getTimeout() != null){
			strCurrentTimeoutUnit = getTimeout().getUnitName();
		}
		else{
			strCurrentTimeoutUnit = "seconds";
		}
		
		if(strCurrentTimeoutUnit.toLowerCase().equals("seconds")){
			timeoutUnit.setSelectedIndex(0);
		}
		else if(strCurrentTimeoutUnit.toLowerCase().equals("minutes")){
			timeoutUnit.setSelectedIndex(1);
		}
		else if(strCurrentTimeoutUnit.toLowerCase().equals("hours")){
			timeoutUnit.setSelectedIndex(2);
		}
		
		// Action listener to update the actual data when the field is updated
		timeoutUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Get the actual value
				double value;
				if(getTimeout() == null){
					value = 1;
				}
				else{
					value = getTimeout().getUnitValue();
				}
				setTimeout(new TimeUnit((String)timeoutUnit.getSelectedItem(), value));
			}
		});
		
		timeoutPanel.add(timeoutUnit);
		
		// Checkbox for enabling and disabling the Competence
		final JCheckBox enabled = new JCheckBox("Enabled?", isEnabled());

		// Action listener for enabling / disabling the Competence
		enabled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setEnabled(enabled.isSelected());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
		JPanel panel = new JPanel();
		
		JLabel typeLabel = new JLabel(" - Competence - ");
		// Add each panel
		// Seperate panels are used to keep labels adjacent to text fields
		panel.add(typeLabel);
		panel.add(namePanel);
		panel.add(timeoutPanel);
		panel.add(enabled);
		
		mainGui.setPropertiesPanel(panel);
	}

	/**
	 * Get a reference to this object. This is an evil hack brought on by the 
	 * use of the anonymous innner classes, which need a nice clean reference
	 * to "this" from time to time.
	 * 
	 * @return This object, again.
	 **/
	public IEditableElement getSelf() {
		return this;
	}

	private static int compElement = 1;

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
		menu.add(new JMenuItem("Competence"));
		menu.addSeparator();

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

		JMenuItem addNew = new JMenuItem("Add new Competence Element");
		addNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				String name = "Some Name";
				while (name.indexOf(" ") >= 0) {
					name = JOptionPane.showInputDialog(showOn.getParent(), "What would you like to name this new competence element?", "CompetenceElement" + compElement++).trim();
					if (name.indexOf(" ") >= 0)
						JOptionPane.showMessageDialog(showOn.getParent(), "Competence Element Names can not contain spaces!");
				}

				CompetenceElement element = new CompetenceElement(name, new ArrayList(), "act_" + name);
				ArrayList elementList = new ArrayList();
				elementList.add(element);

				getElementLists().add(elementList);
				window.updateDiagrams(diagram, element);
			}
		});

		JMenuItem addGoal = new JMenuItem("Add Goal Sense");
		addGoal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				ActionElement actionElement = new ActionElement(true, "SomeSense" + compElement++);
				getGoal().add(actionElement);
				window.updateDiagrams(diagram, actionElement);
			}
		});

		JMenuItem delete = new JMenuItem("Delete Competence");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (JOptionPane.showConfirmDialog(showOn.getParent(), "Delete Competence", "Are you sure you want to delete the competence?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					lap.getElements().remove(showOn.getValue());

					window.updateDiagrams(diagram, null);
				}

			}
		});

		menu.add(disableThis);
		menu.addSeparator();
		menu.add(addNew);
		menu.add(addGoal);
		menu.add(delete);

		menu.show(showOn, 0, 0);
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
		Color drawingColor;
		if (isEnabled()) {
			drawingColor = Configuration.getRGB("colours/competence");
		} else {
			drawingColor = Color.LIGHT_GRAY;
		}

		JTreeNode compNode = new JTreeNode(getName(), "Competence", drawingColor, this, root);
		compNode.setGroup(getElementLists());

		if (detailed)
			ActionElement.actionListToTree("Goal", "Goal of competence", getGoal(), compNode, this, this.isEnabled());

		Iterator outer = getElementLists().iterator();
		while (outer.hasNext()) {
			ArrayList group = (ArrayList) outer.next();
			Iterator inner = group.iterator();
			while (inner.hasNext()) {
				CompetenceElement comp = (CompetenceElement) inner.next();
				JTreeNode result = comp.buildTree(compNode, lap, detailed, expanded);
				result.setGroup(group);
			}
		}

		return compNode;
	}

}

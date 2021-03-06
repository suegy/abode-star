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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;

import model.IEditableElement;
import abode.Configuration;
import abode.JAbode;
import abode.control.AbodeActionHandler;
import abode.editing.posh.ActionElementEdit;
import abode.visual.HorizontalListOrganiser;
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
public class ActionElement implements IEditableElement, Cloneable {
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
	
	private JEditorWindow _subGui = null;
	private JDiagram _diagram = null;

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
	
	@Override
	public void setDocumentation(String newDocumentation) {
		this.documentation = newDocumentation;
	}
	
	@Override
	public String getElementDocumentation() {
		return this.documentation;
	}
	
	/** Clone interface implementation,
	 * used for duplicating ActionElements.
	 */
	public ActionElement clone() throws CloneNotSupportedException {

		ActionElement clone=(ActionElement)super.clone();
		
		clone.setIsSense(this.getIsSense());
		clone.setElementName(this.getElementName());
		
		clone.strValue = this.strValue;
		clone.strComparator = this.strComparator;
		clone.documentation = this.documentation;
		clone.enabled = this.enabled;
		
	    return clone;

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
	@Override
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
	
	public void refresh(){
		_subGui.repaint();
		_subGui.updateDiagrams(_diagram, getSelf());
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
	@Override
	public void onSelect(JAbode mainGui, final JEditorWindow subGui, final JDiagram diagram) {
		// Refresh the diagram and make the right hand side menus come out
		mainGui.popOutProperties();
		diagram.repaint();

		_subGui=subGui;
		_diagram=diagram;
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

		
		// Action listener to update the actual data when the field is updated
		ArrayList <String> existing_values = new ArrayList<String>();
		
		if(getIsSense()){
			for(String str : subGui.getListOfSenses()){
				existing_values.add(str);
			}
		}
		else{
			for(String str : subGui.getListOfActions()){
				existing_values.add(str);
			}
		}
		
		final JComboBox namefield = new JComboBox(existing_values.toArray());
		
		// Set the combo box to be the currently selected value if possible
		for(int i = 0; i < namefield.getItemCount(); i++){
			if(getElementName().equals(namefield.getItemAt(i))){
				namefield.setSelectedIndex(i);
				break;
			}
		}
		namefield.setEditable(true);
		
		namefield.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), 
						new ActionElementEdit(getSelf(), getSelf().bIsSense, namefield.getSelectedItem().toString(), getSelf().strValue, getSelf().strComparator, getSelf().enabled, getSelf().documentation)));
				setElementName(namefield.getSelectedItem().toString());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
//		TODO: Have changed this to a combo box, does this need to be in here?
//		namefield.getDocument().addUndoableEditListener(AbodeUndoManager.getUndoManager());

		JPanel namePanel = new JPanel();
		
		namePanel.add(namelabel);
		namePanel.add(namefield);
		
		JPanel valuePanel = null;
		
		if(getIsSense()){
			
			// Add name label for the frequency of a drive
			String[] unitStrings = {"=","!=",">",">=","<","<=","=="};
			
			final JComboBox predicateSelector = new JComboBox(unitStrings);
			
			// Value
			// Add name label for the frequency of a drive
			JLabel valueLabel = new JLabel("Value");
	
			// Setup spinner
			double startingValue = 1;
			
			if(getValue() != null){
				startingValue = Double.parseDouble(getValue());
			}
			final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(startingValue,
					0, 999999, 1);
			
			// Action listener to update the actual data when the field is updated
			spinnerModel.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					double value = (Double) spinnerModel.getValue();
					
					
					if (Double.toString(value).length() < 1) {
						_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), 
								new ActionElementEdit(getSelf(), getSelf().bIsSense, getSelf().strElementName, null, null, getSelf().enabled, getSelf().documentation)));

						setValue(null);
						setPredicate(null);
					} else {
						_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), 
								new ActionElementEdit(getSelf(), getSelf().bIsSense, getSelf().strElementName, Double.toString(value), (String)predicateSelector.getSelectedItem(), getSelf().enabled, getSelf().documentation)));
						setValue(Double.toString(value));
						setPredicate((String)predicateSelector.getSelectedItem());
					}
					subGui.repaint();
					subGui.updateDiagrams(diagram, getSelf());
				}
			});
			
			JSpinner valueSpinner = new JSpinner(spinnerModel);
			
			String strCurrentPredicate;
			if(getPredicate() != null){
				strCurrentPredicate = getPredicate();
			}
			else{
				strCurrentPredicate = "=";
			}
			
			if(strCurrentPredicate.toLowerCase().equals("=")){
				predicateSelector.setSelectedIndex(0);
			}
			else if(strCurrentPredicate.toLowerCase().equals("!=")){
				predicateSelector.setSelectedIndex(1);
			}
			else if(strCurrentPredicate.toLowerCase().equals(">")){
				predicateSelector.setSelectedIndex(2);
			}
			else if(strCurrentPredicate.toLowerCase().equals(">=")){
				predicateSelector.setSelectedIndex(3);
			}
			else if(strCurrentPredicate.toLowerCase().equals("<")){
				predicateSelector.setSelectedIndex(4);
			}
			else if(strCurrentPredicate.toLowerCase().equals("<=")){
				predicateSelector.setSelectedIndex(5);
			}
			else if(strCurrentPredicate.toLowerCase().equals("==")){
				predicateSelector.setSelectedIndex(6);
			}
			
			// Action listener to update the actual data when the field is updated
			predicateSelector.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ActionElement a=ActionElement.this;
					_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), 
							new ActionElementEdit(getSelf(), getSelf().bIsSense, getSelf().strElementName, getSelf().strValue, (String)predicateSelector.getSelectedItem(), getSelf().enabled, getSelf().documentation)));
					// Get the actual value
					setPredicate((String)predicateSelector.getSelectedItem());
					setValue(Double.toString((Double)spinnerModel.getValue()));
					subGui.repaint();
					subGui.updateDiagrams(diagram, getSelf());
				}
			});
			
			valuePanel = new JPanel();
			
			valuePanel.add(valueLabel);
			valuePanel.add(predicateSelector);
			valuePanel.add(valueSpinner);	
		}
	
		JPanel panel = new JPanel();
		// Set the panel layout
		panel.setLayout(new java.awt.GridLayout(0, 1));

		JLabel typeLabel;
		if(getIsSense()){
			typeLabel = new JLabel("Sense Properties (" + getElementName() + ")");
		}
		else{
			typeLabel = new JLabel("Action Properties (" + getElementName() + ")");
		}
		typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		typeLabel.setFont(new Font(typeLabel.getFont().getName(),Font.BOLD,typeLabel.getFont().getSize() + 1));
		// Add each panel
		// Seperate panels are used to keep labels adjacent to text fields
		panel.add(typeLabel);
		panel.add(namePanel);
		
		if(getIsSense()){
			panel.add(valuePanel);
		}

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
	@Override
	public void showContextMenu(final JTreeNode showOn, final LearnableActionPattern lap, final JEditorWindow window, final JDiagram diagram) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("Rearrange Elements"));
		menu.addSeparator();

		final ArrayList group = showOn.getGroup();
		if (group.size() > 1) {
			if (group.indexOf(showOn.getValue()) > 0) {
				JMenuItem moveLeft = new JMenuItem("Move Left");
				moveLeft.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AbodeActionHandler.getActionHandler().moveUpInGroupAction(diagram, window, showOn);
					}
				});
				menu.add(moveLeft);
			}

			if (group.indexOf(showOn.getValue()) < (group.size() - 1)) {
				JMenuItem moveRight = new JMenuItem("Move Right");
				moveRight.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AbodeActionHandler.getActionHandler().moveDownInGroupAction(diagram, window, showOn);						
					}
				});
				menu.add(moveRight);
			}
		}

		JMenuItem deleteElement = new JMenuItem("Delete Action/Sense Element");
		deleteElement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbodeActionHandler.getActionHandler().deleteElementAction(diagram, window, showOn);
			}
		});
		
		JMenuItem refactorElements = new JMenuItem("Rename all \"" + getElementName() + "\" elements");
		refactorElements.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String strName = " ";
				String strOldName = getElementName();
				
				// Prompt for new name
				while (strName.indexOf(" ") >= 0) {
					strName = JOptionPane.showInputDialog(showOn.getParent(), "What would you like to rename" +
							" all of the elements with the name " + getElementName() + " to?" , strOldName);
					if (strName.indexOf(" ") >= 0)
						JOptionPane.showMessageDialog(showOn.getParent(), "Element name cannot contain spaces!");
					else if(strName.length() < 1){
						JOptionPane.showMessageDialog(showOn.getParent(), "Element name cannot be empty!");
					}
				}
				
				// For actions
				if(!getIsSense()){
					// Rename all of the elements with the old name to the new name
					Iterator it = lap.getElements().iterator();
					while (it.hasNext()) {
						IEditableElement element = (IEditableElement) it.next();
						if (element instanceof DriveCollection) {
							DriveCollection collection = (DriveCollection) element;
							Iterator driveElementLists = collection.getDriveElements().iterator();
							while (driveElementLists.hasNext()) {
								Iterator driveElements = ((ArrayList) driveElementLists.next()).iterator();
								while (driveElements.hasNext()) {
									DriveElement driveElement = (DriveElement) driveElements.next();
									if(driveElement.getAction().equals(strOldName)){
										driveElement.setAction(strName);
									}
								}
							}
						} else if (element instanceof Competence) {
							Competence competence = (Competence) element;
							Iterator competenceLists = competence.getElementLists().iterator();
							while (competenceLists.hasNext()) {
								Iterator competences = ((ArrayList) competenceLists.next()).iterator();
								while (competences.hasNext()) {
									CompetenceElement compElement = (CompetenceElement) competences.next();
									if(compElement.getAction().equals(strOldName)){
										compElement.setAction(strName);
									}
								}
							}
						} else if (element instanceof ActionPattern) {
							ActionPattern ap = (ActionPattern) element;
	
							Iterator elements = ap.getElements().iterator();
							while (elements.hasNext()) {
								ActionElement actionElement = (ActionElement) elements.next();
								if(actionElement.getElementName().equals(strOldName)){
									actionElement.setElementName(strName);
								}
							}
						}
					}
				}
				// Sense replace
				else{
					Iterator it = lap.getElements().iterator();
					while (it.hasNext()) {
						IEditableElement element = (IEditableElement) it.next();
						if (element instanceof DriveCollection) {
							DriveCollection collection = (DriveCollection) element;
							
							Iterator goalList = collection.getGoal().iterator();
							while(goalList.hasNext()){
								ActionElement goal = (ActionElement)goalList.next();
								if(goal.getElementName().equals(strOldName)){
									goal.setElementName(strName);
								}
							}
							
							Iterator driveElementLists = collection.getDriveElements().iterator();
							while (driveElementLists.hasNext()) {
								Iterator driveElements = ((ArrayList) driveElementLists.next()).iterator();
								while (driveElements.hasNext()) {
									DriveElement driveElement = (DriveElement) driveElements.next();
									Iterator triggerElements = driveElement.getTrigger().iterator();
									while (triggerElements.hasNext()) {
										ActionElement actionElement = (ActionElement) triggerElements.next();
										if(actionElement.getElementName().equals(strOldName)){
											actionElement.setElementName(strName);
										}
									}
								}
							}
						} else if (element instanceof Competence) {
							Competence competence = (Competence) element;
							// Get all of the goal senses as well
							Iterator goalList = competence.getGoal().iterator();
							while(goalList.hasNext()){
								ActionElement goal = (ActionElement)goalList.next();
								if(goal.getElementName().equals(strOldName)){
									goal.setElementName(strName);
								}
							}
							
							Iterator competenceLists = competence.getElementLists().iterator();
							while (competenceLists.hasNext()) {
								Iterator competences = ((ArrayList) competenceLists.next()).iterator();
								while (competences.hasNext()) {
									CompetenceElement compElement = (CompetenceElement) competences.next();
									Iterator triggerElements = compElement.getTrigger().iterator();
									while (triggerElements.hasNext()) {
										ActionElement actionElement = (ActionElement) triggerElements.next();
										if(actionElement.getElementName().equals(strOldName)){
											actionElement.setElementName(strName);
										}
									}
								}
							}
						}
					}
				}
				window.updateDiagrams(diagram, null);
			}
		});
		
		JMenuItem disableThis = null;
		if (this.isEnabled()) {
			disableThis = new JMenuItem("Disable element");
		} else {
			disableThis = new JMenuItem("Enable element");
		}
		disableThis.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (isEnabled()) {
					setEnabled(false);
				} else {
					setEnabled(true);
				}
				window.updateDiagrams(diagram, null);
			}
		});
		
		JMenuItem duplicateElement = new JMenuItem("Duplicate element");
		duplicateElement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbodeActionHandler.getActionHandler().duplicateElement(diagram,window,showOn);
			}
		});
		
		/* TODO: This has been disabled because this functionality doesn't actually work */
//		menu.add(disableThis);
		menu.addSeparator();
		menu.add(deleteElement);
		menu.addSeparator();
		menu.add(refactorElements);
		menu.add(duplicateElement);

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
			prev = new JTreeNode(ae.getElementName(), "Sense" +  (ae.getPredicate() == null ? " " : " (If " + ae.getPredicate() + " ")
					+ (ae.getValue() == null ? "" : ae.getValue() + ")"), colorToDraw, ae, prev);
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
	@Override
	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap, boolean detailed, boolean expanded) {
		Color colorToDraw;
		if (this.isEnabled()) {
			colorToDraw = Configuration.getRGB("colours/actionElement");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		}
		return new JTreeNode(getElementName(), (getIsSense() ? "Sense" : "Action/Event") + 
				(getPredicate() == null ? " " : " (If " + getPredicate() + " ") 
				+ (getValue() == null ? "" : getValue()),
				colorToDraw, this, root);
	}
}

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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.IEditableElement;
import model.INamedElement;
import model.TimeUnit;
import abode.Configuration;
import abode.visual.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;
import abode.visual.VerticalListOrganiser;

/**
 * A Drive Element is a name, a trigger list of action elements and a named
 * action, and an optional timeout for scheduling frequency
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class DriveElement implements IEditableElement {
	// Our name
	private String strName = null;

	// Our list of ActionElements that comprises the trigger
	private ArrayList alTrigger = null;

	// The action to invoke
	private String strAction = null;

	// Frequency for scheduling
	private TimeUnit tFrequency = null;
	
	// Element enabled
	private boolean enabled = true;
	
//	Docs
	private String documentation;
	
	private LearnableActionPattern parentPattern = null;
	
	/**
	 * Create this drive element with a name, trigger (list of actionelements)
	 * and a corresponding action name to invoke.
	 * 
	 * @param name
	 *            Name of the drive element
	 * @param trigger
	 *            Arraylist of action elements comprising our trigger
	 * @param action
	 *            POSH Root to trigger
	 */
	public DriveElement(String name, ArrayList trigger, String action) {
		strName = name;
		alTrigger = trigger;
		strAction = action;
	}

	/**
	 * Create this drive element, with a name, trigger (list of action elements)
	 * and a corresponding name of action to invoke, as well as a scheduling
	 * frequency.
	 * 
	 * @param name
	 *            Name of the drive element
	 * @param trigger
	 *            Arraylist of action elements comprising our trigger
	 * @param action
	 *            Posh root to trigger
	 * @param freq
	 *            Frequency specification for this drive element
	 */
	public DriveElement(String name, ArrayList trigger, String action, TimeUnit freq) {
		this(name, trigger, action);
		tFrequency = freq;
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
		//Disable children
		Iterator childrenIterator = this.getTrigger().iterator();
		while (childrenIterator.hasNext()) {
			IEditableElement nextElement = (IEditableElement)childrenIterator.next();
			nextElement.setEnabled(false);			
		}
		
		//Need to get hold of the element which is the action by its name rather
		// than an object reference to it. This might be good to change at some point
		// sooner rather than later.
		//((IEditableElement)parentPattern.getElementNamed(this.getAction())).setEnabled(value);
	}
	/**
	 * Get the name of the action we invoke
	 * 
	 * @return POSH Action Name
	 */
	public String getAction() {
		return strAction;
	}

	/**
	 * Get the scheduling frequency of this drive element
	 * 
	 * @return Frequency time unit
	 */
	
	public TimeUnit getFrequency() {
		return tFrequency;
	}

	public void setParentPattern(LearnableActionPattern parentPattern) {
		this.parentPattern = parentPattern;
	}
	
	/**
	 * Get the name of this drive element
	 * 
	 * @return Name of the drive element
	 */
	public String getName() {
		return strName;
	}

	/**
	 * Get our trigger list
	 * 
	 * @return Trigger in form of arraylist
	 */
	public ArrayList getTrigger() {
		return alTrigger;
	}

	/**
	 * Set our action name to invoke
	 * 
	 * @param act
	 *            Action name.
	 */
	public void setAction(String act) {
		strAction = act;
	}

	/**
	 * Set our scheduling frequency
	 * 
	 * @param time
	 *            New frequency
	 */
	public void setFrequency(TimeUnit time) {
		tFrequency = time;
	}

	/**
	 * Set the name of this drive element
	 */
	public void setName(String name) {
		strName = name;
	}

	/**
	 * Set our trigger (Should be arraylist of actionelement objects)
	 * 
	 * @param trigger
	 *            Arraylist of action elements comprising the trigger
	 */
	public void setTrigger(ArrayList trigger) {
		alTrigger = trigger;
	}

	/**
	 * When we click this Action Element in the GUI populate the properties
	 * panel with the various attributes and setup listeners to catch
	 * modifications that are made.
	 * 
	 * @param mainGui
	 *            The reference to the outer GUI
	 * @param subGui
	 *            The internal frame we're referring to
	 * @param diagram
	 *            The diagram we're being select on.
	 */
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
		
		// Add name label for action
		JLabel actionlabel = new JLabel("Action");

		final JTextField actionfield = new JTextField(getAction(), vTextFieldSize);

		// Action listener to update the actual data when the field is updated
		actionfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction(actionfield.getText());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
		JPanel actionPanel = new JPanel();

		actionPanel.add(actionlabel);
		actionPanel.add(actionfield);

		// Add name label for the frequency of a drive
		JLabel frequencyValueLabel = new JLabel("Frequency");

		// Setup spinner
		double startingValue = 1;
		
		if(getFrequency() != null){
			startingValue = getFrequency().getUnitValue();
		}
		final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(startingValue,
				0, 9999, 1);
		
		// Action listener to update the actual data when the field is updated
		spinnerModel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double value = (Double) spinnerModel.getValue();
				if (Double.toString(value).length() < 1) {
					setFrequency(null);
				} else {
					String strTimeUnit;
					// If no previous time unit set, use seconds
					if (getFrequency() == null){
						strTimeUnit = "seconds";
					}
					else{
						// Get the previous unit of time
						strTimeUnit = getFrequency().getUnitName();
					}
					//Set the new frequency
					setFrequency(new TimeUnit(strTimeUnit, value));
				}
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
		JSpinner frequencyValueSpinner = new JSpinner(spinnerModel);

		JPanel frequencyValuePanel = new JPanel();
		
		frequencyValuePanel.add(frequencyValueLabel);
		frequencyValuePanel.add(frequencyValueSpinner);
		
		// Add name label for the frequency of a drive
		String[] unitStrings = {"seconds","minutes","hours"};
		
		final JComboBox frequencyUnit = new JComboBox(unitStrings);
		
		String strCurrentFrequencyUnit;
		if(getFrequency() != null){
			strCurrentFrequencyUnit = getFrequency().getUnitName();
		}
		else{
			strCurrentFrequencyUnit = "seconds";
		}
		
		if(strCurrentFrequencyUnit.toLowerCase().equals("seconds")){
			frequencyUnit.setSelectedIndex(0);
		}
		else if(strCurrentFrequencyUnit.toLowerCase().equals("minutes")){
			frequencyUnit.setSelectedIndex(1);
		}
		else if(strCurrentFrequencyUnit.toLowerCase().equals("hours")){
			frequencyUnit.setSelectedIndex(2);
		}
		
		// Action listener to update the actual data when the field is updated
		frequencyUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Get the actual value
				double value;
				if(getFrequency() == null){
					value = 1;
				}
				else{
					value = getFrequency().getUnitValue();
				}
				setFrequency(new TimeUnit((String)frequencyUnit.getSelectedItem(), value));
			}
		});
		
		frequencyValuePanel.add(frequencyUnit);
		
		JPanel panel = new JPanel();
		
		JLabel typeLabel = new JLabel(" - Drive Element - ");
		// Add each panel
		// Seperate panels are used to keep labels adjacent to text fields
		panel.add(typeLabel);
		panel.add(namePanel);
		panel.add(actionPanel);
		panel.add(frequencyValuePanel);
		
		mainGui.setPropertiesPanel(panel);
	}

	/**
	 * Get a reference back to the drive element itself, since sometimes inner
	 * classes have some truly horrible syntax for referring back to the parent
	 * class.
	 * 
	 * @return this
	 */
	public DriveElement getSelf() {
		return this;
	}

	// Number of new senses we've created
	private int senseCount = 1;

	/**
	 * Produce and show a context menu for this object
	 * 
	 * @param showOn
	 *            The tree node invoking us
	 * @param lap
	 *            The file we're a part of
	 * @param window
	 *            The window we're being dispalyed in
	 * @param diagram
	 *            The diagram in the window we'return being shown on
	 */
	public void showContextMenu(final JTreeNode showOn, LearnableActionPattern lap, final JEditorWindow window, final JDiagram diagram) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("Drive Element"));
		menu.addSeparator();
		
		JMenu trigger = new JMenu("Set Triggered Action...");
		final ArrayList elements = lap.getElements();

		JMenuItem itemAddAP = new JMenuItem("New Action Pattern...");
		itemAddAP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String name = JOptionPane.showInputDialog(window, "Please enter a name for this new action pattern.", "");
				if ((name != null) && (name.length() > 0)) {
					ActionPattern ap = new ActionPattern(name, new TimeUnit("Minutes", 1), new ArrayList());
					elements.add(ap);
					setAction(ap.getName());
					window.updateDiagrams(diagram, showOn.getValue());
				}
			}
		});
		trigger.add(itemAddAP);

		JMenuItem itemAddComp = new JMenuItem("New Competence...");
		itemAddComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String name = JOptionPane.showInputDialog(window, "Please enter a name for this new competence", "");
				if ((name != null) && (name.length() > 0)) {
					Competence c = new Competence(name, new TimeUnit("Minutes", 1), new ArrayList(), new ArrayList());
					elements.add(c);
					setAction(c.getName());
					window.updateDiagrams(diagram, showOn.getValue());
				}
			}
		});
		trigger.add(itemAddComp);
		trigger.addSeparator();

		Iterator it = elements.iterator();
		while (it.hasNext()) {
			IEditableElement current = (IEditableElement) it.next();
			if (current instanceof INamedElement) {
				final INamedElement namedCurrent = (INamedElement) current;
				JMenuItem focus = new JMenuItem(namedCurrent.getName() + ((namedCurrent instanceof ActionPattern) ? " (AP)" : " (C)"));
				focus.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						getSelf().setAction(namedCurrent.getName());
						window.updateDiagrams(diagram, showOn.getValue());
					}
				});

				trigger.add(focus);
			}
		}

		JMenuItem addTrigger = new JMenuItem("Add Trigger Element");
		addTrigger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				ActionElement actionElement = new ActionElement(true, "SomeSense" + senseCount++);
				getTrigger().add(actionElement);
				window.updateDiagrams(diagram, actionElement);
			}
		});

		JMenuItem itemDelete = new JMenuItem("Delete " + getName());
		itemDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((showOn.getGroup() != null) && (showOn.getParentNode().getGroup() != null)) {
					if (JOptionPane.showConfirmDialog(window, "Are you sure you want to delete this Drive Element?", "Delete " + getName() + "?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						showOn.getParentNode().getGroup().remove(showOn.getGroup());
						window.updateDiagrams(diagram, showOn.getParentNode().getValue());
					}
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
		menu.add(trigger);
		menu.add(addTrigger);
		menu.add(itemDelete);
		menu.show(showOn, 0, 0);
	}

	/**
	 * Build the tree structure of the file
	 * 
	 * @root Root node this tree attatches to
	 * @lap File we're mapping to
	 * @detailed Show detailing nodes (i.e. trigger lists)
	 * @expanded Recursive expansion of sub-nodes
	 * 
	 * @return Tree node representing this node and the relevent sub-tree for
	 *         the specified diagram rendering settings
	 */
	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap, boolean detailed, boolean expanded) {
		if (this.parentPattern == null) {
			this.parentPattern = lap;
		}
		
		Color colorToDraw;
		if (this.isEnabled()) {
			colorToDraw = Configuration.getRGB("colours/driveElement");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		}
		JTreeNode chainStart = new JTreeNode(getName(), "Drive-Element", colorToDraw, this, root);

		if (this.isEnabled()) {
			colorToDraw = Configuration.getRGB("colours/triggeredAction");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		}
		JTreeNode actionNode = new JTreeNode(getAction(), "Action to Trigger", colorToDraw, this, chainStart);

		// A detailed tree will show the trigger information for this
		if (detailed)
			ActionElement.actionListToTree("Trigger Elements", "", getTrigger(), chainStart, this, this.isEnabled());
		
		// An expanded tree will show our invoked element fully
		if (expanded)
			lap.scanActionTree(actionNode, getAction(), detailed, expanded);

		chainStart.setOrganiser(new VerticalListOrganiser());
		return chainStart;
	}

}

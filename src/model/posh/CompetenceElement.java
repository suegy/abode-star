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
import java.awt.Font;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import model.IEditableElement;
import model.INamedElement;
import model.TimeUnit;
import abode.Configuration;
import abode.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;
import abode.visual.VerticalListOrganiser;

/**
 *  A Competence Element is is a named trigger and an action, along with
 *  an optional number of retries.
 **/
public class CompetenceElement implements IEditableElement, INamedElement {
	// Name of this competence element
	private String strName = null;

	// Arraylist containing ActionElement triggers
	private ArrayList alTrigger = null;

	// Name of action to invoke
	private String strAction = null;

	// How many times to retry?
	private int iRetries = 0;
	
	// Element enabled?
	private boolean enabled = true;
	
	//Docs
	private String documentation;

	/**
	 * Initialize this Competence Element with a name, a list of triggers, an action
	 * and the optional number of retries IS NOT being specified.
	 *
	 * @param name Name of this competence
	 * @param triggerList List of actionelements comprising the trigger
	 * @param action Action to invoke
	 **/
	public CompetenceElement(String name, ArrayList triggerList, String action) {
		setName(name);
		setTrigger(triggerList);
		setAction(action);
	}

	/**
	 * Initialize this Competence Element with a name, a list of triggers, an action
	 * and the optional number of retries being specified.
	 *
	 * @param name Name of this competence
	 * @param triggerList List of actionelements comprising the trigger
	 * @param action Action to invoke
	 **/
	public CompetenceElement(String name, ArrayList triggerList, String action, int ret) {
		this(name, triggerList, action);
		setRetries(ret);
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
	 * Get the action
	 *
	 * @return Name of our invoked action
	 **/
	public String getAction() {
		return strAction;
	}

	/**
	 * Get the name of this competence element
	 *
	 * @return Name of this competence element
	 **/
	public String getName() {
		return strName;
	}

	/**
	 * Get the number of retries for this element
	 *
	 * @return Number of retries
	 **/
	public int getRetries() {
		return iRetries;
	}

	/**
	 * Get the arraylist of action elements that comprises our trigger.
	 *
	 * @return Arraylist of action eleemtns comprising the trigger
	 **/
	public ArrayList getTrigger() {
		return alTrigger;
	}

	/**
	 * Set the name of the action to invoke
	 *
	 * @param action Name of new action to invoke
	 **/
	public void setAction(String action) {
		strAction = action;
	}

	/**
	 * Set the name of this competence element
	 * 
	 * @param name New name of this competence element
	 **/
	public void setName(String name) {
		strName = name;
	}

	/**
	 * Set the number of retries for this element.
	 *
	 * @param val New number of retries.
	 **/
	public void setRetries(int val) {
		iRetries = val;
	}

	/**
	 * Set our list of triggers
	 **/
	public void setTrigger(ArrayList list) {
		alTrigger = list;
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
		
		// Add name label for action
		JLabel actionlabel = new JLabel("Action");

		// Action listener to update the actual data when the field is updated
		ArrayList <String> existing_values = new ArrayList<String>();
		
		ArrayList elements = subGui.getLearnableActionPattern().getElements();
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			IEditableElement current = (IEditableElement) it.next();
			if (current instanceof INamedElement) {
				INamedElement namedCurrent = (INamedElement) current;
				existing_values.add(namedCurrent.getName());
			}
		}
		
		final JComboBox actionfield = new JComboBox(existing_values.toArray());
		
		// Set the combo box to be the currently selected value if possible
		for(int i = 0; i < actionfield.getItemCount(); i++){
			if(getAction().equals(actionfield.getItemAt(i))){
				actionfield.setSelectedIndex(i);
				break;
			}
		}
		actionfield.setEditable(true);
		
		actionfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction((String)actionfield.getSelectedItem());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
		JPanel actionPanel = new JPanel();

		actionPanel.add(actionlabel);
		actionPanel.add(actionfield);
		
		// Add name label for the number of retries of the competence element
		JLabel retriesLabel = new JLabel("Retries");

		// Setup spinner
		int startingValue = getRetries();
		int minimumValue = 0;
		int maximumValue = 9999;
		
		// TODO: Not sure if this is correct, as some scripts have retry values
		// of -1
		if(startingValue < minimumValue){
			startingValue = minimumValue;
		}
		else if(startingValue > maximumValue){
			startingValue = maximumValue;
		}
		
		final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(startingValue,
				minimumValue, maximumValue, 1);
		
		// Action listener to update the actual data when the field is updated
		spinnerModel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = (Integer) spinnerModel.getValue();
				if (Integer.toString(value).length() < 1) {
					setRetries(0);
				} else {
					//Set the new frequency
					setRetries(value);
				}
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
		JSpinner retriesSpinner = new JSpinner(spinnerModel);
		
		retriesSpinner.setToolTipText("Sets the number of times a competence element will " +
				"be fired before giving up. Set this to 0 for unlimited retries. ");

		JPanel retriesPanel = new JPanel();
		
		retriesPanel.add(retriesLabel);
		retriesPanel.add(retriesSpinner);
		
		JPanel panel = new JPanel();
		
		// Set the panel layout
		panel.setLayout(new java.awt.GridLayout(0, 1));
		JLabel typeLabel = new JLabel("Competence Element Properties (" + getName() + ")");
		typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		typeLabel.setFont(new Font(typeLabel.getFont().getName(),Font.BOLD,typeLabel.getFont().getSize() + 1));
		
		panel.add(typeLabel);
		panel.add(namePanel);
		panel.add(actionPanel);
		panel.add(retriesPanel);
		
		mainGui.setPropertiesPanel(panel);
	}

	public CompetenceElement getSelf() {
		return this;
	}

	private int senseCount = 1;

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
		menu.add(new JMenuItem("Competence Element"));
		menu.addSeparator();

		JMenu trigger = new JMenu("Set Triggered Action...");
		final ArrayList elements = lap.getElements();

		JMenuItem itemAddAP = new JMenuItem("New Action Pattern...");
		itemAddAP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String name = JOptionPane.showInputDialog(window, "Please enter a name for this new action pattern.", "");
				if ((name != null) && (name.length() > 0)) {
					ActionPattern ap = new ActionPattern(name, new TimeUnit("minutes", 1), new ArrayList());
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
					Competence c = new Competence(name, new TimeUnit("minutes", 1), new ArrayList(), new ArrayList());
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

		/* TODO: This has been disabled because this functionality doesn't actually work */
//		menu.add(disableThis);
		menu.addSeparator();
		menu.add(trigger);
		menu.add(addTrigger);
		menu.add(itemDelete);
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
		Color colorToDraw;
		if (this.isEnabled()) {
			colorToDraw = Configuration.getRGB("colours/competenceElement");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		}
		JTreeNode result = new JTreeNode(getName(), "Competence Element", colorToDraw, this, root);
		JTreeNode action = null;
		if (detailed) {
			if (this.isEnabled()) {
				colorToDraw = Configuration.getRGB("colours/triggeredAction");
			} else {
				colorToDraw = Color.LIGHT_GRAY;
			}
			ActionElement.actionListToTree("Trigger Elements", "", getTrigger(), result, this, this.isEnabled());
			action = new JTreeNode(getAction(), "Action to Trigger", colorToDraw, this, result);
		}
		if (expanded) {
			// If we're expanded and we'return also detailed, then chain the action tree from the "Action to trigger node"
			if (action == null)
				action = result;

			lap.scanActionTree(action, getAction(), detailed, expanded);
		}
		result.setOrganiser(new VerticalListOrganiser());
		return result;
	}
}

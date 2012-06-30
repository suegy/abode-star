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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;

import model.IEditableElement;
import model.INamedElement;
import model.TimeUnit;
import abode.Configuration;
import abode.JAbode;
import abode.editing.DeleteEdit;
import abode.editing.posh.ActionPatternEdit;
import abode.visual.HorizontalListOrganiser;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;
import abode.visual.ListOrganiser;

/**
 * An Action Pattern is a name, interval and a sequence of action elements
 * 
 * @author  CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class ActionPattern implements IEditableElement, INamedElement {

	// Array of elements that this action pattern contains
	private ArrayList alElements = null;

	// Time interval/timeout (Ymir-like!)
	private TimeUnit tTimeOut = null;

	// Name of this action pattern
	private String strName = null;

	private boolean enabled = true;
	
	//Docs
	private String documentation;
	
	private JEditorWindow _subGui = null;
	private JDiagram _diagram = null;
	
	/**
	 * Initialize our action pattern with a blank list of elements
	 *
	 * @param name Name of this action Pattern
	 * @param time Time unit for our interval/timeout (Ymir-style)
	 **/
	public ActionPattern(String name, TimeUnit time) {
		strName = name;
		tTimeOut = time;
		alElements = new ArrayList();
	}

	/**
	 * Create an Action Pattern with a pre-loaded list of elements
	 *
	 * @param name Name of this action pattern
	 * @param time Time unit for our interval/timeout (Ymir-style)
	 **/
	public ActionPattern(String name, TimeUnit time, ArrayList children) {
		this(name, time);
		alElements = children;
	}

	public ActionPattern(String name, TimeUnit time, ArrayList children, boolean shouldBeEnabled) {
		this(name, time, children);
		this.setEnabled(shouldBeEnabled);
	}

	@Override
	public void setDocumentation(String newDocumentation) {
		this.documentation = newDocumentation;
	}
	
	@Override
	public String getElementDocumentation() {
		return this.documentation;
	}
	
	/**
	 * Return whether or not this element is enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Set whether or not this element is enabled
	 */
	@Override
	public void setEnabled(boolean newValue) {
		this.enabled = newValue;
		//Disable children of this also
		Iterator childrenIterator = this.getElements().iterator();
		while (childrenIterator.hasNext()) {
			((IEditableElement)childrenIterator.next()).setEnabled(newValue);
		}
	}

	/**
	 * Get the list of elements of this action pattern
	 *
	 * @return List of elements in this action pattern
	 **/
	public ArrayList getElements() {
		return alElements;
	}

	/**
	 * Get the name of this action pattern
	 *
	 * @return Name of the action pattern.
	 **/
	@Override
	public String getName() {
		return strName;
	}

	/**
	 * Get the timeout for this action pattern
	 *
	 * @return Time unit for our ymir like timeout/interval
	 **/
	public TimeUnit getTimeUnit() {
		return tTimeOut;
	}

	/**
	 * Set the list of elements used in this action pattern
	 * 
	 * @param elements New list of elements for this action pattern
	 **/
	public void setElements(ArrayList elements) {
		alElements = elements;
	}

	/**
	 * Set the name of this action pattern
	 *
	 * @param name The new name of this action pattern
	 **/
	@Override
	public void setName(String name) {
		strName = name;
	}

	/**
	 * Set the timeout/interval for this action pattern
	 *
	 * @param unit The new timeout for this action pattern.
	 **/
	public void setTimeUnit(TimeUnit unit) {
		tTimeOut = unit;
	}
	
	public void refresh(){
		_subGui.repaint();
		_subGui.updateDiagrams(_diagram, null);
	}
	
	/**
	 * When we click this Action Pattern in the GUI populate the properties
	 * panel with the various attributes and setup listeners to catch modifications
	 * that are made.
	 *
	 * @param mainGui Reference to the MDI
	 * @param subGui Reference to the editor window
	 * @param diagram Diagram refernece on our editing window
	 **/
	@Override
	public void onSelect(JAbode mainGui, final JEditorWindow subGui, final JDiagram diagram) {
		// Show the right menu and refresh this button to show the new state
		mainGui.popOutProperties();
		diagram.repaint();
		_diagram = diagram;
		_subGui = subGui;

		mainGui.setDocumentationField(this);
		
		// Add name label
		JLabel namelabel = new JLabel("Name");

		int vTextFieldSize = 15;
		final JTextField namefield = new JTextField(getName(), vTextFieldSize);

		// Action listener to update the actual data when the field is updated
		namefield.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new ActionPatternEdit(getSelf(), alElements, tTimeOut, namefield.getText(), enabled, documentation)));
				setName(namefield.getText());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});

		JPanel namePanel = new JPanel();
		
		namePanel.add(namelabel);
		namePanel.add(namefield);

		// Add name label for the timeout
		JLabel timeoutLabel = new JLabel("Timeout Length");

		// Setup spinner
		double startingValue = 1;
		
		if(getTimeUnit() != null){
			startingValue = getTimeUnit().getUnitValue();
		}
		final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(startingValue,
				0, 9999, 1);
		
		// Action listener to update the actual data when the field is updated
		spinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				double value = (Double) spinnerModel.getValue();
				if (Double.toString(value).length() < 1) {
					_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new ActionPatternEdit(getSelf(), alElements, null, strName, enabled, documentation)));
					
					setTimeUnit(null);
				} else {
					String strTimeUnit;
					// If no previous time unit set, use seconds
					if (getTimeUnit() == null){
						strTimeUnit = "seconds";
					}
					else{
						// Get the previous unit of time
						strTimeUnit = getTimeUnit().getUnitName();
					}
					//Set the new timeout
					_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new ActionPatternEdit(getSelf(), alElements, new TimeUnit(strTimeUnit, value), strName, enabled, documentation)));
					setTimeUnit(new TimeUnit(strTimeUnit, value));
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
		if(getTimeUnit() != null){
			strCurrentTimeoutUnit = getTimeUnit().getUnitName();
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
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get the actual value
				double value;
				if(getTimeUnit() == null){
					value = 1;
				}
				else{
					value = getTimeUnit().getUnitValue();
				}
				_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new ActionPatternEdit(getSelf(), alElements, new TimeUnit((String)timeoutUnit.getSelectedItem(), value), strName, enabled, documentation)));
				setTimeUnit(new TimeUnit((String)timeoutUnit.getSelectedItem(), value));
			}
		});
		
		timeoutPanel.add(timeoutUnit);
		
		// Checkbox for enabling and disabling the Action Pattern
		final JCheckBox enabled = new JCheckBox("Enabled?", isEnabled());

		// Action listener for enabling / disabling the Action Pattern
		enabled.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new ActionPatternEdit(getSelf(), alElements, tTimeOut, strName, enabled.isSelected(), documentation)));
				setEnabled(enabled.isSelected());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
		JPanel panel = new JPanel();
		
		// Set the panel layout
		panel.setLayout(new java.awt.GridLayout(0, 1));
		JLabel typeLabel = new JLabel("Action Pattern Properties (" + getName() + ")");
		typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		typeLabel.setFont(new Font(typeLabel.getFont().getName(),Font.BOLD,typeLabel.getFont().getSize() + 1));
		// Add each panel
		// Seperate panels are used to keep labels adjacent to text fields
		panel.add(typeLabel);
		panel.add(namePanel);
		panel.add(timeoutPanel);
		// TODO: Removed this as this functionality isn't currently working
//		panel.add(enabled);

		mainGui.setPropertiesPanel(panel);
	}

	public ActionPattern getSelf() {
		return this;
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
		menu.add(new JMenuItem("Action Pattern"));
		menu.addSeparator();
		_subGui=window;
		_diagram=diagram;

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
				_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new ActionPatternEdit(getSelf(), alElements, tTimeOut, strName, isEnabled(), documentation)));
				window.updateDiagrams(diagram, null);
			}
		});

		JMenuItem addElement = new JMenuItem("Add Action Element");
		addElement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aev) {
				ActionElement ae = new ActionElement(false, "someComposite");
				ArrayList newElements = (ArrayList) getSelf().getElements().clone();
				newElements.add(ae);
				_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new ActionPatternEdit(getSelf(), newElements, tTimeOut, strName, enabled, documentation)));
				getSelf().setElements(newElements);
				window.updateDiagrams(diagram, ae);
			}
		});

		JMenuItem deleteElement = new JMenuItem("Delete Action Pattern");
		deleteElement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(diagram, "Are you sure you want to delete this Action Pattern?") == JOptionPane.YES_OPTION) {
					_undoListener.undoableEditHappened(new UndoableEditEvent(getSelf(), new DeleteEdit(diagram, window, showOn.getParentNode(), getSelf(), lap.getElements().indexOf(getSelf()), lap.getElements())));
					lap.getElements().remove(getSelf());
					window.updateDiagrams(diagram, showOn.getParentNode().getValue());
				}
			}
		});

		/* TODO: This has been disabled because this functionality doesn't actually work */
//		menu.add(disableThis);
		menu.addSeparator();
		menu.add(addElement);
		menu.add(deleteElement);
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
	@Override
	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap, boolean detailed, boolean expanded) {
		Color colorToDraw;
		if (isEnabled()) {
			colorToDraw = Configuration.getRGB("colours/actionPattern");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		}
		JTreeNode apNode = new JTreeNode(getName(), "Action Pattern", colorToDraw, this, root);
		JTreeNode chain = apNode;
		
		for (Object obj : getElements()) {
			ActionElement ae = (ActionElement) obj;
			chain = ae.buildTree(chain, lap, detailed, expanded);
			chain.setOrganiser(new HorizontalListOrganiser());
			chain.setGroup(getElements());
		}
			
		
		apNode.setGroup(lap.getElements());
		apNode.setOrganiser(new ListOrganiser());
		return apNode;
	}

}

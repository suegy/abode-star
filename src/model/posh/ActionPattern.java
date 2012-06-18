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

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import model.IEditableElement;
import model.INamedElement;
import model.TimeUnit;
import abode.Configuration;
import abode.visual.HorizontalListOrganiser;
import abode.visual.JAbode;
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

	public void setDocumentation(String newDocumentation) {
		this.documentation = newDocumentation;
	}
	
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
	
	/**
	 * When we click this Action Pattern in the GUI populate the properties
	 * panel with the various attributes and setup listeners to catch modifications
	 * that are made.
	 *
	 * @param mainGui Reference to the MDI
	 * @param subGui Reference to the editor window
	 * @param diagram Diagram refernece on our editing window
	 **/
	public void onSelect(JAbode mainGui, final JEditorWindow subGui, final JDiagram diagram) {
		// Show the right menu and refresh this button to show the new state
		mainGui.popOutProperties();
		diagram.repaint();

		mainGui.setDocumentationField(this);
		
		// Prepare our table model
		TableModel tableModel = new AbstractTableModel() {
			//Added to properly implement Serializable
			private static final long serialVersionUID = 1;

			// Column titles
			private String[] columnNames = { "Attribute", "Value" };

			// Our state data
			private Object[][] data = populateData();

			/**
			 * Get the name of the column with the specified index
			 *
			 * @param col Column index
			 * @return Name of the column
			 **/
			public String getColumnName(int col) {
				return columnNames[col];
			}

			/**
			 * Get a value from the array of state data
			 * 
			 * @param row Row to look at.
			 * @param col Column to look at
			 * @return Object stored in specified location
			 **/
			public Object getValueAt(int row, int col) {
				return data[row][col];
			}

			/**
			 * Get the number of rows in this table model
			 *
			 * @return Number of rows
			 **/
			public int getRowCount() {
				return data.length;
			}

			/**
			 * Get the type of data represented in the column with the specified index
			 * 
			 * @param c Index of column
			 * @return Class of data stored in column (taken from first row)
			 **/
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			/**
			 * Get the number of columns in this table model
			 *
			 * @return Number of columns
			 **/
			public int getColumnCount() {
				return columnNames.length;
			}

			/**
			 * Can the specified cell be edited?
			 *
			 * @param row Row 
			 * @param col Column
			 * @return True if editable, false if not
			 **/
			public boolean isCellEditable(int row, int col) {
				if (col == 0)
					return false;
				return true;
			}

			/**
			 * Set the value at the specified location
			 * 
			 * @param value Value to store
			 * @param row Row to store in
			 * @param col Column to store in
			 **/
			public void setValueAt(Object value, int row, int col) {
				if (col != 1)
					return;

				if (row == 0)
					setName(value.toString());
				if (row == 1) {
					if (value.toString().length() < 1) {
						getTimeUnit().setUnitValue(0);
					} else {
						getTimeUnit().setUnitValue(Double.parseDouble(value.toString()));
					}
				}
				if (row == 2)
					getTimeUnit().setUnitName(value.toString());
				if (row == 3) {
					setEnabled(Boolean.valueOf(value.toString()).booleanValue());
				}

				data[row][col] = value;
				subGui.updateDiagrams(diagram, getSelf());
				subGui.repaint();

			}

			/**
			 * Perform first time initialization of the data array.
			 *
			 * @return Current state of the object
			 **/
			private Object[][] populateData() {
				Object[][] result = new Object[4][2];
				result[0][0] = "Name";
				result[0][1] = getName();
				result[1][0] = "Timeout Length";
				result[1][1] = new Double(getTimeUnit().getUnitValue());
				result[2][0] = "Timeout Unit";
				result[2][1] = getTimeUnit().getUnitName();
				result[3][0] = "Enabled";
				result[3][1] = new Boolean(isEnabled());
				return result;
			}
		};

		JTable table = new JTable(tableModel);
		mainGui.setPropertiesTable(table);
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
	public void showContextMenu(final JTreeNode showOn, final LearnableActionPattern lap, final JEditorWindow window, final JDiagram diagram) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("Action Pattern"));
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

		JMenuItem addElement = new JMenuItem("Add Action Element");
		addElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				ActionElement ae = new ActionElement(false, "someComposite");

				getSelf().getElements().add(ae);
				window.updateDiagrams(diagram, ae);
			}
		});

		JMenuItem deleteElement = new JMenuItem("Delete Action Pattern");
		deleteElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(diagram, "Are you sure you want to delete this Action Pattern?") == JOptionPane.YES_OPTION) {
					lap.getElements().remove(getSelf());
					window.updateDiagrams(diagram, showOn.getParentNode().getValue());
				}
			}
		});

		menu.add(disableThis);
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

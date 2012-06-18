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

import javax.swing.JMenu;
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
import abode.visual.JAbode;
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
		
		TableModel tableModel = new AbstractTableModel() {
			//Added to get rid of warnings and properly implement Serializable
			private static final long serialVersionUID = 1;

			private String[] columnNames = { "Attribute", "Value" };

			private Object[][] data = populateData();

			public String getColumnName(int col) {
				return columnNames[col];
			}

			public Object getValueAt(int row, int col) {
				return data[row][col];
			}

			public int getRowCount() {
				return data.length;
			}

			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public int getColumnCount() {
				return columnNames.length;
			}

			public boolean isCellEditable(int row, int col) {
				if (col == 0)
					return false;

				return true;
			}

			public void setValueAt(Object value, int row, int col) {
				if (col != 1)
					return;

				if (row == 0)
					setName(value.toString());

				if (row == 1)
					setAction(value.toString());

				if (row == 2) {
					int ret = 0;
					try {
						ret = Integer.parseInt(value.toString());
					} catch (Exception e) {
					}

					setRetries(ret);
					if (ret == 0)
						value = "0 (Unlimited)";
				}

				data[row][col] = value;
				subGui.updateDiagrams(diagram, getSelf());
				subGui.repaint();
			}

			private Object[][] populateData() {
				Object[][] result = new Object[3][2];
				result[0][0] = "Competence Element";
				result[0][1] = getName();
				result[1][0] = "Action Triggered";
				result[1][1] = getAction();
				result[2][0] = "Retries";
				result[2][1] = (getRetries() > 0) ? "" + getRetries() : "0 (Unlimited)";
				return result;

			}
		};

		JTable table = new JTable(tableModel);
		// TODO:
//		mainGui.setPropertiesPanel(table);
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
			action = new JTreeNode(getAction(), "Action to Trigger", colorToDraw, this, result);
			ActionElement.actionListToTree("Trigger Elements", "", getTrigger(), result, this, this.isEnabled());
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

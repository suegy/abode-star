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
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.IEditableElement;
import abode.Configuration;
import abode.JAbode;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;
import abode.visual.VerticalListOrganiser;

/**
 * A (RealTime/Discrete Time) drive collection is a named goal and a set of
 * drive elements that work to achieve that goal.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class DriveCollection implements IEditableElement {
	// Are we a realtime drive collection (False->Discrete Time)
	private boolean bIsRealTime = false;
	
	
	private boolean bIsStrict = false;

	// Our name for this drive collection
	private String strName = null;

	// Our goal (ArrayList<Object>of actionelements)
	private ArrayList<Object>alGoal = null;

	// Our drive elements for this collection
	private ArrayList<Object>alDriveElements = null;

	private boolean enabled = true;

	// Docs
	private String documentation;

	/**
	 * Initialize this drive collection
	 * 
	 * @param name
	 *            Name of the collection
	 * @param realTime
	 *            Is this a real-time drive collection?
	 * @param elements
	 *            ArrayList<Object> of drive elements (or lists thereof, to be more
	 *            precise)
	 */
	public DriveCollection(String name, boolean realTime, ArrayList<Object>goal,
			ArrayList<Object>elements) {
		strName = name;
		bIsRealTime = realTime;
		alGoal = goal;
		alDriveElements = elements;
	}

	public DriveCollection(String name, boolean realTime, ArrayList<Object>goal,
			ArrayList<Object>elements, boolean shouldBeEnabled) {
		this(name, realTime, goal, elements);
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

	public boolean isEnabled() {
		return this.enabled;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setEnabled(boolean newValue) {
		this.enabled = newValue;
		// Disable the children. In a complicated fashion, naturally.
		for (Object directChildren : this.getDriveElements()) {
			if (directChildren instanceof ArrayList)
				for (Object grandChild : (ArrayList<Object>)directChildren) {
					((IEditableElement) grandChild).setEnabled(newValue);
				}
		}
	}

	/**
	 * Reset the name of this drive element
	 * 
	 * @param name
	 *            New name of the drive collection
	 */
	public void setName(String name) {
		strName = name;
	}

	/**
	 * Get the name of this drive collection
	 * 
	 * @return Name of the drive collection
	 */
	public String getName() {
		return strName;
	}

	/**
	 * Get our list of drive elements
	 * 
	 * @return Arraylist of lists of drive elements
	 */
	public ArrayList<Object>getDriveElements() {
		return alDriveElements;
	}

	/**
	 * Get the arraylist of actionelements that comprise our goal
	 * 
	 * @return Arraylist of action elements comprising our goal
	 */
	public ArrayList<Object>getGoal() {
		return alGoal;
	}

	/**
	 * Get whether or not this is a real-time drive collection
	 * 
	 * @return True if real time drive collection, false otherwise
	 */
	public boolean getRealTime() {
		return bIsRealTime;
	}

	/**
	 * Set our list of drive elements to be some new list
	 * 
	 * @param drive
	 *            Drive element lists .
	 */
	public void setDriveElements(ArrayList<Object>drive) {
		alDriveElements = drive;
	}

	/**
	 * Set our goal list
	 */
	public void setGoal(ArrayList<Object>goal) {
		alGoal = goal;
	}

	/**
	 * Set whether or not this is a real-time drive collection
	 * 
	 * @param real
	 *            Real time if true, discrete time if not
	 */
	public void setRealTime(boolean real) {
		bIsRealTime = real;
	}
	
	
	public boolean getStrictMode() {
		return bIsStrict;
	}
	
	public void setStrictMode(boolean isStrict) {
		bIsStrict = isStrict;
	}

	/**
	 * Get reference back this object for inner class
	 */
	public DriveCollection getSelf() {
		return this;
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
	@Override
	public void onSelect(JAbode mainGui, final JEditorWindow subGui,
			final JDiagram diagram) {
		mainGui.popOutProperties();
		diagram.repaint();

		mainGui.setDocumentationField(this);

		// Add name label
		JLabel namelabel = new JLabel("Name");

		int vNamefieldSize = 15;
		final JTextField namefield = new JTextField(getName(), vNamefieldSize);

		// Action listener to update the actual data when the field is updated
		namefield.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setName(namefield.getText());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});

		JPanel namePanel = new JPanel();
		
		namePanel.add(namelabel);
		namePanel.add(namefield);

		// Tick box for realtime Drive Collection
		final JCheckBox realtime = new JCheckBox("Realtime?", getRealTime());

		// Action listener for setting the data in the class
		realtime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setRealTime(realtime.isSelected());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});

		JPanel realtimePanel = new JPanel();
		
		realtimePanel.add(realtime);
		
		// Checkbox for enabling and disabling the Drive Collection
		final JCheckBox enabled = new JCheckBox("Enabled?", isEnabled());

		// Action listener for enabling / disabling the drive collection
		enabled.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEnabled(enabled.isSelected());
				subGui.repaint();
				subGui.updateDiagrams(diagram, getSelf());
			}
		});
		
	
		JPanel panel = new JPanel();
		
		// Set the panel layout
		panel.setLayout(new java.awt.GridLayout(0, 1));
		JLabel typeLabel = new JLabel("Drive Collection Properties (" + getName() + ")");
		typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		typeLabel.setFont(new Font(typeLabel.getFont().getName(),Font.BOLD,typeLabel.getFont().getSize() + 1));
		// Add each panel
		// Seperate panels are used to keep labels adjacent to text fields
		panel.add(typeLabel);
		panel.add(namePanel);
		panel.add(realtimePanel);
		
		/* TODO: This has been disabled because this functionality doesn't actually work */
//		panel.add(enabled);

		// Add this panel to the main GUI
		mainGui.setPropertiesPanel(panel);
	}

	// Automatically increment drive element numbers
	private int driveElement = 1;

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
	@Override
	public void showContextMenu(final JTreeNode showOn,
			final LearnableActionPattern lap, final JEditorWindow window,
			final JDiagram diagram) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("Drive Collection"));
		menu.addSeparator();

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

		JMenuItem addNew = new JMenuItem("Add new Drive Element");
		addNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String name = "Some Name";
				while (name.indexOf(" ") >= 0) {
					name = JOptionPane
							.showInputDialog(
									showOn.getParent(),
									"What would you like to name this new drive element?",
									"DriveElement" + driveElement++).trim();
					if (name.indexOf(" ") >= 0)
						JOptionPane.showMessageDialog(showOn.getParent(),
								"Drive Element Names can not contain spaces!");
				}

				DriveElement element = new DriveElement(name, new ArrayList<Object>(),
						"act_" + name);
				ArrayList<Object>elementList = new ArrayList<Object>();
				elementList.add(element);

				getDriveElements().add(elementList);
				window.updateDiagrams(diagram, element);
			}
		});

		JMenuItem addGoal = new JMenuItem("Add Goal Sense");
		addGoal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				ActionElement actionElement = new ActionElement(true,
						"SomeSense" + driveElement++);
				getGoal().add(actionElement);
				window.updateDiagrams(diagram, actionElement);
			}
		});

		JMenuItem delete = new JMenuItem("Delete drive Collection");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (JOptionPane.showConfirmDialog(
						showOn.getParent(),
						"Delete Drive Collection",
						"Are you sure you want to delete the drive collection?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					lap.getElements().remove(showOn.getValue());

					window.updateDiagrams(diagram, null);
				}

			}
		});

		/* TODO: This has been disabled because this functionality doesn't actually work */
//		menu.add(disableThis);
		menu.addSeparator();
		menu.add(addNew);
		menu.add(addGoal);
		menu.add(delete);

		menu.show(showOn, showOn.getX(), showOn.getY());
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public JTreeNode buildTree(JTreeNode root, LearnableActionPattern lap,
			boolean detailed, boolean expanded) {
		Color colorToDraw;
		if (isEnabled()) {
			colorToDraw = Configuration.getRGB("colours/driveCollection");
		} else {
			colorToDraw = Color.LIGHT_GRAY;
		}
		JTreeNode base = new JTreeNode(getName(),
				(getRealTime() ? "Real-Time DC" : "Non Real-Time DC"),
				colorToDraw, this, root);

		if (detailed) {
			ActionElement.actionListToTree("Goal", "Goal of drive collection",
					getGoal(), base, this, this.isEnabled());
		}

		base.setGroup(getDriveElements());

		for (Object groups : this.getDriveElements()) {
			ArrayList<Object>groupBy = (ArrayList) groups;
			for (Object inGroup : (ArrayList<Object>)groups) {
				JTreeNode node = ((DriveElement) inGroup).buildTree(
						base, lap, detailed, expanded);
				node.setGroup(groupBy);
				node.setOrganiser(new VerticalListOrganiser());
			}
		}
		return base;
	}

}

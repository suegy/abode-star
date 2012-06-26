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
package abode.visual;


import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

import model.IEditableElement;
import abode.JAbode;
import abode.editing.IRenderer;


/**
 * The JTreeNode encodes a semantic relationship between editable elements of
 * the Learnable Action Pattern files, and is used primarily for the
 * organisation of such elements before rendering them.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class JTreeNode extends JButton {

	// Added to implement serializable properly
	private static final long serialVersionUID = 1;

	// The node before us in the tree
	private JTreeNode parent = null;

	// All nodes that branch from us
	private ArrayList children = new ArrayList();;

	// The object we're linked to
	private IEditableElement value = null;

	// The main text on this button
	private String label;

	// The sub-text of this button
	private String subText;

	// The colour of the background for this button
	private Color colour;

	// The popup context menu for this button
	// private JPopupMenu menu;

	// Do we want to be rendered visually or not?
	private boolean bIsRendered = true;

	// Does the node refer to a valid primitive/composite
	private boolean valid = true;

	// Our popup context menu
	private JPopupMenu popup = null;

	// our diagram render
	private IRenderer renderer = null;

	// Get the object we group by
	private ArrayList groupBy = null;

	// What manages our rearranging
	private IListOrganiser organiser = null;

	public JTreeNode(String title, String caption, Color col, IEditableElement element, JTreeNode parentNode, IRenderer renderObject, ArrayList group) {
		this(title, caption, col, element, parentNode, renderObject);
		setGroup(group);
	}

	/**
	 * Create this tree node, but make sure it has a rendering object of its
	 * own.
	 */
	public JTreeNode(String title, String caption, Color col, IEditableElement element, JTreeNode parentNode, IRenderer renderObject) {
		this(title, caption, col, element, parentNode);
		setRenderer(renderObject);
	}

	/**
	 * Initialize our tree node
	 */
	public JTreeNode(String title, String caption, Color col, IEditableElement element, JTreeNode parentNode) {
		super.setRolloverEnabled(false);
		super.setFocusPainted(false);

		// Add to our parents child list if need be
		if (parentNode != null)
			parentNode.addChild(this);

		// Set our attributes
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setFocusPainted(false);
		setValue(element);
		setTitle(title);
		setColour(col);
		setSubText(caption);
		setParent(parentNode);
		setDoubleBuffered(true);

		// Start listening for when anything happens to this (i.e. clicked)
		this.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				treeNodeActionPerformed(evt);
			}
		});

		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				actionMouseClicked(evt);
			}

			public void mousePressed(MouseEvent evt) {
				actionMouseClicked(evt);
			}
		});

		// When we're selected and de-selected, hook these so we
		// can update the display of this button accordingly.
		this.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				treeNodeFocusGained(evt);
			}

			public void focusLost(java.awt.event.FocusEvent evt) {
				treeNodeFocusLost(evt);
			}
		});
	}

	/**
	 * Get the object we group with
	 */
	public ArrayList getGroup() {
		return groupBy;
	}

	/**
	 * Set the object we group with
	 */
	public void setGroup(ArrayList o) {
		groupBy = o;
	}

	/**
	 * Set our rendering object
	 */
	public void setRenderer(IRenderer renderObject) {
		renderer = renderObject;

		// Propegate to children
		Iterator it = getChildren().iterator();
		while (it.hasNext())
			((JTreeNode) it.next()).setRenderer(renderObject);
	}

	/**
	 * Get our rendering object
	 */
	public IRenderer getRenderer() {

		if (renderer == null) {
			if (parent == null)
				return ((JDiagram) getParent()).getRenderer();
			else
				return parent.getRenderer();
		}

		return renderer;
	}

	/**
	 * Rewrite the tree so that any extra "Action to trigger" nodes are removed.
	 * 
	 * @param root
	 *            Root of this sub-tree
	 */
	public void rewrite() {
		ArrayList children = new ArrayList();
		ArrayList myChildren = getChildren();
		Iterator it = myChildren.iterator();
		while (it.hasNext()) {
			JTreeNode node = (JTreeNode) it.next();
			if (node.getSubTitle().equals("Action to Trigger")) {
				if (node.getChildren().size() > 0) {
					Iterator itInner = node.getChildren().iterator();
					while (itInner.hasNext())
						children.add(itInner.next());
				}
			} else {
				node.rewrite();
				children.add(node);
			}
		}

		setChildren(children);
	}

	/**
	 * Get the list organiser
	 */
	public IListOrganiser getOrganiser() {
		return organiser;
	}

	/**
	 * Set our list organiser object
	 */
	public void setOrganiser(IListOrganiser org) {
		organiser = org;
	}

	/**
	 * Get the colour we'return supposed to be painted with
	 */
	public Color getColour() {
		return colour;
	}

	/**
	 * Render this tree node invisibly?
	 */
	public void setRendered(boolean render) {
		bIsRendered = render;
	}

	/**
	 * Do we want this to show up on the tree?
	 */
	public boolean getRendered() {
		return bIsRendered;
	}

	/**
	 * Scan the tree looking for an element with the correct value and return
	 * it, as well as grabbing the focus.
	 */
	public JTreeNode findAndFocus(IEditableElement element) {
		if (element == getValue()) {
			grabFocus();
			return this;
		}

		Iterator it = getChildren().iterator();
		while (it.hasNext()) {
			JTreeNode result = ((JTreeNode) it.next()).findAndFocus(element);
			if (result != null)
				return result;
		}
		return null;
	}

	/**
	 * Set the parent of this object in the tree
	 */
	public void setParent(JTreeNode node) {
		parent = node;
	}

	/**
	 * Set the title text of this button
	 */
	public void setTitle(String text) {
		label = text;
		repaint();
	}

	/**
	 * Get the title of this button
	 */
	public String getTitle() {
		return label;
	}

	/**
	 * Set the sub=text of this button
	 */
	public void setSubText(String text) {
		subText = text;
		repaint();
	}

	/**
	 * Get the sub-title of this button
	 */
	public String getSubTitle() {
		return subText;
	}

	/**
	 * Set the background color of this button
	 */
	public void setColour(Color col) {
		colour = col;
	}

	/**
	 * Get the name of the node
	 */
	public String getName() {
		return label;
	}

	/**
	 * Get a list of our children
	 */
	public ArrayList getChildren() {
		return children;
	}

	/**
	 * Set our list of children
	 */
	public void setChildren(ArrayList childList) {
		children = childList;
		Iterator it = children.iterator();
		while (it.hasNext()) {
			JTreeNode child = (JTreeNode) it.next();
			child.setParentNode(this);
		}
	}

	/**
	 * Add a new child node off this one
	 */
	public void addChild(JTreeNode child) {
		children.add(child);
		child.setParentNode(this);
	}

	/**
	 * Get the editable construct we're representing
	 */
	public IEditableElement getValue() {
		return value;
	}

	/**
	 * Set the value of our edited construct
	 */
	public void setValue(IEditableElement objVal) {
		value = objVal;
	}

	/**
	 * Set the parent node of this element
	 */
	public void setParentNode(JTreeNode parentNode) {
		parent = parentNode;
	}

	/**
	 * Get the parent node of this element
	 */
	public JTreeNode getParentNode() {
		return parent;
	}

	/**
	 * An action was performed on the button, typically a click
	 */
	public void treeNodeActionPerformed(ActionEvent event) {
		// If we'return not encapsulating a construct, no point in
		// triggering an event
		IEditableElement element = getValue();
		if (element == null)
			return;

		// Get our various attributes for passing to the element
		JDiagram diagram = (JDiagram) getParent();
		JEditorWindow internal = diagram.getInternalFrame();
		JAbode gui = internal.getMainFrame();

		// Trigger the selection action on our editable element
		element.onSelect(gui, internal, diagram);
		
		// Command panel
		gui.getCommandsPanel().removeAll();
		// Edit panel
		gui.getEditPanel().removeAll();

		if (organiser != null)
			organiser.populateOptionsPanel(gui, internal, diagram, this);
		
		// Revalidate / draw the updated command panel
		gui.getCommandsPanel().revalidate();
		gui.getCommandsPanel().repaint();
		
		// Edit panel
		gui.getEditPanel().revalidate();
		gui.getEditPanel().repaint();

	}

	/**
	 * The user has clicked on us
	 */
	private void actionMouseClicked(MouseEvent evt) {
		if (evt.isPopupTrigger() || (evt.getClickCount() > 1)) {
			if (popup != null)
				popup.show(evt.getComponent(), evt.getX(), evt.getY());
			else {
				IEditableElement element = getValue();
				if (element != null) {
					JDiagram diagram = (JDiagram) getParent();
					JEditorWindow internal = diagram.getInternalFrame();
					element.showContextMenu(this, internal.getLearnableActionPattern(), internal, diagram);
				}
			}
		}
	}

	/**
	 * We've been focused
	 */
	private void treeNodeFocusGained(FocusEvent event) {
		getParent().repaint();
		JDiagram diagram = (JDiagram) getParent();
		JEditorWindow window = (JEditorWindow) diagram.getInternalFrame();
		JAbode abode = (JAbode) window.getMainFrame();
		abode.setStatus("Editing " + getName());
	}

	/**
	 * We've lost focus
	 */
	private void treeNodeFocusLost(FocusEvent event) {
		Component opposite = event.getOppositeComponent();

		if ((opposite == null) || (opposite instanceof JTreeNode) || (opposite instanceof JDiagram)) {
			JDiagram diagram = (JDiagram) getParent();
			JEditorWindow window = (JEditorWindow) diagram.getInternalFrame();
			JAbode abode = (JAbode) window.getMainFrame();

			abode.clearProperties();
			abode.getCommandsPanel().removeAll();
		}

		getParent().repaint();
	}

	/**
	 * Repaint the node
	 */
	public void paintComponent(Graphics g) {
		IRenderer renderObject = getRenderer();
		if (renderObject != null)
			renderObject.paintTreeNode(this, g);
	}

	/**
	 * Set the validity of this node
	 */
	public void setValid(boolean validBool) {
		valid = validBool;
	}

	/**
	 * Is this a valid node?
	 */
	public boolean isValid() {
		return valid;
	}
}

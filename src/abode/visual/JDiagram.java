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


import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import model.IEditableElement;
import model.posh.ActionElement;
import model.posh.CompetenceElement;
import model.posh.DriveElement;
import model.posh.LearnableActionPattern;
import abode.JAbode;
import abode.editing.IRenderer;

/**
 * A GuiDiagram is a diagramatic rendering of a JTreeNode hieararchy
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class JDiagram extends javax.swing.JPanel {

	// Added to properly implement Serializable
	private static final long serialVersionUID = 1;

	// The root of the tree that we're rendering from
	private JTreeNode root = null;

	// The JInteranlFrame that is hosting us.
	private JEditorWindow parent = null;

	// Our diagram rendering object
	private IRenderer renderer = null;

	// How big is this diagram supposed to be when fully drawn?
	private Dimension diagramDimensions = null;

	// Detailed diagram?
	private boolean bDetailed = false;

	// Expanded diagram?
	private boolean bExpanded = false;

	/**
	 * Create a new diagram with a root node and a parent frame
	 * 
	 * @param parentFrame
	 *            Window that is hosting this diagram
	 * @param tree
	 *            The root of the tree this diagram contains
	 * @param render
	 *            The rendering object to use for this particular display
	 * @param expanded
	 *            Does this diagram show expansion (i.e. un-ravel the recursive
	 *            references)
	 * @param detailed
	 *            Does this diagram show detailed information and extra nodes
	 *            (i.e. trigger lists)
	 */
	public JDiagram(JEditorWindow parentFrame, JTreeNode tree, IRenderer render, boolean expanded, boolean detailed) {
		initComponents();
		setVisible(true);
		setPreferredSize(null);

		
		// Our properties
		bDetailed = detailed;
		bExpanded = expanded;

		// Set our parent
		parent = parentFrame;

		// Load settings, add our root (setRoot will trigger a repaint)
		setRenderer(render);
		setRoot(tree);
	}

	/**
	 * Are we an expanded diagram, showing elements invoked by elements on the
	 * tree?
	 * 
	 * @return True if expanded, false otherwise
	 */
	public boolean getExpanded() {
		return bExpanded;
	}

	/**
	 * Are we a detailed diagram, showing elements not directly related to the
	 * actions?
	 * 
	 * @return True if detailed, false otherwise
	 */
	public boolean getDetailed() {
		return bDetailed;
	}

	/**
	 * set everything as valid to undo the previous validation process
	 */
	public void unvalidate() {
		unvalidateRecursive(root);
		repaint();
	}

	/**
	 * Recurse through the tree setting everything as valid
	 */
	private void unvalidateRecursive(JTreeNode root) {
		root.setValid(true);
		
		for (Object child : root.getChildren()) {
			unvalidateRecursive((JTreeNode) child);
		}

	}

	/**
	 * Validate this diagram
	 */
	public void validate(LearnableActionPattern lap, ArrayList actions, ArrayList senses) {
		validateRecursive(lap, root, actions, senses);
	}

	/**
	 * Recurse through tree looking for primitives that are not defined.
	 */
	private void validateRecursive(LearnableActionPattern lap, JTreeNode root, ArrayList actions, ArrayList senses) {
		// Assume we'return valid
		root.setValid(true);

		// If we've got a value, check it out
		if (root.getValue() != null) {
			IEditableElement element = root.getValue();

			// If we're an action element, we may be a sense, action or
			// posh-root
			if (element instanceof ActionElement) {
				ActionElement actionElement = (ActionElement) element;
				if (actionElement.getIsSense()) {
					// If we're not in the sense list we're invalid
					if (!senses.contains(actionElement.getElementName())) {
						root.setValid(false);
						parent.addError();
						parent.addOutputBuffer("<font style=color:red><b>Validation Error: </b></font>" + "<u>" + root.getName() + "</u>" + " is not a valid primitive/aggregate<BR>");
					}
				} else {
					// If we're an action and not part of the file or named as
					// an action primitive, then we're invalid
					if ((!actions.contains(actionElement.getElementName())) && (!lap.containsElementNamed(actionElement.getElementName()))) {
						root.setValid(false);
						parent.addError();
						parent.addOutputBuffer("<font style=color:red><b>Validation Error: </b></font>" + "<u>" + root.getName() + "</u>" + " is not a valid primitive/aggregate<BR>");
					}
				}
			}

			// If we're a competence we may trigger an action or action pattern
			// or another competence
			if (element instanceof CompetenceElement) {
				CompetenceElement ce = (CompetenceElement) element;

				// If we're not in the element list AND not in the action list,
				// we're invalid
				if ((!actions.contains(ce.getAction())) && (!lap.containsElementNamed(ce.getAction()))) {
					root.setValid(false);
					parent.addError();
					parent.addOutputBuffer("<font style=color:red><b>Validation Error: </b></font>" + "<u>" + root.getName() + "</u>" + " is not a valid primitive/aggregate<BR>");
				}
			}

			if (element instanceof DriveElement) {
				DriveElement de = (DriveElement) element;
				if ((!actions.contains(de.getAction())) && (!lap.containsElementNamed(de.getAction()))) {
					root.setValid(false);
					parent.addError();
					parent.addOutputBuffer("<font style=color:red><b>Validation Error: </b></font>" + "<u>" + root.getName() + "</u>" + " is not a valid primitive/aggregate<BR>");
				}
			}
		}

		Iterator iterator = root.getChildren().iterator();
		while (iterator.hasNext())
			validateRecursive(lap, (JTreeNode) iterator.next(), actions, senses);

	}

	/**
	 * Get the object responsible for rendering us
	 */
	public IRenderer getRenderer() {
		return renderer;
	}

	/**
	 * If our diagram rendering object has been changed, propegate the change to
	 * our diagram elements
	 */
	public void setRenderer(IRenderer render) {
		renderer = render;
		if (root != null)
			root.setRenderer(render);
		render.layoutNodes(this, root);
		repaint();
	}

	/**
	 * Get the internal frame that houses us
	 */
	public JEditorWindow getInternalFrame() {
		return parent;
	}

	/**
	 * Set the current dimensions od the diagram
	 */
	public void setDimensions(Dimension d) {
		setSize(d);
		setPreferredSize(d);
		diagramDimensions = d;
	}

	/**
	 * Get the current dimensions of the diagram
	 */
	public Dimension getDimensions() {
		return diagramDimensions;
	}

	/**
	 * Get the root of the tree we're rendering
	 */
	public JTreeNode getRoot() {
		return root;
	}

	/**
	 * Reset the root of the tree we're rendering
	 */
	public void setRoot(JTreeNode rootNode) {
		// If we've already got a root, remove it
		if (root != null)
			remove(root);

		// Set our new root and add it
		root = rootNode;
		add(root);

		// Lay out the nodes differently
		renderer.layoutNodes(this, root);
		repaint();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {

		setLayout(null);

		setBackground(new java.awt.Color(51, 51, 51));
		setToolTipText("Click to expand diagram view");
		setName("diagramPanel");
		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				formMouseClicked(evt);
			}
		});

	}

	// </editor-fold>//GEN-END:initComponents

	private void formMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseClicked
		JAbode abode = parent.getMainFrame();
		abode.hideProperties();
		abode.hideConsole();
	}// GEN-LAST:event_formMouseClicked

	/**
	 * Redraw the component
	 * 
	 * @param g
	 *            Swing graphics object
	 */
	@Override
	public void paintComponent(Graphics g) {
		if (renderer == null)
			return;

		// Render to the window
		renderer.paintDiagram(this, g);
	}

	/**
	 * Repaint the diagram
	 */
	@Override
	public void repaint() {
		super.repaint();
	}
}

// Variables declaration - do not modify//GEN-BEGIN:variables
// End of variables declaration//GEN-END:variables


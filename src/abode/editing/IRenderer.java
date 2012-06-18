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
package abode.editing;


import java.awt.Graphics;

import abode.visual.JDiagram;
import abode.visual.JTreeNode;


/**
 * Objects that impelement IRenderer are used to provide pluggable painting
 * support for diagrams, so that various styles can be implemented for the same
 * information to be displayed in different ways.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public interface IRenderer {
	/**
	 * Get the zoom level of this diagram
	 * 
	 * @return Current level of zoom
	 */
	public double getZoomLevel();

	/**
	 * Set the zoom level of this diagram
	 * 
	 * @param zoomLevel
	 *            New zoom level
	 */
	public void setZoomLevel(double zoomFactor);

	/**
	 * Paint a diagrams backgrounds and the connecting lines between elements of
	 * a tree.
	 * 
	 * @param diagram
	 *            Diagram to paint to.
	 * @param graphics
	 *            Graphics object to paint with.
	 */
	public void paintDiagram(JDiagram diagram, Graphics graphics);

	/**
	 * Paint a tree node on a diagram.
	 * 
	 * @param node
	 *            Node to paint.
	 * @param graphics
	 *            Graphics object to paint with.
	 */
	public void paintTreeNode(JTreeNode node, Graphics graphics);

	/**
	 * Lay out a hierarchy of nodes onto the diagram.
	 * 
	 * @param diagram
	 *            Diagram to lay out the nodes on.
	 * @param root
	 *            Root of the hierachy of nodes.
	 */
	public void layoutNodes(JDiagram diagram, JTreeNode root);
}

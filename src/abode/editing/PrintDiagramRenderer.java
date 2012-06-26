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


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;

import model.IEditableElement;
import model.posh.ActionElement;
import model.posh.ActionPattern;
import model.posh.Competence;
import model.posh.CompetenceElement;
import model.posh.DriveElement;
import model.posh.LearnableActionPattern;
import abode.Configuration;
import abode.visual.JDiagram;
import abode.visual.JTreeNode;


/**
 * The standard diagram renderer is useful for producing coloured displays of
 * the node hierarchy in a top to bottom, left to right non-proportional tree
 * structure.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class PrintDiagramRenderer implements IRenderer {

	// Button base dimensions
	private double WIDTH_BASE = 100;

	private double HEIGHT_BASE = 30;

	// Current button dimensions
	private int WIDTH = 100;

	private int HEIGHT = 30;

	// Our default scalar
	private double SCALAR = 1.0;

	// Our grid layout functions use this to track where
	// we are in the layout of things.
	private int yDepth = 0;

	// Pixels of spacing between buttons (base before scaling)
	private double XSPACING_BASE = 5;

	private double YSPACING_BASE = 10;

	// Scalar-multiplied versions of the above
	private int XSPACING = 5;

	private int YSPACING = 10;

	// Used to render the background of the diagram panel
	// private GradientPaint gradient = null;

	// Is the layout changed?
	protected boolean layoutInvalid = true;

	/**
	 * Initialize the diagram renderer
	 */
	public PrintDiagramRenderer() {
		loadConfiguration();
	}

	/**
	 * Reload attributes from the configuration database.
	 */
	public void loadConfiguration() {
		// Load our initial dimensions from the configuration file
		WIDTH_BASE = 140;
		HEIGHT_BASE = 60;

		// Spacing factors between the nodes
		XSPACING_BASE = 4;
		YSPACING_BASE = 10;

		// Zoom level
		setZoomLevel(Double.parseDouble(Configuration.getByKey("environment/zoomLevelDefaultPercent").get(1).toString()) / 100);
	}

	/**
	 * Get our currnet zoom level
	 */
	@Override
	public double getZoomLevel() {
		return SCALAR;
	}

	public void setXSpacing(int value) {
		XSPACING = value;
	}

	public void setYSpacing(int value) {
		YSPACING = value;
	}

	public int getXSpacing() {
		return XSPACING;
	}

	public int getYSpacing() {
		return YSPACING;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return WIDTH;
	}

	/**
	 * Reset the zoom level of the diagram
	 */
	@Override
	public void setZoomLevel(double zoom) {
		// Sensible constrictions upon the zoom levels
		double minZoom = Double.parseDouble(Configuration.getByKey("environment/zoomLevelBounds").get(1).toString()) / 100;
		double maxZoom = Double.parseDouble(Configuration.getByKey("environment/zoomLevelBounds").get(2).toString()) / 100;

		// Reset the min/max levels to some sane defaults if the values in the
		// file are impossible
		if ((minZoom > maxZoom) || (minZoom < 1)) {
			minZoom = 0.3;
			maxZoom = 3.0;
		}

		// Set out new zoom level if we are allowed
		zoom = (zoom < minZoom) ? minZoom : zoom;
		zoom = (zoom > maxZoom) ? maxZoom : zoom;

		SCALAR = zoom;
		WIDTH = (int) (WIDTH_BASE * SCALAR);
		HEIGHT = (int) (HEIGHT_BASE * SCALAR);
		XSPACING = (int) (XSPACING_BASE * SCALAR);
		YSPACING = (int) (YSPACING_BASE * SCALAR);

		layoutInvalid = true;
	}

	/**
	 * Paint a JDiagram for the user
	 */
	@Override
	public void paintDiagram(JDiagram diagram, Graphics g) {

		// Render this object with anti-aliasing turned on!
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Do we need to re-layout the nodes
		if (layoutInvalid)
			layoutNodes(diagram, diagram.getRoot());

		// Get our bounds
		// Rectangle bounds = diagram.getBounds();

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, diagram.getWidth(), diagram.getHeight());

		// Paint the black lines between the buttons
		g2d.setColor(Color.BLACK);
		if (diagram.getRoot() != null)
			paintRecursively(g2d, diagram.getRoot());
	}

	/**
	 * Recursively iterate through the tree and draw the lines connecting
	 * buttons
	 */
	private void paintRecursively(Graphics g, JTreeNode node) {
		Graphics2D g2d = (Graphics2D) g;

		// Calculate the point that lies on our right side at half-way up
		Point rightMiddle = new Point(node.getBounds().x + node.getBounds().width, node.getBounds().y);

		// For each of our children, calculate their leftmost side's middle
		// point
		Iterator children = node.getChildren().iterator();
		while (children.hasNext()) {
			JTreeNode child = (JTreeNode) children.next();

			if (node.getRendered()) {
				Point leftMiddle = new Point(child.getBounds().x, child.getBounds().y + child.getBounds().height);

				// Draw the three stages of the line
				g2d.setStroke(new BasicStroke(2.0f));
				g2d.drawLine(rightMiddle.x + (getXSpacing() / 3), rightMiddle.y, rightMiddle.x + (getXSpacing() / 3), leftMiddle.y);
			}
			paintRecursively(g2d, child);
		}
	}

	/**
	 * Paint an individual tree node
	 */
	@Override
	public void paintTreeNode(JTreeNode node, Graphics g) {

		node.setBorder(null);

		JDiagram diagram = (JDiagram) node.getParent();
		LearnableActionPattern lap = diagram.getInternalFrame().getLearnableActionPattern();

		// Get our bounds
		Graphics2D g2d = (Graphics2D) g;// diagram.getGraphics2D();
		Font mainFont = null;

		// Set the font and colour
		mainFont = g2d.getFont();
		mainFont = mainFont.deriveFont(Font.PLAIN, (int) (10 * getZoomLevel()));
		g2d.setFont(mainFont);

		// Truncate the text we'return drawing based on width
		String labelText = node.getTitle();
		if (labelText.length() > ((int) ((float) getWidth() / 4)))
			labelText = labelText.substring(0, (getWidth() / 4) - 3) + "...";

		if (node.getValue() != null) {
			IEditableElement element = node.getValue();

			if (element instanceof ActionElement) {
				// Draw our text
				g2d.setColor(Color.BLACK);
				g2d.drawString(labelText, (int) (10 * getZoomLevel()), (int) (12 * getZoomLevel()));
				g2d.setStroke(new BasicStroke(1.0f));
				g2d.drawLine((int) (10 * getZoomLevel()), (int) (16 * getZoomLevel()), (int) (10 * getZoomLevel() + 2 * (getWidth() / 3)), (int) (16 * getZoomLevel()));
			} else if (element instanceof DriveElement) {
				DriveElement de = (DriveElement) element;

				String subText = "";
				if (lap.containsElementNamed(de.getAction())) {
					IEditableElement el = lap.getElementNamed(de.getAction());
					if (el instanceof ActionPattern)
						subText += "(AP) ";
					if (el instanceof Competence)
						subText += "(C) ";
				}
				if (de.getTrigger() != null) {
					ArrayList triggers = de.getTrigger();
					Iterator it = triggers.iterator();
					while (it.hasNext()) {
						ActionElement el = (ActionElement) it.next();
						subText += "(" + el.getElementName();
						if (el.getValue() != null)
							subText += " " + el.getValue();
						if (el.getPredicate() != null)
							subText += " " + el.getPredicate();
						subText += ") ";
					}

				}
				if (de.getFrequency() != null) {
					String freq = new Double(de.getFrequency().getUnitValue()).toString();
					subText += "freq: " + freq + " ";
				}
				// Draw our text
				g2d.setColor(Color.BLACK);
				g2d.drawString(labelText, (int) (10 * getZoomLevel()), (int) (12 * getZoomLevel()));
				g2d.setStroke(new BasicStroke(1.0f));

				// Draw our subText
				Font subFont = mainFont.deriveFont(Font.ITALIC, (int) (9 * getZoomLevel()));
				g2d.setFont(subFont);
				if (subText.length() > ((int) ((float) getWidth() / 4))) {
					String line1 = subText.substring(0, (getWidth() / 5) - 3) + "...";
					g2d.drawString(line1, (int) (10 * getZoomLevel()), (int) (25 * getZoomLevel()));
					String line2 = subText.substring((getWidth() / 5) - 3);
					g2d.drawString(line2, (int) (10 * getZoomLevel()), (int) (40 * getZoomLevel()));
				} else {
					g2d.drawString(subText, (int) (10 * getZoomLevel()), (int) (25 * getZoomLevel()));
				}
			} else if (element instanceof CompetenceElement) {
				CompetenceElement de = (CompetenceElement) element;

				String subText = "";
				if (lap.containsElementNamed(de.getAction())) {
					IEditableElement el = lap.getElementNamed(de.getAction());
					if (el instanceof ActionPattern)
						subText += "(AP) ";
					if (el instanceof Competence)
						subText += "(C) ";
				}
				if (de.getTrigger() != null) {
					ArrayList triggers = de.getTrigger();
					Iterator it = triggers.iterator();
					while (it.hasNext()) {
						ActionElement el = (ActionElement) it.next();
						subText += "(" + el.getElementName();
						if (el.getValue() != null)
							subText += " " + el.getValue();
						if (el.getPredicate() != null)
							subText += " " + el.getPredicate();
						subText += ") ";
					}

				}

				// Draw our text
				g2d.setColor(Color.BLACK);
				g2d.drawString(labelText, (int) (10 * getZoomLevel()), (int) (12 * getZoomLevel()));
				g2d.setStroke(new BasicStroke(1.0f));

				// Draw our subText
				Font subFont = mainFont.deriveFont(Font.ITALIC, (int) (9 * getZoomLevel()));
				g2d.setFont(subFont);
				if (subText.length() > ((int) ((float) getWidth() / 4))) {
					String line1 = subText.substring(0, (getWidth() / 5) - 3) + "...";
					g2d.drawString(line1, (int) (10 * getZoomLevel()), (int) (25 * getZoomLevel()));
					String line2 = subText.substring((getWidth() / 5) - 3);
					g2d.drawString(line2, (int) (10 * getZoomLevel()), (int) (40 * getZoomLevel()));
				} else {
					g2d.drawString(subText, (int) (10 * getZoomLevel()), (int) (25 * getZoomLevel()));
				}
			} else {
				// Draw our text
				g2d.setColor(Color.BLACK);
				g2d.drawString(labelText, (int) (10 * getZoomLevel()), (int) (12 * getZoomLevel()));

				// Draw our subText
				Font subFont = mainFont.deriveFont(Font.ITALIC, (int) (9 * getZoomLevel()));
				g2d.setFont(subFont);
				g2d.drawString(node.getSubTitle(), (int) (10 * getZoomLevel()), (int) (25 * getZoomLevel()));
			}
		}

		// paintTreeNodeToImage(node, null);
	}

	/**
	 * Paint an individual tree node
	 */
	/*
	 * public void paintTreeNodeToImage(JTreeNode node, Graphics g) {
	 * 
	 * node.setBorder(null);
	 * 
	 * JDiagram diagram = (JDiagram) node.getParent(); LearnableActionPattern
	 * lap = diagram.getInternalFrame().getLearnableActionPattern(); ArrayList
	 * elements = lap.getElements();
	 *  // Get our bounds Graphics2D g2d = diagram.getGraphics2D(); Rectangle
	 * bounds = node.getBounds(); Font mainFont = null;
	 *  // Set the font and colour mainFont = g2d.getFont(); mainFont =
	 * mainFont.deriveFont(Font.PLAIN, (int)(10*getZoomLevel()));
	 * g2d.setFont(mainFont);
	 *  // Truncate the text we'return drawing based on width String labelText =
	 * node.getTitle(); if (labelText.length() > ((int) ((float) getWidth()/4)))
	 * labelText = labelText.substring(0, (getWidth()/4)-3) + "...";
	 * 
	 * if (node.getValue()!=null) { IEditableElement element = node.getValue();
	 * 
	 * 
	 * if (element instanceof ActionElement) { // Draw our text
	 * g2d.setColor(Color.BLACK);
	 * g2d.drawString(labelText,node.getLocation().x+(int)(10*getZoomLevel()),node.getLocation().y+(int)(12*getZoomLevel()));
	 * g2d.setStroke(new BasicStroke(1.0f));
	 * g2d.drawLine(node.getLocation().x+(int)(10*getZoomLevel()),node.getLocation().y+(int)(16*getZoomLevel()),node.getLocation().x+(int)(10*getZoomLevel()+2*(getWidth()/3)),
	 * node.getLocation().y+(int)(16*getZoomLevel())); } else if (element
	 * instanceof DriveElement) { DriveElement de = (DriveElement) element;
	 * 
	 * String subText = ""; if (lap.containsElementNamed(de.getAction())) {
	 * IEditableElement el = lap.getElementNamed(de.getAction()); if (el
	 * instanceof ActionPattern) subText += "(AP) "; if (el instanceof
	 * Competence) subText += "(C) "; } if (de.getTrigger()!= null) { ArrayList
	 * triggers = de.getTrigger(); Iterator it = triggers.iterator(); while
	 * (it.hasNext()){ ActionElement el = (ActionElement) it.next(); subText +=
	 * "(" + el.getElementName(); if (el.getValue()!=null) subText +=
	 * el.getValue()+ " "; if (el.getPredicate()!=null) subText +=
	 * el.getPredicate()+" "; subText += ") "; }
	 *  } if (de.getFrequency()!=null) { String freq = new
	 * Double(de.getFrequency().getUnitValue()).toString(); subText += "freq: "+
	 * freq + " "; } // Draw our text g2d.setColor(Color.BLACK);
	 * g2d.drawString(labelText,node.getLocation().x+(int)(10*getZoomLevel()),node.getLocation().y+(int)(12*getZoomLevel()));
	 * g2d.setStroke(new BasicStroke(1.0f));
	 *  // Draw our subText Font subFont = mainFont.deriveFont(Font.ITALIC,
	 * (int)(9*getZoomLevel())); g2d.setFont(subFont); if (subText.length() >
	 * ((int) ((float) getWidth()/4))){ String line1 = subText.substring(0,
	 * (getWidth()/5)-3) + "...";
	 * g2d.drawString(line1,(int)(10*getZoomLevel()),(int)(25*getZoomLevel()));
	 * String line2 = subText.substring((getWidth()/5)-3);
	 * g2d.drawString(line2,node.getLocation().x+(int)(10*getZoomLevel()),node.getLocation().y+(int)(40*getZoomLevel())); }
	 * else{
	 * g2d.drawString(subText,node.getLocation().x+(int)(10*getZoomLevel()),node.getLocation().y+(int)(25*getZoomLevel())); } }
	 * else { // Draw our text g2d.setColor(Color.BLACK);
	 * g2d.drawString(labelText,node.getLocation().x+(int)(10*getZoomLevel()),node.getLocation().y+(int)(12*getZoomLevel()));
	 *  // Draw our subText Font subFont = mainFont.deriveFont(Font.ITALIC,
	 * (int)(9*getZoomLevel())); g2d.setFont(subFont);
	 * g2d.drawString(node.getSubTitle(),node.getLocation().x+(int)(10*getZoomLevel()),node.getLocation().y+(int)(25*getZoomLevel())); } } }
	 */

	/**
	 * Create a composite colour for transparency
	 */
	public AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

	/**
	 * Lay out a hierarchy of nodes onto the diagram
	 */
	@Override
	public void layoutNodes(JDiagram diagram, JTreeNode root) {
		layoutInvalid = false;

		// Reset depth tracking
		yDepth = 0;

		// Our dimensions are now 0 x 0 (We'll expand
		// as we see fit later)
		Dimension dimensions = new Dimension(0, 0);

		// If we've got a tree, then propegate our new
		// scalar value and layout the nodes
		if (root != null) {

			layoutNodes(diagram, dimensions, root, 0);
		}

		// Reset diagram dimensions
		diagram.setDimensions(dimensions);

		// Repaint the diagram
		diagram.repaint();
	}

	/**
	 * Recursively render the diagram
	 */
	private void layoutNodes(JDiagram diagram, Dimension dimensions, JTreeNode node, int xDepth) {
		// Add each button to the display at the relevent position
		if (node.getRendered())
			layoutButton(diagram, dimensions, node, xDepth, yDepth);

		Iterator it = node.getChildren().iterator();
		while (it.hasNext())
			layoutNodes(diagram, dimensions, (JTreeNode) it.next(), node.getRendered() ? xDepth + 1 : xDepth);

		if (node.getChildren().size() == 0)
			yDepth++;
	}

	public void invalidateLayout() {
		layoutInvalid = true;
	}

	/**
	 * Draw the button onto the form at the relevant position
	 */
	private void layoutButton(JDiagram diagram, Dimension dimensions, JTreeNode button, int x, int y) {
		if (button.getParent() == null)
			diagram.add(button);

		// Calculate our bounds and set our initial size
		button.setLocation((x * (WIDTH + XSPACING) + XSPACING), (y * (HEIGHT + YSPACING)) + YSPACING);
		button.setSize(WIDTH, HEIGHT);

		// Re-calculate the dimensions we've reached so far
		int currentWidth = (x + 1) * (WIDTH + XSPACING);
		int currentHeight = (y + 1) * (HEIGHT + YSPACING);

		// Update the diagram dimensions if we'return rendering beyond them at
		// all
		if (currentWidth > dimensions.width)
			dimensions.width = currentWidth;
		if (currentHeight > dimensions.height)
			dimensions.height = currentHeight;
	}
}

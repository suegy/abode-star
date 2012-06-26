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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

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
public class StandardDiagramRenderer implements IRenderer {

	// Button base dimensions
	private double WIDTH_BASE = 130;

	private double HEIGHT_BASE = 30;

	// Current button dimensions
	private int WIDTH = 130;

	private int HEIGHT = 30;

	// Our default scalar
	private double SCALAR = 1.0;

	// Our grid layout functions use this to track where
	// we are in the layout of things.
	private int yDepth = 0;

	// Pixels of spacing between buttons (base before scaling)
	private double XSPACING_BASE = 10;

	private double YSPACING_BASE = 10;

	// Scalar-multiplied versions of the above
	private int XSPACING = 20;

	private int YSPACING = 10;

	// Used to render the background of the diagram panel
	// private GradientPaint gradient = null;

	// Is the layout changed?
	protected boolean layoutInvalid = true;

	/**
	 * Initialize the diagram renderer
	 */
	public StandardDiagramRenderer() {
		loadConfiguration();
	}

	/**
	 * Reload attributes from the configuration database.
	 */
	public void loadConfiguration() {
		// Load our initial dimensions from the configuration file
		WIDTH_BASE = Double.parseDouble(Configuration.getByKey("environment/buttonDimensions").get(1).toString());
		HEIGHT_BASE = Double.parseDouble(Configuration.getByKey("environment/buttonDimensions").get(2).toString());

		// Spacing factors between the nodes
		XSPACING_BASE = Integer.parseInt(Configuration.getByKey("environment/nodeSpacingBase").get(1).toString());
		YSPACING_BASE = Integer.parseInt(Configuration.getByKey("environment/nodeSpacingBase").get(2).toString());

		// Zoom level
		setZoomLevel(Double.parseDouble(Configuration.getByKey("environment/zoomLevelDefaultPercent").get(1).toString()) / 100);
	}

	/**
	 * Get our current zoom level
	 * 
	 * @return Current zoom level
	 */
	public double getZoomLevel() {
		return SCALAR;
	}

	/**
	 * Reset the X Spacing
	 * 
	 * @param value
	 *            New value
	 */
	public void setXSpacing(int value) {
		XSPACING = value;
	}

	/**
	 * Reset the Y spacing
	 * 
	 * @param value
	 *            New value
	 */
	public void setYSpacing(int value) {
		YSPACING = value;
	}

	/**
	 * Get the X spacing
	 * 
	 * @return X Spacing
	 */
	public int getXSpacing() {
		return XSPACING;
	}

	/**
	 * Get the Y spacing
	 * 
	 * @return Y spacing between diagram elements
	 */
	public int getYSpacing() {
		return YSPACING;
	}

	/**
	 * Get the width of the buttons on the diagram
	 * 
	 * @return Width of buttons
	 */
	public int getWidth() {
		return WIDTH;
	}

	/**
	 * Get the height of buttons on the diagram
	 * 
	 * @return height of buttons
	 */
	public int getHeight() {
		return WIDTH;
	}

	/**
	 * Reset the zoom level of the diagram
	 * 
	 * @param zoom
	 *            New zoom level
	 */
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
	public void paintDiagram(JDiagram diagram, Graphics g) {

		// Render this object with anti-aliasing turned on!
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Do we need to re-layout the nodes
		if (layoutInvalid) {
			layoutNodes(diagram, diagram.getRoot());
		}

		// Get our bounds
		Rectangle bounds = diagram.getBounds();

		// Get our gradiant colourJOptionsScreen configuration file
		Color top = Configuration.getRGB("colours/backgroundTop");
		Color bottom = Configuration.getRGB("colours/backgroundBottom");

		// Create our gradiant painting object and set it for our painter, and
		// fill the shape
		Paint gradientPaint = new GradientPaint(new Point2D.Double(0, 0), top, new Point2D.Double(bounds.width, bounds.height), bottom);
		g2d.setPaint(gradientPaint);
		g2d.fillRect(0, 0, bounds.width, bounds.height);

		// Paint the lines between the buttons usJOptionsScreennnecting line
		// colour
		g2d.setColor(Configuration.getRGB("colours/connectingLine"));
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
		Point rightMiddle = new Point(node.getBounds().x + node.getBounds().width, node.getBounds().y + (int) (0.5 * node.getBounds().height));

		// For grouping
		ArrayList currentGroup = null;
		Point topLeft = new Point(0, 0);
		Point bottomRight = new Point(0, 0);
		Color currentColour = null;

		// For each of our children, calculate their leftmost side's middle
		// point
		Iterator children = node.getChildren().iterator();
		while (children.hasNext()) {
			JTreeNode child = (JTreeNode) children.next();
			currentColour = child.getColour();

			// If we've changed to rendering a different group
			if (child.getGroup() != currentGroup) {
				// If we were rendering something before
				if (currentGroup != null) {
					if (currentGroup.size() > 1) {
						// Draw from topLeft to bottomRight
						g2d.setColor(child.getColour());
						g2d.setComposite(makeComposite(0.5f));
						g2d.fillRoundRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y, (int) (20 * SCALAR), (int) (20 * SCALAR));
						g2d.setColor(Configuration.getRGB("colours/connectingLine"));
						float[] dash = { 4.0f };
						Stroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash, 0.0f);
						g2d.setStroke(stroke);
						g2d.drawRoundRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y, (int) (20 * SCALAR), (int) (20 * SCALAR));
						g2d.setStroke(new BasicStroke(1.0f));

					}
				}

				currentGroup = child.getGroup();

				// topLeft = some point top left of this child
				topLeft.x = child.getBounds().x - 5;
				topLeft.y = child.getBounds().y - 5;

			} else {
				// bottomRight is the current nodes bottom right
				bottomRight.x = child.getBounds().x + WIDTH + 5;
				bottomRight.y = child.getBounds().y + HEIGHT + 5;
			}

			if (node.getRendered()) {
				Point leftMiddle = new Point(child.getBounds().x, child.getBounds().y + (int) (0.5 * child.getBounds().height));

				// Draw the three stages of the line
				g2d.drawLine(rightMiddle.x - 1, rightMiddle.y, rightMiddle.x + (XSPACING / 3) + 1, rightMiddle.y);
				g2d.drawLine(rightMiddle.x + (XSPACING / 3), rightMiddle.y, rightMiddle.x + (XSPACING / 3), leftMiddle.y);
				g2d.drawLine(leftMiddle.x - (2 * (XSPACING / 3)), leftMiddle.y, leftMiddle.x, leftMiddle.y);

				// Draw the arrowhead
				Polygon p = new Polygon();
				p.addPoint((int) (leftMiddle.x - (6 * SCALAR)), (int) (leftMiddle.y - (6 * SCALAR)));
				p.addPoint(leftMiddle.x, leftMiddle.y);
				p.addPoint((int) (leftMiddle.x - (6 * SCALAR)), (int) (leftMiddle.y + (6 * SCALAR)));
				p.addPoint((int) (leftMiddle.x - (3 * SCALAR)), leftMiddle.y);
				p.addPoint((int) (leftMiddle.x - (6 * SCALAR)), (int) (leftMiddle.y - (6 * SCALAR)));

				g2d.fillPolygon(p);
			}

			paintRecursively(g2d, child);
		}

		if (currentGroup != null) {
			if (currentGroup.size() > 1) {
				// Draw from topLeft to bottomRight
				g2d.setColor(currentColour);
				g2d.setComposite(makeComposite(0.5f));
				g2d.fillRoundRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y, (int) (20 * SCALAR), (int) (20 * SCALAR));
				g2d.setColor(Configuration.getRGB("colours/connectingLine"));
				float[] dash = { 4.0f };
				Stroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash, 0.0f);
				g2d.setStroke(stroke);
				g2d.drawRoundRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y, (int) (20 * SCALAR), (int) (20 * SCALAR));
				g2d.setStroke(new BasicStroke(1.0f));

			}
		}
	}

	/**
	 * Paint an individual tree node
	 */
	public void paintTreeNode(JTreeNode node, Graphics g) {
		// Make sure the ndoe is in the right state
		node.setSize(WIDTH, HEIGHT);
		node.setBorder(null);
		node.setDoubleBuffered(true);

		Graphics2D g2d = (Graphics2D) g;

		// Get our bounds
		Rectangle bounds = node.getBounds();

		// Set the font and colour
		Font mainFont = g2d.getFont();
		mainFont = mainFont.deriveFont(Font.PLAIN, (int) (12 * SCALAR));
		g2d.setFont(mainFont);
		g2d.setColor(node.getColour());
		g2d.setComposite(makeComposite(node.isValid() ? 1f : 0.3f));

		// Draw the background of this node
		g2d.fillRoundRect(0, 0, (int) bounds.getWidth() - 1, (int) bounds.getHeight() - 1, (int) (20 * SCALAR), (int) (20 * SCALAR));

		// Draw the border of this node using our configuration specified colour
		if (node.hasFocus()) {
			g2d.setColor(Configuration.getRGB("colours/buttonBorderFocused"));
			if (node.isValid())
				g2d.setStroke(new BasicStroke(5.0f));
			else {
				float[] dash = { 4.0f };
				Stroke stroke = new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash, 0.0f);
				g2d.setStroke(stroke);
			}
			g2d.drawRoundRect(0, 0, (int) bounds.getWidth() - 2, (int) bounds.getHeight() - 2, (int) (20 * SCALAR), (int) (20 * SCALAR));
		} else {
			g2d.setColor(Configuration.getRGB("colours/buttonBorder"));
			if (node.isValid())
				g2d.setStroke(new BasicStroke(1.0f));
			else {
				float[] dash = { 4.0f };
				Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash, 0.0f);
				g2d.setStroke(stroke);
			}
			g2d.drawRoundRect(0, 0, (int) bounds.getWidth() - 2, (int) bounds.getHeight() - 2, (int) (20 * SCALAR), (int) (20 * SCALAR));
		}

		// Truncate the text we'return drawing based on width
		String labelText = node.getTitle();
		if (labelText.length() > ((int) ((float) WIDTH / 7)))
			labelText = labelText.substring(0, (WIDTH / 7) - 3) + "...";

		// Draw our text
		g2d.setColor(Configuration.getRGB("colours/text"));
		g2d.drawString(labelText, (int) (10 * SCALAR), (int) (12 * SCALAR));

		// Draw our subText
		g2d.setColor(Configuration.getRGB("colours/subText"));
		Font subFont = mainFont.deriveFont(Font.ITALIC, (int) (11 * SCALAR));
		g2d.setFont(subFont);
		g2d.drawString(node.getSubTitle(), (int) (10 * SCALAR), (int) (25 * SCALAR));
	}

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

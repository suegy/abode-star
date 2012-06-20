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
 *
 * Some code here for dealing with tiling/cascading windows
 * is taken from Sun Microsystems code examples, to extend
 * functionality of JInternalFrames, but has been heavily 
 * adapted to deal with the object model of this application.
 */
package abode.visual;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultDesktopManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import model.IEditableElement;
import abode.AbodeUndoManager;
import abode.Configuration;
import abode.control.DotLapReader;
import abode.control.ILAPReader;
import abode.control.JManual;


/**
 * JAbode is the main GUI (i.e. the thing all the other windows exist within)
 * for the system, meaning that its purpose is little more than that of a shell,
 * with some basic interactions with elements within.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class JAbode extends JFrame {
	// Added to get rid of warnings and properly implement Serializable
	private static final long serialVersionUID = 1;

	// File parser objects for loading and saving files
	private static ArrayList alFileReader = null;

	/**
	 * Perform some first time initialization tasks
	 */
	static {
		alFileReader = new ArrayList();
		alFileReader.add(new DotLapReader());
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		checkDesktopSize();
	}

	private static int FRAME_OFFSET = 20;

	private MDIDesktopManager manager;

	private int PopoutHorizontal = 0;

	private int PopoutVertical = 0;

	private int VerticalSplit = 0;

	public Component add(JInternalFrame frame) {
		JInternalFrame[] array = desktop.getAllFrames();
		Point p;
		int w;
		int h;

		Component retval = super.add(frame);
		checkDesktopSize();
		if (array.length > 0) {
			p = array[0].getLocation();
			p.x = p.x + FRAME_OFFSET;
			p.y = p.y + FRAME_OFFSET;
		} else {
			p = new Point(0, 0);
		}
		frame.setLocation(p.x, p.y);
		if (frame.isResizable()) {
			w = getWidth() - (getWidth() / 3);
			h = getHeight() - (getHeight() / 3);
			if (w < frame.getMinimumSize().getWidth())
				w = (int) frame.getMinimumSize().getWidth();
			if (h < frame.getMinimumSize().getHeight())
				h = (int) frame.getMinimumSize().getHeight();
			frame.setSize(w, h);
		}
		desktop.moveToFront(frame);
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			frame.toBack();
		}
		return retval;
	}

	public void remove(Component c) {
		super.remove(c);
		checkDesktopSize();
	}

	/**
	 * Cascade all internal frames
	 */
	public void cascadeFrames() {
		int x = 0;
		int y = 0;
		JInternalFrame allFrames[] = desktop.getAllFrames();

		int verticalLocation = mainSplitpane.getDividerLocation();
		int horizontalLocation = sideSplitpane.getDividerLocation();

		manager.setNormalSize();
		int frameHeight = (getBounds().height - (getBounds().height - verticalLocation)) - allFrames.length * FRAME_OFFSET;
		int frameWidth = (getBounds().width - (getBounds().width - horizontalLocation)) - allFrames.length * FRAME_OFFSET;
		for (int i = allFrames.length - 1; i >= 0; i--) {
			allFrames[i].setSize(frameWidth, frameHeight);
			allFrames[i].setLocation(x, y);
			x = x + FRAME_OFFSET;
			y = y + FRAME_OFFSET;
		}
	}

	/**
	 * Tile all internal frames
	 */
	public void tileFrames() {
		java.awt.Component allFrames[] = desktop.getAllFrames();
		manager.setNormalSize();
		int verticalLocation = mainSplitpane.getDividerLocation();
		int horizontalLocation = sideSplitpane.getDividerLocation();

		int frameHeight = (getBounds().height - (getBounds().height - verticalLocation)) / allFrames.length;
		int y = 0;

		for (int i = 0; i < allFrames.length; i++) {
			allFrames[i].setSize((getBounds().width - (getBounds().width - horizontalLocation)), frameHeight);
			allFrames[i].setLocation(0, y);
			y = y + frameHeight;
		}
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given dimension.
	 */
	public void setAllSize(Dimension d) {
		desktop.setMinimumSize(d);
		desktop.setMaximumSize(d);
		desktop.setPreferredSize(d);
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given width and height.
	 */
	public void setAllSize(int width, int height) {
		setAllSize(new Dimension(width, height));
	}

	private void checkDesktopSize() {
		if (getParent() != null && isVisible())
			manager.resizeDesktop();
	}

	/**
	 * Initialize the window
	 */
	public JAbode() {
		// Netbeans code
		initComponents();

		// To maintain netbeans compatability, we must do evil unto code :(
		// TOOD: Look further at this and see if it can be removed now we're in Eclipse
		menubar.remove(helpMenu);
		menubar.remove(windowMenu);
		menubar.add(new WindowMenu(this, desktop));
		menubar.add(helpMenu);

		// Populate the engines list and load the most recently used list
		populateComboBox();
		refreshMRU();

		// Set our default size
		Dimension screen_resolution = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int screen_width = screen_resolution.width;
		int screen_height = screen_resolution.height;
		setSize(new Dimension(screen_width, screen_height - 30));

		manager = new MDIDesktopManager(this, desktop);
		desktop.setDesktopManager(manager);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		// Show us to the world
		setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		statusPanel = new JPanel();
		statusBar = new JTextArea();
		jTextArea2 = new JTextArea();
		jProgressBar1 = new JProgressBar();
		mainSplitpane = new JSplitPane();
		sideSplitpane = new JSplitPane();
		desktop = new JDesktopPane();
		innerSplitpane = new JSplitPane();
		propertiesPanel = new JPanel();
		propertiesTitle = new JLabel();
		//Documentation window
		documentationPanel = new JPanel();
		documentationTitle = new JLabel();
		documentationEditor = new JTextArea();
		documentationScroller = new JScrollPane();
		topSideSplitpane = new JSplitPane();
		
		tablePanel = new JPanel();
		propertiesPanelContents = new JPanel();
		commandsPanel = new JPanel();
		jLabel3 = new JLabel();
		comButtonPanel = new JPanel();
		outputPanel = new JPanel();
		outputLabel = new JLabel();
		outputTab = new JTabbedPane();
		jScrollPane1 = new JScrollPane();
		jTextArea1 = new JTextArea();
		jPanel1 = new JPanel();
		toolbar1 = new JToolBar();
		newButton = new JButton();
		openButton = new JButton();
		saveButton = new JButton();
		printButton = new JButton();
		jToolBar1 = new JToolBar();
		jLabel1 = new JLabel();
		jComboBox1 = new JComboBox();
		jButton1 = new JButton();
		menubar = new JMenuBar();
		fileMenu = new JMenu();
		fileMenuItem = new JMenuItem();
		openMenuItem = new JMenuItem();
		recentlyUsedMenu = new JMenu();
		saveMenuItem = new JMenuItem();
		saveAllMenuItem = new JMenuItem();
		saveAsMenuItem = new JMenuItem();
		jSeparator1 = new JSeparator();
		jMenuItem6 = new JMenuItem();
		viewMenu = new JMenu();
		jCheckBoxMenuItem1 = new JCheckBoxMenuItem();
		jCheckBoxMenuItem2 = new JCheckBoxMenuItem();
		jMenuItem4 = new JMenuItem();
		toolMenu = new JMenu();
		jMenuItem1 = new JMenuItem();
		windowMenu = new JMenu();
		helpMenu = new JMenu();
		jMenuItem2 = new JMenuItem();
		jMenuItem3 = new JMenuItem();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("A.B.O.D.E");
		setName("appFrame");
		statusPanel.setLayout(new java.awt.GridLayout(1, 0));

		statusBar.setBackground(UIManager.getDefaults().getColor("Button.background"));
		statusBar.setEditable(false);
		statusBar.setText("Ready");
		statusBar.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		statusPanel.add(statusBar);

		jTextArea2.setBackground(UIManager.getDefaults().getColor("Button.background"));
		jTextArea2.setEditable(false);
		jTextArea2.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		statusPanel.add(jTextArea2);

		jProgressBar1.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		statusPanel.add(jProgressBar1);

		getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

		mainSplitpane.setDividerLocation(2000);
		mainSplitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainSplitpane.setResizeWeight(0.5);
		mainSplitpane.setContinuousLayout(true);
		mainSplitpane.setMaximumSize(new java.awt.Dimension(400, 165));
		mainSplitpane.setOneTouchExpandable(true);
		sideSplitpane.setDividerLocation(2000);
		sideSplitpane.setResizeWeight(1.0);
		sideSplitpane.setContinuousLayout(true);
		sideSplitpane.setOneTouchExpandable(true);
		desktop.setBackground(new java.awt.Color(153, 153, 153));
		desktop.setToolTipText("Abode Workspace");
		desktop.setDoubleBuffered(true);
		sideSplitpane.setLeftComponent(desktop);

		innerSplitpane.setDividerLocation(300);
		innerSplitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		innerSplitpane.setResizeWeight(0.5);
		innerSplitpane.setContinuousLayout(true);
		innerSplitpane.setOneTouchExpandable(true);
		innerSplitpane.setPreferredSize(new java.awt.Dimension(302, 126));
		propertiesPanel.setLayout(new java.awt.BorderLayout());

		propertiesTitle.setFont(new java.awt.Font("MS Sans Serif", 1, 13));
		propertiesTitle.setIcon(new ImageIcon(getClass().getResource("/image/propertiesIcon.gif")));
		propertiesTitle.setLabelFor(propertiesPanel);
		propertiesTitle.setText("| Properties |");
		propertiesTitle.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		
		
		propertiesPanel.add(propertiesTitle, java.awt.BorderLayout.NORTH);

		tablePanel.setLayout(new java.awt.BorderLayout());

		tablePanel.add(propertiesPanelContents, java.awt.BorderLayout.CENTER);

		propertiesPanel.add(tablePanel, java.awt.BorderLayout.CENTER);

		innerSplitpane.setRightComponent(propertiesPanel);

		commandsPanel.setLayout(new java.awt.BorderLayout());

		jLabel3.setFont(new java.awt.Font("MS Sans Serif", 1, 13));
		jLabel3.setIcon(new ImageIcon(getClass().getResource("/image/toolIcon.gif")));
		jLabel3.setLabelFor(commandsPanel);
		jLabel3.setText("| Commands |");
		jLabel3.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		commandsPanel.add(jLabel3, java.awt.BorderLayout.NORTH);

		comButtonPanel.setLayout(new java.awt.GridLayout(0, 1));

		commandsPanel.add(comButtonPanel, java.awt.BorderLayout.CENTER);

		documentationPanel.setLayout(new BorderLayout());
		documentationTitle.setFont(new java.awt.Font("MS Sans Serif", 1, 13));
		documentationTitle.setIcon(new ImageIcon(getClass().getResource("/image/icon/document.gif")));
		documentationTitle.setLabelFor(documentationPanel);
		documentationTitle.setText("| Documentation |");
		documentationTitle.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		documentationEditor.setLineWrap(true);
		documentationEditor.setWrapStyleWord(true);
		documentationScroller = new JScrollPane(documentationEditor);
		documentationPanel.add(documentationTitle, BorderLayout.NORTH);
		documentationPanel.add(documentationScroller, BorderLayout.CENTER);

		topSideSplitpane.setDividerLocation(120);
		topSideSplitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		topSideSplitpane.setResizeWeight(0.5);
		topSideSplitpane.setContinuousLayout(true);
		topSideSplitpane.setOneTouchExpandable(true);
		topSideSplitpane.setPreferredSize(new java.awt.Dimension(302, 126));
		topSideSplitpane.setLeftComponent(commandsPanel);
		topSideSplitpane.setRightComponent(documentationPanel);
		
		jLabel3.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		
		
		innerSplitpane.setLeftComponent(topSideSplitpane);

		sideSplitpane.setRightComponent(innerSplitpane);

		mainSplitpane.setLeftComponent(sideSplitpane);

		outputPanel.setLayout(new java.awt.BorderLayout());

		outputLabel.setFont(new java.awt.Font("MS Sans Serif", 1, 13));
		outputLabel.setLabelFor(outputPanel);
		outputLabel.setText("| Output |");
		outputLabel.setToolTipText("Output");
		outputLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
		outputLabel.setVerticalTextPosition(SwingConstants.TOP);
		outputPanel.add(outputLabel, java.awt.BorderLayout.NORTH);

		outputTab.setName("outputTabs");
		jTextArea1.setEditable(false);
		jScrollPane1.setViewportView(jTextArea1);

		outputTab.addTab("Environment Notices", new ImageIcon(getClass().getResource("/image/globe_icon.gif")), jScrollPane1);

		outputPanel.add(outputTab, java.awt.BorderLayout.CENTER);

		mainSplitpane.setRightComponent(outputPanel);

		getContentPane().add(mainSplitpane, java.awt.BorderLayout.CENTER);

		jPanel1.setLayout(new java.awt.GridLayout(1, 0));

		toolbar1.setPreferredSize(new java.awt.Dimension(126, 25));
		newButton.setIcon(new ImageIcon(getClass().getResource("/image/new_document_32.gif")));
		newButton.setToolTipText("Create a new learnable action plan");
		newButton.setBorder(new EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
		newButton.setFocusPainted(false);
		newButton.setHorizontalAlignment(SwingConstants.LEFT);
		newButton.setMaximumSize(new java.awt.Dimension(25, 20));
		newButton.setMinimumSize(new java.awt.Dimension(20, 20));
		newButton.setPreferredSize(new java.awt.Dimension(35, 32));
		newButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newButtonActionPerformed(evt);
			}
		});

		toolbar1.add(newButton);

		openButton.setIcon(new ImageIcon(getClass().getResource("/image/open_document_32.gif")));
		openButton.setToolTipText("Open an existing learnable action plan");
		openButton.setBorder(null);
		openButton.setFocusPainted(false);
		openButton.setHorizontalAlignment(SwingConstants.LEFT);
		openButton.setMaximumSize(new java.awt.Dimension(30, 20));
		openButton.setMinimumSize(new java.awt.Dimension(20, 20));
		openButton.setVerticalAlignment(SwingConstants.TOP);
		openButton.setVerticalTextPosition(SwingConstants.TOP);
		openButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openButtonActionPerformed(evt);
			}
		});

		toolbar1.add(openButton);

		saveButton.setIcon(new ImageIcon(getClass().getResource("/image/save_32.gif")));
		saveButton.setToolTipText("Save the selected plan");
		saveButton.setBorder(null);
		saveButton.setFocusPainted(false);
		saveButton.setHorizontalAlignment(SwingConstants.LEFT);
		saveButton.setMaximumSize(new java.awt.Dimension(30, 20));
		saveButton.setMinimumSize(new java.awt.Dimension(20, 20));
		saveButton.setVerticalAlignment(SwingConstants.TOP);
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});

		toolbar1.add(saveButton);

		printButton.setIcon(new ImageIcon(getClass().getResource("/image/print_32.gif")));
		printButton.setToolTipText("Print the selected plan");
		printButton.setBorder(null);
		printButton.setFocusPainted(false);
		printButton.setHorizontalAlignment(SwingConstants.LEFT);
		printButton.setMaximumSize(new java.awt.Dimension(20, 20));
		printButton.setMinimumSize(new java.awt.Dimension(20, 20));
		printButton.setVerticalAlignment(SwingConstants.TOP);
		printButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				printButtonActionPerformed(evt);
			}
		});

		toolbar1.add(printButton);

		jPanel1.add(toolbar1);

		jToolBar1.setOpaque(false);
		jLabel1.setText("POSH Engine:  ");
		jToolBar1.add(jLabel1);

		jComboBox1.setMaximumSize(new java.awt.Dimension(150, 20));
		jToolBar1.add(jComboBox1);

		jButton1.setIcon(new ImageIcon(getClass().getResource("/image/icon_run.gif")));
		jButton1.setText("Run!");
		jButton1.setBorder(null);
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jToolBar1.add(jButton1);

		jPanel1.add(jToolBar1);

		getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

		fileMenu.setMnemonic('f');
		fileMenu.setText("File");
		fileMenu.setName("fileMenu");
		fileMenuItem.setMnemonic('n');
		fileMenuItem.setText("New");
		fileMenuItem.setName("fileMenuItem");
		fileMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fileMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(fileMenuItem);

		openMenuItem.setText("Open");
		openMenuItem.setName("openMenuItem");
		openMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(openMenuItem);

		recentlyUsedMenu.setText("Open Recent");
		fileMenu.add(recentlyUsedMenu);

		saveMenuItem.setText("Save");
		saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(saveMenuItem);

		saveAllMenuItem.setText("Save All");
		saveAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveAllMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(saveAllMenuItem);

		saveAsMenuItem.setText("Save As...");
		saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveAsMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(saveAsMenuItem);

		fileMenu.add(jSeparator1);

		menubar.add(fileMenu);

		JMenu menu = new JMenu();
		menu.setMnemonic('e');
		menu.setText("Edit");
		menu.setName("editMenu");
		
		JMenuItem menuItem = new JMenuItem();
		menuItem.setMnemonic('u');
		menuItem.setText("Undo");
		menuItem.setName("undoMenuItem");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
				AbodeUndoManager.getUndoManager().undo();
				}
				catch(CannotUndoException e){
					
				}
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem();
		menuItem.setMnemonic('r');
		menuItem.setText("Redo");
		menuItem.setName("redoMenuItem");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					AbodeUndoManager.getUndoManager().redo();
					}
					catch(CannotRedoException e){
						
					}
			}
		});
		menu.add(menuItem);
		menubar.add(menu);
		
		viewMenu.setMnemonic('v');
		viewMenu.setText("View");
		viewMenu.setName("viewMenu");
		jCheckBoxMenuItem1.setText("Output");
		jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxMenuItem1ActionPerformed(evt);
			}
		});

		viewMenu.add(jCheckBoxMenuItem1);

		jCheckBoxMenuItem2.setText("Commands/Properties");
		jCheckBoxMenuItem2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxMenuItem2ActionPerformed(evt);
			}
		});

		viewMenu.add(jCheckBoxMenuItem2);

		jMenuItem4.setText("Hide Validation");
		jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem4ActionPerformed(evt);
			}
		});

		viewMenu.add(jMenuItem4);

		menubar.add(viewMenu);

		toolMenu.setMnemonic('t');
		toolMenu.setText("Tools");
		jMenuItem1.setText("Options");
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem1ActionPerformed(evt);
			}
		});

		toolMenu.add(jMenuItem1);

		menubar.add(toolMenu);

		windowMenu.setText("Window");
		windowMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				windowMenuActionPerformed(evt);
			}
		});

		menubar.add(windowMenu);

		helpMenu.setMnemonic('h');
		helpMenu.setText("Help");
		helpMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				helpMenuActionPerformed(evt);
			}
		});

		jMenuItem2.setText("Manual");
		jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem2ActionPerformed(evt);
			}
		});

		helpMenu.add(jMenuItem2);

		jMenuItem3.setText("About");
		jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem3ActionPerformed(evt);
			}
		});

		helpMenu.add(jMenuItem3);

		menubar.add(helpMenu);

		setJMenuBar(menubar);
		pack();
	}
	// </editor-fold>//GEN-END:initComponents

	private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem4ActionPerformed
		JEditorWindow window = (JEditorWindow) desktop.getSelectedFrame();
		if (window != null)
			window.resetDiagrams();
	}// GEN-LAST:event_jMenuItem4ActionPerformed

	private void windowMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_windowMenuActionPerformed

	}// GEN-LAST:event_windowMenuActionPerformed

	private void helpMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_helpMenuActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_helpMenuActionPerformed

	/**
	 * Close the application
	 */
	private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem6ActionPerformed

		setVisible(false);
		dispose();
	}// GEN-LAST:event_jMenuItem6ActionPerformed

	private void jCheckBoxMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItem2ActionPerformed
		if (jCheckBoxMenuItem2.isSelected()) {
			popOutProperties();
		} else {
			hideProperties();
		}
	}// GEN-LAST:event_jCheckBoxMenuItem2ActionPerformed

	private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
		if (jCheckBoxMenuItem1.isSelected()) {
			popOutConsole();
		} else {
			hideConsole();
		}

	}// GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem2ActionPerformed
		JManual manual = new JManual();
		manual.show();
	}// GEN-LAST:event_jMenuItem2ActionPerformed

	private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem3ActionPerformed
		JAbout about = new JAbout();
		about.show();
	}// GEN-LAST:event_jMenuItem3ActionPerformed

	private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_printButtonActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_printButtonActionPerformed

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
		try {
			String command = jComboBox1.getSelectedItem().toString();
			command = command.substring(command.indexOf(',') + 1);

			if (desktop.getSelectedFrame() instanceof JEditorWindow) {
				JEditorWindow window = (JEditorWindow) desktop.getSelectedFrame();
				window.saveFile();
				if (window.fileName() != null)
					command = command.replaceAll("|file|", window.fileName());
			}

			Runtime.getRuntime().exec(command);
		} catch (IOException ioe) {
			System.out.println("IO Exception");
		}

	}// GEN-LAST:event_jButton1ActionPerformed

	private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem1ActionPerformed
		JOptionsScreen options = new JOptionsScreen(this);
		Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
		options.setLocation(SCREEN_SIZE.width / 2 - options.getWidth() / 2, SCREEN_SIZE.height / 2 - options.getHeight() / 2);
		setEnabled(false);
		options.show();
	}// GEN-LAST:event_jMenuItem1ActionPerformed

	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveButtonActionPerformed
		if (desktop.getSelectedFrame() instanceof JEditorWindow)
			((JEditorWindow) desktop.getSelectedFrame()).saveFile();
	}// GEN-LAST:event_saveButtonActionPerformed

	private void saveAllMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveAllMenuItemActionPerformed
		for (int x = 0; x < desktop.getAllFrames().length; x++) {
			((JEditorWindow) desktop.getAllFrames()[x]).saveFile();
		}
	}// GEN-LAST:event_saveAllMenuItemActionPerformed

	private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveAsMenuItemActionPerformed
		if (desktop.getSelectedFrame() instanceof JEditorWindow)
			((JEditorWindow) desktop.getSelectedFrame()).saveAs();
	}// GEN-LAST:event_saveAsMenuItemActionPerformed

	private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveMenuItemActionPerformed
		if (desktop.getSelectedFrame() instanceof JEditorWindow)
			((JEditorWindow) desktop.getSelectedFrame()).saveFile();
	}// GEN-LAST:event_saveMenuItemActionPerformed

	private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openMenuItemActionPerformed
		openButtonActionPerformed(evt);
	}// GEN-LAST:event_openMenuItemActionPerformed

	/**
	 * The user wants to create a new file, so give them a new window with
	 * nothing in it and a pre-selected default filename.
	 */
	private void newDocument() {
		popOutConsole();
		writeEnvironmentLine("New LAP file created");
		JEditorWindow internal = new JEditorWindow(this, null, null);
		desktop.add(internal);
		internal.toFront();
		internal.grabFocus();
		setStatus("Created new LAP");
	}

	private void fileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fileMenuItemActionPerformed
		newDocument();
	}// GEN-LAST:event_fileMenuItemActionPerformed

	private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openButtonActionPerformed
		String filename = "";
		JFileChooser fc = new JFileChooser(new java.io.File(filename));

		// Show open dialog; this method does not return until the dialog is
		// closed
		fc.showOpenDialog(this);

		// If no file was selected, don't bother loading it
		File selFile = fc.getSelectedFile();
		if (selFile == null)
			return;
		try {
			loadFile(selFile.getAbsolutePath());

		} catch (Exception e) {

		}
	}// GEN-LAST:event_openButtonActionPerformed

	/**
	 * Load the file specified
	 * 
	 * @param filePath
	 *            Path to file.
	 */
	private void loadFile(String filePath) throws Exception {
		popOutConsole();

		// Try to find a parser that can read this file!
		Iterator parserIterator = null;

		// For each parser in the file handler list
		try {
			boolean done = false;
			while (!done) {
				parserIterator = alFileReader.iterator();

				writeEnvironmentLine("Trying to load " + filePath);
				while (parserIterator.hasNext()) {
					ILAPReader current = (ILAPReader) parserIterator.next();
					// Can this parser read the file?
					if (current.canRead(filePath)) {
						// Create internal pane and load the file into it
						JEditorWindow internal = new JEditorWindow(this, filePath, current.load(filePath));
						desktop.add(internal);
						internal.toFront();
						internal.grabFocus();
						setStatus("Loaded " + filePath);

						ArrayList list = Configuration.getByKey("environment/recentlyUsed");

						// If we'return in the list, remove us
						String file = "\"" + filePath + "\"";
						if (list.contains(file))
							list.remove(file);

						// Add us (or re-add) at index 1
						list.add(1, file);

						// Remove everyone past 11
						while (list.size() > 11)
							list.remove(11);
						Configuration.update();

						// Refresh MRU list
						refreshMRU();

						done = true;
						break;
					}
				}

				if (done)
					writeEnvironmentLine("Loaded " + filePath);
				else {
					writeEnvironmentLine("Error Loading " + filePath);
					if (JOptionPane.showConfirmDialog(this, "The specified file could not be loaded/parsed. Do you wish to retry?", "Loading Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
						done = true;
				}
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error Loading File:" + e.toString());
			writeEnvironmentLine("Error Loading " + filePath);
			System.out.println("EXCEPTION\n");
			e.printStackTrace();
		}
	}

	private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newButtonActionPerformed
		newDocument();
	}// GEN-LAST:event_newButtonActionPerformed

	/**
	 * Refresh the most recently used list
	 */
	private void refreshMRU() {
		// Remove all sub-elements
		recentlyUsedMenu.removeAll();

		// Get the recently used list
		ArrayList list = Configuration.getByKey("environment/recentlyUsed");
		Iterator iterator = list.iterator();

		// Skip first element
		if (iterator.hasNext())
			iterator.next();

		int index = 0;
		while (iterator.hasNext()) {
			index++;
			JMenuItem item = new JMenuItem(iterator.next().toString().replaceAll("\"", ""));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					try {
						loadFile(((JMenuItem) evt.getSource()).getText());
					} catch (Exception e) {
					}
				}
			});
			recentlyUsedMenu.add(item);
		}
	}

	/**
	 * Populate the list of posh engines
	 */
	public void populateComboBox() {
		jComboBox1.removeAllItems();
		ArrayList al = Configuration.getByKey("environment/engines");
		Iterator it = al.iterator();

		// Skip "engines"
		if (it.hasNext())
			it.next();

		while (it.hasNext()) {
			jComboBox1.addItem(it.next().toString().replaceAll("\"", ""));
		}
	}

	private boolean HorizontalHidden = true;

	private boolean VerticalHidden = true;

	/**
	 * Show the properties panel
	 */
	public void popOutProperties() {
		if (PopoutHorizontal == 0) {
			Rectangle bounds = getBounds();
			PopoutHorizontal = 4 * (bounds.width / 5);
			VerticalSplit = innerSplitpane.getHeight() / 2;
		}

		if (HorizontalHidden) {
			HorizontalHidden = false;
			sideSplitpane.setDividerLocation(PopoutHorizontal);
			innerSplitpane.setDividerLocation(VerticalSplit);
		}
	}

	/**
	 * Show the console
	 */
	public void popOutConsole() {
		if (PopoutVertical == 0) {
			Rectangle bounds = getBounds();
			PopoutVertical = (int) 7 * (bounds.height / 10);
		}

		if (VerticalHidden) {
			VerticalHidden = false;
			mainSplitpane.setDividerLocation(PopoutVertical);
		}
	}

	/**
	 * Hide the console
	 */
	public void hideConsole() {
		if (!VerticalHidden) {
			jCheckBoxMenuItem1.setSelected(false);
			PopoutVertical = mainSplitpane.getDividerLocation();
			mainSplitpane.setDividerLocation(2000);
			VerticalHidden = true;
		}
	}

	/**
	 * Hide the properties panel
	 */
	public void hideProperties() {
		if (!HorizontalHidden) {
			jCheckBoxMenuItem2.setSelected(false);
			PopoutHorizontal = sideSplitpane.getDividerLocation();
			VerticalSplit = innerSplitpane.getDividerLocation();
			sideSplitpane.setDividerLocation(2000);
			HorizontalHidden = true;
		}
	}

	/**
	 * Add a named output tab to the console
	 * 
	 * @param title
	 *            Title of the tab
	 * @param outputScroll
	 *            Scrollpane for output
	 */
	public void addOutputTab(String title, JScrollPane outputScroll) {
		outputTab.add(title, outputScroll);
	}

	/**
	 * Remove a tab fro mthe list
	 * 
	 * @param outputScroll
	 *            Scrollpane to remove
	 */
	public void removeOutputTab(JScrollPane outputScroll) {
		outputTab.remove(outputScroll);
	}

	/**
	 * Give a specified tab the focus for a moment
	 * 
	 * @param outputScroll
	 *            Scrollpane to focus
	 */
	public void focusOutputTab(JScrollPane outputScroll) {
		outputTab.setSelectedComponent(outputScroll);
	}

	/**
	 * Get the right hand table for properties editing
	 * 
	 * @return Table for properties editor
	 */
	public JPanel getPropertiesPanelContents() {
		return propertiesPanelContents;
	}

	/**
	 * Get hold of the panel that holds the various action buttons above the
	 * properties panel.
	 * 
	 * @return Panel
	 */
	public JPanel getCommandsPanel() {
		return comButtonPanel;
	}

	/**
	 * Change our properties table to be another object
	 * 
	 * @param table
	 *            New table
	 */
	public void setPropertiesPanel(JPanel panel) {
		propertiesPanelContents = panel;
		tablePanel.removeAll();
		tablePanel.add(propertiesPanelContents);
		validate();
	}

	/**
	 * Set documentation field
	 */
	private IEditableElement currentEditingElement;
	public void setDocumentationField(IEditableElement newElement) {
		if (currentEditingElement != null) {
			currentEditingElement.setDocumentation(this.getDocumentationEditorContents());
		}
		documentationEditor.setText(newElement.getElementDocumentation());
		currentEditingElement = newElement;
	}
	
	/**
	 * Get the documentation field
	 */
	public String getDocumentationEditorContents() {
		return this.documentationEditor.getText();
	}
	
	/**
	 * Clear the properties table
	 */
	public void clearProperties() {
		tablePanel.removeAll();
	}

	/**
	 * Change status bar contents
	 * 
	 * @param status
	 *            New status bar text
	 */
	public void setStatus(String status) {
		statusBar.setText(status);
	}

	/**
	 * Write a line to the console about the environment state
	 * 
	 * @param text
	 *            New output to write
	 */
	public static void writeEnvironmentLine(String text) {
		jTextArea1.append(text + "\n");
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JPanel comButtonPanel;

	private JPanel commandsPanel;

	private JDesktopPane desktop;

	private JMenu fileMenu;

	private JMenuItem fileMenuItem;

	private JMenu helpMenu;

	private JSplitPane innerSplitpane;

	private JButton jButton1;

	private JCheckBoxMenuItem jCheckBoxMenuItem1;

	private JCheckBoxMenuItem jCheckBoxMenuItem2;

	private JComboBox jComboBox1;

	private JLabel jLabel1;

	private JLabel jLabel3;

	private JMenuItem jMenuItem1;

	private JMenuItem jMenuItem2;

	private JMenuItem jMenuItem3;

	private JMenuItem jMenuItem4;

	private JMenuItem jMenuItem6;

	private JPanel jPanel1;

	private JProgressBar jProgressBar1;

	private JScrollPane jScrollPane1;

	private JSeparator jSeparator1;

	private static JTextArea jTextArea1;

	private JTextArea jTextArea2;

	private JToolBar jToolBar1;

	public JSplitPane mainSplitpane;

	private JMenuBar menubar;

	private JButton newButton;

	private JButton openButton;

	private JMenuItem openMenuItem;

	private JLabel outputLabel;

	private JPanel outputPanel;

	private JTabbedPane outputTab;

	private JButton printButton;

	private JPanel propertiesPanel;

	private JPanel propertiesPanelContents;

	private JLabel propertiesTitle;
	
	private JPanel documentationPanel;
	
	private JLabel documentationTitle;
	
	private JTextArea documentationEditor;

	private JScrollPane documentationScroller;
	
	private JMenu recentlyUsedMenu;

	private JMenuItem saveAllMenuItem;

	private JMenuItem saveAsMenuItem;

	private JButton saveButton;

	private JMenuItem saveMenuItem;

	public JSplitPane sideSplitpane;
	
	public JSplitPane topSideSplitpane;

	private JTextArea statusBar;

	private JPanel statusPanel;

	private JPanel tablePanel;

	private JMenu toolMenu;

	private JToolBar toolbar1;

	private JMenu viewMenu;

	private JMenu windowMenu;
	// End of variables declaration//GEN-END:variables

}

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane.
 * Used to provide scrollbar functionality.
 */
class MDIDesktopManager extends DefaultDesktopManager {
	// Added to get rid of warnings and properly implement Serializable
	private static final long serialVersionUID = 1;

	private JDesktopPane desktop;

	private JAbode abode;

	public MDIDesktopManager(JAbode jabode, JDesktopPane desktop) {
		this.desktop = desktop;
		this.abode = jabode;
	}

	public void endResizingFrame(JComponent f) {
		super.endResizingFrame(f);
		resizeDesktop();
	}

	public void endDraggingFrame(JComponent f) {
		super.endDraggingFrame(f);
		resizeDesktop();
	}

	public void setNormalSize() {
		JScrollPane scrollPane = getScrollPane();
		int x = 0;
		int y = 0;
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			d.setSize(d.getWidth() - 20, d.getHeight() - 20);
			abode.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}

	private Insets getScrollPaneInsets() {
		return new Insets(0, 0, 0, 0);
	}

	private JScrollPane getScrollPane() {
		if (desktop.getParent() instanceof JViewport) {
			JViewport viewPort = (JViewport) desktop.getParent();
			if (viewPort.getParent() instanceof JScrollPane)
				return (JScrollPane) viewPort.getParent();
		}
		return null;
	}

	protected void resizeDesktop() {
		int x = 0;
		int y = 0;
		JScrollPane scrollPane = getScrollPane();
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			JInternalFrame allFrames[] = desktop.getAllFrames();
			for (int i = 0; i < allFrames.length; i++) {
				if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
					x = allFrames[i].getX() + allFrames[i].getWidth();
				}
				if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
					y = allFrames[i].getY() + allFrames[i].getHeight();
				}
			}
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			if (x <= d.getWidth())
				x = ((int) d.getWidth()) - 20;
			if (y <= d.getHeight())
				y = ((int) d.getHeight()) - 20;
			abode.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}
}

/**
 * Menu component that handles the functionality expected of a standard
 * "Windows" menu for MDI applications.
 */
class WindowMenu extends JMenu {
	// Added to get rid of warnings and properly implement Serializable
	private static final long serialVersionUID = 1;

	private final JAbode abode;

	private final JDesktopPane desktop;

	private JMenuItem cascade = new JMenuItem("Cascade");

	private JMenuItem tile = new JMenuItem("Tile");

	private JMenuItem min = new JMenuItem("Minimize All");

	public WindowMenu(final JAbode abode, final JDesktopPane desktop) {
		this.desktop = desktop;
		this.abode = abode;

		setText("Window");
		cascade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				WindowMenu.this.abode.cascadeFrames();
			}
		});

		tile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				WindowMenu.this.abode.tileFrames();
			}
		});

		min.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JInternalFrame[] frames = desktop.getAllFrames();
				for (int x = 0; x < frames.length; x++)
					try {
						frames[x].setIcon(true);
					} catch (Exception e) {
						// Nevermind!
					}
			}
		});

		addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}

			public void menuDeselected(MenuEvent e) {
				removeAll();
			}

			public void menuSelected(MenuEvent e) {
				buildChildMenus();
			}
		});
	}

	/* Sets up the children menus depending on the current desktop state */
	private void buildChildMenus() {
		int i;
		ChildMenuItem menu;
		JInternalFrame[] array = desktop.getAllFrames();

		add(cascade);
		add(tile);
		add(min);

		if (array.length > 0)
			addSeparator();
		cascade.setEnabled(array.length > 0);
		tile.setEnabled(array.length > 0);

		for (i = 0; i < array.length; i++) {
			menu = new ChildMenuItem(array[i]);
			menu.setState(i == 0);
			menu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JInternalFrame frame = ((ChildMenuItem) ae.getSource()).getFrame();
					frame.moveToFront();
					try {
						frame.setSelected(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			});
			menu.setIcon(array[i].getFrameIcon());
			add(menu);
		}
	}

	/*
	 * This JCheckBoxMenuItem descendant is used to track the child frame that
	 * corresponds to a give menu.
	 */
	class ChildMenuItem extends JCheckBoxMenuItem {

		// Added to get rid of warnings and properly implement Serializable
		private static final long serialVersionUID = 1;

		private JInternalFrame frame;

		public ChildMenuItem(JInternalFrame frame) {
			super(frame.getTitle());
			this.frame = frame;
		}

		public JInternalFrame getFrame() {
			return frame;
		}
	}
}
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


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.WindowConstants;

import model.IEditableElement;
import model.posh.ActionElement;
import model.posh.ActionPattern;
import model.posh.Competence;
import model.posh.CompetenceElement;
import model.posh.DriveCollection;
import model.posh.DriveElement;
import model.posh.LearnableActionPattern;
import abode.JAbode;
import abode.control.ConsoleWriter;
import abode.control.DotLapReader;
import abode.control.DotLapWriter;
import abode.control.ILapWriter;
import abode.control.IPrimitiveReader;
import abode.control.PrimitiveManager;
import abode.control.PrintUtilities;
import abode.editing.PrintDiagramRenderer;
import abode.editing.StandardDiagramRenderer;

/**
 * The JEditorWindow is where we edit files.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class JEditorWindow extends JInternalFrame{

	// Added to properly implement serializable
	private static final long serialVersionUID = 1;

	// For counting validation errors
	private static int errorCount = 0;

	// For counting new files
	private static int iNewFiles = 0;

	// The LearnableActionPattern that this window is currently editor for.
	private LearnableActionPattern lapCurrent = null;

	// Absolute path of the file we're using
	private String strFileName = null;

	private JScrollPane outputScroll = new JScrollPane();

	private JEditorPane output = new JEditorPane();

	private String outputBuffer = "";

	// The four diagrams we are displaying
	private JDiagram diagramOverview = null;

	private JDiagram diagramActionPatterns = null;

	private JDiagram diagramCompetences = null;

	private JDiagram diagramDriveCollections = null;

	private JDiagram printDiagram = null;

	private JDiagram logicDiagram = null;

	// Primitives and senses
	private ArrayList primitiveSenses = new ArrayList();

	private ArrayList primitiveActions = new ArrayList();

	// The frame that hosts us
	private JAbode mainFrame = null;

	/** Creates new form guiInternal */
	public JEditorWindow(JAbode main, String fileName, LearnableActionPattern pattern) {
		initComponents();
		mainFrame = main;


		
		setSize((int) (getMainFrame().getWidth() * 0.79), (int) (getMainFrame().getHeight() * 0.69));

		// Set the window title
		if (fileName == null)
			setTitle("Untitled" + (++iNewFiles) + ".lap");
		else
			setTitle(strFileName = fileName);

		// If we're loading a file, then store that reference, otherwise create
		// a new, blank learnableactionpattern object ready for editing! 
		if (pattern != null) {
			lapCurrent = pattern;
			sourceArea.setText((new DotLapReader()).getFileContents(fileName));
		} else {
			lapCurrent = new LearnableActionPattern();
			lapCurrent.getElements().add(new DriveCollection("NewDriveCollection", false, new ArrayList(), new ArrayList()));
			sourceArea.setText("You must save your current working file to see the generated source code.");
			pattern=lapCurrent;
		}
		
		pattern.printElements();

		// Load documentation
		txtTitle.setText(lapCurrent.getDocumentation().getTitle());
		txtAuthor.setText(lapCurrent.getDocumentation().getAuthor());
		txtMemo.setText(lapCurrent.getDocumentation().getMemo());

		// Add diagram panels
		addDiagrams();

		// add the output textArea to the main gui
		output.setEditable(false);
		output.setContentType("text/html");
		outputScroll.setViewportView(output);
		mainFrame.addOutputTab(getTitle(), outputScroll);

		// Add the line counter for the source code pane and set the highlighter
		countLines();
		CurrentLineHighlighter.install(sourceArea);
		
		// Set the default option when closing to do nothing
		// We want the parent JAbode object to deal with this
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Save the file
	 */
	public void saveFile() {
		// If we've not got a name yet, then use save-as instead
		if (strFileName == null) {
			saveAs();
			return;
		}

		ILapWriter writer = new DotLapWriter();
		//ILapWriter writer = new CommentedLAPWriter();
		writer.save(strFileName, getLearnableActionPattern());
		
		// Once saved the file, load it in the source code view
		sourceArea.setText((new DotLapReader()).getFileContents(strFileName));
	}

	/**
	 * Get the name of the file we're saved as.
	 */
	public String fileName() {
		return strFileName;
	}

	/**
	 * Save a file. This code isnt modular, and really should be in order to
	 * support new I/O formats for LAP files.
	 */
	public void saveAs() {
		// Save this file
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save " + title + " as...");
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File pathName) {
				if (pathName.getPath().toLowerCase().indexOf(".lap") > 0)
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return "Learnable Action Pattern Files (.lap)";
			}
		});

		int returnedValue = chooser.showSaveDialog(this);
		if (returnedValue == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			strFileName = file.getPath();
			setTitle(strFileName);
			saveFile();
		}
	}

	/**
	 * Get the instance of the frame that hosts us
	 * 
	 * @return Outer frame housing this editor window
	 */
	public JAbode getMainFrame() {
		return mainFrame;
	}

	/**
	 * Get the LAP object for this frame
	 * 
	 * @return Current object model
	 */
	public LearnableActionPattern getLearnableActionPattern() {
		return lapCurrent;
	}

	/**
	 * Add some text to the console output
	 * 
	 * @param text
	 *            Text to add
	 */
	public void addOutputBuffer(String text) {
		outputBuffer += text;
	}

	/**
	 * Get the current contents of the console output buffer
	 * 
	 * @return Console output buffer contents
	 */
	public String getOutputBuffer() {
		return outputBuffer;
	}

	/**
	 * Clear the console output buffer
	 */
	public void clearOutputBuffer() {
		outputBuffer = "";
	}

	/**
	 * Simple thunk for populating the diagram panels and tracking the instances
	 * as local class variables.
	 */
	private void addDiagrams() {
		// Create some truncated trees
		JTreeNode overviewTree = getLearnableActionPattern().toOverviewTree();
		overviewTree.rewrite();
		JTreeNode printTree = getLearnableActionPattern().toOverviewTree();
		printTree.rewrite();
		JTreeNode logicTree = getLearnableActionPattern().toLogicTree();
		logicTree.rewrite();

		// Initialise the diagrams
		diagramOverview = new JDiagram(this, overviewTree, new StandardDiagramRenderer(), true, false);
		diagramActionPatterns = new JDiagram(this, getLearnableActionPattern().toActionTree(), new StandardDiagramRenderer(), false, true);
		diagramCompetences = new JDiagram(this, getLearnableActionPattern().toCompetenceTree(), new StandardDiagramRenderer(), false, true);
		diagramDriveCollections = new JDiagram(this, getLearnableActionPattern().toDriveTree(), new StandardDiagramRenderer(), false, true);
		printDiagram = new JDiagram(this, printTree, new PrintDiagramRenderer(), true, false);
		logicDiagram = new JDiagram(this, logicTree, new StandardDiagramRenderer(), true, true);

		// Set the preferred dimensions
		diagramOverview.setPreferredSize(diagramOverview.getDimensions());
		diagramActionPatterns.setPreferredSize(diagramActionPatterns.getDimensions());
		diagramCompetences.setPreferredSize(diagramCompetences.getDimensions());
		diagramDriveCollections.setPreferredSize(diagramDriveCollections.getDimensions());
		printDiagram.setPreferredSize(printDiagram.getDimensions());
		logicDiagram.setPreferredSize(logicDiagram.getDimensions());

		// Add each to their respective scrollable view panes
		overviewPane.setViewportView(diagramOverview);
		actionPatternScrollPane.setViewportView(diagramActionPatterns);
		competencesScrollPane.setViewportView(diagramCompetences);
		driveScrollPane.setViewportView(diagramDriveCollections);
		printScrollPane.setViewportView(printDiagram);
		logicPane.setViewportView(logicDiagram);
	}

	/**
	 * Update the line counter on the left of the source pane by adding numbers
	 * for each line along with a line feed. Updates on every keypress, but
	 * appears to be fast enough.
	 */
	public void countLines() {
		countArea.setText("");
		for (int x = 1; x <= sourceArea.getLineCount(); x++)
			countArea.append("" + x + "\n");
	}

	public JEditorPane getOutput() {
		return output;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		jCheckBox1 = new javax.swing.JCheckBox();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		internalTabs = new javax.swing.JTabbedPane();
		overviewPane = new javax.swing.JScrollPane();
		logicPane = new javax.swing.JScrollPane();
		actionPatternScrollPane = new javax.swing.JScrollPane();
		competencesScrollPane = new javax.swing.JScrollPane();
		driveScrollPane = new javax.swing.JScrollPane();
		printScrollPane = new javax.swing.JScrollPane();
		sourceScrollPane = new javax.swing.JScrollPane();
		sourcePanel = new javax.swing.JPanel();
		sourceArea = new javax.swing.JTextArea();
		countArea = new javax.swing.JTextArea();
		commentsPane = new javax.swing.JScrollPane();
		jPanel2 = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		txtTitle = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		txtAuthor = new javax.swing.JTextField();
		jPanel3 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		txtMemo = new javax.swing.JTextPane();
		buttonPanel = new javax.swing.JPanel();
		jToolBar1 = new javax.swing.JToolBar();
		bttnZoomIn = new javax.swing.JButton();
		bttnZoomOut = new javax.swing.JButton();
		bttnValidate = new javax.swing.JButton();
		bttnListPrims = new javax.swing.JButton();
		bttnExport = new javax.swing.JButton();
		bttnPrint = new javax.swing.JButton();
		jMenu1.setText("Menu");
		jMenuBar1.add(jMenu1);

		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setTitle("Learnable Action Plan Editor - untitled.lap");
		setMinimumSize(new java.awt.Dimension(400, 400));
		setNormalBounds(new java.awt.Rectangle(0, 0, 400, 0));
		setPreferredSize(new java.awt.Dimension(400, 400));
		try {
			setSelected(true);
		} catch (java.beans.PropertyVetoException e1) {
			e1.printStackTrace();
		}
		setVisible(true);
		addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
			@Override
			public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
				formInternalFrameActivated(evt);
			}

			@Override
			public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
				formInternalFrameClosed(evt);
			}

			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
			}

			@Override
			public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
			}

			@Override
			public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
			}

			@Override
			public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
			}

			@Override
			public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
			}
		});

		overviewPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				overviewPaneComponentShown(evt);
			}
		});

		internalTabs.addTab("Overview", new javax.swing.ImageIcon(getClass().getResource("/image/diagramIcon.gif")), overviewPane);

		logicPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				logicPaneComponentShown(evt);
			}
		});

		internalTabs.addTab("Logical View", new javax.swing.ImageIcon(getClass().getResource("/image/pictureIcon.gif")), logicPane);

		actionPatternScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				actionPatternScrollPaneComponentShown(evt);
			}
		});

		internalTabs.addTab("Action Patterns", new javax.swing.ImageIcon(getClass().getResource("/image/icon/actionpat.png")), actionPatternScrollPane);

		competencesScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				competencesScrollPaneComponentShown(evt);
			}
		});

		internalTabs.addTab("Competences", new javax.swing.ImageIcon(getClass().getResource("/image/icon/competence.png")), competencesScrollPane);

		driveScrollPane.setBackground(new java.awt.Color(255, 255, 255));
		driveScrollPane.setAutoscrolls(true);
		driveScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				driveScrollPaneComponentShown(evt);
			}
		});

		internalTabs.addTab("Drive Collections", new javax.swing.ImageIcon(getClass().getResource("/image/icon/drivecoll.gif")), driveScrollPane);

		printScrollPane.setBackground(new java.awt.Color(102, 102, 102));
		printScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				printScrollPaneComponentShown(evt);
			}
		});

		internalTabs.addTab("Print View", new javax.swing.ImageIcon(getClass().getResource("/image/printer-icon-small.gif")), printScrollPane, "Print-friendly diagram");

		sourceScrollPane.setBackground(new java.awt.Color(255, 255, 255));
		sourceScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentHidden(java.awt.event.ComponentEvent evt) {
				sourceScrollPaneComponentHidden(evt);
			}

			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				sourceScrollPaneComponentShown(evt);
			}
		});

		sourcePanel.setLayout(new java.awt.BorderLayout());

		sourceArea.setEditable(false);
		sourceArea.setFont(new java.awt.Font("MonoSpaced", 0, 14));
		sourceArea.setForeground(new java.awt.Color(102, 102, 255));
		sourceArea.setToolTipText("You may not edit the source of a file.");
		sourceArea.setCaretColor(new java.awt.Color(255, 102, 102));
		sourcePanel.add(sourceArea, java.awt.BorderLayout.CENTER);

		countArea.setBackground(new java.awt.Color(204, 204, 204));
		countArea.setEditable(false);
		countArea.setFont(new java.awt.Font("MonoSpaced", 0, 14));
		sourcePanel.add(countArea, java.awt.BorderLayout.WEST);

		sourceScrollPane.setViewportView(sourcePanel);

		internalTabs.addTab("Source", new javax.swing.ImageIcon(getClass().getResource("/image/source.gif")), sourceScrollPane, "LAP Source Code");

		commentsPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentHidden(java.awt.event.ComponentEvent evt) {
				commentsPaneComponentHidden(evt);
			}

			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				commentsPaneComponentShown(evt);
			}
		});

		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

		jLabel1.setText("Title:");
		jPanel1.add(jLabel1);

		txtTitle.setMinimumSize(new java.awt.Dimension(11, 11));
		txtTitle.setPreferredSize(new java.awt.Dimension(150, 20));
		txtTitle.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				txtTitleActionPerformed(evt);
			}
		});

		jPanel1.add(txtTitle);

		jLabel2.setText("Author:");
		jPanel1.add(jLabel2);

		txtAuthor.setPreferredSize(new java.awt.Dimension(150, 20));
		txtAuthor.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				txtAuthorActionPerformed(evt);
			}
		});

		jPanel1.add(txtAuthor);

		jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

		jPanel3.setLayout(new java.awt.BorderLayout());

		jLabel3.setText("Comments:");
		jPanel3.add(jLabel3, java.awt.BorderLayout.NORTH);

		txtMemo.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyTyped(java.awt.event.KeyEvent evt) {
				txtMemoKeyTyped(evt);
			}
		});

		jPanel3.add(txtMemo, java.awt.BorderLayout.CENTER);

		jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

		commentsPane.setViewportView(jPanel2);

		internalTabs.addTab("Documentation", new javax.swing.ImageIcon(getClass().getResource("/image/icon/document.gif")), commentsPane, "Comments for this file");

		getContentPane().add(internalTabs, java.awt.BorderLayout.CENTER);

		buttonPanel.setLayout(new java.awt.GridLayout(1, 0));

		buttonPanel.setMinimumSize(new java.awt.Dimension(500, 22));
		bttnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/zoomin_small.gif")));
		bttnZoomIn.setText("Zoom In");
		bttnZoomIn.setToolTipText("Zoom In");
		bttnZoomIn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		bttnZoomIn.setFocusPainted(false);
		bttnZoomIn.setMaximumSize(new java.awt.Dimension(250, 38));
		bttnZoomIn.setMinimumSize(new java.awt.Dimension(20, 20));
		bttnZoomIn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				bttnZoomInActionPerformed(evt);
			}
		});

		jToolBar1.add(bttnZoomIn);

		bttnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/zoomout_small.gif")));
		bttnZoomOut.setText("Zoom Out");
		bttnZoomOut.setToolTipText("Zoom Out");
		bttnZoomOut.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		bttnZoomOut.setFocusPainted(false);
		bttnZoomOut.setMaximumSize(new java.awt.Dimension(250, 38));
		bttnZoomOut.setMinimumSize(new java.awt.Dimension(20, 20));
		bttnZoomOut.setPreferredSize(new java.awt.Dimension(100, 32));
		bttnZoomOut.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				bttnZoomOutActionPerformed(evt);
			}
		});

		jToolBar1.add(bttnZoomOut);

		bttnValidate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/properties_doc_32.gif")));
		bttnValidate.setText("Validate Primitives");
		bttnValidate.setToolTipText("Validate against primitives file");
		bttnValidate.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		bttnValidate.setFocusPainted(false);
		bttnValidate.setMaximumSize(new java.awt.Dimension(250, 38));
		bttnValidate.setMinimumSize(new java.awt.Dimension(20, 20));
		bttnValidate.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				bttnValidateActionPerformed(evt);
			}
		});

		// TODO: Removed for now, as i'm not convinced this actually works
//		jToolBar1.add(bttnValidate);

		bttnListPrims.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/copy_clipboard_32.gif")));
		bttnListPrims.setText("List Primitives");
		bttnListPrims.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		bttnListPrims.setMaximumSize(new java.awt.Dimension(250, 38));
		bttnListPrims.setPreferredSize(new java.awt.Dimension(129, 38));
		bttnListPrims.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				bttnListPrimsActionPerformed(evt);
			}
		});

		jToolBar1.add(bttnListPrims);

		bttnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/pictureIcon.gif")));
		bttnExport.setText("Export to Image");
		bttnExport.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		bttnExport.setMaximumSize(new java.awt.Dimension(250, 38));
		bttnExport.setMinimumSize(new java.awt.Dimension(81, 30));
		bttnExport.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				bttnExportActionPerformed(evt);
			}
		});

		jToolBar1.add(bttnExport);

		bttnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/print_32.gif")));
		bttnPrint.setText("Print Diagram");
		bttnPrint.setToolTipText("Print the current diagram");
		bttnPrint.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		bttnPrint.setFocusPainted(false);
		bttnPrint.setMaximumSize(new java.awt.Dimension(250, 38));
		bttnPrint.setMinimumSize(new java.awt.Dimension(20, 20));
		bttnPrint.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				bttnPrintActionPerformed(evt);
			}
		});

		jToolBar1.add(bttnPrint);

		buttonPanel.add(jToolBar1);

		getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void bttnListPrimsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bttnListPrimsActionPerformed
		// Dump a list of the primitives used to the console

		ArrayList actionList = getListOfActions();
		ArrayList senseList = getListOfSenses();
		
		addOutputBuffer("<b>Action Primitives Used:</b><ul>");

		Iterator actions = actionList.iterator();
		while (actions.hasNext())
			addOutputBuffer("<li>" + actions.next().toString());

		addOutputBuffer("</ul><b>Sense Primitives Used:</b><ul>");

		Iterator senses = senseList.iterator();
		while (senses.hasNext())
			addOutputBuffer("<li>" + senses.next().toString());
		addOutputBuffer("</ul>");

		ConsoleWriter.writeLine(getOutputBuffer(), this);
		clearOutputBuffer();
		// Open the console window to show this
		mainFrame.popOutConsole();
	}// GEN-LAST:event_bttnListPrimsActionPerformed

	private void logicPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_logicPaneComponentShown
		logicPane.repaint();
	}// GEN-LAST:event_logicPaneComponentShown

	private void commentsPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_commentsPaneComponentShown
		bttnZoomIn.setEnabled(false);
		bttnZoomOut.setEnabled(false);
		bttnPrint.setEnabled(false);
		bttnExport.setEnabled(false);
	}// GEN-LAST:event_commentsPaneComponentShown

	private void commentsPaneComponentHidden(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_commentsPaneComponentHidden
		bttnZoomIn.setEnabled(true);
		bttnZoomOut.setEnabled(true);
		bttnPrint.setEnabled(true);
		bttnExport.setEnabled(true);
	}// GEN-LAST:event_commentsPaneComponentHidden

	private void sourceScrollPaneComponentHidden(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_sourceScrollPaneComponentHidden
		bttnZoomIn.setEnabled(true);
		bttnZoomOut.setEnabled(true);
		bttnPrint.setEnabled(true);
		bttnExport.setEnabled(true);
	}// GEN-LAST:event_sourceScrollPaneComponentHidden

	private void sourceScrollPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_sourceScrollPaneComponentShown
		bttnZoomIn.setEnabled(false);
		bttnZoomOut.setEnabled(false);
		bttnPrint.setEnabled(false);
		bttnExport.setEnabled(false);
	}// GEN-LAST:event_sourceScrollPaneComponentShown

	private void bttnExportActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bttnExportActionPerformed
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save " + title + " as...");
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File pathName) {
				if (pathName.getPath().toLowerCase().indexOf(".png") > 0)
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return "PNG Image Files (.PNG)";
			}
		});

		int returnedValue = chooser.showSaveDialog(this);
		if (returnedValue == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			strFileName = file.getPath();
			if (strFileName.toLowerCase().indexOf(".png") < 5) {
				strFileName += ".png";
				file = new File(strFileName);
			}

			JScrollPane pane = (JScrollPane) internalTabs.getSelectedComponent();
			JDiagram diagram = (JDiagram) pane.getViewport().getView();

			// Create our buffered image
			BufferedImage bufferedImage = new BufferedImage((int) diagram.getDimensions().getWidth(), (int) diagram.getDimensions().getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D imageWriterGraphics = bufferedImage.createGraphics();

			diagram.getRenderer().paintDiagram(diagram, imageWriterGraphics);
			diagram.paintAll(imageWriterGraphics);

			// Write generated image to a file
			try {
				// Save as PNG
				// File file = new File(strFileName);
				ImageIO.write(bufferedImage, "png", file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "There was an I/O error saving the image to the specified location.");
			}
		}
	}// GEN-LAST:event_bttnExportActionPerformed

	private void overviewPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_overviewPaneComponentShown
		overviewPane.repaint();
	}// GEN-LAST:event_overviewPaneComponentShown

	private void actionPatternScrollPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_actionPatternScrollPaneComponentShown
		actionPatternScrollPane.repaint();
	}// GEN-LAST:event_actionPatternScrollPaneComponentShown

	private void competencesScrollPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_competencesScrollPaneComponentShown
		competencesScrollPane.repaint();
	}// GEN-LAST:event_competencesScrollPaneComponentShown

	private void driveScrollPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_driveScrollPaneComponentShown
		driveScrollPane.repaint();
	}// GEN-LAST:event_driveScrollPaneComponentShown

	private void printScrollPaneComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_printScrollPaneComponentShown
		printScrollPane.repaint();
	}// GEN-LAST:event_printScrollPaneComponentShown

	private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {// GEN-FIRST:event_formInternalFrameActivated
		mainFrame.focusOutputTab(outputScroll);
	}// GEN-LAST:event_formInternalFrameActivated

	private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {// GEN-FIRST:event_formInternalFrameClosed
		mainFrame.removeOutputTab(outputScroll);
	}// GEN-LAST:event_formInternalFrameClosed

	private void bttnValidateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bttnValidateActionPerformed
		errorCount = 0;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select Primitives File to Load");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			IPrimitiveReader reader = PrimitiveManager.getPrimitiveReader(chooser.getSelectedFile().getAbsolutePath());

			if (reader == null) {
				JOptionPane.showMessageDialog(this, "The specified file contains no primitives, or was not readable.");
			} else {
				// Show the output console
				getMainFrame().popOutConsole();

				// Read senses and actions from the files
				primitiveActions = reader.getActions(chooser.getSelectedFile().getAbsolutePath());
				primitiveSenses = reader.getSenses(chooser.getSelectedFile().getAbsolutePath());

				// Validate overview diagram
				addOutputBuffer("<font style=color:blue><b>Validating Overview: </b></font><BR>");
				diagramOverview.validate(getLearnableActionPattern(), primitiveActions, primitiveSenses);
				ConsoleWriter.writeLine(getOutputBuffer(), this);
				// clearOutputBuffer();

				// Validatw diagram
				addOutputBuffer("<font style=color:blue><b>Validating Logical View: </b></font><BR>");
				logicDiagram.validate(getLearnableActionPattern(), primitiveActions, primitiveSenses);
				ConsoleWriter.writeLine(getOutputBuffer(), this);
				// clearOutputBuffer();

				// Validate the action patterns view
				addOutputBuffer("<font style=color:blue><b>Validating Action Patterns: </b></font><BR>");
				diagramActionPatterns.validate(getLearnableActionPattern(), primitiveActions, primitiveSenses);
				ConsoleWriter.writeLine(getOutputBuffer(), this);
				// clearOutputBuffer();

				// Validate the competences view
				addOutputBuffer("<font style=color:blue><b>Validating Competences: </b></font><BR>");
				diagramCompetences.validate(getLearnableActionPattern(), primitiveActions, primitiveSenses);
				ConsoleWriter.writeLine(getOutputBuffer(), this);
				// clearOutputBuffer();

				// Validate the drive collections view
				addOutputBuffer("<font style=color:blue><b>Validating Drive Collections: </b></font><BR>");
				diagramDriveCollections.validate(getLearnableActionPattern(), primitiveActions, primitiveSenses);
				ConsoleWriter.writeLine(getOutputBuffer(), this);
				// clearOutputBuffer();

				// We'return done
				addOutputBuffer("<font style=color:green><b>Validation against " + chooser.getSelectedFile().getName() + " complete: </b></font>" + errorCount + " errors<BR>");
				ConsoleWriter.writeLine(getOutputBuffer(), this);
				clearOutputBuffer();
				// Open the console to show the results
				mainFrame.popOutConsole();
			}
		}
	}// GEN-LAST:event_bttnValidateActionPerformed

	/**
	 * Keep track when an error occurs
	 */
	public void addError() {
		errorCount++;
	}

	private void bttnPrintActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bttnPrintActionPerformed
		JScrollPane pane = (JScrollPane) internalTabs.getSelectedComponent();
		JViewport view = pane.getViewport();
		JDiagram diagram = (JDiagram) view.getView();

		PrintUtilities.printComponent(diagram);
	}// GEN-LAST:event_bttnPrintActionPerformed

	private void txtMemoKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtMemoKeyTyped
		getLearnableActionPattern().getDocumentation().setMemo(txtMemo.getText());
	}// GEN-LAST:event_txtMemoKeyTyped

	private void txtAuthorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtAuthorActionPerformed
		getLearnableActionPattern().getDocumentation().setAuthor(txtAuthor.getText());
	}// GEN-LAST:event_txtAuthorActionPerformed

	private void txtTitleActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtTitleActionPerformed
		getLearnableActionPattern().getDocumentation().setTitle(txtTitle.getText());
	}// GEN-LAST:event_txtTitleActionPerformed

	private void bttnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bttnZoomOutActionPerformed
		JScrollPane pane = (JScrollPane) internalTabs.getSelectedComponent();
		JViewport view = pane.getViewport();
		JDiagram diagram = (JDiagram) view.getView();
		if (diagram.getRenderer() != null) {
			diagram.getRenderer().setZoomLevel(diagram.getRenderer().getZoomLevel() - 0.2);
			diagram.getRenderer().layoutNodes(diagram, diagram.getRoot());
		}

		diagram.getRenderer().layoutNodes(diagram, diagram.getRoot());
		diagram.repaint();
		System.out.println("Zoom 2: " + diagram.getDimensions());
		pane.setPreferredSize(diagram.getDimensions());
		pane.validate();
	}// GEN-LAST:event_bttnZoomOutActionPerformed

	private void bttnZoomInActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bttnZoomInActionPerformed
		JScrollPane pane = (JScrollPane) internalTabs.getSelectedComponent();
		JViewport view = pane.getViewport();
		JDiagram diagram = (JDiagram) view.getView();
		if (diagram.getRenderer() != null) {
			diagram.getRenderer().setZoomLevel(diagram.getRenderer().getZoomLevel() + 0.2);
			diagram.getRenderer().layoutNodes(diagram, diagram.getRoot());
		}

		System.out.println("Zoom 1: " + diagram.getDimensions());

		diagram.repaint();
		pane.setPreferredSize(diagram.getDimensions());
		pane.validate();
	}// GEN-LAST:event_bttnZoomInActionPerformed

	public void resetDiagrams() {
		diagramOverview.unvalidate();
		diagramActionPatterns.unvalidate();
		diagramCompetences.unvalidate();
		diagramDriveCollections.unvalidate();
		printDiagram.unvalidate();
		logicDiagram.unvalidate();
	}

	/**
	 * Re-render all of the diagrams and make sure the current diagram and the
	 * selected object are focused once more.
	 */
	public void updateDiagrams(JDiagram focusedDiagram, IEditableElement showItem) {
		System.out.println("Trying to update diagram");
		
		// Get the active tab
		JScrollPane currentScrollpane = (JScrollPane) internalTabs.getSelectedComponent();

		// Redraw the diagrams
		addDiagrams();

		// Repaint the scroll pane
		currentScrollpane.repaint();

		// Switch to the appropriate view to see the updated diagram
		if (showItem instanceof Competence) {
			// switch to competence view
			internalTabs.setSelectedComponent(competencesScrollPane);
		}
		if (showItem instanceof DriveCollection) {
			// switch to drive collection view
			internalTabs.setSelectedComponent(driveScrollPane);
		}
		if (showItem instanceof ActionPattern) {
			// switch to action pattern view
			internalTabs.setSelectedComponent(actionPatternScrollPane);
		}

		currentScrollpane = (JScrollPane) internalTabs.getSelectedComponent();
		// Grab the focus to this tab, and get the new diagram
		currentScrollpane.grabFocus();
		JDiagram newDiagram = (JDiagram) currentScrollpane.getViewport().getView();
		JTreeNode scanFor = newDiagram.getRoot().findAndFocus(showItem);
		if (scanFor != null) {
			scanFor.treeNodeActionPerformed(null);
			scanFor.grabFocus();
			currentScrollpane.getViewport().scrollRectToVisible(scanFor.getBounds());
		}
	}
	
	
	/** Returns a string of all of the actions in the given LAP object */
	public ArrayList<String> getListOfActions() {
		ArrayList <String> actionList = new ArrayList <String> ();

		Iterator it = lapCurrent.getElements().iterator();
		while (it.hasNext()) {
			IEditableElement element = (IEditableElement) it.next();
			if (element instanceof DriveCollection) {
				DriveCollection collection = (DriveCollection) element;
				Iterator driveElementLists = collection.getDriveElements().iterator();
				while (driveElementLists.hasNext()) {
					Iterator driveElements = ((ArrayList) driveElementLists.next()).iterator();
					while (driveElements.hasNext()) {
						DriveElement driveElement = (DriveElement) driveElements.next();
						if (!actionList.contains(driveElement.getAction()) && (!lapCurrent.containsElementNamed(driveElement.getAction())))
							actionList.add(driveElement.getAction());
					}
				}
			} else if (element instanceof Competence) {
				Competence competence = (Competence) element;
				Iterator competenceLists = competence.getElementLists().iterator();
				while (competenceLists.hasNext()) {
					Iterator competences = ((ArrayList) competenceLists.next()).iterator();
					while (competences.hasNext()) {
						CompetenceElement compElement = (CompetenceElement) competences.next();
						if (!actionList.contains(compElement.getAction()) && (!lapCurrent.containsElementNamed(compElement.getAction())))
							actionList.add(compElement.getAction());
					}
				}
			} else if (element instanceof ActionPattern) {
				ActionPattern ap = (ActionPattern) element;

				Iterator elements = ap.getElements().iterator();
				while (elements.hasNext()) {
					ActionElement actionElement = (ActionElement) elements.next();
					if (!actionList.contains(actionElement.getElementName()) && (!lapCurrent.containsElementNamed(actionElement.getElementName())))
						actionList.add(actionElement.getElementName());
				}
			}
		}
		return actionList;
	}
	
	/** Returns a string of all of the senses in the given LAP object */
	public ArrayList<String> getListOfSenses() {
		ArrayList <String> senseList = new ArrayList <String> ();
		
		Iterator it = lapCurrent.getElements().iterator();
		while (it.hasNext()) {
			IEditableElement element = (IEditableElement) it.next();
			if (element instanceof DriveCollection) {
				DriveCollection collection = (DriveCollection) element;
				
				Iterator goalList = collection.getGoal().iterator();
				while(goalList.hasNext()){
					ActionElement goal = (ActionElement)goalList.next();
					if (!senseList.contains(goal.getElementName()))
						senseList.add(goal.getElementName());
				}
				
				Iterator driveElementLists = collection.getDriveElements().iterator();
				while (driveElementLists.hasNext()) {
					Iterator driveElements = ((ArrayList) driveElementLists.next()).iterator();
					while (driveElements.hasNext()) {
						DriveElement driveElement = (DriveElement) driveElements.next();
						Iterator triggerElements = driveElement.getTrigger().iterator();
						while (triggerElements.hasNext()) {
							ActionElement actionElement = (ActionElement) triggerElements.next();
							if (!senseList.contains(actionElement.getElementName()))
								senseList.add(actionElement.getElementName());
						}
					}
				}
			} else if (element instanceof Competence) {
				Competence competence = (Competence) element;
				// Get all of the goal senses as well
				Iterator goalList = competence.getGoal().iterator();
				while(goalList.hasNext()){
					ActionElement goal = (ActionElement)goalList.next();
					if (!senseList.contains(goal.getElementName()))
						senseList.add(goal.getElementName());
				}
				
				Iterator competenceLists = competence.getElementLists().iterator();
				while (competenceLists.hasNext()) {
					Iterator competences = ((ArrayList) competenceLists.next()).iterator();
					while (competences.hasNext()) {
						CompetenceElement compElement = (CompetenceElement) competences.next();
						Iterator triggerElements = compElement.getTrigger().iterator();
						while (triggerElements.hasNext()) {
							ActionElement actionElement = (ActionElement) triggerElements.next();
							if (!senseList.contains(actionElement.getElementName()))
								senseList.add(actionElement.getElementName());
						}
					}
				}
			}
		}
		return senseList;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane actionPatternScrollPane;

	private javax.swing.JButton bttnExport;

	private javax.swing.JButton bttnListPrims;

	private javax.swing.JButton bttnPrint;

	private javax.swing.JButton bttnValidate;

	private javax.swing.JButton bttnZoomIn;

	private javax.swing.JButton bttnZoomOut;

	private javax.swing.JPanel buttonPanel;

	private javax.swing.JScrollPane commentsPane;

	private javax.swing.JScrollPane competencesScrollPane;

	private javax.swing.JTextArea countArea;

	private javax.swing.JScrollPane driveScrollPane;

	private javax.swing.JTabbedPane internalTabs;

	private javax.swing.JCheckBox jCheckBox1;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JLabel jLabel3;

	private javax.swing.JMenu jMenu1;

	private javax.swing.JMenuBar jMenuBar1;

	private javax.swing.JPanel jPanel1;

	private javax.swing.JPanel jPanel2;

	private javax.swing.JPanel jPanel3;

	private javax.swing.JToolBar jToolBar1;

	private javax.swing.JScrollPane logicPane;

	private javax.swing.JScrollPane overviewPane;

	private javax.swing.JScrollPane printScrollPane;

	private javax.swing.JTextArea sourceArea;

	private javax.swing.JPanel sourcePanel;

	private javax.swing.JScrollPane sourceScrollPane;

	private javax.swing.JTextField txtAuthor;

	private javax.swing.JTextPane txtMemo;

	private javax.swing.JTextField txtTitle;
	// End of variables declaration//GEN-END:variables

}

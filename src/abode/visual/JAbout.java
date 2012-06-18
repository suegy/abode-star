/*
 * JAbout.java
 *
 * Created on 22 October 2005, 12:11
 */

package abode.visual;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * 
 * @author Cat
 */
public class JAbout extends javax.swing.JFrame {

	// Added to get rid of warnings and properly implement Serializable
	private static final long serialVersionUID = 1;

	/** Creates new form JAbout */
	public JAbout() {
		initComponents();
		setSize(390, 250);
		Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(SCREEN_SIZE.width / 2 - getWidth() / 2, SCREEN_SIZE.height / 2 - getHeight() / 2);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		jTextArea1 = new javax.swing.JTextArea();
		jTextArea2 = new javax.swing.JTextArea();

		getContentPane().setLayout(new java.awt.GridLayout(2, 0));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("About");
		setBackground(new java.awt.Color(204, 204, 204));
		setResizable(false);
		jPanel1.setLayout(new java.awt.GridLayout());

		jPanel1.setBackground(new java.awt.Color(204, 204, 204));
		jTextArea1.setBackground(new java.awt.Color(204, 204, 204));
		jTextArea1.setFont(new java.awt.Font("Courier New", 0, 12));
		jTextArea1
				.setText("        _    ____   ___  ____  _____ \n       / \\  | __ ) / _ \\|  _ \\| ____|      Advanced\n      / _ \\ |  _ \\| | | | | | |  _|        Behaviour\n     / ___ \\| |_) | |_| | |_| | |___       Oriented\n    /_/   \\_\\____/ \\___/|____/|_____|      Design\n          www.cobaltsoftware.net           Environment\n ");
		jTextArea1.setEditable(false);
		jPanel1.add(jTextArea1);

		getContentPane().add(jPanel1);

		jTextArea2.setBackground(new java.awt.Color(204, 204, 204));
		jTextArea2.setLineWrap(true);
		jTextArea2
				.setText("Behavior Oriented Design (BOD) is a methodology for developing control of complex intelligent agents, such as virtual reality characters, humanoid robots or intelligent environments.  It combines the advantages of Behavior-Based AI and Object Oriented Design. ABODE is a Java based integrated development environment for designing and building such plans.\n");
		jTextArea2.setWrapStyleWord(true);
		jTextArea2.setEditable(false);
		getContentPane().add(jTextArea2);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel jPanel1;

	private javax.swing.JTextArea jTextArea1;

	private javax.swing.JTextArea jTextArea2;
	// End of variables declaration//GEN-END:variables

}

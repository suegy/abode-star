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
package abode;

import javax.swing.DefaultListModel;

import abode.visual.JOptionsScreen;


/**
 * Another overly simply UI widget, this time it's an input box pair for getting
 * paths/names for engines in the configuration panel. I wish I had time to pad
 * this out to something more impressive.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class JEngineInput extends javax.swing.JFrame {
	// Added to implement seralizable
	private static final long serialVersionUID = 1;

	// Our list
	private DefaultListModel engineList = null;

	// Parent forms
	private JOptionsScreen parentForm = null;

	/**
	 * Initialize this engine input box
	 * 
	 * @param list
	 *            List model we're storing to
	 * @param parent
	 *            Parent component
	 */
	public JEngineInput(DefaultListModel list, JOptionsScreen parent) {
		initComponents();
		engineList = list;
		parentForm = parent;
		setSize(300, 100);
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
		jTextField1 = new javax.swing.JTextField();
		jTextField2 = new javax.swing.JTextField();
		jPanel2 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jPanel1.setLayout(new java.awt.GridLayout(2, 0));

		jTextField1.setMinimumSize(new java.awt.Dimension(170, 20));
		jPanel1.add(jTextField1);

		jTextField2.setMinimumSize(new java.awt.Dimension(170, 20));
		jPanel1.add(jTextField2);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

		jPanel2.setLayout(new java.awt.GridLayout(2, 0));

		jLabel1.setText("Engine Name:");
		jPanel2.add(jLabel1);

		jLabel2.setText("Command:");
		jPanel2.add(jLabel2);

		getContentPane().add(jPanel2, java.awt.BorderLayout.WEST);

		jPanel3.setMinimumSize(new java.awt.Dimension(250, 33));
		jButton1.setText("Ok");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jPanel3.add(jButton1);

		jButton2.setText("Cancel");
		jPanel3.add(jButton2);

		getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
		engineList.addElement(jTextField1.getText() + "," + jTextField2.getText());
		parentForm.repaint();
		dispose();
	}// GEN-LAST:event_jButton1ActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButton1;

	private javax.swing.JButton jButton2;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JPanel jPanel1;

	private javax.swing.JPanel jPanel2;

	private javax.swing.JPanel jPanel3;

	private javax.swing.JTextField jTextField1;

	private javax.swing.JTextField jTextField2;
	// End of variables declaration//GEN-END:variables

}

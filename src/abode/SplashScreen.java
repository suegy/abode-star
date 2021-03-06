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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


/**
 * SplashScreen is the class for pre-initialization of the various classes used
 * by the software at launch time. Using Class.forName(string) it ensures each
 * of the classes are pre-initialized when the application starts, so that
 * various delays waiting for resources and initialization do not occur during
 * general program use, where they may beunexpected by the user.
 * 
 * @author CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */
public class SplashScreen extends javax.swing.JFrame {

	// Added to implement serializable properly
	private static final long serialVersionUID = 1;
	
	private JAbode abodeCore;

	// List of all classes within the application that are to be pre-initialized
//	private File models;
	private HashSet<String> classes;
	
	// Our lovely splash image
	private ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("image/splash/monkey.png"));

	/**
	 * Constructs the splash screen and starts the various actions rolling.
	 * Perhaps in poor form for JFrame constructors, this particular class makes
	 * itself visible, but it's usefulness in the application is so restricted
	 * that this was considered an acceptable compromise.
	 */
	public SplashScreen() {
		
		// setting Nimbus look and feel layout
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // handle exception
		} 
		
		initComponents();

			
					
		// Centre us on the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));

		// Make us visible and bring us forward
		setVisible(true);
		toFront();

		// Show all classes
		doInitialization();
	}
	
	private HashSet<String> getRecursiveChildren(Enumeration<JarEntry> enumeration,String packageName,HashSet<String> children)
	{
		while (enumeration.hasMoreElements()) {
			JarEntry entry = enumeration.nextElement();
			if (!entry.isDirectory())
			{
				String name=entry.getName();
				name=name.replace(".class", "").replaceAll("[$0-9]+", "");
				
				children.add(name);
			}
				
		}
		return children;
		
	}

	/**
	 * Perform the one-time initialization of all classes by using the Java
	 * class loader and a list of named classes from our system.
	 */
	private void doInitialization() {
		boolean extraDefinition = false;
		
		//TODO : @Swen: This is real dirty and needs to be modified. 
				//       I am not sure why it was needed to load an external configuration for POSH elements
				JarFile jarFile= null;
				URL additional = ClassLoader.getSystemResource("ABODE-star.jar");
				
				if (additional instanceof URL)
					try {
						jarFile = new JarFile(additional.getFile());
					
						if (jarFile.getEntry("model/posh/") instanceof JarEntry)
							extraDefinition = true;
					} catch (IOException e) {
						System.out.println("Additional ABODE-posh.jar seems corrupted!");
						e.printStackTrace();
					} 
				else
				{
					classes=new HashSet<String>();
					classes.add("No additional posh configuration found!");
					for (int i=0;i<10;i++)
						classes.add("Loading predefined POSH files: "+i+"0%");	
				}

		if (extraDefinition){
				classes = getRecursiveChildren(jarFile.entries(), "", new HashSet<String>());
		}
	
		// Set the correct bounds on the progress bar
		jProgressBar1.setMinimum(0);
		jProgressBar1.setMaximum(classes.size());
	
		
		// For each of the classes we're going to initialize
		for (String model : classes) {
			
			jProgressBar1.setValue(jProgressBar1.getValue()+1);

			// Show the class we're about to load and wait 1/20th of a second
			jProgressBar1.setBackground(Color.green);
			jProgressBar1.setString("Initializing " + model);
			repaint();
			try {
				Thread.sleep(20);
			} catch (Exception e) {
			}

			// Initialize the class
			try {
				if  (extraDefinition){
					jarFile.getEntry(model).getClass();
					jProgressBar1.setString("Initialized " + model);
				} else
					jProgressBar1.setString(model);
				
				// Success, notify user
				repaint();
				
			} catch (Exception e) {
				jProgressBar1.setBackground(Color.RED);
				jProgressBar1.setString("Failure initializing " + model);
				repaint();
				try {
					Thread.sleep(3000);
				} catch (Exception f) {
				}
			}
		}

		// Get rid of this frame
		dispose();
		setVisible(false);

		// Show the main application (Which should spring up quite rapidly now)
		abodeCore = new JAbode();
		abodeCore.setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		splashPanel = new javax.swing.JPanel();
		jProgressBar1 = new javax.swing.JProgressBar();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("\n");
		setForeground(java.awt.Color.white);
		setUndecorated(true);
		splashPanel.setLayout(null);

		splashPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		splashPanel.setMaximumSize(new java.awt.Dimension(349, 200));
		splashPanel.setMinimumSize(new java.awt.Dimension(349, 200));
		splashPanel.setPreferredSize(new java.awt.Dimension(349, 200));
		jProgressBar1.setString("Initialising Classes");
		jProgressBar1.setStringPainted(true);
		splashPanel.add(jProgressBar1);
		jProgressBar1.setBounds(46, 155, 269, 14);

		getContentPane().add(splashPanel, java.awt.BorderLayout.CENTER);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	/**
	 * Evil overloaded paint method that actually skips the rendering of the
	 * panel itself, but draws directly onto the form background then renders
	 * the children of the panel. This particular workaround prevents the need
	 * for us to create a new class just to deal with a simple bit of background
	 * rendering.
	 * 
	 * @param g
	 *            Graphics object for rendering with.
	 */
	@Override
	public void paint(Graphics g) {
		// Draw background image
		g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null, null);

		// EVIL: Call containing panels child painting method.
		splashPanel.paintComponents(g);
	}
	
	

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JProgressBar jProgressBar1;

	private javax.swing.JPanel splashPanel;
	// End of variables declaration//GEN-END:variables
	
	
	/**
     * Start the program by invoking the splash screen, which will initialize
     * all of the classes in the program and then invoke the JAbode main GUI
     * class.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
        SplashScreen prog = new SplashScreen();
        //TODO: Swen:this is another ugly fix because we should check if the file is usable first 
        if (args != null && args.length > 0)
			try {
				prog.abodeCore.loadFile(args[args.length-1]);
			} catch (Exception e) {
				System.err.println("The supplied configuration file was incorrect!");
			}
        
    }

}

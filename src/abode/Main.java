package abode;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;




/**
 * Main is a simple empty class used for housing the main method
 * away from anywhere unexpected that it doesnt really belong. Hopefully
 * Java will allow specification of the bootstrap method in a nicer way
 * one day, but until then we'return using this.
 * 
 * @author  CobaltSoftware (abode.devteam@cobaltsoftware.net)
 * @version 1.0
 */

@Deprecated
public class Main {   
    /**
     * Start the program by invoking the splash screen, which will initialize
     * all of the classes in the program and then invoke the JAbode main GUI
     * class.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
    	// Trying out OS Look and feel
    	// TODO: Implement this as an option?
        try {
            // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
    	
        new SplashScreen();
    }
}

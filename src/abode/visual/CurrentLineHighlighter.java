/* CVS: CobaltVault SERVER:\\halo TREE:\abode_mainline
 *       _    ____   ___  ____  _____
 *      / \  | __ ) / _ \|  _ \| ____|      Advanced
 *     / _ \ |  _ \| | | | | | |  _|        Behavior
 *    / ___ \| |_) | |_| | |_| | |___       Oriented
 *   /_/   \_\____/ \___/|____/|_____|      Design
 *         www.cobaltsoftware.net           Environment
 *
 * This file uses code from other authors. You can find the
 * original source at:-
 *  http://jroller.com/page/santhosh/Weblog/highlighting_current_line?catname=
 */
package abode.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * This class can be used to highlight the current line for any JTextComponent.
 *
 * @author Santhosh Kumar T
 * @author Peter De Bruycker
 * @version 1.0
 */
public class CurrentLineHighlighter {
	private static final String LINE_HIGHLIGHT = "linehilight"; //NOI18N - used as clientproperty

	private static final String PREVIOUS_CARET = "previousCaret"; //NOI18N - used as clientproperty

	private static Color col = new Color(255, 255, 204); //Color used for highlighting the line

	public CurrentLineHighlighter() {
		// static util class only
	}

	// Installs CurrentLineHilighter for the given JTextComponent
	public static void install(JTextComponent c) {
		try {
			Object obj = c.getHighlighter().addHighlight(0, 0, painter);
			c.putClientProperty(LINE_HIGHLIGHT, obj);
			c.putClientProperty(PREVIOUS_CARET, new Integer(c.getCaretPosition()));
			c.addCaretListener(caretListener);
			c.addMouseListener(mouseListener);
			c.addMouseMotionListener(mouseMotionListener);
		} catch (BadLocationException ex) {
		}
	}

	// Uninstalls CurrentLineHighligher for the given JTextComponent
	public static void uninstall(JTextComponent c) {
		c.putClientProperty(LINE_HIGHLIGHT, null);
		c.putClientProperty(PREVIOUS_CARET, null);
		c.removeCaretListener(caretListener);
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(mouseMotionListener);
	}

	private static CaretListener caretListener = new CaretListener() {
		@Override
		public void caretUpdate(CaretEvent e) {
			JTextComponent c = (JTextComponent) e.getSource();
			CurrentLineHighlighter.caretUpdate(c);
		}
	};

	private static MouseListener mouseListener = new MouseAdapter() {
		// highlight the line the user clicks on
		@Override
		public void mousePressed(MouseEvent e) {
			JTextComponent c = (JTextComponent) e.getSource();
			caretUpdate(c);
		}
	};

	private static MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			JTextComponent c = (JTextComponent) e.getSource();
			caretUpdate(c);
		}
	};

	/**
	 * Fetches the previous caret location, stores the current caret location,
	 * If the caret is on another line, repaint the previous line and the current line
	 * @param c the text component
	 */
	private static void caretUpdate(JTextComponent c) {
		try {
			int previousCaret = ((Integer) c.getClientProperty(PREVIOUS_CARET)).intValue();
			final int actualCaretPosition = c.getCaretPosition();
			c.putClientProperty(PREVIOUS_CARET, new Integer(actualCaretPosition));
			Rectangle prev = c.modelToView(previousCaret);
			Rectangle r = c.modelToView(actualCaretPosition);
			//c.putClientProperty(PREVIOUS_CARET, new Integer(actualCaretPosition));

			if (prev.y != r.y) {
				c.repaint(0, prev.y, c.getWidth(), r.height);
				c.repaint(0, r.y, c.getWidth(), r.height);
			}
		} catch (BadLocationException ignore) {
		}
	}

	private static Highlighter.HighlightPainter painter = new Highlighter.HighlightPainter() {
		@Override
		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
			try {
				Rectangle r = c.modelToView(c.getCaretPosition());
				g.setColor(col);
				g.fillRect(0, r.y, c.getWidth(), r.height);
			} catch (BadLocationException ignore) {
			}
		}
	};
}
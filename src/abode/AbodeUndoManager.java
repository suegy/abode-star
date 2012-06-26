package abode;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;

public class AbodeUndoManager extends AbstractUndoableEdit implements UndoableEditListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2184859822749248400L;
	private static UndoManager undo;
	private static AbodeUndoManager manager;
	
	
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	
	private JButton undoButton;
	private JButton redoButton;

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		undo.addEdit(e.getEdit());
		updateButtons();
	}
	
	private AbodeUndoManager(){
		undo = new UndoManager();
	}
	
	protected void registerUndoMenuItem(JMenuItem undo){
		this.undoMenuItem=undo;
	}
	protected void registerRedoMenuItem(JMenuItem redo){
		this.redoMenuItem=redo;
	}
	
	
	protected void registerUndoButton(JButton undo){
		this.undoButton=undo;
	}
	protected void registerRedoButton(JButton redo){
		this.redoButton=redo;
	}
	
	
	public static AbodeUndoManager getUndoManager(){
		if (!(manager instanceof AbodeUndoManager))
			manager = new AbodeUndoManager();
			
		return manager;
	}
	
	public static UndoableEditListener getUndoListener(){
		if (manager instanceof AbodeUndoManager)
			return manager;
		else {
			manager = new AbodeUndoManager();
			return manager;
		}
	}
	
	@Override
	public void redo(){
		if (undo.canRedo()){
			undo.redo();
			if(redoMenuItem instanceof JMenuItem)
				redoMenuItem.setText(" "+undo.getRedoPresentationName());
		}
		updateButtons();
	}
	@Override
	public void undo(){
		if (undo.canUndo()){
			undo.undo();
			if(undoMenuItem instanceof JMenuItem)
				undoMenuItem.setText(" "+undo.getUndoPresentationName());
			
		}
		updateButtons();
	}
	
	public void updateButtons(){
		undoMenuItem.setEnabled(undo.canUndo());
		redoMenuItem.setEnabled(undo.canRedo());
		undoButton.setEnabled(undo.canUndo());
		redoButton.setEnabled(undo.canRedo());
	}

}

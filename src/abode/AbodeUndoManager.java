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
	
	
	private JMenuItem undoButton;
	private JMenuItem redoButton;

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		if(undoButton instanceof JMenuItem)
			undoButton.setText(undoButton.getText().trim()+" "+e.getEdit().getUndoPresentationName());
		undo.addEdit(e.getEdit());
	}
	
	private AbodeUndoManager(){
		undo = new UndoManager();
	}
	
	protected void registerUndoButton(JMenuItem undo){
		this.undoButton=undo;
	}
	protected void registerRedoButton(JMenuItem redo){
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
	
	public void redo(){
		if (undo.canRedo()){
			undo.redo();
			if(redoButton instanceof JMenuItem)
				redoButton.setText("Redo "+undo.getRedoPresentationName());
		}
	}
	public void undo(){
		if (undo.canUndo()){
			undo.undo();
			if(undoButton instanceof JMenuItem)
				undoButton.setText("Undo "+undo.getUndoPresentationName());
			
		}
	}

}

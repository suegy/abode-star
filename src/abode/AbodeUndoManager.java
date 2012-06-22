package abode;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class AbodeUndoManager implements UndoableEditListener {
	
	private static UndoManager undo;
	private static AbodeUndoManager manager;

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		// TODO Auto-generated method stub
		undo.addEdit(e.getEdit());
	}
	
	private AbodeUndoManager(){
		undo = new UndoManager();
	}
	
	
	public static UndoManager getUndoManager(){
		if (manager instanceof AbodeUndoManager)
			return undo;
		else {
			manager = new AbodeUndoManager();
			return undo;
		}
			
	}
	public static UndoableEditListener getUndoListener(){
		if (manager instanceof AbodeUndoManager)
			return manager;
		else {
			manager = new AbodeUndoManager();
			return manager;
		}
	}

}

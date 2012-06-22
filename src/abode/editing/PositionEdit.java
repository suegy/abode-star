package abode.editing;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import model.IEditableElement;

public class PositionEdit extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3954919886912770181L;
	
	private int o_Pos;
	private int pos;
	private Object elem;
	private ArrayList store;
	
	public PositionEdit(Object element, int oldPos, int newPos,ArrayList storage) {
		store=storage;
		elem=element;
		o_Pos=oldPos;
		pos=newPos;
	}
	
	public void undo(){
		super.undo();
		store.remove(elem);
		store.add(o_Pos, elem);
		
	}
	public void redo(){
		super.redo();
		store.remove(elem);
		store.add(pos, elem);
		
	}

}

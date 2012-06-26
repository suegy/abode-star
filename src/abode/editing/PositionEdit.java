package abode.editing;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import model.IEditableElement;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;

public class PositionEdit extends AbstractUndoableEdit {
	
	/**
	 * diagram and editor are used to update the visual presentation of Abode
	 */
	private JDiagram _diagram;
	private JEditorWindow _editor;
	
	private static final long serialVersionUID = -3954919886912770181L;
	
	private int o_Pos;
	private int pos;
	private Object elem;
	private ArrayList store;
	
	
	// @TODO: Arraylist needs a real typecast because it seems this was not intentionally left without one.
	/**
	 *  The PosistionEdit takes care of all changes inside the tree structure of an Abode LAP tree. 
	 *  It is used by the Undomanager to undo/redo changes to the elements in terms of moving up/down & left/right inside a subtree.
	 *  
	 * @param diagram The Diagram contains the JTreeNode components which are the visual representation of a LAP file.
	 * @param editor The EditorWindow contains the diagram and is just called to update the structure.
	 * @param element The PoshElement which is moved inside the action plan.
	 * @param oldPos The old Position inside the Arraylist structure which caries the action element.
	 * @param newPos The new Position inside the Arraylist structure which caries the action element.
	 * @param storage The Arraylist which represents an ordered set of action plan elements.
	 */
	public PositionEdit(JDiagram diagram, JEditorWindow editor, Object element, int oldPos, int newPos,ArrayList storage) {
		
		_diagram=diagram;
		_editor=editor;
		
		store=storage;
		elem=element;
		o_Pos=oldPos;
		pos=newPos;
	}
	
	public void undo(){
		super.undo();
		store.remove(elem);
		store.add(o_Pos, elem);
		
		if (elem instanceof IEditableElement)
			_editor.updateDiagrams(_diagram, (IEditableElement)elem);
		
	}
	public void redo(){
		super.redo();
		store.remove(elem);
		store.add(pos, elem);
		
		if (elem instanceof IEditableElement)
			_editor.updateDiagrams(_diagram, (IEditableElement)elem);
		
	}

}

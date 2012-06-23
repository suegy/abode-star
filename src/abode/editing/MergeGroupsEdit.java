package abode.editing;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;

import model.IEditableElement;

public class MergeGroupsEdit extends AbstractUndoableEdit {
	
	/**
	 * diagram and editor are used to update the visual presentation of Abode
	 */
	private JDiagram _diagram;
	private JEditorWindow _editor;
	
	private static final long serialVersionUID = -3954919886912770181L;
	
	private int o_Pos;
	private int pos;
	private Object elem;
	private JTreeNode parent;
	private ArrayList store;
	
	
	// @TODO: Arraylist needs a real typecast because it seems this was not intentionally left without one.
	/**
	 *  The PosistionEdit takes care of all changes inside the tree structure of an Abode LAP tree. 
	 *  It is used by the Undomanager to undo/redo changes to the elements in terms of moving up/down & left/right inside a subtree.
	 *  
	 * @param diagram The Diagram contains the JTreeNode components which are the visual representation of a LAP file.
	 * @param editor The EditorWindow contains the diagram and is just called to update the structure.
	 * @param parent The node which is linked to the parent of the PoshElement which is deleted from the action plan.
	 * @param element The Element which is deleted. The Element can be a PoshElement or an ArrayList containing a group of elements.
	 * @param oldPos The old Position inside the ArrayList structure which caries the action element.
	 * @param storage The ArrayList which represents an ordered set of action plan elements.
	 */
	public MergeGroupsEdit(JDiagram diagram, JEditorWindow editor, JTreeNode parent, Object element, int oldPos, ArrayList storage) {
		
		_diagram=diagram;
		_editor=editor;
		
		store=storage;
		this.parent=parent;
		elem=element;
		o_Pos=oldPos;
		
	}
	
	public void undo(){
		super.undo();
		store.add(o_Pos, elem);
		
		_editor.updateDiagrams(_diagram,parent.getValue());
		
	}
	public void redo(){
		super.redo();
		store.remove(elem);
		
		_editor.updateDiagrams(_diagram, parent.getValue());
		
	}

}

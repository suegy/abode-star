package abode.editing;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import model.IEditableElement;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;

public class MergeGroupsEdit extends AbstractUndoableEdit {
	
	/**
	 * diagram and editor are used to update the visual presentation of Abode
	 */
	private JDiagram _diagram;
	private JEditorWindow _editor;
	
	private static final long serialVersionUID = -3954919886912770181L;
	
	private int o_Pos;
	private ArrayList<IEditableElement> elem;
	private ArrayList<IEditableElement> target;
	private ArrayList<IEditableElement> o_target;
	private JTreeNode parent;
	private ArrayList store;
	
	
	// @TODO: Arraylist needs a real typecast because it seems this was not intentionally left without one.
	
	/**
	 * 	The MergeGroupsEdit takes care of the combination of two groups of elements within an action plan. 
	 *  It is used by the Undomanager to undo/redo changes to the elements in terms of combining or separation groups of elements.
	 * @param diagram The Diagram contains the JTreeNode components which are the visual representation of a LAP file.
	 * @param editor The EditorWindow contains the diagram and is just called to update the structure.
	 * @param origin The Element which is merged into another group. The Element can is an ArrayList containing a group of elements.
	 * @param oldPos The Position of the element inside the storage before merging.
	 * @param mergeGroup The ArrayList which will include the elements from the origin ArrayList.
	 * @param storage  The ArrayList which represents an ordered set of action plan elements.
	 * @param parent The node which is linked to the parent of the PoshElement which is deleted from the action plan.
	 */
	@SuppressWarnings("unchecked")
	public MergeGroupsEdit(JDiagram diagram, JEditorWindow editor, ArrayList<IEditableElement> origin, int oldPos, ArrayList<IEditableElement> mergeGroup, ArrayList storage, JTreeNode parent) {
		
		_diagram=diagram;
		_editor=editor;
		
		elem=origin;
		this.target=mergeGroup;
		this.o_target=(ArrayList<IEditableElement>) mergeGroup.clone();
		this.store=storage;
		this.parent=parent;
		
		o_Pos=oldPos;
		
	}
	
	public void undo(){
		super.undo();
		int index = store.indexOf(target);
		store.remove(target);
		store.add(index, o_target);
		store.add(o_Pos, elem);
		
		
		_editor.updateDiagrams(_diagram,parent.getValue());
		
	}
	public void redo(){
		super.redo();
		store.remove(elem);
		int index = store.indexOf(o_target);
		store.remove(o_target);
		store.add(index, target);

		
		_editor.updateDiagrams(_diagram, parent.getValue());
		
	}

}

package abode.control;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import abode.AbodeUndoManager;
import abode.editing.DeleteEdit;
import abode.editing.PositionEdit;
import abode.visual.JDiagram;
import abode.visual.JEditorWindow;
import abode.visual.JTreeNode;

public class AbodeActionHandler {
	
	private static AbodeActionHandler listener;
	private static UndoableEditListener _undoListener = AbodeUndoManager.getUndoListener();
	private static AbodeUndoManager _undo = AbodeUndoManager.getUndoManager();
	
	private AbodeActionHandler(){
		
	}
	
	public static AbodeActionHandler getActionHandler(){
		if (!(listener instanceof AbodeActionHandler))
			listener = new AbodeActionHandler();
		return listener;
	}

	public void deleteElementAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject){
		final ArrayList myGroup = subject.getGroup();
		final ArrayList groupGroup = subject.getParentNode().getGroup();
		
		if (JOptionPane.showConfirmDialog(diagram, "Are you sure you want to delete this item?") == JOptionPane.YES_OPTION) {
			_undoListener.undoableEditHappened(new UndoableEditEvent(myGroup, new DeleteEdit(diagram, internal, subject.getParentNode(), subject.getValue(), myGroup.indexOf(subject.getValue()), myGroup)));
			myGroup.remove(subject.getValue());
			internal.updateDiagrams(diagram, subject.getParentNode().getValue());
		}
		
	}
	public void deleteGroupAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject){
		final ArrayList myGroup = subject.getGroup();
		final ArrayList groupGroup = subject.getParentNode().getGroup();
		
		if (JOptionPane.showConfirmDialog(diagram, "Are you sure you want to delete this item?") == JOptionPane.YES_OPTION) {
			_undoListener.undoableEditHappened(new UndoableEditEvent(groupGroup, new DeleteEdit(diagram, internal, subject.getParentNode(), myGroup,groupGroup.indexOf(myGroup), groupGroup)));
			groupGroup.remove(myGroup);
			internal.updateDiagrams(diagram, subject.getParentNode().getValue());
		}
		
	}
	
	public void moveUpAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject){
		moveAction(diagram, internal, subject, true);
	}
	
	public void moveDownAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject){
		moveAction(diagram, internal, subject, false);
	}
	

	
	public void moveUpInGroupAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject){
		moveInGroupAction(diagram, internal, subject, true);
	}
	
	public void moveDownInGroupAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject){
		moveInGroupAction(diagram, internal, subject, false);
	}
	
	
	
	
	
	private void moveAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject,boolean up){
		final ArrayList myGroup = subject.getGroup();
		final ArrayList groupGroup = subject.getParentNode().getGroup();
		
		int index = groupGroup.indexOf(myGroup);
		int newIndex;
		if (up)
			newIndex=index-1;
		else
			newIndex=index+1;
		
		_undoListener.undoableEditHappened(new UndoableEditEvent(groupGroup, new PositionEdit(diagram,internal, myGroup, index, newIndex, groupGroup)));
		
		groupGroup.add(newIndex, groupGroup.remove(index));
		internal.updateDiagrams(diagram, subject.getValue());
		
	}
	
	private void moveInGroupAction(JDiagram diagram, JEditorWindow internal, JTreeNode subject,boolean up){
		final ArrayList myGroup = subject.getGroup();
		final ArrayList groupGroup = subject.getParentNode().getGroup();
		
		int index = myGroup.indexOf(subject.getValue());
		int newIndex;
		
		if(up)
			newIndex=index-1;
		else
			newIndex=index+1;
		
		_undoListener.undoableEditHappened(new UndoableEditEvent(myGroup, new PositionEdit(diagram, internal, subject.getValue(), index, newIndex, myGroup)));
		
		myGroup.add(newIndex, myGroup.remove(index));
		
		internal.updateDiagrams(diagram, subject.getValue());
	}
	
	



}

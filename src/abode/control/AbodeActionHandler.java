package abode.control;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import model.IEditableElement;
import model.posh.ActionElement;
import model.posh.ActionPattern;
import model.posh.Competence;
import model.posh.CompetenceElement;
import model.posh.DriveCollection;
import model.posh.DriveElement;

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
	
	/** Duplicates an action element.
	 * This method has to first find the correct parent node, and then clones the action element
	 * adding it to the parent / trigger / goal / action list (as appropriate).
	 * 
	 * @param diagram Node Diagram
	 * @param subject Subject JTreeNode
	 * @param internal Editor Window
	 */
	public void duplicateElement(JDiagram diagram, JEditorWindow internal, JTreeNode subject){
		JTreeNode parent;
		JTreeNode current = subject;
		
		/** Keep looping through the parents until we find the type of parent node that we are after
		 * This can be an action pattern, a competence, competence element or drive element.
		 */
		while((parent = current.getParentNode()) != null){
			// TODO: Undo support
			// Duplicate an action in an action pattern
			if(parent.getValue() instanceof ActionPattern){
				ActionPattern apParent = (ActionPattern) parent.getValue();
				ArrayList actions = (ArrayList)apParent.getElements().clone();
				int index = actions.indexOf(subject.getValue());
				try {
					actions.add(index,((ActionElement) (subject.getValue())).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				apParent.setElements(actions);
				break;
			}
			// Duplicate a goal sense of a competence
			else if(parent.getValue() instanceof Competence){
				Competence compParent = (Competence)parent.getValue();
				ArrayList goals = (ArrayList)compParent.getGoal().clone();
				int index = goals.indexOf(subject.getValue());
				try {
					goals.add(index,((ActionElement) (subject.getValue())).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				compParent.setGoal(goals);
				break;
			}
			// Duplicate a trigger of a competence element
			else if(parent.getValue() instanceof CompetenceElement){
				CompetenceElement elementParent = (CompetenceElement)parent.getValue();
				ArrayList triggers = (ArrayList)elementParent.getTrigger().clone();
				int index = triggers.indexOf(subject.getValue());
				try {
					triggers.add(index,((ActionElement) (subject.getValue())).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				elementParent.setTrigger(triggers);
				break;
			}
			// Duplicate a trigger of a drive element
			else if(parent.getValue() instanceof DriveElement){
				DriveElement elementParent = (DriveElement)parent.getValue();
				ArrayList triggers = (ArrayList)elementParent.getTrigger().clone();
				int index = triggers.indexOf(subject.getValue());
				try {
					triggers.add(index,((ActionElement) (subject.getValue())).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				elementParent.setTrigger(triggers);
				break;
			}
			// Duplicate a goal of a drive collection
			else if(parent.getValue() instanceof DriveCollection){
				DriveCollection dcParent = (DriveCollection)parent.getValue();
				ArrayList goal = (ArrayList)dcParent.getGoal().clone();
				int index = goal.indexOf(subject.getValue());
				try {
					goal.add(index,((ActionElement) (subject.getValue())).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				dcParent.setGoal(goal);
				break;
			}
			
			current = parent;
		}
		
		internal.updateDiagrams(diagram, subject.getValue());
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

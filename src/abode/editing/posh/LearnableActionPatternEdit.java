package abode.editing.posh;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import abode.editing.Documentation;

import model.TimeUnit;
import model.posh.Competence;
import model.posh.LearnableActionPattern;

public class LearnableActionPatternEdit extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9078250272460417485L;

	/**
	 * 
	 */
	

	
	private LearnableActionPattern learnableaction;

	// Array of elements that this action pattern contains
	private ArrayList alElements = null;
	private ArrayList o_alElements = null;

	//Docs
	private Documentation documentation;
	private Documentation o_documentation;
	
	
	/**
	 * This is an Editpart for the class Competence.
	 * The edit takes care of all old and new values to allow undo/redo
	 * @param competence
	 * @param elements
	 * @param timeOut new TimeOut value
	 * @param name new name
	 * @param enabled new enabled state
	 * @param doc new documentation
	 */
	public LearnableActionPatternEdit(LearnableActionPattern pattern, ArrayList elements, Documentation doc){
		
		this.learnableaction = pattern;
		
		this.alElements = elements;
		this.documentation=doc;
		
		this.o_alElements = pattern.getElements();
		this.o_documentation=pattern.getDocumentation();
		
	}
	
	@Override
	public void undo(){
		super.undo();

		learnableaction.setElements(o_alElements);
		learnableaction.setDocumentation(o_documentation);
		learnableaction.refresh();
	}
	
	@Override
	public void redo(){
		super.redo();
		learnableaction.setElements(alElements);
		learnableaction.setDocumentation(documentation);
		learnableaction.refresh();
		
	}
	
	

}

package abode.editing;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import model.TimeUnit;
import model.posh.ActionElement;
import model.posh.ActionPattern;
import model.posh.Competence;

public class CompetenceEdit extends AbstractUndoableEdit {

	/**
	 * 
	 */
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2025195168003520548L;

	/**
	 * 
	 */
	private Competence competence;

	// Array of elements that this action pattern contains
	private ArrayList alElements = null;
	private ArrayList o_alElements = null;

	// Time interval/timeout (Ymir-like!)
	private TimeUnit tTimeOut = null;
	private TimeUnit o_tTimeOut = null;
	
	// Name of this action pattern
	private String strName = null;
	private String o_strName = null;
	
	// The goal is an arraylist of ActionElements
	private ArrayList alGoal = null;
	private ArrayList o_alGoal = null;
	
	private boolean enabled = true;
	private boolean o_enabled = true;
	//Docs
	private String documentation;
	private String o_documentation;
	
	
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
	public CompetenceEdit(Competence competence, ArrayList elements, ArrayList goals, TimeUnit timeOut, String name, boolean enabled,String doc){
		
		this.competence = competence;
		
		this.alElements = elements;
		this.strName = name;
		this.tTimeOut = timeOut;
		this.alGoal = goals;
		this.enabled=enabled;
		this.documentation=doc;
		
		o_alElements = competence.getElementLists();
		o_strName = competence.getName();
		o_tTimeOut = competence.getTimeout();
		o_alGoal = competence.getGoal();
		o_enabled=competence.isEnabled();
		o_documentation=competence.getElementDocumentation();
		
	}
	
	public void undo(){
		super.undo();

		competence.setName(o_strName);
		competence.setElementLists(o_alElements);
		competence.setTimeout(o_tTimeOut);
		competence.setGoal(o_alGoal);
		competence.setEnabled(o_enabled);
		competence.setDocumentation(o_documentation);
		competence.refresh();
	}
	
	public void redo(){
		super.redo();
		competence.setName(strName);
		competence.setElementLists(alElements);
		competence.setTimeout(tTimeOut);
		competence.setGoal(alGoal);
		competence.setEnabled(enabled);
		competence.setDocumentation(documentation);
		competence.refresh();
		
	}
	
	

}

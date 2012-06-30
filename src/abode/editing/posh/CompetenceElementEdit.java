package abode.editing.posh;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import model.TimeUnit;
import model.posh.Competence;
import model.posh.CompetenceElement;

public class CompetenceElementEdit extends AbstractUndoableEdit {



	/**
	 * 
	 */
	private static final long serialVersionUID = -3043285850634884922L;

	/**
	 * 
	 */
	private CompetenceElement competence;

	// Name of this competence element
	private String strName = null;
	private String o_strName = null;

	// Arraylist containing ActionElement triggers
	private ArrayList alTrigger = null;
	private ArrayList o_alTrigger = null;

	// Name of action to invoke
	private String strAction = null;
	private String o_strAction = null;

	// How many times to retry?
	private int iRetries = 0;
	private int o_iRetries = 0;
	
	
	private boolean enabled = true;
	private boolean o_enabled = true;
	

	//Docs
	private String documentation;
	private String o_documentation;
	
	
	/**
	 * This is an Editpart for the class CompetenceElement.
	 * The edit takes care of all old and new values to allow undo/redo
	 * @param competence
	 * @param elements
	 * @param timeOut new TimeOut value
	 * @param name new name
	 * @param enabled new enabled state
	 * @param doc new documentation
	 */
	public CompetenceElementEdit(CompetenceElement competence, String name, ArrayList triggers, String action,int retries, boolean enabled,String doc){
		
		this.competence = competence;
		
		this.strName = name;
		this.alTrigger = triggers;
		this.strAction = action;
		this.iRetries = retries;
		this.enabled=enabled;
		this.documentation=doc;
		
		o_strName = competence.getName();
		o_alTrigger = competence.getTrigger();
		o_strAction = competence.getAction();
		o_iRetries = competence.getRetries();
		o_enabled=competence.isEnabled();
		o_documentation=competence.getElementDocumentation();
		
	}
	
	@Override
	public void undo(){
		super.undo();

		competence.setName(o_strName);
		competence.setTrigger(o_alTrigger);
		competence.setAction(o_strAction);
		competence.setRetries(o_iRetries);
		competence.setEnabled(o_enabled);
		competence.setDocumentation(o_documentation);
		competence.refresh();
	}
	
	@Override
	public void redo(){
		super.redo();
		competence.setName(strName);
		competence.setTrigger(alTrigger);
		competence.setAction(strAction);
		competence.setRetries(iRetries);
		competence.setEnabled(enabled);
		competence.setDocumentation(documentation);
		competence.refresh();
		
	}
	
	

}

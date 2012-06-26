package abode.editing;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import model.TimeUnit;
import model.posh.ActionPattern;

public class ActionPatternEdit extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4206622143700996160L;

	/**
	 * 
	 */
	private ActionPattern action;

	// Array of elements that this action pattern contains
	private ArrayList alElements = null;
	private ArrayList o_alElements = null;

	// Time interval/timeout (Ymir-like!)
	private TimeUnit tTimeOut = null;
	private TimeUnit o_tTimeOut = null;
	
	// Name of this action pattern
	private String strName = null;
	private String o_strName = null;
	
	private boolean enabled = true;
	private boolean o_enabled = true;
	//Docs
	private String documentation;
	private String o_documentation;
	
	
	
	/**
	 * This is an Editpart for the class ActionPattern.
	 * The edit takes care of all old and new values to allow undo/redo
	 * @param action
	 * @param elements
	 * @param timeOut new TimeOut value
	 * @param name new name
	 * @param enabled new enabled state
	 * @param doc new documentation
	 */
	public ActionPatternEdit(ActionPattern action, ArrayList elements, TimeUnit timeOut, String name, boolean enabled,String doc){
		
		this.action = action;
		
		this.alElements = elements;
		this.strName = name;
		this.tTimeOut = timeOut;
		this.enabled=enabled;
		this.documentation=doc;
		
		o_alElements = action.getElements();
		o_strName = action.getName();
		o_tTimeOut = action.getTimeUnit(); 
		o_enabled=action.isEnabled();
		o_documentation=action.getElementDocumentation();
		
	}
	
	@Override
	public void undo(){
		super.undo();

		action.setName(o_strName);
		action.setElements(o_alElements);
		action.setTimeUnit(o_tTimeOut);
		action.setEnabled(o_enabled);
		action.setDocumentation(o_documentation);
		action.refresh();
	}
	
	@Override
	public void redo(){
		super.redo();
		action.setName(strName);
		action.setElements(alElements);
		action.setTimeUnit(tTimeOut);
		action.setEnabled(enabled);
		action.setDocumentation(documentation);
		action.refresh();
		
	}
	
	

}

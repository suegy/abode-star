package abode.editing;

import javax.swing.undo.AbstractUndoableEdit;

import model.posh.ActionElement;

public class ActionElementEdit extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7529679449884815615L;
	
	private ActionElement actionElement;
	
	// Is this a sense?
	private boolean bIsSense;
	private boolean o_bIsSense;
	
	// Name of the action, sense or composite we refer to
	private String strElementName;
	private String o_strElementName;
	// Value input for the sense for comparison
	private String strValue;
	private String o_strValue;
	// Comparator / Predicate
	private String strComparator;
	private String o_strComparator;
	// Enabled or otherwise
	private boolean enabled;
	private boolean o_enabled;
	//Docs
	private String documentation;
	private String o_documentation;
	
	/**
	 * This is an Editpart for the class ActionElement.
	 * The edit takes care of all old and new values to allow undo/redo
	 * @param action the ActionElement itself
	 * @param sense the value of the new sense or if it did not change the old one
	 * @param name new name
	 * @param value new value
	 * @param compare new predicate
	 * @param enabled new enabled state
	 * @param doc new documentation
	 */
	public ActionElementEdit(ActionElement action,boolean sense, String name,String value,String compare, boolean enabled,String doc){
		actionElement=action;
		
		bIsSense=sense;
		strElementName=name;
		strValue=value;
		strComparator=compare;
		this.enabled=enabled;
		documentation=doc;
		
		o_bIsSense=action.getIsSense();
		o_strElementName=action.getElementName();
		o_strValue=action.getValue();
		o_strComparator=action.getPredicate();
		o_enabled=action.isEnabled();
		o_documentation=action.getElementDocumentation();
		
	}
	
	public void undo(){
		super.undo();
		actionElement.setIsSense(o_bIsSense);
		actionElement.setElementName(o_strElementName);
		actionElement.setValue(o_strValue);
		actionElement.setPredicate(o_strComparator);
		actionElement.setEnabled(o_enabled);
		actionElement.setDocumentation(o_documentation);
		actionElement.refresh();
	}
	
	public void redo(){
		super.redo();
		actionElement.setIsSense(bIsSense);
		actionElement.setElementName(strElementName);
		actionElement.setValue(strValue);
		actionElement.setPredicate(strComparator);
		actionElement.setEnabled(enabled);
		actionElement.setDocumentation(documentation);
		actionElement.refresh();
		
	}
	
	

}

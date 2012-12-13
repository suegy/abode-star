package model.posh;

import java.util.ArrayList;
import java.util.List;


public class Node<T> {
	
	private static long counter = 0L;

	private Node<T> parent;
	private T content;
	private ArrayList<Node<T>> children;
	private long id;
	
	
	private Node()
	{
		id = counter++;
		parent = null;
		this.content = null;
		this.children = new ArrayList<Node<T>>();
	}
	
	public Node(T content)
	{	this();
		this.content = content;
	}
	
	public Node(T content, List<Node<T>>children)
	{	
		this();
		this.content = content;
		setChildren(children);
	}
	
	public Node(Node<T> parent, T content, List<Node<T>> children)
	{	
		this(content,children);
		this.parent = parent;
	}
	
	public T getContent()
	{
		return content;
	}
	
	public void setContent(T content)
	{
		this.content = content;
	}
	
	public Node<T> getParent()
	{
		return parent;
	}
	
	public void setParent(Node<T> parent)
	{
		if (this.parent != null)
			parent.removeDirectChild(this);

		this.parent = parent;
	}
	
	@SuppressWarnings("unchecked")
	public Node<T> [] getChildren()
	{
		return (Node<T>[]) children.toArray();
	}
	public void setChildren(List<Node<T>> children)
	{
		this.children = new ArrayList<Node<T>>();
		addChildren(children);
	}
	public void addChildren(List<Node<T>> children)
	{
		for (Node<T> node : children) {
			node.setParent(this);
			this.children.add(node);
		}
	}
	
	public boolean removeDirectChild(Node<T> child)
	{
		return this.children.remove(child);
	}
	
	public boolean isRootNode()
	{
		return (parent == null) ? true : false;
	}
	
	public boolean isLeaf()
	{
		return (children == null || children.isEmpty()) ? true : false;
	}
	
	public long getID()
	{
		return id;
	}
	
	/**
	 * Left to right depth first search of children
	 **/
	public Node<T> getChild(long id)
	{
		if (isLeaf())
			return null;
		
		for (Node<T> node : this.children) 
			if (node.id == id)
				return node;
		// partial depth first search
		for (Node<T> node : this.children) {
			if (node.getChild(id) != null)
				return node.getChild(id);
		}
		
		return null;
	}
	
	
	public List<Node<T>> removeChildren(List<Node<T>> children)
	{
		for (Node<T> node : children) {
			if (this.removeDirectChild(node))
				children.remove(node);
		}
		for (Node<T> child : this.children) {
			children = child.removeChildren(children);
		}
		
		return children;
	}
		
}

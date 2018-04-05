package com.tamirhassan.publisher.model;

import java.util.List;


/**
 * Base class for layout objects within a column
 * i.e. laid out to a set width
 * 
 * @author tam
 *
 */
public abstract class PAFlexIncolObject implements PAFlexObject
{	
	public abstract PAFlexLayoutResult layout(float width);
	public int id = 0;
	
	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	
}

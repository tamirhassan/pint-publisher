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
	// alignment
	final public static int ALIGN_LEFT = 0;
	final public static int ALIGN_CENTRE = 1;
	final public static int ALIGN_CENTRE_KNUTH = 11;
	final public static int ALIGN_JUSTIFY = 2;
	final public static int ALIGN_RIGHT = 3;
	final public static int ALIGN_FORCE_JUSTIFY = 4;
	
	// justifies text, left-aligns other objects
	protected int alignment = ALIGN_JUSTIFY;
	
	// content width vs actual width?
	
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
	
	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
}

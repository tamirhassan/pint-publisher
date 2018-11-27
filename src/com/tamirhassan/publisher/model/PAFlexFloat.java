package com.tamirhassan.publisher.model;

/**
 * 
 * Top-level class for floats (figures, table floats, etc.)
 * 
 * @author tam
 *
 */
public abstract class PAFlexFloat extends PAFlexContainer
{
	boolean appearsBelow = false;
	
	float spacing = 0.0f;

	public float getSpacing() {
		return spacing;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	public boolean isAppearsBelow() {
		return appearsBelow;
	}

	public void setAppearsBelow(boolean appearsBelow) {
		this.appearsBelow = appearsBelow;
	}
}
package com.tamirhassan.publisher.knuthplass;
//this import (and extension) would be necessary to use KPGlue as a vspace too
import com.tamirhassan.publisher.model.PAFlexObject;
import com.tamirhassan.publisher.model.PAPhysObject;

public class KPGlue implements KPItem, PAFlexObject, PAPhysObject
{

	float amount;
	float stretchability;
	float shrinkability;
	float adjRatio = 0;
	boolean indent = false;

	public boolean isIndent() {
		return indent;
	}

	public void setIndent(boolean indent) {
		this.indent = indent;
	}

	/**
	 * creates a KPGlue object with default stretchability (2/3 amount)
	 * and shrinkability (1/3 amount)
	 * 
	 * @param amount
	 */
	public KPGlue(float amount) {
		this.amount = amount;
		this.stretchability = amount * (2.0f/3.0f);
		this.shrinkability = amount * (1.0f/3.0f);
	}
	
	public KPGlue(float amount, float stretchability, float shrinkability) {
		this.amount = amount;
		this.stretchability = stretchability;
		this.shrinkability = shrinkability;
	}
	
	/**
	 * clones the glueItem object and sets the adjacency ratio
	 * 
	 * @param glueItem
	 * @param adjRatio
	 */
	public KPGlue(KPGlue glueItem, float adjRatio) {
		this.amount = glueItem.amount;
		this.stretchability = glueItem.stretchability;
		this.shrinkability = glueItem.shrinkability;
		this.adjRatio = adjRatio;
	}
	
	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getStretchability() {
		return stretchability;
	}

	public void setStretchability(float stretchability) {
		this.stretchability = stretchability;
	}

	public float getShrinkability() {
		return shrinkability;
	}

	public void setShrinkability(float shrinkability) {
		this.shrinkability = shrinkability;
	}
	
	public float getAdjRatio() {
		return adjRatio;
	}

	public void setAdjRatio(float adjRatio) {
		this.adjRatio = adjRatio;
	}

	public String textContent()
	{
		return "KPGlue(" + amount + ", " + stretchability + ", " + shrinkability + ")";
	}
	
	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}
	
	public float spaceAmount() {
		
		if (adjRatio >= 0)
			return amount + (stretchability * adjRatio);
		else
			return amount + (shrinkability * adjRatio);
		
	}
	
	public KPGlue copy() {
		KPGlue retVal = 
				new KPGlue(this.amount, this.stretchability, this.shrinkability);
		retVal.setAdjRatio(this.adjRatio);
		return retVal;
	}

	/*
	@Override
	public float getHeight() {
//		System.err.println("KPGlue.getHeight() called!  Deprecated -- use getAmount() instead!");
		return amount;
	}
	
	@Override
	public float getWidth() {
//		System.err.println("KPGlue.getHeight() called!  Deprecated -- use getAmount() instead!");
		return amount;
	}
	*/
}

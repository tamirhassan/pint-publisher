package com.tamirhassan.publisher.knuthplass;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tamirhassan.publisher.model.PAFlexObject;
import com.tamirhassan.publisher.model.PAPhysObject;

// N.B.: PAFlexObject and PAPhysObject implementations added to enable
// use in vertical column breaking
// TODO 2018-02-14: why also PAPhysObject? look into this! perhaps because first H, then V optimization?
public class KPPenalty implements KPItem, PAFlexObject, PAPhysObject
{
	float amount; // in points
	KPBox additionalBox = null; // additional width to add for e.g. hyphens
	boolean flag = false; // probably irrelevant for vertical -- only horiz
	
	public KPPenalty(float amount)
	{
		this.amount = amount;
	}
	
	public KPPenalty(float amount, KPBox additionalBox, boolean flag)
	{
		this.amount = amount;
		this.additionalBox = additionalBox;
		this.flag = flag;
	}
	
	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public KPBox getAdditionalBox() {
		return additionalBox;
	}

	public void setAdditionalBox(KPBox additionalBox) {
		this.additionalBox = additionalBox;
	}

	public String textContent()
	{
		return "KPPenalty(" + amount + ", " + flag + " additional box: " + additionalBox + ")";
	}
	
	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}

	@Override
	public String tagName() 
	{
		return "kp-penalty";
	}

	@Override
	public void writeToPhysDocument(Document doc, Element el) 
	{
		// do nothing
	}

	// height and width are obviously zero
	/*
	@Override
	public float getHeight() {
		return 0;
	}
	
	@Override
	public float getWidth() {
		return 0;
	}
	*/
}

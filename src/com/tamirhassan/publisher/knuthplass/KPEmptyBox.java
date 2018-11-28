package com.tamirhassan.publisher.knuthplass;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tamirhassan.publisher.model.PAPhysObject;

public class KPEmptyBox implements KPBox, PAPhysObject {
	
	float amount;
	
	public KPEmptyBox(float amount)
	{
		this.amount = amount;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	@Override
	public String textContent() 
	{
		return "";
	}

	@Override
	public String tagName() 
	{
		return "kp-empty-box";
	}

	@Override
	public void writeToPhysDocument(Document doc, Element el) 
	{
		// do nothing, at least for the moment
	}
	
}
package com.tamirhassan.publisher.model;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public abstract class PAPhysGraphic extends PAPhysContainer
{
	// TODO: separate classes for vector and bitmap?
	
	// decided to implement PAPhysContainer and not PAPhysObject
	// PAPhysObject combines container (width/height/render) and glue (amount)
	
	// TODO: PAPhysContainer also has demerits = 1.0d;
	
	@Override
	public String textContent() 
	{
		return new String(this.getClass().getName());
	}

	@Override
	public float contentHeight() 
	{
		return height;
	}
	
	@Override
	public String tagName()
	{
		return "graphic";
	}
}
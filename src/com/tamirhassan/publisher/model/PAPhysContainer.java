package com.tamirhassan.publisher.model;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Renderable object in PDF
 * TODO: rename this class? Is not necessarily a container for anything
 * 
 * @author tam
 *
 */
public abstract class PAPhysContainer implements PAPhysObject
{
	float width;
	float height;
	double demerits;
	boolean markWarning = false;
	int flexID;
	
	// alignment
	final public static int ALIGN_LEFT = 0;
	final public static int ALIGN_CENTRE = 1;
	final public static int ALIGN_CENTRE_KNUTH = 11;
	final public static int ALIGN_JUSTIFY = 2;
	final public static int ALIGN_RIGHT = 3;
	final public static int ALIGN_FORCE_JUSTIFY = 4;
	
	// justifies text, left-aligns other objects
	protected int alignment = ALIGN_JUSTIFY;
	
	// TODO: standardize here on items?
	// probably retain demerits as calculated field: for columns, best to do it on a per-line level
	// and this is not possible when the lines are grouped into PAPhysTextBlocks.
	
	// NB: might be better to get rid of PAPhysTextBlocks? And just recreate them at the PDF-generation
	// stage?
	
	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public abstract float contentHeight();

	public double getDemerits() {
		return demerits;
	}

	public void setDemerits(double demerits) {
		this.demerits = demerits;
	}
	
	public int getFlexID() {
		return flexID;
	}

	public void setFlexID(int flexID) {
		this.flexID = flexID;
	}
	
	public void renderWarning(PDPageContentStream contentStream, 
			float x1, float y2) throws IOException
	{
		if (markWarning)
		{
			contentStream.saveGraphicsState();
			contentStream.setStrokingColor(Color.RED);
			
			contentStream.lineTo(x1 + width, y2);
			contentStream.lineTo(x1 + width, y2 + height);
			contentStream.lineTo(x1, y2 + height);
			contentStream.lineTo(x1, y2);
			contentStream.stroke();
			contentStream.restoreGraphicsState();
		}
	}
	
	public void writeToPhysDocument(Document doc, Element el)
	{
		Element childEl = doc.createElement(tagName());
		childEl.setAttribute("width", String.valueOf(width));
		childEl.setAttribute("height", String.valueOf(height));
		if (flexID != 0)
			childEl.setAttribute("flex-id", String.valueOf(flexID));
		
		el.appendChild(childEl);
	}
	
	public abstract void render(PDPageContentStream contentStream, 
			float x1, float y2) throws IOException;
	
	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}
	
}

package com.tamirhassan.publisher.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSString;

import com.tamirhassan.publisher.knuthplass.KPBox;
import com.tamirhassan.publisher.knuthplass.KPGlue;

// cannot be rendered to PDF directly or added to a PAPhysColumn
// needs to be part of a PAPhysTextBlock
// (therefore does not extend PAPhysContainer)

public class PAPhysTextLine //extends PAPhysContainer // implements KPBox
{
//	FontMetric metric = null;
//	PAFontMetrics metric = null;
//	PDSimpleFont font;// = PDType1Font.HELVETICA;
	
	// double adjustmentRatio
	
	float width;
	float height;
	double demerits;

	List<PAPhysObject> items = new ArrayList<PAPhysObject>(); 
	
	float textHeight; 			// generally set to the largest font size excluding leading
	float spacingBefore = 0; 	// generally zero

//  instead of spacingAfter, we set the value of height to refer to the total height
//	i.e. textHeight + leading = height
	
//	float spacingAfter; 		// the additional space to be added for leading
								// does not count if last line of column 
								// (counts at end of paragraph, though)
	
	// having the spacing on a per-line basis makes it possible to adjust line-by-line
	// e.g. for math
	
	/*
	
	float adjustmentRatio = 0;
	
	int wordSpacing = 0;
	int tracking = 0;
	float horizScale = 100;
	float sentenceSpacing = 0; // TODO: implement!
	
	// below this limit, only adjust character spacing
	float sentenceLowLimit = 0.8f;
	// above this limit, adjust character spacing
	float sentenceHighLimit = 1.5f;
	
	boolean kerning = true;
//	float leading = 1.2f;
	
	*/
	
	
//	List<Boolean> endOfSentence = new ArrayList<Boolean>();
//	List<Float> indivSpacingAdjustments = new ArrayList<Float>();
	
	public PAPhysTextLine()
	{
		// TODO: constructor with all necessary methods
//		getMetric();
	}
	
	public List<PAPhysObject> getItems() {
		return items;
	}

	public void setItems(List<PAPhysObject> items) {
		this.items = items;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * returns the height, _including_ the extra line spacing owing to leading
	 */
	public float getHeight() {
		return height;
	}	

	public void setHeight(float height) {
		this.height = height;
	}
	
	/**
	 * override for use as KPBox
	 */
	/*
	@Override
	public float getAmount() {
		return height;
	}
	*/
	
	public double getDemerits() {
		return demerits;
	}

	public void setDemerits(double demerits) {
		this.demerits = demerits;
	}

	/**
	 * normally the largest font size
	 * 
	 * @return textHeight
	 */
	public float getTextHeight() {
		return textHeight;
	}

	public void setTextHeight(float textHeight) {
		this.textHeight = textHeight;
	}

	public float getSpacingBefore() {
		return spacingBefore;
	}

	public void setSpacingBefore(float spacingBefore) {
		this.spacingBefore = spacingBefore;
	}

	/**
	 * amount of inter-line spacing in points
	 * not applied to final line in a column (only column, not paragraph)
	 * 
	 * @return spacingAfter
	 */
	/*
	public float getSpacingAfter() {
		return spacingAfter;
	}

	public void setSpacingAfter(float spacingAfter) {
		this.spacingAfter = spacingAfter;
	}
	
	/*
	 * in 1/1000ths of fontsize
	 * TODO: check whether to use int or float
	 */
	public float contentWidth(boolean withSpacing)
	{
		// TODO: implement for TT
		// e.g. retVal += font.getFontWidth(32);
		
		float retVal = 0;

		for (PAPhysObject item : items)
			if (item instanceof KPBox)
				retVal += ((KPBox)item).getAmount(); //getWidth();
			else if (withSpacing && item instanceof KPGlue)
				retVal += ((KPGlue)item).spaceAmount();
		
		return retVal;
	}
	
	public float minContentWidth()
	{
		// TODO: implement for TT
		// e.g. retVal += font.getFontWidth(32);
		
		float retVal = 0;

		for (PAPhysObject item : items)
			if (item instanceof KPBox)
				retVal += ((KPBox)item).getAmount(); //getWidth();
			else if (item instanceof KPGlue)
				retVal += (((KPGlue)item).getAmount() * ((KPGlue)item).getShrinkability());
		
		return retVal;
	}
	
	public float maxContentWidth()
	{
		// TODO: implement for TT
		// e.g. retVal += font.getFontWidth(32);
		
		float retVal = 0;

		for (PAPhysObject item : items)
			if (item instanceof KPBox)
				retVal += ((KPBox)item).getAmount(); //getWidth();
			else if (item instanceof KPGlue)
				retVal += (((KPGlue)item).getAmount() * ((KPGlue)item).getStretchability());
		
		return retVal;
	}
	
	public String textContent()
	{
		StringBuffer retVal = new StringBuffer();
		for (PAPhysObject i : items)
			if (i instanceof PAWord)
				retVal.append(((PAWord)i).getText());
			else if (i instanceof KPGlue)
				retVal.append(" ");
		return retVal.toString();
	}
	
	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}
	
	/*
	 * adds the brackets already!
	 */
	public static String convertToPDFEncoding(String str)
	{
		COSString cosStr = new COSString(str);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        //try {
			//cosStr.writePDF( buffer );
			//return new String( buffer.toByteArray(), "ISO-8859-1");
        	// TODO: check that this works! commented out method was removed in 2.0
        	return (cosStr.toString());
		//} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
        
        //return null;
	}

}

package com.tamirhassan.publisher.model;

import com.tamirhassan.publisher.knuthplass.KPBox;
import com.tamirhassan.publisher.stylesheet.PACharFormatting;

// Model summary: This class is used both to represent
// words as KPBoxes before K-P linebreaking/layout as well
// as words in the final physical objects.

// The physical words should be _cloned_, as some layout
// algorithms may alter parameters such as tracking, etc.

public class PAWord implements KPBox, PAPhysObject
{
//	FontMetric metric = null;
//	PAFontMetrics metric = null;
	
	String text;
	PACharFormatting cf;
	
	float width;
	float height;
	
	boolean kerning = true;
	
	/**
	 * creates a word object (KPBox) and calculates its width
	 * based on the font information. No hyphenation is carried out.
	 * height is set to fontSize
	 * 
	 * @param text
	 * @param font
	 * @param fontSize
	 * @param stretchFactor
	 */
	public PAWord(String text, PACharFormatting cf)
	{
		this.text = text;
		this.cf = cf;
		this.width = cf.getStringWidthInPoints(text);
		this.height = cf.getFontSize();
	}
	
	/**
	 * Creates a new PAWord with the same attributes
	 * 
	 * @param w
	 */
	public PAWord(PAWord w)
	{
		this.text = w.text;
		this.cf = w.cf;
		this.width = w.width;
		this.height = w.height;
	}
	
	/**
	 * manually clones the object
	 * 
	 * @return a clone of the object
	 */
	public PAWord copy()
	{
		PAWord retVal = new PAWord(text, cf);
		return retVal;
	}
	
	/**
	 * Creates a new PAWord by concatenating two words
	 * with the same attributes
	 * Recalculates width to allow for kerning adjustments
	 * 
	 * @param w
	 * @param w2
	 */
	/*
	public PAWord(PAWord w1, PAWord w2)
	{
		this.text = w1.text.concat(w2.text);
		this.font = w1.font;
		this.fontSize = w1.fontSize;
		this.stretchFactor = w1.stretchFactor;
		this.metric = w1.metric;
		this.width = metric.getStringWidth(text, kerning) * (fontSize / 1000) * stretchFactor;
		this.height = w1.height;
		
		// TODO: error handling if attributes do not match
	}
	*/
	
	/**
	 * creates a word object (KPBox) and sets the width and height that are
	 * passed to it
	 * 
	 * @param text
	 * @param cf
	 * @param width
	 * @param height
	 */
	public PAWord(String text, PACharFormatting cf, float width, float height)
	{
		this.text = text;
		this.cf = cf;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public float getAmount() {
		// TODO Auto-generated method stub
		return width;
	}
	
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

	public PACharFormatting getCharFormatting() {
		return cf;
	}

	public void setCharFormatting(PACharFormatting cf) {
		this.cf = cf;
	}

	/*
	public int getTracking() {
		return tracking;
	}

	public void setTracking(int tracking) {
		this.tracking = tracking;
	}
		
	public float getStretchFactor() {
		return stretchFactor;
	}

	public void setStretchFactor(float stretchFactor) {
		this.stretchFactor = stretchFactor;
	}
	*/
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * based on the class variables for font,
	 * finds the font metrics and
	 * sets the metric class variable
	 * 
	 * TODO: move to utils method?
	 */
	/*
	public void obtainMetric()
	{
		if (font instanceof PDType1Font)
		{
			PDType1Font t1font = (PDType1Font)font;
			
			// get AFM (perhaps do this at beginning and cache?)
			try 
			{
				Method method;
				method = t1font.getClass().getSuperclass().getSuperclass().
						getDeclaredMethod("getStandard14AFM");
				method.setAccessible(true);
				FontMetrics afmMetric = (FontMetrics)method.invoke(t1font);
				metric = new PAFontMetrics(afmMetric);
			} 
			catch (Exception e) 
			{
				System.err.println(
						"error retrieving metrics of " + t1font);
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println(
					"only T1 fonts are currently supported.  Error retrieving metrics of " + font);
		}
	}
	*/
	
	public float contentWidth()
	{
		return cf.getStringWidthInPoints(text);
	}
	
	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}

	@Override
	public String textContent() {
		return text;
	}
}

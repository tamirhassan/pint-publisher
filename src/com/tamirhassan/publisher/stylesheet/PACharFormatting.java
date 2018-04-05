package com.tamirhassan.publisher.stylesheet;

import java.io.IOException;
import java.math.BigInteger;

import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;

/**
 * Class for character-level formatting only; i.e. everything within one line
 * 
 * @author tam
 *
 */
public class PACharFormatting
{
	PDFont font; // TODO: PDSimpleFont?
	//PAFontMetrics fontMetric;
	FontMetrics fontMetrics;
	KerningSubtable horizKerningSubtable;
	float fontSize;
	boolean kerning = true;
	
	// TODO: horiz stretch
	// TODO: tracking
	
	//float stretchFactor = 1.0f; // ???
	
	public PACharFormatting(PDFont font, float fontSize)
	{
		this.font = font;
		this.fontSize = fontSize;
	}

	// note: values in the metric are floats
	
	public int getCharWidth(char code)
	{
		
		if (hasMetrics() && font instanceof PDSimpleFont)
		{
			Encoding enc = ((PDSimpleFont)font).getEncoding();
			String name = enc.getName(code);
			return (int)fontMetrics.getCharacterWidth(name);
		}
		else
		{
			try 
			{
				System.out.println("returning TT char width of " + code + ": " + font.getStringWidth(Character.toString(code)));
				return (int) font.getStringWidth(Character.toString(code));
			} 
			catch (IOException ioe) 
			{
				// TODO Auto-generated catch block
				ioe.printStackTrace();
				return 0;
				// TODO: error message
			} 
		}
	}
	
	public int getStringWidth(String str)//, boolean kerning)
	{
		int retVal = 0;
		for (int i = 0; i < str.length(); i ++)
		{
			if (i > 0 && kerning)
				// +ve movement is to the right (i.e. increases width)
				retVal += getKerningAdjustment(str.charAt(i - 1), str.charAt(i));

			retVal += getCharWidth(str.charAt(i));
		}
		return retVal;
	}
	
	public float getStringWidthInPoints(String str)
	{
		return (float)getStringWidth(str) * ((float)fontSize / 1000);
	}
	
	public int getKerningAdjustment(char char1, char char2)
	{
		if (hasMetrics() && font instanceof PDSimpleFont)
		{
			Encoding enc = ((PDSimpleFont)font).getEncoding();
			String name1 = enc.getName(char1);
			String name2 = enc.getName(char2);
			return (int)fontMetrics.getKerningAdjustment(name1, name2);
		}
		else if (hasMetrics() && font instanceof PDType0Font)
		{
			try 
			{
				byte[] b1 = font.encode(Character.toString(char1));
				byte[] b2 = font.encode(Character.toString(char2));
				
				int code1 = new BigInteger(b1).intValue();
				int code2 = new BigInteger(b2).intValue();
				
//				System.out.println("returning kerning for pair: " + char1 + " " + char2 + ": " + code1 + " " + code2 + ": " + foo.getKerning(code1, code2));
				float ttfOffset = horizKerningSubtable.getKerning(code1, code2);
				// PS (and T1) are based on 1000 ppem; TTF is based on 2048 ppem.
				return (int)(ttfOffset / 2.048f);
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
				return 0;
			}
		}
		else
		{
			return 0; // TODO: error message
		}
	}
	
	public static boolean isSameFont(PACharFormatting cf1, PACharFormatting cf2)
	{
		return (cf1.getFont() == cf2.getFont() 
				&& cf1.getFontSize() == cf2.getFontSize()
				//&& cf1.getStretchFactor() == cf2.getStretchFactor()
				&& cf1.isKerning() == cf2.isKerning());
	}
	
	public boolean isSameFont(PACharFormatting cf2)
	{
		return isSameFont(this, cf2);
	}
	
	public boolean hasMetrics()
	{
		return(fontMetrics != null || horizKerningSubtable != null);
	}
	
	/*
	 * 2018-01-11 moved to PAStylesheet
	 * 
	public PACharFormatting(List<String> tagList, NodeList styles)
	{
		// this.font = null;
		// this.fontSize = 12.0f;
		
		String font = "times"; // currently support only times, helvetica, courier
		boolean isBold = false, isItalic = false;
		float fontSize = 12.0f;
		
		// TODO: in a later version, allow numeric weights (CSS) and styles
		// (once OpenType fonts are supported)
		
		for (String tag : tagList)
		{
			for (int i = 0; i < styles.getLength(); i ++) 
            {
				if (styles.item(i) instanceof Element)
				{
					Element el = (Element) styles.item(i);
					if (el.getTagName().equals("character") ||
							el.getTagName().equals("block"))
					{
						if (el.getAttribute("tag").equals(tag))
						{
							// TODO: font dictionary. For now, only support inbuilt fonts
							
							// iterate through the attributes
							NamedNodeMap attributes = el.getAttributes();
							
							for (int j = 0; j < attributes.getLength(); j ++) 
				            {
								Node att = attributes.item(j);
								att.getNodeName();
								
								if (att.getNodeName().equals("tag"))
								{
									// ignore this attribute
								}
								else if (att.getNodeName().equals("font"))
								{
									if (att.getNodeValue().equals("times"))
									{
										font = "times";
									}
									else if (att.getNodeValue().equals("helvetica"))
									{
										font = "helvetica";
									}
									else if (att.getNodeValue().equals("courier"))
									{
										font = "courier";
									}
									else if (att.getNodeValue().equals("symbol"))
									{
										font = "symbol";
									}
									else if (att.getNodeValue().equals("zapf-dingbats"))
									{
										font = "zapf-dingbats";
									}
								}
								else if (att.getNodeName().equals("font-weight"))
								{
									if (att.getNodeValue().equals("bold"))
									{
										isBold = true;
									}
									else if (att.getNodeValue().equals("normal") ||
											att.getNodeValue().equals("regular"))
									{
										isBold = false;
									}
								}
								else if (att.getNodeName().equals("font-style"))
								{
									if (att.getNodeValue().equals("italic") ||
											att.getNodeValue().equals("cursive") ||
											att.getNodeValue().equals("oblique"))
									{
										isItalic = true;
									}
									else if (att.getNodeValue().equals("normal") ||
											att.getNodeValue().equals("upright"))
									{
										isItalic = false;
									}
								}
								else if (att.getNodeName().equals("font-size"))
								{
									fontSize = Integer.parseInt(att.getNodeValue());
								}
								else 
								{
									// unrecognized or unsupported attribute
								}
				            }	
							
							// block-level attributes, such as alignment, are ignored here
						}
					}
				}
            }
		}
		
		this.fontSize = fontSize;
		
		if (font.equals("helvetica"))
		{
			if (!isBold && !isItalic)
				this.font = PDType1Font.HELVETICA;
			else if (!isBold && isItalic)
				this.font = PDType1Font.HELVETICA_OBLIQUE;
			else if (isBold && !isItalic)
				this.font = PDType1Font.HELVETICA_BOLD;
			else if (isBold && isItalic)
				this.font = PDType1Font.HELVETICA_BOLD_OBLIQUE;
		}
		else if (font.equals("times"))
		{
			if (!isBold && !isItalic)
				this.font = PDType1Font.TIMES_ROMAN;
			else if (!isBold && isItalic)
				this.font = PDType1Font.TIMES_ITALIC;
			else if (isBold && !isItalic)
				this.font = PDType1Font.TIMES_BOLD;
			else if (isBold && isItalic)
				this.font = PDType1Font.TIMES_BOLD_ITALIC;
		}
		else if (font.equals("courier"))
		{
			if (!isBold && !isItalic)
				this.font = PDType1Font.COURIER;
			else if (!isBold && isItalic)
				this.font = PDType1Font.COURIER_OBLIQUE;
			else if (isBold && !isItalic)
				this.font = PDType1Font.COURIER_BOLD;
			else if (isBold && isItalic)
				this.font = PDType1Font.COURIER_BOLD_OBLIQUE;
		}
		else if (font.equals("symbol"))
		{
			this.font = PDType1Font.SYMBOL;
		}
		else if (font.equals("zapf-dingbats"))
		{
			this.font = PDType1Font.ZAPF_DINGBATS;
		}
		
		try 
		{
			Method method = this.font.getClass().getSuperclass().getSuperclass().
					getDeclaredMethod("getStandard14AFM");
			method.setAccessible(true);
			FontMetrics metric = (FontMetrics)method.invoke(this.font);
			this.fontMetric = new PAFontMetrics(metric);
		}
		catch (NoSuchMethodException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SecurityException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// TODO: move to utils or CharFormatting method?
    public static float getVerticalSpacing(String tagFrom, String tagTo, NodeList styles)
    {
    	float retVal = 0.0f;
    	
    	// iterate through the list, overriding any previous settings
    	// (later items have priority over earlier)
    	for (int i = 0; i < styles.getLength(); i ++) 
        {
			if (styles.item(i) instanceof Element)
			{
				Element el = (Element) styles.item(i);
				if (el.getTagName().equals("spacing"))
				{
					if (!el.hasAttribute("from") || el.getAttribute("from").equals(tagFrom)) 
					{
						if (!el.hasAttribute("to") || el.getAttribute("to").equals(tagTo)) 
						{
							if (el.hasAttribute("amount"))
							{
								retVal = Float.parseFloat(el.getAttribute("amount"));
								
								// TODO: what to do if invalid value?
								// TODO: understand units of measure
							}
							else
							{
								// amount is blank?
							}
						}
					}
				}
			}
        }
    	return retVal;
    }
    
	*/
	
	// automatically generated getters and setters
	
	public PDFont getFont() {
		return font;
	}
	public void setFont(PDFont font) {
		this.font = font;
	}
	
	/*
	public PAFontMetrics getFontMetric() {
		return fontMetric;
	}

	public void setFontMetric(PAFontMetrics fontMetric) {
		this.fontMetric = fontMetric;
	}
	*/
	
	public FontMetrics getFontMetrics() {
		return fontMetrics;
	}

	public void setFontMetrics(FontMetrics fontMetrics) {
		this.fontMetrics = fontMetrics;
	}

	public KerningSubtable getHorizKerningSubtable() {
		return horizKerningSubtable;
	}

	public void setHorizKerningSubtable(KerningSubtable horizKerningSubtable) {
		this.horizKerningSubtable = horizKerningSubtable;
	}

	public float getFontSize() {
		return fontSize;
	}
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public boolean isKerning() {
		return kerning;
	}

	public void setKerning(boolean kerning) {
		this.kerning = kerning;
	}
	
	
}
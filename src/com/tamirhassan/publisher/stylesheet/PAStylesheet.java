package com.tamirhassan.publisher.stylesheet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tamirhassan.publisher.knuthplass.PAKPTextBlock;
import com.tamirhassan.publisher.model.PAFlexFormattedParagraph;
import com.tamirhassan.publisher.model.PAFlexIncolObject;

public class PAStylesheet
{
	protected NodeList styles;
	protected HashMap<String, PDFont> fontMap;
	protected HashMap<String, List<String>> fontFamilyMap;
	
	// TODO: why does it only work with single root element (<doc-spec>)?
	
	protected static final String DEFAULT_STYLESHEET_XML =
		
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<doc-spec>" +
		"<!-- character styles -->" +
	    "<character tag=\"emph\" font-style=\"italic\"/>" +
	    "<character tag=\"b\" font-style=\"bold\"/>" +
	    "<character tag=\"i\" font-style=\"italic\"/>" +
	    "<!-- block styles -->" +
	    "<block tag=\"p\" font=\"times\" font-weight=\"normal\" font-style=\"regular\" font-size=\"12\" min-font-size=\"10\" max-font-size=\"12\" alignment=\"left\"/>" +
	    "<block tag=\"h1\" font=\"helvetica\" font-weight=\"bold\" font-style=\"regular\" font-size=\"20\" alignment=\"left\"/>" +
	    "<block tag=\"h2\" font=\"helvetica\" font-weight=\"bold\" font-style=\"regular\" font-size=\"17\" alignment=\"left\"/>" +
	    "<!-- inter-block styles -->" +
	    "<spacing amount=\"6\" /> <!-- interblock spacing: 6pt -->" +
	    "<spacing from=\"h1\" amount=\"12\"/> <!-- spacing after h1 12pt; overrides above -->" +
	    "</doc-spec>";
	
	/**
	 * generates empty stylesheet
	 * 
	 */
	public PAStylesheet()
	{
		this.styles = generateNodeList("<?xml version=\"1.0\" encoding=\"UTF-8\"?><doc-spec></doc-spec>");
	}
	
	/**
	 * generates stylesheet object corresponding to XML element
	 * 
	 * @param styles
	 */
	public PAStylesheet(NodeList styles)
	{
		//this.styles = join(generateNodeList(DEFAULT_STYLESHEET_XML), styles);
		this.styles = styles;
	}
	
	/**
	 * adds default styles to a stylesheet
	 * intended to be used after constructing an empty stylesheet
	 * 
	 */
	public void addDefaultStyles()
	{
		this.styles = join(this.styles, generateNodeList(DEFAULT_STYLESHEET_XML));
	}
	
	/**
	 * adds default styles to a stylesheet
	 * intended to be used after constructing an empty stylesheet
	 * 
	 */
	public void addStyles(NodeList styles)
	{
		this.styles = join(this.styles, styles);
	}
	
	/**
	 * initializes font objects
	 */
	public void loadFonts(PDDocument document)
	{
		fontMap = new HashMap<String, PDFont>();
		fontFamilyMap = new HashMap<String, List<String>>();
		
		try
		{
			for (int i = 0; i < styles.getLength(); i ++) 
	        {
				if (styles.item(i) instanceof Element)
				{
					Element el = (Element) styles.item(i);
					if (el.getTagName().equals("font"))
					{
						if (el.hasAttribute("name") && el.hasAttribute("file"))
						{
							String fontName = el.getAttribute("name");
							String fileName = el.getAttribute("file");
							File fontFile = new File(fileName);
							
							PDFont font = null;
							
							// TODO: there must be a more robust way!
							if (fileName.endsWith("ttf") || fileName.endsWith("TTF"))
							{
								// is deprecated
								//font = PDTrueTypeFont.loadTTF(document, fontFile);
								
								// TODO: input stream, subsetting
//								font = PDType0Font.load(document, fontFile);
								InputStream fis = new FileInputStream(fontFile);
								font = PDType0Font.load(document, fis, false);
								
								fontMap.put(fontName, font);
								
							}
							else if (fileName.endsWith("pfb") || fileName.endsWith("PFB"))
							{
								InputStream pfbIn = new FileInputStream(fontFile);
//								font = new PDType1Font(document, pfbIn);
								font = new PDType1Font(document, pfbIn, WinAnsiEncoding.INSTANCE);
								
								// workaround for PDFBOX-4115
								font.getCOSObject().setItem(COSName.ENCODING, COSName.WIN_ANSI_ENCODING);
								
								System.out.println("type: " + font.getType() + " subtype: " + font.getSubType());
								
								System.out.println("spaceWidth: " + font.getSpaceWidth());
								System.out.println("space width from string: " + font.getStringWidth(" "));
								
								fontMap.put(fontName, font);
								
							}
							if (fileName.endsWith("otf") || fileName.endsWith("OTF"))
							{
								// is deprecated
//								font = PDTrueTypeFont.loadTTF(document, fontFile);
								
								// TODO: input stream, subsetting
//								font = PDType0Font.load(document, fontFile);
								
								// this should enable subsetting
								InputStream otfIn = new FileInputStream(fontFile);
								font = PDType0Font.load(document, otfIn, true);
								
								fontMap.put(fontName, font);
							}
						}
						else
						{
							// TODO: error - incomplete font tag
						}
					}
					else if (el.getTagName().equals("font-family"))
					{
						if (el.hasAttribute("name"))
						{
							String fontName = el.getAttribute("name");
							
							ArrayList<String> familyList = new ArrayList<String>(4);
							for (int j = 0; j < 4; j ++)
								familyList.add("");
							
							// currently support only the four typical styles
							
							if (el.hasAttribute("regular"))
							{
								familyList.set(0, el.getAttribute("regular"));
							}
							else if (el.hasAttribute("normal"))
							{
								familyList.set(0, el.getAttribute("normal"));
							}
							else if (el.hasAttribute("roman"))
							{
								familyList.set(0, el.getAttribute("roman"));
							}
							if (el.hasAttribute("italic"))
							{
								familyList.set(1, el.getAttribute("italic"));
							}
							else if (el.hasAttribute("cursive"))
							{
								familyList.set(1, el.getAttribute("cursive"));
							}
							if (el.hasAttribute("bold"))
							{
								familyList.set(2, el.getAttribute("bold"));
							}
							if (el.hasAttribute("bold-italic"))
							{
								familyList.set(3, el.getAttribute("bold-italic"));
							}
							else if (el.hasAttribute("bold-cursive"))
							{
								familyList.set(3, el.getAttribute("bold-cursive"));
							}
							
							fontFamilyMap.put(fontName, familyList);
						}
					}
				}
	        }
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	/**
	 * generates a NodeList from the given XML input string
	 * 
	 * @param xml
	 * @return node list
	 */
	protected NodeList generateNodeList(String xml)
	{
		NodeList retVal = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
	    	        .newInstance();
	    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	InputStream is = new ByteArrayInputStream(xml.getBytes());
	    	Document document = documentBuilder.parse(is);
	    	
	    	Element docEl = (Element) document.getDocumentElement();
	    	retVal = docEl.getChildNodes();
	    	//retVal = docEl.getElementsByTagName("doc-spec").item(0).getChildNodes();
		}
    	catch (ParserConfigurationException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * gets font object from cache or returns null if not found
	 * 
	 * @param fontName
	 * @param weight
	 * @param style
	 * @return
	 */
	protected PDFont getFontObject(String fontName, String fontWeight, String fontStyle)
	{
		PDFont fontObj = null;
		
		// TODO: bold and italic!
		boolean isBold = false;
		if (fontWeight.equals("bold"))
			isBold = true;
		else if (fontWeight.equals("normal") ||
				fontWeight.equals("regular"))
			isBold = false;
		
		boolean isItalic = false;
		if (fontStyle.equals("italic") ||
				fontStyle.equals("cursive") ||
				fontStyle.equals("oblique"))
			isItalic = true;
		else if (fontStyle.equals("normal") ||
				fontStyle.equals("regular") ||
				fontStyle.equals("upright"))
			isItalic = false;
		
		if (fontName.equals("helvetica"))
		{
			if (!isBold && !isItalic)
				fontObj = PDType1Font.HELVETICA;
			else if (!isBold && isItalic)
				fontObj = PDType1Font.HELVETICA_OBLIQUE;
			else if (isBold && !isItalic)
				fontObj = PDType1Font.HELVETICA_BOLD;
			else if (isBold && isItalic)
				fontObj = PDType1Font.HELVETICA_BOLD_OBLIQUE;
		}
		else if (fontName.equals("times"))
		{
			if (!isBold && !isItalic)
				fontObj = PDType1Font.TIMES_ROMAN;
			else if (!isBold && isItalic)
				fontObj = PDType1Font.TIMES_ITALIC;
			else if (isBold && !isItalic)
				fontObj = PDType1Font.TIMES_BOLD;
			else if (isBold && isItalic)
				fontObj = PDType1Font.TIMES_BOLD_ITALIC;
		}
		else if (fontName.equals("courier"))
		{
			if (!isBold && !isItalic)
				fontObj = PDType1Font.COURIER;
			else if (!isBold && isItalic)
				fontObj = PDType1Font.COURIER_OBLIQUE;
			else if (isBold && !isItalic)
				fontObj = PDType1Font.COURIER_BOLD;
			else if (isBold && isItalic)
				fontObj = PDType1Font.COURIER_BOLD_OBLIQUE;
		}
		else if (fontName.equals("symbol"))
		{
			fontObj = PDType1Font.SYMBOL;
		}
		else if (fontName.equals("zapf-dingbats"))
		{
			fontObj = PDType1Font.ZAPF_DINGBATS;
		}
		else if (fontFamilyMap.containsKey(fontName))
		{
			// families override fontnames if they are the same
			
			List<String> familyList = fontFamilyMap.get(fontName);
			
			int index = 0;
			if (!isBold && !isItalic)
				index = 0;
			else if (!isBold && isItalic)
				index = 1;
			else if (isBold && !isItalic)
				index = 2;
			else if (isBold && isItalic)
				index = 3;
			
			fontObj = fontMap.get(familyList.get(index));
			// TODO: what if it is missing? fallback? error handling
		}
		else if (fontMap.containsKey(fontName))
		{
			// fallback if no family defined
			fontObj = fontMap.get(fontName);
		}
		else
		{
			return null;
		}
		
		return fontObj;
	}
	
	/**
	 * temporary method - will be replaced by a method to extract all
	 * block-level styles; currently alignment is the only supported style
	 * 
	 */
	/*
	public int alignment(Element content)
	{
		int retVal = PAKPTextBlock.ALIGN_JUSTIFY;
		
		String tag = content.getTagName();
		
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
							
							if (att.getNodeName().equals("alignment"))
							{
								String val = att.getNodeValue();
								if (val.equals("left"))
								{
									retVal = PAKPTextBlock.ALIGN_LEFT;
								}
								else if (val.equals("right"))
								{
									retVal = PAKPTextBlock.ALIGN_RIGHT;
								}
								else if (val.equals("centre") || val.equals("center"))
								{
									retVal = PAKPTextBlock.ALIGN_CENTRE;
								}
								else if (val.equals("justify") || val.equals("justified"))
								{
									retVal = PAKPTextBlock.ALIGN_JUSTIFY;
								}
								else if (val.equals("force-justify") || val.equals("force-justified"))
								{
									retVal = PAKPTextBlock.ALIGN_FORCE_JUSTIFY;
								}
							}
			            }	
					}
				}
			}
        }
		
		return retVal;
	}
	*/
	
	public void setBlockAttributes(PAFlexFormattedParagraph para)
	{
		int alignment = PAKPTextBlock.ALIGN_JUSTIFY;
		float firstLineIndent = 0.0f;
		
		String tag = para.getContent().getTagName();
		
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
							
							if (att.getNodeName().equals("alignment"))
							{
								String val = att.getNodeValue();
								if (val.equals("left"))
								{
									alignment = PAFlexIncolObject.ALIGN_LEFT;
								}
								else if (val.equals("right"))
								{
									alignment = PAFlexIncolObject.ALIGN_RIGHT;
								}
								else if (val.equals("centre") || val.equals("center"))
								{
									alignment = PAFlexIncolObject.ALIGN_CENTRE;
								}
								else if (val.equals("justify") || val.equals("justified"))
								{
									alignment = PAFlexIncolObject.ALIGN_JUSTIFY;
								}
								else if (val.equals("force-justify") || val.equals("force-justified"))
								{
									alignment = PAFlexIncolObject.ALIGN_FORCE_JUSTIFY;
								}
							}
							else if (att.getNodeName().equals("first-line-indent"))
							{
								firstLineIndent = Float.parseFloat(att.getNodeValue());
							}
							else if (att.getNodeName().equals("first-line-indent-first"))
							{
								if (para.isFirstPara())
									firstLineIndent = Float.parseFloat(att.getNodeValue());
							}
							else if (att.getNodeName().equals("first-line-indent-following"))
							{
								if (!para.isFirstPara())
									firstLineIndent = Float.parseFloat(att.getNodeValue());
							}
			            }	
					}
				}
			}
        }
		
		para.setAlignment(alignment);
		para.setFirstLineIndent(firstLineIndent);
	}
	
	/**
	 * returns 
	 * 
	 * @param tagList
	 * @return char formatting object
	 */
	public PACharFormatting charFormatting(List<String> tagList)
	{
		// this.font = null;
		// this.fontSize = 12.0f;
		
		String font = "times"; // currently support only times, helvetica, courier
		//boolean isBold = false, isItalic = false;
		String fontWeight = "regular", fontStyle = "regular";
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
									font = att.getNodeValue();
								}
								else if (att.getNodeName().equals("font-weight"))
								{
									fontWeight = att.getNodeValue();
								}
								else if (att.getNodeName().equals("font-style"))
								{
									fontStyle = att.getNodeValue();
								}
								else if (att.getNodeName().equals("font-size"))
								{
									fontSize = Float.parseFloat(att.getNodeValue());
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
		
		PDFont fontObj = getFontObject(font, fontWeight, fontStyle);
		if (fontObj == null)
		{
			// unrecognized font string
			fontObj = PDType1Font.TIMES_ROMAN;
			// TODO - exception?
		}
		
		PACharFormatting retVal = new PACharFormatting(fontObj, fontSize);
		
		try 
		{
			// try setting metric if possible
			if (fontObj.isStandard14())
			{
				Method method = fontObj.getClass().getSuperclass().getSuperclass().
						getDeclaredMethod("getStandard14AFM");
				method.setAccessible(true);
				FontMetrics metric = (FontMetrics)method.invoke(fontObj);
				//retVal.fontMetric = new PAFontMetrics(metric);
				retVal.fontMetrics = metric;
				
				int dummy = -1;
				FontBoxFont genericFont = ((PDType1Font)fontObj).getFontBoxFont();
				dummy = -1;
			}
			else if (fontObj instanceof PDType0Font) // TrueType font >256 chars
			{
				try
				{
					PDType0Font t0f = (PDType0Font)fontObj;
					
					Field f = t0f.getClass().getDeclaredField("ttf"); //NoSuchFieldException
					f.setAccessible(true);
					TrueTypeFont ttf = (TrueTypeFont) f.get(t0f); //IllegalAccessException
					
					if (ttf != null)
					{
		//				KerningTable kt = ((PDType0Font) fontObj).getKerningTable();
						KerningTable kt = ttf.getKerning();
						KerningSubtable horizKerningSubtable = kt.getHorizontalKerningSubtable();
						
						if (horizKerningSubtable != null)
							retVal.horizKerningSubtable = horizKerningSubtable;
					}
					else
					{
						System.err.println("Problem obtaining kerning pairs from font: " + fontObj.getName());
					}
				}
				catch (NoSuchFieldException nsfe)
				{
					nsfe.printStackTrace();
				}
				catch (IllegalAccessException iae)
				{
					iae.printStackTrace();
				}
				catch (NullPointerException npe)
				{
					npe.printStackTrace();
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
 
			}
			else if (fontObj instanceof PDType1Font) // but not Standard14
			{
				PDType1Font t1f = (PDType1Font)fontObj;
				
				// TODO
			}
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
		
		return retVal;
	}
	
	/**
	 * vertical interblock spacing
	 * 
	 * @param tagFrom
	 * @param tagTo
	 * @return
	 */
	public float interblockSpacing(String tagFrom, String tagTo)
    {
		// don't add extra space if vspace is used to override
		if (tagFrom.equals("vspace") || tagTo.equals("vspace"))
			return 0;
		
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
	
	public float insetSpacing()
	{
		float retVal = 12.0f;
		
		for (int i = 0; i < styles.getLength(); i ++) 
        {
			if (styles.item(i) instanceof Element)
			{
				Element el = (Element) styles.item(i);
				if (el.getTagName().equals("spacing"))
				{
					if (el.hasAttribute("inset"))
					{
						retVal = Float.parseFloat(el.getAttribute("inset"));
					}	
				}
			}
        }
		
		return retVal;
	}
	
	public float gutterWidth()
	{
		float retVal = 12.0f;
		
		for (int i = 0; i < styles.getLength(); i ++) 
        {
			if (styles.item(i) instanceof Element)
			{
				Element el = (Element) styles.item(i);
				if (el.getTagName().equals("spacing"))
				{
					if (el.hasAttribute("gutter"))
					{
						retVal = Float.parseFloat(el.getAttribute("gutter"));
					}
				}
			}
        }
		
		return retVal;
	}
	
	// taken from: 
	// https://stackoverflow.com/questions/26527191/java-concatinate-two-xml-nodelist
	public static NodeList join(final NodeList... lists) {

	    int count = 0;
	    for (NodeList list : lists) {
	        count += list.getLength();
	    }
	    final int length = count;

	    final Node[] joined = new Node[length];
	    int outputIndex = 0;
	    for (NodeList list : lists) {
	        for (int i = 0, n = list.getLength(); i < n; i++) {
	            joined[outputIndex++] = list.item(i);
	        }
	    }
	    class JoinedNodeList implements NodeList {
	        public int getLength() {
	            return length;
	        }

	        public Node item(int index) {
	            return joined[index];
	        }
	    }

	    return new JoinedNodeList();
	}
}
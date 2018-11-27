package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tamirhassan.publisher.knuthplass.KPEmptyBox;
import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPItem;
import com.tamirhassan.publisher.knuthplass.KPPenalty;
import com.tamirhassan.publisher.knuthplass.PAKPTextBlock;
import com.tamirhassan.publisher.stylesheet.PACharFormatting;
import com.tamirhassan.publisher.stylesheet.PAStylesheet;

// Model summary: This class extends PAKPTextBlock
// and contains the following:
// textual content - currently string; later TextStream with control codes (e.g. font changes)
// font and fontsize for paragraph (as defined by style)

// Processing order:
// apply styles to this object (at creation?)
// replace reference control codes with text
// hyphenate text (i.e. insert soft hyphens)
// generate K-P boxes from text

// NB: Inheritance order:
// PAFlexParagraph -> PAKPTextBlock -> PAFlexBreakableObject -> PAFlexIncolObject -> PAFlexObject

// TODO: PAFlexSimpleParagraph, PAFlexFormattedParagraph as types of PAFlexParagraph

/**
 * 
 * 
 * @author tam
 *
 */
public class PAFlexFormattedParagraph extends PAFlexParagraph //PAKPTextBlock
{
	// 	2018-01-10 moved to PAFlexParagraph.java
	// 	boolean kerning = true;
	// 	Locale locale = Locale.getDefault();
	// 	boolean hyphenate = true;
	// 	String hyphenationChar = "-"; // TODO: locale dependent?
		
	//	float lineSpacing = 1.2f; inherited from PAKPTextBlock

	// necessary to have the element for the node type (<p>, <h1>, etc.)
	// String content;
	Element content;
	PAStylesheet stylesheet;
	boolean firstPara = false;
	
	public PAFlexFormattedParagraph()
	{
	}

	public PAFlexFormattedParagraph(Element content, PAStylesheet stylesheet, Locale locale,
			Element prevItem)
	{
		this.content = content;
		this.stylesheet = stylesheet;
		this.locale = locale;
		
		if (prevItem != null)
		{
			if (content.getTagName().equals(prevItem.getTagName()))
			{
				firstPara = false;
			}
			else
			{
				firstPara = true;
			}
		}
		
		// assign block-level styles
//		setAlignment(stylesheet.alignment(content));
		stylesheet.setBlockAttributes(this);
		
//		this.setAlignment(ALIGN_LEFT);
//		this.hyphenate = false;
	}
	
	public PAFlexFormattedParagraph(Element content, PAStylesheet stylesheet, Locale locale)
	{
		this(content, stylesheet, locale, null);
	}
	
	public Element getContent() {
		return content;
	}
	
	public void setContent(Element content) {
		this.content = content;
	}

	public PAStylesheet getStyles() {
		return stylesheet;
	}

	public void setStyles(PAStylesheet styles) {
		this.stylesheet = styles;
	}

	public PAStylesheet getStylesheet() {
		return stylesheet;
	}

	public void setStylesheet(PAStylesheet stylesheet) {
		this.stylesheet = stylesheet;
	}

	public boolean isFirstPara() {
		return firstPara;
	}

	public void setFirstPara(boolean firstPara) {
		this.firstPara = firstPara;
	}

	public String textContent()
	{
		return content.getTextContent();
	}
	
	// TODO: rewrite generateBoxGlueItems to be a recursive
	// TODO: method (static?) to determine character formatting
	//       e.g. returning a style object that can be queried
	//            for font, font-size and other parameters when supported
	
	protected void addBoxGlueItems(float stretchFactor, Element contentEl, List<String> tagList, 
			Locale loc, int level)
	{
		System.out.println("in addBoxGlueItems with level: " + level + " and tagName: " + contentEl.getTagName());
		tagList.add(contentEl.getTagName());
		System.out.print("tagList: ");
		for (String tag : tagList)
			System.out.print(tag + "  ");
		System.out.println();
		
		// iterate through children
		NodeList nl = contentEl.getChildNodes();
		for (int i = 0; i < nl.getLength(); i ++) 
        {
			Node childNode = nl.item(i);
			if (nl.item(i) instanceof Element)
    		{ 
	        	Element childEl = (Element) childNode;
	        	
	        	System.out.println("childEl nodeName: " + childEl.getNodeName() + " tagName: " + childEl.getTagName() + " textContent: " + childEl.getTextContent());
	        	
	        	// check for change of language
	        	Locale thisLoc = loc;
	        	if (childEl.hasAttribute("lang"))
	        		thisLoc = Locale.forLanguageTag(childEl.getAttribute("lang"));
	        	
	        	// TODO: check for paragraphs inside of paragraphs
	        	// and other block-level violations (span is OK)
	        	if (childEl.getTagName().trim().equals("p"))
	        	{
	        		int dummy = 0;
	        		dummy ++;
	        		// WHOA! We have an illegal element here
	        	}
	        	
	        	// TODO: check if any block-level or other illegal elements here
	        	else if (childEl.getTagName().trim().equals("br"))
	        	{
	        		// (not in K-P) break to inhibit breaking before finishing glue
	    			boxGlueItems.add(new KPPenalty(+100000));
	    			
	    			// add finishing glue (K-P p. 1124 PDF 6)
	    			boxGlueItems.add(new KPGlue(0, 100000, 0));
	    			
	    			// add finishing penalty of -inf (K-P p. 1124 PDF 6)
	    			// TODO: flag this penalty? then we need to take care when fixing hyphens
	    			boxGlueItems.add(new KPPenalty(-100000));
	        	}
	        	else if (childEl.getTagName().trim().equals("hspace"))
	        	{
	    			boxGlueItems.add(new KPGlue(12, 0, 0));
	        	}
	        	else
	        	{
		        	// copy tag list to ensure that it remains unaltered
		        	List<String> childTagList = new ArrayList<String>();
		        	childTagList.addAll(tagList);
		        	
		        	// recurse; checking against stylesheet is done later
					addBoxGlueItems(stretchFactor, childEl, childTagList, thisLoc, level + 1);
	        	}
    		}
			else if (nl.item(i).getNodeType() == 3) // text node
			{
				String textContent = childNode.getTextContent();

				System.out.println("child text node nodeName: " + childNode.getNodeName() + " textContent: " + childNode.getTextContent());
				
				if (textContent.trim().length() > 0) // can occur if e.g. newline between two tags, e.g. <p> and <b> at start of para
				{
					// new method TH
					// replace carriage returns with spaces
					// keep trailing and leading spaces
					// in calling method remove trailing & leading spaces of block-level object
					// and shorten runs of consecutive spaces to one space
					// (later: purposeful spaces, e.g. indents handled)
					
					// remove leading and trailing newlines  \s*\n ... \n\s*
					// other newlines get replaced by space
					// TODO: decide whether to handle beginnings/ends of paragraphs 
					//       (block-level elements) differently to e.g. <b>s within text
					
					// String cleanedText = textContent.replaceAll("^\\s*\\n+", "").replaceAll("\\n+\\s*$", "").replaceAll("\\n", " ");
					
					// TH: replace all newlines with space
					String cleanedText = textContent.replaceAll("\\n", " ");
					
					// checks if space before or after, trims it and stores for later adding
					
					boolean spaceBefore = false, spaceAfter = false;
					
					if (cleanedText.matches("^\\s+.*"))
					{
						spaceBefore = true;
						cleanedText = cleanedText.replaceAll("^\\s+", "");
					}
					if (cleanedText.matches(".*\\s+$"))
					{
						spaceAfter = true;
						cleanedText =  cleanedText.replaceAll("\\s+$", "");
					}
					
					
					// leading and trailing spaces become empty words
					// other spaces between words disappear
					String[] words = cleanedText.split("\\s+");
					//String[] words = textContent.trim().split("\\s+");
					
					// TODO: different spacing for sentence ends
					// TODO: locale agnostic! (see code of LineFold)
					// TODO: hyphenation!
					
					PACharFormatting cf = stylesheet.charFormatting(tagList);
					
					float spaceWidth = cf.getCharWidth(' ') * 
							(cf.getFontSize() / 1000.0f) * stretchFactor;
					
					// 2018-06-26: no preservation of leading and trailing spaces as these are
					// only at beginning or end of paragraph (and not e.g. at line breaks)
					if (spaceBefore)
					{
						boxGlueItems.add(new KPGlue(spaceWidth, spaceWidth/2, spaceWidth/3, true));
//						addSpace(spaceWidth);
					}
					
					for (int j = 0; j < words.length; j ++)
					{
						// preserve leading/trailing spaces (which lead to empty strings) here
						if (j > 0) // || words[j].isEmpty())
							// add a space as glue
							boxGlueItems.add(new KPGlue(spaceWidth, spaceWidth/2, spaceWidth/3, true));
//							addSpace(spaceWidth);
						
						if (!(words[j].isEmpty()))
						{
							// add the word as a box
							float subStringWidth = 
									cf.getStringWidthInPoints(words[j]);
			            	System.out.println("subStringWidth: " + words[j] + ": " + subStringWidth);
							PAWord w = new PAWord(words[j], cf, subStringWidth, cf.getFontSize());
							boxGlueItems.add(w);
//							System.out.println("word: " + words[j]);
						}
						
					}
					
					// 2018-06-26: no preservation of leading and trailing spaces as these are
					// only at beginning or end of paragraph (and not e.g. at line breaks)
					if (spaceAfter)
					{
						boxGlueItems.add(new KPGlue(spaceWidth, spaceWidth/2, spaceWidth/3, true));
//						addSpace(spaceWidth);
					}
				}
			}
			else
			{
				// TODO: what to catch here? Non-element nodes? Comments?
			}
		}
	}
	
	protected void correctBlockLevelSpacing()
	{
		// TH: remove trailing and leading (runs of) spaces at block level only
		// TODO: later: non-space character spaces (e.g. indentations, hspaces)
		boolean exitLoop = false;
		for (int i = 0; !exitLoop && i < boxGlueItems.size(); i ++)
		{
			KPItem bgItem = boxGlueItems.get(i);
			if (bgItem instanceof KPGlue)
			{
				// safer to delete item at that location, in case the same
				// glue item is re-used in the list
				boxGlueItems.remove(i);
				i --; // will be re-incremented in next loop to same value
			}
			else
			{
				exitLoop = true;
			}
		}
		
		exitLoop = false;
		for (int i = boxGlueItems.size() - 1; !exitLoop && i >= 0; i --)
		{
			KPItem bgItem = boxGlueItems.get(i);
			if (bgItem instanceof KPGlue)
			{
				// safer to delete item at that location, in case the same
				// glue item is re-used in the list
				boxGlueItems.remove(i);
			}
			else
			{
				exitLoop = true;
			}
		}
		
		// TH: shorten consecutive space runs to one space
		boolean prevSpace = false;
		for (int i = 0; !exitLoop && i < boxGlueItems.size(); i ++)
		{
			KPItem bgItem = boxGlueItems.get(i);
			if (bgItem instanceof KPGlue)
			{
				if (prevSpace == false) // first space found
				{
					prevSpace = true;
				}
				else
				{
					// duplicate space found
					boxGlueItems.remove(i);
					i --; // will be re-incremented to same value
				}
			}
			else
			{
				prevSpace = false;
			}
		}
	}
	

	/**
	 * creates a low-level box-glue representation of the paragraph
	 * 
	 * @param hyphenTree -- pass a null tree to disable hyphenation
	 * @param stretchFactor
	 * @return
	 */
	public void generateBoxGlueItems(float stretchFactor)
	{
		System.out.println("toBoxGlue called with PAFlexFormattedParagraph: " + this);
		
		boxGlueItems = new ArrayList<KPItem>();
		
		// create tagList with first tag for recursion
		ArrayList<String> tagList = new ArrayList<String>();

		// necessary for centred text
		PACharFormatting cf = stylesheet.charFormatting(tagList);
		float spaceWidth = cf.getCharWidth(' ') * 
				(cf.getFontSize() / 1000) * stretchFactor;
		
		addBoxGlueItems(stretchFactor, content, tagList, locale, 1);
		
		// remove spacing (due to whitespace characters) at beginning and end
		while (boxGlueItems.size() > 0 && boxGlueItems.get(0) instanceof KPGlue
				&& ((KPGlue)boxGlueItems.get(0)).isSpace())
		{
			boxGlueItems.remove(0);
		}
		
		while(boxGlueItems.size() > 0 && boxGlueItems.get(boxGlueItems.size() - 1) instanceof KPGlue
				&& ((KPGlue)boxGlueItems.get(boxGlueItems.size () -1)).isSpace())
		{
			boxGlueItems.remove(boxGlueItems.size () -1);
		}
		
		// replace space with correct type of space
		ArrayList<KPItem> boxGlueItemsTemp = new ArrayList<KPItem>();
		for (KPItem i : boxGlueItems)
		{
			if (i instanceof KPGlue)
			{
				if (((KPGlue)i).isSpace())
				{
					float sw = i.getAmount();
					
					if (getAlignment() == ALIGN_LEFT || getAlignment() == ALIGN_CENTRE ||
							getAlignment() == ALIGN_RIGHT)
					{
						// K-P p. 1139 PDF 21 w=0, y=18, z=0
						boxGlueItemsTemp.add(new KPGlue(0, sw * 3.0f, 0));
						boxGlueItemsTemp.add(new KPPenalty(0));
						boxGlueItemsTemp.add(new KPGlue(sw, sw * -3.0f, 0));
					}
					else if (getAlignment() == ALIGN_CENTRE_KNUTH)
					{
						// K-P p. 1140 PDF 22 w=0, y=18, z=0
						boxGlueItemsTemp.add(new KPGlue(0, sw * 3.0f, 0));
						boxGlueItemsTemp.add(new KPPenalty(0));
						// K-P p. 1140 PDF 22 w=0, y=-36, z=0
						boxGlueItemsTemp.add(new KPGlue(sw, sw * -6.0f, 0));
						boxGlueItemsTemp.add(new KPEmptyBox(0));
						boxGlueItemsTemp.add(new KPPenalty(10000, new KPEmptyBox(0), false));
						boxGlueItemsTemp.add(new KPGlue(0, sw * 3.0f, 0));
					}
					else // ALIGN_JUSTIFY or ALIGN_FORCE_JUSTIFY
					{
						// K-P p. 1124 PDF 6 w=6, y=3, z=2
						boxGlueItemsTemp.add(i);
					}
				}
				else
				{
					boxGlueItemsTemp.add(i);
				}
			}
			else
			{
				boxGlueItemsTemp.add(i);
			}
		}
		
		boxGlueItems.clear();
		
		// add at beginning; before indent if applicable
		if (getAlignment() == ALIGN_CENTRE_KNUTH)
		{
			// not mentioned in K-P, but otherwise starting glue disappears!
			boxGlueItems.add(new KPEmptyBox(0));
			// (K-P p. 1120 PDF 22)
			boxGlueItems.add(new KPGlue(0, spaceWidth * 3, 0));
		}
		
		// add at beginning
		if (getFirstLineIndent() != 0.0f)
		{
			boxGlueItems.add(new KPEmptyBox(0));
			boxGlueItems.add(new KPGlue(getFirstLineIndent(), 0, 0));
		}

		boxGlueItems.addAll(boxGlueItemsTemp);
		
		// finishing glue, etc.
		if (getAlignment() == ALIGN_FORCE_JUSTIFY)
		{
			// add finishing glue (K-P p. 1124 PDF 6)
//				retVal.add(new KPGlue(0, 0, 0));
			
			// add finishing penalty of -inf (K-P p. 1124 PDF 6)
			boxGlueItems.add(new KPPenalty(-100000));
		}
		else if (getAlignment() == ALIGN_CENTRE_KNUTH)
		{
			// (K-P p. 1120 PDF 22)
			boxGlueItems.add(new KPGlue(0, spaceWidth * 3, 0));
			
			// add finishing penalty of -inf (K-P p. 1124 PDF 6)
			boxGlueItems.add(new KPPenalty(-100000));
		}
		else // ALIGN_JUSTIFY or ALIGN_CENTRE or ALIGN_LEFT or ALIGN_RIGHT
		{
			// (not in K-P) break to inhibit breaking before finishing glue
			boxGlueItems.add(new KPPenalty(+100000));
			
			// add finishing glue (K-P p. 1124 PDF 6)
			boxGlueItems.add(new KPGlue(0, 100000, 0));
			
			// add finishing penalty of -inf (K-P p. 1124 PDF 6)
			boxGlueItems.add(new KPPenalty(-100000));
		}
		
		int dummyy=0;
		
//		removed 2018-06-22 -> multiple spaces are ok at the moment
//		method needs rewriting to ensure leading and trailing glue is preserved (centred, left alignment)
//		correctBlockLevelSpacing();
	}
}

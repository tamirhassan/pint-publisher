package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.Locale;

import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPItem;
import com.tamirhassan.publisher.knuthplass.KPPenalty;
import com.tamirhassan.publisher.stylesheet.PACharFormatting;

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
public class PAFlexSimpleParagraph extends PAFlexParagraph //PAKPTextBlock
{
	Locale locale = Locale.getDefault();
	
//	float lineSpacing = 1.2f; inherited from PAKPTextBlock
		
	String content;
	PACharFormatting cf;
	
	//NodeList styles;
	
	boolean hyphenate = true;
	String hyphenationChar = "-"; // TODO: locale dependent?
	
	public PAFlexSimpleParagraph()
	{
	}
	
	public PAFlexSimpleParagraph(String content, PACharFormatting cf)
	{
		this.content = content;
		this.cf = cf;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public PACharFormatting getCf() {
		return cf;
	}

	public void setCf(PACharFormatting cf) {
		this.cf = cf;
	}

	public boolean isHyphenate() {
		return hyphenate;
	}

	public void setHyphenate(boolean hyphenate) {
		this.hyphenate = hyphenate;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getHyphenationChar() {
		return hyphenationChar;
	}

	public void setHyphenationChar(String hyphenationChar) {
		this.hyphenationChar = hyphenationChar;
	}

	public String textContent()
	{
		return getContent();
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
		//System.out.println("toBoxGlue called with PAFlexParagraph: " + this);
		
		boxGlueItems = new ArrayList<KPItem>();
		
		try
		{
			// TODO: Initial version: Just extract the text, ignoring <b> etc.
			String[] words = content.split("\\s+");
			
			// TODO: different spacing for sentence ends
			// TODO: locale agnostic! (see code of LineFold)
			// TODO: hyphenation!
			
			// TODO: TrueType as well as AFM
/*	
			PDType1Font t1font = (PDType1Font)font;
			
			//System.out.println("a width: " + t1font.getAFM());
			Method method = t1font.getClass().getSuperclass().getSuperclass().
					getDeclaredMethod("getStandard14AFM");
			method.setAccessible(true);
			FontMetrics metric = (FontMetrics)method.invoke(t1font);
			
//			System.out.println("avg width: " + font.getAverageFontWidth());
//			System.out.println("spc width: " + font.getSpaceWidth());
			
			PAFontMetrics fontMetric = new PAFontMetrics(metric);
*/			
//			System.out.println("metric a width: " + fontMetric.getCharWidth('a'));
//			System.out.println("metric spc width: " + fontMetric.getCharWidth(' '));
			
			for (int i = 0; i < words.length; i++)
			{
				if (i > 0)
				{
					// add a space as glue
					float spaceWidth = cf.getCharWidth(' ') * 
							(cf.getFontSize() / 1000) * stretchFactor;
					
					boxGlueItems.add(new KPGlue(spaceWidth, spaceWidth/2, spaceWidth/3));
					// as there is no hyphenation, increase stretchability greatly
					// is unnecessary, as this is only the relative stretchability among other spaces on same line
//					retVal.add(new KPGlue(fontMetric.getCharWidth(' ') * (fontSize / 1000), 5.0f, 0.0f));
				}
				
				// add the word as a box
				float subStringWidth = 
						cf.getStringWidthInPoints(words[i]);
//            	System.out.println("subStringWidth: " + words[i] + ": " + subStringWidth);
				PAWord w = new PAWord(words[i], cf, subStringWidth, cf.getFontSize());
				boxGlueItems.add(w);
//				System.out.println("word: " + words[i] + " at position " + (retVal.size() - 1));
				
			}
			
			if (getAlignment() == ALIGN_FORCE_JUSTIFY)
			{
				// add finishing glue (K-P p. 1124 PDF 6)
//				retVal.add(new KPGlue(0, 0, 0));
				
				// add finishing penalty of -inf (K-P p. 1124 PDF 6)
				boxGlueItems.add(new KPPenalty(-100000));
			}
			else // ALIGN_JUSTIFY
			{
				// (not in K-P) break to inhibit breaking before finishing glue
				boxGlueItems.add(new KPPenalty(+100000));
				
				// add finishing glue (K-P p. 1124 PDF 6)
				boxGlueItems.add(new KPGlue(0, 100000, 0));
				
				// add finishing penalty of -inf (K-P p. 1124 PDF 6)
				boxGlueItems.add(new KPPenalty(-100000));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		/*
		PAKPTextBlock retVal = new PAKPTextBlock(boxGlueItems);
		retVal.setAlignment(alignment);
		return retVal;
		*/
	}
	
	/*
	public PALogParagraph(List<PALogWord> content)
	{
		this.content = content;
	}
	
	public PALogParagraph(String content)
	{
		String[] words = content.toString().split("\\s+");
		
		for (int i = 0; i < words.length; i ++)
		{
			this.content.add(new PALogWord(words[i]));
		}
	}
	
	public List<PALogWord> getContent() {
		return content;
	}

	public void setContent(List<PALogWord> content) {
		this.content = content;
	}
	*/
		
	//@Override
	/**
	 * creates a PAKPLowLevelText object, adds hyphenation
	 * and then runs its layout method
	 */
	/*
	public List<PAFlexLayoutResult> layout(float width, float height,
			float stretchFactor, boolean group, int breakFrom, int breakTo,
			List<List<Integer>> breakpoints, List<Integer> allLegalBreakpoints)
	{
		preLayoutTasks(stretchFactor);
		return super.layout(width, height, stretchFactor, group, 
				breakFrom, breakTo, breakpoints, allLegalBreakpoints);
	}
	*/
	
	
}

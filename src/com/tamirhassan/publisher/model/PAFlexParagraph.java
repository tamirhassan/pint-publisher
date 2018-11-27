package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.id.pbw.hyfo.hyph.HyphenBreak;
import au.id.pbw.hyfo.hyph.HyphenatedWord;
import au.id.pbw.hyfo.hyph.HyphenationTree;
import au.id.pbw.hyfo.hyph.HyphenationTreeCache;

import com.tamirhassan.publisher.knuthplass.KPEmptyBox;
import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPItem;
import com.tamirhassan.publisher.knuthplass.KPPenalty;
import com.tamirhassan.publisher.knuthplass.PAKPTextBlock;
import com.tamirhassan.publisher.stylesheet.PACharFormatting;

public abstract class PAFlexParagraph extends PAKPTextBlock
{
	boolean kerning = true;
	Locale locale = Locale.getDefault();
	boolean hyphenate = true;
	String hyphenationChar = "-"; // TODO: locale dependent?
	
	/**
	 * creates a low-level box-glue representation of the paragraph
	 * 
	 * @param hyphenTree -- pass a null tree to disable hyphenation
	 * @param stretchFactor
	 * @return
	 */
	public abstract void generateBoxGlueItems(float stretchFactor);
	
	public boolean isHyphenate() {
		return hyphenate;
	}

	public void setHyphenate(boolean hyphenate) {
		this.hyphenate = hyphenate;
	}

	public boolean isKerning() {
		return kerning;
	}

	public void setKerning(boolean kerning) {
		this.kerning = kerning;
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
	
	/**
	 * no longer used by PAFlexFormattedParagraph as method is more complicated
	 * requiring removal of leading and trailing spaces afterwards
	 * 
	 * @param spaceWidth
	 */
	protected void addSpace(float spaceWidth)
	{
		if (getAlignment() == ALIGN_LEFT || getAlignment() == ALIGN_CENTRE ||
				getAlignment() == ALIGN_RIGHT)
		{
			// K-P p. 1139 PDF 21 w=0, y=18, z=0
			boxGlueItems.add(new KPGlue(0, spaceWidth * 3.0f, 0));
			boxGlueItems.add(new KPPenalty(0));
			boxGlueItems.add(new KPGlue(spaceWidth, spaceWidth * -3.0f, 0));
		}
		else if (getAlignment() == ALIGN_CENTRE_KNUTH)
		{
			// K-P p. 1140 PDF 22 w=0, y=18, z=0
			boxGlueItems.add(new KPGlue(0, spaceWidth * 3.0f, 0));
			boxGlueItems.add(new KPPenalty(0));
			// K-P p. 1140 PDF 22 w=0, y=-36, z=0
			boxGlueItems.add(new KPGlue(spaceWidth, spaceWidth * -6.0f, 0));
			boxGlueItems.add(new KPEmptyBox(0));
			boxGlueItems.add(new KPPenalty(10000, new KPEmptyBox(0), false));
			boxGlueItems.add(new KPGlue(0, spaceWidth * 3.0f, 0));
		}
		else // ALIGN_JUSTIFY or ALIGN_FORCE_JUSTIFY
		{
			// K-P p. 1124 PDF 6 w=6, y=3, z=2
			boxGlueItems.add(new KPGlue(spaceWidth, spaceWidth/2, spaceWidth/3));
		}
	}
	
	protected Object[] processHyphenatedWord(String thisWord, String hyphenationChar)
	{
		Object[] retVal = new Object[thisWord.length()];
		for (int i = 0; i < retVal.length; i ++)
			retVal[i] = null;
			
		String[] parts = thisWord.split(hyphenationChar);
		
		if (parts.length > 1)
		{
			int sum = -1;
			for (int i = 0; i < parts.length - 1; i ++)
			{
				sum += 1; // for the hyphen
				sum += parts[i].length();
				retVal[sum] = new KPEmptyBox(0);
			}
		}
		
		return retVal;
	}
	
	public void hyphenate(HyphenationTree hyphenTree, String hyphenationChar)
	{
		List<KPItem> newItems = new ArrayList<KPItem>();
		
		for (KPItem item : boxGlueItems)
		{
			if (item instanceof PAWord)
			{
				PAWord thisWord = (PAWord)item;
				PACharFormatting cf = thisWord.getCharFormatting();
				/*
				if (fontMetric == null)
				{
					thisWord.obtainMetric();
					fontMetric = thisWord.getMetric();
				}
				*/
				boolean kerning = cf.isKerning();
				//float stretchFactor = thisWord.getStretchFactor();
				
				// TODO: exception handling if metric unavailable
				/*
				PAWord hyphenationWord = new PAWord(
						hyphenationChar, font, fontSize, fontMetric, stretchFactor);
				*/
				// automatically sets width
				
				// try splitting item
				List<KPItem> splitItems = new ArrayList<KPItem>();
				
				//System.out.println("thisWord: " + thisWord + " with length: " + thisWord.getText().length());
				
				if (thisWord.getText().length() == 0)
				{
					int dummy = 0;
					System.err.println("Attempt to hyphenate a zero-length word");
					// TODO: Catch this error
				}
				
				//System.out.println("thisWord.getText(): " + thisWord.getText());
				
				// TODO: change to array of int. >0 is break, equal to weight. Easier to read.
				Object[] breaks; 
				
				if (hyphenTree != null)
				{
					HyphenatedWord hyph_word =
			                hyphenTree.hyphenate(thisWord.getText());
			        HyphenBreak[] breakpoints = hyph_word.get_string_breakpoints();
			        
			        breaks = new Object[breakpoints.length];
			        for (int i = 0; i < breakpoints.length; i ++)
			        	breaks[i] = breakpoints[i];
				}
				else
				{
					breaks = processHyphenatedWord(thisWord.getText(), hyphenationChar);
				}
				
		        // breakpoints[] is an array with the length of the substring
		        // each position contains either null (no break)
		        // or a HyphenBreak object (hyphenate after this character)
		        
				// 2018-06-28: array changed to type Object
				// to allow processing of hyphenated words (not via hyfo)
				
//		        System.out.println();
//		        System.out.print("hyphenating " + thisWord.getText() + ": ");
		        
		        int prevBreak = -1;
		        for (int j = 0; j < breaks.length; j++) 
		        {
		        	// if there is a valid break at this position, the break object is not null
		        	// TODO: take weights into account (later)
		            if (breaks[j] != null) 
		            {
		            	// hyphenbreak at j=2
		            	// add chars at 0, 1 and 2
		            	// i.e. prevBreak+1 .. 
		            	// save prevBreak as 2
		            	
		            	String subString = thisWord.getText().substring
		            			(prevBreak + 1, j + 1);
		            	prevBreak = j;
		            	
//		            	System.out.print(subString + " ");
		            	
		            	// print all words that are split due to hyphenation (debug)
		            	
		            	if (subString.length() < thisWord.getText().length())
		            		System.out.println("hyphenating: " + thisWord.getText());
		            	
		            	
		            	// create new object for hyphenation box
		            	PAWord hyphenationWord = new PAWord(hyphenationChar, cf);
		            	
		            	float kerningAdj = 0.0f;
		            	
		            	if (kerning && j < breaks.length - 1)
		            	{
		            		// check if there is a kerning adjustment across this hyphenation
		            		kerningAdj = cf.getKerningAdjustment(
		            				thisWord.getText().charAt(j), thisWord.getText().charAt(j + 1));
		            		
		            		if (kerningAdj != 0.0) 
		            		{
		            			System.out.println("kerning across hyphenation boundary! " + kerningAdj);
		            		
			            		// this adjustment is already part of the normal box length
			            		// if hyphenated, we need to deduct this adjustment from the width of the hyphen box
		            			// (note that, as most kerning adjustments are negative, this will lead to the box being wider)
			            		hyphenationWord.setWidth(
			            				cf.getStringWidthInPoints(hyphenationChar) -
			            				(kerningAdj * (cf.getFontSize() / 1000)));
//			            				(cf.getStringWidth(hyphenationChar) - kerningAdj)
//			            				* (cf.getFontSize() / 1000));
			            				//cf.getStringWidthInPoints(hyphenationChar) - kerningAdj);
			            		
			            		System.out.println("hyphenationWord: " + hyphenationWord.getText() + " with width: " + hyphenationWord.getWidth());
		            		}
		            	}
		            	
		            	
		            	// add the substring as a box, adding kerningAdj to account for default (non-hyphenated) case
		            	float subStringWidth = cf.getStringWidthInPoints(subString) + (kerningAdj * (cf.getFontSize() / 1000));
		            	System.out.println("subStringWidth: " + subString + ": " + subStringWidth + " kerning: " + kerning);

		            	PAWord w = new PAWord(subString, cf, subStringWidth, cf.getFontSize());
						newItems.add(w);
//							System.out.println("position " + (retVal.size() - 1) + " box with " + subString);
						
						// add hyphenation penalty
						if (getAlignment() == ALIGN_LEFT || getAlignment() == ALIGN_CENTRE
								|| getAlignment() == ALIGN_RIGHT)
						{
							// TODO: what about stretchFactor?
							float spaceWidth = cf.getCharWidth(' ') * 
									(cf.getFontSize() / 1000);// * stretchFactor;
							
							if (hyphenTree != null)
							{
								newItems.add(new KPPenalty(10000));
								newItems.add(new KPGlue(0, spaceWidth * 3, 0));
								newItems.add(new KPPenalty(500, hyphenationWord, true));
								newItems.add(new KPGlue(0, spaceWidth * -3, 0));
							}
							else
							{
								newItems.add(new KPPenalty(10000));
								newItems.add(new KPGlue(0, spaceWidth * 3, 0));
								// 2018-06-28: using a null empty box causes spacing problems
//								newItems.add(new KPPenalty(500, null, true));
								newItems.add(new KPPenalty(500, new KPEmptyBox(0), true));
								newItems.add(new KPGlue(0, spaceWidth * -3, 0));
							}
						}
						else // ALIGN_JUSTIFY or ALIGN_FORCE_JUSTIFY
						{
							if (hyphenTree != null)
								newItems.add(new KPPenalty(50, hyphenationWord, true));
							else
								newItems.add(new KPPenalty(50, null, true));
						}
						
//							System.out.println("position " + (retVal.size() - 1) + " hyph penalty");
		            }
		        }
		        
		        // add last box to end of word
		        String subString = thisWord.getText().substring
            			(prevBreak + 1, thisWord.getText().length());
            	
            	// add the substring as a box
		        float subStringWidth = cf.getStringWidthInPoints(subString);
		        PAWord w = new PAWord(subString, cf, subStringWidth, cf.getFontSize());
				newItems.add(w);
//					System.out.println("position " + (retVal.size() - 1) + " box with " + subString);
			}
			else
			{
				newItems.add(item);
			}
		}
		
		boxGlueItems = newItems;
	}
	
	/**
	 * generates box-glue items and runs hyphenation
	 * 
	 * @param stretchFactor
	 */
	public void preLayoutTasks(float stretchFactor)
	{
//		PAKPTextBlock boxGlueText = toBoxGlueModel(stretchFactor);
		if (boxGlueItems == null)
			generateBoxGlueItems(stretchFactor);
		
//		System.out.println("boxGlue items");
//		ListUtils.printList(boxGlueText.getItems());
		
		// 2018-06-28: first, insert penalties for hyphenated words
		// regardless of whether hyphenation is carried out
		hyphenate(null, hyphenationChar);
		
		if (hyphenate)
		{
			try
			{
				// this was a class variable -- perhaps move back?
				HyphenationTreeCache hyphenTrees =
		                HyphenationTreeCache.get_cache_instance();
				
				hyphenTrees.add_alias("en", "en_US");
				
				
				System.out.println("generating hyphenation tree with: " + locale.toString());
				HyphenationTree hyphenTree =
		                hyphenTrees.get_hyphenation_tree(locale.toString());
						
	//			boxGlueText.hyphenate(hyphenTree, hyphenationChar);
				hyphenate(hyphenTree, hyphenationChar);
			}
			catch(IOException ioe)
			{
				System.err.println("failed to hyphenate text:");
				ioe.printStackTrace();
			}
		}
	}
	
	public PAFlexLayoutResult layout(float width, float height)
	{
		// TODO: remove stretch factor?
		preLayoutTasks(1.0f);
		return super.layout(width, height);
	}
	
	//@Override
		/**
		 * more general method with fewer parameters
		 */
		/*
		public List<PAFlexLayoutResult> layout(float width, float height,
				float stretchFactor, boolean group) 
		{
			return layout(width, height, stretchFactor, group, -1, -1, null, null);
		}
		 */
		
		/*
		// TODO: resurrect when doing flexible layouts/optimization!
		
		@Override
		public PAFlexLayoutResult layoutLoosest(float width, float height,
				float stretchFactor, int breakFrom, int breakTo,
				List<List<Integer>> breakpoints, List<Integer> allLegalBreakpoints) 
		{
			preLayoutTasks(stretchFactor);
			return super.layoutLoosest(width, height, stretchFactor, breakFrom, breakTo, 
					breakpoints, allLegalBreakpoints);
		}

		@Override
		public PAFlexLayoutResult layoutTightest(float width, float height,
				float stretchFactor, int breakFrom, int breakTo,
				List<List<Integer>> breakpoints, List<Integer> allLegalBreakpoints) 
		{
			preLayoutTasks(stretchFactor);
			return super.layoutTightest(width, height, stretchFactor, breakFrom, breakTo, 
					breakpoints, allLegalBreakpoints);
		}

		*/
		
		/*
		// deprecated -- leading now a property of PATextBlock
		public void setLeading(List<? extends PAPhysObject> items)
		{
			// create a sub-list containing only the lines
			List<PAPhysTextLine> lines = new ArrayList<PAPhysTextLine>();
			for (PAPhysObject l : items)
				if (l instanceof PAPhysTextLine)
					lines.add((PAPhysTextLine) l);
			
			for (PAPhysTextLine l : lines)
			{
				l.setLeading(leading);
			}
		}
		*/
		
		/*
		public void stretchAndShrinkToWidth(List<PAPhysTextLine> items)
		{
			int index = -1;
			for (PAPhysTextLine l : items)
			{
				index ++;
				if (index != items.size() - 1)
					l.stretchAndShrinkToWidth();
			}
		}
		*/
}
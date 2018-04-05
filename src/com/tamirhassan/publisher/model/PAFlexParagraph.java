package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.id.pbw.hyfo.hyph.HyphenBreak;
import au.id.pbw.hyfo.hyph.HyphenatedWord;
import au.id.pbw.hyfo.hyph.HyphenationTree;
import au.id.pbw.hyfo.hyph.HyphenationTreeCache;

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
				HyphenatedWord hyph_word =
		                hyphenTree.hyphenate(thisWord.getText());
		        HyphenBreak[] breaks = hyph_word.get_string_breakpoints();
		        
		        // breaks[] is an array with the length of the substring
		        // each position contains either null (no break)
		        // or a HyphenBreak object (hyphenate after this character)
		        
//		        System.out.println();
//		        System.out.print("hyphenating " + thisWord.getText() + ": ");
		        
		        int prevBreak = -1;
		        for (int j = 0; j < breaks.length; j++) 
		        {
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
						newItems.add(new KPPenalty(50, hyphenationWord, true));
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
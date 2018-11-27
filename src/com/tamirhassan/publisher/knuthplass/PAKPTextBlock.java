package com.tamirhassan.publisher.knuthplass;

//import iiuf.awt.FindListener;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dbai.pdfwrap.utils.ListUtils;

import com.tamirhassan.publisher.model.PAFlexBreakableObject;
import com.tamirhassan.publisher.model.PAFlexLayoutResult;
import com.tamirhassan.publisher.model.PAPhysObject;
import com.tamirhassan.publisher.model.PAPhysTextBlock;
import com.tamirhassan.publisher.model.PAPhysTextLine;
import com.tamirhassan.publisher.model.PAWord;

// Model summary: This class contains the
// K-P boxes only; PAFlexParagraph, which extends
// this class, contains all the text functions, etc.

/**
 * Represents a text object in its low-level form
 * (the box-glue model).
 * 
 * @author tam
 *
 */
public class PAKPTextBlock extends PAFlexBreakableObject
{
	/* 
	// 2018-11-10 moved to PAFlexIncolObject
	
	final public static int ALIGN_LEFT = 0;
	final public static int ALIGN_CENTRE = 1;
	final public static int ALIGN_CENTRE_KNUTH = 11;
	final public static int ALIGN_JUSTIFY = 2;
	final public static int ALIGN_RIGHT = 3;
	final public static int ALIGN_FORCE_JUSTIFY = 4;
	
	int alignment = ALIGN_JUSTIFY;
	*/
	
	float lineSpacing = 1.2f;
	
	float firstLineIndent = 0.0f;
	
	/*
	// probably better to just change box size!
	float leftIndent = 0.0f;
	float rightIndent = 0.0f;
	float firstLineAdjustment = 0.0f;
	*/
	
	// TODO: replace with penalties?
	boolean preventWidows = true;
	boolean preventClubs = true;
	boolean mergeWords = true; // remerges adjacent PAWords e.g. across hyphenation boundaries
	
	int startBreakpointIndex = -1;
	int endBreakpointIndex = -1;
	
	/*
	// the following affect column layout!
	float widowPenalty = 10000;
	float orphanPenalty = 10000;
	float keepWithNextPenalty = 0;
	*/
	
	// TODO: uncertain whether to keep font information here
	// (each PAWord has is own font info ...)
//	PDSimpleFont font;
//	float fontSize;
//	boolean kerning = true;
	
	protected List<KPItem> boxGlueItems;
	
	public PAKPTextBlock()
	{
	}
	
	public PAKPTextBlock(List<KPItem> items)
	{
		this.boxGlueItems = items;
	}

	public PAKPTextBlock copy()
	{
		PAKPTextBlock retVal = new PAKPTextBlock();
		retVal.setBoxGlueItems(this.boxGlueItems);
		retVal.setAlignment(this.alignment);
		retVal.setLineSpacing(this.lineSpacing);
		
		// ID not copied!
		
		retVal.setStartBreakpointIndex(this.startBreakpointIndex);
		retVal.setEndBreakpointIndex(this.endBreakpointIndex);
		
		return retVal;
	}
	
	
	public List<KPItem> getBoxGlueItems() {
		return boxGlueItems;
	}

	public void setBoxGlueItems(List<KPItem> items) {
		this.boxGlueItems = items;
	}

	/*
	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	*/
	
	public float getLineSpacing() {
		return lineSpacing;
	}

	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

	public int getStartBreakpointIndex() {
		return startBreakpointIndex;
	}

	public void setStartBreakpointIndex(int startBreakpointIndex) {
		this.startBreakpointIndex = startBreakpointIndex;
	}

	public int getEndBreakpointIndex() {
		return endBreakpointIndex;
	}

	public void setEndBreakpointIndex(int endBreakpointIndex) {
		this.endBreakpointIndex = endBreakpointIndex;
	}

	public float getFirstLineIndent() {
		return firstLineIndent;
	}

	public void setFirstLineIndent(float firstLineIndent) {
		this.firstLineIndent = firstLineIndent;
	}

	public String textContent()
	{
		if (boxGlueItems.size() == 0)
			return "empty";
		else if (boxGlueItems.size() == 1)
			return ("1 item: ");// + boxGlueItems.get(0).textContent());
		else
			return (boxGlueItems.size() + " items: TODO ");// + boxGlueItems.get(0).textContent() + " ...");
	}
	
	public void removeGlueAtEnd(List<List<KPItem>> lines)
	{
		for (List<KPItem> thisLine : lines)
		{
			// first item should be either a box or a penalty of value -inf (<= -1000)
			List<KPItem> itemsToRemove = new ArrayList<KPItem>();
			for (KPItem item : thisLine)
			{
				// build up list of consecutive glues (unbroken by box)
				// and clear list if a box is encountered
				if (item instanceof KPGlue)
					itemsToRemove.add(item);
				else if (item instanceof KPBox)
					itemsToRemove = new ArrayList<KPItem>();
			}
			
			thisLine.removeAll(itemsToRemove);
		}
	}
	
	
	/**
	 * 
	 * uses the greedy algorithm to fit lines
	 * 
	 * @param width
	 * @return
	 */
	/*
	protected List<PAPhysTextLine> emergencyLineFitting(float width, 
			List<Integer> legalBreakpoints, List<Integer> retBreakIndices)
	{
		// use the greedy algorithm to fit lines left-first
		// (left alignment)

		// removes glue (and runs of glue) at start of lines
		List<Integer> subList = new ArrayList<Integer>();
		
		int sbpi = startBreakpointIndex, ebpi = endBreakpointIndex;
		if (sbpi < 0) sbpi = Integer.MIN_VALUE;
		if (ebpi < 0) ebpi = Integer.MAX_VALUE;
		
		for (int i : legalBreakpoints)
			if (i >= sbpi && i <= ebpi)
				subList.add(i);
		
		List<PAPhysTextLine> retVal = new ArrayList<PAPhysTextLine>();
		PAPhysTextLine l = new PAPhysTextLine();
		float largestFontSize = 0;
		
		int startItemIndex = subList.get(0);
		int endItemIndex = subList.get(subList.size() - 1);
		
		//for (KPItem item : boxGlueItems)
		for (int i = startItemIndex; i <= endItemIndex; i ++)
		{
			KPItem item = boxGlueItems.get(i);
			
			// TODO: look at min/max widths, or overkill?
			float remainingWidth = width - l.contentWidth(true);
		
			if (item instanceof PAWord)
			{
				// clone the item to support further changes (e.g. tracking)
				PAWord wordItem = ((PAWord)item).copy();
				
				if (item.getAmount() <= remainingWidth)
				{
					l.getItems().add(wordItem);
					
					if (wordItem.getFontSize() > largestFontSize)
						largestFontSize = wordItem.getFontSize();
				}
				else if (item.getAmount() > width)
				{
					// too large for next line too!
					// add to current line and, for now, run into margin
					l.getItems().add(wordItem);
					
					if (wordItem.getFontSize() > largestFontSize)
						largestFontSize = wordItem.getFontSize();
					
					// TODO: break at last character, if possible
					// BUT ensure that the algorithm will terminate!
					// (corner case: box narrower than a single character)
				}
				else
				{
					// start a new line; add current line to result
					l.setWidth(width);
					l.setHeight(largestFontSize * lineSpacing);
					l.setTextHeight(largestFontSize);
					retVal.add(l);
					retBreakIndices.add(i);
					
					l = new PAPhysTextLine();
					largestFontSize = 0;
					
					// add to current line (will fit!)
					l.getItems().add(wordItem);
					
					if (wordItem.getFontSize() > largestFontSize)
						largestFontSize = wordItem.getFontSize();
				}
			}
			else if (item instanceof KPGlue)
			{
				// ignore glue at beginning of newline
				if (l.getItems().size() > 0)
				{
					// clone the item to support further changes (e.g. tracking)
					KPGlue glueItem = ((KPGlue)item).copy();
				
					if (item.getAmount() <= remainingWidth)
					{
						// add to current line
						l.getItems().add(glueItem);
					}
					else if (item.getAmount() > width)
					{
						// too large for next line too!
						// add to current line and, for now, run into margin
						// (should not happen with glue, anyway!)
						l.getItems().add(glueItem);
					}
					else
					{
						// start a new line; add current line to result
						l.setWidth(width);
						l.setHeight(largestFontSize * lineSpacing);
						l.setTextHeight(largestFontSize);
						retVal.add(l);
						retBreakIndices.add(i);
						
						l = new PAPhysTextLine();
						largestFontSize = 0;
						
						// no need to add glue - will be ignored in next line
					}
				}
			}
			else 
			{
				// TODO: penalties are currently ignored here - add them?
			}
		}
		
		// add last line to result
		if (l.getItems().size() > 0)
		{
			l.setWidth(width);
			l.setHeight(largestFontSize * lineSpacing);
			l.setTextHeight(largestFontSize);
			retVal.add(l);
			retBreakIndices.add(boxGlueItems.size() - 1);
		}
		
		// TODO: justify or centre afterwards
		return retVal;
	}
	*/
	
	/**
	 * // merges PAWords across hyphenation boundaries to enable kerning
	 * 
	 * @param items
	 */
	protected void mergeBoxItems(List<PAPhysObject> items)
	{
		for (int i = 1; i < items.size(); i ++)
		{
			PAPhysObject thisItem = items.get(i);
			PAPhysObject prevItem = items.get(i - 1);
			
			if (thisItem instanceof PAWord)
			{
				if (prevItem instanceof PAWord)
				{
					PAWord prevWord = (PAWord)prevItem;
					PAWord thisWord = (PAWord)thisItem;
					
					// should be the same font object if previously split due to hyphenation
					// but better to be safe
					// TODO: "same font/typography" util method?
					if (prevWord.getCharFormatting().isSameFont(thisWord.getCharFormatting()))
					{
						prevWord.setText(prevWord.getText().concat(thisWord.getText()));
						prevWord.setWidth(prevWord.getWidth() + thisWord.getWidth());
						items.remove(i);
						i --;
					}
				}
			}
			else if (thisItem instanceof KPGlue)
			{
				// do nothing
			}
			else
			{
				// should not get here!
				System.out.println("should not have gotten here!");
			}
		}
	}
	
	/**
	 * 
	 * this duplicates (clones) the KPItems!
	 * 
	 * @param result
	 * @param width
	 * @return
	 */
	protected List<PAPhysTextLine> generateLineObjects(KPResult result, float width)
	{
		List<PAPhysTextLine> retVal = new ArrayList<PAPhysTextLine>();
		
		// otherwise justification gets messed up!
		//     now performed in KnuthPlass, when calculating adjacency ratio
		//     not necessary here any more.
		//removeGlueAtEnd(result.getItems());
		
		System.out.println("in generateLineObjects with objects: " + result.getItems().size());
		
		// TODO: check whether penalties and glue at start are removed
		int index = -1;
		for (List<KPItem> thisLine : result.getItems())
		{
			index ++;
			double adjRatio = result.getAdjRatios().get(index);
			
			// can occur with empty lines containing no stretchability
			// obviously, values of infinity break rendering later on
			// but this must be done earlier
			if (!Double.isFinite(adjRatio))
				adjRatio = 0;
			
			PAPhysTextLine l = new PAPhysTextLine();
			float largestFontSize = 0;
			
			for (KPItem item : thisLine)
			{
				if (item instanceof PAWord)
				{
					// clone the item to support further changes (e.g. tracking)
					PAWord wordItem = ((PAWord)item).copy();
					
					l.getItems().add(wordItem);
					
					if (wordItem.getCharFormatting().getFontSize() > largestFontSize)
						largestFontSize = wordItem.getCharFormatting().getFontSize();
				}
				else if (item instanceof KPGlue)
				{
					//l.getItems().add((KPGlue)item);
					KPGlue glueItem = (KPGlue)item;
					
					l.getItems().add(new KPGlue(glueItem, (float) adjRatio));
					//l.getItems().add(new PAPhysSpace(glueItem, (float) adjRatio));
				}
				else if (item instanceof KPPenalty)
				{
					// assumes that penalties inside of line have already been removed
					
					KPBox addBox = ((KPPenalty)item).getAdditionalBox();
					if (addBox != null) // do not add if null!
					{
						// all instantiable types are of type PAPhysObject:
						// PAWord and KPEmptyBox
						l.getItems().add(((PAPhysObject)addBox));
						
						if (addBox instanceof PAWord)
						{
							PAWord addWord = (PAWord)addBox;
							if (addWord.getCharFormatting().getFontSize() > largestFontSize)
							{
								largestFontSize = addWord.getCharFormatting().getFontSize();
							}
						}
					}
				}
			}
			
			l.setWidth(width);
			if (l.contentWidth() < width)
				l.setWidth(l.contentWidth());
			l.setHeight(largestFontSize * lineSpacing);
			l.setTextHeight(largestFontSize);
//			l.setSpacingAfter(largestFontSize * (1 - lineSpacing));
			l.setDemerits(result.getLineDemerits().get(index));
			// TODO: enable also fixed line height
			
			System.out.println("for line: " + l.toString());
			System.out.println("adjRatio: " + result.getAdjRatios().get(index));
			
			boolean finalLine = false;
			if (index == (result.getItems().size() - 1)) finalLine = true;
			
			fixAlignment(l, thisLine, finalLine);
			
			if (mergeWords)
			{
				mergeBoxItems(l.getItems());
			}
			
			retVal.add(l);
		}
			
		return retVal;
	}
	
	protected void fixAlignment(PAPhysTextLine l, List<KPItem> kpItems, boolean finalLine)
	{
		if (alignment == ALIGN_LEFT || alignment == ALIGN_CENTRE)
		{
			// if line ends in a hyphenation penalty,
			// swap the final two items
			
			if (!finalLine) // do not process last line
			{
				if (l.getItems().size() > 1 && 
						kpItems.get(kpItems.size() - 1) instanceof KPPenalty)
				{
					// only flagged penalties are hyphenations
					if (((KPPenalty)kpItems.get(kpItems.size() - 1)).isFlag())
					{
						PAPhysObject hyphen = l.getItems().remove(l.getItems().size() - 1);
						l.getItems().add(l.getItems().size() - 1, hyphen);
					}
				}
			}
		}
		if (alignment == ALIGN_CENTRE)
		{
			
			// remove glues at end (penalties too, but they shouldn't be here)
			while(l.getItems().size() > 0 &&
					!(l.getItems().get(l.getItems().size() -1) instanceof KPBox)) 
			{
				l.getItems().remove(l.getItems().size() -1);
			}
			
			// add glue equally to beginning and end of line
			/*
			if (l.getItems().size() > 0)
			{
				// 2018-06-29 contentWidth() method rewritten
//				float glueWidth = (l.getWidth() - l.contentWidth(true)) / 2.0f;
				float glueWidth = (l.getWidth() - l.contentWidth()) / 2.0f;
				
				l.getItems().add(0, new KPGlue(glueWidth));
				l.getItems().add(l.getItems().size(), new KPGlue(glueWidth));
			}
			*/
		}
		else if (alignment == ALIGN_CENTRE_KNUTH)
		{
			// do nothing
		}
		else if (alignment == ALIGN_FORCE_JUSTIFY)
		{
			// do nothing
		}
		else if (alignment == ALIGN_JUSTIFY)
		{
			// do nothing
		}
	}
	
	// distributes space evenly before and after line in case of 
	// centre (and later right) alignment
	protected void fixCentreAlignment(List<PAPhysTextLine> lines, float width)
	{
		for (PAPhysTextLine l : lines)
		{
			if (alignment == ALIGN_CENTRE)
			{
				// add glue equally to beginning and end of line
				
				if (l.getItems().size() > 0)
				{
					// 2018-06-29 contentWidth() method rewritten
//					float glueWidth = (l.getWidth() - l.contentWidth(true)) / 2.0f;
					float glueWidth = (width - l.contentWidth()) / 2.0f;
					
					l.getItems().add(0, new KPGlue(glueWidth));
					l.getItems().add(l.getItems().size(), new KPGlue(glueWidth));
				}
			}
			else if (alignment == ALIGN_RIGHT)
			{
				// do nothing (yet)
			}
		}
	}
	
	// TODO: combine these two methods with layout somehow
	/*
	public List<PAPhysTextLine> layoutTextBlockKPWidth(
			float width, float stretchFactor) 
			throws IOException, LineFoldException, FontFormatException
	{
		KnuthPlass kp = new KnuthPlass(items);
		
		// try to find a layout with max stretchability = 4,
		//                               shrinkability = 1.5
		List<List<List<KPItem>>> fitLineGroups = 
				kp.fitLines(width, 4.0f, 1.5f, -1, -1, false);
		
		// if no possible layout found, enable excessively wide gaps and take the best, single result
		if (fitLineGroups.size() == 0)
			fitLineGroups = kp.fitLines(width, Integer.MAX_VALUE, 1.5f, -1, -1, false);
		
		// TODO: deal with the problem of single word being too wide for column
		
		// kp.fitLines returns only one item in list when called with group==false
		
		return generateLineObjects(fitLineGroups.get(0), width);
	}
	
	public List<List<PAPhysTextLine>> layoutTextBlockKPWidthFlex(
			float width, float stretchFactor) 
			throws IOException, LineFoldException, FontFormatException
	{
		KnuthPlass kp = new KnuthPlass(items);
		
		// try to find a layout with max stretchability = 4,
		//                               shrinkability = 1.5
		List<List<List<KPItem>>> fitLineGroups = 
				kp.fitLines(width, 4.0f, 1.5f, -1, -1, true);
		
		// if no possible layout found, enable excessively wide gaps and take the best, single result
		if (fitLineGroups.size() == 0)
			fitLineGroups = kp.fitLines(width, Integer.MAX_VALUE, 1.5f, -1, -1, true);
		
		// TODO: deal with the problem of single word being too wide for column
		
		// kp.fitLines returns only one item in list when called with group==false
		
		List<List<PAPhysTextLine>> retVal = new ArrayList<List<PAPhysTextLine>>();
		for (List<List<KPItem>> fitLines : fitLineGroups)
		{
			// add the whole list as a single list item
			retVal.add(generateLineObjects(fitLineGroups.get(0), width));
		}
		return retVal;
	}
	*/
	
	// this was a test method
	// TODO: reactivate later
	/*
	public List<PAPhysTextLine> layoutTextBlockKPWidthFlex(
			float width, float stretchFactor) 
			throws IOException, LineFoldException, FontFormatException
	{
		
		
		KnuthPlass kp = new KnuthPlass(items);
		
		List<List<List<KPItem>>> fitLineGroups = kp.fitLines(width, 4, 1.5f, -1, -1, true);
		
		// if no possible layout found, enable excessively wide gaps and take the best, single result
		if (fitLineGroups.size() == 0)
			fitLineGroups = kp.fitLines(width, Integer.MAX_VALUE, 1.5f, -1, -1, false);

		// TODO: deal with the problem of single word being too wide for column
		
		List<PAPhysTextLine> retVal = new ArrayList<PAPhysTextLine>();
		
		for (List<List<KPItem>> fitLines : fitLineGroups)
		{
			System.out.println("group with " + fitLines.size() + " lines.");
			retVal.addAll(generateLineObjects(fitLines, width));
		}
		
		return retVal;
	}
	*/
	
	public PAFlexLayoutResult layout(float width)
	{
		return this.layout(width, Float.MAX_VALUE); //, -1, -1);
	}
	
	/*
	public PAFlexLayoutResult layout(float width, float height)
	{
		return this.layout(width, height, -1, -1);
	}
	*/
	
	/**
	 * class enabling layout of part of a paragraph
	 * by specifying breaks, etc.
	 * 
	 * changed from bp index (valid bps: 1,2,3, ...)
	 * to bp char index (valid bps e.g. 1, 3, 5, ...)!
	 * 
	 * @param breakFrom: bp char index, inclusive, -1 if from start
	 * @param breakTo: bp char index, _inclusive_, -1 if to end
	 * @param breakpoints: pass empty list to obtain the breakpoint indices; otherwise null
	 */
	public PAFlexLayoutResult layout(float width, float height) { //, int startBreakpointIndex, int endBreakpointIndex)	{
		
		System.out.println("in layout of KP " + this.getClass());
		
//		try 
//		{
			// TODO: if ending at a hyphenation, turn this into the final break
			KnuthPlass kp = new KnuthPlass(boxGlueItems);
		
			// TODO: move these variables away from hard code
			// try to find a layout with max stretchability = 4,
			//                               shrinkability = 1.5
			// List<KPResult> fitLineGroups = new ArrayList<KPResult>();
			KPResult fitLines = null;
			
			// would be done automatically later, but we need the breakpoints here too
			kp.generateLegalBreakpointIndices();
			
			// use this list to collect breakpoint list, even if no valid list
			// is passed, as we need this list for later index generation regardless
			// WHY?
			// List<Integer> tempLegalBreakpoints = kp.getLegalBreakpoints();

			System.out.println("normal startBPIndex: " + startBreakpointIndex + 
					" endBPIndex: " + endBreakpointIndex);
			
			List<KPResult> allResults = kp.runKnuthPlass(width, 3.0f, 1.0f,
					-1, -1, false, startBreakpointIndex, endBreakpointIndex);
			
			// if no possible layout found, 
			// enable excessively wide gaps and take the best, single result
			if (allResults.size() == 0)
			{
				System.out.println("allResults = 0");
				allResults = kp.runKnuthPlass(width, Integer.MAX_VALUE, 1.5f,
						-1, -1, false, startBreakpointIndex, endBreakpointIndex);
				System.out.println("allResults new = " + allResults.size());
				if (allResults.size() == 0)
				{
					int dummy = 0;
				}
			}
			
			fitLines = kp.findBestResult(allResults);
			
			// above commented code carried out by below
			//fitLines = kp.fitLinesSingle(width, 3.0f, 1.0f, -1, -1, null);
			
			// TODO: deal with the problem of single word being too wide for column
			
			//System.out.println("boxGlueItems: " + boxGlueItems.size() + " width: " + width + "allResults: " + allResults + " fitLines: " + fitLines);
			
			List<PAPhysTextLine> laidOutLines;
			boolean emergencyFit = false;
			List<Integer> retBreakIndices = new ArrayList<Integer>();
			
			// TODO: improve code structure here!
			if (fitLines == null)
			{
				// no K-P result, even after relaxing constraints
				// Either is a word longer than a line, or too few
				// words to fill a justified line
				// use greedy line-fitting, breaking at any character
				// if necessary
				
				System.out.println("emergency fitting with startBPIndex: " + startBreakpointIndex + 
						" endBPIndex: " + endBreakpointIndex + " and text: " + this.textContent());
				
				ListUtils.printList(this.boxGlueItems);
				// TODO: give warning! (serious one)
				
				allResults = kp.emergencyFit(width, Integer.MAX_VALUE, 1.5f,
						-1, -1, false, startBreakpointIndex, endBreakpointIndex);
				
				fitLines = allResults.get(0);
				emergencyFit = true;
			}
			
			laidOutLines = generateLineObjects(fitLines, width);
			
			// TODO: find out target width of physical object
			// if less than width, actual width (e.g. one-liner)
			// else equal to (desired input) width
			
			float maxLineWidth = 0;
			for (PAPhysTextLine l : laidOutLines)
				if (l.getWidth() > maxLineWidth)
					maxLineWidth = l.getWidth();
			
			float targetWidth = width;
			if (maxLineWidth < width) targetWidth = maxLineWidth;
			
			PAPhysTextBlock result = new PAPhysTextBlock();
			result.setWidth(targetWidth);
			result.setAlignment(alignment);
			result.setFlexID(id);
			result.setItems(laidOutLines);
			
			fixCentreAlignment(laidOutLines, targetWidth);
			
			// do not include last line's spaceAfter in height calculation
			if (result.contentHeight(false) > height)
			{
				//System.out.println("result.contentHeight: " + result.contentHeight(false));
				//System.out.println("# laidOutLines: " + laidOutLines.size());
				//System.out.println("height: " + height);
				
				// generate partial result block
				result = new PAPhysTextBlock();
				result.setWidth(width);
				result.setAlignment(alignment);
				result.setFlexID(id);
				
				// TODO: copy the lines and readjust their width!
				
//				List<PAPhysTextLine> remainingContent = new ArrayList<PAPhysTextLine>();
				
				while (result.contentHeight(true) 
						+ laidOutLines.get(0).getTextHeight() <= height)
				{
					// remove first item and add to result
					result.getItems().add(laidOutLines.remove(0));
				}
				
				// laidOutLines is now the remaining content
				
				if (result.getItems().size() == 0)
				{
					// return empty result with exit status insufficient height
					
					result = null;
					return new PAFlexLayoutResult(result, -1, null, 
							PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT);
				}
				
				// check if only single line of >=2 line block (orphan)
				else if (preventWidows && result.getItems().size() == 1
						&& laidOutLines.size() > 0)
				{
					// return empty result with exit status insufficient height
					
					result = null;
					return new PAFlexLayoutResult(result, -1, null, 
							PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT);
				}
				
				// check if only last line missing (widow)
				else if (preventClubs && laidOutLines.size() == 1) 
				{
					if (result.getItems().size() >= 3)
					{
						// remove last line from result to ensure at least two
						// lines in remaining content
						PAPhysTextLine l = 
								result.getItems().remove(result.getItems().size() - 1);
						
						// unnecessary as this is no longer used
						laidOutLines.add(0, l);
					}
					
					else
					{
						// 2+1 or 1+2; neither solution is valid
						// return empty result with exit status insufficient height
						
						result = null;
						return new PAFlexLayoutResult(result, -1, null, 
								PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT);
					}
				}
				
				PAKPTextBlock remainingContent = this.copy();
				int remainingContentBP;
				
				// get breakpoint at nth line (where n = number of lines in result)
				remainingContentBP = fitLines.getBreakpoints().
						get(result.getItems().size() - 1);
				
				remainingContent.setStartBreakpointIndex(remainingContentBP);
				result.setHeight(result.contentHeight(true));
				System.out.println("bboxGlueItems: " + boxGlueItems.size() + " width: " + width + "allResults: " + allResults + " fitLines: " + fitLines);
				
				// return result with exit status partial success
				PAFlexLayoutResult retVal = new PAFlexLayoutResult(result, -1, 
						remainingContent, PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS);
				
				return retVal;
			}
			
			result.setHeight(result.contentHeight(true));
			
			// to delete! added to test indentation debugging
			/*
			PAPhysTextLine firstLine = result.getItems().get(0);
			KPGlue quartInchIndent = new KPGlue(18.0f);
			firstLine.getItems().add(0, quartInchIndent);
			*/
			
//			fixAlignment(result);
			
			PAFlexLayoutResult retVal = new PAFlexLayoutResult(result, -1, null, 
					PAFlexLayoutResult.ESTAT_SUCCESS);
			
			System.out.println("returning result of KP " + this.getClass());
			
			return retVal;
			

//		} 
//		catch (Exception e) 
//		{
//			fark
//			e.printStackTrace();
//		} 
		
//		System.out.println("number of low level text layouts: " + retVal.size());

	}
	
	/*
	protected void fixAlignment(PAPhysTextBlock result)
	{
		int index = -1;
		for (PAPhysTextLine l : result.getItems())
		{
			index ++;
			
			if (alignment == ALIGN_LEFT || alignment == ALIGN_CENTRE)
			{
				// if line ends in a hyphenation penalty,
				// swap the final two items
				
				if (index < (result.getItems().size() - 1)) // do not process last line
				{
					if (l.getItems().size() > 1 && 
							l.getItems().get(l.getItems().size() - 1) instanceof KPPenalty)
					{
						PAPhysObject hyphen = l.getItems().remove(l.getItems().size() - 1);
						l.getItems().add(l.getItems().size() - 2, hyphen);
					}
				}
			}
			if (alignment == ALIGN_CENTRE)
			{
				
			}
			else if (alignment == ALIGN_CENTRE_KNUTH)
			{
				// do nothing
			}
			else if (alignment == ALIGN_FORCE_JUSTIFY)
			{
				// do nothing
			}
			else if (alignment == ALIGN_JUSTIFY)
			{
				// do nothing
			}
			
//			l.mergeWords();
		}
	}
	*/
	
	
	// TODO move to PAPhysTextBlock? But first make layout return a PAPhysTextBlock ...
	/*
	public void alignPhysLines(List<? extends PAPhysObject> items)
	{
		// create a sub-list containing only the lines
		List<PAPhysTextLine> lines = new ArrayList<PAPhysTextLine>();
		for (PAPhysObject l : items)
			if (l instanceof PAPhysTextLine)
				lines.add((PAPhysTextLine) l);
		
		int index = -1;
		for (PAPhysTextLine l : lines)
		{
			index ++;
			
			// TODO: other alignments
			
			if (alignment == ALIGN_FORCE_JUSTIFY)
			{
				l.stretchToWidth(); // shrinks too
			}
			else if (alignment == ALIGN_JUSTIFY)
			{
				if (index != lines.size() - 1)
					l.stretchToWidth();
			}
			
			l.mergeWords();
		}
	}
	*/
}

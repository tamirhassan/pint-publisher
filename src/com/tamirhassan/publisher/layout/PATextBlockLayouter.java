package com.tamirhassan.publisher.layout;

import java.util.ArrayList;
import java.util.List;

import com.tamirhassan.publisher.knuthplass.KPBox;
import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPItem;
import com.tamirhassan.publisher.knuthplass.KPPenalty;
import com.tamirhassan.publisher.knuthplass.KPResult;
import com.tamirhassan.publisher.knuthplass.KnuthPlass;
import com.tamirhassan.publisher.knuthplass.PAKPTextBlock;
import com.tamirhassan.publisher.model.PAPhysTextBlock;
import com.tamirhassan.publisher.model.PAPhysTextLine;
import com.tamirhassan.publisher.model.PAWord;

// N.B.: beforehand, all layout results were stored using the class PAFlexLayoutResult
// now, the layouter stores all other output fields that can be queried afterwards

public class PATextBlockLayouter {

	// TODO: later move to general layouting class for breakable blocks?
	final public static int LAYOUT_SINGLE = 11;
	final public static int LAYOUT_FLEX = 12;
	final public static int LAYOUT_LOOSEST = -1;
	final public static int LAYOUT_TIGHTEST = 1;
	
	float stretchabilityAdjust = 3.0f;
	float shrinkabilityAdjust = 1.0f;
	
	// are used at line level for column breaking
	// TODO: move to a state object
	/*
	float widowPenalty = 10000;
	float orphanPenalty = 10000;
	float keepWithNextPenalty = 0;
	*/
	
	double lastDemerits;
	List<Double> lastDemeritsList;
	
	// TODO: probably irrelevant for laying out text blocks
	/*
	List<PAFlexObject> lastRemainingContent;
	List<List<PAFlexObject>> lastRemainingContentList;
	*/
	
	/**
	 * greedy, first-fit algorithm
	 * 
	 * @param input
	 * @return
	 */
	public PAPhysTextBlock simpleLayout(PAKPTextBlock input, float width)
	{
		return null;
	}
	
	public double getLastDemerits() {
		return lastDemerits;
	}

	public List<Double> getLastDemeritsList() {
		return lastDemeritsList;
	}

	/**
	 * returns single, optimal K-P result
	 * 
	 * @param input
	 * @return
	 */
	public PAPhysTextBlock knuthPlassLayout(PAKPTextBlock input, float width)
	{
		List<PAPhysTextBlock> resultList = 
				knuthPlassLayouts(input, width, LAYOUT_SINGLE, -1, -1, null, null);
		
		if (resultList.size() > 0)
			return resultList.get(0);
		else
			return null;
	}
	
	/**
	 * returns several K-P results within bounds
	 * 
	 * @param input
	 * @return
	 */
	protected List<PAPhysTextBlock> knuthPlassLayouts(PAKPTextBlock input, float width, int layoutMode,
			int breakIndexFrom, int breakIndexTo, List<List<Integer>> breakpoints, 
			List<Integer> allLegalBreakpoints)
	{
		List<PAPhysTextBlock> retVal = new ArrayList<PAPhysTextBlock>();
		
		KnuthPlass kp = new KnuthPlass(input.getBoxGlueItems());
		
		// TODO: move these variables away from hard code
		// try to find a layout with max stretchability = 4,
		//                               shrinkability = 1.5
		List<KPResult> fitLineGroups = new ArrayList<KPResult>();

		// would be done automatically later, but we need the breakpoints here too
		kp.generateLegalBreakpointIndices();
		
		// use this list to collect breakpoint list, even if no valid list
		// is passed, as we need this list for later index generation regardless
		List<Integer> tempLegalBreakpoints = kp.getLegalBreakpoints();
					
		if (allLegalBreakpoints != null)
			allLegalBreakpoints.addAll(tempLegalBreakpoints);
		
		//List<KPResult> allResults = kp.runKnuthPlass(width, 4.0f, 1.5f,
		//		-1, -1, true, breakIndexFrom, breakIndexTo);
		
		List<KPResult> allResults = kp.runKnuthPlass(width, 3.0f, 1.0f,
				-1, -1, true, breakIndexFrom, breakIndexTo);
		
//		System.out.println("aa allResults: " + allResults.size());
		
		if (allResults.size() > 0)
		{
			List<List<KPResult>> resultGroups = kp.groupResults(allResults);
			
			if (layoutMode == LAYOUT_LOOSEST)
			{
				fitLineGroups.add(kp.findLoosestFit(resultGroups));
			}
			else if (layoutMode == LAYOUT_TIGHTEST)
			{
				fitLineGroups.add(kp.findTightestFit(resultGroups));
			}
			else if (layoutMode == LAYOUT_FLEX)
			{
//				fitLineGroups = 
//						kp.fitLinesFlex(width, 4.0f, 1.5f, -1, -1, numLegalBreakpoints);
				for (List<KPResult> group : resultGroups)
					fitLineGroups.add(kp.findBestResult(group));
			}
			else // layoutMode == LAYOUT_SINGLE
			{
//				fitLineGroups.add(kp.fitLinesSingle(width, 4.0f, 1.5f, -1, -1,
//						numLegalBreakpoints));
				fitLineGroups.add(kp.findBestResult(allResults));
			}
			
//			System.out.println("no fit line groups: " + fitLineGroups.size());
		}
		
		// if no possible layout found, 
		// enable excessively wide gaps and take the best, single result
		if (fitLineGroups.size() == 0)
			fitLineGroups.add(kp.fitLinesSingle(width, Integer.MAX_VALUE, 1.5f, 
					-1, -1, null)); // no need to redo allLegalBreakpoints
		
		// TODO: deal with the problem of single word being too wide for column
		
		// generate the line objects based on the K-P objects
		// (i.e. remove penalties and extraneous glue)
		// new, empty list for lastDemeritsList
		lastDemeritsList = new ArrayList<Double>();
		List<List<PAPhysTextLine>> laidOutLineGroups = new ArrayList<List<PAPhysTextLine>>();
		for (KPResult fitLines : fitLineGroups)
		{
//			System.out.println("sppp fitLines: " + fitLines.getItems().size());
			
			// add the whole list as a single list item
//			laidOutLineGroups.add(generateLineObjects(fitLineGroups.get(0), width));
			List<PAPhysTextLine> laidOutLines = 
					generateLineObjects(fitLines, width, input.getLineSpacing());
			
//			System.out.println("sppp laidOutLines: " + laidOutLines.size());
		
			// was PAPhysObject!
			List<PAPhysTextLine> layoutResult = new ArrayList<PAPhysTextLine>();
			
			int index = -1;
			float resultHeight = 0;
			for (PAPhysTextLine l : laidOutLines)
			{
				index ++;
				layoutResult.add(l);
				resultHeight += l.getHeight();
	
			}
			
			// should already be aligned
			//alignPhysLines(layoutResult);
			
//			setLeading(layoutResult);
			
			PAPhysTextBlock result = new PAPhysTextBlock(layoutResult, width, resultHeight);
			retVal.add(result);
			lastDemerits = fitLines.getDemerits();
			lastDemeritsList.add(fitLines.getDemerits());
			
//			System.out.println("sppp layoutResult with " + result.getItems().size() + " lines.");
			
			// add breakpoints to passed list
			if (breakpoints != null)
			{
				// TODO: don't add the breakpoints themselves
				// but obtain their indices according to the
				// legal breakpoint list
				
//				breakpoints.add(fitLines.getBreakpoints());
				
				List<Integer> breakpointIndices = new ArrayList<Integer>();
				for (Integer bp : fitLines.getBreakpoints())
					breakpointIndices.add(tempLegalBreakpoints.indexOf(bp));
				
				breakpoints.add(breakpointIndices);
			}
		}
		return retVal;
	}
	
	public PAPhysTextBlock knuthPlassLoosest(PAKPTextBlock input, float width)
	{
		List<PAPhysTextBlock> resultList = 
				knuthPlassLayouts(input, width, LAYOUT_LOOSEST, -1, -1, null, null);
		
		if (resultList.size() > 0)
			return resultList.get(0);
		else
			return null;
	}
	
	public PAPhysTextBlock knuthPlassTightest(PAKPTextBlock input, float width)
	{
		List<PAPhysTextBlock> resultList = 
				knuthPlassLayouts(input, width, LAYOUT_TIGHTEST, -1, -1, null, null);
		
		if (resultList.size() > 0)
			return resultList.get(0);
		else
			return null;
	}

	public float getStretchabilityAdjust() {
		return stretchabilityAdjust;
	}

	public void setStretchabilityAdjust(float stretchabilityAdjust) {
		this.stretchabilityAdjust = stretchabilityAdjust;
	}

	public float getShrinkabilityAdjust() {
		return shrinkabilityAdjust;
	}

	public void setShrinkabilityAdjust(float shrinkabilityAdjust) {
		this.shrinkabilityAdjust = shrinkabilityAdjust;
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
	
	protected List<PAPhysTextLine> generateLineObjects(KPResult result, float width, float lineSpacing)
	{
		List<PAPhysTextLine> retVal = new ArrayList<PAPhysTextLine>();
		
		// otherwise justification gets messed up!
		removeGlueAtEnd(result.getItems());
		
		// TODO: check whether penalties and glue at start are removed
		int index = -1;
		for (List<KPItem> thisLine : result.getItems())
		{
			index ++;
			double adjRatio = result.getAdjRatios().get(index);
			PAPhysTextLine l = new PAPhysTextLine();
			float largestFontSize = 0;
			
			for (KPItem item : thisLine)
			{
				if (item instanceof PAWord)
				{
					// clone the item to support further changes (e.g. tracking)
					PAWord wordItem = ((PAWord)item).copy();
					
					l.getItems().add(wordItem);
					
					if (wordItem.getFontSize() > largestFontSize)
						largestFontSize = wordItem.getFontSize();
				}
				else if (item instanceof KPGlue)
				{
					//l.getItems().add((KPGlue)item);
					double spaceAmount = ((KPGlue)item).getAmount() * adjRatio;
					l.getItems().add(new PAPhysHSpace((float)spaceAmount));
				}
				else if (item instanceof KPPenalty)
				{
					// assumes that penalties inside of line have already been removed
					
					PAWord addBox = (PAWord) ((KPPenalty)item).getAdditionalBox();
					if (addBox != null) // do not add if null!
						l.getItems().add(addBox);
					
					if (addBox != null && addBox instanceof PAWord)
						if (addBox.getFontSize() > largestFontSize)
							largestFontSize = addBox.getFontSize();
				}
			}
			
			l.setWidth(width);
			l.setHeight(largestFontSize * lineSpacing);
			l.setTextHeight(largestFontSize);
			l.setSpacingAfter(largestFontSize * (1 - lineSpacing));
			l.setDemerits(result.getLineDemerits().get(index));
			// TODO: enable also fixed line height
			
			retVal.add(l);
		}
			
		return retVal;
	}
	
}

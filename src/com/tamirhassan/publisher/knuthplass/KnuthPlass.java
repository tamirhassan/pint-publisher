package com.tamirhassan.publisher.knuthplass;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.tamirhassan.publisher.model.PAWord;


import at.ac.tuwien.dbai.pdfwrap.utils.ListUtils;

// TODO: penalties and hyphenation!

public class KnuthPlass 
{
	// input list of objects
	List<KPItem> lineObjects = new ArrayList<KPItem>();
	
	float addedStretchability = 0;
	float addedShrinkability = 0;
	
	protected List<Integer> legalBreakpoints;
	
	// output list of lists of objects
	
	// box for each individual character
	// penalty object for possible hyphens
	
	// legal breakpoint is b such that x(b) is
	// either a penalty item with p(b) < +inf OR
	// x(b) is a glue item and x(b-1) a box
//	protected boolean[] isLegalBreakpoint;
	
	// i.e. breaks are only possible at a penalty
	// (non-inf) or at glue following a box
	
	public KnuthPlass(List<KPItem> lineObjects)
	{
		this.lineObjects = lineObjects;
	}
	
	public List<KPItem> getLineObjects() {
		return lineObjects;
	}

	public void setLineObjects(List<KPItem> lineObjects) {
		this.lineObjects = lineObjects;
	}

	public float getAddedStretchability() {
		return addedStretchability;
	}

	public void setAddedStretchability(float addedStretchability) {
		this.addedStretchability = addedStretchability;
	}

	public float getAddedShrinkability() {
		return addedShrinkability;
	}

	public void setAddedShrinkability(float addedShrinkability) {
		this.addedShrinkability = addedShrinkability;
	}

	/**
	 * 
	 * @param numLegalBreakpoints - if passed, sets with total number of legal breakpoints found
	 * @return
	 */
	public void generateLegalBreakpointIndices()
	{
		// find a list of legal breakpoints
		// as indices of lineObjects
		legalBreakpoints = new Vector<Integer>(lineObjects.size() / 3);
		
		// first breakpoint is at the beginning, before the first item
		legalBreakpoints.add(-1);
//		System.out.println("first BP at position -1 with index 0");
		
		int legalBreakpointIndex = 0;
		
		// TODO: don't recalculate if already set?
		for (int i = 0; i < lineObjects.size(); i ++)
		{
//			System.out.println("LO " + i + " : " + lineObjects.get(i));
			
			// legal breakpoint is b such that x(b) is
			// either a penalty item with p(b) < +inf OR ...
			if (lineObjects.get(i) instanceof KPPenalty)
				if (((KPPenalty)lineObjects.get(i)).amount < 1000)
				{
//					System.out.println("LO penalty bp at " + i);
					legalBreakpoints.add(i);
					legalBreakpointIndex ++;
//					System.out.println("with index: " + legalBreakpointIndex);
				}
			// ... x(b) is a glue item and x(b-1) a box
			if (i > 0)
				if (lineObjects.get(i) instanceof KPGlue &&
						lineObjects.get(i-1) instanceof KPBox)
				{
					legalBreakpoints.add(i);
//					System.out.println("LO glue bp at " + i);
					legalBreakpointIndex ++;
//					System.out.println("with index: " + legalBreakpointIndex);
				}
		}
		
//		System.out.println("LO legal breakpoints found: " + legalBreakpoints.size());
	}
	
	/*
	public KPResult fitLinesLoosest(float width, float stretchTolerance, float shrinkTolerance, 
			// both values should be 1 by default
			int minLines, int maxLines, boolean group, Integer numLegalBreakpoints) 
	{
		// TODO: avoid repeating once found
		Vector<Integer> legalBreakpointIndices = findLegalBreakpointIndices(numLegalBreakpoints);
		
		for (Integer legalBreakpoint : legalBreakpointIndices)
		{
			
		}
		
		return null;
	}
	
	public KPResult fitLinesTightest(float width, float stretchTolerance, float shrinkTolerance, 
			// both values should be 1 by default
			int minLines, int maxLines, boolean group, Integer numLegalBreakpoints) 
	{
		// TODO: avoid repeating once found
		Vector<Integer> legalBreakpointIndices = findLegalBreakpointIndices(numLegalBreakpoints);
		
		return null;
	}
	*/
	
	/**
	 * 
	 * runs the K-P algorithm to find the optimal line breaks
	 * 
	 * @param width
	 * @param stretchTolerance - default 1 (in theory)
	 * @param shrinkTolerance - default 1 (in theory)
	 * @param minLines - no effect if -1
	 * @param maxLines - no effect if -1
	 * @return
	 */
	public List<KPResult> fitLinesFlex(float width, float stretchTolerance, float shrinkTolerance, 
													// both values should be 1 by default
			int minLines, int maxLines) 
	{
		List<KPResult> allResults = runKnuthPlass(width, stretchTolerance, shrinkTolerance,
				minLines, maxLines, true, -1, -1);
		
		return findBestGroupedResults(allResults);
	}
	
	/**
	 * 
	 * runs the K-P algorithm to find the optimal line breaks
	 * 
	 * @param width
	 * @param stretchTolerance - default 1 (in theory)
	 * @param shrinkTolerance - default 1 (in theory)
	 * @param minLines - no effect if -1
	 * @param maxLines - no effect if -1
	 * @return
	 */
	public KPResult fitLinesSingle(float width, float stretchTolerance, float shrinkTolerance, 
													// both values should be 1 by default
			int minLines, int maxLines, List<Integer> allLegalBreakpoints) 
	{
		List<KPResult> allResults = runKnuthPlass(width, stretchTolerance, shrinkTolerance,
				minLines, maxLines, false, -1, -1);
		
		return findBestResult(allResults);
	}
	
	public KPResult findLoosestFit(List<List<KPResult>> resultGroups)
	{
		// find group with the most lines
		int highestValue = Integer.MIN_VALUE;
		int highestIndex = -1;

		for (int i = 0; i < resultGroups.size(); i ++)
		{
			List<KPResult> group = resultGroups.get(i);
			KPResult firstResult = group.get(0); // take first as representative of all
			if (firstResult.getBreakpoints().size() > highestValue)
			{
				highestValue = firstResult.getBreakpoints().size();
				highestIndex = i;
			}
		}
		
		List<KPResult> chosenGroup = resultGroups.get(highestIndex);
		
		// now, for this chosen group, find the result with the earliest penultimate break
		int lowestValue = Integer.MAX_VALUE;
		int lowestIndex = -1;
		
		for (int i = 0; i < chosenGroup.size(); i ++)
		{
			KPResult result = chosenGroup.get(i);
			
			// if only one line, then only one possibility. Return this result.
			if (result.getBreakpoints().size() < 2)
				return result;
						
			int penultimateBreakpoint = result.getBreakpoints().get(
					result.getBreakpoints().size() - 2);
			if (penultimateBreakpoint < lowestValue)
			{
				lowestValue = penultimateBreakpoint;
				lowestIndex = i;
			}
		}
		
		return chosenGroup.get(lowestIndex);
	}
	
	public KPResult findTightestFit(List<List<KPResult>> resultGroups)
	{
		// find group with the fewest lines
		int lowestValue = Integer.MAX_VALUE;
		int lowestIndex = -1;
		
		for (int i = 0; i < resultGroups.size(); i ++)
		{
			List<KPResult> group = resultGroups.get(i);
			KPResult firstResult = group.get(0); // take first as representative of all
			if (firstResult.getBreakpoints().size() < lowestValue)
			{
				lowestValue = firstResult.getBreakpoints().size();
				lowestIndex = i;
			}
		}
		
		List<KPResult> chosenGroup = resultGroups.get(lowestIndex);
		
		// now, for this chosen group, find the result with the latest penultimate break
		int highestValue = Integer.MIN_VALUE;
		int highestIndex = -1;
		
		for (int i = 0; i < chosenGroup.size(); i ++)
		{
			KPResult result = chosenGroup.get(i);
			
			// if only one line, then only one possibility. Return this result.
			if (result.getBreakpoints().size() < 2)
				return result;
			
			int penultimateBreakpoint = result.getBreakpoints().get(
					result.getBreakpoints().size() - 2);
			if (penultimateBreakpoint > highestValue)
			{
				highestValue = penultimateBreakpoint;
				highestIndex = i;
			}
		}
		
		return chosenGroup.get(highestIndex);
	}
	
	// TODO: does it make sense to have minLines and maxLines for emergencyFit at all?
	
	// greedy algorithm
	public List<KPResult> emergencyFit(float width, 
			float stretchTolerance, float shrinkTolerance, // both values should be 1 by default
			int minLines, int maxLines, boolean flex, 
			int startBreakpointIndex, int endBreakpointIndex)//, boolean emergencyFit)
	{
		if (legalBreakpoints == null)
			generateLegalBreakpointIndices();
		
		// generate the sub-list
		List<Integer> subList = new ArrayList<Integer>();
		
		if (startBreakpointIndex < 0) startBreakpointIndex = Integer.MIN_VALUE;
		if (endBreakpointIndex < 0) endBreakpointIndex = Integer.MAX_VALUE;
		
		for (int i : legalBreakpoints)
			if (i >= startBreakpointIndex && i <= endBreakpointIndex)
				subList.add(i);
		
		return emergencyFit(width, stretchTolerance, shrinkTolerance,
				minLines, maxLines, flex, subList);
	}
	
	// greedy algorithm
	public List<KPResult> emergencyFit(float width, 
			float stretchTolerance, float shrinkTolerance, // both values should be 1 by default
			int minLines, int maxLines, boolean flex, 
			List<Integer> breakpoints) // both values should be -1 by default
	{
		// copy this list for processing, as we might need to remove some items
		List<Integer> breakpointsCopy = new ArrayList<Integer>();
		for (Integer bp : breakpoints)
			breakpointsCopy.add(bp);
		
//				System.out.println("td1 tempLegalBreakpointIndices.size: " + tempLegalBreakpointIndices.size());
		
		
		//List<FeasibleBreakpoint> activeBreakpoints = new ArrayList<FeasibleBreakpoint>();
		FeasibleBreakpoint fb; //TODO: rename to solution
		
		// start with the first breakpoint (beginning of paragraph) in active list
		FeasibleBreakpoint firstBreakpoint = 
				new FeasibleBreakpoint(lineObjects, breakpointsCopy.get(0));
		//activeBreakpoints.add(firstBreakpoint);
		fb = firstBreakpoint;
		
		boolean loop = true;
		while(loop)
		{
			// find acceptable breakpoints for next line
			List<FeasibleBreakpoint> acceptableBreakpoints = new ArrayList<FeasibleBreakpoint>();
			List<Float> adjRatios = new ArrayList<Float>();
			int lastAttemptedBreakpoint = -1;
			int prevAttemptedBreakpoint = -1;
			
			boolean goneTooFar = false;
			for (Integer legalBreakpoint : breakpointsCopy)
			{
				if (!goneTooFar && fb.index != legalBreakpoint) // breakpoints not equal
				{
					List<KPItem> newLineObjects = fb.getItemsTo(legalBreakpoint);
					System.out.println("newLineObjects: " + newLineObjects.size());
					
					// if not at last breakpoint, remove glue at end
					if (legalBreakpoint != breakpointsCopy.get(breakpointsCopy.size() - 1))
						removeGlueAtEnd(newLineObjects);
					
					// calculate necessary adjustment ratio
					float adjRatio = Float.NEGATIVE_INFINITY; // default = line too long
					try 
					{
						System.out.println("calling findAdjRatio with newLineObjects hc: " + newLineObjects.hashCode());
						adjRatio = findAdjustmentRatio(newLineObjects, width,
								addedStretchability, addedShrinkability);
					} 
					catch (KnuthPlassException e) 
					{
						//System.out.println("KPE thrown! fb: " + fb.index + " lb: " + legalBreakpoint + " width: " + width + " as " + addedStretchability + " as " + addedShrinkability);
						//ListUtils.printList(newLineObjects);
						// do nothing
					}
					
					if (adjRatio <= stretchTolerance && // generally 1
							adjRatio >= (0 - shrinkTolerance)) // generally -1
					{	
						// create new feasible breakpoint and append to list
						FeasibleBreakpoint newFB = new FeasibleBreakpoint(lineObjects, legalBreakpoint);
						for (Integer existingBreak : fb.prevBreaks)
							newFB.prevBreaks.add(existingBreak);
						newFB.prevBreaks.add(fb.index);
						acceptableBreakpoints.add(newFB);
						adjRatios.add(adjRatio);
					}
					
					// check if new line is too long -- in this case remove from active list
					// as no longer a candidate for future lines
					else if (adjRatio <= (0 - shrinkTolerance))
					{
						goneTooFar = true;
					}
					
					prevAttemptedBreakpoint = lastAttemptedBreakpoint;
					lastAttemptedBreakpoint = legalBreakpoint;
				}
			}
			
			// find the best acceptableBreakpoint
			if (acceptableBreakpoints.size() >= 1)
			{
				// break at the "best" breakpoint (greedily)
				
				float bestRatio = Float.MAX_VALUE;
				int bestIndex = -1;
				for (int i = 0; i < adjRatios.size(); i ++)
				{
					if (adjRatios.get(i) < bestRatio)
					{
						bestRatio = adjRatios.get(i);
						bestIndex = i;
					}
				}
				
				fb = acceptableBreakpoints.get(bestIndex);
			}
			else
			{
				// OLD
				
				// if current goneTooFar and 
					// if prev contains glue
						// take prev
					// else
						// also take prev (later space it out)
				// else (we must be at the end)
					// take current at any rate. Prob. can't be stretched (finishing glue?)
					// not something that should happen!
				
				
				int newBreakpoint;
				//if (goneTooFar && prevAttemptedBreakpoint > fb.index)
				if (prevAttemptedBreakpoint > fb.index)
				{
					newBreakpoint = prevAttemptedBreakpoint;
					
					// break and simply stretch any glue as far as necessary
					// TODO: if just one word, stretch it out
				}
				else
				{
					// no progress is being made since last breakpoint!
					// avoid endless loop
					newBreakpoint = lastAttemptedBreakpoint;
					
					// TODO: later try breaking at illegal position
					// and if that fails, break at character within a word
					// and if this fails, run character into margin
				}
				
				// create new feasible breakpoint and append to list
				FeasibleBreakpoint newFB = new FeasibleBreakpoint(lineObjects, newBreakpoint);
				for (Integer existingBreak : fb.prevBreaks)
					newFB.prevBreaks.add(existingBreak);
				newFB.prevBreaks.add(fb.index);
				
				fb = newFB;
				
				// TODO: finishing glue missing? (should not happen) -> return KPE
			}

			// break out of loop if reached the end
			if (fb.index == breakpointsCopy.get(breakpointsCopy.size() - 1))
			{
				loop = false;
			}
		}
		
		// is only one result here
		List<KPResult> validResults = new ArrayList<KPResult>();

		FeasibleBreakpoint bp = fb;
		{
			// this section same as in runKnuthPlass TODO: refactor
			
			List<List<KPItem>> fittedLines = bp.getFittedLines(false);
	//			System.out.println("bp: " + bp + " fittedLines: " + fittedLines.size());
			
			if (minLines >= 0 && fittedLines.size() < minLines ||
					maxLines >= 0 && fittedLines.size() > maxLines)
			{
				// don't add result at all -- does not fit the
				// criteria and is therefore invalid
			}
			else
			{
				// TODO: this array is now redundant, but its removal needs to be tested
				double demerits = calculateDemerits(fittedLines, width, minLines, maxLines);
				// following method also sets calcAdjRatios
				List<Double> lineDemerits = calculateDemeritsList(fittedLines, width);
				
				List<Integer> chosenBreakpoints = new ArrayList<Integer>();
				chosenBreakpoints.addAll(bp.prevBreaks);
				chosenBreakpoints.add(bp.index);
				// remove the first breakpoint, always -1 -- this is equivalent to the last
				// when dealing with more than one block
				chosenBreakpoints.remove(0);
				
				KPResult kpr = new KPResult(fittedLines, demerits, lineDemerits, 
						calcAdjRatios, chosenBreakpoints);
				validResults.add(kpr);
			}
		}
		
		return validResults;
	}
	
	
	/**
	 * runs Knuth-Plass with a sub-list
	 */
	
	public List<KPResult> runKnuthPlass(float width, 
			float stretchTolerance, float shrinkTolerance, // both values should be 1 by default
			int minLines, int maxLines, boolean flex, 
			int startBreakpointIndex, int endBreakpointIndex)//, boolean emergencyFit)
	{
//		System.out.println("in runKnuthPlass with start: " + startBreakpointIndex + 
//				" end: " + endBreakpointIndex);
		
		// generate list of legal breakpoints on first run (unless called separately)
		if (legalBreakpoints == null)
			generateLegalBreakpointIndices();
		
		// generate the sub-list
		List<Integer> subList = new ArrayList<Integer>();
		/*
		if (startBreakpointIndex < 0) startBreakpointIndex = 0;
		if (endBreakpointIndex < 0) endBreakpointIndex = legalBreakpoints.size() - 1;
		
		for (int i = startBreakpointIndex; i <= endBreakpointIndex; i ++)
			subList.add(legalBreakpoints.get(i));
		*/
		
		if (startBreakpointIndex < 0) startBreakpointIndex = Integer.MIN_VALUE;
		if (endBreakpointIndex < 0) endBreakpointIndex = Integer.MAX_VALUE;
		
		for (int i : legalBreakpoints)
			if (i >= startBreakpointIndex && i <= endBreakpointIndex)
				subList.add(i);
		
		// add the appropriate finishing penalty (code -10000)
		// (without this the result will be missing the final line!)
//		if (endBreakpointIndex < legalBreakpoints.size() - 1)
//			subList.add(-10000);
		
		/*
		System.out.print("subList: ");
		for (Integer i : subList)
			System.out.print(i + "  ");
		System.out.println();
		*/
		
		// no finishing glue should be added, as last line of paragraph
		// should take up the full width
		
		// run K-P for this sub-list
//		if (!emergencyFit)
			return runKnuthPlass(width, stretchTolerance, shrinkTolerance,
					minLines, maxLines, flex, subList);
//		else
//			return emergencyFit(width, stretchTolerance, shrinkTolerance,
//					minLines, maxLines, flex, subList);
		
		// no need to perform any adjustment for offsetting indices?
		
		// CHECK
		// breaks at end ....
		// no break directly before finishing glue possible
		// last possible early break -- word (or hyphen point, etc.) before
		// finishing glue
		
		// CHECK
		// startBreakpointIndex = 0; endBreakpointIndex = bp.size - 1
		// should encompass all breakpoints from -1 to finishing penalty
		// no extra penalty should be added in this case
		// (or this method should simply not be called?)
		//
		// startBreakpointIndex = 0; endBreakpointIndex = bp.size - 2
		// encompasses all breakpoints from -1 to penultimate break
		// (last break before finishing glue)
		// in this case, finishing penalty should be added
		//
		// no duplicate results with incremental indices
	}
	
	/*
	// greedy algorithm
	public List<KPResult> emergencyFit(float width, 
			float stretchTolerance, float shrinkTolerance, // both values should be 1 by default
			int minLines, int maxLines, boolean flex, 
			List<Integer> breakpoints) // both values should be -1 by default
	{
		// copy this list for processing, as we might need to remove some items
		List<Integer> breakpointsCopy = new ArrayList<Integer>();
		for (Integer bp : breakpoints)
			breakpointsCopy.add(bp);
		
		//List<FeasibleBreakpoint> resultingBreakpoints = new ArrayList<FeasibleBreakpoint>();
		
		List<FeasibleBreakpoint> activeBreakpoints = new ArrayList<FeasibleBreakpoint>();
		// start with the first breakpoint (beginning of paragraph) in active list
		FeasibleBreakpoint firstBreakpoint = 
				new FeasibleBreakpoint(lineObjects, breakpointsCopy.get(0));
		activeBreakpoints.add(firstBreakpoint);
		
		for (Integer legalBreakpoint : breakpointsCopy)
		{
			// measure distance resultingBreakpoints -> bp
			
			List<KPItem> newLineObjects = fb.getItemsTo(legalBreakpoint);
		}
		
		return null;
	}
	*/
	
	/**
	 * 
	 * runs Knuth-Plass
	 * 
	 * @param width
	 * @param stretchTolerance
	 * @param shrinkTolerance
	 * @param minLines
	 * @param maxLines
	 * @param flex
	 * @param startBreakpointIndex -- default -1; otherwise value of start index of index
	 * @param endBreakpointIndex -- default -1; otherwise value of end index of index
	 * @param allLegalBreakpoints
	 * @return
	 */
	public List<KPResult> runKnuthPlass(float width, 
			float stretchTolerance, float shrinkTolerance, // both values should be 1 by default
			int minLines, int maxLines, boolean flex, 
			List<Integer> breakpoints) // both values should be -1 by default
	{
		// copy this list for processing, as we might need to remove some items
		List<Integer> breakpointsCopy = new ArrayList<Integer>();
		for (Integer bp : breakpoints)
			breakpointsCopy.add(bp);
		
//		System.out.println("td1 tempLegalBreakpointIndices.size: " + tempLegalBreakpointIndices.size());
		
		List<FeasibleBreakpoint> activeBreakpoints = new ArrayList<FeasibleBreakpoint>();
		// start with the first breakpoint (beginning of paragraph) in active list
		FeasibleBreakpoint firstBreakpoint = 
				new FeasibleBreakpoint(lineObjects, breakpointsCopy.get(0));
		activeBreakpoints.add(firstBreakpoint);
		
		for (Integer legalBreakpoint : breakpointsCopy)
		{
//			System.out.println("***** loop with " + legalBreakpoint);
			
			// check to see if there is any feasible breakpoint A
			// so that the line from A to the current legalBreakpoint
			// has an acceptable adjustment ratio
			List<FeasibleBreakpoint> acceptableBreakpoints = new ArrayList<FeasibleBreakpoint>();
			List<FeasibleBreakpoint> breakpointsNoLongerActive = new ArrayList<FeasibleBreakpoint>();
			for (FeasibleBreakpoint fb : activeBreakpoints)
			{
//				System.out.println("examining fb: " + fb + " lb: " + legalBreakpoint);
				if (fb.index != legalBreakpoint) // breakpoints not equal!
				{
					/*
					System.out.println();
					System.out.println("*fb: " + fb);
					System.out.println("**one with fb.index: " + fb.index);
					*/
					
					List<KPItem> newLineObjects = fb.getItemsTo(legalBreakpoint);
//					System.out.println("newLineObjects: " + newLineObjects.size());
					
					// if not at last breakpoint, remove glue at end
					if (legalBreakpoint != breakpointsCopy.get(breakpointsCopy.size() - 1))
						removeGlueAtEnd(newLineObjects);
					
					// calculate necessary adjustment ratio
					float adjRatio = Float.NEGATIVE_INFINITY; // default = line too long
					try 
					{
//						System.out.println("calling findAdjRatio with newLineObjects hc: " + newLineObjects.hashCode());
						adjRatio = findAdjustmentRatio(newLineObjects, width,
								addedStretchability, addedShrinkability);
					} 
					catch (KnuthPlassException e) 
					{
						//System.out.println("KPE thrown! fb: " + fb.index + " lb: " + legalBreakpoint + " width: " + width + " as " + addedStretchability + " as " + addedShrinkability);
						//ListUtils.printList(newLineObjects);
						// do nothing
					}
							
					/*
					System.out.println("adjRatio: " + adjRatio);
					System.out.println("width: " + width);
					System.out.println("newLineObjects: " + newLineObjects.size());
					*/
					
					// 
					if (adjRatio <= stretchTolerance && // generally 1
							adjRatio >= (0 - shrinkTolerance)) // generally -1
					{
//						System.out.println("two with ar: " + adjRatio);
						
						// create new feasible breakpoint and append to list
						FeasibleBreakpoint newFB = new FeasibleBreakpoint(lineObjects, legalBreakpoint);
						for (Integer existingBreak : fb.prevBreaks)
							newFB.prevBreaks.add(existingBreak);
						newFB.prevBreaks.add(fb.index);
						acceptableBreakpoints.add(newFB);
					}
					
					// check if new line is too long -- in this case remove from active list
					// as no longer a candidate for future lines
					else if (adjRatio <= (0 - shrinkTolerance))
					{
//						System.out.println("three with st " + shrinkTolerance);
						breakpointsNoLongerActive.add(fb);
					}
					else
					{
//						System.out.println("four");
						// new line is too short -- do nothing and keep looking further
						
						// TEST
						
						/*
						// create new feasible breakpoint and append to list
						FeasibleBreakpoint newFB = new FeasibleBreakpoint(lineObjects, legalBreakpoint);
						for (Integer existingBreak : fb.prevBreaks)
							newFB.prevBreaks.add(existingBreak);
						newFB.prevBreaks.add(fb.index);
						acceptableBreakpoints.add(newFB);
						*/
					}
				}
			}
			
//			System.out.println("five");
			
			// all acceptableBreakpoints point to the same final breakpoint, 
			// but have different intermediate breakpoints
			// find the most optimal of these and add to the list
			
			/*
			System.out.print("td1s found new acceptable breakpoints: ");
			for (FeasibleBreakpoint fb : acceptableBreakpoints)
				System.out.print(fb + " ");
			System.out.println();
		    */
			
			if (acceptableBreakpoints.size() > 1)
			{
				if (!flex)
				{
					FeasibleBreakpoint optimalBreakpoint =
							findOptimalBreakpoint(acceptableBreakpoints, width, minLines, maxLines);
					activeBreakpoints.add(optimalBreakpoint);
				}
				else
				{
					List<FeasibleBreakpoint> optimalBreakpoints =
							findOptimalGroupedBreakpoints(acceptableBreakpoints, width, minLines, maxLines);
					activeBreakpoints.addAll(optimalBreakpoints);
				}
				
//				System.out.println("added optimalBreakpoint 1: " + optimalBreakpoint);
			}
			else if (acceptableBreakpoints.size() == 1)
			{
				activeBreakpoints.add(acceptableBreakpoints.get(0));
//				System.out.println("added optimalBreakpoint 2: " + acceptableBreakpoints.get(0));
			}
			
			activeBreakpoints.removeAll(breakpointsNoLongerActive);
			
			/*
			System.out.print("removing breakpoints no longer active: ");
			for (FeasibleBreakpoint fb : breakpointsNoLongerActive)
				System.out.print(fb + " ");
			System.out.println();
			
			System.out.print("current active: ");
			for (FeasibleBreakpoint fb : activeBreakpoints)
				System.out.print(fb + " ");
			System.out.println();
			*/
			
			/*
			int index = 0;
			for (FeasibleBreakpoint fb : activeBreakpoints)
			{
				System.out.println("***fb: " + fb);
				List<List<KPItem>> result = fb.getFittedLines(false);
				double demerits = calculateDemerits(result, width, minLines, maxLines);
				
				System.out.println("-->result " + index + " with demerits " + demerits);
				printResult(result);
				
				index ++;
			}
			*/
			
		}
		
//		System.out.println("spp active results before remove: " + activeBreakpoints.size());
//		System.out.println("removing unequal to: " + breakpoints.get(breakpoints.size() - 1));
		
		//ListUtils.printList(activeBreakpoints);
		
		// remove all breakpoints in the active list that do not end at the final
		// breakpoint (these can cause duplicate results later)
		List<FeasibleBreakpoint> breakpointsToRemove = new ArrayList<FeasibleBreakpoint>();
		for (FeasibleBreakpoint fb : activeBreakpoints)
			if (fb.index != breakpoints.get(breakpoints.size() - 1))
				breakpointsToRemove.add(fb);
		activeBreakpoints.removeAll(breakpointsToRemove);
		
		// the breakpoints remaining in the active list reflect all valid results
		
//		System.out.println("spp active results after remove: " + activeBreakpoints.size());
		
//		ListUtils.printList(activeBreakpoints);
		
		List<KPResult> validResults = new ArrayList<KPResult>();
		for (FeasibleBreakpoint bp : activeBreakpoints)
		{
			List<List<KPItem>> fittedLines = bp.getFittedLines(false);
//			System.out.println("bp: " + bp + " fittedLines: " + fittedLines.size());
			
			if (minLines >= 0 && fittedLines.size() < minLines ||
					maxLines >= 0 && fittedLines.size() > maxLines)
			{
				// don't add result at all -- does not fit the
				// criteria and is therefore invalid
			}
			else
			{
				// TODO: this array is now redundant, but its removal needs to be tested
				double demerits = calculateDemerits(fittedLines, width, minLines, maxLines);
				// following method also sets calcAdjRatios
				List<Double> lineDemerits = calculateDemeritsList(fittedLines, width);
				
				List<Integer> chosenBreakpoints = new ArrayList<Integer>();
				chosenBreakpoints.addAll(bp.prevBreaks);
				chosenBreakpoints.add(bp.index);
				// remove the first breakpoint, always -1 -- this is equivalent to the last
				// when dealing with more than one block
				chosenBreakpoints.remove(0);
				
				KPResult kpr = new KPResult(fittedLines, demerits, lineDemerits, 
						calcAdjRatios, chosenBreakpoints);
				validResults.add(kpr);
			}
		}
		
//		System.out.println("found validResults: " + validResults.size());
		
		return validResults;
		
		// find the demerits of each and return the best
		
//		System.out.println("//// active breakpoints: " + activeBreakpoints.size());
		
		/*
		if (!flex)
		{
			// return a list with just the one item (non-flexible layout)
			
			List<KPResult> retVal = new ArrayList<KPResult>();
			retVal.add(findBestResult(activeBreakpoints, width, minLines, maxLines));
			return retVal;
		}
		else
		{
			return(findBestGroupedResults(activeBreakpoints, width, minLines, maxLines));
		}
		*/
	}
	

	/**
	 * 
	 * groups the breakpoints according to the number of intermediate breakpoints (lines)
	 * used for flexible layouting
	 * 
	 * @param breakpoints - all pointing to the same breakpoint but with different intermediate breaks
	 * @return
	 */
	public List<List<KPResult>> groupResults(List<KPResult> results)
	{
		List<List<KPResult>> retVal = new ArrayList<List<KPResult>>();
		List<Integer> groupSizes = new ArrayList<Integer>();
		
		for (KPResult result : results)
		{
			// check if group already exists
			int groupIndex = -1;
			for (int i = 0; i < groupSizes.size(); i ++)
			{
				if (result.getBreakpoints().size() == groupSizes.get(i))
				{
					groupIndex = i;
					i = groupSizes.size();
				}
			}
			
			if (groupIndex >= 0) // group already exists
			{
				List<KPResult> thisGroup = retVal.get(groupIndex);
				thisGroup.add(result);
			}
			else // group not found
			{
				// create new group
				List<KPResult> newGroup = new ArrayList<KPResult>();
				newGroup.add(result);
				retVal.add(newGroup);
				groupSizes.add(new Integer(result.getBreakpoints().size()));
			}
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * deprecated!
	 * TODO: Deleteme!
	 * 
	 * groups the breakpoints according to the number of intermediate breakpoints (lines)
	 * used for flexible layouting
	 * 
	 * @param breakpoints - all pointing to the same breakpoint but with different intermediate breaks
	 * @return
	 */
	protected List<List<FeasibleBreakpoint>> groupBreakpoints(List<FeasibleBreakpoint> breakpoints)
	{
		List<List<FeasibleBreakpoint>> breakpointGroups = new ArrayList<List<FeasibleBreakpoint>>();
		List<Integer> groupSizes = new ArrayList<Integer>();
		
		for (FeasibleBreakpoint bp : breakpoints)
		{
			// check if group already exists
			int groupIndex = -1;
			for (int i = 0; i < groupSizes.size(); i ++)
			{
				if (bp.prevBreaks.size() == groupSizes.get(i))
				{
					groupIndex = i;
					i = groupSizes.size();
				}
			}
			
			if (groupIndex >= 0) // group already exists
			{
				List<FeasibleBreakpoint> thisGroup = breakpointGroups.get(groupIndex);
				thisGroup.add(bp);
			}
			else // group not found
			{
				// create new group
				List<FeasibleBreakpoint> newGroup = new ArrayList<FeasibleBreakpoint>();
				newGroup.add(bp);
				breakpointGroups.add(newGroup);
				groupSizes.add(new Integer(bp.prevBreaks.size()));
				
//				System.out.println("created new group with " + bp.prevBreaks.size() + " breakpoints");
			}
		}
		
		return breakpointGroups;
	}

	/**
	 * returns null if passed empty string
	 * 
	 * @param results
	 * @return
	 */
	public KPResult findBestResult(List<KPResult> results)
	{
		// find index position with lowest demerits
		double lowestValue = Double.POSITIVE_INFINITY;
		int lowestIndex = -1;
		
//		for (int i = 0; i < demerits.length; i ++)
//		not all of demerits might have been used up
		for (int i = 0; i < results.size(); i ++)
		{
			if (results.get(i).getDemerits() <= lowestValue)
			{
				lowestValue = results.get(i).getDemerits();
				lowestIndex = i;
			}
		}
		
		if (lowestIndex < 0)
			return null;
		else return results.get(lowestIndex);
	}
	
	protected List<KPResult> findBestGroupedResults(List<KPResult> results)
	{
		List<List<KPResult>> resultGroups =
				groupResults(results);
		
		List<KPResult> retVal = new ArrayList<KPResult>();
		
		for (List<KPResult> group : resultGroups)
			retVal.add(findBestResult(group));
		
		return retVal;
	}
	
	/**
	 * 
	 * deprecated!
	 * TODO: deleteme!
	 * 
	 * @param breakpoints
	 * @param width
	 * @param minLines
	 * @param maxLines
	 * @return
	 */
	/*
	protected KPResult findBestResult
			(List<FeasibleBreakpoint> breakpoints, float width, 
//			FeasibleBreakpoint optimalBreakpointToSet, int minLines, int maxLines)
//			optimalBreakpoint is only relevant for the findOptimalBreakpoint method
			int minLines, int maxLines)
	{
//		System.out.println("--- start findBestResult ---");
		List<KPResult> validResults = new ArrayList<KPResult>();
		double[] demerits = new double[breakpoints.size()];
		
//		System.out.println("active breakpoints remaining: " + breakpoints.size());
		
		int index = 0;
		for (FeasibleBreakpoint bp : breakpoints)
		{
//			System.out.println("**bp: " + bp);
			List<List<KPItem>> fittedLines = bp.getFittedLines(true);
			
			if (minLines >= 0 && fittedLines.size() < minLines ||
					maxLines >= 0 && fittedLines.size() > maxLines)
			{
				// don't add result at all -- does not fit the
				// criteria and is therefore invalid
			}
			else
			{
				// TODO: this array is now redundant, but its removal needs to be tested
				demerits[index] = calculateDemerits(fittedLines, width, minLines, maxLines);
			
				List<Integer> chosenBreakpoints = new ArrayList<Integer>();
				chosenBreakpoints.addAll(bp.prevBreaks);
				chosenBreakpoints.add(bp.index);
				
				KPResult kpr = new KPResult(fittedLines, demerits[index], chosenBreakpoints);
				validResults.add(kpr);
					
				index ++;
	//			System.out.println("---");
			}
		}
		
		// find index position with lowest demerits
		double lowestValue = Double.POSITIVE_INFINITY;
		int lowestIndex = -1;
		
//		for (int i = 0; i < demerits.length; i ++)
//		not all of demerits might have been used up
		for (int i = 0; i < index; i ++)
		{
			if (demerits[i] <= lowestValue)
			{
				lowestValue = demerits[i];
				lowestIndex = i;
			}
		}
		
//		System.out.println("--- end findBestResult with lowestIndex: " + lowestIndex + " ---" + breakpoints.get(lowestIndex));
		return validResults.get(lowestIndex);
	}
	*/
	/**
	 * 
	 * deprecated!
	 * TODO: deleteme!
	 * 
	 * returns a list of optimal results for each size (number of lines)
	 * 
	 * @param breakpoints
	 * @param width
	 * @param minLines
	 * @param maxLines
	 * @return
	 */
	/*
	protected List<KPResult> findBestGroupedResults
			(List<FeasibleBreakpoint> breakpoints, float width, 
//			FeasibleBreakpoint optimalBreakpointToSet, int minLines, int maxLines)
//			optimalBreakpoint is only relevant for the findOptimalBreakpoint method
			int minLines, int maxLines)
	{
		List<List<FeasibleBreakpoint>> breakpointGroups =
				groupBreakpoints(breakpoints);
		
		List<KPResult> retVal = new ArrayList<KPResult>();
		
		for (List<FeasibleBreakpoint> group : breakpointGroups)
			retVal.add(findBestResult(group, width, minLines, maxLines));
		return retVal;
	}
	*/

	// identical to above method
	// except returns FeasibleBreakpoint as output
	// and does not continue to end of paragraph (ends at breakpoint index)
	
	/**
	 * 
	 * from a set of breakpoint objects pointing to the same breakpoint (but with
	 * different intermediate breaks), finds the best one
	 * 
	 * @param breakpoints
	 * @param width
	 * @param minLines
	 * @param maxLines
	 * @return
	 */
	protected FeasibleBreakpoint findOptimalBreakpoint
			(List<FeasibleBreakpoint> breakpoints, float width, int minLines, int maxLines)
	{
//		System.out.println("--- start findOptimalBreakpoint ---");
//		List<List<List<KPItem>>> validResults = new ArrayList<List<List<KPItem>>>();
		double[] demerits = new double[breakpoints.size()];
		
//		System.out.println("active breakpoints remaining: " + breakpoints.size());
		
		int index = 0;
		for (FeasibleBreakpoint bp : breakpoints)
		{
//			System.out.println("examining breakpoint: " + bp);
			List<List<KPItem>> result = bp.getFittedLines(false);
//			validResults.add(result); // only used by findBestResult
			demerits[index] = calculateDemerits(result, width, minLines, maxLines);
//			System.out.println("  demerits [" + index + "] = " + demerits[index]);
			index ++;
		}
		
		// find index position with lowest demerits
		double lowestValue = Double.POSITIVE_INFINITY;
		int lowestIndex = -1;
		
		for (int i = 0; i < demerits.length; i ++)
		{
			// <= instead of <; this way, will not crash,
			// even if all values are e.g. infinity
			if (demerits[i] <= lowestValue)
			{
				lowestValue = demerits[i];
				lowestIndex = i;
			}
		}
		
//		System.out.println("--- end findOptimalBreakpoint with lowestIndex: " + lowestIndex + " ---");
		return breakpoints.get(lowestIndex);
	}

	// identical to above method
	// except returns FeasibleBreakpoint as output
	// and does not continue to end of paragraph (ends at breakpoint index)
	
	/**
	 * 
	 * from a set of breakpoint objects pointing to the same breakpoint (but with
	 * different intermediate breaks), groups them according to how many intermediate
	 * breaks (lines) and finds the best one for each group (using findOptimalBreakpoint)
	 * 
	 * @param breakpoints
	 * @param width
	 * @param minLines
	 * @param maxLines
	 * @return
	 */
	protected List<FeasibleBreakpoint> findOptimalGroupedBreakpoints
			(List<FeasibleBreakpoint> breakpoints, float width, int minLines, int maxLines)
	{
		// group the breakpoints according to how many intermediate breakpoints (i.e. lines)
		List<List<FeasibleBreakpoint>> breakpointGroups =
				groupBreakpoints(breakpoints);

		// for each group, run findOptimalBreakpoint
		List<FeasibleBreakpoint> retVal = new ArrayList<FeasibleBreakpoint>();
		for (List<FeasibleBreakpoint> group : breakpointGroups)
			retVal.add(findOptimalBreakpoint(group, width, minLines, maxLines));
		
		return retVal;
	}

	protected static List<KPBox> getBoxItems(List<KPItem> objectList)
	{
		List<KPBox> retVal = new ArrayList<KPBox>();
		for (KPItem item : objectList)
			if (item instanceof KPBox)
				retVal.add((KPBox) item);
		
		return retVal;
	}
	
	protected static List<KPGlue> getGlueItems(List<KPItem> objectList)
	{
		List<KPGlue> retVal = new ArrayList<KPGlue>();
		for (KPItem item : objectList)
			if (item instanceof KPGlue)
				retVal.add((KPGlue) item);
		
		return retVal;
	}
	
	public List<Integer> getLegalBreakpoints() {
		return legalBreakpoints;
	}

	public void setLegalBreakpoints(List<Integer> legalBreakpoints) {
		this.legalBreakpoints = legalBreakpoints;
	}

	// TODO: add adjustment ratio as parameter
	protected static float getTotalWidth(List<? extends KPItem> objectList)
	{
		float retVal = 0;
		int index = -1;
		for (KPItem item : objectList) 
		{
			index ++;
			if (item instanceof KPBox)
				retVal += ((KPBox)item).getAmount();
			else if (item instanceof KPGlue)
				retVal += ((KPGlue)item).getAmount(); // TODO: AR
			else if (item instanceof KPPenalty)
				if (index == objectList.size() - 1) {
					
					// TODO: ?do penalties remain in the line to simplify calculation of demerits?

					PAWord additionalBox = ((PAWord)((KPPenalty)item).getAdditionalBox());
					if (additionalBox != null)
						retVal += additionalBox.getWidth();
					
				}
		}
		return retVal;
	}
	
	protected static float getTotalStretchability(List<? extends KPItem> objectList)
	{
		float retVal = 0;
		for (KPItem item : objectList)
			retVal +=((KPGlue)item).stretchability;
		return retVal;
	}
	
	protected static void removeGlueAtEnd(List<KPItem> items)
	{
		// first item should be either a box or a penalty of value -inf (<= -1000)
		List<KPItem> itemsToRemove = new ArrayList<KPItem>();
		for (KPItem item : items)
		{
			// build up list of consecutive glues (unbroken by box)
			// and clear list if a box is encountered
			if (item instanceof KPGlue)
				itemsToRemove.add(item);
			else if (item instanceof KPBox)
				itemsToRemove = new ArrayList<KPItem>();
		}
		
		items.removeAll(itemsToRemove);
	}
	
	// linewidth = glue1.width + glue1.stretchability*AR + glue2.width + glue2.stretchability*AR + ...
	// find extra space
	// split up against the stretchabilities (proportioned) (NOT proportional to glue width, just the stretchability)
	
	public static float findAdjustmentRatio(List<KPItem> items, float targetWidth, 
			float addedStretchability, float addedShrinkability) 
			throws KnuthPlassException
	{
//		System.out.println("in findAdjustmentRatio with items: " + items.hashCode());
		/*
		System.out.print("->line: ");
		for (KPItem kpi : items)
			if (kpi instanceof PAWord)
				System.out.print(((PAWord)kpi).getText() + " ");
		System.out.println();
		*/
		
		// moved to calling method -- not to be done for last line of paragraph!
		//removeGlueAtEnd(items);
		
		float totalContentWidth = getTotalWidth(items);
		List<KPBox> boxes = getBoxItems(items);
		float totalBoxWidth = getTotalWidth(boxes);
		
//		System.out.println("targetWidth: " + targetWidth);
//		System.out.println("totalBoxWidth: " + totalBoxWidth);
		
		if (totalBoxWidth > targetWidth)
		{
			/*
			System.out.println("items: " + items.size());
			System.out.print("->line: ");
			for (KPItem kpi : items)
				if (kpi instanceof PAWord)
					System.out.print("[" + ((PAWord)kpi).getText() + "] ");
			System.out.println();
			System.out.println("total box width exceeds target width");
			System.out.println("targetWidth: " + targetWidth);
			System.out.println("totalBoxWidth: " + totalBoxWidth);
			*/
			throw new KnuthPlassException("total box width exceeds target width");
		}
		
		float glueExpansion = targetWidth - totalContentWidth;
		List<KPGlue> glues = getGlueItems(items);
		
		/*
		System.out.println("boxes: " + boxes.size() + " glues: " + glues.size() + 
				" totalContentWidth: " + totalContentWidth + 
				" totalBoxWidth: " + totalBoxWidth + " glueExpansion: " + glueExpansion);
		*/
		
		if (glueExpansion >= 0)
		{
			float totalStretchability = addedStretchability;
			for (KPGlue glue: glues)
				totalStretchability += glue.stretchability;
			
//			System.out.println("total stretchability: " + totalStretchability);
			
			return glueExpansion/totalStretchability;
		}
		else
		{
			float totalShrinkability = addedShrinkability;
			for (KPGlue glue: glues)
				totalShrinkability += glue.shrinkability;
			
//			System.out.println("total shrinkability: " + totalShrinkability);
			
			return glueExpansion/totalShrinkability;
		}
	}
	
	public double calculateDemeritsLine(List<KPItem> thisLine, float width)
	{
		double retVal = 0;
		try
		{
//			index += 1;
			/*
			System.out.print("i" + index + " ");
			
			for (KPItem kpi : thisLine)
				System.out.println(kpi);
			System.out.println("---");
			*/
			
			double adjRatio = findAdjustmentRatio(thisLine, width, 
					addedStretchability, addedShrinkability);
			// for reference by other methods
			calcAdjRatio = adjRatio;
			
			// throws KPE if content is wider than width
//			System.out.println("adjRatio: " + adjRatio);
			
			double badness = Double.MAX_VALUE;
			if (adjRatio >= -1.0f)		
				badness = 100 * (Math.pow(Math.abs(adjRatio), 3.0));
			
			double penalty = 0; 
			
			// set penalty if this line ends in a penalty item K-P p.1127 (p9 PDF)
			if (thisLine.get(thisLine.size() - 1) instanceof KPPenalty)
				penalty = thisLine.get(thisLine.size() - 1).getAmount();
			
			double consecutivePenalty = 0; // TODO consecutive penalty
			
			// TODO: consecutivePenalty set (usually at 3000) where two consecutive
			// lines are hyphenated (flagged penalty) -- see p. 1128 (p10 PDF) of paper
			
			double demerits;
			if (penalty >= 0)
			{
//				System.out.print("d1 " + " adjRatio: " + adjRatio + " badness: " + badness + " penalty: " + penalty + " ");
				demerits = Math.pow((1 + badness + penalty), 2) + consecutivePenalty;
			}
			else if (penalty > -1000) // and <= 0
			{
//				System.out.print("d2 ");
				demerits = Math.pow((1 + badness), 2) - Math.pow(penalty, 2) + consecutivePenalty;
			}
			else // penalty < -1000 (-infinity) 
			{
				// YES, this line is necessary! We do not need to consider the
				// penalty here, as such breaks are forced (note: discontinuous function!)
				
				// NOTE: in this case, page 1122 (p4 PDF) of Knuth-Plass states
				// that any penalty >= +/- 1000 is treated as +/- infinity
				
//				System.out.print("d3 ");
				demerits = Math.pow((1 + badness), 2) + consecutivePenalty;
			}
			
//			System.out.println(demerits);
			retVal += demerits;
		}
		catch(KnuthPlassException kpe)
		{
//			kpe.printStackTrace();
//			return 10000; // +ve infinity
			return Double.POSITIVE_INFINITY;
		}
		
		return retVal;
	}
	
	protected double calcAdjRatio;
	protected List<Double> calcAdjRatios;
	
	public double calculateDemerits(List<List<KPItem>> lines, float width, int minLines, int maxLines)
	{
		// don't check minLines here as this method is used to evaluate part results
		if (maxLines >= 0 && lines.size() > maxLines)
			return Double.POSITIVE_INFINITY;
		
		double retVal = 0; // use double for internal computation

//		int index = -1;
		
		for (List<KPItem> thisLine : lines)
		{
			retVal += calculateDemeritsLine(thisLine, width);
		}
		
//		System.out.println("overall demerits: " + retVal);
//		System.out.println();
		
		return (double) retVal;
	}
	
	public List<Double> calculateDemeritsList(List<List<KPItem>> lines, float width)
	{
		List<Double> retVal = new ArrayList<Double>();
		calcAdjRatios = new ArrayList<Double>();
		
		for (List<KPItem> thisLine : lines)
		{
			// following method also sets calcAdjRatio
			retVal.add(calculateDemeritsLine(thisLine, width));
			calcAdjRatios.add(calcAdjRatio);
		}
		return retVal;
	}
	
	public void printResult(List<List<KPItem>> result)
	{
		int index = -1;
		for (List<KPItem> thisLine : result)
		{
			index += 1;
			System.out.print("i" + index + " ");
			for (KPItem item : thisLine)
			{
				if (item instanceof KPBox)
				{
					System.out.print("X");
				}
				else if (item instanceof KPGlue)
				{
					System.out.print(" ");
				}
				else if (item instanceof KPPenalty)
				{
					if (((KPPenalty)item).isFlag())
						System.out.print("-");
				}
			}
			System.out.println();
		}
	}
}	

// FeasibleBreakpoint = part solution to the problem
class FeasibleBreakpoint
{
	// indexing: refers to the index of the last item in the line
	// i.e. next line starts at breakpoint + 1
	int index;
	List<Integer> prevBreaks = new ArrayList<Integer>();
	List<KPItem> lineObjects = new ArrayList<KPItem>(); // pointer to object list in calling method
	
	// initializes part-solution at first breakpoint index
	public FeasibleBreakpoint(List<KPItem> lineObjects, int index)
	{
		this.lineObjects = lineObjects;
		this.index = index;
	}
	
	public static void removeItemsAtBeginningAndEnd(List<KPItem> line, boolean removeGlueAtEnd)
	{
		// now remove glue and penalty items from the start of each line
		// according to K-P p. 1125 (PDF p. 7)
		
		// and remove penalty items in the middle of each line
		// i.e. penalty at the _end_ of a line means that the break occurred
		// at the penalty (as legal break only at penalty or glue following box)
		
		// first item should be either a box or a penalty of value -inf (<= -1000)
		List<KPItem> itemsToRemove = new ArrayList<KPItem>();
		boolean startItemFound = false;
		List<KPItem> glueAtEnd = new ArrayList<KPItem>();
		for (KPItem item : line)
		{
			if (!startItemFound)
			{
				if (item instanceof KPBox ||
						item instanceof KPPenalty && 
						((KPPenalty)item).amount <= -1000)
				{
					startItemFound = true;
				}
				else
				{
					// glue or penalty > -1000
					itemsToRemove.add(item);
				}
			}
			else
			{
				// remove "unconsumed" penalties
				if (item instanceof KPPenalty &&
						item != line.get(line.size() - 1))
					itemsToRemove.add(item);
				
				// build up list of consecutive glues (unbroken by box)
				// and clear list if a box is encountered
				if (item instanceof KPGlue)
					glueAtEnd.add(item);
				else if (item instanceof KPBox)
					glueAtEnd = new ArrayList<KPItem>();
			}
		}
		line.removeAll(itemsToRemove);
		
		// glue left at end makes a mess of the justification
		// and should therefore be removed AFTER obtaining final lines (in layout method), not here
		// it should not be removed in intermediate calculations of the adjustmentRatio
		// as the stretchability it provides is essential for column breaking to work properly
		if (removeGlueAtEnd)
			line.removeAll(glueAtEnd);
	}

	public List<KPItem> getItemsTo(int newBreakpointIndex)
	{
//		System.out.print("fb getItemsTo " + newBreakpointIndex);
		
		List<KPItem> retVal = new ArrayList<KPItem>();

		for (int i = index + 1; i <= newBreakpointIndex; i ++)
			retVal.add(lineObjects.get(i));
//		System.out.println(" from " + index);

		removeItemsAtBeginningAndEnd(retVal, false);
		
		return retVal;
	}
	
	public List<List<KPItem>> getFittedLines(boolean lastLineToEnd)
	{
		List<List<KPItem>> retVal = new ArrayList<List<KPItem>>();
		
		// recreate list to avoid altering original
		List<Integer> breaks = new ArrayList<Integer>();
		breaks.addAll(prevBreaks);
		breaks.add(index);
		if (lastLineToEnd) // add end position as final break
			breaks.add(lineObjects.size() - 1);
		
		// first breakpoint is always -1
		
		for (int i = 1; i < breaks.size(); i ++) // start at second position
		{
			List<KPItem> thisLine = new ArrayList<KPItem>();
			
			//for (int j = prevBreaks.get(i - 1) + 1; j <= prevBreaks.get(i); j ++)
			int firstItemIndex = breaks.get(i - 1) + 1; // prevBreaks.get(prevBreaks.size()-2) + 1 on last iteration
			int lastItemIndex = breaks.get(i);
			
//			System.out.print("*(" + (breaks.get(i - 1) + 1) + ", " + breaks.get(i) + ") ");
			
			thisLine.addAll(lineObjects.subList(firstItemIndex, lastItemIndex + 1));
			retVal.add(thisLine);
		}
		
		/*
		if (breaks.size() == 0 && !lastLineToEnd)
		{
			System.err.println("no breaks in fitted line!");
			return null;
		}
		*/
		
		/*
		
		// now for the last line from the last break to the end
		if (lastLineToEnd)
		{
			int firstItemIndex = 0; // if no prevBreaks, then set to first char
			if (prevBreaks.size() > 0)
				firstItemIndex = prevBreaks.get(prevBreaks.size() - 1) + 1; // i.e. getLast()
			int lastItemIndex = lineObjects.size() - 1; // i.e. also getLast()
			
			List<KPItem> thisLine = new ArrayList<KPItem>();
			thisLine.addAll(lineObjects.subList(firstItemIndex, lastItemIndex + 1));
			retVal.add(thisLine);
		}
		*/
		
		// now remove glue and penalty items from the start of each line
		// according to K-P p. 1125 (PDF p. 7)
		
		// and remove penalty items in the middle of each line
		// i.e. penalty at the _end_ of a line means that the break occurred
		// at the penalty (as legal break only at penalty or glue following box)
		
		// first item should be either a box or a penalty of value -inf (<= -1000)
		
		for (List<KPItem> line : retVal)
		{
			// 
			// below code moved to a reusable, static method as it is also
			// used by getFittedLines
			
			// removing at end for all but last line (finishing glue)
			
			if (line == retVal.get(retVal.size() - 1))
				removeItemsAtBeginningAndEnd(line, false);
			else
				removeItemsAtBeginningAndEnd(line, true);
			
			//removeItemsAtBeginningAndEnd(line, false);
			
			/*
			List<KPItem> itemsToRemove = new ArrayList<KPItem>();
			boolean startItemFound = false;
			List<KPItem> glueAtEnd = new ArrayList<KPItem>();
			for (KPItem item : line)
			{
				if (!startItemFound)
				{
					if (item instanceof KPBox ||
							item instanceof KPPenalty && 
							((KPPenalty)item).amount <= -1000)
					{
						startItemFound = true;
					}
					else
					{
						// glue or penalty > -1000
						itemsToRemove.add(item);
					}
				}
				else
				{
					// remove "unconsumed" penalties
					if (item instanceof KPPenalty &&
							item != line.get(line.size() - 1))
						itemsToRemove.add(item);
					
					// build up list of consecutive glues (unbroken by box)
					// and clear list if a box is encountered
					if (item instanceof KPGlue)
						glueAtEnd.add(item);
					else if (item instanceof KPBox)
						glueAtEnd = new ArrayList<KPItem>();
				}
			}
			line.removeAll(itemsToRemove);
			// glue left at end makes a mess of the justification
			line.removeAll(glueAtEnd);
			*/
		}
		
		// now remove all empty lines (occurs often with finishing glue & penalty)
		
		List<List<KPItem>> itemsToRemove = new ArrayList<List<KPItem>>();
		for (List<KPItem> line : retVal)
		{
//			if (line.size() == 0) itemsToRemove.add(line);
			
			// remove lines with no box objects
			// otherwise the finishing penalty will remain
			// (according to Knuth's rules)
			
			int boxes = 0;
			for (KPItem i : line)
				if (i instanceof KPBox) boxes ++;
			
			if (boxes == 0) itemsToRemove.add(line);
		}

//		System.out.println("itemsToRemove: " + itemsToRemove.size());
		retVal.removeAll(itemsToRemove);
		
//		System.out.println();
		return retVal;
	}
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("bp(");
		for (Integer pb : prevBreaks)
		{
			sb.append(pb + " ");
		}
		sb.append(index + ") ");
		
		return sb.toString();
	}
}

class KnuthPlassException extends Exception
{
    private Exception embedded;
    
    public KnuthPlassException(String msg)
    {
        super(msg);
    }
    
    public KnuthPlassException(Exception e)
    {
        super(e.getMessage());
        setEmbedded(e);
    }
    
    public Exception getEmbedded()
    {
        return embedded;
    }
    
    private void setEmbedded(Exception e)
    {
        embedded = e;
    }
}

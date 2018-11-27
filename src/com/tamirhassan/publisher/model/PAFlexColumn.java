package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;

import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPPenalty;

public class PAFlexColumn extends PAFlexContainer
{	
	// moved to PAFlexContainer
	//
	// FlexObject allows the use of spaces, penalties and objects such as figures ...
	//List<PAFlexObject> content = new ArrayList<PAFlexObject>();

	// the last figure is the number of breakpoints
	//List<Integer> breakpointOffsets = new ArrayList<Integer>();
	
	public PAFlexColumn()
	{
	
	}
	
	public PAFlexColumn(List<PAFlexObject> content)
	{
		this.content = content;
	}
	
	@Override
	public String textContent() 
	{
		// TODO more info here
		return "PAFlexSingleColumn";
	}
	
	/**
	 * lays out to given width and maximum height
	 * 
	 * @param width   given width
	 * @param height  maximum height
	 * 
	 */
	public PAFlexLayoutResult layout(float width, float height)
	{
		PAFlexLayoutResult objResult = layoutObject(width, height);
		
		List<PAFlexFloat> floats = objResult.getFloats();
		if (floats == null || floats.size() == 0)
		{
			return objResult;
		}
		else
		{
			// TODO: check whether objresult returned success or partial success
			
			System.out.println("float found");
			// crude algorithm to start off
			
			// place figure at the top of the column,
			// then format the rest
			
			// only place the first figure;
			// ignore any further ones for now
			
			PAFlexFloat thisFloat = floats.get(0);
			PAFlexLayoutResult floatResult = thisFloat.layout(width, height);
			
			// TODO: vertical page division (X-cut) instead of sequence
			// TODO: float list
			
			
			// optmization/demerit calculation for object
			// (greedy algorithm)
			
			// global optimization is reserved for external procedures
			
			if (floatResult.exitStatus == PAFlexLayoutResult.ESTAT_SUCCESS)
			{
				PAPhysColumn result = new PAPhysColumn();
				result.width = width;
				
				
				
				// TODO: add spacer
//				result.getItems().add(new KPGlue(6 * 72));
				
//				float residualHeight = height - floatResult.getResult().contentHeight();
				float residualHeight = 
						height - floatResult.getResult().height - thisFloat.spacing;
				
				PAFlexLayoutResult contentResult = layoutObject(width, residualHeight);
				if (contentResult.exitStatus == PAFlexLayoutResult.ESTAT_SUCCESS ||
				contentResult.exitStatus == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
				{
					// check that we haven't pushed float's calling point to next page
					if (contentResult.getFloats().size() > 0 &&
							contentResult.getFloats().get(0) == thisFloat)
					{
						if (thisFloat.appearsBelow)
						{						
							// columns inside of column
							result.getItems().add(contentResult.getResult());
							// add spacing with default stretch- and shrinkability (1/2 and 1/3)
							result.getItems().add(new KPGlue(thisFloat.spacing));
							PAFlexLayoutResult retVal = new PAFlexLayoutResult
									(result, -1, contentResult.remainingContent, contentResult.exitStatus);
							result.getItems().add(floatResult.getResult());
							
							return retVal;
						}
						else
						{
							result.getItems().add(floatResult.getResult());
							
							// columns inside of column
							result.getItems().add(contentResult.getResult());
							// add spacing with default stretch- and shrinkability (1/2 and 1/3)
							result.getItems().add(new KPGlue(thisFloat.spacing));
							PAFlexLayoutResult retVal = new PAFlexLayoutResult
									(result, -1, contentResult.remainingContent, contentResult.exitStatus);
							
							return retVal;
						}
					}
					else
					{
						// quickfix 2018-08-10
						// float's calling point pushed to next page
						// return initial result
						// but always as partial success
						// with float as remaining content
						
						objResult.exitStatus = PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS;
						if (objResult.remainingContent != null)
						{
							PAFlexColumn remainingCol = (PAFlexColumn)objResult.remainingContent;
							remainingCol.getContent().add(0, thisFloat);
						}
						else
						{
							PAFlexColumn remainingCol = new PAFlexColumn();
							remainingCol.getContent().add(thisFloat);
						}
						
						return objResult;
					}
					
				}
				else
				{
					System.err.println("not enough room to add content after float");
					//...
					return objResult;
				}
			}
			else
			{
				System.err.println("not enough room to add float");
				//...
				return objResult;
			}
			
		}

	}
	
	/**
	 * lays out column only, excluding floats
	 * 
	 * @param width   given width
	 * @param height  maximum height
	 * 
	 */
	public PAFlexLayoutResult layoutObject(float width, float height)
	{
		// TODO: join vspaces and paragraphs together to form
		// PAPhysTextBlock
		
		System.out.println("in layout of PAFlexColumn. Content objects: " + content.size());
		
		PAPhysColumn result = new PAPhysColumn();
		result.setFlexID(id);
		result.width = width;
		result.height = height;
		//result.height = 0;
		PAFlexColumn remainingContent = new PAFlexColumn();
		remainingContent.setID(id);
		float resultHeight = 0.0f;
		boolean exitLoop = false;
		//boolean prevItemBox = false;
		int lastValidBreak = -1;
		List<PAFlexFloat> floats = new ArrayList<PAFlexFloat>();
		
		// to determine:
		// resultObj and remainingContent
		
		// for backtracking:
		// store "last good" result
		// copy of resultObj, copy of remainingContent
		// partially successful not possible - as this implies breaking here
		
		// backtracking only occurs when invalid breakpoint reached; all objects up to that point
		// will have been fully laid out
		
		// according to K-P p.7
		// one can break at a penalty, providing it is not infinity (10000), or
		// one can break at glue, as long as this glue immediately follows a box
		// this means a penalty of 10000 precedes an invalid breakpoint, i.e. HEADING PENALTY GLUE PAR
		//
		// changes for vertical use:
		// one can break at a partially laid out box
		// => one must also be able to break at a fully laid out box*
		// unless a penalty succeeds it
		// (headings >3 lines can also be broken!)
		//
		// * otherwise, if there is room for the paragraph, but not the glue after it,
		// the whole para gets pushed to the next page!
		
		//for (PAFlexObject contentObj : content)
		for (int i = 0; i < content.size(); i ++)
		{
			PAFlexObject contentObj = (PAFlexObject)content.get(i);
			///System.out.println("i: " + i + " content: " + contentObj);
			if(!exitLoop)
			{
				float residualHeight = height - resultHeight;
				
				if (contentObj instanceof PAFlexFloat)
				{
					floats.add((PAFlexFloat)contentObj);
					
					// never backtrack, otherwise float might appear twice!
					lastValidBreak = i;
				}
				else if (contentObj instanceof PAFlexContainer || contentObj instanceof PAFlexIncolObject)
				{
					if (contentObj instanceof PAFlexTable)
					{
						int dummy = 0;
						dummy ++;
					}
					
					// at a box
					PAFlexLayoutResult resultObj = null;
					
					if (contentObj instanceof PAFlexContainer)
					{
						// e.g. multicol
						resultObj = 
								((PAFlexContainer)contentObj).layout(width, residualHeight);
					}
					else if (contentObj instanceof PAFlexIncolObject)
					{
						// e.g. paragraph; equation (not vspace)
						
						if (contentObj instanceof PAFlexBreakableObject)
						{
							System.out.print("tc: ");
							System.out.println(((PAFlexBreakableObject)contentObj).textContent());
							// paragraph, list, etc.
							resultObj = 
									((PAFlexBreakableObject)contentObj).layout(width, residualHeight);					
						}
						else
						{
							resultObj = 
									((PAFlexIncolObject)contentObj).layout(width);
							
							// 2018-08-10 hack added to check height
							if (resultObj.getExitStatus() == PAFlexLayoutResult.ESTAT_SUCCESS)
							{
								if (resultObj.getResult().getHeight() > residualHeight)
									resultObj.exitStatus = PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT;
							}
							
							// TODO: perhaps move to PAFlexIncolObject - 2-parameter method?
							// not to happy with altering the result object here,
							// even though it's a disposable object
						}
					}
					
					///System.out.println("contentObj: " + contentObj);
					
					if (resultObj.getExitStatus() == PAFlexLayoutResult.ESTAT_SUCCESS)
					{
						// add to result
						result.getItems().add(resultObj.getResult());
						resultHeight += resultObj.getResult().getHeight();
						
						/*
						if (contentObj instanceof PAFlexIncolObject)
						{
							if (((PAFlexIncolObject) contentObj).alignment == 
									PAFlexIncolObject.ALIGN_CENTRE ||
									((PAFlexIncolObject) contentObj).alignment == 
									PAFlexIncolObject.ALIGN_CENTRE_KNUTH)
							{
								if (resultObj.getResult().getWidth() < width)
								{
									float xOffset = 0.5f * 
											(width - resultObj.getResult().getWidth());
									
									result.xOffsets.put(resultObj.getResult(), xOffset);
								}
							}
							else if (((PAFlexIncolObject) contentObj).alignment == 
									PAFlexIncolObject.ALIGN_RIGHT)
							{
								if (resultObj.getResult().getWidth() < width)
								{
									float xOffset =
											(width - resultObj.getResult().getWidth());
									
									result.xOffsets.put(resultObj.getResult(), xOffset);
								}
							}
						}
						*/
						
						// TODO: demerits?
						
						// no update to lastValidBreak, as we cannot break at a box (unless partial success)
						// NEW: we do break here as long as a penalty of infinity does not follow this box
						if (i == content.size() - 1 || 
								!(content.get(i + 1) instanceof KPPenalty) ||
								((KPPenalty)content.get(i + 1)).getAmount() < 10000)
						{
							lastValidBreak = i;
						}
					}
					else if (resultObj.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
					{
						System.out.println("partial success");
						
						exitLoop = true;
						
						// TODO: ensure it doesn't get stuck
						
						// add to result
						result.getItems().add(resultObj.getResult());
						resultHeight += resultObj.getResult().getHeight();
						remainingContent.getContent().add(resultObj.getRemainingContent());
						
						// this result is always the final result, as this is always a valid break
						// (note that the paragraphs will not return widow _or_ orphan results if detection enabled)
						
					}
					else if (resultObj.getExitStatus() == PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT)
					{
						System.out.println("insufficient height");
						
						exitLoop = true;
						
						// TODO: ensure it doesn't get stuck
						
						remainingContent.getContent().add(contentObj);	
						
						// backtrack to last good result - this is not necessarily the previous iteration
						// and update result.items and remainingcontent accordingly
						
						if (lastValidBreak < i - 1)
						{
							for (int j = lastValidBreak + 1; j <= i - 1; j ++)
							{
								// remove extra laid out content after valid break
								//result.getItems().remove(j);
								// always remove from the end (as indices change)
								result.getItems().remove(result.getItems().size() - 1);
								// add non-laid out content at beginning of list
								remainingContent.getContent().add(j - (lastValidBreak + 1), content.get(j));
							}
						}
					}
					else if (resultObj.getExitStatus() == PAFlexLayoutResult.ESTAT_FAIL)
					{
						// skip this object?
						
						// TODO: error handling here
					}
				}
				
				else if (contentObj instanceof KPGlue)
				{
					// clone the glue
					KPGlue vSpace = new KPGlue((KPGlue) contentObj, 0.0f);
					
					// check if enough space to add glue (makes no difference in many cases)
					if (resultHeight + vSpace.spaceAmount() <= height)
					{
						// add without stretching or shrinking
						
						// do not add glue at top of new column (unless glue*)
						// NB: If several glues at start of page, all will be removed
						if (result.getItems().size() > 0 || ((KPGlue)contentObj).isIndent())
						{
							result.getItems().add(vSpace);
							
							// add non-stretched glue to result height (no optmization)
							resultHeight += vSpace.spaceAmount();
							
							// break if this glue follows a box
							if ((content.get(i - 1) instanceof PAFlexContainer 
									|| content.get(i - 1) instanceof PAFlexIncolObject))
							{
								// TODO: check if enough space before adding
								lastValidBreak = i;
							}
						}
					}
					else
					{
						// identical to above routine
						
						exitLoop = true;
						
						// TODO: ensure it doesn't get stuck
						
						remainingContent.getContent().add(contentObj);	
						
						// backtrack to last good result - this is not necessarily the previous iteration
						// and update result.items and remainingcontent accordingly
						
						if (lastValidBreak < i - 1)
						{
							for (int j = lastValidBreak + 1; j <= i - 1; j ++)
							{
								// remove extra laid out content after valid break
								//result.getItems().remove(j);
								// always remove from the end (as indices change)
								result.getItems().remove(result.getItems().size() - 1);
								// add non-laid out content at beginning of list
								remainingContent.getContent().add(j - (lastValidBreak + 1), content.get(j));
							}
						}
					}
				}
				else if (contentObj instanceof KPPenalty)
				{
					// extra box for penalty (hyphenation penalty) not supported (unnecessary here)
					// therefore no height check
					
					result.getItems().add((KPPenalty) contentObj);
					
					if (((KPPenalty) contentObj).getAmount() < 10000)
						lastValidBreak = i;
				}
			}
			else
			{
				// out of space
				remainingContent.getContent().add(contentObj);
			}
		}
		System.out.println("thousand");

		// resultHeight calculation is not carried out in case of partical success
		// call column's method instead
//		result.height = result.contentHeight();
		
		if (exitLoop)
		{
			return new PAFlexLayoutResult(result, -1.0f, remainingContent, floats,
					PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS);
		}
		else if (result.getItems().size() == 0)
		{
			return new PAFlexLayoutResult(null, -1.0f, remainingContent, floats,
					PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT);
		}
		else
		{
			result.height = result.contentHeight();
			return new PAFlexLayoutResult(result, -1.0f, null, floats,
					PAFlexLayoutResult.ESTAT_SUCCESS);
		}
	}
	

}

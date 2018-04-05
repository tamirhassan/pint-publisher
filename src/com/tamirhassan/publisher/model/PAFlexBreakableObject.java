package com.tamirhassan.publisher.model;

import java.util.List;



/**
 * to be extended not just for paragraphs, but other breakable
 * structures with discrete layout, such as lists, etc.
 * 
 * (therefore no alignment or linespacing here)
 * 
 * @author tam
 *
 */
public abstract class PAFlexBreakableObject extends PAFlexIncolObject {
	
	final public static int LAYOUT_SINGLE = 11;
	final public static int LAYOUT_FLEX = 12;
	final public static int LAYOUT_LOOSEST = -1;
	final public static int LAYOUT_TIGHTEST = 1;
	
	public abstract float getLineSpacing();
	
	public abstract PAFlexLayoutResult layout(
			float width, float height);
	
	/*
	public abstract PAFlexLayoutResult layout(
			float width, float height, int breakpointFrom, int breakpointTo);
	*/
	
	// no longer part of model?
	/*  
	public abstract List<PAFlexLayoutResult> layout
			(float width, float height,
			float stretchFactor, boolean flex, 
			int breakFrom, int breakTo, List<List<Integer>> breakpoints,
			List<Integer> allLegalBreakpoints);
	*/
	/*
	public abstract PAFlexLayoutResult layoutLoosest(float width, float height,
			float stretchFactor, 
			int breakFrom, int breakTo, List<List<Integer>> breakpoints, 
			List<Integer> allLegalBreakpoints);
	
	public abstract PAFlexLayoutResult layoutTightest(float width, float height,
			float stretchFactor, 
			int breakFrom, int breakTo, List<List<Integer>> breakpoints, 
			List<Integer> allLegalBreakpoints);
	*/
}

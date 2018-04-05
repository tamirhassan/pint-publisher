package com.tamirhassan.publisher.knuthplass;

import java.util.List;

/**
 * a tuple with the K-P result (list of KPItems)
 * and the result's overall demerits
 * 
 * @author tam
 *
 */
public class KPResult
{
	List<List<KPItem>> items;
	double demerits;
	List<Double> lineDemerits;
	List<Double> adjRatios;
	List<Integer> breakpoints;
	
	public KPResult()
	{
		// empty
	}
	
	public KPResult(List<List<KPItem>> items, double demerits, 
			List<Double> lineDemerits, List<Double> adjRatios, 
			List<Integer> breakpoints)
	{
		this.items = items;
		this.demerits = demerits;
		this.lineDemerits = lineDemerits;
		this.adjRatios = adjRatios;
		this.breakpoints = breakpoints;
	}

	public List<List<KPItem>> getItems() {
		return items;
	}

	public void setItems(List<List<KPItem>> items) {
		this.items = items;
	}

	public double getDemerits() {
		return demerits;
	}

	public void setDemerits(double demerits) {
		this.demerits = demerits;
	}

	public List<Double> getLineDemerits() {
		return lineDemerits;
	}

	public void setLineDemerits(List<Double> lineDemerits) {
		this.lineDemerits = lineDemerits;
	}

	public List<Double> getAdjRatios() {
		return adjRatios;
	}

	public void setAdjRatios(List<Double> adjRatios) {
		this.adjRatios = adjRatios;
	}

	public List<Integer> getBreakpoints() {
		return breakpoints;
	}

	public void setBreakpoints(List<Integer> breakpoints) {
		this.breakpoints = breakpoints;
	}
}
package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;


/**
 * a pair with the layout result (list of ? extends PAPhysObject)
 * and the result's overall demerits
 * plus height and width
 * 
 * @author tam
 *
 */
public class PAFlexLayoutResult
{
	public final static int ESTAT_SUCCESS = 0;
	public final static int ESTAT_PARTIAL_SUCCESS = 1; // partial success
	public final static int ESTAT_FAIL = 10;
	public final static int ESTAT_FAIL_INSUFFICIENT_HEIGHT = 11;
	
	PAPhysContainer result;
	double demerits;
	PAFlexObject remainingContent;
	int exitStatus;
	
	// floats that have been called out in the content laid out
	List<PAFlexFloat> floats = new ArrayList<PAFlexFloat>();
	
	public PAFlexLayoutResult()
	{
		// empty
	}
	
	public PAFlexLayoutResult(PAPhysContainer result, double demerits, 
			PAFlexObject remainingContent, int exitStatus)
	{
		this.result = result;
		this.demerits = demerits;
		this.remainingContent = remainingContent;
		this.exitStatus = exitStatus;
	}
	
	public PAFlexLayoutResult(PAPhysContainer result, double demerits, 
			PAFlexObject remainingContent, List<PAFlexFloat> floats, int exitStatus)
	{
		this.result = result;
		this.demerits = demerits;
		this.remainingContent = remainingContent;
		this.floats = floats;
		this.exitStatus = exitStatus;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(int exitStatus) {
		this.exitStatus = exitStatus;
	}

	public PAPhysContainer getResult() {
		return result;
	}

	public void setResult(PAPhysContainer result) {
		this.result = result;
	}

	public double getDemerits() {
		return demerits;
	}

	public void setDemerits(double demerits) {
		this.demerits = demerits;
	}

	public PAFlexObject getRemainingContent() {
		return remainingContent;
	}

	public void setRemainingContent(PAFlexContainer remainingContent) {
		this.remainingContent = remainingContent;
	}

	public List<PAFlexFloat> getFloats() {
		return floats;
	}

	public void setFloats(List<PAFlexFloat> floats) {
		this.floats = floats;
	}

}
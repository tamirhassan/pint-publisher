package com.tamirhassan.publisher.model;

public class PAFragment {

	String text;
	float horizAdjust;
	
	public PAFragment(String text, float horizAdjust)
	{
		this.text = text;
		this.horizAdjust = horizAdjust;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public float getHorizAdjust() {
		return horizAdjust;
	}
	public void setHorizAdjust(float horizAdjust) {
		this.horizAdjust = horizAdjust;
	}
	
}

package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PAPhysAbsPosContainer // extend something?
{
	List<PAPhysObject> items = new ArrayList<PAPhysObject>();
	
	float x1;
	float y2;
	int flexID; // duplicated from PAPhysContainer -> object structure?
	
	public PAPhysAbsPosContainer(float x1, float y2)
	{
		this.x1 = x1;
		this.y2 = y2;
		items = new ArrayList<PAPhysObject>();
	}

	public List<PAPhysObject> getItems() {
		return items;
	}

	public void setItems(List<PAPhysObject> items) {
		this.items = items;
	}

	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}

	public int getFlexID() {
		return flexID;
	}

	public void setFlexID(int flexID) {
		this.flexID = flexID;
	}

	public String textContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public float contentHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void render (PDPageContentStream contentStream)
			throws IOException
	{
		// TODO Auto-generated method stub
		PAPhysColumn col = new PAPhysColumn(this.items, Float.MAX_VALUE, Float.MAX_VALUE);
		col.render(contentStream, x1, y2);
	}
}
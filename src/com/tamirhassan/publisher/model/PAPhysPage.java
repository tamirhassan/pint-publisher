package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PAPhysPage //extends PAPhysObject 
//implements PACanvas
{
	// these are page specifications
	List<PAPhysContainer> items = new ArrayList<PAPhysContainer>();
	List<PAPhysAbsPosContainer> absItems = new ArrayList<PAPhysAbsPosContainer>();
	float leftMargin;
	float rightMargin;
	float topMargin;
	float bottomMargin;
	
	List<Integer> callouts = new ArrayList<Integer>();
	
	int flexID;
	
	//TODO: header and footer
	
	// height and with were previously inherited from PAPhysObject
	float height;
	float width;
	
	int pageNo = -1; // no effect
	
	public PAPhysPage()
	{
		
	}
	
	public PAPhysPage(float width, float height)
	{
		this.width = width;
		this.height = height;
	}
	
	public PAPhysPage(float width, float height, float leftMargin, float rightMargin,
			float topMargin, float bottomMargin)
	{
		this.width = width;
		this.height = height;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
	}
	
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public List<PAPhysContainer> getItems() {
		return items;
	}

	public void setItems(List<PAPhysContainer> items) {
		this.items = items;
	}
	
	public float getLeftMargin() {
		return leftMargin;
	}


	public void setLeftMargin(float leftMargin) {
		this.leftMargin = leftMargin;
	}


	public float getRightMargin() {
		return rightMargin;
	}


	public void setRightMargin(float rightMargin) {
		this.rightMargin = rightMargin;
	}


	public float getTopMargin() {
		return topMargin;
	}


	public void setTopMargin(float topMargin) {
		this.topMargin = topMargin;
	}


	public int getPageNo() {
		return pageNo;
	}


	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}


	public float getBottomMargin() {
		return bottomMargin;
	}


	public void setBottomMargin(float bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	/* move to PhysPage
	public void render(PDPageContentStream contentStream) throws IOException
	{
		// TODO: marginal stuff; headers and footers
		
		content.render(contentStream, leftInsideMargin, height - topMargin);
	}
	*/
	
	public int getFlexID() {
		return flexID;
	}

	public void setFlexID(int flexID) {
		this.flexID = flexID;
	}

	public List<PAPhysAbsPosContainer> getAbsItems() {
		return absItems;
	}

	public void setAbsItems(List<PAPhysAbsPosContainer> absItems) {
		this.absItems = absItems;
	}

	public String textContent()
	{
		// TODO
//		System.err.println("PAPage.toText() not yet implemented");
		return "PAPhysPage";
	}
	
	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}
	
	public void render(PDDocument document) throws IOException
	{
		PDPage page = new PDPage(new PDRectangle(width, height));
		document.addPage( page );
		
		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		
		// add absolutely positioned items, if any (e.g. header, footer)
		for (PAPhysAbsPosContainer item : absItems)
			item.render(contentStream);
		
		// now add the page's content to this stream
		for (PAPhysContainer item : items)
			item.render(contentStream, leftMargin, (height - topMargin));
		
		// Make sure that the content stream is closed:
		contentStream.close();
	}

	/*
	public void render(PDPageContentStream contentStream) throws IOException
	{
		System.out.println("contentStream: " + contentStream);
		content.render(contentStream, leftMargin, (height - topMargin));
	}
	*/
}

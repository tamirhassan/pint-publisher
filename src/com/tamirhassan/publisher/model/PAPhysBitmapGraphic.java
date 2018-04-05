package com.tamirhassan.publisher.model;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PAPhysBitmapGraphic extends PAPhysGraphic
{
	// TODO: separate classes for vector and bitmap?
	
	// decided to implement PAPhysContainer and not PAPhysObject
	// PAPhysObject combines container (width/height/render) and glue (amount)
	
	// TODO: PAPhysContainer also has demerits = 1.0f;
	
	//	File bitmapFile;
	PDImageXObject pdImage;
	
	// note: just like with fonts, require PDDoc to create PDImageXObject
	// decided to do this at creation (not rendering) time, as
	// custom fonts are handled this way too
	//
	// this approach allows reusing the same object if graphic is reused
	
	public PAPhysBitmapGraphic(File bitmapFile, PDDocument doc, float width, float height) 
			throws IOException
	{
//		this.bitmapFile = bitmapFile;
		pdImage = PDImageXObject.createFromFileByContent(bitmapFile, doc);
		this.demerits = 0.0d;
		this.width = width;
		this.height = height;
	}
	
	public PAPhysBitmapGraphic(String bitmapFile, PDDocument doc, float width, float height) 
			throws IOException
	{
//		this.bitmapFile = new File(bitmapFile);
		pdImage = PDImageXObject.createFromFile(bitmapFile, doc);
		this.demerits = 0.0d;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(PDPageContentStream contentStream, float x1, float y2)
			throws IOException 
	{
		contentStream.drawImage(pdImage, x1, y2 - height, width, height);
	}
}
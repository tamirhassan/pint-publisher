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
		correctDimensions();
	}
	
	public PAPhysBitmapGraphic(String bitmapFile, PDDocument doc, float width, 
			float height) throws IOException
	{
//		this.bitmapFile = new File(bitmapFile);
		pdImage = PDImageXObject.createFromFile(bitmapFile, doc);
		this.demerits = 0.0d;
		this.width = width;
		this.height = height;
		correctDimensions();
	}

	protected void correctDimensions()
	{
		if (width >= 0 && height >= 0)
		{
			// width and height specified; do nothing
		}
		else if (width < 0 && height >= 0)
		{
			// height specified; calculate width
			width = height * ((float)pdImage.getWidth() / (float)pdImage.getHeight());
		}
		else if (height < 0 && width >= 0)
		{
			// width specified; calculate height
			height = width * ((float)pdImage.getHeight() / (float)pdImage.getWidth());
		}
		else
		{
			// no dimensions specified; for now default to 72dpi
			width = pdImage.getWidth();
			height = pdImage.getHeight();
		}
	}
	
	@Override
	public void render(PDPageContentStream contentStream, float x1, float y2)
			throws IOException 
	{
		// missing dimensions need to be added earlier
		// to enable inset calculation, etc.
		
		/*
		float renderWidth = width, renderHeight = height;
		
		if (renderWidth >= 0 && renderHeight >= 0)
		{
			// width and height specified; do nothing
		}
		else if (renderWidth < 0 && renderHeight >= 0)
		{
			// height specified; calculate width
			renderWidth = height * ((float)pdImage.getWidth() / (float)pdImage.getHeight());
		}
		else if (renderHeight < 0 && renderWidth >= 0)
		{
			// width specified; calculate height
			renderHeight = width * ((float)pdImage.getHeight() / (float)pdImage.getWidth());
		}
		else
		{
			// no dimensions specified; for now default to 72dpi
			renderWidth = pdImage.getWidth();
			renderHeight = pdImage.getHeight();
		}
		*/
		
		contentStream.drawImage(pdImage, x1, y2 - height, width, height);
	}
}
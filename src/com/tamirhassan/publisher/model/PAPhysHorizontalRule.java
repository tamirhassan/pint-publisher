package com.tamirhassan.publisher.model;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// 2018-06-29 this also makes sense, but would require a FlexRule and a PhysRule
// only the latter with concrete height & width -> later
//public class PAHorizontalRule extends PAPhysContainer implements PAFlexObject


public class PAPhysHorizontalRule extends PAPhysContainer // = renderable
{
	public PAPhysHorizontalRule()
	{
		
	}
	
	@Override
	public String textContent() 
	{
		return " <hr/> ";
	}
	
	
	
	@Override
	public float contentHeight() 
	{
		return 0;
	}

	@Override
	public void render(PDPageContentStream contentStream, float x1, float y2)
			throws IOException 
	{
		contentStream.saveGraphicsState();
		contentStream.setLineWidth(0.3f);
		contentStream.moveTo(x1, y2);
		contentStream.lineTo(x1 + width, y2);
		contentStream.stroke();
		contentStream.restoreGraphicsState();
	}

	@Override
	public String tagName() 
	{
		return "hr";
	}
	
}
package com.tamirhassan.publisher.model;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

// 2018-06-29 this also makes sense, but would require a FlexRule and a PhysRule
// only the latter with concrete height & width -> later
//public class PAHorizontalRule extends PAPhysContainer implements PAFlexObject


public class PAFlexHorizontalRule extends PAFlexIncolObject
{
	public PAFlexHorizontalRule()
	{
		
	}
	
	@Override
	public String textContent() 
	{
		return " PAFlexHorizontalRule ";
	}

	@Override
	public PAFlexLayoutResult layout(float width) 
	{
		PAPhysHorizontalRule physObj = new PAPhysHorizontalRule();
		physObj.setHeight(0);
		physObj.setWidth(width);
		
		PAFlexLayoutResult res = new PAFlexLayoutResult(
				physObj, 0.0, null, PAFlexLayoutResult.ESTAT_SUCCESS);
		
		return res;
	}
	
}
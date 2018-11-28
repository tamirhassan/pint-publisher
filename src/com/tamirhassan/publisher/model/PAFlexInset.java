package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Used for ACM copyright blurb
 * 
 * @author tam
 *
 */
public class PAFlexInset extends PAFlexFloat // TODO: implement PAFlexColumn?
{
	// List<PAFlexObject> content inherited from ancestor
	
	// TODO: above or below!
	
	public PAFlexInset()
	{
	
	}
	
	public PAFlexInset(List<PAFlexObject> content)
	{
		this.content = content;
	}
	
	public PAFlexLayoutResult layout(float width)
	{
		return this.layout(width, Float.MAX_VALUE); //, -1, -1);
	}
	
	@Override
	public PAFlexLayoutResult layout(float width, float height) 
	{
		PAFlexColumn fc = new PAFlexColumn(content);
		
		PAFlexLayoutResult res = fc.layout(width, height);
		res.getResult().setFlexID(this.getID());
		
//		return fc.layout(width, height);
		return res;
	}

	@Override
	public String textContent() 
	{
		PAFlexColumn fc = new PAFlexColumn(content);
		return fc.toString();
	}
}
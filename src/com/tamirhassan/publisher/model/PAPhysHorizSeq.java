package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.tamirhassan.publisher.knuthplass.KPGlue;

public class PAPhysHorizSeq extends PAPhysContainer
{
	List<PAPhysObject> items = new ArrayList<PAPhysObject>();
	
	// set leading to 120% of font size
	// http://help.adobe.com/en_US/illustrator/cs/using/WSC7A7BE38-87CE-4edb-B55A-F27458444E40a.html
	// TODO: move to paragraph?
	
	public PAPhysHorizSeq()
	{
	}
	
	public PAPhysHorizSeq(List<PAPhysObject> items, float width, float height)
	{
		this.items = items;
		this.width = width;
		this.height = height;
	}

	public List<PAPhysObject> getItems() {
		return items;
	}

	public void setItems(List<PAPhysObject> items) {
		this.items = items;
	}
	
	public String textContent()
	{
//		StringBuffer retVal = new StringBuffer();
//		for (PAPhysObject item : items)
//			retVal.append(item.toText() + "\n");
//		return retVal.toString();
		
//		return "PAPhysHorizSeq with " + items.size() + " items";
		
		StringBuffer retVal = new StringBuffer();
		retVal.append("PAPhysHorizSeq with " + items.size() + " items\n");
		for (PAPhysObject item : items)
			retVal.append(item.hashCode() + " " + item.toString() + "\n");
		return retVal.toString();
		//return "PAPhysHorizSeq with " + items.size() + " items";
	}
	
	@Override
	/**
	 * returns the amount of vertical space used up by
	 * this container, i.e. the height of the tallest column
	 */
	public float contentHeight()
	{
		float retVal = 0.0f;
		for (PAPhysObject c : items)
		{
			//System.out.println("** in CH with item: " + c);
			if (c instanceof PAPhysContainer)
			{
				PAPhysContainer r = (PAPhysContainer)c;
				//System.out.println("** in CH with renderable: " + r + " and height: " + r.contentHeight());
				if (r.contentHeight() > retVal)
					retVal = r.contentHeight();
			}
		}
		return retVal;
	}
	
	public void render(PDPageContentStream contentStream, 
			float x1, float y2) throws IOException
	{
		//System.out.println("in render of " + this);
		
		if (items.size() < 1) 
		{
			System.err.println("nothing to render");
			return; // nothing to render
		}
		
//		contentStream.beginText();
//		contentStream.moveTextPositionByAmount(x1, y2);
//		contentStream.endText();
		
		for (PAPhysObject o : items)
		{
			if (o instanceof PAPhysContainer)
			{
				((PAPhysContainer)o).render(contentStream, x1, y2);
				x1 += ((PAPhysContainer)o).getWidth();
			}
			else if (o instanceof KPGlue)
			{
				x1 += ((KPGlue) o).getAmount();
//				contentStream.beginText();
//				contentStream.moveTextPositionByAmount(0, 1 - ((PAVSpace) c).amount);
//				contentStream.endText();
//				System.out.println("appending: move 0, " + (1 - ((PAVSpace) c).amount));
					
				// newline also required after a vspace
				// prevObjectLine = false;
			}
		}
		
//		System.out.println("content height: " + contentHeight());
	}
}

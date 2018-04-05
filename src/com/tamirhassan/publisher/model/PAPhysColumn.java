package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.tamirhassan.publisher.knuthplass.KPGlue;

public class PAPhysColumn extends PAPhysContainer //implements PARenderable
{
	List<PAPhysObject> items = new ArrayList<PAPhysObject>();
	
	// TODO: moved to paragraph; remove comment
	// set leading to 120% of font size
	// http://help.adobe.com/en_US/illustrator/cs/using/WSC7A7BE38-87CE-4edb-B55A-F27458444E40a.html
	
	public PAPhysColumn()
	{
	}
	
	public PAPhysColumn(List<PAPhysObject> items, float width, float height)
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
	
	// deprecated -- TODO: remove
	/*
	public List<PAPhysTextLine> onlyLines()
	{
		List<PAPhysTextLine> retVal = new ArrayList<PAPhysTextLine>();
		for (PAPhysObject c : items)
			if (c instanceof PAPhysTextLine)
				retVal.add((PAPhysTextLine) c);
		
		return retVal;
	}
	 */
	
	public String textContent()
	{
		StringBuffer retVal = new StringBuffer();
		for (PAPhysObject item : items)
			retVal.append(item.textContent() + "\n");
		return retVal.toString();
	}
	
	// TODO: uncertain whether to resurrect PAPhysParagraph
	// as it is not used for the output
	// but what about the demerit calculation? or is this also
	// done for the whole flow?
	/*
	public void stretchToWidth()
	{
		int index = -1;
		for (PAPhysContainer l : items)
		{
			index ++;
			if (index != items.size() - 1)
				l.stretchToWidth();
		}
	}
	
	public void stretchAndShrinkToWidth()
	{
		int index = -1;
		for (PAPhysContainer l : items)
		{
			index ++;
			if (index != items.size() - 1)
				l.stretchAndShrinkToWidth();
		}
	}
	
	// average fill ratio
	// composed of all the constituent lines (except the final)
	// overfull -> positive; underfull -> negative
	public float fillRatio()
	{
		if (items.size() < 2) return 1.0f;
		
		float ratioSum = 0.0f;
		int index = -1;
		for (PAPhysTextLine item : items)
		{
			index ++;
			if (index != items.size() - 1)
				ratioSum += item.fillRatio();
			
		}
		
		return ratioSum/items.size();
	}
	*/

	/**
	 * calculate content height based on all
	 * PARenderable and PAVSpace objects
	 */
	public float contentHeight()
	{
		float retVal = 0.0f;
		for (PAPhysObject o : items)
		{
			if (o instanceof PAPhysContainer)
			{
				retVal += ((PAPhysContainer) o).contentHeight();
			}
			else if (o instanceof KPGlue)
			{
				retVal += ((KPGlue) o).getAmount();
			}
		}
		return retVal;
	}
	
	/**
	 * calculate content height based on all
	 * PARenderable and PAVSpace objects;
	 * also allow PAPhysTextLine objects to be present;
	 * these are then removed after column balancing is complete
	 */
	/*
	public float intermediateHeightCalculation()
	{
		float retVal = 0.0f;
		for (PAPhysObject c : items)
		{
			System.out.println("***" + hashCode() + " in ch with " + c);
			if (c instanceof PARenderable)
			{
				retVal += ((PARenderable) c).contentHeight();
				System.out.println("***" + hashCode() + " renderable height: " + ((PARenderable) c).contentHeight());
			}
			else if (c instanceof PAPhysTextLine)
				retVal += ((PAPhysTextLine) c).getHeight();
			else if (c instanceof KPGlue)
				retVal += ((KPGlue) c).getAmount();
			else if (!(c instanceof KPPenalty))
			{
				// TODO: exception handling
				System.err.println("PAPhysColumn.items may only contain the types PARenderable, KPGlue and KPPenalty");
				System.err.println("as well as PAPhysTextLine for intermediate calculation");
				System.err.println("offending item: " + c);
				System.exit(1);
			}
		}
		System.out.println("***" + hashCode() + " in ch with retVal: " + retVal);
		return retVal;
	}
	*/
	
	/*
	// TODO: adjust to take into account that no line objects
	// are directly included in items
	public float contentHeight()
	{
		float retVal = 0.0f;
		
		PDSimpleFont currFont = null;
		float currFontSize = -1;
		
		boolean prevObjectLine = false;
		for (PAPhysObject c : items)
		{
			if (c instanceof PAPhysTextLine)
			{
				PAPhysTextLine l = (PAPhysTextLine)c;
				
				// append newline between lines -- automatically adds leading space
				if (prevObjectLine)
					retVal += (1 - l.leading * Utils.minimum(l.fontSize, currFontSize)); // currFontSize = font size of previous line
				
				if (l.font != currFont || l.fontSize != currFontSize)
				{
					currFont = l.font;
					currFontSize = l.fontSize;
				}
				retVal += l.fontSize;
				prevObjectLine = true;
			}
			else if (c instanceof PAVSpace)
			{
				retVal += ((PAVSpace)c).amount;
				prevObjectLine = false;
			}
		}
		
		return retVal; // TODO
	}
	*/
	
	public void render(PDPageContentStream contentStream, 
			float x1, float y2) throws IOException
	{
		System.out.println(hashCode() + "in render of PAPhysColumn with items: " + items.size());
		
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
			System.out.println(hashCode() + " item: " + o);
			if (o instanceof PAPhysContainer) // was: PARenderable
			{
				((PAPhysContainer)o).render(contentStream, x1, y2);
				y2 -= ((PAPhysContainer)o).contentHeight();
				
				System.out.println("  " + hashCode() + " with contentHeight: " + ((PAPhysContainer)o).contentHeight());
			}
			else if (o instanceof KPGlue)
			{
				y2 -= ((KPGlue) o).getAmount();
			}
		}
		
		renderWarning(contentStream, x1, y2);
		
//		System.out.println("content height: " + contentHeight());
	}
	
	// deprecated
	/*
	public void renderLinesDirectly(PDPageContentStream contentStream, 
			float x1, float y2) throws IOException
	{
		// TODO: when the Column contains a mixture of text and graphics,
		// split up into contiguous text chunks, each with a BeginText().
		
		// current method should be valid for a single text chunk
		
		if (items.size() < 1) 
		{
			System.err.println("nothing to render");
			return; // nothing to render
		}
		
		List<PAPhysTextLine> onlyLines = onlyLines();
		
		// account for height of first (text) line
		// as rendering is baseline based
		float yOffset = 0;
		if (onlyLines.size() > 0)
			yOffset = onlyLines.get(0).fontSize;

		contentStream.beginText();
		System.out.println("moving to offset: (" + x1 + ", " + (y2 - yOffset) + ")");
		contentStream.moveTextPositionByAmount(x1, (y2 - yOffset));
		
		PDSimpleFont currFont = null;
		float currFontSize = -1;
//		float currPropLeading = -1;
		float currAbsLeading = -1;
		
		boolean prevObjectLine = false;
		for (PAPhysObject c : items)
		{
			System.out.println("item found: " + c);
			if (c instanceof PAPhysTextLine)
			{
				PAPhysTextLine l = (PAPhysTextLine)c;
				
				// calculate current absolute leading based on the smallest
				// of the following two values: l.fontSize and (previous)currFontSize.
				// TODO: this doesn't quite correspond to the rules used by
				// LibreOffice, Word, etc.
				
				float smallerFontSize = l.fontSize;
				if (currFontSize > 0 && currFontSize < l.fontSize) // only takes effect after 1st line
					smallerFontSize = currFontSize;

				// NOTE: for the 1st line, NO LEADING SPACE is added
				// and therefore the calculation in contentHeight() is simpler
				// however, the value is set at the 1st line for tidyness
				// and only changed if it requires changing
				
				// TODO: change references to font size here to line height
				// ATM these are the same, but for e.g. math, may be different
				// leading should solely be calculated based on line height.
				
				float absLeading = smallerFontSize * l.leading; // affects spacing BEFORE line, not after
				
				// do the spacing and carriage return BEFORE the line and
				// then output the line
				
				// call setFont if font or fontsize changed
				if (l.font != currFont || l.fontSize != currFontSize)
				{
					contentStream.setFont( l.font, l.fontSize );
				}
				// adjust leading if fontsize or leading proportion changed
//				if (l.fontSize != currFontSize || l.leading != currPropLeading)
//				{
//					contentStream.appendRawCommands((l.fontSize * l.leading) + " TL \n");
//					System.out.println("appending: " + (l.fontSize * l.leading) + " TL \\n");
//				}
				// adjust leading if absolute leading value changed
				{
					contentStream.appendRawCommands(absLeading + " TL \n");
					System.out.println("appending: " + absLeading + " TL \\n");
				}
				// set current variables to new values
				if (l.font != currFont)
				{
					currFont = l.font;
				}
				if (l.fontSize != currFontSize)
				{
					currFontSize = l.fontSize;
				}
//				currently not in use
//				if (l.leading != currPropLeading)
//				{
//					currPropLeading = l.leading;
//				}
				if (absLeading != currAbsLeading)
				{
					currAbsLeading = absLeading;
				}
				
				// append newline between lines -- automatically adds leading space
				if (prevObjectLine)
				{
	//				contentStream.moveTextPositionByAmount(0, l.height * -1.2f);
					contentStream.appendRawCommands("T* \n");
					System.out.println("appending: T* \\n");
				}
				
				l.render(contentStream);
				System.out.println("appending: line " + l.toText());
				prevObjectLine = true;
			}
			else if (c instanceof PAVSpace)
			{
				
				contentStream.moveTextPositionByAmount(0, 1 - ((PAVSpace) c).amount);
				System.out.println("appending: move 0, " + (1 - ((PAVSpace) c).amount));
					
				// newline also required after a vspace
				// prevObjectLine = false;
				
			}
		}
		contentStream.endText();
		
		System.out.println("content height: " + contentHeight());
	}
	*/
}

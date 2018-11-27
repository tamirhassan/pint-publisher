package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tamirhassan.publisher.stylesheet.PAStylesheet;

public class PAFlexPageSpec
{
	int id;
	
	// content to be laid out
	PAFlexColumn content;
	
	// dimensions
	float width;
	float height;

	// can be all the same if necessary
	PAFlexMarginSpec oddMarginSpec;
	PAFlexMarginSpec evenMarginSpec;
	PAFlexMarginSpec firstMarginSpec;
	
//	boolean facingPages = false;

	int startPageNo = 1; // no effect yet
	
	// TODO: numbering
	// TODO: headers and footers
	// TODO: codes!
	
	// TODO: hardcode common sizes (e.g. A4)
	// TODO: default margins 1"?
	
	public PAFlexPageSpec(float width, float height)
	{
		this.width = width;
		this.height = height;
		this.content = new PAFlexColumn();
	}
	
	public PAFlexPageSpec(float width, float height, PAFlexColumn content)
	{
		this.width = width;
		this.height = height;
		this.content = content;
	}
	
	public PAFlexPageSpec(Element el, PAStylesheet stylesheet, Locale loc)
	{
    	this.width = (float) (210.0 * (72 / 25.4));
    	this.height = (float) (297.0 * (72 / 25.4));
    	this.content = new PAFlexColumn();
    	
//    	pageSpec.setID(pageSpecID);
    	
        if (el.hasAttribute("size"))
        {
        	String pageSizeString = el.getAttribute("size");
        	if (pageSizeString.equals("a4"))
        	{
        		// a4 is the default
        		// TODO: look up page sizes in dictionary
        	}
        	else if (pageSizeString.equals("a4l"))
        	{
        		this.setWidth((float)(297.0 * (72 / 25.4)));
        		this.setHeight((float)(210.0 * (72 / 25.4)));
        	}
        	else if (pageSizeString.equals("letter"))
        	{
        		this.setWidth((float)(8.5 * 72));
        		this.setHeight((float)(11.0 * 72));
        	}
        	else if (pageSizeString.equals("letterl"))
        	{
        		this.setWidth((float)(11.0 * 72));
        		this.setHeight((float)(8.5 * 72));
        	}
        }
        
        // TODO: replace parseFloat with parseValue method (e.g. 1in)
        // - currently supports only points
        
        // default margins 1 inch
        PAFlexMarginSpec ms = new PAFlexMarginSpec(72, 72, 72, 72);
        ms.setAttributes(el, stylesheet, loc);
        
    	// set for odd, even and first. Copy the object to allow changes later!
    	this.setOddMarginSpec(new PAFlexMarginSpec(ms));
    	this.setEvenMarginSpec(new PAFlexMarginSpec(ms));
    	this.setFirstMarginSpec(new PAFlexMarginSpec(ms));
    	
    	ms = this.getEvenMarginSpec();
    	ms.setEvenAttributes(el, stylesheet, loc);
    	
    	ms = this.getOddMarginSpec();
    	ms.setOddAttributes(el, stylesheet, loc);
    	
    	// first apply odd margins; if first page specified, will overwrite
    	ms = this.getFirstMarginSpec();
    	ms.setOddAttributes(el, stylesheet, loc);
    	ms.setFirstAttributes(el, stylesheet, loc);
    	
	}
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public PAFlexColumn getContent() {
		return content;
	}

	public void setContent(PAFlexColumn content) {
		this.content = content;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	/* moved to PhysPage
	public void render(PDPageContentStream contentStream) throws IOException
	{
		// TODO: marginal stuff; headers and footers
		
		content.render(contentStream, leftInsideMargin, height - topMargin);
	}
	*/
	
public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PAFlexMarginSpec getOddMarginSpec() {
		return oddMarginSpec;
	}

	public void setOddMarginSpec(PAFlexMarginSpec oddMarginSpec) {
		this.oddMarginSpec = oddMarginSpec;
	}

	public PAFlexMarginSpec getEvenMarginSpec() {
		return evenMarginSpec;
	}

	public void setEvenMarginSpec(PAFlexMarginSpec evenMarginSpec) {
		this.evenMarginSpec = evenMarginSpec;
	}

	public PAFlexMarginSpec getFirstMarginSpec() {
		return firstMarginSpec;
	}

	public void setFirstMarginSpec(PAFlexMarginSpec firstMarginSpec) {
		this.firstMarginSpec = firstMarginSpec;
	}

	public int getStartPageNo() {
		return startPageNo;
	}

	public void setStartPageNo(int startPageNo) {
		this.startPageNo = startPageNo;
	}

	//	public List<PAPhysObject> layout(float width, float height, float stretchFactor)
	/**
	 * currently limited to only one layout
	 */
	public List<PAPhysPage> layout()
	{
		List<PAPhysPage> retVal = new ArrayList<PAPhysPage>();
		
		//PAFlexColumn mainContentCol = new PAFlexColumn(content);
		
		/*
		float contentWidth = this.width - leftMargin - rightMargin;
		float contentHeight = this.height - topMargin - bottomMargin;
		*/
		
		// to begin with, set remainingContent = content
		PAFlexObject remainingContent = content; // = (PAFlexContainer) mainContentCol;
		content.setID(id);
		int index = startPageNo - 1;
		double totalDemerits = 0;
		
		while(remainingContent != null) 
			// && remainingContent.getContent().size() > 0)
			// no more empty remainingContent being returned
		{
			index ++;
			
			PAFlexMarginSpec ms;
			if (index == startPageNo)
			{
				// first
				ms = firstMarginSpec;
			}
			else if (index % 2 == 0)
			{
				// even
				ms = evenMarginSpec;
			}
			else
			{
				// odd
				ms = oddMarginSpec;
			}
			
			float contentWidth = this.width - ms.leftMargin - ms.rightMargin;
			float contentHeight = this.height - ms.topMargin - ms.bottomMargin;
			
			PAPhysPage newPage = new PAPhysPage (this.width, this.height,
					ms.leftMargin, ms.rightMargin, ms.topMargin, ms.bottomMargin);
			
			// add header (and TODO: footer)
			if (ms.headerLeft != null)
			{
				PAFlexLayoutResult layoutRes = ms.headerLeft.layout(width);
				PAPhysAbsPosContainer c = new PAPhysAbsPosContainer(ms.leftMargin, 
						30 + height - ms.topMargin);
				c.items.add(layoutRes.getResult());
				c.setFlexID(ms.headerLeft.id);
				newPage.absItems.add(c);
			}
			if (ms.headerRight != null)
			{
				PAFlexLayoutResult layoutRes = ms.headerRight.layout(width);
				PAPhysTextBlock tb = (PAPhysTextBlock)layoutRes.getResult();
				float lineWidth = tb.contentWidth();
				PAPhysAbsPosContainer c = new PAPhysAbsPosContainer(
						width - ms.rightMargin - lineWidth - 12, 
						30 + height - ms.topMargin);
				c.items.add(layoutRes.getResult());
				c.setFlexID(ms.headerRight.id);
				newPage.absItems.add(c);
			}
						
			newPage.setPageNo(index);
			newPage.setFlexID(id); // TODO: allow null values
			
			// casting is legitimate as remaining content must be of same type (PAFlexColumn extends PAFlexContainer)
			PAFlexLayoutResult layoutRes = ((PAFlexContainer) remainingContent)
					.layout(contentWidth, contentHeight);

			PAPhysContainer resObj = layoutRes.getResult();
//			resObj.setHeight(this.height); // otherwise zero - now done in calling method
			newPage.getItems().add(layoutRes.getResult());
			
			System.out.println("adding new page");
			
			retVal.add(newPage);
			
			remainingContent = layoutRes.getRemainingContent();
			
		}
		
		return retVal;
	}
	
	public String textContent()
	{
		// TODO
//		System.err.println("PAPage.toText() not yet implemented");
		return "PAFlexPageSpec";
	}
}

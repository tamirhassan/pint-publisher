package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;

public class PAFlexPageSpec 
							// OLD: extends PAFlexContainer 
							// OLD: implements PACanvasSpec
{
	int id;
	
	// content to be laid out
	PAFlexColumn content;
	
	// dimensions
	float width;
	float height;

	float leftInsideMargin;
	float rightOutsideMargin;
	float topMargin;
	float bottomMargin;
	
	// TODO: header and footer
	
	boolean facingPages = false;

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
	
	public float getLeftInsideMargin() {
		return leftInsideMargin;
	}

	public void setLeftInsideMargin(float leftInsideMargin) {
		this.leftInsideMargin = leftInsideMargin;
	}

	public float getRightOutsideMargin() {
		return rightOutsideMargin;
	}

	public void setRightOutsideMargin(float rightOutsideMargin) {
		this.rightOutsideMargin = rightOutsideMargin;
	}

	public float getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(float topMargin) {
		this.topMargin = topMargin;
	}

	public float getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(float bottomMargin) {
		this.bottomMargin = bottomMargin;
	}
	
	/* moved to PhysPage
	public void render(PDPageContentStream contentStream) throws IOException
	{
		// TODO: marginal stuff; headers and footers
		
		content.render(contentStream, leftInsideMargin, height - topMargin);
	}
	*/
	
//	public List<PAPhysObject> layout(float width, float height, float stretchFactor)
	/**
	 * currently limited to only one layout
	 */
	public List<PAPhysPage> layout()
	{
		List<PAPhysPage> retVal = new ArrayList<PAPhysPage>();
		
		//PAFlexColumn mainContentCol = new PAFlexColumn(content);
		
		float contentWidth = this.width - leftInsideMargin - rightOutsideMargin;
		float contentHeight = this.height - topMargin - bottomMargin;
		
		// to begin with, set remainingContent = content
		PAFlexObject remainingContent = content; // = (PAFlexContainer) mainContentCol;
		content.setID(id);
		int index = startPageNo - 1;
		double totalDemerits = 0;
		
		while(remainingContent != null) 
			// && remainingContent.getContent().size() > 0)
			// no more empty remainingContent being returned
		{
			
			PAPhysPage newPage = new PAPhysPage (this.width, this.height,
					leftInsideMargin, rightOutsideMargin, topMargin, bottomMargin);
			
			index ++;
			newPage.setPageNo(index);
			newPage.setFlexID(id); // TODO: allow null values
			
			// casting is legitimate as remaining content must be of same type (PAFlexColumn extends PAFlexContainer)
			PAFlexLayoutResult layoutRes = ((PAFlexContainer) remainingContent).layout(contentWidth, contentHeight);

			PAPhysContainer resObj = layoutRes.getResult();
			resObj.setHeight(this.height); // otherwise zero
			newPage.getItems().add(layoutRes.getResult());
			
			retVal.add(newPage);
			
			remainingContent = layoutRes.getRemainingContent();
			
		}
		
		return retVal;
	}
	
	public String textContent()
	{
		// TODO
		System.err.println("PAPage.toText() not yet implemented");
		return "";
	}
}

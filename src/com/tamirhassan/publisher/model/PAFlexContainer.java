package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;



/**
 * Top level base class for page layout objects
 * 
 */
public abstract class PAFlexContainer implements PAFlexObject                                          
{	
	// FlexObject allows the use of spaces, penalties and objects such as figures (graphics?) ...
	List<PAFlexObject> content = new ArrayList<PAFlexObject>();
	int id;
	
	// moved to "PAFlexObject"
	//public abstract String textContent();
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public List<PAFlexObject> getContent() {
		return content;
	}

	public void setContent(List<PAFlexObject> content) {
		this.content = content;
	}

	public boolean isMarkWarning() {
		return markWarning;
	}

	public void setMarkWarning(boolean markWarning) {
		this.markWarning = markWarning;
	}

	public String toString()
	{
		return this.getClass().getName() + ": " + textContent();
	}
	
	public abstract PAFlexLayoutResult layout(
			float width, float height);
	
}

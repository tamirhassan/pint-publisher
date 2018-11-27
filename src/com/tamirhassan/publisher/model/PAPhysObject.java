package com.tamirhassan.publisher.model;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Class to combine PhysContainers and KPGlue
 * 
 * @author tam
 *
 */
public interface PAPhysObject
{
	public abstract String textContent();
	
	public String toString();
	
	public String tagName();
	
	public void writeToPhysDocument(Document doc, Element el);
	
}

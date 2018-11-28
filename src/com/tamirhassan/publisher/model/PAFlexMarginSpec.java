package com.tamirhassan.publisher.model;

import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tamirhassan.publisher.stylesheet.PAStylesheet;

public class PAFlexMarginSpec
{
	float leftMargin;
	float rightMargin;
	float topMargin;
	float bottomMargin;
	
	// 2018-06-30 TODO: change to columns
	PAFlexIncolObject headerLeft = null;
	PAFlexIncolObject headerCentre = null;
	PAFlexIncolObject headerRight = null;
	
	PAFlexIncolObject footerLeft = null;
	PAFlexIncolObject footerCentre = null;
	PAFlexIncolObject footerRight = null;
	
	public PAFlexMarginSpec(float left, float right, float top, float bottom)
	{
		this.leftMargin = left;
		this.rightMargin = right;
		this.topMargin = top;
		this.bottomMargin = bottom;
	}
	
	public PAFlexMarginSpec(PAFlexMarginSpec objToCopy)
	{
		this.leftMargin = objToCopy.leftMargin;
		this.rightMargin = objToCopy.rightMargin;
		this.topMargin = objToCopy.topMargin;
		this.bottomMargin = objToCopy.bottomMargin;
		
		this.headerLeft = objToCopy.headerLeft;
		this.headerCentre = objToCopy.headerCentre;
		this.headerRight = objToCopy.headerRight;
		
		this.footerLeft = objToCopy.footerLeft;
		this.footerCentre = objToCopy.footerCentre;
		this.footerRight = objToCopy.footerRight;
	}
	
	public void setAttributes(Element el, PAStylesheet stylesheet, Locale loc)
	{
		if (el.hasAttribute("left-margin")) 
        	this.setLeftMargin(Float.parseFloat(el.getAttribute("left-margin")));
        
        if (el.hasAttribute("right-margin")) 
        	this.setRightMargin(Float.parseFloat(el.getAttribute("right-margin")));
        
        if (el.hasAttribute("top-margin")) 
        	this.setTopMargin(Float.parseFloat(el.getAttribute("top-margin")));
        
        if (el.hasAttribute("bottom-margin")) 
        	this.setBottomMargin(Float.parseFloat(el.getAttribute("bottom-margin")));
        
        // look for headers and footers
        // TODO: add header centre; footers
        NodeList headerList = el.getElementsByTagName("header");
    	if (headerList.getLength() > 0)
    	{
    		Element headerEl = (Element)headerList.item(0);
    		setHeaders(headerEl, stylesheet, loc);
        }
    	NodeList footerList = el.getElementsByTagName("footer");
    	if (footerList.getLength() > 0)
    	{
    		Element footerEl = (Element)footerList.item(0);
    		setFooters(footerEl, stylesheet, loc);
        }
	}
	
	
	public void setOddAttributes(Element el, PAStylesheet stylesheet, Locale loc)
	{
		if (el.hasAttribute("left-margin-odd")) 
        	this.setLeftMargin(Float.parseFloat(el.getAttribute("left-margin-odd")));
        
        if (el.hasAttribute("right-margin-odd")) 
        	this.setRightMargin(Float.parseFloat(el.getAttribute("right-margin-odd")));
        
        if (el.hasAttribute("top-margin-odd")) 
        	this.setTopMargin(Float.parseFloat(el.getAttribute("top-margin-odd")));
        
        if (el.hasAttribute("bottom-margin-odd")) 
        	this.setBottomMargin(Float.parseFloat(el.getAttribute("bottom-margin-odd")));
        
        // look for headers and footers
        // TODO: add header centre; footers
        NodeList headerList = el.getElementsByTagName("header-odd");
    	if (headerList.getLength() > 0)
    	{
    		Element headerEl = (Element)headerList.item(0);
    		clearHeaders();
    		setHeaders(headerEl, stylesheet, loc);
        }
    	NodeList footerList = el.getElementsByTagName("footer-odd");
    	if (footerList.getLength() > 0)
    	{
    		Element footerEl = (Element)footerList.item(0);
    		clearFooters();
    		setFooters(footerEl, stylesheet, loc);
        }
	}
	
	public void setEvenAttributes(Element el, PAStylesheet stylesheet, Locale loc)
	{
		if (el.hasAttribute("left-margin-even")) 
        	this.setLeftMargin(Float.parseFloat(el.getAttribute("left-margin-even")));
        
        if (el.hasAttribute("right-margin-even")) 
        	this.setRightMargin(Float.parseFloat(el.getAttribute("right-margin-even")));
        
        if (el.hasAttribute("top-margin-even")) 
        	this.setTopMargin(Float.parseFloat(el.getAttribute("top-margin-even")));
        
        if (el.hasAttribute("bottom-margin-even")) 
        	this.setBottomMargin(Float.parseFloat(el.getAttribute("bottom-margin-even")));
        
        // look for headers and footers
        // TODO: add header centre; footers
        NodeList headerList = el.getElementsByTagName("header-even");
    	if (headerList.getLength() > 0)
    	{
    		Element headerEl = (Element)headerList.item(0);
    		clearHeaders();
    		setHeaders(headerEl, stylesheet, loc);
        }
    	NodeList footerList = el.getElementsByTagName("footer-even");
    	if (footerList.getLength() > 0)
    	{
    		Element footerEl = (Element)footerList.item(0);
    		clearFooters();
    		setFooters(footerEl, stylesheet, loc);
        }
	}
	
	public void setFirstAttributes(Element el, PAStylesheet stylesheet, Locale loc)
	{
		if (el.hasAttribute("left-margin-first")) 
        	this.setLeftMargin(Float.parseFloat(el.getAttribute("left-margin-first")));
        
        if (el.hasAttribute("right-margin-first")) 
        	this.setRightMargin(Float.parseFloat(el.getAttribute("right-margin-first")));
        
        if (el.hasAttribute("top-margin-first")) 
        	this.setTopMargin(Float.parseFloat(el.getAttribute("top-margin-first")));
        
        if (el.hasAttribute("bottom-margin-first")) 
        	this.setBottomMargin(Float.parseFloat(el.getAttribute("bottom-margin-first")));
        
        // look for headers and footers
        // TODO: add header centre; footers
        NodeList headerList = el.getElementsByTagName("header-first");
    	if (headerList.getLength() > 0)
    	{
    		Element headerEl = (Element)headerList.item(0);
    		clearHeaders();
    		setHeaders(headerEl, stylesheet, loc);
        }
    	NodeList footerList = el.getElementsByTagName("footer-first");
    	if (footerList.getLength() > 0)
    	{
    		Element footerEl = (Element)footerList.item(0);
    		clearFooters();
    		setFooters(footerEl, stylesheet, loc);
        }
	}
	
	protected void clearHeaders()
	{
		headerLeft = null;
		headerCentre = null;
		headerRight = null;
	}
	
	protected void clearFooters()
	{
		footerLeft = null;
		footerCentre = null;
		footerRight = null;
	}
	
	protected void setHeaders(Element el, PAStylesheet stylesheet, Locale loc)
	{
		NodeList subList;
		subList = el.getElementsByTagName("left");
    	if (subList.getLength() > 0)
    	{
    		Element subEl = (Element)subList.item(0);
    		
    		PAFlexParagraph newObj = new PAFlexFormattedParagraph(
    				subEl, stylesheet, loc, null);
    		this.setHeaderLeft(newObj);
    	}
    	subList = el.getElementsByTagName("centre");
    	if (subList.getLength() > 0)
    	{
    		Element subEl = (Element)subList.item(0);
    		
    		PAFlexParagraph newObj = new PAFlexFormattedParagraph(
    				subEl, stylesheet, loc, null);
    		this.setHeaderCentre(newObj);
    	}
    	subList = el.getElementsByTagName("right");
    	if (subList.getLength() > 0)
    	{
    		Element subEl = (Element)subList.item(0);
    		
    		PAFlexParagraph newObj = new PAFlexFormattedParagraph(
    				subEl, stylesheet, loc, null);
    		this.setHeaderRight(newObj);
    	}
	}
	
	protected void setFooters(Element el, PAStylesheet stylesheet, Locale loc)
	{
		NodeList subList;
		subList = el.getElementsByTagName("left");
    	if (subList.getLength() > 0)
    	{
    		Element subEl = (Element)subList.item(0);
    		
    		PAFlexParagraph newObj = new PAFlexFormattedParagraph(
    				subEl, stylesheet, loc, null);
    		this.setFooterLeft(newObj);
    	}
    	subList = el.getElementsByTagName("centre");
    	if (subList.getLength() > 0)
    	{
    		Element subEl = (Element)subList.item(0);
    		
    		PAFlexParagraph newObj = new PAFlexFormattedParagraph(
    				subEl, stylesheet, loc, null);
    		this.setFooterCentre(newObj);
    	}
    	subList = el.getElementsByTagName("right");
    	if (subList.getLength() > 0)
    	{
    		Element subEl = (Element)subList.item(0);
    		
    		PAFlexParagraph newObj = new PAFlexFormattedParagraph(
    				subEl, stylesheet, loc, null);
    		this.setFooterRight(newObj);
    	}
	}
	
	public float getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(float leftMargin) {
		this.leftMargin = leftMargin;
	}

	public float getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(float rightMargin) {
		this.rightMargin = rightMargin;
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

	public PAFlexIncolObject getHeaderLeft() {
		return headerLeft;
	}

	public void setHeaderLeft(PAFlexIncolObject headerLeft) {
		this.headerLeft = headerLeft;
	}

	public PAFlexIncolObject getHeaderCentre() {
		return headerCentre;
	}

	public void setHeaderCentre(PAFlexIncolObject headerCentre) {
		this.headerCentre = headerCentre;
	}

	public PAFlexIncolObject getHeaderRight() {
		return headerRight;
	}

	public void setHeaderRight(PAFlexIncolObject headerRight) {
		this.headerRight = headerRight;
	}

	public PAFlexIncolObject getFooterLeft() {
		return footerLeft;
	}

	public void setFooterLeft(PAFlexIncolObject footerLeft) {
		this.footerLeft = footerLeft;
	}

	public PAFlexIncolObject getFooterCentre() {
		return footerCentre;
	}

	public void setFooterCentre(PAFlexIncolObject footerCentre) {
		this.footerCentre = footerCentre;
	}

	public PAFlexIncolObject getFooterRight() {
		return footerRight;
	}

	public void setFooterRight(PAFlexIncolObject footerRight) {
		this.footerRight = footerRight;
	}
}
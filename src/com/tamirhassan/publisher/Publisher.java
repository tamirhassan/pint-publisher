/**
 * Pint Publisher - Pint Is Not TeX
 * Copyright (c) by the authors/contributors.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the names pdfXtk or PDF Extraction Toolkit; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://pdfxtk.sourceforge.net
 *
 */
package com.tamirhassan.publisher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPPenalty;
import com.tamirhassan.publisher.knuthplass.PAKPTextBlock;
import com.tamirhassan.publisher.model.PAFlexColumn;
import com.tamirhassan.publisher.model.PAFlexContainer;
import com.tamirhassan.publisher.model.PAFlexFigure;
import com.tamirhassan.publisher.model.PAFlexFloat;
import com.tamirhassan.publisher.model.PAFlexFormattedParagraph;
import com.tamirhassan.publisher.model.PAFlexHorizontalRule;
import com.tamirhassan.publisher.model.PAFlexIncolObject;
import com.tamirhassan.publisher.model.PAFlexInset;
import com.tamirhassan.publisher.model.PAFlexLayoutResult;
import com.tamirhassan.publisher.model.PAFlexMultiCol;
import com.tamirhassan.publisher.model.PAFlexObject;
import com.tamirhassan.publisher.model.PAFlexPageSpec;
import com.tamirhassan.publisher.model.PAFlexParagraph;
import com.tamirhassan.publisher.model.PAFlexSimpleParagraph;
import com.tamirhassan.publisher.model.PAFlexTable;
import com.tamirhassan.publisher.model.PAFlexSimpleTable;
import com.tamirhassan.publisher.model.PAPhysAbsPosContainer;
import com.tamirhassan.publisher.model.PAPhysBitmapGraphic;
import com.tamirhassan.publisher.model.PAPhysColumn;
import com.tamirhassan.publisher.model.PAPhysContainer;
import com.tamirhassan.publisher.model.PAPhysGraphic;
import com.tamirhassan.publisher.model.PAPhysHorizSeq;
import com.tamirhassan.publisher.model.PAPhysObject;
import com.tamirhassan.publisher.model.PAPhysPage;
import com.tamirhassan.publisher.stylesheet.PAStylesheet;

import at.ac.tuwien.dbai.pdfwrap.exceptions.DocumentProcessingException;


/**
 * Main class
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 * @author Ben Litchfield (ben@csh.rit.edu)
 */
public class Publisher
{
	// TODO: move somewhere sensible!  this is a global var, at least for GUI
	// moved to GUI 30.11.06
	//public static float XML_RESOLUTION = 150;
	
    private static final Logger LOG = Logger.getLogger( Publisher.class );

    /**
     * This is the default encoding of the text to be output.
     */
    public static final String DEFAULT_ENCODING =
        //null;
        //"ISO-8859-1";
        //"ISO-8859-6"; //arabic
        //"US-ASCII";
        "UTF-8";
        //"UTF-16";
        //"UTF-16BE";
        //"UTF-16LE";

    //private static Document resultDocument;
    
    /**
     * The stream to write the output to.
     */
    //protected static Writer output;
    
    public static final String EDIT = "-edit";
//    public static final String DIALOG = "-dialog";
    
    /**
     * Infamous main method.
     *
     * @param args Command line arguments, should be one and a reference to a file.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public static void main(String[] args) throws Exception
    {
        String inFile = null;
        String outFile = null;
        int currentArgumentIndex = 0;
        boolean editMode = false;

        for( int i=0; i<args.length; i++ )
        {
            /*
            else if( args[i].equals( START_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                startPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( END_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                endPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( CONSOLE ) )
            {
                toConsole = true;
            }
            else */
           
            if( args[i].equals( EDIT ))
            {
            	editMode = true;
            }
            else
            {
                if( inFile == null )
                {
                    inFile = args[i];
                }
                else
                {
                    outFile = args[i];
                }
            }
        }

        if( inFile == null )
        {
            usage();
        }

        if( outFile == null && inFile.length() > 4 )
        {
        	// TODO: later outfile will no longer be given an extension here
        	// (as it will have several extensions)
            outFile = inFile.substring( 0, inFile.length() - 4 ) + ".pdf";
        }
        
        if (editMode)
        {
        	System.out.println("in edit mode");
        	// editing existing file from existing formatted document
        	// (.flex.xml and .phys.xml files)
        	// .pdf will be overwritten if present
        	
        	// generate input filenames
        	String flexFilename = inFile + ".flex.xml";
            String physFilename = inFile + ".phys.xml";
        	File flexFile = new File(flexFilename);
        	File physFile = new File(physFilename);
            
        	PDDocument document = new PDDocument();
            List<PAFlexPageSpec> flexDoc = processInput(flexFile, document, null);
            		
            List<PAPhysPage> pages = processFormattedDocument(flexDoc, document, physFile);
            
            // assume frame order in order written to file (for now)
            List<PAPhysColumn> frames = obtainFrameOrder(pages);
            
//          List<PAPhysColumn> contentFrames = new ArrayList<PAPhysColumn>();
//          List<PAPhysColumn> floatFrames = new ArrayList<PAPhysColumn>();
            
            // sort frame list into articles/flows
            HashMap<Integer, List<PAPhysColumn>> articleMap =
            		new HashMap<Integer, List<PAPhysColumn>>();
            
            for (PAPhysColumn frame : frames)
            {
            	if (articleMap.containsKey(frame.getFlexID()))
            	{
            		articleMap.get(frame.getFlexID()).add(frame);
            	}
            	else
            	{
            		List<PAPhysColumn> frameList = new ArrayList<PAPhysColumn>();
            		frameList.add(frame);
            		articleMap.put(frame.getFlexID(), frameList);
            	}
            }
            
            // TODO: obtainContentSequence takes a flexID
            // run for all flows
            
            for (int articleID : articleMap.keySet())
            {
            	List<PAFlexObject> content = 
            			obtainContentSequence(flexDoc, articleID);
            	List<PAPhysColumn> frameList = articleMap.get(articleID);
            	
            	layoutTextIntoColumns(frameList, content);
            }
            
            // TODO: mark stuff in red that doesn't fit!
            
         	// output the phys document
            writePhysDocument(pages, physFile);
            
            // TODO: check that conditions are met; otherwise return warnings
            
            // now create the PDF
            //PDDocument document = new PDDocument();
            int pageIndex = 1;
    		for (PAPhysPage page : pages)
    		{
    			page.render(document);
    			pageIndex ++;
    		}
    		System.out.println("finished render, pageIndex is now: " + pageIndex);
    		
    		System.out.println("Saving PDF to: " + outFile);
    		// Save the results and ensure that the document is properly closed:
    		document.save( outFile );
    		document.close();
        }
        else
        {
        	// creating new file
        	
        	String flexFilename = inFile.substring( 0, inFile.length() - 4 ) + ".flex.xml";
            String physFilename = inFile.substring( 0, inFile.length() - 4 ) + ".phys.xml";
            
//    		System.err.println("Processing: " + inFile);
    		
            // load the input file
            File inputFile = new File(inFile);
            File flexFile = new File(flexFilename);
            File physFile = new File(physFilename);
            
            PDDocument document = new PDDocument();
            
            List<PAFlexPageSpec> flexDoc = processInput(inputFile, document, flexFile);
            
            // now lay out the document
            List<PAPhysPage> pages = new ArrayList<PAPhysPage>();
            for (PAFlexPageSpec pageSpec : flexDoc)
            {
            	pages.addAll(pageSpec.layout());
            }
            
            // output the phys document
            writePhysDocument(pages, physFile);
            
            // TODO: check that conditions are met; otherwise return warnings
            
            // now create the PDF
            //PDDocument document = new PDDocument();
            int pageIndex = 1;
    		for (PAPhysPage page : pages)
    		{
    			page.render(document);
    			pageIndex ++;
    		}
    		System.out.println("finished render, pageIndex is now: " + pageIndex);
    		
    		
    		// TODO: embed the physical and flex files here
    		// Save the results and ensure that the document is properly closed:
    		document.save( outFile );
    		document.close();
            
        }
        
    }
    
    protected static List<PAFlexObject> recObtainContentSequence
    		(PAFlexContainer c, int flexId, int prevID)
    {
    	List<PAFlexObject> retVal = new ArrayList<PAFlexObject>();
    	
    	// cannot always check against c.getID()
    	// as sometimes c.getID() == 0 (as for e.g. page)
    	// and we need to go one level up
    	int closestValidID = c.getID();
    	if (c.getID() <= 0)
    		closestValidID = prevID;
    	
    	// c.getContent() is empty for PAFlexFigure
    	if (c instanceof PAFlexFigure)
    	{
    		if (closestValidID == flexId)
    		{
    			retVal.add(c);
    		}
    	}
    	else
    	{
	    	for (PAFlexObject o : c.getContent())
	    	{
	    		if (o instanceof PAFlexContainer) 
	    		{
	    			// recurse 
	    			retVal.addAll(recObtainContentSequence
	    					((PAFlexContainer)o, flexId, closestValidID));
	    		}
	    		else
	    		{
	    			// PAFlexIncolObject, KPGlue or KPPenalty
	    			
	    			// Paragraph (not a PAFlexContainer), Figure (a container), Graphic (phys), etc. 
	    			if (closestValidID == flexId)
	    				retVal.add(o);
	    		}
	    	}
    	}
    	
    	return retVal;
    }
    
    protected static List<PAFlexObject> obtainContentSequence
    		(List<PAFlexPageSpec> flexDoc, int flexId)
    {
    	List<PAFlexObject> retVal = new ArrayList<PAFlexObject>();
    	
    	for (PAFlexPageSpec ps : flexDoc)
    	{
    		// content is a single PAFlexColumn
    		retVal.addAll(recObtainContentSequence(ps.getContent(), flexId, ps.getID())); 
    	}
    	
    	return retVal;
    }
    
    
    protected static List<List<PAPhysColumn>> recObtainFrameOrders(PAPhysContainer c)
    {
    	List<List<PAPhysColumn>> retVal = new ArrayList<List<PAPhysColumn>>();
    	
    	// if horiz-seq
    	
    	return retVal;
    }
    
    protected static List<List<PAPhysColumn>> obtainFrameOrders(List<PAPhysPage> pages)
    {
    	List<List<PAPhysColumn>> retVal = new ArrayList<List<PAPhysColumn>>();
    	
    	for (PAPhysPage page : pages)
    	{
    		System.out.println("new page");
    		for (PAPhysContainer c : page.getItems())
    		{
    			retVal.addAll(recObtainFrameOrder(c));
    		}
    	}
    	
    	return retVal;
    }
    
    protected static List<PAPhysColumn> recObtainFrameOrder(PAPhysContainer c)
    {
    	List<PAPhysColumn> retVal = new ArrayList<PAPhysColumn>();
    	
    	if (c instanceof PAPhysColumn)
    	{
    		// new alg
    		// if it has a flexID, add it
    		// otherwise recurse
    		
    		PAPhysColumn col = (PAPhysColumn)c;
    		
    		if (c.getFlexID() > 0 || c.getFlexID() == -1)
    		{
    			System.out.println("adding PAPhysColumn");
    			retVal.add(col);
    		}
//    		2018-08-12 "else" removed
//    		else
    		{
    			for (PAPhysObject o : col.getItems())
        		{
    				if (o instanceof PAPhysContainer)
    					retVal.addAll(recObtainFrameOrder((PAPhysContainer)o));
        		}
    		}
    		
    		/*
    		PAPhysColumn col = (PAPhysColumn)c;
    		retVal.add(col);
    		for (PAPhysObject o : col.getItems())
    		{
    			if (o instanceof PAPhysContainer)
    				retVal.addAll(recObtainFrameOrder((PAPhysContainer)o));
    		}
    		*/
    	}
    	else if (c instanceof PAPhysHorizSeq)
    	{
    		// new alg
    		// always recurse
    		
    		PAPhysHorizSeq hs = (PAPhysHorizSeq)c;
    		for (PAPhysObject o : hs.getItems())
    		{
    			if (o instanceof PAPhysContainer)
    				retVal.addAll(recObtainFrameOrder((PAPhysContainer)o));
    		}
    		
    		/*
    		PAPhysHorizSeq hs = (PAPhysHorizSeq)c;
    		for (PAPhysObject o : hs.getItems())
    		{
    			if (o instanceof PAPhysContainer)
    				retVal.addAll(recObtainFrameOrder((PAPhysContainer)o));
    		}
    		*/
    	}
    	
    	return retVal;
    }
    
    protected static List<PAPhysColumn> obtainFrameOrder(List<PAPhysPage> pages)
    {
    	List<PAPhysColumn> retVal = new ArrayList<PAPhysColumn>();
    	
    	for (PAPhysPage page : pages)
    	{
    		System.out.println("new page");
    		for (PAPhysContainer c : page.getItems())
    		{
    			retVal.addAll(recObtainFrameOrder(c));
    		}
    	}
    	return retVal;
    }
    
    protected static int layoutFloatIntoFrame(PAPhysColumn frame, PAFlexFloat flt)
    {
    	PAFlexLayoutResult res = flt.layout(frame.getWidth(), frame.getHeight());
    	
    	PAPhysColumn resultCol = (PAPhysColumn) res.getResult();
    	
    	frame.setItems(resultCol.getItems());
    	
    	return res.getExitStatus();
    }
    
    // TODO: return a list of floats and pages
    protected static void layoutTextIntoColumns(List<PAPhysColumn> contentFrames, 
	    		List<PAFlexObject> content)
	    {
    		if (content.size() == 1 && content.get(0) instanceof PAFlexFloat)
    		{
    			layoutFloatIntoFrame(contentFrames.get(0), (PAFlexFloat) content.get(0));
    			return;
    		}
    	
	    	List<PAPhysColumn> unusedContentFrames = new ArrayList<PAPhysColumn>();
	    	for (PAPhysColumn c : contentFrames)
	    		unusedContentFrames.add(c);
	    	
	    	List<PAFlexObject> remainingContent = new ArrayList<PAFlexObject>();
	    	for (PAFlexObject o : content)
	    		remainingContent.add(o);
	    	
	    	List<PAFlexFloat> floats = new ArrayList<PAFlexFloat>();
	    	
	    	while(unusedContentFrames.size() > 0 && remainingContent.size() > 0)
	    	{
	    		PAPhysColumn col = unusedContentFrames.remove(0);
	    		
	    		// this method creates a FlexColumn and lays it out anew
	    		PAFlexLayoutResult res = col.layoutColumn(remainingContent);
	    		
	    		if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_SUCCESS ||
	    				res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
	    		{
	    			PAPhysColumn resultCol = (PAPhysColumn)res.getResult();
	    			col.setItems(resultCol.getItems());
	    			col.setHeight(resultCol.getHeight());
	    			col.setWidth(resultCol.getWidth());
	    			col.setDemerits(resultCol.getDemerits());
	    			
	    			floats.addAll(res.getFloats());
	    			
	    			// reobtains the items
	    			if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
	    				remainingContent = ((PAFlexColumn)res.getRemainingContent()).getContent();
	    			else
	    				remainingContent.clear();
	    		}
	    	}
	    	
	    	if (unusedContentFrames.size() > 0)
	    		System.out.println("more frames than required for content; should be removed");
	    	
	    	if (remainingContent.size() > 0)
	    		System.out.println("content truncated; more frames need to be added according to rules");
	    	
	    	// check and lay out floats!
	    	// TODO: deal with float edits (change height of dock)!
	    	
	    	/*
	    	List<PAPhysColumn> unusedFloatFrames = new ArrayList<PAPhysColumn>();
	    	for (PAPhysColumn c : floatFrames)
	    		unusedFloatFrames.add(c);
	    	
	    	while(unusedFloatFrames.size() > 0 && floats.size() > 0)
	    	{
	    		PAPhysColumn frame = unusedFloatFrames.remove(0);
	    		
	    		PAFlexFloat ffloat = floats.remove(0);
	    		
	//    		this does not work - "content" is unused in figures
	//    		PAFlexLayoutResult res = frame.layoutColumn(ffloat.getContent());
	    		PAFlexLayoutResult res = ffloat.layout(frame.getWidth(), frame.getHeight());
	    		
	    		
	    		if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_SUCCESS ||
	    				res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
	    		{
	    			PAPhysColumn resultCol = (PAPhysColumn)res.getResult();
	    			frame.setItems(resultCol.getItems());
	    			frame.setHeight(resultCol.getHeight());
	    			frame.setWidth(resultCol.getWidth());
	    			frame.setDemerits(resultCol.getDemerits());
	    		}
	    		else
	    		{
	    			System.out.println("Float did not fit into space");
	    		}
	    		if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
	    		{
	    			System.out.println("Float did not fit completely into space");
	    		}
	    	}
	    	
	    	if (unusedFloatFrames.size() > 0)
	    		System.out.println("more frames than required for floats; should be removed");
	    	
	    	if (floats.size() > 0)
	    		System.out.println("floats truncated; more frames need to be added according to rules");
	    	*/
	    }

	protected static void layoutTextIntoColumnsOld(List<PAPhysColumn> contentFrames, 
    		List<PAPhysColumn> floatFrames, List<PAFlexObject> content)
    {
    	List<PAPhysColumn> unusedContentFrames = new ArrayList<PAPhysColumn>();
    	for (PAPhysColumn c : contentFrames)
    		unusedContentFrames.add(c);
    	
    	List<PAFlexObject> remainingContent = new ArrayList<PAFlexObject>();
    	for (PAFlexObject o : content)
    		remainingContent.add(o);
    	
    	List<PAFlexFloat> floats = new ArrayList<PAFlexFloat>();
    	
    	while(unusedContentFrames.size() > 0 && remainingContent.size() > 0)
    	{
    		PAPhysColumn col = unusedContentFrames.remove(0);
    		
    		// this method creates a FlexColumn and lays it out anew
    		PAFlexLayoutResult res = col.layoutColumn(remainingContent);
    		
    		if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_SUCCESS ||
    				res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
    		{
    			PAPhysColumn resultCol = (PAPhysColumn)res.getResult();
    			col.setItems(resultCol.getItems());
    			col.setHeight(resultCol.getHeight());
    			col.setWidth(resultCol.getWidth());
    			col.setDemerits(resultCol.getDemerits());
    			
    			floats.addAll(res.getFloats());
    			
    			// reobtains the items
    			if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
    				remainingContent = ((PAFlexColumn)res.getRemainingContent()).getContent();
    			else
    				remainingContent.clear();
    		}
    	}
    	
    	if (unusedContentFrames.size() > 0)
    		System.out.println("more frames than required for content; should be removed");
    	
    	if (remainingContent.size() > 0)
    		System.out.println("content truncated; more frames need to be added according to rules");
    	
    	// check and lay out floats!
    	// TODO: deal with float edits (change height of dock)!
    	
    	List<PAPhysColumn> unusedFloatFrames = new ArrayList<PAPhysColumn>();
    	for (PAPhysColumn c : floatFrames)
    		unusedFloatFrames.add(c);
    	
    	while(unusedFloatFrames.size() > 0 && floats.size() > 0)
    	{
    		PAPhysColumn frame = unusedFloatFrames.remove(0);
    		
    		PAFlexFloat ffloat = floats.remove(0);
    		
//    		this does not work - "content" is unused in figures
//    		PAFlexLayoutResult res = frame.layoutColumn(ffloat.getContent());
    		PAFlexLayoutResult res = ffloat.layout(frame.getWidth(), frame.getHeight());
    		
    		
    		if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_SUCCESS ||
    				res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
    		{
    			PAPhysColumn resultCol = (PAPhysColumn)res.getResult();
    			frame.setItems(resultCol.getItems());
    			frame.setHeight(resultCol.getHeight());
    			frame.setWidth(resultCol.getWidth());
    			frame.setDemerits(resultCol.getDemerits());
    		}
    		else
    		{
    			System.out.println("Float did not fit into space");
    		}
    		if (res.getExitStatus() == PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
    		{
    			System.out.println("Float did not fit completely into space");
    		}
    	}
    	
    	if (unusedFloatFrames.size() > 0)
    		System.out.println("more frames than required for floats; should be removed");
    	
    	if (floats.size() > 0)
    		System.out.println("floats truncated; more frames need to be added according to rules");
    }
    
    protected static void recProcessFormattedDocument(PDDocument doc, Element el, 
    		List itemList)
    {
    	NodeList nl = el.getChildNodes();
    	
    	for (int i = 0; i < nl.getLength(); i ++) 
        {
    		if (nl.item(i) instanceof Element)
    		{
    			Element childEl = (Element) nl.item(i);
        		
        		if (childEl.getTagName().equals("phys-col"))
        		{
        			PAPhysColumn newObj = new PAPhysColumn();
    				
    				newObj.setWidth(Float.parseFloat(childEl.getAttribute("width")));
        			newObj.setHeight(Float.parseFloat(childEl.getAttribute("height")));
        			
        			if (childEl.hasAttribute("flex-id"))
        			{
        				newObj.setFlexID(Integer.parseInt(childEl.getAttribute("flex-id")));
        			}
        			
        			recProcessFormattedDocument(doc, childEl, newObj.getItems());
        			itemList.add(newObj);
        		}
        		else if (childEl.getTagName().equals("horiz-seq"))
        		{
        			PAPhysHorizSeq newObj = new PAPhysHorizSeq();
        			
        			if (childEl.hasAttribute("flex-id"))
        				newObj.setFlexID(Integer.parseInt(childEl.getAttribute("flex-id")));
    				
    				newObj.setWidth(Float.parseFloat(childEl.getAttribute("width")));
        			newObj.setHeight(Float.parseFloat(childEl.getAttribute("height")));
        			
        			recProcessFormattedDocument(doc, childEl, newObj.getItems());
        			itemList.add(newObj);
        		}
        		else if (childEl.getTagName().equals("text-block"))
        		{
        			// do nothing for now
        		}
        		else if (childEl.getTagName().equals("graphic"))
        		{
        			// do nothing for now
    			}
    			else if (childEl.getTagName().equals("kp-glue"))
    			{
    				float amount = Float.parseFloat(childEl.getAttribute("amount"));
    				
    				float adjRatio = 0.0f;
    				if (childEl.hasAttribute("adj-ratio"))
    					adjRatio = Float.parseFloat(childEl.getAttribute("adj-ratio"));
    				
    				KPGlue newObj = new KPGlue(amount);
    				newObj.setAdjRatio(adjRatio);
    				itemList.add(newObj);
    			}
    			
    		}
    		
        }
    }
    
    public static List<PAPhysPage> processFormattedDocument(List<PAFlexPageSpec> flexDoc, 
    		PDDocument pdDoc, File physFile)
    {
    	List<PAPhysPage> retVal = new ArrayList<PAPhysPage>();
    	
    	try
    	{
    		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
	    	        .newInstance();
	    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	Document document = documentBuilder.parse(physFile);
	    	
	    	Element docEl = (Element) document.getElementsByTagName("phys-doc").item(0);
	    	
	    	NodeList nl = document.getElementsByTagName("page");
	    	
	    	for (int i = 0; i < nl.getLength(); i ++) 
            {
	    		Element pageEl = (Element) nl.item(i);
	    		float height = Float.parseFloat(pageEl.getAttribute("height"));
	    		float width = Float.parseFloat(pageEl.getAttribute("width"));
	    		float leftMargin = Float.parseFloat(pageEl.getAttribute("left-margin"));
	    		float rightMargin = Float.parseFloat(pageEl.getAttribute("right-margin"));
	    		float topMargin = Float.parseFloat(pageEl.getAttribute("top-margin"));
	    		float bottomMargin = Float.parseFloat(pageEl.getAttribute("bottom-margin"));
	    		
	    		
	    		
	    		PAPhysPage page = new PAPhysPage(width, height, leftMargin, rightMargin, 
	    				topMargin, bottomMargin);
	    		
	    		if (pageEl.hasAttribute("flex-id"))
    				page.setFlexID(Integer.parseInt(pageEl.getAttribute("flex-id")));
	    		
	    		retVal.add(page);
	    		
	    		// TODO: Sort out type issues!
	    		recProcessFormattedDocument(pdDoc, pageEl, page.getItems());
	    		
            }
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return retVal;
    }
    
    
    // TODO: change to PAFlexDocument - list of PAPageSpecs
    /**
     * 
     * @param inputFile
     * @param doc - new PDDocument, for processing stylesheet
     * @param outFile - the file with added flexIDs (if not null)
     * @return
     */
    public static List<PAFlexPageSpec> processInput(File inputFile, PDDocument doc, File outFile)
    {
    	List<PAFlexPageSpec> retVal = new ArrayList<PAFlexPageSpec>();
    	
    	// below example from:
    	// http://stackoverflow.com/questions/7704827/java-reading-xml-file
    	
    	/*
    	File file = new File("userdata.xml");
    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
    	        .newInstance();
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document document = documentBuilder.parse(file);
    	String usr = document.getElementsByTagName("user").item(0).getTextContent();
    	String pwd = document.getElementsByTagName("password").item(0).getTextContent();
    	*/
    	
    	try 
    	{
    		// go through stories (content tags)
    		// go through styles (character, block, spacing)
    		// go through pages, styling and inserting stories as appropriate
    		// TODO: stylesheet groupings, views
    		
	    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
	    	        .newInstance();
	    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	Document document = documentBuilder.parse(inputFile);
	    	
	    	// TODO: if less or more than one doc-spec element catch this!
	    	Element docEl = (Element) document.getElementsByTagName("doc-spec").item(0);
	    	
	    	// document language - default to EN
	    	String docLang = "en";
	    	if (docEl.hasAttribute("lang")) 
	    		docLang = docEl.getAttribute("lang");
	    	
	    	// go through stories first
	    	HashMap<String, NodeList> storyMap = new HashMap<String, NodeList>();
	    	NodeList nl = docEl.getElementsByTagName("content");
	    	
	    	for (int i = 0; i < nl.getLength(); i ++) 
            {
	    		Element el = (Element) nl.item(i);
	    		
	    		String storyName = "default";
	    		
	    		if (el.hasAttribute("name"))
	    			storyName = el.getAttribute("name");
	    		
	    		if (storyMap.containsKey("name"))
	    		{
	    			// TODO: warn that key is already assigned!
	    		}
	    		else
	    		{
	    			storyMap.put(storyName, el.getChildNodes());
	    		}
            }
	    	
	    	// TODO: stylesheet object - prob. unnecessary, but think about it!
	    	// Just add all the elements to a NodeList and pass it on to processPageContent
	    	// Query it, e.g. using JQuery
	    	// Order for a PAFlexParagraph might look like: <p>, <emph>, <foo>
	    	// apply for each tag in order
	    	// just need a method to read all _character_ attributes and apply them (which can also be in the block def)
	    	// [separate method to read all block attributes, called only at beginning; TBD later (indentations, etc.)]
	    	
	    	// TODO: includes the content too!
            NodeList styles = docEl.getChildNodes();
            //PAStylesheet stylesheet = new PAStylesheet(styles);
            PAStylesheet stylesheet = new PAStylesheet();
            stylesheet.addDefaultStyles();
            stylesheet.addStyles(styles);
            stylesheet.loadFonts(doc);
            
	    	// generateBoxGlueItems is where all the magic happens (char level processing)
	    	// need to move from iteration to recursion through content
	    	// creation of PAWord object will involve iterating through path tags
	    	// throws error if tag not in stylesheet
	    	
	    	// get page specifications and add stories
	    	// later deal with page specifications nested in stylesheets
	    	nl = document.getElementsByTagName("page");
	    	
            for (int i = 0; i < nl.getLength(); i++) 
            {
            	Element el = (Element) nl.item(i);
    	    	
            	String pageLang = docLang;
                if (el.hasAttribute("lang")) 
                	pageLang = el.getAttribute("lang");
                
                // get locale object
                Locale loc = Locale.forLanguageTag(pageLang);
	            
                PAFlexPageSpec pageSpec = new PAFlexPageSpec(el, stylesheet, loc);
                
                if (el.hasAttribute("id"))
                {
                	int pageSpecID = Integer.parseInt(el.getAttribute("id"));
                	pageSpec.setID(pageSpecID);
                }
                else
                {
                	int pageSpecID = generateID();
                	// TODO: is the following line necessary? Is this rewritten?
                	el.setAttribute("id", String.valueOf(pageSpecID));
                	pageSpec.setID(pageSpecID);
                }
                
                // call recursive method to add either structuring or content elements
	            pageSpec.getContent().getContent().addAll(
	            		recProcessPageContent(el.getChildNodes(), stylesheet, doc, loc));
	            
	            // NB: currently the content is a PAFlexColumn. Need not be! PAFlexColumn -> mainContent
	            //processPageContent(pageSpec, el.getChildNodes());
	            
	            // add the one page spec to result
				retVal.add(pageSpec);
				
				if (outFile != null)
				{
					// write the content into xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(document);
					StreamResult result = new StreamResult(outFile);
	
					// Output to console for testing
					// StreamResult result = new StreamResult(System.out);
	
					transformer.transform(source, result);
	
					System.out.println("File saved!");
				}
            }
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return retVal;
    }
    
    
    protected static NodeList writePhysDocument(List<PAPhysPage> physDoc, File outFile)
    {
    // based on http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
    	
        try 
        {
        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    		// root elements
    		Document doc = docBuilder.newDocument();
    		Element rootElement = doc.createElement("phys-doc");
    		doc.appendChild(rootElement);
    		
    		for (PAPhysPage ps : physDoc)
    		{
    			Element pageElement = doc.createElement("page");
    			// set coords, etc.
    			pageElement.setAttribute("width", String.valueOf(ps.getWidth()));
    			pageElement.setAttribute("height", String.valueOf(ps.getHeight()));
    			pageElement.setAttribute("left-margin", String.valueOf(ps.getLeftMargin()));
    			pageElement.setAttribute("right-margin", String.valueOf(ps.getRightMargin()));
    			pageElement.setAttribute("top-margin", String.valueOf(ps.getTopMargin()));
    			pageElement.setAttribute("bottom-margin", String.valueOf(ps.getBottomMargin()));
    			
//    			PAPhysColumn pageCol = (PAPhysColumn) ps.getItems();
    			
        		// 2018-07-01 start with absolutely positioned elements
        		// e.g. header and footer
        		for (PAPhysAbsPosContainer absc : ps.getAbsItems())
        		{
        			Element childEl = doc.createElement("abs-pos");
        			
        			childEl.setAttribute("x1", String.valueOf(absc.getX1()));
        			childEl.setAttribute("y2", String.valueOf(absc.getY2()));
        			
        			if (absc.getFlexID() != 0) // 0 is default value
                		childEl.setAttribute("flex-id", String.valueOf(absc.getFlexID()));
        			
        			for (PAPhysObject cc : absc.getItems())
        				cc.writeToPhysDocument(doc, childEl);
        			
        			pageElement.appendChild(childEl);
        		}
    			
    			// do not write page column as extra tag - just confuses matters
    			//recWritePhysDocument(doc, pageElement, pageCol);
        		
        		for (PAPhysObject cc : ps.getItems())
        			cc.writeToPhysDocument(doc, pageElement); 
        		
    			rootElement.appendChild(pageElement);
    		}
    		
    		// write the content into xml file
    		System.out.println("Writing phys file: " + outFile.getPath());
    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
    		Transformer transformer = transformerFactory.newTransformer();
    		
    		// from here: https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
    		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    		
    		DOMSource source = new DOMSource(doc);
    		StreamResult result = new StreamResult(outFile);
    		
    		// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    	return null;
    }
    
    /*
    protected static void recWritePhysDocument(Document doc, Element el, PAPhysObject o)
    {
    	// TODO: sort casting mess with object/container!!
    	// move height/width to new class
    	
    	Element childEl = null;
    	
    	System.out.println("writing object o: " + o);
    	
    	if (o instanceof KPGlue)
    	{
    		KPGlue g = (KPGlue)o;
    		childEl = doc.createElement("kp-glue");
    		childEl.setAttribute("amount", String.valueOf(g.getAmount()));
    		childEl.setAttribute("adj-ratio", String.valueOf(g.getAdjRatio()));
    	}
    	else // o instanceof PAPhysContainer
    	{
    		PAPhysContainer c = (PAPhysContainer)o;
    		
    		if (c instanceof PAPhysColumn)
        	{
        		childEl = doc.createElement("phys-col");
        		for (PAPhysObject cc : ((PAPhysColumn) c).getItems())
        			recWritePhysDocument(doc, childEl, cc);
        	}
        	else if (c instanceof PAPhysHorizSeq)
        	{
        		childEl = doc.createElement("horiz-seq");
        		for (PAPhysObject cc : ((PAPhysHorizSeq) c).getItems())
        			recWritePhysDocument(doc, childEl, cc);
        	}
        	else if (c instanceof PAPhysTextBlock)
        	{
        		childEl = doc.createElement("text-block");    
        		PAPhysTextBlock tb = (PAPhysTextBlock)c;
        		
        		childEl.setTextContent(tb.toString());
        	}
        	else if (c instanceof PAPhysGraphic)
        	{
        		childEl = doc.createElement("graphic");
        	}
        	else if (c instanceof PAPhysHorizontalRule)
    		{
        		childEl = doc.createElement("hr");
    		}
        	else if (c instanceof PAPhysTabular)
    		{
        		childEl = doc.createElement("tabular");
    		}
    		
        	childEl.setAttribute("width", String.valueOf(c.getWidth()));
        	
        	if (!(c instanceof PAPhysHorizontalRule))
        	{
        		// TODO: currently, <hr> does not have any height. Change later!
        		childEl.setAttribute("height", String.valueOf(c.getHeight()));
        	}
        	
        	// TODO: exception handling here - if writing an object fails,
        	//       continue with rest of physical structure
        	
        	if (c.getFlexID() != 0) // 0 is default value
        	{
        		childEl.setAttribute("flex-id", String.valueOf(c.getFlexID()));
        	}
    	}
    	
    	el.appendChild(childEl);
    }
    */
    
    protected static int generateID()
    {
    	// TODO: keep track to avoid collisions!
    	// TODO: move to util method
    	return randomWithRange(0, 16777215); // Integer.MAX_VALUE returns negatives!
    }
    
    // from https://stackoverflow.com/questions/7961788/math-random-explained#7961881
    protected static int randomWithRange(int min, int max)
    {
       int range = (max - min) + 1;     
       return (int)(Math.random() * range) + min;
    }
    
    protected static boolean isHeading(Element el)
    {
    	return (el.getTagName().charAt(0) == 'h' && !el.getTagName().startsWith("header"));
    }
    
    protected static boolean isTextBlock(Element el)
    {
    	return (el.getTagName().equals("p") || isHeading(el));
    }
    
    protected static boolean isTextElement(Element el)
    {
    	return (isTextBlock(el) || el.getTagName().equals("tt"));
    }
    
    protected static List<PAFlexObject> recProcessPageContent(NodeList nl, PAStylesheet stylesheet, PDDocument doc, Locale loc)
    {
    	ArrayList<PAFlexObject> retVal = new ArrayList<PAFlexObject>();
    	
    	Element prevElement = null;
    	
    	for (int i = 0; i < nl.getLength(); i++) 
        {
//    		if (i % 10 == 0)
//    			System.out.print("Processing item " + i + " of " + nl.getLength() + ": ");
    		
    		if (nl.item(i) instanceof Element)
    		{
	        	Element el = (Element) nl.item(i);
//	        	System.out.println(el);
	        	
	        	int elID;
	        	if (el.hasAttribute("id"))
	        	{
	        		elID = Integer.parseInt(el.getAttribute("id"));
	        	}
	        	else
	        	{
		        	elID = generateID();
		        	el.setAttribute("id", String.valueOf(elID));
	        	}
	        	
	        	// TODO: set ID of all other elements, not just <fig>
	        	// decide whether to add to constructor or not
	        	
	        	// check for change of language
	        	Locale thisLoc = loc;
	        	if (el.hasAttribute("lang"))
	        		thisLoc = Locale.forLanguageTag(el.getAttribute("lang"));
	        	
	        	// insert respective vertical gap
	        	if (prevElement != null)// && nl.item(i - 1) instanceof Object)
	        	{
	        		float interBlockSpacing = stylesheet.interblockSpacing
	        				(prevElement.getTagName(), el.getTagName());
	        		
	        		// add penalty if prevElement == hx
	        		// (prevElement is not set for non-visible inline elements such as fig)
	        		
	        		if (isTextBlock(el))
	        		{
	        			if (prevElement != null && prevElement.getTagName().charAt(0) == 'h')
	        			{
	        				retVal.add(new KPPenalty(10000));
	        			}
	        		}
	        		
	        		if (interBlockSpacing > 0)
	        		{
	        			retVal.add(new KPGlue(interBlockSpacing));
	        			
	        			// add penalty before and after
	        			// TODO: rules not same as for setting paragraphs
	        			// according to K-P, enough to add penalty before glue to inhibit breaking 
	        			if (isTextBlock(el))
		        		{
		        			if (prevElement != null && isHeading(prevElement))
		        			{
		        				retVal.add(new KPPenalty(10000));
		        			}
		        		}
	        		}
	        	}
	        	
	        	// if <multicol> etc. (layout level), recurse#
	        	if (el.getTagName().equals("vspace"))
	        	{
	        		retVal.add(new KPGlue(Float.parseFloat(el.getAttribute("amount"))));
	        	}
	        	else if (el.getTagName().equals("col"))
	        	{
	        		PAFlexColumn newObj =
	        				new PAFlexColumn(recProcessPageContent(el.getChildNodes(), stylesheet, doc, thisLoc));
	        		newObj.setID(elID);
	        				
	        		retVal.add(newObj);
	        	}
	        	else if (el.getTagName().equals("multi-col"))
	        	{
	        		// TODO: throw exception if given invalid value
	        		int numCols = Integer.parseInt(el.getAttribute("num-cols"));
	        		// TODO: flex col and flex multicol do not have consistent constructors
	        		PAFlexMultiCol mc = new PAFlexMultiCol(numCols, PAFlexMultiCol.MCOL_EQUAL_WIDTH);
	        		mc.getContent().addAll(recProcessPageContent(el.getChildNodes(), stylesheet, doc, thisLoc));
	        		mc.setGutterWidth(stylesheet.gutterWidth());
	        		mc.setID(elID);
	        		
	        		retVal.add(mc);
	        	}
	        	// if <p>, <h1>, etc. (block level), add all children and don't recurse
	        	// TODO: replace with method to lookup from stylesheet
	        	else if (isTextElement(el))
	        	{
	        		// pass prevElement to enable first-line indents on second para, etc.
	        		PAFlexParagraph newObj = new PAFlexFormattedParagraph(
	        				el, stylesheet, thisLoc, prevElement);
	        		newObj.setID(elID);
	        		retVal.add(newObj);
	        	}
	        	else if (el.getTagName().equals("hr"))
	        	{
	        		retVal.add(new PAFlexHorizontalRule());
	        	}
	        	else if (el.getTagName().equals("table"))
	        	{
	        		// simple table has a paragraph object for each cell (<td>)
	        		
	        		PAFlexSimpleTable tab = new PAFlexSimpleTable();
	        		tab.setID(elID);
	        		if (el.hasAttribute("col-gap"))
	        			tab.setColGap(Float.parseFloat(el.getAttribute("col-gap")));
	        		if (el.hasAttribute("row-gap"))
	        			tab.setRowGap(Float.parseFloat(el.getAttribute("row-gap")));
	        		
	        		NodeList rows = el.getElementsByTagName("tr");
	        		
	        		for (int j = 0; j < rows.getLength(); j ++)
	        		{
	        			Element rowEl = (Element)rows.item(j);
	        			NodeList cols = rowEl.getElementsByTagName("td");
	        			
	        			List<PAFlexIncolObject> thisRow = 
	        					new ArrayList<PAFlexIncolObject>();
	        			
	        			for (int k = 0; k < cols.getLength(); k ++)
		        		{
	        				Element colEl = (Element)cols.item(k);
	        				
	        				PAFlexParagraph newObj = new PAFlexFormattedParagraph(
	    	        				colEl, stylesheet, thisLoc, prevElement);
	    	        		thisRow.add(newObj);
	    	        		
	    	        		// make sure each col position has a width of -1
	    	        		if (tab.getColWidths().size() < k + 1)
	    	        			tab.getColWidths().add(-1.0f);
		        		}
	        			tab.getRows().add(thisRow);
	        		}
	        		retVal.add(tab);
	        	}
	        	else if (el.getTagName().equals("str-table"))
	        	{
	        		// str-table has a column object for each cell
	        		
	        		PAFlexTable tab = new PAFlexTable();
	        		tab.setID(elID);
	        		if (el.hasAttribute("col-gap"))
	        			tab.setColGap(Float.parseFloat(el.getAttribute("col-gap")));
	        		if (el.hasAttribute("row-gap"))
	        			tab.setRowGap(Float.parseFloat(el.getAttribute("row-gap")));
	        		if (el.hasAttribute("alignment"))
	        		{
	        			String val = el.getAttribute("alignment");
	        			int alignment = PAFlexIncolObject.ALIGN_LEFT;
	        			
	        			if (val.equals("left"))
						{
							alignment = PAFlexIncolObject.ALIGN_LEFT;
						}
						else if (val.equals("right"))
						{
							alignment = PAFlexIncolObject.ALIGN_RIGHT;
						}
						else if (val.equals("centre") || val.equals("center"))
						{
							alignment = PAFlexIncolObject.ALIGN_CENTRE;
						}
						else if (val.equals("justify") || val.equals("justified"))
						{
							alignment = PAFlexIncolObject.ALIGN_JUSTIFY;
						}
						else if (val.equals("force-justify") || val.equals("force-justified"))
						{
							alignment = PAFlexIncolObject.ALIGN_FORCE_JUSTIFY;
						}
	        			
						tab.setAlignment(alignment);
	        		}
	        			
	        		
	        		NodeList rows = el.getElementsByTagName("tr");
	        		
	        		for (int j = 0; j < rows.getLength(); j ++)
	        		{
	        			Element rowEl = (Element)rows.item(j);
	        			NodeList cols = rowEl.getElementsByTagName("td");
	        			
	        			List<PAFlexColumn> thisRow = 
	        					new ArrayList<PAFlexColumn>();
	        			
	        			for (int k = 0; k < cols.getLength(); k ++)
		        		{
	        				Element colEl = (Element)cols.item(k);
	        				
	        				PAFlexColumn newObj =
	    	        				new PAFlexColumn(recProcessPageContent(
	    	        				colEl.getChildNodes(), stylesheet, doc, thisLoc));
	    	        		
	    	        		thisRow.add(newObj);
	    	        		
	    	        		// make sure each col position has a width of -1
	    	        		if (tab.getColWidths().size() < k + 1)
	    	        			tab.getColWidths().add(-1.0f);
		        		}
	        			tab.getRows().add(thisRow);
	        		}
	        		retVal.add(tab);
	        	}
	        	else if (el.getTagName().equals("ul"))
	        	{
	        		PAFlexSimpleTable tab = new PAFlexSimpleTable();
	        		tab.setID(elID);
	        		if (el.hasAttribute("col-gap"))
	        			tab.setColGap(Float.parseFloat(el.getAttribute("col-gap")));
	        		else
	        			tab.setColGap(4);
	        		if (el.hasAttribute("row-gap"))
	        			tab.setRowGap(Float.parseFloat(el.getAttribute("row-gap")));
	        		else
	        			tab.setRowGap(0);
	        		
	        		NodeList items = el.getElementsByTagName("li");
	        		
	        		// empty col for indentation
	        		tab.getColWidths().add(10.0f);
	        		
	        		for (int j = 0; j < items.getLength(); j ++)
	        		{
	        			List<PAFlexIncolObject> thisRow = 
	        					new ArrayList<PAFlexIncolObject>();
	        			
	        			// glue for indentation
	        			PAFlexParagraph indentBlock = new PAFlexSimpleParagraph();
	        			thisRow.add(indentBlock);
	        			
	        			// add bullet
	        			try
	        			{
		                	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		            		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		            		Document docu = docBuilder.newDocument();
		            		Element bulletEl = docu.createElement("li");
		            		bulletEl.setTextContent("•");
		        					
		        			PAFlexParagraph bulletObj = new PAFlexFormattedParagraph(
	    	        				bulletEl, stylesheet, thisLoc, prevElement);
		        			thisRow.add(bulletObj);
	        			}
	        			catch (ParserConfigurationException pce)
	        			{
	        				pce.printStackTrace();
	        			}
	        			Element itemEl = (Element)items.item(j);
        				
        				PAFlexParagraph newObj = new PAFlexFormattedParagraph(
    	        				itemEl, stylesheet, thisLoc, prevElement);
    	        		thisRow.add(newObj);
    	        		
    	        		// make sure each col position has a width of -1
    	        		if (tab.getColWidths().size() < 3)
    	        			tab.getColWidths().add(-1.0f);
    	        		
    	        		tab.getRows().add(thisRow);
	        		}
	        		retVal.add(tab);
	        	}
	        	else if (el.getTagName().equals("fig"))
	        	{
	        		PAPhysGraphic bmp = null;
	        		try {
		        		String graphicPath = el.getAttribute("graphic");
		        		float width = -1;
		        		if (el.hasAttribute("width"))
		        			width = Float.parseFloat(el.getAttribute("width"));
		        		float height = -1;
		        		if (el.hasAttribute("height"))
	       					height = Float.parseFloat(el.getAttribute("height"));
		        		// TODO constructor that creates graphic depending on extension
	        		
						 bmp = new PAPhysBitmapGraphic(graphicPath, doc, width, height);
					} 
	        		catch (IOException e) {
						// TODO also catch missing fields
						e.printStackTrace();
					}
	        		catch (NullPointerException npe) {
						// TODO also catch missing fields
						npe.printStackTrace();
					}
	        		
	        		PAFlexFormattedParagraph caption =
	        				new PAFlexFormattedParagraph(el, stylesheet, thisLoc);

	        		PAFlexFigure fig = new PAFlexFigure(bmp, caption);
	        		fig.setID(elID);
	        		
	        		fig.setSpacing(stylesheet.insetSpacing());
	        		retVal.add(fig);
	        		
	        	}
	        	else if (el.getTagName().equals("inset"))
	        	{
	        		PAFlexInset newObj =
	        				new PAFlexInset(recProcessPageContent(el.getChildNodes(), stylesheet, doc, thisLoc));
	        		newObj.setID(elID);
	        		
	        		newObj.setSpacing(stylesheet.insetSpacing());
	        		if (el.getAttribute("position").equals("below"))
	        			newObj.setAppearsBelow(true);
	        		
	        		retVal.add(newObj);
	        	}
	        	else if (el.getTagName().equals("span"))
	        	{
	        		// just recurse; any attributes have already taken effect
	        		retVal.addAll(recProcessPageContent(el.getChildNodes(), stylesheet, doc, thisLoc));
	        	}
	        	else
	        	{
	        		// whitespace
	        		
	        		// unrecognized or invalid tag
	        		// e.g. <b> or other inline styling tag
	        	}
	        	
	        	// necessary to calculate spacing between block-level elements
	        	// (span and insets should have no effect!)
	        	if (el.getTagName().equals("span") ||
	        			el.getTagName().equals("inset") ||
	        			el.getTagName().equals("fig") ||
	        			el.getTagName().equals("tt"))
	        	{
	        		// do not set prevElement
	        	}
	        	else
	        	{
	        		prevElement = el;
	        	}
    		}
    		else
    		{
    			// ignore whitespace and other text here
    			// TODO: perhaps WARN when non-whitespace text found
    		}
        }
    	
    	return retVal;
    }
    
    // try/catch moved to calling method 9.04.06
    protected static org.w3c.dom.Document setUpXML(String nodeName) 
        throws ParserConfigurationException
    {
        //try
        //{
            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder myDocBuilder = myFactory.newDocumentBuilder();
            DOMImplementation myDOMImpl = myDocBuilder.getDOMImplementation();
            // resultDocument = myDOMImpl.createDocument("com.tamirhassan", "PDFResult", null);
            org.w3c.dom.Document resultDocument = 
                myDOMImpl.createDocument("com.tamirhassan.pint", nodeName, null);
            return resultDocument;
        //}
        //catch (ParserConfigurationException e)
        //{
         //   e.printStackTrace();
         //   return null;
        //}
        
    }
    
//  Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    public static byte[] serializeXML(org.w3c.dom.Document resultDocument)
        throws DocumentProcessingException
    {
        // calls the above and returns a byte[] from the XML Document.
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        
        try
        {
        	Writer output = new OutputStreamWriter(outStream, DEFAULT_ENCODING);
            serializeXML(resultDocument, output);
        }
        catch (IOException e)
        {
        	throw new DocumentProcessingException(e);
    	}

        return outStream.toByteArray();
    }
    
    public static void serializeXML(org.w3c.dom.Document resultDocument, OutputStream outStream)
        throws DocumentProcessingException
    {
        try
        {
        	Writer output = new OutputStreamWriter(outStream, DEFAULT_ENCODING);
            serializeXML(resultDocument, output);
        }
        catch (IOException e)
        {
        	throw new DocumentProcessingException(e);
    	}
    }
    
    public static void serializeXML
        (org.w3c.dom.Document resultDocument, Writer output)
        throws IOException
    {
        // The third parameter in the constructor method for
        // _OutputFormat_ controls whether indenting should be
        // used.  Unfortunately, I have found some bugs in the
        // indenting implementation that have corrupted the text
        // so I have switched it off. 
         
        OutputFormat myOutputFormat =
            new OutputFormat(resultDocument,
                             "UTF-8",
                             true);

        // output used to be replaced with System.out
        XMLSerializer s = 
        new XMLSerializer(output, 
                              myOutputFormat);

        try {
        s.serialize(resultDocument);
        // next line added by THA 21.03.05
        output.flush();
        }
        catch (IOException e) {
            System.err.println("Couldn't serialize document: "+
               e.getMessage());
            throw e;
        }        

         // end of addition
    }
    
    /**
     * This will print the usage requirements and exit.
     */
    private static void usage()
    {
        System.err.println( "Usage: java at.ac.tuwien.dbai.pdfwrap.ProcessFile [OPTIONS] <PDF file> [Text File]\n" +
            "  -password  <password>        Password to decrypt document\n" +
            "  -encoding  <output encoding> (ISO-8859-1,UTF-16BE,UTF-16LE,...)\n" +
            "  -xmillum                     output XMIllum XML (instead of XHTML)\n" +
            "  -norulinglines               do not process ruling lines\n" +
            "  -spaces                      split low-level segments according to spaces\n" +
            "  -console                     Send text to console instead of file\n" +
            "  -startPage <number>          The first page to start extraction(1 based)\n" +
            "  -endPage <number>            The last page to extract(inclusive)\n" +
            "  <PDF file>                   The PDF document to use\n" +
            "  [Text File]                  The file to write the text to\n"
            );
        System.exit( 1 );
    }

	// TODO: change to PAFlexDocument - list of PAPageSpecs
    public static List<PAFlexPageSpec> processInputSimplePara(File inputFile)
    {
    	List<PAFlexPageSpec> retVal = new ArrayList<PAFlexPageSpec>();
    	
    	// below example from:
    	// http://stackoverflow.com/questions/7704827/java-reading-xml-file
    	
    	/*
    	File file = new File("userdata.xml");
    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
    	        .newInstance();
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document document = documentBuilder.parse(file);
    	String usr = document.getElementsByTagName("user").item(0).getTextContent();
    	String pwd = document.getElementsByTagName("password").item(0).getTextContent();
    	*/
    	
    	try 
    	{
	    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
	    	        .newInstance();
	    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	Document document = documentBuilder.parse(inputFile);
	    	// get first story
	    	NodeList storyList = document.getElementsByTagName("content");
	    	Node story = storyList.item(0);
//	    	Node story = document.getElementsByTagName("story").item(0);
	    
	    	PAFlexPageSpec pageSpec = new PAFlexPageSpec((float) (8.25 * 72), (float) (11.75 * 72));
	    	pageSpec.setLeftMargin(72f); // 1 inch
			pageSpec.setRightMargin(72f);
			pageSpec.setTopMargin(72f);
			pageSpec.setBottomMargin(72f);
			
			PAFlexColumn titleCol = new PAFlexColumn();;
			PAFlexSimpleParagraph titlePar = new PAFlexSimpleParagraph(
					"Next generation typeface representations: Revisiting parametric fonts", 
					PDType1Font.HELVETICA_BOLD, 20);
			titleCol.getContent().add(titlePar);
			
			pageSpec.getContent().getContent().add(titleCol);
			//pageSpec.getContent().getContent().add(titlePar);
			pageSpec.getContent().getContent().add(new KPGlue(12));
			
			PAFlexMultiCol multiCol = new PAFlexMultiCol(2, 0);
			pageSpec.getContent().getContent().add(multiCol);
			
			PAFlexMultiCol multiCol3 = new PAFlexMultiCol(1, 0);
			pageSpec.getContent().getContent().add(multiCol3);
			
			multiCol3.setContent(multiCol.getContent());
			
			boolean noBreakAfter = false;
			for (int i = 0; i < story.getChildNodes().getLength(); i ++)
			{
				Node n = story.getChildNodes().item(i);
				
				String nodeName = n.getNodeName().toLowerCase();
				if (nodeName.equals("p") ||
					nodeName.equals("h1") ||
					nodeName.equals("h2") ||
					nodeName.equals("h3") ||
					nodeName.equals("h4"))
				{
					
					if (i > 0)
					{
						// insert penalty before vspace to inhibit breaking after headings
						// (TODO: it's more complex than this in practice, but this is a start)
						if (noBreakAfter)
							multiCol.getContent().add(
									new KPPenalty(10000));
						
						// insert vertical glue between paras
						if (nodeName.equals("h1") ||
							nodeName.equals("h2") ||
							nodeName.equals("h3") ||
							nodeName.equals("h4"))
						{
							multiCol.getContent().add(
									new KPGlue(12, 0, 0));
						}
						else
						{
							multiCol.getContent().add(
									new KPGlue(6, 0, 0));
						}
					}
					
					
					
	//				String content = n.getFirstChild().getTextContent();
					// replace all newlines with space characters
					String content = n.getTextContent().replaceAll("[\\t\\n\\r]"," ").trim();
					
					PDType1Font times = PDType1Font.TIMES_ROMAN;
					PDType1Font helvb = PDType1Font.HELVETICA_BOLD;
					
					PAFlexParagraph newPar = null;
					
					if (n.getNodeName().toLowerCase().equals("p"))
					{
						newPar = new PAFlexSimpleParagraph(content, times, 11.35f);
						noBreakAfter = false;
					}
					else if (n.getNodeName().toLowerCase().equals("h1"))
					{
						newPar = new PAFlexSimpleParagraph(content, helvb, 20);
						noBreakAfter = true;
					}
					else if (n.getNodeName().toLowerCase().equals("h2"))
					{
						newPar = new PAFlexSimpleParagraph(content, helvb, 17);
						noBreakAfter = true;
					}
					else if (n.getNodeName().toLowerCase().equals("h3"))
					{
						newPar = new PAFlexSimpleParagraph(content, helvb, 14);
						noBreakAfter = true;
					}
					
					if (newPar != null) // WTF?
					{
						multiCol.getContent().add(newPar);
					}
				}
				else
				{
					System.out.println("unrecognized node: " + nodeName + ": " + n);
				}
			}
			
			// add the one page spec to result
			retVal.add(pageSpec);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return retVal;
    }
}

// the above taken from: 
// http://userpage.fu-berlin.de/~ram/pub/pub_jf47htqHHt/java_sax_parser_en

/** utility class */

final class XML
{ /** create a new XML reader */
  final public static org.xml.sax.XMLReader makeXMLReader()  
  throws Exception 
  { final javax.xml.parsers.SAXParserFactory saxParserFactory   =  
    javax.xml.parsers.SAXParserFactory.newInstance(); 
    final javax.xml.parsers.SAXParser        saxParser = saxParserFactory.newSAXParser(); 
    final org.xml.sax.XMLReader              parser    = saxParser.getXMLReader(); 
    return parser; }}

/*
 * @(#)ExampleFileFilter.java	1.13 02/06/13
 */

class ExampleFileFilter extends FileFilter {

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";

    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    /**
     * Creates a file filter. If no filters are added, then all
     * files are accepted.
     *
     * @see #addExtension
     */
    public ExampleFileFilter() {
	this.filters = new Hashtable();
    }

    /**
     * Creates a file filter that accepts files with the given extension.
     * Example: new ExampleFileFilter("jpg");
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String extension) {
	this(extension,null);
    }

    /**
     * Creates a file filter that accepts the given file type.
     * Example: new ExampleFileFilter("jpg", "JPEG Image Images");
     *
     * Note that the "." before the extension is not needed. If
     * provided, it will be ignored.
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String extension, String description) {
	this();
	if(extension!=null) addExtension(extension);
 	if(description!=null) setDescription(description);
    }

    /**
     * Creates a file filter from the given string array.
     * Example: new ExampleFileFilter(String {"gif", "jpg"});
     *
     * Note that the "." before the extension is not needed adn
     * will be ignored.
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String[] filters) {
	this(filters, null);
    }

    /**
     * Creates a file filter from the given string array and description.
     * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
     *
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String[] filters, String description) {
	this();
	for (int i = 0; i < filters.length; i++) {
	    // add filters one by one
	    addExtension(filters[i]);
	}
 	if(description!=null) setDescription(description);
    }

    /**
     * Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     *
     * Files that begin with "." are ignored.
     *
     * @see #getExtension
     * @see FileFilter#accepts
     */
    public boolean accept(File f) {
	if(f != null) {
	    if(f.isDirectory()) {
		return true;
	    }
	    String extension = getExtension(f);
	    if(extension != null && filters.get(getExtension(f)) != null) {
		return true;
	    };
	}
	return false;
    }

    /**
     * Return the extension portion of the file's name .
     *
     * @see #getExtension
     * @see FileFilter#accept
     */
     public String getExtension(File f) {
	if(f != null) {
	    String filename = f.getName();
	    int i = filename.lastIndexOf('.');
	    if(i>0 && i<filename.length()-1) {
		return filename.substring(i+1).toLowerCase();
	    };
	}
	return null;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     *
     * For example: the following code will create a filter that filters
     * out all files except those that end in ".jpg" and ".tif":
     *
     *   ExampleFileFilter filter = new ExampleFileFilter();
     *   filter.addExtension("jpg");
     *   filter.addExtension("tif");
     *
     * Note that the "." before the extension is not needed and will be ignored.
     */
    public void addExtension(String extension) {
	if(filters == null) {
	    filters = new Hashtable(5);
	}
	filters.put(extension.toLowerCase(), this);
	fullDescription = null;
    }


    /**
     * Returns the human readable description of this filter. For
     * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     * @see FileFilter#getDescription
     */
    public String getDescription() {
	if(fullDescription == null) {
	    if(description == null || isExtensionListInDescription()) {
 		fullDescription = description==null ? "(" : description + " (";
		// build the description from the extension list
		Enumeration extensions = filters.keys();
		if(extensions != null) {
		    fullDescription += "." + (String) extensions.nextElement();
		    while (extensions.hasMoreElements()) {
			fullDescription += ", ." + (String) extensions.nextElement();
		    }
		}
		fullDescription += ")";
	    } else {
		fullDescription = description;
	    }
	}
	return fullDescription;
    }

    /**
     * Sets the human readable description of this filter. For
     * example: filter.setDescription("Gif and JPG Images");
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     */
    public void setDescription(String description) {
	this.description = description;
	fullDescription = null;
    }

    /**
     * Determines whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see isExtensionListInDescription
     */
    public void setExtensionListInDescription(boolean b) {
	useExtensionsInDescription = b;
	fullDescription = null;
    }

    /**
     * Returns whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see setExtensionListInDescription
     */
    public boolean isExtensionListInDescription() {
	return useExtensionsInDescription;
    }
}

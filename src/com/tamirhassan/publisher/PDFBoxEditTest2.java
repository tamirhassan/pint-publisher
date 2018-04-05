package com.tamirhassan.publisher;

// http://stackoverflow.com/questions/17685132/edit-pdf-page-using-pdfbox

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
 
public class PDFBoxEditTest2 {
 
    /**
     * @param args
     * @throws InvalidPasswordException 
     * @throws IOException 
     * @throws COSVisitorException 
     */
    public static void main(String[] args) throws InvalidPasswordException, IOException {
        // Create a new document
        //PDDocument document = new PDDocument();
    	File pdffile = new File("master-project-prip.pdf");
    	PDDocument document = PDDocument.load(pdffile);
    	// Create a new page and add to the document
        //PDPage page = new PDPage();
        //PDPage page = new PDPage(new PDRectangle((float)8.5*72, (float)11.75*72));
        //document.addPage(page);
        PDPage page = document.getPage(0);
        
     // Create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.TIMES_ROMAN;
        PDFont lastFont = null;
        
        PDResources resources = page.getResources();
        Iterator<COSName> ite = resources.getFontNames().iterator();
        while (ite.hasNext()) {
            COSName name = ite.next();
            //PDFont 
            lastFont = font;
            font = resources.getFont(name);
            boolean isEmbedded = font.isEmbedded();
            // ... do something with the results ...
            System.out.println("found font: " + font.getName() + " is embedded: " + isEmbedded);
        }
        
        
        try {
            // Start a new content stream which will hold content to be created
            PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, true, true);
            
            // Define a text content stream using the selected font, moving the
            // cursor and showing the text "Hello World"
            contentStream.beginText();
            System.out.println("current font: " + lastFont.getName());
            contentStream.setFont(lastFont, 12);
            // Move to the start of the next line, offset from the start of the
            // current line by (150, 700).
            contentStream.newLineAtOffset(150, 650);
            // Shows the given text at the location specified by the current
            // text matrix.
            contentStream
                    .showText("This is a PDF document created by PDFBox library2");
            contentStream.endText();
 
            // Make sure that the content stream is closed.
            contentStream.close();
            document.save("SamplePDF3.pdf");
            // finally make sure that the document is properly closed.
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
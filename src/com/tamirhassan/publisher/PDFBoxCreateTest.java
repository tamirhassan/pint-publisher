package com.tamirhassan.publisher;

// http://stackoverflow.com/questions/17685132/edit-pdf-page-using-pdfbox

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
 
public class PDFBoxCreateTest {
 
    /**
     * @param args
     * @throws IOException 
     * @throws COSVisitorException 
     */
    public static void main(String[] args) throws IOException {
        // Create a new document
        PDDocument document = new PDDocument();
        // Create a new page and add to the document
        PDPage page = new PDPage();
        //PDPage page = new PDPage(new PDRectangle((float)8.5*72, (float)11.75*72));
        document.addPage(page);
        // Create a new font object selecting one of the PDF base fonts
//        PDFont font = PDType1Font.TIMES_ROMAN;
        File fontfile = new File("hum531n.ttf");
        PDFont font = PDTrueTypeFont.loadTTF(document, fontfile);
        try {
            // Start a new content stream which will hold content to be created
            PDPageContentStream contentStream = new PDPageContentStream(
                    document, page);
 
            // Define a text content stream using the selected font, moving the
            // cursor and showing the text "Hello World"
            contentStream.beginText();
            contentStream.setFont(font, 12);
            // Move to the start of the next line, offset from the start of the
            // current line by (150, 700).
            contentStream.newLineAtOffset(150, 650);
            // Shows the given text at the location specified by the current
            // text matrix.
            contentStream
                    .showText("This is a PDF document created by PDFBox library");
            contentStream.endText();
 
            // Make sure that the content stream is closed.
            contentStream.close();
            document.save("SamplePDF.pdf");
            // finally make sure that the document is properly closed.
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
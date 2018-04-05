package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.stylesheet.PACharFormatting;

public class PAPhysTextBlock extends PAPhysContainer // implements PARenderable
{
	String content;
	List<PAPhysTextLine> items = new ArrayList<PAPhysTextLine>();
//	float leading = 0; // extra space between each line
	
	public PAPhysTextBlock()
	{
	}
	
	public PAPhysTextBlock(List<PAPhysTextLine> items, float width, float height)
	{
		this.items = items;
		this.width = width;
		this.height = height;
	}

	public List<PAPhysTextLine> getItems() {
		return items;
	}

	public void setItems(List<PAPhysTextLine> items) {
		this.items = items;
	}
	
	/*
	public float getLeading() {
		return leading;
	}

	public void setLeading(float leading) {
		this.leading = leading;
	}
	*/
	
	// TODO: remove calculated field -- only getters?
	public float contentHeight()
	{
		return contentHeight(true);
	}
	
	/**
	 *  
	 * @param finalSpaceAfter (default true) - includes final spaceAfter in total
	 * @return
	 */
	public float contentHeight(boolean finalSpaceAfter)
	{
//		float retVal = (leading * items.size());// (items.size() - 1));
		float retVal = 0;
		
		// including leading for the last line obtains the same result
		// as with LibreOffice
		for (PAPhysTextLine tl : items)
		{
			if (!finalSpaceAfter && tl == items.get(items.size() - 1)) // check if last line
				retVal += tl.getTextHeight();
			else
				retVal += tl.getHeight();
		}
		return retVal;
		
	}
	
	public String textContent()
	{
		/*
		StringBuffer retVal = new StringBuffer();
		for (PAPhysObject item : items)
			retVal.append(item.toText() + "\n");
		return retVal.toString();
		*/
		if (items.size() == 0)
			return "empty";
		else if (items.size() == 1)
			return (items.get(0).textContent());
		else
			return (items.get(0).textContent() + " ...");
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

	/*
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
	
	/*
	 * adds the brackets already!
	 * 
	 * copied from PAPhysTextLine -- TODO: move to util method?
	 */
	public static byte[] convertToPDFEncoding(String str)
	{
        try 
        {
			return str.getBytes("ISO-8859-1");
		} 
        catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
        return null;
	}
	
	// TODO: is this method still in use?
	protected void renderWord(PAWord w, PDPageContentStream contentStream)
			throws IOException
	{
		PACharFormatting cf = w.getCharFormatting();
		
		if (cf.isKerning() && cf.hasMetrics())
		{
			System.out.println("foo2kern");
			// TODO: hyphenation pairs for TrueType fonts
			
			// TODO: implement!
			
			StringBuffer currentGroup = new StringBuffer();
			ArrayList<String> groups = 
					new ArrayList<String>(textContent().length() / 3);
			ArrayList<Integer> horizAdjustments = 
					new ArrayList<Integer>(textContent().length() / 3);
			
			// hack to make multi-word algorithm work with single words
			String[] words = new String[1];
			words[0] = w.getText();
			
			boolean firstWord = true;
			for (String word : words)
			{
				if (firstWord) firstWord = false;
				else
				{
					// add space between words
					currentGroup.append(" ");
//					word spacing taking care of by Tw operator
				}
				
				for (int i = 0; i < word.length(); i ++)
				{
					char thisChar = word.charAt(i);
//					System.out.println("AFM char width: " + metric.getCharWidth(thisChar));
					if (i >= 1) // check for kerning
					{
						char prevChar = word.charAt(i - 1);
						int horizAdjustment = 0;
						
						if (cf.isKerning()) // if metric=null returns zero
							horizAdjustment += cf.getKerningAdjustment(prevChar, thisChar);
						
						if (horizAdjustment != 0)
						{
							System.out.println("horizontal adjustment in word: " + w.toString());
							groups.add(currentGroup.toString());
							currentGroup = new StringBuffer();
							horizAdjustments.add(horizAdjustment);
//							System.out.println("horizAdjustment: " + horizAdjustment);
						}
					}
					currentGroup.append(thisChar);
				}
			}	
			
			// final group without any horiz adjustment
			boolean extraGroup = false;
			if (currentGroup.length() > 0)
			{
				groups.add(currentGroup.toString());
				extraGroup = true;
			}
			
			StringBuffer operand = new StringBuffer();
			
			for (int i = 0; i < groups.size(); i ++)
			{
				String text = groups.get(i);
				operand.append(text); // this methods adds the brackets already
				
				if (i == (groups.size() - 1) && extraGroup)
				{
					// final group without any horiz adjustment
				}
				else
				{
					float horizAdjust = horizAdjustments.get(i);
					int haInt = (int)(horizAdjust * -1); // positioning instructions are negative
					operand.append(" ");
					operand.append(Integer.toString(haInt));
					operand.append(" ");
				}
			}
			
			if (groups.size() > 0)
			{
				contentStream.appendRawCommands("[ ");
				contentStream.appendRawCommands(operand.toString());
//				System.out.println("operand: " + operand + " width: " + 
//						contentWidth(false) + " scaledWidth: " + 
//						(((float)contentWidth(false)/1000)*fontSize) +
//						" fillRatio: " + fillRatio());
				contentStream.appendRawCommands(" ] ");
				contentStream.appendRawCommands("TJ\n");

			}
		}
		else
		{
			System.out.println("bar2kern");
			// Simple text drawing with Tj operator
			contentStream.drawString(w.getText());
		}
	}
	
	protected List<PAFragment> getFragments(
			String text, PACharFormatting cf)
	{
		List<PAFragment> retVal = new ArrayList<PAFragment>();

		StringBuffer currentGroup = new StringBuffer();

		for (int i = 0; i < text.length(); i ++)
		{
			char thisChar = text.charAt(i);
//			System.out.println("AFM char width: " + metric.getCharWidth(thisChar));
			if (i >= 1) // check for kerning
			{
				char prevChar = text.charAt(i - 1);
				int horizAdjustment = 0;
				
				if (cf.isKerning()) // if metric is null returns zero
					horizAdjustment += cf.getKerningAdjustment(prevChar, thisChar);
				
				if (horizAdjustment != 0)
				{
					PAFragment newFragment = new PAFragment(
							currentGroup.toString(), horizAdjustment);
					retVal.add(newFragment);
					
					currentGroup = new StringBuffer();
					
//					System.out.println("horizAdjustment: " + horizAdjustment);
				}
			}
			currentGroup.append(thisChar);
		}
		
		// final group without any horiz adjustment
		if (currentGroup.length() > 0)
		{
			PAFragment newFragment = new PAFragment(currentGroup.toString(), 0);
			retVal.add(newFragment);
		}
		
		return retVal;
	}
			
	// TODO: adjust!
	/**
	 * adds the operands to the relevant TJ instruction in contentStream
	 * 
	 * @param contentStream
	 * @param l line
	 * @param adjRatio - removed
	 * @throws IOException
	 */
	protected void renderLineItems(PDPageContentStream contentStream, 
			PAPhysTextLine l)//, float adjRatio) 
			throws IOException
	{
		OutputStream output = null;
		try 
		{
			Field f = contentStream.getClass().getDeclaredField("output");
			f.setAccessible(true);
			output = (OutputStream) f.get(contentStream); //IllegalAccessException
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("in renderLineSegment");
		
		float horizScale = 100; // TODO
		
		//PAFontMetrics metric = ls.getMetrics(); // TODO: consistent naming

//		ByteArrayOutputStream operand = new ByteArrayOutputStream();
		ArrayList operand = new ArrayList();
		ArrayList<Object> newOperand = new ArrayList<Object>();
		
		// TEST: set the word spacing here instead of per word
		// (assumes that all gaps are the same width)
		/*
		boolean found = false;
		PALSFloat firstGap = null;
		for (PALSObject item : ls.getItems())
			if (!found && item instanceof PALSFloat)
			{
				firstGap = (PALSFloat) item;
				found = true;
			}
		if (found)
		{
			char spaceChar = ' ';
			float spaceWidth = metric.getCharWidth(spaceChar);
			float wordSpacing = firstGap.getValue() - spaceWidth;
			
			// word spacing adjustments are absolute
			float spacingOperand = ls.getFontSize() * (wordSpacing / 1000);
			
			System.out.println("spacing: " + spacingOperand);
			
			contentStream.appendRawCommands(Float.toString(spacingOperand));
			contentStream.appendRawCommands(" Tw\n");
		}
		*/
		// END OF TEST
		
		System.out.println();
		float width = 0.0f;
		
		//for (PALSObject item : ls.getItems())
		for (PAPhysObject item: l.getItems())
		{
			//if (item instanceof PALSFloat)
			if (item instanceof KPGlue)
			{
				// add a horizontal space consisting of:
				// space character + kerning adjustment to achieve desired gap width
				
				// TODO: define space character in utils or config
				
				char spaceChar = ' ';
				
				float spaceWidth = 0.0f;
				// require space width of previously used font!
				//if (currMetrics != null) 
				
				if (currCF != null)
				//if (false)
				{
					spaceWidth = currCF.getCharWidth(spaceChar);
					
					width += ((KPGlue)item).spaceAmount();
					
					float spaceAmount = ((KPGlue)item).spaceAmount() * (1000 / currCF.getFontSize());
					float kernAdjust = spaceAmount - spaceWidth;
					
					
				
					
					// kerning adjustments are negative
					int kernOperand = (int) (kernAdjust * -1);
					// TODO: change all these values to floats?
					
					System.out.println("adding space as kernOperand: " + kernOperand);
	
//					operand.append(String.valueOf(spaceChar));
//					operand.write(convertToPDFEncoding(String.valueOf(spaceChar)));
					// TODO: check that encoding correct!
					operand.add(convertToPDFEncoding(String.valueOf(spaceChar)));
					newOperand.add(String.valueOf(spaceChar));
//					operand.append(" ");
//					operand.write(convertToPDFEncoding(" "));
					operand.add(" ");
//					operand.append(Integer.toString(kernOperand));
//					operand.write(convertToPDFEncoding(Integer.toString(kernOperand)));
					operand.add(Integer.toString(kernOperand));
					newOperand.add((float)kernOperand);
//					operand.append(" ");
//					operand.write(convertToPDFEncoding(" "));
					operand.add(" ");
					
					/*
					int kernOperand = (int) (spaceAmount * -1);
					operand.append(Integer.toString(kernOperand));
					*/
				}
				else
				{
					// if space at beginning of paragraph (no previous font)
					// just add the gap without the space character
					
					float spaceAmount = ((KPGlue)item).spaceAmount() * (1000 / currCF.getFontSize());
					int kernOperand = (int) (spaceAmount * -1);
//					operand.append(Integer.toString(kernOperand));
//					operand.write(convertToPDFEncoding(Integer.toString(kernOperand)));
					operand.add(Integer.toString(kernOperand));
					newOperand.add((float)kernOperand);
					
					/*
					float kernAdjust = ((PALSFloat)item).getValue();
					int kernOperand = (int) (kernAdjust * -1);
					operand.append(Integer.toString(kernOperand));
					*/
					
					System.err.println("space before text begin");
				}
				
			}
			//if (item instanceof PALSString)
			if (item instanceof PAWord)
			{
				PAWord w = (PAWord)item;
				PACharFormatting cf = w.getCharFormatting();
				
				/*
				PDFont font = cf.getFont();
				float fontSize = cf.getFontSize();
				boolean hasMetrics = cf.hasMetrics();
				boolean kerning = cf.isKerning();
				*/
				
				System.out.print(" " + w.getText() + w.getWidth());
				width += w.getWidth();
				
				// call setFont if font or fontsize changed
				//if (font != currFont || fontSize != currFontSize)
				if (currCF == null || !currCF.isSameFont(cf))
				{
					System.out.println("calling setFont");
					
					// add all previous text
//					appendTJ(contentStream, operand, output);
					contentStream.showTextWithPositioning(newOperand.toArray());
					operand = new ArrayList();
					newOperand = new ArrayList<Object>();
					
					contentStream.setFont( cf.getFont(), cf.getFontSize() );
					
				}
				
				// the above does not work mid-line!
				// call setFont if font or fontsize changed
				
				/*
				if (font != currFont || fontSize != currFontSize)
				{
					System.out.println("calling setFont with font: " + font + " fontSize: " + fontSize);
					contentStream.endText();
					contentStream.setFont( font, fontSize );
					contentStream.beginText();
				}
				*/
		
				// set current variables to new values
				/*
				if (font != currFont)
					currFont = font;
				if (fontSize != currFontSize)
					currFontSize = fontSize;
				*/
				currCF = cf;
				
				if (currCF.isKerning() && currCF.hasMetrics())
				{
					System.out.print("K");
					List<PAFragment> fragments = getFragments(w.getText(), currCF);
					
					if (fragments.size() > 0)
					{
						for (PAFragment fragment : fragments)
						{
//							operand.append(fragment.getText()); // this methods adds the brackets already
//							operand.write(currFont.encode(fragment.getText()));
//							System.out.println("printing fragment: " + fragment.getText());
							operand.add(cf.getFont().encode(fragment.getText()));
							newOperand.add(fragment.getText());
							
							if (fragment.getHorizAdjust() != 0)
							{
								int haInt = (int)(fragment.getHorizAdjust() * -1); // positioning instructions are negative
//								operand.append(" ");
//								operand.write(convertToPDFEncoding(" "));
								operand.add(" ");
//								operand.append(Integer.toString(haInt));
//								operand.write(convertToPDFEncoding(Integer.toString(haInt)));
								operand.add(Integer.toString(haInt));
								newOperand.add((float)haInt);
//								operand.append(" ");
//								operand.write(convertToPDFEncoding(" "));
								operand.add(" ");
							}
							else
							{
								//???
							}
						}
						
						// now add the operator and operand to the content stream
					}
				}
				// TODO: backup plan if no metrics available?
				else
				{
					System.out.print("k");
					// Simple text drawing with Tj operator
//					works with TT font!
//					contentStream.drawString(w.getText());
					//operand.add(cf.getFont().encode(w.getText()));
					operand.add(cf.getFont().encode(w.getText()));
					newOperand.add(w.getText());
//					operand.add(" 0 ");
				}
			}
		}
		
//		appendTJ(contentStream, operand, output);
		contentStream.showTextWithPositioning(newOperand.toArray());
		
		// above static function replaces this block (which worked)
		// TH: required to enable font changes within line
		/* /////////////////////////
		
		contentStream.appendRawCommands("[ ");
//		contentStream.appendRawCommands(currFont.encode(operand.toString()));//.getBytes("ISO-8859-1"));
//		COSWriter.writeString(currFont.encode(operand.toString()), output);
		
//		contentStream.appendRawCommands(operand.toByteArray());
		for (Object o : operand) 
		{
			if (o instanceof String)
			{
				contentStream.appendRawCommands((String) o);
			}
			if (o instanceof byte[])
			{
				COSWriter.writeString((byte[])o, output);
			}
			else
			{
				//System.out.println(o);
				//System.out.println(o instanceof byte[]);
				//System.out.println("foo");
			}
		}
		
		
//		output.write(operand.toByteArray());
		contentStream.appendRawCommands("] ");
		contentStream.appendRawCommands("TJ\n");
		
		*/ /////////////////////////
		
		
		/*
		 * following done by showText()
		 * 
		COSWriter.writeString(font.encode(text), output);
        write(" ");

        writeOperator("Tj")
		*/
		
		//  output.write(commands); for appending byte[]
		// output.write(commands.getBytes(Charsets.US_ASCII)); for String
		
		/*
		byte[] ba = currFont.encode("fubar");
//		contentStream.appendRawCommands("[ ");
		contentStream.appendRawCommands(currFont.encode("fubar"));
//		contentStream.appendRawCommands("] ");
		contentStream.appendRawCommands(" ");
		contentStream.appendRawCommands("Tj\n");
		*/
		
		// works
		/*
		COSWriter.writeString(currFont.encode("fubar"), output);
//        output.write(new String(" Tj\n").getBytes("ISO-8859-1"));
		appendToStream(" Tj\n", output);
        */
		
//		contentStream.showText("arb");
        
//		System.out.println(" total=" + width);
	}
	
	/*
	protected static void appendTJ(PDPageContentStream contentStream, List<Object> operand, OutputStream output) 
			throws IOException
	{
		contentStream.appendRawCommands("[ ");
//		contentStream.appendRawCommands(currFont.encode(operand.toString()));//.getBytes("ISO-8859-1"));
//		COSWriter.writeString(currFont.encode(operand.toString()), output);
		
//		contentStream.appendRawCommands(operand.toByteArray());
		for (Object o : operand) 
		{
			if (o instanceof String)
			{
				contentStream.appendRawCommands((String) o);
			}
			if (o instanceof byte[])
			{
				COSWriter.writeString((byte[])o, output);
			}
			else
			{
				//System.out.println(o);
				//System.out.println(o instanceof byte[]);
				//System.out.println("foo");
			}
		}
		
		
//		output.write(operand.toByteArray());
		contentStream.appendRawCommands("] ");
		contentStream.appendRawCommands("TJ\n");
	}
	*/
	
	protected static void appendToStream(String s, OutputStream os) 
			throws UnsupportedEncodingException, IOException
	{
		os.write(s.getBytes("ISO-8859-1"));
	}
	
	
	// TODO: font/fontsize/positioning changes
	// can also occur within a line
	
	// TODO: change references to font size here to line height
	// ATM these are the same, but for e.g. math, may be different
	// leading should solely be calculated based on line height.
	
	// TODO: store a TextState (passed to render method as object)
	// to avoid redundant PDF operators for font/size/leading changes
	
	protected PACharFormatting currCF = null;
	//protected PDFont currFont = null;
	//protected PAFontMetrics currMetrics = null;
	//protected float currFontSize = -1;
	protected float currPdfLeading = -1;
	//protected boolean currKerning = true;
	boolean prevObjectLine = false;
	
	public void render(PDPageContentStream contentStream, 
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
		
		// account for height of first (text) line
		// as rendering is baseline based
		float yOffset = 0;
		if (items.size() > 0)
			yOffset = items.get(0).getHeight();

		contentStream.beginText();
//		System.out.println("moving to offset: (" + x1 + ", " + (y2 - yOffset) + ")");
//		contentStream.moveTextPositionByAmount(x1, (y2 - yOffset));
		contentStream.newLineAtOffset(x1, (y2 - yOffset));
		
		// set leading
//		contentStream.appendRawCommands((1 - leading) + " TL \n");
//		System.out.println("appending: " + leading + " TL \\n");
		
		// TODO: go through the individual items of the line
		// and manage font changes
		
		for (PAPhysTextLine l : items)
		{
			System.out.println("rendering line: " + l.textContent());
			//System.out.println("contentWidth: " + l.contentWidth());
			
			// TEST 2
			// calculate what the spacing should be ...
			
			/*
			
			float textContentWidth = 0;
			int numSpaces = 0;
			float fontSizeTest = 0;
			PAFontMetrics metricTest = null;
			float spCharWidth = 0;
			for (PAPhysInlineObject o : l.getItems())
			{
				if (o instanceof PAWord)
				{
					fontSizeTest = ((PAWord) o).getFontSize();
					metricTest = ((PAWord) o).getMetric();
//					
//					float boxWidth = o.getWidth();
					float boxWidth = (metricTest.getStringWidth(((PAWord) o).getText(), true) * (fontSizeTest / 1000));
					spCharWidth = (metricTest.getStringWidth(" ", true));// * (fontSizeTest / 1000));
					
					System.out.println("fs: " + fontSizeTest + " boxWidth: " + boxWidth + " stored width: " + o.getWidth());
					
					textContentWidth += boxWidth;
					
				}
				else if (o instanceof KPGlue)
				{
					System.out.println("glue: " + o.getWidth());
					numSpaces ++;
				}
			}
			
			float widthPerSpace = ((width - textContentWidth)/numSpaces);
			
			float normalizedWidth = widthPerSpace * (1000/fontSizeTest);
			
			System.out.println(l.toText());
			System.out.println("l contentWidth: " + textContentWidth + " width: " + l.getWidth() +
					" numSpaces: " + numSpaces + " spCharWidth: " + spCharWidth +
					" width per space: " + widthPerSpace + " normalizedWidth: " + normalizedWidth);
				
			// END OF TEST 2
			
			*/
			
			// do the spacing and carriage return BEFORE the line and
			// then output the line
			
			// pdf requires a -ve leading value which includes the line height
//			float pdfLeading = l.getHeight() + leading;
			float pdfLeading = l.getHeight();
			
//			System.out.println("pdfLeading: " + pdfLeading);
//			System.out.println("l.height: " + l.height);
		
			if (pdfLeading != currPdfLeading)
			{
				// always run before first line
				// additionally if the line height changes
				
				//contentStream.appendRawCommands(pdfLeading + " TL \n");
				contentStream.setLeading(pdfLeading);
//					System.out.println("appending: " + pdfLeading + " TL \\n");
				currPdfLeading = pdfLeading;
			}
			// append newline between lines -- automatically adds leading space
			if (prevObjectLine)
			{
//				contentStream.appendRawCommands("T* \n");
				contentStream.newLine();
//				System.out.println("appending: T* \\n");
			}
			
			
			//List<PAPhysLineSegment> lineSegments = l.generateLineSegments();
			
			//for (PAPhysLineSegment ls : lineSegments)
			//for (PAPhysInlineObject lo: l.getItems())
			//{	
				renderLineItems(contentStream, l);//, l.adjustmentRatio);
			//}
			
			prevObjectLine = true;
		}
		contentStream.endText();
	}
}

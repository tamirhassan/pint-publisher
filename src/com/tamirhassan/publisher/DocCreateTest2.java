package com.tamirhassan.publisher;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.afm.KernPair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import at.ac.tuwien.dbai.pdfwrap.model.document.GenericSegment;

import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPPenalty;
import com.tamirhassan.publisher.model.PAFlexColumn;
import com.tamirhassan.publisher.model.PAFlexLayoutResult;
import com.tamirhassan.publisher.model.PAFlexObject;
import com.tamirhassan.publisher.model.PAFlexPageSpec;
import com.tamirhassan.publisher.model.PAFlexParagraph;
import com.tamirhassan.publisher.model.PAPhysColumn;
import com.tamirhassan.publisher.model.PAPhysContainer;
import com.tamirhassan.publisher.model.PAPhysPage;
import com.tamirhassan.publisher.model.PAPhysTextBlock;
import com.tamirhassan.publisher.model.PAPhysTextLine;

public class DocCreateTest2 
{
	public static void main(String[] args) throws Exception
	{
		// Create a document
		PDDocument document = new PDDocument();
	
		// TODO: try importing TT fonts
		/*
		PDTrueTypeFont ttf = 
				PDTrueTypeFont.loadTTF(document, "/home/tam/Downloads/l_10646.ttf");
		
		PDTrueTypeFont arial = 
				PDTrueTypeFont.loadTTF(document, "/home/tam/Downloads/ARIALUNI.TTF");
		*/
		
		// Create a new font object selecting one of the PDF base fonts
		PDType1Font times = PDType1Font.TIMES_ROMAN;
		PDType1Font helveticaBold = PDType1Font.HELVETICA_BOLD;
		
		/*
		String heading1text = "Lorem ipsum";
		PAFlexParagraph heading1par =
				new PAFlexParagraph(heading1text, helveticaBold, 16);
		
		String par1text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
		PAFlexParagraph par1 =
				new PAFlexParagraph(par1text, helvetica, 10);
		
		String par2text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
		PAFlexParagraph par2 =
				new PAFlexParagraph(par2text, helvetica, 10);
		
		String heading3text = "Duis autem";
		PAFlexParagraph heading3par =
				new PAFlexParagraph(heading3text, helveticaBold, 16);
		
		String par3text = "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. ";
		PAFlexParagraph par3 =
				new PAFlexParagraph(par3text, helvetica, 10);
		
		String par4text = "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. ";
		PAFlexParagraph par4 =
				new PAFlexParagraph(par4text, helvetica, 10);
		
		String par5text = "In olden times, when wishing still helped, there lived a king whose daughters were all beautiful, but the youngest was so beautiful that the sun itself, which had seen so many things, was always filled with amazement each time it cast its rays upon her face.  Now, there was a great dark forest ...";
		*/
		//String par6text = "In olden times, when wishing still helped, there lived a king whose daughters were all beautiful, but the youngest was so beautiful that the sun itself, which had seen so many things, was always filled with amazement each time it cast its rays upon her face.";// Now, there was a great dark forest near the king's castle, and in this forest, beneath an old linden tree, was a well. Whenever the days were very hot, the king's daughter would go into this forest and sit down by the edge of the cool well. If she became bored, she would take her golden ball, throw it into the air, and catch it. More than anything else she loved playing with this ball.";
		
		// Knuth's version - translation by Margaret Hunt - out of copyright
		//String par6text = "In olden times when wishing still helped one, there lived a king whose daughters were all beautiful; and the youngest was so beautiful that the sun itself, which has seen so much, was astonished whenever it shone in her face.";
		
		// more authentic version
		String par6text = "In old times when wishing still helped one, there lived a king whose daughters were all beautiful, but the youngest was so beautiful that the sun itself, which has seen so much, was astonished whenever it shone in her face.";
		
		
		String par7text = "Vienna is the capital and largest city of Austria, and one of the nine states of Austria. Vienna is Austria's primary city, with a population of about 1.794 million (2.6 million within the metropolitan area, more than 20% of Austria's population), and its cultural, economic, and political centre. It is the 7th-largest city by population within city limits in the European Union. Until the beginning of the 20th century it was the largest German-speaking city in the world, and before the splitting of the Austro-Hungarian Empire in World War I the city had 2 million inhabitants. Today it is the second only to Berlin in German speakers. Vienna is host to many major international organizations, including the United Nations and OPEC. The city lies in the east of Austria and is close to the borders of the Czech Republic, Slovakia, and Hungary. These regions work together in a European Centrope border region. Along with nearby Bratislava, Vienna forms a metropolitan region with 3 million inhabitants. In 2001, the city centre was designated a UNESCO World Heritage Site.";
		String par8text = "Konstanz is a university city with approximately 80,000 inhabitants located at the western end of Lake Constance in the south-west corner of Germany, bordering Switzerland. The city houses the University of Konstanz and was for more than 1200 years residence of the Roman Catholic Diocese of Konstanz.";
		
		String par9text = "In olden times, when wishing still helped, there lived a king whose daughters were all beautiful, but the youngest was so beautiful that the sun itself, which had seen so many things, was always filled with amazement each time it cast its rays upon her face. Now, there was a great dark forest near the king's castle, and in this forest, beneath an old linden tree, was a well. Whenever the days were very hot, the king's daughter would go into this forest and sit down by the edge of the cool well. If she became bored, she would take her golden ball, throw it into the air, and catch it. More than anything else she loved playing with this ball. In olden times, when wishing still helped, there lived a king whose daughters were all beautiful, but the youngest was so beautiful that the sun itself, which had seen so many things, was always filled with amazement each time it cast its rays upon her face. Now, there was a great dark forest near the king's castle, and in this forest, beneath an old linden tree, was a well. Whenever the days were very hot, the king's daughter would go into this forest and sit down by the edge of the cool well. If she became bored, she would take her golden ball, throw it into the air, and catch it. More than anything else she loved playing with this ball.";
		/*
		PAFlexParagraph par5 =
				new PAFlexParagraph(par5text, helvetica, 24);
		*/
		
		/*
		PAFlexParagraph par6 =
				new PAFlexParagraph(par6text, times, 24);
		*/
		/*
		PAFlexParagraph par7 =
				new PAFlexParagraph(par5text, helvetica, 24);
		
		PAFlexParagraph par8 =
				new PAFlexParagraph(par5text, helvetica, 24);
		*/
		
		// was 36pt
		
//		String testString = "Both constructors could have been declared in Bicycle because they have different argument lists. As with methods, the Java platform differentiates constructors on the basis of the number of arguments in the list and their types. You cannot write two constructors that have the same number and type of arguments for the same class, because the platform would not be able to tell them apart. Doing so causes a compile-time error.";
		
		// Step 2: test the Page object
		
		/*
		PAPageSpec pageSpec = new PAPageSpec();
//		physPage.setWidth((float) (8.25 * 72)); // A4 imperial
//		physPage.setHeight((float) (11.75 * 72));
		pageSpec.setLeftInsideMargin(72f); // 1 inch
		pageSpec.setRightOutsideMargin(72f);
		pageSpec.setTopMargin(72f);
		pageSpec.setBottomMargin(72f);
		*/
		
//		PAFlexColumnSpec pageContainer = new PAFlexColumnSpec
//				(PAFlexColumnSpec.FIXED_WIDTH_FIXED_NUM_COLS);
//		pageContainer.setNoCols(1);
		
		/*
		PAFlexSingleColumn pageContainer = new PAFlexSingleColumn();
		
		PAFlexColumnSpec pageContent = new PAFlexColumnSpec
				(PAFlexColumnSpec.FIXED_WIDTH_FIXED_NUM_COLS);
		pageContent.setNoCols(4);
		pageContent.setColSpacing(16);
		pageContent.setBalanceCols(true);
		
		String heading10text = "Duis autem";
		PAFlexParagraph heading10par =
				new PAFlexParagraph(heading10text, helveticaBold, 16);
		*/
		
		// TODO: is PhysFlow a Container in the same way as Paragraph?
		
//		float paragraphSpacing = 6;
		float paragraphSpacing = 0;
		float sectionSpacing = 0 * 1.2f;
//		float beforeSectionSpacing = 4 * 1.2f;
		float afterHeadingSpacing = 4 * 1.2f;

		/*
		 * with these settings wrong column breaking TEST!
		float paragraphSpacing = 0;
		float sectionSpacing = 2 * 1.2f;
		float afterHeadingSpacing = 2 * 1.2f;
		*/
		
//		pageSpec.setContent(pageContent);
//		pageContainer.getContent().add(pageContent);
//		pageContainer.getContent().add(new KPGlue(sectionSpacing, 0, 0));
//		pageContainer.getContent().add(heading10par);
//		pageContainer.getContent().add(par5);
		/*
		pageContainer.getContent().add(par6);
//		pageContainer.getContent().add(par7);
//		pageContainer.getContent().add(par8);
		pageSpec.setContent(pageContainer);
		*/
		
		// gets deleted due to penalty -- add a zero-height box
//		pageContent.getContent().add(new PAFlexParagraph("", helvetica, 10));
//		pageContent.getContent().add(new KPGlue(sectionSpacing));  // before heading spacing
																   // necc. for vert. alignment
		/*
		pageContent.getContent().add(heading1par);
		addNonBreakingSpace(pageContent.getContent(), afterHeadingSpacing);
		pageContent.getContent().add(par1);
		pageContent.getContent().add(new KPGlue(paragraphSpacing, 0, 0));
		pageContent.getContent().add(par2);
		pageContent.getContent().add(new KPGlue(sectionSpacing, 0, 0));
		pageContent.getContent().add(heading3par);
		addNonBreakingSpace(pageContent.getContent(), afterHeadingSpacing);
		pageContent.getContent().add(par3);
		pageContent.getContent().add(new KPGlue(paragraphSpacing, 0, 0));
		pageContent.getContent().add(par4);
		*/
		
//		par1.setAlignment(PAFlexParagraph.ALIGN_FORCE_JUSTIFY);
//		par2.setAlignment(PAFlexParagraph.ALIGN_FORCE_JUSTIFY);
//		par3.setAlignment(PAFlexParagraph.ALIGN_FORCE_JUSTIFY);
//		par4.setAlignment(PAFlexParagraph.ALIGN_FORCE_JUSTIFY);
		
		/*
		PAFlexParagraph newPara = new PAFlexParagraph(
				"This is a case where the name and address fit in nicely with the review.",
				times, 16);
		PAFlexColumnSpec newContainer = new PAFlexColumnSpec
				(PAFlexColumnSpec.FIXED_WIDTH_FIXED_NUM_COLS);
		newContainer.setNoCols(1);
		*/
		
		/*
		PAKPTextBlock llt = newPara.generateBoxGlueItems(1.0f);
		llt.getBoxGlueItems().add(new KPPenalty(100000));
		llt.getBoxGlueItems().add(new KPGlue(0, 100000, 0));
		llt.getBoxGlueItems().add(new KPPenalty(50));
		llt.getBoxGlueItems().add(new KPGlue(16, 0, 0));
		llt.getBoxGlueItems().add(new PAWord("", helvetica, 16, null, 1));
		llt.getBoxGlueItems().add(new KPPenalty(100000));
		llt.getBoxGlueItems().add(new KPGlue(0, 100000, 0));
		llt.getBoxGlueItems().add(new PAWord("Reviewer", helvetica, 16, null, 1));
		llt.getBoxGlueItems().add(new KPPenalty(-100000));
		llt.setAlignment(PAFlexBreakableBlock.ALIGN_FORCE_JUSTIFY);
		
		
//		newContainer.getContent().add(par1);//(newPara);
		newContainer.getContent().add(llt);
		newContainer.getContent().add(new KPGlue(300));
//		newContainer.getContent().add()
//		pageSpec.setContent(newContainer);
		*/
//		System.out.println("new container has items: " + newContainer.getContent().size());
		
		/*
		List<PAFlexLayoutResult> docLayouts = pageSpec.layout(
				(float) (8.25 * 72), (float) (11.75 * 72), 1.00f, true);
		*/
		
		
		// vary between 6 and 30*
		float fontSize = 18;
		for (float i = 10; i <= 32; i += 0.25)
		{
			System.out.println("doceng *************************************************");
			System.out.println("doceng i = " + i);
			PAFlexPageSpec pageSpec = new PAFlexPageSpec();
//			physPage.setWidth((float) (8.25 * 72)); // A4 imperial
//			physPage.setHeight((float) (11.75 * 72));
			pageSpec.setLeftInsideMargin(72f); // 1 inch
			pageSpec.setRightOutsideMargin(72f);
			pageSpec.setTopMargin(72f);
			pageSpec.setBottomMargin(72f);
			
			// calculate width of page in points
			float width = (fontSize * i) + 144;
			
			PAFlexColumn pageContainer = new PAFlexColumn();
			
			// 6 grim 7 vienna 8 konstanz
			PAFlexParagraph par6 =
					new PAFlexParagraph(par6text, times, fontSize);
			
			pageContainer.getContent().add(par6);
//			pageContainer.getContent().add(par7);
//			pageContainer.getContent().add(par8);
			pageSpec.setContent(pageContainer);
			
			List<PAFlexLayoutResult> docLayouts = pageSpec.layout(
					(float) width, (float) ((480/(i+10)) * 72), 1.00f, true);
			
			String csv = ("doceng csv " + i + " " + docLayouts.size() + " ");
			boolean set = false;
			int smallestLayoutSize = -1, optimumLayoutSize = -1, largestLayoutSize = -1;
			float smallestLayoutDemerits = 0, optimumLayoutDemerits = 0, largestLayoutDemerits = 0;
			
			
			
			int index = -1;
			int pageIndex = 1;
			for (PAFlexLayoutResult layout : docLayouts)
			{
				index ++;
				System.out.println("doceng layout " + index + " with demerits: " + layout.getDemerits() + " pages: " + pageIndex + " to: " + (layout.getItems().size() + pageIndex - 1));
				
				
				PAPhysPage physPage = (PAPhysPage)layout.getItems().get(0);
				PAPhysColumn physCol = (PAPhysColumn)physPage.getItems();
				PAPhysTextBlock tb = (PAPhysTextBlock)physCol.getItems().get(0);
				List<PAPhysTextLine> lines = tb.getItems();
				
				
				
				float demerits = 0;
				int line = 0;
				for (PAPhysTextLine patl : lines)
				{
					line ++;
					System.out.println("doceng l" + line + " d: " + patl.demerits);
					demerits += patl.demerits;
				}
				
				System.out.println("doceng noLines: " + lines.size() + " with demerits: " + demerits);
				
				//System.out.println("doceng height: " + ((PAPhysObject)layout.getItems().get(0).getContent().getItems().get(0)).getHeight());
																						//pg
				
				for (PAPhysContainer page : layout.getItems())
				{
					((PAPhysPage)page).render(document);
					pageIndex ++;
				}
				System.out.println("finished render, pageIndex is now: " + pageIndex);
				
				if (!set)
				{
					smallestLayoutSize = lines.size();
					optimumLayoutSize = lines.size();
					largestLayoutSize = lines.size();
					
					smallestLayoutDemerits = demerits;
					optimumLayoutDemerits = demerits;
					largestLayoutDemerits = demerits;
					
					set = true;
				}
				else
				{
					if (lines.size() < smallestLayoutSize)
					{
						smallestLayoutSize = lines.size();
						smallestLayoutDemerits = demerits;
					}
					else if (lines.size() > largestLayoutSize)
					{
						largestLayoutSize = lines.size();
						largestLayoutDemerits = demerits;
					}
					
					if (demerits < optimumLayoutDemerits)
					{
						optimumLayoutSize = lines.size();
						optimumLayoutDemerits = demerits;
					}
				}
				
			}
			csv = csv + smallestLayoutSize + " " + smallestLayoutDemerits + " " +
					optimumLayoutSize + " " + optimumLayoutDemerits + " " +
					largestLayoutSize + " " + largestLayoutDemerits;
			System.out.println(csv);
		}
		
		/*
		int index = -1;
		int pageIndex = 1;
		for (PAFlexLayoutResult layout : docLayouts)
		{
			index ++;
			System.out.println("layout " + index + " with demerits: " + layout.getDemerits() + " pages: " + pageIndex + " to: " + (layout.getItems().size() + pageIndex - 1));
			
			for (PAPhysObject page : layout.getItems())
			{
				((PAPhysPage)page).render(document);
				pageIndex ++;
			}
			System.out.println("finished render, pageIndex is now: " + pageIndex);
		}
		*/
		
		/*
		List<? extends PAPhysObject> pages = pageSpec.layout(
				(float) (8.25 * 72), (float) (16.75 * 72), 1.00f, true).get(0).getItems();
//				(float) (8.25 * 72), (float) (11.75 * 72), 1.00f);
//				(float) (5.875 * 72), (float) (8.25 * 72), 1.00f);
		
		for (PAPhysObject page : pages)
		{
			((PAPhysPage)page).render(document);
		}
		
		PAPhysPage firstPage = (PAPhysPage)(pages.get(0));
		*/
		
		/*
		System.out.println("Hello World");
		System.out.println("width: " + calculateWidth("Hello World", font, true));
		System.out.println("width: " + calculateWidth("Hello World", font, false));
		System.out.println("width: " + calculateWidth("aa", font, true));
		System.out.println("width: " + calculateWidth("aa", font, false));
		System.out.println("width: " + calculateWidth("AW", font, true));
		System.out.println("width: " + calculateWidth("AW", font, false));
		
		font = new PDType1Font("Cantarell");
	
		PDType1Font t1font = (PDType1Font)font;
		
		
		byte[] bytes = "a".getBytes("ISO-8859-1");
		System.out.println("a width: " + font.getFontWidth('a'));
		List l = font.getWidths();
		System.out.println("a width: " + font.getFontWidth(bytes, 0, 1));
//		System.out.println("a width: " + t1font.getAFM());
		System.out.println("avg width: " + font.getAverageFontWidth());
		System.out.println("spc width: " + font.getSpaceWidth());
		
//		Object afm = font.get
		
		
		
//		t1font.get
//		FontMetric fm = t1font.getAFM();
		*/
		
		
		// hack to access private methods
		// http://stackoverflow.com/questions/18496325/better-to-use-reflection-or-my-little-hack-to-access-a-private-method
		/*
		Method method = t1font.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getAFM");
		method.setAccessible(true);
		FontMetric metric = (FontMetric)method.invoke(t1font);
		*/
		/*
		System.out.println("kp: " + metric.getKernPairs().size());
		System.out.println("kp0: " + metric.getKernPairs0().size());
		System.out.println("kp1: " + metric.getKernPairs1().size());
		*/
		/*
		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
	
		// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
		contentStream.beginText();
		contentStream.setFont( font, 12 );
		contentStream.moveTextPositionByAmount( 100, 700 );
		contentStream.drawString( "Hello World AWAWAWnew text" );
		contentStream.drawString( "new text" );
		contentStream.endText();
		*/
		
		// Make sure that the content stream is closed:
//		contentStream.close();
	
		// Save the results and ensure that the document is properly closed:
		document.save( "Hello World.pdf");
		document.close();
	}
	
	protected static void addNonBreakingSpace(List<PAFlexObject> content, float amount)
	{
		content.add(new KPPenalty(10000)); // do NOT break here
		
		content.add(new KPGlue(amount, 0, 0)); // 6pt space
		
		content.add(new KPPenalty(10000)); // do NOT break here
	}
	
	public static String[] hyphenate(String[] words)
	{
		// TODO: implement!
		return words;
	}
	
	public float getKerningAdjustment(PDType1Font t1font, String char1, String char2)
	{
		// hack to access private methods
		// http://stackoverflow.com/questions/18496325/better-to-use-reflection-or-my-little-hack-to-access-a-private-method
		// TODO: use a quicker way to access the hidden variable
		
		float retVal = 0; // if no pair found, or something fails, return 0
		
		try 
		{
			Method method = t1font.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getStandard14AFM");
			method.setAccessible(true);
			FontMetrics metric = (FontMetrics)method.invoke(t1font);
			
			// TODO: quicker way of searching through list!
			for (KernPair kp : metric.getKernPairs())
			{
				if (kp.getFirstKernCharacter().equals(char1) &&
					kp.getSecondKernCharacter().equals(char2))
					retVal = kp.getX();
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			System.err.println("Problem finding kerning pairs of "+ t1font);
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	public static float calculateWidth(String s, PDType1Font t1font, boolean kerning)
	{
		float retVal = -1;
		try 
		{
			// hack to access private methods
			// http://stackoverflow.com/questions/18496325/better-to-use-reflection-or-my-little-hack-to-access-a-private-method
			Method method = t1font.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getAFM");
			method.setAccessible(true);
			FontMetric metric = (FontMetric)method.invoke(t1font);
			
			retVal = 0;
			for (int i = 0; i < s.length(); i ++)
			{
				// TODO: adapt for multibyte encodings later
				// can also use: t1font.getFontWidth(byte[], offset, length)
				String charString = s.substring(i, i + 1);
				retVal += metric.getCharacterWidth(charString);
				
				// look for kerning pairs
				if (i < (s.length() - 1) && kerning)
				{
					String nextChar = s.substring(i + 1, i + 2);
					float kernValue = 0;
					// TODO: hash!
					for (KernPair kp : metric.getKernPairs())
					{
						if (kp.getFirstKernCharacter().equals(charString) &&
							kp.getSecondKernCharacter().equals(nextChar))
							kernValue = kp.getX();
					}
					retVal += kernValue;
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			System.err.println("Problem obtaining metrics of "+ t1font);
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	// TODO: return only the best ...
	/*
	public static PAPhysColumn setWordsIntoLines
		(PAFlexParagraph par, float width, PDType1Font font, boolean kern)
	{
//		System.out.println("one");
//		List<PAPhysParagraph> retVal = new ArrayList<PAPhysParagraph>();
//		retVal.setWidth(width);
		
		List<PartlySetParagraph> states = new ArrayList<PartlySetParagraph>();
		
		// create first blank state
		PartlySetParagraph firstState = new PartlySetParagraph(width);
		firstState.unusedWords.addAll(par.getContent());
		states.add(firstState);
		
		// calculate width of each word and store in hash
		HashMap<PALogWord, Float> wordWidths = new HashMap<PALogWord, Float>();
		float largestWordWidth = 0;
		for (PALogWord w : par.getContent())
		{
			float wordWidth = calculateWidth(w.toText(), font, kern);
			if (wordWidth > largestWordWidth) largestWordWidth = wordWidth;
			wordWidths.put(w, wordWidth);
		}
		
		// iterate through states
		// until all are full
		boolean loop = true;
		while(loop)
		{
//			System.out.println("two");
			boolean allStatesFull = true;
			List<PartlySetParagraph> newStates = new ArrayList<PartlySetParagraph>();
			for (PartlySetParagraph thisState: states)
			{
//				System.out.println("three");
				// start building the next line
				// build a line object for each number of unused words
				// either till they run out, or exceed the line width
				List<PAPhysTextLine> candLines = new ArrayList<PAPhysTextLine>();
				List<List<PALogWord>> usedWordsByLine = new ArrayList<List<PALogWord>>();
//				HashMap<PAPhysTextLine, Float> lineWidths =	new HashMap<PAPhysTextLine, Float>();
				for (int i = 1; i <= thisState.unusedWords.size(); i ++)
				{
//					System.out.println("four");
					allStatesFull = false;
					PAPhysTextLine candLine = new PAPhysTextLine();
					List<PALogWord> usedWords = new ArrayList<PALogWord>();
					float lineWidth = 0;
					for (int j = 0; j < i; j ++)
					{
//						System.out.println("five");
						// add the word to candLine
						PALogWord thisWord = thisState.unusedWords.get(j);
						String wordText = thisWord.getContent();
						candLine.words.add(wordText);
						usedWords.add(thisWord);
//						lineWidth += calculateWidth(wordText, font, true);
						lineWidth += wordWidths.get(thisWord);
						
						if (j > 0) // add the width of a space
							lineWidth += font.getSpaceWidth();
					}
					if (lineWidth <= width)
					{
						candLine.width = lineWidth;
						candLines.add(candLine);
						usedWordsByLine.add(usedWords);
//						lineWidths.put(candLine, lineWidth);
					}
					else i = thisState.unusedWords.size() + 1; // break out of loop
				}
				
				// calculate "badness" of the most-filled line
				// and remove everything with badness > 2.5 * this figure
				// TODO: implement this heuristic!
				
				// TODO: non-optimal "equivalents" removal
				
				if (thisState.unusedWords.size() > 0) // if state not already full
				{
					if (candLines.size() < 1)
						System.err.println("Line did not fit");
						// TODO: break the line arbitrarily
						// or do a LaTeX and overfill the box anyway
					
					// add fullest line to state
					// happens after forking due to duplicate
					
					// fork the state for all remaining candLines
					for (int i = candLines.size() - 2; i >= 0; i --)
					{
						PAPhysTextLine newLine = candLines.get(i);
						
						// check for badness
//						if (lineWidths.get(newLine) >= (width - largestWordWidth))
						if (newLine.width  >= (width - largestWordWidth))
						{
							PartlySetParagraph newState = new PartlySetParagraph(width);
							newState.setLines.addAll(thisState.setLines);
							newState.setLines.add(newLine);
							newState.unusedWords.addAll(thisState.unusedWords);
							newState.unusedWords.removeAll(usedWordsByLine.get(i));
							newState.calcMaxBadness();

							newStates.add(newState);
						}
					}
					
					// add fullest line to state
					thisState.setLines.add(candLines.get(candLines.size() - 1));
					thisState.unusedWords.removeAll(usedWordsByLine.get(usedWordsByLine.size() - 1));
					thisState.calcMaxBadness();
				}
			}
			states.addAll(newStates);
			
			// clean up unlikely and impossible states
			List<PartlySetParagraph> statesToRemove = new ArrayList<PartlySetParagraph>();
			
//			System.out.println("one");
			
			// if two or more states have the same unused words (incl. hyphenations)
			// then take only the best one
			boolean repeat = true;
			while(repeat)
			{
				repeat = false;
				boolean changeMade = false;
				for (PartlySetParagraph s1 : states)
				{
					if (!statesToRemove.contains(s1)) // not removed already
					{
//						System.out.println("two");
						for (PartlySetParagraph s2 : states)
						{
							if (!statesToRemove.contains(s1) && // not removed already
								!statesToRemove.contains(s2) &&
								s1 != s2)
							{
//								System.out.println("four");
								// same number of unused words TODO: hyphenation
								if (s1.unusedWords.size() == s2.unusedWords.size())
								{
//									System.out.println("s2.maxBadness: " + s2.maxBadness);
//									System.out.println("s1.maxBadness: " + s1.maxBadness);
									
									boolean removeS2 = false;
									
									if (s2.maxBadness > s1.maxBadness)
										removeS2 = true;
									if (s2.maxBadness == s1.maxBadness)
									{
										// if same maxbadness, remove one with shorter
										// last list
										
										// in practice, this means for any two states of
										// equal badness, prefer the words being set
										// earlier to later
										
										PAPhysTextLine s1LastLine =
											s1.setLines.get(s1.setLines.size() - 1);
										PAPhysTextLine s2LastLine =
											s2.setLines.get(s2.setLines.size() - 1);
										
										if (s2LastLine.words.size() > s1LastLine.words.size())
											removeS2 = true;
									}
									if (removeS2)
									{
//										System.out.println("six");
										// if the other way round, will be removed
										// on another iteration
										statesToRemove.add(s2);
										
										// and set changeMade
										changeMade = true;
									}
								}
							}
						}
					}
						
				}
				if (changeMade) repeat = true;
			}
			
			for (PartlySetParagraph str : states)
			{
				System.out.println("exists state " + str.hashCode() + " with badness: " + str.maxBadness + 
						" and lines: " + str.setLines.size());
				for (PAPhysTextLine l : str.setLines)
					System.out.println("  " + l.toText() + " badness: " + (width - l.width));
			}
			for (PartlySetParagraph str : statesToRemove)
				System.out.println("removing state " + str.hashCode() + " with badness: " + str.maxBadness + 
					" and lines: " + str.setLines.size());
			
			states.removeAll(statesToRemove);
			
			// truncate list to 256 best elements
			
//			if (false)
			if (states.size() > 256)
			{
				Collections.sort(states, new StateComparator());
				states = new ArrayList<PartlySetParagraph>(states.subList(0, 256));
			}
			
//			System.out.println("eight");
			
			// TODO: extend to hyphenations; check that the first unused word
			// is indeed identical (will be a different object)
			
			// the above method should also take care of states at the same
			// point but with extra lines
			
			// if necessary, perform further optimizations to remove unlikely
			// possibilities
			
			System.out.println("states: " + states.size());
			
			if (allStatesFull) loop = false;
		}
		// LATER: delete clearly non-optimal states
		
//		for (PartlySetParagraph state : states)
//		{
//			PAPhysParagraph newPar = new PAPhysParagraph();
//			newPar.items.addAll(state.setLines);
//			retVal.add(newPar);
//			System.out.println("newPar: " + newPar);
//		}
		
		System.out.println("number of results: " + states.size());
		
		if (states.size() <= 256) // otherwise already sorted
			Collections.sort(states, new StateComparator());
		
		for (PartlySetParagraph p : states)
		{
			System.out.println("badness: " + p.maxBadness + " lines: " + p.setLines.size());
			for (PAPhysTextLine l : p.setLines)
				System.out.println(l);
		}
		
		PAPhysColumn newPar = new PAPhysColumn();
		newPar.items.addAll(states.get(0).setLines);
		
		for (PAPhysTextLine l : states.get(0).setLines)
			System.out.println("setLine: " + l + " badness: " + (width - l.width));
		
		return(newPar);
	}

*/

	// TODO: static method for finding width of a text
	
	public void drawParagraphText(PDPageContentStream contentStream, 
		StringBuffer text, GenericSegment bBox)
	{
		// Overall structure -- how to do the hierarchy?
		
		// METHOD: SetWordsIntoLines(float width)
		
		// ignore hyphenation possibilities for now ...
		
		// a "state" consists of
		// list of PAPhysTextLines
		// list of unused words
		// LATER: remaining hyphenation
		
		// create empty list of possibilities
		// repeat until (all possibilities have no more unused words)
		
		// METHOD: JustifyParagraph
		// ? - perhaps this should be part of the Paragraph class?
		
		// LATER: TODO
		
		// Plan
				// create method to flow text given a bounding box with following params
					// bool fit to box
					// bool use kerning pairs
					// bool optimize
					// float alterSize         0..1
					// float alterLeading
					// float alterTracking
					// float alterWordSpacing
					// float alterCharWidth
				
					// split into words, put them into an array
		String[] words = text.toString().split("\\s+");
		
					// work letter-by-letter and use TJ instruction
		
					// iterate through array
		
					// failsafe methods in case no metric information available
	}
}

/*
class PartlySetParagraph
{
	List<PAPhysTextLine> setLines = new ArrayList();
	List<PALogWord> unusedWords = new ArrayList();
	float width;
	float maxBadness;
	
	public PartlySetParagraph(float width)
	{
		this.width = width;
	}
	
	public void calcMaxBadness()
	{
		float retVal = 0;
		if (setLines.size() <= 1)
		{
			maxBadness = retVal;
			return;
		}
		PAPhysTextLine lastLine = setLines.get(setLines.size() - 1);
		for (PAPhysTextLine l : setLines)
		{
			if (l != lastLine)
			{
				float lineBadness = width - l.width;
				if (lineBadness > retVal) retVal = lineBadness;
			}
		}
		maxBadness = retVal;
	}
	
//	public float maxBadness()
//	{
//		float retVal = -1;
//		for (PAPhysTextLine l : setLines)
//		{
//			float lineBadness = width - l.width;
//			if (lineBadness > retVal) retVal = lineBadness;
//		}
//		return retVal;
//	}
}

class StateComparator implements Comparator<PartlySetParagraph>
{
	public int compare(PartlySetParagraph s1, PartlySetParagraph s2)
	{
		// return elements in order
		if (s1.setLines.size() < s2.setLines.size())
		{
			return -1;
		}
		else if (s1.setLines.size() == s2.setLines.size())
		{
			if (s1.maxBadness < s2.maxBadness)
				return -1;
			else if (s1.maxBadness == s2.maxBadness)
				return 0;
			else return 1;
		}
		else return 1;
	}

	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}
}
*/
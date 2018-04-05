package com.tamirhassan.publisher;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.tamirhassan.publisher.knuthplass.KPBox;
import com.tamirhassan.publisher.knuthplass.KPGlue;
import com.tamirhassan.publisher.knuthplass.KPItem;
import com.tamirhassan.publisher.knuthplass.KPPenalty;
import com.tamirhassan.publisher.knuthplass.KnuthPlass;
import com.tamirhassan.publisher.model.PAFlexParagraph;
import com.tamirhassan.publisher.model.PAPhysTextLine;

import au.id.pbw.hyfo.hyph.HyphenationTree;
import au.id.pbw.hyfo.hyph.HyphenationTreeCache;
import au.id.pbw.linefold.AttributedStringWithText;
import au.id.pbw.linefold.ConstantWidthSource;
import au.id.pbw.linefold.JustifyMode;
import au.id.pbw.linefold.LineBox;
import au.id.pbw.linefold.LineBreakMode;
import au.id.pbw.linefold.LineFoldException;
import au.id.pbw.linefold.Paragraph;
import au.id.pbw.linefold.ParagraphFactory;
import au.id.pbw.linefold.TextGenerator;
import au.id.pbw.linefold.tests.ParagraphTest;
import au.id.pbw.linefold.tests.TextGeneratorTester;

public class LineFitter
{
	// http://docs.oracle.com/javase/7/docs/api/java/awt/font/TextAttribute.html
	// adjust tracking
	// TURN KERNING ON!
	// ligatures later ...
	// edit glyph vector to alter space width ...
	// ideally confirm with 2D Java rendering ...
	
	FontRenderContext frc = new FontRenderContext( null, true, true );
	// antialiased; fractional metrics
	
	boolean HYPHENATE = true;
	boolean NORMALIZE = true;
	boolean COMPLEX_TEXT = true;
	
	private TextGenerator generator = new TextGeneratorTester();
	
	private String arial = "/home/tam/Downloads/tempfonts/arial.ttf";
    private String arial_it = "/home/tam/Downloads/tempfonts/ariali.ttf";
    private String micross = "/home/tam/Downloads/tempfonts/micross.ttf";
    private String lucida_uni = "/home/tam/Downloads/tempfonts/l_10646.ttf";
    private String raghu = "/home/tam/Downloads/tempfonts/raghu.ttf";
    private String luxi = "/home/tam/Downloads/tempfonts/luxirr.ttf";
    private String luxi_it = "/home/tam/Downloads/tempfonts/luxirri.ttf";
    private Font basefont;
    private Font arial_font;
    private Font arial16_font;
    private Font arial_it_font;
    private Font arial16_it_font;
    private Font luc_uni_font;
    private Font micross_font;
    private Font micross16_font;
    private Font raghu_font;
    private Font luxi_font;
    private Font luxi16_font;
    private Font luxi_it_font;
    private Font luxi16_it_font;
    
    private String devangari = "\u0936\u093e\u0902\u0924\u093f";
    private String lawrence = "\u0644\u0648\u0631\u0627\u0646\u0633\u0627\u0644\u0639\u0631\u0628";
    private String nina = "\u05d4\u05d0\u05e1\u05d5\u05e0\u05d5\u05ea \u05e9\u05dc \u05e0\u05d9\u05e0\u05d4 ";
    private String tiger = "\u0e1f\u0e49\u0e32\u0e17\u0e30\u0e25\u0e32\u0e22\u0e42\u0e08\u0e23";
	private String text1 = " Although I am not disposed to maintain that the being born in a" +
            " workhouse, is in itself the most fortunate and enviable " +
            "circumstance that can possibly befall a human being, I do mean to " +
            "say that in this particular instance, it was the best thing for " +
            "Oliver Twist that could by possi";
    private String text2 = "bility have occurred.  The fact " +
            "is, that there was considerable difficulty in inducing Oliver to " +
            "take upon himself the office of respiration,--a troublesome " +
            "practice, but one which custom has rendered necessary to our easy " +
            "existence; and for some time he lay gasping on a little flock " +
            "mattress, rather unequally poised between this world and the " +
            "next: the balance being decid";
    private String text3 = "edly in favour of the latter." +
            " Now, " +
            "if, during this brief period, Oliver had been surrounded by " +
            "careful grandmothers, anxious aunts, experienced nurses, and " +
            "doctors of profound wisdom, he would most inevitably and " +
            "indubitably have been killed in no time.";
    private String text4 =
            " There being nobody by, " +
            "however, but a pauper old woman, who was rendered rather misty by " +
            "an unwonted allowance of beer; and a parish surgeon who did such " +
            "matters by contract; Oliver and Nat";
    private String text5 =  "ure fought out the point " +
            "between them." +
            " The result was, that, after a few struggles, " +
            "Oliver breathed, sneezed, and proceeded to advertise to the " +
            "inmates of the workhouse the fact of a new burden having been " +
            "imposed upon the parish, by set";
    private String text6 = "ting up as loud a cry as could " +
            "reasonably have been expected from a male infant who had not been " +
            "possessed of that very useful appendage, a voice, for a much " +
            "longer space of time than three minutes and a quarter.";
    
    private ParagraphFactory factory;
    
    private Map<Attribute, Object> attributes = new HashMap<Attribute, Object>( 6 );
	
	
	void setUp_factory_with_args(
            FontRenderContext frc, Locale locale, boolean hyphenate, boolean normalize,
            boolean complex_text, JustifyMode justify, LineBreakMode break_mode )
    throws Exception {
		
        File font_file = new File( luxi );
        basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
        luxi_font = basefont.deriveFont( 12f );
        luxi16_font = basefont.deriveFont( 16f );
        font_file = new File( luxi_it );
        basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
        luxi_it_font = basefont.deriveFont( 12f );
        luxi16_it_font = basefont.deriveFont( 16f );
        font_file = new File( arial );
        basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
        arial_font = basefont.deriveFont( 12f );
        arial16_font = basefont.deriveFont( 16f );
        font_file = new File( arial_it );
        basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
        arial_it_font = basefont.deriveFont( 12f );
        arial16_it_font = basefont.deriveFont( 16f );
        font_file = new File( lucida_uni );
        basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
        luc_uni_font = basefont.deriveFont( 12f );
        font_file = new File( raghu );
        basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
        raghu_font = basefont.deriveFont( 12f );
        font_file = new File( micross );
        basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
        micross_font = basefont.deriveFont( 12f );
        micross16_font = basefont.deriveFont( 16f );
        
        // Note complex_text
        factory = new ParagraphFactory( frc, locale, hyphenate, normalize,
                complex_text, justify, break_mode );
        /*
        attributes.put( TextAttribute.FONT, raghu_font );
        factory.add_text( devangari, attributes, generator, new Locale("Hindi") );
        attributes.put( TextAttribute.FONT, micross16_font );
        factory.add_text( lawrence, attributes, generator, new Locale("Arabic") );
        attributes.put( TextAttribute.FONT, micross16_font );
        factory.add_text( nina, attributes, generator, new Locale("Hebrew") );
        attributes.put( TextAttribute.FONT, micross16_font );
        factory.add_text( tiger, attributes, generator, new Locale("Thai") );
        */
        attributes.put( TextAttribute.FONT, luxi16_it_font );
        factory.add_text( text1, attributes, generator );
        attributes.put( TextAttribute.FONT, luxi16_font );
        factory.add_text( text2, attributes, generator );
        attributes.put( TextAttribute.FONT, arial16_it_font );
        factory.add_text( text3, attributes, generator );
        attributes.put( TextAttribute.FONT, arial16_font );
        factory.add_text( text4, attributes, generator );
        attributes.put( TextAttribute.FONT, arial16_it_font );
        factory.add_text( text5, attributes, generator );
        
//        attributes = new HashMap<Attribute, Object>( 6 );
        attributes.put( TextAttribute.FONT, arial16_font );
        factory.add_text( text6, attributes, generator );
    }


	public ParagraphFactory getFactory() {
		return factory;
	}


	public void setFactory(ParagraphFactory factory) {
		this.factory = factory;
	}
	
	public LineFitter() throws Exception
	{
//		setUp_factory_with_args( frc, Locale.getDefault(),
//				HYPHENATE, NORMALIZE, COMPLEX_TEXT, 
//				JustifyMode.JUSTIFY, LineBreakMode.TEX_NEW );
		
		factory = new ParagraphFactory( frc, Locale.getDefault(),
				HYPHENATE, NORMALIZE, COMPLEX_TEXT, 
				JustifyMode.JUSTIFY, LineBreakMode.TEX_NEW );
	}
	
	// finds the required box-filling ratio to fit with the given space
	/*
	public PAPhysColumn layoutParagraphFitHeight(PALogParagraph logPar, float width, float height)
			throws IOException, LineFoldException, FontFormatException
	{
		// first try
		PAPhysColumn initPar = layoutParagraphFitWidth(logPar, width, 1.0f);
		
		int actualLines = initPar.items.size();

		// last line fill ratio
		
		// there is a quicker way to calculate this
		// but this method also works for non-uniform line heights
		int targetLines = 0;
		for (PAPhysTextLine l : initPar.items)
		{
			
		}
		
		
		return initPar; // temp -- so it compiles
	}
	*/
	
	// TODO: start with width
	// find longest line
	// calculate real width based on that
	// redo with adjusted width
	public List<PAPhysTextLine> layoutParagraphFitWidth(
			PAFlexParagraph logPar, float width, float adjRatio) 
			throws IOException, LineFoldException, FontFormatException
	{
		// first try
		List<PAPhysTextLine> initPar = layoutParagraphAbsWidth(logPar, width, width);
		
		// return if one line or less (no "full" line possible)
		if (initPar.size() < 2) return initPar;
		
		// find longest line
		int longestLine = 0;
		for (PAPhysTextLine physLine : initPar)
		{
			int lineWidth = physLine.contentWidth(false); // calculates
			if (lineWidth > longestLine)
				longestLine = lineWidth; 
		}
		
		float longestLineWidth = longestLine * (logPar.fontSize / 1000);
		
		// calculate difference achieved width/target width
		float widthRatio = width / longestLineWidth;
		
//		System.out.println("width: " + width + " longestLineWidth: " + longestLineWidth + " old widthRatio: " + widthRatio);
		
		List<PAPhysTextLine> newPar = layoutParagraphAbsWidth(
				logPar, width * widthRatio * adjRatio, width);

		// return initPar;
		return newPar;
	}
	
	public static List<PAPhysTextLine> layoutParagraphKPTest(
			PAFlexParagraph logPar, float width, float scaleFactor) 
			throws IOException, LineFoldException, FontFormatException
	{
		Locale locale = Locale.US;
//		Locale locale = Locale.getDefault();
		
		// this was a class variable
		HyphenationTreeCache hyphen_trees =
                HyphenationTreeCache.get_cache_instance();
		
		HyphenationTree hyphen_tree =
                hyphen_trees.get_hyphenation_tree(locale.toString());
				
		List<KPItem> kpItems = logPar.toBoxGlueModel(hyphen_tree, scaleFactor); // TODO with hash
		HashMap<KPItem, String> boxMap = logPar.getBoxMap();
		
		KnuthPlass kp = new KnuthPlass(kpItems);
		List<List<KPItem>> fitLines = kp.fitLines(width, 5, 1, -1, -1);
		
		String[] words = logPar.content.split("\\s+");
		
//		int index = 0;
		for (List<KPItem> thisLine : fitLines)
		{
			for (KPItem item : thisLine)
			{
				if (item instanceof KPBox)
				{
//					System.out.print(words[index]);
//					index ++;
					System.out.print(boxMap.get(item));
				}
				else if (item instanceof KPGlue)
				{
					System.out.print(" ");
				}
				else if (item instanceof KPPenalty)
				{
					// TODO: check whether this is at a breakpoint!
					KPPenalty p = (KPPenalty)item;
					if (p.isFlag())
						System.out.print("-");
				}
			}
			System.out.println();
		}
		
		return null; //  TODO
	}

	public List<PAPhysTextLine> layoutParagraphAbsWidth(
			PAFlexParagraph logPar, float reqWidth, float absWidth) 
			throws IOException, LineFoldException, FontFormatException
	{
//		PAPhysColumn retVal = new PAPhysColumn();
		List<PAPhysTextLine> retVal = new ArrayList<PAPhysTextLine>();
		
//		float fontRatio = Toolkit.getDefaultToolkit().getScreenResolution() / 72.0f;
		float fontRatio = 1.0f; // widths are in points -- same unit as fontsize
		Font awtFont = logPar.getFont().getawtFont().deriveFont(logPar.getFontSize() * fontRatio);
		
//		System.out.println("name: " + awtFont.getPSName());
//		System.out.println("size: " + awtFont.getSize2D());
		
		attributes.put( TextAttribute.FONT, awtFont );

//		retVal.width = absWidth;
		
		/*
		File font_file = new File( arial );
		Font basefont = Font.createFont( Font.TRUETYPE_FONT, font_file );
		arial16_font = basefont.deriveFont( 16f );
		attributes.put( TextAttribute.FONT, arial16_font );
		
		GlyphVector a16v = arial16_font.createGlyphVector(frc, "test");
		GlyphVector bv = basefont.createGlyphVector(frc, "test");
        Font pdAwtFont = logPar.getFont().getawtFont();
        GlyphVector pdv = pdAwtFont.createGlyphVector(frc, "test");
		
        System.out.println("a16v font size: " + arial16_font.getSize());
        System.out.println("a16v visual bounds: " + a16v.getVisualBounds());
        System.out.println("bv font size: " + basefont.getSize());
        System.out.println("bv visual bounds: " + bv.getVisualBounds());
        System.out.println("pdv font size: " + pdAwtFont.getSize());
        System.out.println("pdv visual bounds: " + pdv.getVisualBounds());
		
		Font helvFont = logPar.getFont().getawtFont();
		*/
		factory.add_text( logPar.getItems(), attributes, generator );
		Paragraph lfPara = factory.make_paragraph();
		
//		System.out.println("in factory: added text: " + logPar.getContent());
		
		lfPara.set_widths(new ConstantWidthSource(reqWidth));
		lfPara.layout();
		
//		retVal.height = 0.0f;
		
		for (LineBox lb : lfPara.get_optimum_line_box_list())
		{
			// TODO: what about indentations?
			String lbText = lb.get_text();
			
			PAPhysTextLine l = new PAPhysTextLine();
//			System.out.println("line: " + lbText.toString());
			
			String[] words = lbText.toString().split("\\s+");
			
			for (int i = 0; i < words.length; i ++)
			{
				if (i == words.length - 1 && lb.is_hyphenated())
					l.getWords().add(words[i] + logPar.hyphenationChar);
				else
					l.getWords().add(words[i]);
			}

			l.font = logPar.getFont();
			l.fontSize = logPar.getFontSize();
			l.height = logPar.getFontSize();
//			retVal.height += (l.fontSize * retVal.leading);
			l.width = absWidth;
			l.getFontMetric();
			
//			retVal.getItems().add(l);
			retVal.add(l);
			
			// TODO: currently limit ourselves to one font per line
			/*
			GlyphVector gv = lb.get_glyph_vector(0);
			Font font = gv.getFont();
			
			for (int j = lb.get_start_vector_offset(); 
					j < lb.get_end_vector_offset(); j ++)
			{
				
			}
			*/
		}
		
		return retVal;
	}
	
	public List<PAPhysTextLine> layoutParagraphKPWidth(
			PAFlexParagraph logPar, float width, float scaleFactor) 
			throws IOException, LineFoldException, FontFormatException
	{
		List<PAPhysTextLine> retVal = new ArrayList<PAPhysTextLine>();
		
		Locale locale = logPar.getLocale();
		
		// this was a class variable -- perhaps move back?
		HyphenationTreeCache hyphen_trees =
                HyphenationTreeCache.get_cache_instance();
		
		HyphenationTree hyphen_tree =
                hyphen_trees.get_hyphenation_tree(locale.toString());
				
		List<KPItem> kpItems = logPar.toBoxGlueModel(hyphen_tree, scaleFactor); // TODO with hash
		HashMap<KPItem, String> boxMap = logPar.getBoxMap();
		
		KnuthPlass kp = new KnuthPlass(kpItems);
		// TODO: start with (1,1) and try higher tolerance if no
		// possible layout found
		List<List<KPItem>> fitLines = kp.fitLines(width, 1.5f, 1, -1, -1);
		
		// TODO: what about indentations?
		// TODO: replace string with some type of PAPhysObject
		
		
		
		for (List<KPItem> thisLine : fitLines)
		{
//			System.out.println("x new line");
			PAPhysTextLine l = new PAPhysTextLine();
			boolean append = false;
			
			for (KPItem item : thisLine)
			{
				if (item instanceof KPBox)
				{
					if (append)
						l.getWords().set(l.getWords().size() - 1,
								l.getWords().get(l.getWords().size() - 1).
								concat(boxMap.get(item)));
					else
						l.getWords().add(boxMap.get(item));
					append = true;
					System.out.print(boxMap.get(item));
				}
				else if (item instanceof KPGlue)
				{
					append = false;
					System.out.print(" G" + ((KPGlue)item).getStretchability());
				}
				else if (item instanceof KPPenalty)
				{
					KPPenalty p = (KPPenalty)item;
					if (p.isFlag())
					{
						if (append)
							l.getWords().set(l.getWords().size() - 1,
									l.getWords().get(l.getWords().size() - 1).
									concat(logPar.getHyphenationChar()));
						else // this should not happen in practice
							l.getWords().add(logPar.getHyphenationChar());
					}
					System.out.print(" P" + ((KPPenalty)item).getAmount());
				}
			}
			System.out.println();
			
			l.font = logPar.getFont();
			l.fontSize = logPar.getFontSize();
//			height is just the content height, without leading
			l.height = logPar.getFontSize();
//			NOTE: leading is now a function of the source paragraph
//			l.leading += (logPar.lineSpacing - 1) * logPar.fontSize;
			
			l.width = width;
			l.getFontMetric();
			
			retVal.add(l);
		}
			
		return retVal;
	}
}
package com.tamirhassan.publisher.model;

import com.tamirhassan.publisher.knuthplass.KPGlue;

public class PAFlexFigure extends PAFlexFloat
{

	// TODO: currently content is being inherited, but is unused
	
	PAPhysGraphic graphic;
	PAFlexFormattedParagraph caption;
	
	public PAFlexFigure(PAPhysGraphic graphic, PAFlexFormattedParagraph caption)
	{
		this.graphic = graphic;
		this.caption = caption;
	}
	
	@Override
	public String textContent() {
		return(this.getClass().getName() + ": " + caption.textContent());
	}

	public PAFlexLayoutResult layout(float width)
	{
		return this.layout(width, Float.MAX_VALUE); //, -1, -1);
	}
	
	@Override
	public PAFlexLayoutResult layout(float width, float height) 
	{
		// lay out caption first
		// PAFlexLayoutResult captionResult = caption.layout(width);
		PAFlexLayoutResult captionResult = caption.layout(graphic.width);
		
		if (captionResult.getExitStatus() == PAFlexLayoutResult.ESTAT_SUCCESS)
		{
			PAPhysContainer physCaption = captionResult.getResult();
			
			PAPhysColumn innerCol = new PAPhysColumn();
			innerCol.width = graphic.width;
			// TODO: centre result column!
			innerCol.items.add(graphic);
			innerCol.items.add(physCaption);
			// TODO: read glue amount from stylesheet!
			innerCol.items.add(new KPGlue(16));
//			innerCol.markWarning = true;
			innerCol.height = innerCol.contentHeight();
			innerCol.setFlexID(id); // for the time being - TODO: change to figure ID

			// outerCol only for marking red box around figure!
			// TODO: find better solution
			
			/*
			PAPhysColumn outerCol = new PAPhysColumn();
			outerCol.width = width;
			
			outerCol.items.add(innerCol);
			outerCol.items.add(new KPGlue(8)); //TODO: hack
			outerCol.height = outerCol.contentHeight();
//			outerCol.setFlexID(-1);
			*/
			
			// TODO: check height?
//			if (outerCol.contentHeight() <= height)
			if (innerCol.contentHeight() <= height)
			{
				return new PAFlexLayoutResult(innerCol, -1, null, 
						PAFlexLayoutResult.ESTAT_SUCCESS);
			}
			else
			{
				return new PAFlexLayoutResult(innerCol, -1, null, 
						PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT);
			}
		}
		else if (captionResult.getExitStatus() == 
				PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT ||
				captionResult.getExitStatus() == 
				PAFlexLayoutResult.ESTAT_PARTIAL_SUCCESS)
		{
			// caption layout failed due to insufficient height
			
			return new PAFlexLayoutResult(null, -1, null, 
					PAFlexLayoutResult.ESTAT_FAIL_INSUFFICIENT_HEIGHT);
		}
		else 
		{
			// caption could not be laid due to other reason
			// exit status is PAFlexLayoutResult.ESTAT_FAIL
			
			return new PAFlexLayoutResult(null, -1, null, 
					PAFlexLayoutResult.ESTAT_FAIL);
		}
	}
	
}
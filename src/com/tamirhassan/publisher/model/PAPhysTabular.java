package com.tamirhassan.publisher.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Class for tabularly laid out content (e.g. bullet lists)
 * 
 * @author tam
 *
 */
public class PAPhysTabular extends PAPhysContainer // = renderable
{
	List<List<PAPhysContainer>> rows = new ArrayList<List<PAPhysContainer>>();
	
	float colGap;
	float rowGap;
	
	public PAPhysTabular()
	{
		
	}
	
	public PAPhysTabular(float colGap, float rowGap)
	{
		this.colGap = colGap;
		this.rowGap = rowGap;
	}
	
	@Override
	public String textContent() 
	{
		return " PAPhysTabular ";
	}
	
	
	
	public List<List<PAPhysContainer>> getRows() {
		return rows;
	}

	public void setRows(List<List<PAPhysContainer>> rows) {
		this.rows = rows;
	}

	public float getColGap() {
		return colGap;
	}

	public void setColGap(float colGap) {
		this.colGap = colGap;
	}

	public float getRowGap() {
		return rowGap;
	}

	public void setRowGap(float rowGap) {
		this.rowGap = rowGap;
	}

	@Override
	public float contentHeight() 
	{
		int retVal = 0;
		for (List<PAPhysContainer> row : rows)
		{
			float rowHeight = 0;
			
			for (PAPhysContainer cell : row)
			{
				if (cell.getHeight() > rowHeight)
					rowHeight = cell.getHeight();
			}
			
			retVal += rowHeight;
			retVal += rowGap;
		}
		
		return retVal;
	}

	@Override
	public String tagName() 
	{
		return "tabular";
	}
	
	@Override
	public void render(PDPageContentStream contentStream, float x1, float y2)
			throws IOException 
	{
		for (List<PAPhysContainer> row : rows)
		{
			float currX1 = x1;
			float rowHeight = 0;
			
			for (PAPhysContainer cell : row)
			{
				cell.render(contentStream, currX1, y2);
				
				currX1 += cell.getWidth();
				currX1 += colGap;
				
				if (cell.getHeight() > rowHeight)
					rowHeight = cell.getHeight();
			}
			
			y2 -= rowHeight;
			y2 -= rowGap;
		}
	}
}
package com.tamirhassan.publisher.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for tabularly laid out content (e.g. bullet lists)
 * 
 * @author tam
 *
 */
public class PAFlexSimpleTable extends PAFlexIncolObject
{
	List<List<PAFlexIncolObject>> rows = 
			new ArrayList<List<PAFlexIncolObject>>();
	
	// 0 - take up width of text
	// >0 - absolute width
	// -1 (or <0) -> til end of column
	List<Float> colWidths = new ArrayList<Float>();
	
	// horizontal gap between columns
	float colGap = 6;
	float rowGap = 6;
	
	public List<List<PAFlexIncolObject>> getRows() {
		return rows;
	}

	public void setRows(List<List<PAFlexIncolObject>> rows) {
		this.rows = rows;
	}

	public List<Float> getColWidths() {
		return colWidths;
	}

	public void setColWidths(List<Float> colWidths) {
		this.colWidths = colWidths;
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

	public PAFlexSimpleTable()
	{
		
	}
	
	@Override
	public String textContent() 
	{
		return " PAFlexSimpleTable ";
	}

	@Override
	public PAFlexLayoutResult layout(float width) 
	{
		// populate result table with empty list
		PAPhysTabular tab = new PAPhysTabular(colGap, rowGap);
		for (List<PAFlexIncolObject> row : rows)
			tab.rows.add(new ArrayList<PAPhysContainer>());
		
		ArrayList<Float> computedWidths = new ArrayList<Float>();
		
		tab.width = width;
		float cumulativeWidth = 0;
		
		for (int col = 0; col < colWidths.size(); col ++)
		{
			float maxCellWidth = 0; // only relevant when colWidth == 0
			
			// first rasterize all text in this column
			for (int row = 0; row < rows.size(); row ++)
			{
				PAFlexIncolObject cell = rows.get(row).get(col);
				PAFlexLayoutResult res;
				
				float cellWidth;
				float rowHeight = 0;
				
				if (colWidths.get(col) == 0)
				{
					// lay out all text in one line and take the longest
					cellWidth = Float.MAX_VALUE;
				}
				else if (colWidths.get(col) < 0)
				{
					// til end of column
					cellWidth = width - cumulativeWidth;
				}
				else
				{
					// absolute col width
					cellWidth = colWidths.get(col);
				}
				
				// lay out all text in one line and take the longest
				res = cell.layout(cellWidth);
				
				if (res.exitStatus == PAFlexLayoutResult.ESTAT_SUCCESS)
				{
					// TODO: content width even if >1 line?
					float contentWidth = res.getResult().getWidth();
					PAPhysTextBlock tb = (PAPhysTextBlock)res.getResult();
					if (tb.getItems().size() == 1)
						contentWidth = tb.getItems().get(0).contentWidth();
							
					// update maxCellWidth
					if (contentWidth > maxCellWidth)
						maxCellWidth = contentWidth;
					
					// add to result
					tab.rows.get(row).add(res.getResult());
				}
				else
				{
					// failure adding object to table
					//TODO: add empty cell as placeholder?
				}
			}
			
			// determine column width
//			computedWidths.set(col, maxCellWidth);
			computedWidths.add(maxCellWidth);
			
			// update widths of all cells to match maxCellWidth
			for (int row = 0; row < rows.size(); row ++)
				tab.rows.get(row).get(col).width = maxCellWidth;
			
			cumulativeWidth += maxCellWidth;
			cumulativeWidth += colGap;
		}
		
		// calculate the height of the table
		float cumulativeHeight = 0;
		for (int row = 0; row < rows.size(); row ++)
		{
			// calculate the height of each row
			float rowHeight = 0;
			for (PAPhysContainer cell : tab.rows.get(row))
				if (cell.height > rowHeight)
					rowHeight = cell.height;
			
			cumulativeHeight += rowHeight;
			
			if (row < rows.size() - 1) // don't add for final row
				cumulativeHeight += rowGap;
		}
		tab.height = cumulativeHeight;
		tab.width = cumulativeWidth;
		
		tab.setFlexID(id);
		PAFlexLayoutResult retVal = new PAFlexLayoutResult(
				tab, 0.0, null, PAFlexLayoutResult.ESTAT_SUCCESS);
		
		return retVal;
	}
	
}
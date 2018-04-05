package com.tamirhassan.publisher.model;

import java.util.List;


/**
 * Base class for layout objects within a column
 * i.e. laid out to a set width
 * 
 * @author tam
 *
 */
public abstract class PAPhysIncolObject implements PAFlexObject
{	
	public abstract PAFlexLayoutResult layout(float width);

}

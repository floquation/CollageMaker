package editor.data;

import java.awt.Color;

public class ResultXML {
	
	/** Name of the output file (w/o extension) when Result is being generated */
	public String name;
	/** Reference to the file via RefListXML */
	public int orgImageId;
	/** Output size */
	public Size size;
	/** Recipe to generate the image */
	public Grid grid;
	
	public static class Size{
		public int w;
		public int h;
		
		public Size clone(){
			Size out = new Size();
			out.w = w;
			out.h = h;
			return out;
		}
	}
	public static class Grid{
		/** Size of <elements>. Written separately, because <elements>=null before it is generated, whereas the size of the grid is known a priori. */
		public Size size;
		/** List of the images used to generate the result. Null before the algorithm executes. */
		public Element[][] elements;
		
		public static class Element{
			public int imgId;
			/** Color at this grid position of the orgImage */
			public Color color;
		}
	
		public Grid clone(){
			Grid out = new Grid();
			out.size = size.clone();
			if(elements!=null)
				out.elements = elements.clone();
			return out;
		}
	}
	
	
	/**
	 * Constructor sets everything to default values.
	 * Everything is public, so the user may set whatever he needs.
	 * 
	 * grid.elements will remain null. The rest is non-null.
	 * 
	 * @author Kevin van As
	 */
	public ResultXML(){
		name = "generatedResult";
		orgImageId = 4;
		size = new Size();
		size.w = 100;
		size.h = 100;
		grid = new Grid();
		grid.size = new Size();
		grid.size.w = 10;
		grid.size.h = 10;
	}
	
	public ResultXML clone(){
		ResultXML out = new ResultXML();
		out.name = name;
		out.orgImageId = orgImageId;
		out.size = size.clone();
		out.grid = grid.clone();
		return out;
	}
	
}

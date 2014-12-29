package editor.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Map;

import util.ArrayUtils;
import algorithms.collageGenerators.CollageAlgorithmUtils;

public abstract class ImageTools {
		
	/**
	 * Like computeAvgColor(Image img, Rectangle clipRect), but now uses img.srcClip as the rectangle
	 * 
	 * @param img
	 * @return Color : average color in img.srcClip
	 */
	public static Color computeAvgColor(Image img){
		System.out.println("Computing the average color for \"" + img.getName() + "\".");
//		img.generateImage(); //Generates the image, which will automatically correct srcClip if it is incorrect.
		Rectangle imgSrcClip = img.getClipRect();
		return computeAvgColor(img,imgSrcClip);
	}
	
	/**
	 * Credits:
	 * This method is inspired by Mota at http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image.
	 * The method is a factor 100 faster for a 6x8 image than using the method "bi.getData().getPixel(x, y, array);" and ran in only 1E-4s, instead of 0.01s!
	 * 
	 * The method uses "a running average", allowing arbitrarily big images to be used (although it may become very slow, of course - but probably no problem (<<1sec for 1000x1000)).
	 * 
	 * @author Kevin van As
	 * @param img
	 * @return a Color object representing the average Color of the image.
	 */
	public static Color computeAvgColor(Image img, Rectangle clipRect){
		float r=0,g=0,b=0;
		int counter=0;		
		
		//Get the actual image
		BufferedImage bi = img.getImage();
		int w = bi.getWidth();
		int h = bi.getHeight();
		//And convert it to a useful type:
		if(bi.getType() != BufferedImage.TYPE_3BYTE_BGR && bi.getType() != BufferedImage.TYPE_4BYTE_ABGR){
			BufferedImage bi1 = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
			Graphics graphics = bi1.getGraphics();
			graphics.drawImage(bi, 0, 0, null);
			graphics.dispose();
			bi = bi1;
		}		
		
		final byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
	    final boolean hasAlphaChannel = bi.getAlphaRaster() != null;
	    
	    final int isAlphaOffset = hasAlphaChannel ? 1 : 0;
	    final int pixelLength = 3 + isAlphaOffset;
	    
//	    long time1 = System.nanoTime();
	    		
    	int pixelIni = pixelLength*(w*clipRect.y+clipRect.x); //Skip until we reach (x,y)
    	int pixel, imod;
    	for (int i = 0; i<clipRect.w*clipRect.h; i++) { //iterate over pixels in domain
    		imod = i%clipRect.w;
    		pixel = pixelIni + pixelLength*((i-imod)/clipRect.w*w + imod)+isAlphaOffset;
    		
//            System.out.println(img.name + ": (" + i + ") imod = " + imod + "; pixel = " + pixel +
//            		";");

//    		System.out.println(b + ", " + g + ", " + r);
    		
            b = b*(counter/(counter+1f)) + ((int) pixels[pixel] & 0xff)/(counter+1f); // blue
            g = g*(counter/(counter+1f)) + (((int) pixels[pixel+1] & 0xff) )/(counter+1f); // green
            r = r*(counter/(counter+1f)) + (((int) pixels[pixel+2] & 0xff)  )/(counter+1f); // red
            counter++;
        }
    	
    	
//	    System.out.println("Elapsed Time = " + (System.nanoTime()-time1)/1000000000f);
//		time1 = System.nanoTime();
//		
//		//Validation:
//		int[] array = new int[4];		
//		int[] avgArray = new int[4];
//		for(int x = imgSrcClip.srcX; x<(imgSrcClip.srcW+imgSrcClip.srcX); x++){
//			for(int y = imgSrcClip.srcY; y<(imgSrcClip.srcH+imgSrcClip.srcY); y++){
//				bi.getData().getPixel(x, y, array);
//				avgArray[0] += array[0];
//				avgArray[1] += array[1];
//				avgArray[2] += array[2];
//				avgArray[3] += array[3];
//			}
//		}
//		
//		avgArray[0] /= (float) imgSrcClip.srcW*imgSrcClip.srcH;
//		avgArray[1] /= (float) imgSrcClip.srcW*imgSrcClip.srcH;
//		avgArray[2] /= (float) imgSrcClip.srcW*imgSrcClip.srcH;
//		avgArray[3] /= (float) imgSrcClip.srcW*imgSrcClip.srcH;
//		
//		System.out.println("avgArray = " + avgArray[0] + ", " + avgArray[1] + ", " + avgArray[2] + ", " + avgArray[3] + ";");
//
//	    System.out.println("Elapsed Time = " + (System.nanoTime()-time1)/1000000000f);
		
    	
		return new Color((int) r, (int) g, (int) b);
	}

	/**
	 * Like computeAvgColor(ImageRefList.Image img), but now designed to compute a grid of averaged colors.
	 * It automatically writes it to result.grid.elements.
	 * 
	 * The method uses "a running average", allowing arbitrarily big images to be used (although it may become very slow, of course).
	 * 
	 * @author Kevin van As
	 * @param result
	 * @param refList : used to find the appropriate image defined in result.orgImageId
	 * @return void (writes to result)
	 */
	public static void computeAvgColorGrid(ResultXML result, Image img){		
		//Get the outer bounds of "img":
		img.generateImage(); //Generates the image, which will automatically correct srcClip if it is incorrect.
		Rectangle imgSrcClip = img.getClipRect();
		Rectangle computeRect;
		
		//Prepare the result file:
		result.grid.elements = new ResultXML.Grid.Element[result.grid.size.w][result.grid.size.h];
		
		float lastX = imgSrcClip.x;
		float lastY = imgSrcClip.y;
		float elemWidth = ((float)imgSrcClip.w)/result.grid.size.w;
		float elemHeight = ((float)imgSrcClip.h)/result.grid.size.h;
		for(int x = 0; x<result.grid.size.w; x++){
			for(int y = 0; y<result.grid.size.h; y++){
				result.grid.elements[x][y] = new ResultXML.Grid.Element();
				computeRect = new Rectangle(
						(int)lastX,
						(int)lastY,
						(int)(lastX+elemWidth)-(int)lastX, //writing width like this prevents black lines
						(int)(lastY+elemHeight)-(int)lastY // \-> same
						);
				
//				System.out.println("(x,y)=("+x+","+y+"): " + computeRect.toString());
				Color color = computeAvgColor(img, computeRect);
		    	result.grid.elements[x][y].color = color;
		    	
		    	lastY += elemHeight;
			}
			lastY = imgSrcClip.y;
	    	lastX += elemWidth;
		}
	}

	/**
	 * Generates a BufferedImage conforming to the color map of "result": result.grid.element[][].color.
	 * 
	 * @author Kevin
	 * @param result
	 * @return
	 */
	public static BufferedImage generateColorMapImage(ResultXML result){
		BufferedImage newImg = new BufferedImage(result.size.w, result.size.h, BufferedImage.TYPE_INT_RGB);
				
		//Prepare the result file:
		if(result.grid.elements == null){
			return null;
		}
		
		double lastX = 0;
		double lastY = 0;
		double elemWidth = ((double)result.size.w)/result.grid.size.w;
		double elemHeight = ((double)result.size.h)/result.grid.size.h;

		Graphics2D g = newImg.createGraphics();
		
//		System.out.println("(x,y,w,h) = (" + lastX + ", " + lastY + ", " + elemWidth + ", " + elemHeight + ");");
		for(int x = 0; x<result.grid.size.w; x++){
			for(int y = 0; y<result.grid.size.h; y++){
				
				g.setColor(result.grid.elements[x][y].color);
				g.fillRect(
						(int)lastX,
						(int)lastY,
						(int)(lastX+elemWidth)-(int)lastX, //writing width like this prevents black lines
						(int)(lastY+elemHeight)-(int)lastY // \-> same
						);
				
//				System.out.println(new Rectangle((int)lastX,(int)lastY,(int)(lastX+elemWidth)-(int)lastX,(int)(lastY+elemHeight)-(int)lastY));
				
		    	lastY += elemHeight;
			}
			lastY = 0;
	    	lastX += elemWidth;
		}
		
		return newImg;
	}
	

	/**
	 * Generates a BufferedImage conforming to the images of "result": result.grid.element[][].imgID.
	 * --> The Collage!
	 * 
	 * @author Kevin van As
	 * @param refList
	 * @param resultXML
	 * @return the Collage conforming to resultXML and refList
	 */
	public static BufferedImage generateResultImage(ImageRefList refList, ResultXML resultXML){
		BufferedImage newImg = new BufferedImage(resultXML.size.w, resultXML.size.h, BufferedImage.TYPE_INT_RGB);
				
		//Prepare the result file:
		if(resultXML.grid.elements == null){
			return null;
		}
		
		double lastX = 0;
		double lastY = 0;
		double elemWidth = ((double)resultXML.size.w)/resultXML.grid.size.w;
		double elemHeight = ((double)resultXML.size.h)/resultXML.grid.size.h;

		Graphics2D g = newImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
//		System.out.println("(x,y,w,h) = (" + lastX + ", " + lastY + ", " + elemWidth + ", " + elemHeight + ");");
		for(int x = 0; x<resultXML.grid.size.w; x++){
			for(int y = 0; y<resultXML.grid.size.h; y++){
				Image img = refList.getImage(resultXML.grid.elements[x][y].imgId);
				Rectangle srcClip = img.getClipRect();
								
				g.drawImage(img.getImage(),
						(int)lastX,					//dst_x1
						(int)lastY,					//dst_y1
						(int)(lastX+elemWidth),		//dst_x2
						(int)(lastY+elemHeight),	//dst_y2
						srcClip.x,					//src_x1
						srcClip.y,					//src_y1
						srcClip.x+srcClip.w,		//src_x2
						srcClip.y+srcClip.h,		//src_y2
						null						//observer
						);
				img.clearImage();
				
		    	lastY += elemHeight;
			}
			lastY = 0;
	    	lastX += elemWidth;
		}
		
		return newImg;
	}
	

	/**
	 * Generates a BufferedImage conforming to the 'error map' of "result".
	 * 
	 * @author Kevin
	 * @param result
	 * @param double[][] representing a grid of square errors
	 * @return BufferedImage
	 */
	public static BufferedImage generateErrorLinImage(ResultXML result, double[][] errorSq){
		BufferedImage newImg = new BufferedImage(result.size.w, result.size.h, BufferedImage.TYPE_INT_RGB);
				
		//Prepare the result file:
		if(result.grid.elements == null){
			return null;
		}
		
		double lastX = 0;
		double lastY = 0;
		double elemWidth = ((double)result.size.w)/result.grid.size.w;
		double elemHeight = ((double)result.size.h)/result.grid.size.h;
		
		double minError = ArrayUtils.min(errorSq);
		double maxError = ArrayUtils.max(errorSq);
		double dError = maxError-minError;
		System.out.println("(ImageTools) ErrorBounds (min,max) = (" + minError + ", " + maxError + ");");
		
		Graphics2D g = newImg.createGraphics();
		
		for(int x = 0; x<result.grid.size.w; x++){
			for(int y = 0; y<result.grid.size.h; y++){
				int rf, gf, bf = 0;
				
				rf = (int)(255*((errorSq[x][y]-minError)/dError));
				gf = 255-rf;
//				System.out.println("(x,y)=("+x+", "+y+"): (r,g)=("+rf+", "+gf+");");
				
				g.setColor(new Color(rf, gf, bf));
				g.fillRect(
						(int)lastX,
						(int)lastY,
						(int)(lastX+elemWidth)-(int)lastX,
						(int)(lastY+elemHeight)-(int)lastY
						);
				
//				System.out.println(new Rectangle((int)lastX,(int)lastY,(int)(lastX+elemWidth)-(int)lastX,(int)(lastY+elemHeight)-(int)lastY));
				
		    	lastY += elemHeight;
			}
			lastY = 0;
	    	lastX += elemWidth;
		}
		
		return newImg;
	}
	

	/**
	 * Generates a BufferedImage conforming to the log10('error map') of "result".
	 * 
	 * @author Kevin
	 * @param result
	 * @param double[][] representing a grid of square errors
	 * @return BufferedImage
	 */
	public static BufferedImage generateErrorLogImage(ResultXML result, double[][] errorSq){
		BufferedImage newImg = new BufferedImage(result.size.w, result.size.h, BufferedImage.TYPE_INT_RGB);
				
		//Prepare the result file:
		if(result.grid.elements == null){
			return null;
		}
		
		double lastX = 0;
		double lastY = 0;
		double elemWidth = ((double)result.size.w)/result.grid.size.w;
		double elemHeight = ((double)result.size.h)/result.grid.size.h;
		
		double minError = ArrayUtils.minExclZero(errorSq);
		System.out.println("(ImageTools) ErrorBounds (lin, excl 0) = " + minError);
		minError = Math.log10(minError);
		double maxError = Math.log10(ArrayUtils.max(errorSq));
		double dError = maxError - minError;
		System.out.println("(ImageTools) ErrorBounds (min,max) = (" + minError + ", " + maxError + ");");
		
		Graphics2D g = newImg.createGraphics();
		
		for(int x = 0; x<result.grid.size.w; x++){
			for(int y = 0; y<result.grid.size.h; y++){
				int rf, gf, bf = 0;
				
				rf = (int)(255*((Math.log10(errorSq[x][y])-minError)/dError));
				rf = Math.max(0, Math.min(rf, 255)); //clip to correct range, just to be sure
				gf = 255-rf;
//				System.out.println("(x,y)=("+x+", "+y+"): (r,g)=("+rf+", "+gf+");");
				
				g.setColor(new Color(rf, gf, bf));
				g.fillRect(
						(int)lastX,
						(int)lastY,
						(int)(lastX+elemWidth)-(int)lastX,
						(int)(lastY+elemHeight)-(int)lastY
						);
				
//				System.out.println(new Rectangle((int)lastX,(int)lastY,(int)(lastX+elemWidth)-(int)lastX,(int)(lastY+elemHeight)-(int)lastY));
				
		    	lastY += elemHeight;
			}
			lastY = 0;
	    	lastX += elemWidth;
		}
		
		return newImg;
	}
	
	public static BufferedImage generateClippedImage(Image img){
		BufferedImage bi = img.getImage();
		if(bi==null) return null;

		Rectangle srcClip = img.getClipRect();
		
		int width = srcClip.w;
		int height = srcClip.h;
		
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		
		Graphics g = newImg.createGraphics();
		g.drawImage(bi,
				0,					//dst_x1
				0,					//dst_y1
				width,		//dst_x2
				height,	//dst_y2
				srcClip.x,					//src_x1
				srcClip.y,					//src_y1
				srcClip.x+srcClip.w,		//src_x2
				srcClip.y+srcClip.h,		//src_y2
				null						//observer
				);

		img.clearImage();
		
		return newImg;
	}
	
	public static BufferedImage generateUnicityImage(ResultXML result, ImageRefList refList){
		//Map<Integer,Integer> map = CollageAlgorithmUtils.getUnicityMap(result, refList);
		int[][] array = CollageAlgorithmUtils.getGlobalUnicityArray(result, refList);
		if(array == null) return null;
		
		BufferedImage newImg = new BufferedImage(result.grid.size.w, result.grid.size.h, BufferedImage.TYPE_INT_RGB);
		
		double lastX = 0;
		double lastY = 0;
		double elemWidth = 20;//((double)result.size.w)/result.grid.size.w;
		double elemHeight = 20;//((double)result.size.h)/result.grid.size.h;
		
		double minValue = ArrayUtils.min(array);
		double maxValue = ArrayUtils.max(array);
		double dValue = maxValue-minValue;
		System.out.println("(ImageTools) UnicityImageIdBounds (min,max) = (" + minValue + ", " + maxValue + ");");
		
		Graphics2D g = newImg.createGraphics();
		
		for(int x = 0; x<result.grid.size.w; x++){
			for(int y = 0; y<result.grid.size.h; y++){
				int rf, gf, bf = 0;
				
				rf = (int)(255*((array[x][y]-minValue)/dValue));
				gf = 255-rf;
//				System.out.println("(x,y)=("+x+", "+y+"): (r,g)=("+rf+", "+gf+");");
				
				g.setColor(new Color(rf, gf, bf));
				g.fillRect(
						(int)lastX,
						(int)lastY,
						(int)(lastX+elemWidth)-(int)lastX,
						(int)(lastY+elemHeight)-(int)lastY
						);
				
//				System.out.println(new Rectangle((int)lastX,(int)lastY,(int)(lastX+elemWidth)-(int)lastX,(int)(lastY+elemHeight)-(int)lastY));
				
		    	lastY += elemHeight;
			}
			lastY = 0;
	    	lastX += elemWidth;
		}
		
		
		return newImg;
	}
	
}

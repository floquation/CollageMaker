package editor.data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public abstract class ImageTools_backUp {
	
	/**
	 * Credits:
	 * This method is inspired by Mota at http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image.
	 * The method is a factor 60.000 for a 711x538 image than using the method "bi.getData().getPixel(x, y, array);" and ran in only 0.03s!
	 * 
	 * The method uses "live averaging", allowing arbitrarily big images to be used (although it may become very slow, of course).
	 * 
	 * @author Kevin van As
	 * @param bi
	 * @return a Color object representing the average Color of the image.
	 */
	public static Color computeAvgColor(BufferedImage bi){
		float r=0,g=0,b=0;
		int counter=0;
//		int a=255;
		int rgb=0;
		
		
//	    bi = new BufferedImage(10, 10, BufferedImage.TYPE_USHORT_565_RGB);

//	    bi.getGraphics().clearRect(0, 0, 10, 10);
//	    bi.getGraphics().setColor(new Color(255,10,30));
//	    bi.getGraphics().fillRect(4, 4, 3, 3);

		long time1 = System.nanoTime();
		int[] array = new int[4];
//		System.out.println(new Color(bi.getRGB(24, 10)));
//		bi.getData().getPixel(622, 297, array);
//		System.out.println(array[0] + ", " + array[1] + ", " + array[2] + ", " + array[3]);

		
		int[] avgArray = new int[4];
		for(int x = 0; x<bi.getData().getWidth(); x++){
			for(int y = 0; y<bi.getData().getHeight(); y++){
				bi.getData().getPixel(x, y, array);
				avgArray[0] += array[0];
				avgArray[1] += array[1];
				avgArray[2] += array[2];
				avgArray[3] += array[3];
			}
		}
		
		avgArray[0] /= (float) bi.getData().getWidth()*bi.getData().getHeight();
		avgArray[1] /= (float) bi.getData().getWidth()*bi.getData().getHeight();
		avgArray[2] /= (float) bi.getData().getWidth()*bi.getData().getHeight();
		avgArray[3] /= (float) bi.getData().getWidth()*bi.getData().getHeight();
		
		System.out.println("avgArray = " + avgArray[0] + ", " + avgArray[1] + ", " + avgArray[2] + ", " + avgArray[3] + ";");

	    System.out.println("Elapsed Time = " + (System.nanoTime()-time1)/1000000000f);
		time1 = System.nanoTime();
		
		
		final byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
	    final boolean hasAlphaChannel = bi.getAlphaRaster() != null;
	    
	    
	    
	    if (hasAlphaChannel) {
	    	final int pixelLength = 4;
        	for (int pixel = 0; pixel < pixels.length; pixel += pixelLength) { //iterate over bytes (1byte per colorelement)
//	            a = a*counter/(counter+1) + (((int) pixels[pixel] & 0xff) << 24 )/(counter+1); // alpha
	            b = b*counter/(counter+1) + ((int) pixels[pixel+1] & 0xff)/(counter+1); // blue
	            g = g*counter/(counter+1) + (((int) pixels[pixel+2] & 0xff) << 8 )/(counter+1); // green
	            r = r*counter/(counter+1) + (((int) pixels[pixel+3] & 0xff) << 16 )/(counter+1); // red
	            counter++;
        	}
      	} else {
      		final int pixelLength = 3;
	        for (int pixel = 0; pixel < pixels.length; pixel += pixelLength) { //iterate over bytes (1byte per colorelement)
//	            b = b*(counter/(counter+1f)) + ((int) pixels[pixel] & 0xff)/(counter+1f); // blue
//	            g = g*(counter/(counter+1f)) + (((int) pixels[pixel+1] & 0xff) << 8 )/(counter+1f); // green
//	            r = r*(counter/(counter+1f)) + (((int) pixels[pixel+2] & 0xff) << 16 )/(counter+1f); // red
	            b = b*(counter/(counter+1f)) + ((int) pixels[pixel] & 0xff)/(counter+1f); // blue
	            g = g*(counter/(counter+1f)) + (((int) pixels[pixel+1] & 0xff) )/(counter+1f); // green
	            r = r*(counter/(counter+1f)) + (((int) pixels[pixel+2] & 0xff)  )/(counter+1f); // red
	            	            
//	            System.out.print("("+counter+") r,g,b = ");
//	            System.out.print((int)(pixels[pixel+2] & 0xff) + ", ");
//	            System.out.print((int)(pixels[pixel+1] & 0xff) + ", ");
//	            System.out.print((int)(pixels[pixel] & 0xff) + ";");
//	            System.out.println();
//	    	    System.out.println("rgb = "  + r + ", " + g + ", "+ b);
	            counter++;
	        }
      	}
	    
	    System.out.println("Elapsed Time = " + (System.nanoTime()-time1)/1000000000f);
		Color color = new Color((int) r, (int) g, (int) b);
		return color;
	}
	
	
}

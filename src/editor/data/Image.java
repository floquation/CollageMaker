package editor.data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image{
	/**
	 * BufferedImage `bi' is null until asked for. Will generate a non-blocking error message if the file cannot be loaded.
	 */
	private BufferedImage bi;
	
	private String pwd;
	/** Custom name given by the user. Public because changing it has no harm. */
	private String name;
	private Color color;
	
	//Clipping:
	private Rectangle srcClip;
	
	public Image(String pwd, String name, int srcX, int srcY, int srcW, int srcH, Color color){
		this.pwd = pwd;
		setName(name);
		this.srcClip = new Rectangle(srcX,srcY,srcW,srcH);
		this.color = color;
	}
	
	public void setName(String name){
		this.name = name;
		this.name = this.name.replace("\\", "_");
		this.name = this.name.replace("/", "_");
	}
	public String getName(){
		return name;
	}
	public String getPath(){
		return pwd;
	}
	
	public Color getColor(){
		if(color!=null)
			return color;
		
		recomputeImage();
		return color;
	}
	
	/**
	 * Returns a Rectangle object {contain srcX,srcY,srcW,srcH}.
	 * It is a copy, so any changes to it does not alter the Image object.
	 * @return Rectangle
	 */
	public Rectangle getClipRect(){
		return srcClip.clone();
	}
	
	public BufferedImage getImage(){
		if(bi==null)
			generateImage();
		
		return bi;
	}
	
	/**
	 * Releases system resources by disposing the reference to the BufferedImage instance.
	 */
	public void clearImage(){
		if(bi!=null){
			bi.flush();
			bi = null;
		}
	}
	
	/**
	 * Disposing the current BufferedImage instance and reloads the image from file.
	 */
	public void generateImage(){
		System.out.println("(Image) Starting loading image from file: \"" + pwd + "\".");
		if(bi!=null){
			bi.flush();
			bi = null;
		}
		
		File file = new File(pwd);
		if(file.exists()){
			try {
				bi = ImageIO.read(file);
				if(bi==null){
					System.err.println("File \"" + pwd + "\" is not a valid image file. Are you sure it is an image? Cannot load image.\n" +
							"If this error occurs during loading of a whole FileSystem, it probably means there are non-image files there -> Ignore this message.");
				}else{
					//Success!!
				}
			} catch (IOException e) {
				if(file.isDirectory()){
					System.err.println("File \"" + pwd + "\" is a directory. Cannot load image.");
				}else{
					System.err.println(e.getMessage());
				}
			}				
		}else{
			System.err.println("File \"" + pwd + "\" not found. Cannot load image.");
		}
	}
	
	public void recomputeImage(){
		BufferedImage bi = getImage();
		if(bi==null){
			//Errors should already have been raised by the getImage() method.
		}else{
			ensureClipIsCorrect(srcClip);
			color = ImageTools.computeAvgColor(this);
		}
	}
	
	/**
	 * Crops the current srcClip into the image
	 * 
	 * @author Kevin van As
	 * @param Rectangle representing the srcClip. This parameter will be changed by this method.
	 */
	private void ensureClipIsCorrect(Rectangle srcClip){
		BufferedImage bi = this.getImage(); //ensure it is loaded
		srcClip.x = Math.min(Math.max(srcClip.x, 0),bi.getWidth());
		srcClip.y = Math.min(Math.max(srcClip.y, 0),bi.getHeight());
		srcClip.w = -srcClip.x + Math.min(Math.max(srcClip.w+srcClip.x, 0),bi.getWidth());
		srcClip.h = -srcClip.y + Math.min(Math.max(srcClip.h+srcClip.x, 0),bi.getHeight());		
	}
	
	/**
	 * Overrides the srcClip parameter.
	 * 
	 * @author Kevin van As
	 * @param clipRct
	 */
	public void setClip(Rectangle clipRct){
		if(clipRct != null){
			srcClip = clipRct;
			 //must be re-computed, but only do it now if it is quick to do so
			if(bi==null){ //slow
				color=null;
			}else{ //quick
				recomputeImage();
			}
		}
	}
	
	/**
	 * Sets the "srcClip" parameter to the maximum dimensions possible.
	 * 
	 * WARNING: This requires BufferedImage to be loaded, which is a very slow operation.
	 * If it is already loaded, this method is, however, instant.
	 * 
	 * @author Kevin van As
	 */
	public void maximizeClip(){
		BufferedImage bi = getImage();
		if(bi!=null){
			srcClip.x = 0;
			srcClip.y = 0;
			srcClip.w = bi.getWidth();
			srcClip.h = bi.getHeight();
			recomputeImage(); //recompute the color. This is quick O(ms), since bi is already loaded.
		}
	}
	
	/** The srcClip will start guaranteedly in the bottom-left corner. */
	public static final int SMARTCLIP_BOTLEFT = -1;
	/** The srcClip will be guaranteedly centered. */
	public static final int SMARTCLIP_CENTERED = 0;
	/** The srcClip will end guaranteedly in the top-right corner. */
	public static final int SMARTCLIP_TOPRIGHT = 1;
	/**
	 * Computes the "srcClip" based on the hints specified by the user.
	 * 
	 * WARNING: This requires BufferedImage to be loaded, which is a very slow operation.
	 * If it is already loaded, this method is, however, instant.
	 * 
	 * @author Kevin van As
	 * @param aspectRatio: height/width of the final image
	 * @param grid_aspectRatio: is height/width of the grid: grid.size.h/grid.size.w
	 * @param smartclip_position: Anchor position of the clip, choose between: {Image.SMARTCLIP_BOTLEFT,Image.SMARTCLIP_CENTERED,Image.SMARTCLIP_TOPRIGHT}
	 */
	public void smartClip(double aspectRatio, double grid_aspectRatio, int smartclip_position){
		if(aspectRatio<=0 || grid_aspectRatio <= 0){
			throw new IllegalArgumentException("(Image) The parameter(s) \"*aspectRatio\" must be >0.");
		}
		
		if(smartclip_position == SMARTCLIP_BOTLEFT){
			BufferedImage bi = this.getImage();
			if(bi==null) return;
			
			double img_aspectRatio = ((double)bi.getHeight())/bi.getWidth();
			double trgt_aspectRatio = aspectRatio/grid_aspectRatio;

			System.out.println("img_aspectRatio = " + img_aspectRatio);
			System.out.println("trgt_aspectRatio = " + trgt_aspectRatio);
			
			if(img_aspectRatio < trgt_aspectRatio){
				System.out.println("height maximal");
				//width cannot be filled, height is filled maximally
				srcClip.h = bi.getHeight();
				srcClip.w = (int)Math.ceil(srcClip.h/trgt_aspectRatio);
			}else{
				System.out.println("width maximal");
				//height cannot be filled, width is filled maximally
				srcClip.w = bi.getWidth();
				srcClip.h = (int)Math.ceil(srcClip.w*trgt_aspectRatio);
			}

			srcClip.x = 0; //left corner
			srcClip.y = bi.getHeight()-srcClip.h; //bottom corner (N.B.: 0=top)
			
		}else if(smartclip_position == SMARTCLIP_CENTERED){
			BufferedImage bi = this.getImage();
			if(bi==null) return;

			double img_aspectRatio = ((double)bi.getHeight())/bi.getWidth();
			double trgt_aspectRatio = aspectRatio/grid_aspectRatio;
			
			if(img_aspectRatio < trgt_aspectRatio){
				//width cannot be filled, height is filled maximally
				srcClip.h = bi.getHeight();
				srcClip.w = (int)Math.ceil(srcClip.h/trgt_aspectRatio);
			}else{
				//height cannot be filled, width is filled maximally
				srcClip.w = bi.getWidth();
				srcClip.h = (int)Math.ceil(srcClip.w*trgt_aspectRatio);
			}
			
			srcClip.x = (int)Math.ceil((bi.getWidth()-srcClip.w)/2d); //left-most point if centered
			srcClip.y = (int)Math.ceil((bi.getHeight()-srcClip.h)/2d); //top-most point if centered

		}else if(smartclip_position == SMARTCLIP_TOPRIGHT){
			BufferedImage bi = this.getImage();
			if(bi==null) return;

			double img_aspectRatio = ((double)bi.getHeight())/bi.getWidth();
			double trgt_aspectRatio = aspectRatio/grid_aspectRatio;
			
			if(img_aspectRatio < trgt_aspectRatio){
				//width cannot be filled, height is filled maximally
				srcClip.h = bi.getHeight();
				srcClip.w = (int)Math.ceil(srcClip.h/trgt_aspectRatio);
			}else{
				//height cannot be filled, width is filled maximally
				srcClip.w = bi.getWidth();
				srcClip.h = (int)Math.ceil(srcClip.w*trgt_aspectRatio);
			}

			srcClip.x = bi.getWidth()-srcClip.w; //right-most point
			srcClip.y = 0; //top-most point
			
		}else{
			throw new IllegalArgumentException("(Image) The parameter \"smartclip_position\" may not be " + smartclip_position +
					"Please choose between: {Image.SMARTCLIP_BOTLEFT,Image.SMARTCLIP_CENTERED,Image.SMARTCLIP_TOPRIGHT}.");
		}
		
		//Color must be recomputed after clipping. Do this right away, as it takes only O(ms) (bi is loaded at this point).
		recomputeImage();	
	}
	
}

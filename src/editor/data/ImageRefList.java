package editor.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageRefList {

	private Map<Integer,Image> images = new HashMap<Integer,Image>();

/* Map-overrides: */	
	
	public Image getImage(int id){
		return images.get(id);
	}
	/**
	 * Returns the image last added to the list with a generated ID.
	 * Images manually added using put(id,img) are ignored, since you already know what Image that is.
	 * 
	 * @author Kevin van As
	 * @return Image
	 */
	public Image getLastAddedImage(){
		return images.get(idcounter);
	}
	public boolean containsKey(int id){
		return images.containsKey(id);
	}
	public void put(int id, Image img){
		if(images.containsKey(id)){
			System.err.println("<<Warning>> ImageRefList: Attempting to insert an image with id \"" + id + "\"," +
					"but the id already exists. Generating a new id for this image and inserting the image using this new id.");
			put(img);
		}
		images.put(id,img);
	}
	public void put(Image img){
		images.put(generateID(),img);
	}
	public Set<Integer> keySet(){
		return images.keySet();
	}
	
/* ImageRefListXML methods: */
		
	public boolean importImage(String pwd){
		String[] pwdSplit = pwd.split("\\\\");
		String name = ".../";
		for(int i = Math.max(0, pwdSplit.length-2); i<pwdSplit.length; i++){
			name += pwdSplit[i] + "/";
		}
		name = name.substring(0, name.length()-1); //remove the last "/"
//		System.out.println("pwd = " + pwd);
//		System.out.println("pwdSplit = " + Arrays.toString(pwdSplit));
//		System.out.println("imgName = " + name);

		Image newImg = new Image(pwd, name, 0, 0, 0, 0, null);
		//Will automatically generate the BufferedImage and set srcClip appropriately. Color remains null.:
		BufferedImage bi = newImg.getImage(); //To see if it exists
		
		if(bi!=null){
			newImg.maximizeClip();
			this.put(newImg);
			newImg.clearImage(); // We are guaranteed to run out of memory if we keep all images in memory.
			return true;
		}
		return false;
	}
	
	/**
	 * Recursive helper-method of "importImagesFromFileSystem(String dirPwd)"
	 * 
	 * @author Kevin van As
	 * @param dirPwd
	 */
	private void importImagesRecursively(File parent){
//		System.out.println("parent = " + parent.getPath());
//		System.out.println("abs? " + parent.isAbsolute());
//		System.out.println("dir? " + parent.isDirectory());
//		System.out.println("exists? " + parent.exists());
		if(parent==null || !parent.exists()) return;
		
		if(parent.isDirectory()){
			System.out.println("-- Reading directory: " + parent.getPath() + " --");
			File[] children = parent.listFiles();
						
			for(File child : children){
				importImagesRecursively(child);
			}
		}else{
			//`parent' is not a directory, check if it is an image
			importImage(parent.getAbsolutePath());
			//If it is not an image, a non-blocking error is automatically shown by the importImage method.
		}
	}
	
	/**
	 * Recursively imports images from the given directory.
	 * 
	 * @author Kevin van As
	 * @param dirPwd
	 */
	public void importImagesFromFileSystem(String dirPwd){
		importImagesRecursively(new File(dirPwd));
	}
	
	/**
	 * Recursively imports images from the given directory.
	 * 
	 * @author Kevin van As
	 * @param dirPwd
	 */
	public void importImagesFromFileSystem(URI dirPwd){
		importImagesRecursively(new File(dirPwd));
	}
	
	private int idcounter = 0;
	/**
	 * Simple inefficient algorithm for now:
	 * Start at 0 and count up until we find an unused ID.
	 * It takes long the first time it executes, but
	 * is basically instant thereafter.
	 * 
	 * @author Kevin van As
	 * @return
	 */
	private int generateID(){
		while(images.containsKey(idcounter)){
			idcounter++;
		}
		System.out.println("ImageRefList: Generated ID = " + idcounter);
		return idcounter;
	}
	
}

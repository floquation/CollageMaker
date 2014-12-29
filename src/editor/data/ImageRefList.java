package editor.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public List<Integer> keyList(){
		return new ArrayList<Integer>(images.keySet());
	}

	/**
	 * Checks whether the image belonging to the given "id" is valid.
	 * "Valid" means that it is indeed an image.
	 * <p>
	 * Removes the image from the list if it is invalid. TODO: Prompt user.
	 * 
	 * @param id : id of the image-to-be-checked
	 * @param computeColors : compute colors while the images are loaded anyway? Relatively cheap if it has to be done sometime anyway...
	 * @param autoDispose : dispose BufferedImage automatically? If false, responsibility for the user to remove it!
	 * @return
	 */
	public void checkAllValidity(boolean computeColors, boolean autoDispose){
		for(int key : this.keyList()){ //keyList to prevent ConcurrentModificationException, since checkValidity will remove invalid images.
			this.checkValidity(key,computeColors, autoDispose);
		}
	}
	
	/**
	 * Checks whether the image belonging to the given "id" is valid.
	 * "Valid" means that it is indeed an image.
	 * <p>
	 * Removes the image from the list if it is invalid. TODO: Prompt user.
	 * 
	 * @param id : id of the image-to-be-checked
	 * @param computeColor : compute color while the image is loaded anyway? Relatively cheap if it has to be done sometime anyway...
	 * @param autoDispose : dispose BufferedImage automatically? If false, responsibility for the user to remove it!
	 * @return
	 */
	public boolean checkValidity(int id, boolean computeColor, boolean autoDispose){
		Image img = this.getImage(id);
		BufferedImage bi = img.getImage(); //Just to see if it exists
		
		if(bi!=null){ // It exists!
			if(computeColor){
				img.recomputeImage();
			}
			if(autoDispose) img.clearImage(); // We are guaranteed to run out of memory if we keep all images in memory.
			return true;
		}
		//It does not exist! Remove from the list.
		// TODO: Prompt user something instead...
		images.remove(id);
		return false;
	}
	
/* ImageRefListXML methods: */
	
	/**
	 * Attempts to import a image from a file described by "pwd": the path of the image.
	 * Returns true if "pwd" exists. False otherwise.
	 * <p>
	 * If "checkExistence" is set to true, then the file is loaded to validate that it is indeed an image.
	 * If it is set to false, this check is not performed: existence of the file is enough to return true.
	 * 
	 * @param pwd
	 * @param checkExistance
	 * @return
	 */
	public boolean importImage(String pwd, boolean checkExistence){
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
		
		if(!checkExistence){ //We are done if we need not check if the file is truly an image.
			this.put(newImg);
			return true;
		}
		
		//Will automatically generate the BufferedImage and set srcClip appropriately. Color remains null.:
		BufferedImage bi = newImg.getImage(); //Just to see if it exists
		
		if(bi!=null){
			newImg.maximizeClip();
			newImg.clearImage(); // We are guaranteed to run out of memory if we keep all images in memory.
			return true;
		}
		return false;
	}
	
	/**
	 * Recursive helper-method of "importImagesFromFileSystem(String dirPwd)"
	 * <p>
	 * "checkExistence" determines whether the Image needs to be loaded to see whether it is a valid image.
	 * If false, the Image class is prepared, but no validity check is performed (which is very slow).
	 * 
	 * @author Kevin van As
	 * @param dirPwd
	 * @param checkExistence
	 */
	private void importImagesRecursively(File parent, boolean checkExistence){
//		System.out.println("parent = " + parent.getPath());
//		System.out.println("abs? " + parent.isAbsolute());
//		System.out.println("dir? " + parent.isDirectory());
//		System.out.println("exists? " + parent.exists());
		if(parent==null || !parent.exists()){ System.out.println("Parent does not exist or is null."); return;}
		
		if(parent.isDirectory()){
			System.out.println("-- Reading directory: " + parent.getPath() + " --");
			File[] children = parent.listFiles();
						
			for(File child : children){
				importImagesRecursively(child, checkExistence);
			}
		}else{
			//`parent' is not a directory, check if it is an image
			importImage(parent.getAbsolutePath(), checkExistence);
			//If it is not an image, a non-blocking error is automatically shown by the importImage method.
		}
	}
	
	/**
	 * Recursively imports images from the given directory.
	 * <p>
	 * "checkExistence" determines whether the Image needs to be loaded to see whether it is a valid image.
	 * If false, the Image class is prepared, but no validity check is performed (which is very slow).
	 * 
	 * @author Kevin van As
	 * @param dirPwd
	 * @param checkExistence
	 */
	public void importImagesFromFileSystem(String dirPwd, boolean checkExistence){
		importImagesRecursively(new File(dirPwd), checkExistence);
	}
	
	/**
	 * Recursively imports images from the given directory.
	 * <p>
	 * "checkExistence" determines whether the Image needs to be loaded to see whether it is a valid image.
	 * If false, the Image class is prepared, but no validity check is performed (which is very slow).
	 * 
	 * @author Kevin van As
	 * @param dirPwd
	 * @param checkExistence
	 */
	public void importImagesFromFileSystem(URI dirPwd, boolean checkExistence){
		importImagesRecursively(new File(dirPwd), checkExistence);
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
//		System.out.println("ImageRefList: Generated ID = " + idcounter);
		return idcounter;
	}
	
}

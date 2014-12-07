package editor.io;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import editor.data.Image;
import editor.data.ImageRefList;
import editor.data.ResultXML;

public abstract class xmlReader {

	/**
	 * Imports "xml", assuming it has the format of a <recipe>: "result.xml",
	 * which is the output of the CI algorithm.
	 * 
	 * @param xml
	 * @return a ResultXML struct, being the Java-struct version of the xml file.
	 */
    public static ResultXML readXML_result(ImageRefList refList, String xml){
    	System.out.println("(xmlReader.readXML_result) Loading `" + xml + "'.");
        Document dom;
        ResultXML output = new ResultXML();
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the    
            // XML file
            dom = db.parse(xml);
            
            Element doc = dom.getDocumentElement();

//name:
    	    NodeList nl = doc.getElementsByTagName("name");
    	    if(nl.getLength()>0){
    	    	Node child = nl.item(0);
    	    	if(child.hasChildNodes()){
    	    		output.name = child.getFirstChild().getNodeValue();
    	    	}else{
        	    	System.err.println("xmlReader: tag \"name\" does not have a value specified in \"" + xml +"\".");    
        	    	return null;
    	    	}
    	    }else{
    	    	System.err.println("xmlReader: tag \"name\" not found in \"" + xml +"\".");
    	    	return null;
    	    } 
//orgImageId:
    	    nl = doc.getElementsByTagName("orgImageId");
    	    if(nl.getLength()>0){
    	    	if(nl.item(0).hasChildNodes()){  
	    			try{
	    	    		output.orgImageId = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());  
	    			}catch(NumberFormatException e){
	    				System.err.println("xmlReader: attribute \"orgImageId\" in should be an integer in \"" + xml +"\".");
	        	    	return null;
    	    		}	    		
    	    	}else{
        	    	System.err.println("xmlReader: tag \"orgImageId\" does not have a value specified in \"" + xml +"\".");  
        	    	return null;  	    		
    	    	}
    	    }else{
    	    	System.err.println("xmlReader: tag \"orgImageId\" not found in \"" + xml +"\".");
    	    	return null;
    	    }
    	    //Check if refList contains the id:
    	    if(!refList.containsKey(output.orgImageId)){
    	    	System.err.println("xmlReader: ID \"" + output.orgImageId + "\" from \""+xml+"\" is not found in the reference list. Result file corrupted?");    	    	
    	    	return null;
    	    }
//size:
    	    nl = doc.getElementsByTagName("size");
    	    if(nl.getLength()>0){
    	    	NamedNodeMap attributes = nl.item(0).getAttributes();
    	    	if(attributes != null){
    	    		ResultXML.Size size = output.size; //new ResultXML.Size();
    	    		Node node = attributes.getNamedItem("w");
    	    		if(node!=null){
    	    			try{
    	    				size.w = Integer.parseInt(node.getNodeValue());
    	    			}catch(NumberFormatException e){
    	    				System.err.println("xmlReader: attribute \"w\" in tag \"size\" should be an integer in \"" + xml +"\".");
    	        	    	return null;
        	    		}
    	    		}else{
    	    			System.err.println("xmlReader: tag \"size\" does not have the attribute \"w\" in \"" + xml +"\".");
            	    	return null;
    	    		}
    	    		node = attributes.getNamedItem("h");
    	    		if(node!=null){
    	    			try{
    	    				size.h = Integer.parseInt(node.getNodeValue());
    	    			}catch(NumberFormatException e){
    	    				System.err.println("xmlReader: attribute \"h\" in tag \"size\" should be an integer in \"" + xml +"\".");
    	        	    	return null;
        	    		}
    	    		}else{
    	    			System.err.println("xmlReader: tag \"size\" does not have the attribute \"h\" in \"" + xml +"\".");
            	    	return null;
    	    		}
    	    		output.size = size;
    	    	}else{
        	    	System.err.println("xmlReader: tag \"size\" does not have any attributes in \"" + xml +"\".\n Required: (int)w, (int)h.");   
        	    	return null; 	    		
    	    	}
    	    }else{
    	    	System.err.println("xmlReader: tag \"size\" not found in \"" + xml +"\".");
    	    	return null;
    	    }
//grid:
    	    nl = doc.getElementsByTagName("grid");
    	    if(nl.getLength()>0){
    	    	Node theGrid = nl.item(0);
    	    	NamedNodeMap attributes = theGrid.getAttributes();
    	    	if(attributes != null){
	//grid.size:
    	    		ResultXML.Size size = output.grid.size;//new ResultXML.Size();
    	    		Node node = attributes.getNamedItem("w");
    	    		if(node!=null){
    	    			try{
    	    				size.w = Integer.parseInt(node.getNodeValue());
    	    			}catch(NumberFormatException e){
    	    				System.err.println("xmlReader: attribute \"w\" in tag \"grid\" should be an integer in \"" + xml +"\".");
    	        	    	return null;
        	    		}
    	    		}else{
    	    			System.err.println("xmlReader: tag \"grid\" does not have the attribute \"w\" in \"" + xml +"\".");
            	    	return null;
    	    		}
    	    		node = attributes.getNamedItem("h");
    	    		if(node!=null){
    	    			try{
    	    				size.h = Integer.parseInt(node.getNodeValue());
    	    			}catch(NumberFormatException e){
    	    				System.err.println("xmlReader: attribute \"h\" in tag \"grid\" should be an integer in \"" + xml +"\".");
    	        	    	return null;
        	    		}
    	    		}else{
    	    			System.err.println("xmlReader: tag \"grid\" does not have the attribute \"h\" in \"" + xml +"\".");
            	    	return null;
    	    		}
    	    		output.grid = output.grid;//new ResultXML.Grid();
    	    		output.grid.size = size;
	//grid elements:
    	    		
    	    		NodeList gridChildren = theGrid.getChildNodes();
    	    		if(gridChildren.getLength() < output.grid.size.w*output.grid.size.h){
    	    			//Guaranteedly not enough children: The grid probably has not been generated yet.
    	    			//Do not load the grid.
    	    			System.out.println("xmlReader: not loading grid elements, since there are not enough in \"" + xml + "\". \n\t" +
    	    					"If the grid has not yet been generated, everything is OK. Otherwise the file is corrupted, and thus must be re-generated.");
    	    		}else{ //Read the grid and check if it is complete
        	    		output.grid.elements = new ResultXML.Grid.Element[output.grid.size.w][output.grid.size.h];
        	    		
	    	    		for(int i = 0; i<gridChildren.getLength(); i++){
	    	    			boolean success = readElement(gridChildren.item(i), output.grid.elements, xml);
	    	    			if(!success) return null;
	    	    		}
	    	    		
	    	    		//(Sanity) Check if all images have been specified:
	    	    		for(int i = 0; i<output.grid.elements.length; i++){
	    	    			for(int j = 0; j<output.grid.elements[i].length; j++){
	    	    				if(output.grid.elements[i][j]==null){
	    	    					System.err.println("xmlReader: the \"grid\" in \"" + xml + "\" does not contain the index (" + i + "," + j + ")! The entire width/height must be filled!");
	    	    					return null;
	    	    				}
	    	    			}
	    	    		}
    	    		}
    	    		
    	    		
    	    	}else{
        	    	System.err.println("xmlReader: tag \"grid\" does not have any attributes in \"" + xml +"\".\n Required: (int)w, (int)h.");  
        	    	return null;  	    		
    	    	}
    	    }else{
    	    	System.err.println("xmlReader: tag \"grid\" not found in \"" + xml +"\".");
    	    	return null;
    	    }
    	    

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
            return null;
        } catch (SAXException se) {
            System.out.println(se.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }
        
        return output;
    }
    
    /**
     * Imports "theElement", and writes it to "target".
     * 
     * @param theElement
     * @param target
     * @param xml : used for error message (file path)
     * 
     * returns succes: true/false
     */
    private static boolean readElement(Node theElement, ResultXML.Grid.Element[][] target, String xml){
    	if(theElement.getNodeType() == Node.TEXT_NODE){ //Ignore "\n" etc.
    		return true;
    	}
    	
    	NamedNodeMap attributes = theElement.getAttributes();
    	if(attributes != null){
    		int x,y;
			ResultXML.Grid.Element newElem = new ResultXML.Grid.Element();
    		
    		Node node = attributes.getNamedItem("imgId");
    		if(node!=null){
    			try{
        			newElem.imgId = Integer.parseInt(node.getNodeValue());
    			}catch(NumberFormatException e){
    				System.err.println("xmlReader: attribute \"imgId\" in tag \"element\" should be an integer in \"" + xml +"\".");
    				return false;
	    		}
    		}else{
    			System.err.println("xmlReader: tag \"element\" does not have the attribute \"imgId\" in \"" + xml +"\".");
				return false;
    		}
    		
    		
    		node = attributes.getNamedItem("x");
    		if(node!=null){
    			try{
    				x = Integer.parseInt(node.getNodeValue());
    			}catch(NumberFormatException e){
    				System.err.println("xmlReader: attribute \"x\" in tag \"element\" should be an integer in \"" + xml +"\".");
    				return false;
	    		}
    		}else{
    			System.err.println("xmlReader: tag \"element\" does not have the attribute \"x\" in \"" + xml +"\".");
				return false;
    		}

    		node = attributes.getNamedItem("y");
    		if(node!=null){
    			try{
    				y = Integer.parseInt(node.getNodeValue());
    			}catch(NumberFormatException e){
    				System.err.println("xmlReader: attribute \"y\" in tag \"element\" should be an integer in \"" + xml +"\".");
    				return false;
	    		}
    		}else{
    			System.err.println("xmlReader: tag \"element\" does not have the attribute \"y\" in \"" + xml +"\".");
				return false;
    		}

    		node = attributes.getNamedItem("orgColor");
    		if(node!=null){
    			try{ 
    				newElem.color = Color.decode("0x" + node.getNodeValue());
    			}catch(NumberFormatException e){
    				System.err.println("xmlReader: attribute \"orgColor\" in tag \"element\" should be a hexagonal color in \"" + xml +"\".");
    				return false;
	    		}
    		}else{
    			System.err.println("xmlReader: tag \"element\" does not have the attribute \"orgColor\" in \"" + xml +"\".");
				return false;
    		}
    		
		//Write to correct array index, if not already occupied.
    		
    		if(target[x][y] == null){
    			target[x][y] = newElem;
    		}else{
    			System.err.println("xmlReader: The \"element\" in tag \"grid\" with index (" + x + "," + y + ") occurs twice! Should be unique! In xml file: \"" + xml + "\".");
				return false;
    		}
    		
    	}else{
	    	System.err.println("xmlReader: tag \"element\" does not have any attributes in \"" + xml +"\".\n Required: (int)x, (int)y, (int)imgId, (int)orgColor.");    
			return false;	    		
    	}
    	
    	return true;
    }
    

	/**
	 * Imports "xml", assuming it has the format of a <images>: "refList.xml",
	 * which is the format used to store the imported images.
	 * 
	 * @param xml
	 * @return a RefListXML struct, being the Java-struct version of the xml file.
	 */
    public static ImageRefList readXML_refList(String xml){
    	System.out.println("(xmlReader.readXML_refList) Loading `" + xml + "'.");
        Document dom;
        ImageRefList output = new ImageRefList();
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the    
            // XML file
            dom = db.parse(xml);
            
            Element doc = dom.getDocumentElement();

//get all images:
    	    NodeList nl = doc.getElementsByTagName("image");
            for(int i = 0; i<nl.getLength(); i++){ //For each image to-be-loaded
	    		Node image = nl.item(i);
	    		int srcX = 0;
	    		int srcY = 0;
	    		int srcW = -1;
	    		int srcH = -1;
	    		Color color = null;
	    		String name = "";
	    		String pwd = "";
	    		int id = -1;
	    		
	    		NamedNodeMap attributes_id = image.getAttributes();
    	    	if(attributes_id != null){
    	    		Node node = attributes_id.getNamedItem("id");
    	    		if(node!=null){
    	    			try{
    	    				id = Integer.parseInt(node.getNodeValue());
    	    			}catch(NumberFormatException e){
    	    				System.err.println("xmlReader: attribute \"id\" in tag \"image\" should be an integer in \"" + xml +"\".");
    	    				return null;
    	    			}
    	    		}else{
    	    			System.err.println("xmlReader: tag \"image\" does not have the attribute \"id\" in \"" + xml +"\".");
    	    			return null;
    	    		}
    	    	}else{
	    			System.err.println("xmlReader: tag \"image\" does not have any attributes in \"" + xml +"\". Required: (UNIQUE int)id.");
	    			return null;
	    		}
	    		
    	    	if(image.hasChildNodes()){
    	    		NodeList imageChildren = image.getChildNodes();
    	    		for(int j = 0; j<imageChildren.getLength(); j++){
    	    			Node child = imageChildren.item(j);
        	    		if(child.getNodeType() == Node.TEXT_NODE){
        	    			continue; //ignore "\n" etc.
        	    		}
//Read children of <image>:
        	    		if(child.getNodeName().equals("pwd")){
        	    			if(child.hasChildNodes()){
        	    				pwd = child.getFirstChild().getNodeValue();    	    				
        	    			}else{
        	    				System.err.println("xmlReader: tag \"image->pwd\" does not have a value in " + xml + ".");
        	    				return null;
        	    			}
        	    		}else
        	    		if(child.getNodeName().equals("name")){
        	    			if(child.hasChildNodes()){
        	    				name = child.getFirstChild().getNodeValue();        	    				
        	    			}else{
        	    				System.err.println("xmlReader: tag \"image->name\" does not have a value in " + xml + ".");
        	    				return null;
        	    			}
        	    		}else
        	    		if(child.getNodeName().equals("color")){
        	    			if(child.hasChildNodes()){
        	        			try{ 
        	        				color = Color.decode("0x" + child.getFirstChild().getNodeValue());
        	        			}catch(NumberFormatException e){
        	        				System.err.println("xmlReader: the value of tag \"color\" should be a hexagonal color in \"" + xml +"\".");
        	    	    		}
        	    			}else{
        	    				System.err.println("xmlReader: tag \"image->color\" does not have a value (should be a hexagonal color) in " + xml + ".");
        	    				return null;
        	    			}
        	    		}else
        	    		if(child.getNodeName().equals("srcClip")){
        	    	    	NamedNodeMap attributes = child.getAttributes();
        	    	    	if(attributes != null){
        	    	    		
        	    	    		Node node = attributes.getNamedItem("x");
        	    	    		if(node!=null){
        	    	    			try{
        	    	    				srcX = Integer.parseInt(node.getNodeValue());
        	    	    			}catch(NumberFormatException e){
        	    	    				System.err.println("xmlReader: attribute \"x\" in tag \"image->srcClip\" should be an integer in \"" + xml +"\".");
        	    	    				return null;
        	    	    			}
        	    	    		}else{
        	    	    			System.err.println("xmlReader: tag \"image->srcClip\" does not have the attribute \"x\" in \"" + xml +"\".");
        	    	    			return null;
        	    	    		}
        	    	    		
        	    	    		node = attributes.getNamedItem("y");
        	    	    		if(node!=null){
        	    	    			try{
        	    	    				srcY = Integer.parseInt(node.getNodeValue());
        	    	    			}catch(NumberFormatException e){
        	    	    				System.err.println("xmlReader: attribute \"y\" in tag \"image->srcClip\" should be an integer in \"" + xml +"\".");
        	    	    				return null;
        	    	    			}
        	    	    		}else{
        	    	    			System.err.println("xmlReader: tag \"image->srcClip\" does not have the attribute \"y\" in \"" + xml +"\".");
        	    	    			return null;
        	    	    		}
        	    	    		
        	    	    		node = attributes.getNamedItem("w");
        	    	    		if(node!=null){
        	    	    			try{
        	    	    				srcW = Integer.parseInt(node.getNodeValue());
        	    	    			}catch(NumberFormatException e){
        	    	    				System.err.println("xmlReader: attribute \"w\" in tag \"image->srcClip\" should be an integer in \"" + xml +"\".");
        	    	    				return null;
        	    	    			}
        	    	    		}else{
        	    	    			System.err.println("xmlReader: tag \"image->srcClip\" does not have the attribute \"w\" in \"" + xml +"\".");
        	    	    			return null;
        	    	    		}
        	    	    		
        	    	    		node = attributes.getNamedItem("h");
        	    	    		if(node!=null){
        	    	    			try{
        	    	    				srcH = Integer.parseInt(node.getNodeValue());
        	    	    			}catch(NumberFormatException e){
        	    	    				System.err.println("xmlReader: attribute \"h\" in tag \"image->srcClip\" should be an integer in \"" + xml +"\".");
        	    	    				return null;
        	        	    		}
        	    	    		}else{
        	    	    			System.err.println("xmlReader: tag \"image->srcClip\" does not have the attribute \"h\" in \"" + xml +"\".");
        	    	    			return null;
        	    	    		}
        	    	    		
        	    	    	}else{
    	    	    			System.err.println("xmlReader: tag \"image->srcClip\" does not have any attributes in \"" + xml +"\". Required: (int)x, (int)y, (int)w, (int)h.");
    	    	    			return null;
    	    	    		}
        	    		}else{
        	    			System.err.println("xmlReader: unknown tag specified in \"image\" in " + xml + ". What is " + child.getNodeName() + "?");
        	    			return null;
        	    		}
        	    		
    	    		}//for-loop (j)
    	    		    	       	    		
    	    		if(pwd == null || name == null || color == null){
    	    			System.err.println("xmlReader: tag \"image\" in \"" + xml + "\" is missing children. Required: (String)pwd, (String)name, (unique int)id, (hexagonal)color, (attributes: (int)x,(int)y,(int)w,(int)h)srcClip.");
    	    			return null;
    	    		}
    	    		
    	    	}else{
        	    	System.err.println("xmlReader: tag \"image\" does not have any children \"" + xml +"\".\n Required: <pwd>, <name>, <color> and <srcClip>.");
        	    	return null;
    	    	}
    	    	
    	    	//Write to the Map<id,Image>
    	    	if(output.containsKey(id)){
    	    		System.err.println("xmlReader: tag \"image\" in \"" + xml +"\", must have UNIQUE ids. Duplicated id: " + id + ".");
        	    	return null;
    	    	}
	    		Image newImg = new Image(pwd, name, srcX, srcY, srcW, srcH, color);
	    		output.put(id, newImg);
	    		

	    	}//For each image-loop
    	    
    	    
        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
            return null;
        } catch (SAXException se) {
            System.out.println(se.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }
        
        
        
        return output;
    }
    
    
}

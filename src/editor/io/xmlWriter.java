package editor.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import editor.data.Image;
import editor.data.ImageRefList;
import editor.data.Rectangle;
import editor.data.ResultXML;

public class xmlWriter {
	
	public static void writeXML_result(String xml, ResultXML result){
		Document dom;
        Element e = null;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

        // create the root element
            Element rootEle = dom.createElement("recipe");
            dom.appendChild(rootEle);

        // create data elements and place them under root
            e = dom.createElement("name");
            e.appendChild(dom.createTextNode(result.name));
            rootEle.appendChild(e);
            
            e = dom.createElement("orgImageId");
            e.appendChild(dom.createTextNode(""+result.orgImageId));
            rootEle.appendChild(e);

            e = dom.createElement("size");
            e.setAttribute("h", ""+result.size.h);
            e.setAttribute("w", ""+result.size.w);
            rootEle.appendChild(e);

            e = dom.createElement("grid");
            e.setAttribute("h", ""+result.grid.size.h);
            e.setAttribute("w", ""+result.grid.size.w);
            if(result.grid.elements!=null){
	            for(int i = 0; i<result.grid.elements.length; i++){
	                for(int j = 0; j<result.grid.elements[i].length; j++){
	                	Element newChild = dom.createElement("element");
	                	newChild.setAttribute("imgId", ""+result.grid.elements[i][j].imgId);
	                	newChild.setAttribute("orgColor", Integer.toHexString(result.grid.elements[i][j].color.getRGB()).substring(2)); //Gets the hexa in RGB format, cutting the alpha away using .substring(2)
	                	newChild.setAttribute("x", ""+i);
	                	newChild.setAttribute("y", ""+j);
	                    e.appendChild(newChild);
	                }
	            }
            }
            rootEle.appendChild(e);
            

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty(OutputKeys.STANDALONE, "no");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom), 
                                     new StreamResult(new FileOutputStream(xml)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
	}
	
	public static void writeXML_refList(String xml, ImageRefList refList){
		Document dom;
        Element e = null;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

        // create the root element
            Element rootEle = dom.createElement("images");
            dom.appendChild(rootEle);

        // create data elements and place them under root
            Set<Integer> keys = refList.keySet();
            for(int key : keys){
            	e = dom.createElement("image");
            	e.setAttribute("id", "" + key);

            	Image img = refList.getImage(key);
            	Element newChild = dom.createElement("pwd");
                newChild.appendChild(dom.createTextNode(img.getPath()));
            	e.appendChild(newChild);
            	newChild = dom.createElement("name");
                newChild.appendChild(dom.createTextNode(img.getName()));
            	e.appendChild(newChild);
            	newChild = dom.createElement("color");
                newChild.appendChild(dom.createTextNode(Integer.toHexString(img.getColor().getRGB()).substring(2))); //Gets the hexa in RGB format, cutting the alpha away using .substring(2)
            	e.appendChild(newChild);
            	newChild = dom.createElement("srcClip");
            	Rectangle srcClip = refList.getImage(key).getClipRect();
            	newChild.setAttribute("x", ""+srcClip.x);
            	newChild.setAttribute("h", ""+srcClip.h);
            	newChild.setAttribute("w", ""+srcClip.w);
            	newChild.setAttribute("y", ""+srcClip.y);
            	e.appendChild(newChild);
            	                
                rootEle.appendChild(e);
            }
            

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty(OutputKeys.STANDALONE, "no");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom), 
                                     new StreamResult(new FileOutputStream(xml)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
		
	}
	
}

package framework;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * SRC: http://stackoverflow.com/questions/7373567/java-how-to-read-and-write-xml-files
 * 
 * @author Kevin
 *
 */
public abstract class MyXML {
	public static String getTextValue(String def, Element doc, String tag) {
	    String value = def;
	    NodeList nl;
	    nl = doc.getElementsByTagName(tag);
	    if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
	        value = nl.item(0).getFirstChild().getNodeValue();
	    }
	    return value;
	}
}

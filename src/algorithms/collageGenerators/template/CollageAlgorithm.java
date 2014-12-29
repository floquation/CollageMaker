package algorithms.collageGenerators.template;

import java.util.LinkedList;
import java.util.List;

import editor.data.ImageRefList;
import editor.data.ResultXML;

/**
 * Holds track of the refList and result file.
 * Also holds the keySet in a copied form: a keyList (which has a fixed order).
 * If the keySet changes, this class is NOT notified and thus the algorithm continues with the old keyList.
 * 
 * @author Kevin van As
 *
 */
public class CollageAlgorithm {

	public ResultXML result;
	public ImageRefList refList;
	public List<Integer> keyList;
	
	public CollageAlgorithm(ImageRefList refList, ResultXML result) {
		this.result=result;
		this.refList=refList;
	}

	/**
	 * This method must be called from the outside to initialize the algorithm,
	 * or to later reinitialize {@code keyList}.
	 * 
	 * @author Kevin van As
	 */
	public void reInit(){
		keyList = new LinkedList<Integer>();
		keyList.addAll(refList.keySet());
	}


	
}

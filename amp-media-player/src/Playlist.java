import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Playlist {
	private ArrayList<String> Playlist;
	private ArrayList<String> shuffleQueue; //ArrayList that is made for shuffling method
	
	private HashMap<String,String> itemInfo; //will return name of media and length
	private int currentItemIndex;
	
	private boolean shuffle;
	private boolean repeat;
	
	public void init() {
		Playlist = new ArrayList<String>();
		shuffleQueue = new ArrayList<String>();
		itemInfo = new HashMap<String,String>();
		currentItemIndex = 0;
		shuffle = false;
		repeat = false;
	}
	
	/*public void loadPlaylist(String f) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(f);
            
            if(file.exists()) {
            	Document doc = db.parse(file);
                Element docEle = doc.getDocumentElement();
            }
		}
		
	}*/
	
	public void setRepeat(boolean r) {
		repeat = r;
	}
	
	public boolean getRepeat() {
		return repeat;
	}
}

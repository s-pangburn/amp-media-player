import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Playlist {
	private ArrayList<String> playlist;
	private ArrayList<String> shuffleQueue; //ArrayList that is made for shuffling method
	
	private HashMap<String,String> itemInfo; //will return name of media and length
	private int currentItemIndex;
	
	private boolean shuffle;
	private boolean repeat;
	
	public void init() {
		playlist = new ArrayList<String>();
		shuffleQueue = new ArrayList<String>();
		itemInfo = new HashMap<String,String>();
		currentItemIndex = 0;
		shuffle = false;
		repeat = false;
		
		playlist.add("FirstFile.mp3");
		playlist.add("SecondFile.wmv");
		playlist.add("JustForKicks.mp4");
	
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

	public void savePlaylist() {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Playlist");
			doc.appendChild(rootElement);

			// staff elements
			Element songPath = doc.createElement("SongPath");
			rootElement.appendChild(songPath);

			// shorten way
			// staff.setAttribute("id", "1");
			
			String fileName = "";
			Element song;
			
			for(int i = 0; i < playlist.size(); i++) {
				fileName = playlist.get(i);
				
				song = doc.createElement("Media");
				song.appendChild(doc.createTextNode(fileName));
				songPath.appendChild(song);
			}

			/* firstname elements
			Element firstname = doc.createElement("firstname");
			firstname.appendChild(doc.createTextNode("yong"));
			songPath.appendChild(firstname);

			// lastname elements
			Element lastname = doc.createElement("lastname");
			lastname.appendChild(doc.createTextNode("mook kim"));
			songPath.appendChild(lastname);

			// nickname elements
			Element nickname = doc.createElement("nickname");
			nickname.appendChild(doc.createTextNode("mkyong"));
			songPath.appendChild(nickname);

			// salary elements
			Element salary = doc.createElement("salary");
			salary.appendChild(doc.createTextNode("100000"));
			staff.appendChild(salary);
			*/

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("file.xml"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

		} 
		
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		
		catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}
	
	public void setRepeat(boolean r) {
		repeat = r;
	}
	
	public boolean getRepeat() {
		return repeat;
	}

	public static void main(String[] args) {

		Playlist test = new Playlist();
		test.init();
		test.savePlaylist();
	}
}
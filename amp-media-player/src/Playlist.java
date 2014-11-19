import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

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

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



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
	
		playlist.add("first.mp3");
		playlist.add("second.mp3");
		playlist.add("third.mp3");
		playlist.add("fourth.mp3");
	}
	
	/* INITIAL CODE USING DOM PARSER (DIDN'T WORK, SAVING IN CASE NEEDED)
	 * public void loadPlaylist(String f) {
		//TODO: clear playlist initially with confirmation message (after parsing works)
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			factory.setIgnoringElementContentWhitespace(false);
			
			File file = new File(f);
			Document document = builder.parse(file);

			NodeList nodeList = document.getDocumentElement().getChildNodes();
			System.out.println(nodeList.toString());

			for(int i = 0; i < nodeList.getLength(); i++) {
				System.out.println(nodeList.getLength());
				Node node = nodeList.item(i);
				System.out.println(node.toString());

				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					System.out.println(elem.toString()); //Debug println
					String mediaFile = elem.getElementsByTagName("Name").item(i).getChildNodes().item(0).getNodeValue();
					System.out.println("I added a file!"); //Debug println
					playlist.add(mediaFile);

				}
			}

			System.out.println(playlist);


		} catch (Exception e) {
			System.out.println(e);
		}

	}*/
	

	public void loadPlaylist(String f) {
		//Clear Playlist
		playlist.clear();
		
		//TODO: Add confirmation message (in GUI?) saying that playlist is empty. (placeholders below)
		System.out.println("Playlist: " + playlist);
		System.out.println("The playlist has been cleared! Loading playlist...");
		
		try {
			//Opening file
			Scanner sc = new Scanner(new BufferedReader(new FileReader(f)));
			sc.useLocale(Locale.US);

			//Checks whether or not there is another line inside the xml file
			while (sc.hasNext()) {
				//Sets the string equal to anything inside the Media tags
				String regex = "(\\s)*<Media>(.*)</Media>";
				
				Pattern p = Pattern.compile(regex);
				String check = sc.nextLine();
				Matcher m = p.matcher(check);
				
				//If it finds <Media>text here</Media>, it will add it to the playlist Arraylist.
				if (m.matches()) {
					playlist.add(m.group(2));
				}
			}

		} catch (NullPointerException e) {
			System.out.println("Error!");
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}

		System.out.println(playlist);
	}
    
	public void savePlaylist() {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Playlist");
			doc.appendChild(rootElement);
			
			String fileName = "";
			Element song;
			
			//Loop amount is equal to the number of files inside the arraylist
			for(int i = 0; i < playlist.size(); i++) {
				//Finds the file path from the arraylist
				fileName = playlist.get(i);
				
				//Creates a new Media element and puts the media file into the element
				song = doc.createElement("Media");
				song.appendChild(doc.createTextNode(fileName));
				rootElement.appendChild(song);
			}


			// write the content into xml file
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("file.xml"));

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
	
	public void addMedia(String f) {
		//Adds the fileName string into the Arraylist
		playlist.add(f);
		System.out.println("The media location '" + f + "' has been added to the Playlist.");
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
		test.addMedia("user.media.mp3");
		test.savePlaylist();
		test.loadPlaylist("file.xml");
	}
}
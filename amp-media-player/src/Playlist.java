import java.util.ArrayList;
import java.util.Collections;
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
	private ArrayList<fileProperty> playlist2;
	private ArrayList<fileProperty> shuffleQueue; //ArrayList that is made for shuffling method
	
	private boolean shuffle;
	private boolean repeat;
	
	findData finder;
	
	public Playlist() {
		init();
	}
	
	public void init() {
		playlist = new ArrayList<String>();
		playlist2 = new ArrayList<fileProperty>();
		shuffle = true;
		repeat = false;
		
		finder = new findData();
	
		
//		playlist.add("C:\\Users\\New Ending\\Music\\Icon For Hire - Icon For Hire (Self-Titled)\\01. Cynics & Critics.mp3");
//		playlist.add("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\2. Overtime - EP - Overtime.m4a");
//		playlist.add("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\4. Overtime - EP - Satellites.m4a");
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
	
	
	public void clearPlaylist() {
		playlist.clear();
		playlist2.clear();
	}
	

	public void loadPlaylist(String f) {
		clearPlaylist();
		
		//TODO: Add confirmation message (in GUI?) saying that playlist is empty. (placeholders below)
		System.out.println("loadPlaylist: Checking if Playlist was cleared... ArrayList Playlist = " + playlist);
		System.out.println("loadPlaylist: The playlist has been cleared! Loading playlist...");
		
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
			System.out.println("loadPlaylist: Error!");
		} catch (FileNotFoundException e) {
			System.out.println("loadPlaylist: File not found!");
		}

		System.out.println("loadPlaylist: ArrayList Playlist = " + playlist + "\n");
		loadInfo();
	}
	
	public void loadInfo() {
		String format = "";
		
		if(playlist != null) {
			for(int i = 0; i < playlist.size(); i++) {
				//taking care of "&" escape character from xml parsing
				format = playlist.get(i);
				format = format.replace("&amp;", "&");
				
				playlist2.add(new fileProperty(finder.getTitle(format), format, finder.getLength(format)));
			}

			System.out.println("loadInfo: ArrayList Playlist2 = " + playlist2 + "\n");
		}
	}
	
    
	public void savePlaylist(String path) {
		if (!path.endsWith(".xml")) {
			path = path + ".xml";
		}
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
			for(int i = 0; i < playlist2.size(); i++) {
				//Finds the file path from the arraylist
				fileName = playlist2.get(i).getFilePath();
				
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
			StreamResult result = new StreamResult(new File(path));

			transformer.transform(source, result);

			System.out.println("savePlaylist: The following file paths were saved - ArrayList Playlist = " + playlist + "\n");

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
		if(!playlist.contains(f)) {
			playlist.add(f);
			System.out.println("addMedia: The media location '" + f + "' has been added to the Playlist.");
			String format = f;
			format = format.replace("&amp;", "&");
			
			playlist2.add(new fileProperty(finder.getTitle(format), format, finder.getLength(format)));
			
			System.out.println("addMedia: New ArrayList Playlist2 = " + playlist2 + "\n"); //for debugging purposes
		}
	}
	
	public String getNextItem(String f) {
		//TODO: Check that the item being searched is not the last in the playlist (check repeat true) 
		if(shuffle == false) {
			for(int i = 0; i < playlist2.size(); i++) {
				if(playlist2.get(i).getFilePath() == f) {
					//check that the file path is not the last item in the playlistt
					if(i != playlist2.size() - 1) {
						System.out.println("getNextItem: The filepath '" + f + "' was found in the playlist.");
						System.out.println("getNextItem: Returning the following: " + playlist2.get(i + 1).getFilePath() + "\n");
						return playlist2.get(i + 1).getFilePath();
					}

					//if code reaches this far, this means that the item was the last item in the playlist
					else {
						System.out.println("getNextItem: The filepath '" + f + "' was the last item in the playlist.");

						//check if repeat is on
						if(repeat == true) {
							System.out.println("getNextItem: Because repeat is on, returning the following: " + playlist2.get(0).getFilePath() + "\n");
							return playlist2.get(0).getFilePath(); //returns first item in the playlist
						}

						else {
							System.out.println("getNextItem: Returning -1 because this was the last item in the playlist. \n");
							return "-1";
						}
					}
				}
			}
		}
		
		//shuffle is true here. searches shuffleQueue instead
		else if(shuffle == true && shuffleQueue != null) {
			for(int i = 0; i < shuffleQueue.size(); i++) {
				if(shuffleQueue.get(i).getFilePath() == f) {
					if(i != shuffleQueue.size() - 1) {
						System.out.println("getNextItem: The filepath '" + f + "' was found in the shuffleQueue playlist.");
						System.out.println("getNextItem: Returning the following: " + shuffleQueue.get(i + 1).getFilePath() + "\n");
						return shuffleQueue.get(i + 1).getFilePath();
					}
					
					else {
						System.out.println("getNextItem: The filepath '" + f + "' was the last item in the shuffleQueue playlist.");
						System.out.println("getNextItem: Returning -1 because this was the last item in the playlist. \n");
						return "-1";
					}
				}
			}
		}
		
		else if(shuffleQueue == null) {
			System.out.println("getNextItem: Shuffle has not been called. ShuffleQueue has not been created yet.");
		}
		
		//if code reaches this far, this means that the file was not in the playlist
		System.out.println("getNextItem: The file path of the media (" + f + ") was not found in any playlist. Returning -1. \n");
		return "-1";
	}

	public int getIndex(String f) {
		for(int i = 0; i < playlist2.size(); i ++) {
			if(playlist2.get(i).getFilePath() == f) {
				return i;
			}
		}
		
		return -1;
	}
	
	public String getPreviousItem(String f) {
		if(shuffle == false) {
			for(int i = 0; i < playlist2.size(); i++) {
				if(playlist2.get(i).getFilePath() == f) {
					//check that the file path is not the last item in the playlistt
					if(i != 0) {
						System.out.println("getPreviousItem: The filepath '" + f + "' was found in the playlist.");
						System.out.println("getPreviousItem: Returning the following: " + playlist2.get(i - 1).getFilePath() + "\n");
						return playlist2.get(i - 1).getFilePath();
					}

					//if code reaches this far, this means that the item was the first item in the playlist
					else {
						System.out.println("getPreviousItem: The filepath '" + f + "' was the first item in the playlist.");

						if(repeat == true) {
							System.out.println("getPreviousItem: Because repeat is on, returning the following: " + playlist2.get(playlist2.size() - 1).getFilePath() + "\n");
							return playlist2.get(playlist2.size() - 1).getFilePath(); //returns first item in the playlist
						}

						else {
							System.out.println("getPreviousItem: Returning -1 because this was the first item in the playlist. \n");
							return "-1";
						}
					}

				}
			}
		}
		
		else if(shuffle == true && shuffleQueue != null) {
			for(int i = 0; i < shuffleQueue.size(); i++) {
				if(shuffleQueue.get(i).getFilePath() == f) {
					if(i != 0 ) {
						System.out.println("getPreviousItem: The filepath '" + f + "' was found in the shuffleQueue playlist.");
						System.out.println("getPreviousItem: Returning the following: " + shuffleQueue.get(i - 1).getFilePath() + "\n");
						return shuffleQueue.get(i - 1).getFilePath();
					}
					
					else {
						System.out.println("getPreviousItem: The filepath '" + f + "' was the first item in the shuffleQueue playlist.");

						if(repeat == true) {
							System.out.println("getPreviousItem: Because repeat is on, returning the following: " + shuffleQueue.get(shuffleQueue.size() - 1).getFilePath() + "\n");
							return shuffleQueue.get(shuffleQueue.size() - 1).getFilePath(); //returns first item in the playlist
						}

						else {
							System.out.println("getPreviousItem: Returning -1 because this was the first item in the playlist.");
							return "-1";
						}
					}
				}
			}
		}
		
		else if(shuffleQueue == null) {
			System.out.println("getPreviousItem: Shuffle has not been called. ShuffleQueue has not been created yet.");
		}

		//if code reaches this far, this means that the file was not in the playlist
		System.out.println("getPreviousItem: The file path of the media (" + f + ") was not found in any playlist. Returning -1. \n");
		return "-1";
	}
	
	public void deleteItem(int index) {
		System.out.println("deleteItem: Before deleting an item - ArrayList playlist2 = " + playlist2);
		playlist2.remove(index);
		System.out.println("deleteItem: After deleting an item - ArrayList playlist2 = " + playlist2 + "\n");
	}
	
	public void shuffle() {
		if(shuffle == true) {
			shuffleQueue = new ArrayList<fileProperty>(playlist2);
			Collections.shuffle(shuffleQueue);

			System.out.println("shuffle: ArrayList Playlist2 = " + playlist2);
			System.out.println("shuffle: ArrayList shuffleQueue = " + shuffleQueue + "\n");
		}
		
		else
			System.out.println("shuffle: Shuffle is not on. Nothing was shuffled. \n");
	}
	
	public void swapPosition(String firstFile, String secondFile) {
		int index1 = 0;
		int index2 = 0;
		
		for(int i = 0; i < playlist2.size(); i++) {
			if(playlist2.get(i).getFilePath() == firstFile)
				index1 = i;
			if(playlist2.get(i).getFilePath() == secondFile)
				index2 = i;
		}
		
		System.out.println("swapPosition: Before swap - ArrayList Playlist2: " + playlist2);
		Collections.swap(playlist2, index1, index2);
		System.out.println("swapPosition: After swap - ArrayList Playlist2: " + playlist2 + "\n");
		
	}
	
	public void setRepeat(boolean r) {
		repeat = r;
	}
	
	public boolean getRepeat() {
		return repeat;
	}
	
	public void setShuffle(boolean s) {
		shuffle = s;
	}
	
	public boolean getShuffle() {
		return shuffle;
	}
	
	
	//TODO: (To NOT Do really) Don't use this anymore, this playlist still has xml formatting (and just the file paths anyway)
	public ArrayList<String> getPlaylist() {
		return playlist;
	}
	
	public ArrayList<fileProperty> getPlaylist2() {
		return playlist2;
	}
	
	public ArrayList<fileProperty> getShuffleQueue() {
		return shuffleQueue;
	}

	public static void main(String[] args) {

		Playlist test = new Playlist();
		test.init();
		//test.savePlaylist("file.xml");
		test.loadPlaylist("file.xml");
		test.addMedia("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\2. Overtime - EP - Overtime.m4a");
		test.addMedia("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\4. Overtime - EP - Satellites.m4a");
		test.addMedia("C:\\Users\\New Ending\\Music\\Lights - The Listening\\09-lights-february_air.mp3");		
		//test.deleteItem("C:\\Users\\New Ending\\Music\\Lights - The Listening\\09-lights-february_air.mp3");
		//test.shuffle();
		test.swapPosition("C:\\Users\\New Ending\\Music\\Lights - The Listening\\09-lights-february_air.mp3", "C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\2. Overtime - EP - Overtime.m4a");
		//test.getNextItem("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\4. Overtime - EP - Satellites.m4a");
		//test.getPreviousItem("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\2. Overtime - EP - Overtime.m4a");
		
	}
}

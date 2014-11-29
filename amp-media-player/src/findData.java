import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.parser.video.FLVParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.xuggle.xuggler.IContainer;

public class findData {
	private String title;
	private String length;
	
	findData() {
		title = "";
		length = "";
	}
	
	public String getTitle(String file) {
		try {
			InputStream input = new FileInputStream(new File(file));
			ContentHandler handler = new DefaultHandler();
			Metadata metaData = new Metadata();
			
			//checks file for file extensions, if doesn't find .mp3 or .flv defaults to mp4 parser
			if(file.contains(".mp3")) {
				Parser parser = new Mp3Parser();
				ParseContext parseCtx = new ParseContext();
				parser.parse(input, handler, metaData, parseCtx);
			}
			
			if(file.contains(".flv")) {
				Parser parser = new FLVParser();
				ParseContext parseCtx = new ParseContext();
				parser.parse(input, handler, metaData, parseCtx);
			}
			
			else {
				Parser parser = new MP4Parser();
				ParseContext parseCtx = new ParseContext();
				parser.parse(input, handler, metaData, parseCtx);
			}
			
			input.close();
			
			/* //Println of all inputs (for testing)
			String[] metadataNames = metaData.names();
			
			for(String name : metadataNames) {
				System.out.println(name + ": " + metaData.get(name));
			}
			
			System.out.println("Artists: " + metaData.get("xmpDM:artist"));
			System.out.println("Genre: " + metaData.get("xmpDM:genre"));
			// */
			if(metaData.get("title") != null) {
				title = metaData.get("title");
			}
			
			//title returns "Untitled" if the title is unavailable
			else {
				title = "Untitled";
			}
			
			//System.out.println("Title: " + title); //for debugging
			return title;
		}
		
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(SAXException e) {
			e.printStackTrace();
		}
		catch(TikaException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	//tested on avi, mp4, mp3
	public String getLength(String file) {
		if(file.contains(".mp3")) {
			try {
				InputStream input = new FileInputStream(new File(file));
				ContentHandler handler = new DefaultHandler();
				Metadata metaData = new Metadata();

				if(file.contains(".mp3")) {
					Parser parser = new Mp3Parser();
					ParseContext parseCtx = new ParseContext();
					parser.parse(input, handler, metaData, parseCtx);
					double seconds = Math.round(Double.valueOf(metaData.get(XMPDM.DURATION))/1000);
					int minute = (int)seconds/60;
					int second = (int)Math.round(Double.valueOf(seconds/60 - minute) * 60);
					
					if(second < 10) {
						length = minute + ":0" + second;
					}

					else {
						length = minute + ":" + second;
					}
				}

				input.close();
				//System.out.println(length); //for debugging
				return length;

			}

			catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			catch(SAXException e) {
				e.printStackTrace();
			}
			catch(TikaException e) {
				e.printStackTrace();
			}
		}
		
		else {
			IContainer container = IContainer.make();

			if(container.open(file, IContainer.Type.READ, null) >0) { //checks to see if file opens
				double duration = container.getDuration()/1000000; //total number of seconds
				container.close(); //closes file
				
				int minute = (int)duration/60;
				int second = (int) (Math.round((duration/60 - minute) * 60));

				if(second < 10) {
					length = minute + ":0" + second;
				}

				else {
					length = minute + ":" + second;
				}

				//System.out.println(length); //for debugging

				return length;
			}
		}

		return ""; 
	}
	
	public static void main(String[] args) {
		//NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC");
		//Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		findData test = new findData();
		
		test.getTitle("C:\\Users\\New Ending\\Music\\Icon For Hire - Icon For Hire (Self-Titled)\\01. Cynics & Critics.mp3");
		test.getLength("C:\\Users\\New Ending\\Music\\Icon For Hire - Icon For Hire (Self-Titled)\\01. Cynics & Critics.mp3");
		
		test.getTitle("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\2. Overtime - EP - Overtime.m4a");
		test.getLength("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\2. Overtime - EP - Overtime.m4a");
		
		test.getTitle("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\4. Overtime - EP - Satellites.m4a");
		test.getLength("C:\\Users\\New Ending\\Music\\Cash Cash - Overtime [EP] (iTunes)\\4. Overtime - EP - Satellites.m4a");
		
		test.getTitle("C:\\Users\\New Ending\\Documents\\Already Over.avi"); //TODO: THIS STILL RETURNS NULL
		test.getLength("C:\\Users\\New Ending\\Documents\\Already Over.avi");
		
		test.getTitle("C:\\Users\\New Ending\\Music\\Lights - The Listening\\09-lights-february_air.mp3");
		test.getLength("C:\\Users\\New Ending\\Music\\Lights - The Listening\\09-lights-february_air.mp3");
		
		test.getTitle("C:\\Users\\New Ending\\Music\\Krewella - Get Wet\\03 Come & Get It.m4a");
		test.getLength("C:\\Users\\New Ending\\Music\\Krewella - Get Wet\\03 Come & Get It.m4a");
	}
}

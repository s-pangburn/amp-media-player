import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class findData {
	private String title;
	private String filePath;
	private String time;
	
	//protected EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	findData(String t, String fp, String tm) {
		title = t;
		filePath = fp;
		time = tm;
		
		//mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
	}
	
	public void getTitle(String file) {
		/*
		MediaPlayerFactory factory = new MediaPlayerFactory();
		HeadlessMediaPlayer p = factory.newHeadlessMediaPlayer();
		p.playMedia(file);
		p.parseMedia();
		p.getMediaMeta();
		
		
		System.out.println("Title: " + p.getTitle());
		
		return p.getTitle();
		*/
		
		try {
			InputStream input = new FileInputStream(new File(file));
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			parser.parse(input, handler, metadata, parseCtx);
			input.close();
			
			String[] metadataNames = metadata.names();
			
			for(String name : metadataNames) {
				System.out.println(name + ": " + metadata.get(name));
			}
			
			System.out.println("Title: " + metadata.get("title"));
			System.out.println("Artists: " + metadata.get("xmpDM:artist"));
			System.out.println("Genre: " + metadata.get("xmpDM:genre"));
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
	
	public static void main(String[] args) {
		//NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC");
		//Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		findData test = new findData("Sample Title", "C:\\Users\\New Ending\\Music\\Icon For Hire - Icon For Hire (Self-Titled)\\01. Cynics & Critics.mp3", "Sample Time");
		test.getTitle("C:\\Users\\New Ending\\Music\\Icon For Hire - Icon For Hire (Self-Titled)\\01. Cynics & Critics.mp3");
	}
}

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class findData {
	private String title;
	private String filePath;
	private String time;
	
	protected EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	findData(String t, String fp, String tm) {
		title = t;
		filePath = fp;
		time = tm;
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
	}
	
	public void getTitle(String fp) {
		MediaPlayerFactory factory = new MediaPlayerFactory();
		HeadlessMediaPlayer p = factory.newHeadlessMediaPlayer();
		p.prepareMedia(fp, null);
		p.parseMedia();
		System.out.println("Title: " + p.getTitle());
		
		//return p.getTitle();
	}
	
	public static void main(String[] args) {
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
//		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC\\libvlcore.dll");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		findData test = new findData("Sample Title", "C:\\Users\\New Ending\\Music\\Krewella - Get Wet\\01 Live for the Night", "Sample Time");
		test.getTitle("C:\\Users\\New Ending\\Music\\Krewella - Get Wet\\01 Live for the Night");
	}
}

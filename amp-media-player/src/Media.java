
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class Media {
	private String fileName;
	private EmbeddedMediaPlayerComponent player; 
	
	public void init() {
		player = new EmbeddedMediaPlayerComponent();
	}
	
	public EmbeddedMediaPlayerComponent mediaPlayer() {
		return player;
	}
	
	public void playMedia(String file) {
		fileName = file;
		player.getMediaPlayer().playMedia(file);
	}
	
	public void pause() {
		
	}
	
	public void play() {
		
	}
	
	public long getTimestamp() {
		return 0;
	}
	
	public void setTimeStamp() {
		
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public static void main(final String[] args) {

        NativeLibrary.addSearchPath(
            RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC"
        );
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Media media = new Media();
                JFrame frame = new JFrame("test");
                
                frame.setContentPane(media.mediaPlayer());
                
                frame.setLocation(100,100);
                frame.setSize(1050,600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);		
                
                media.playMedia("C:\\Users\\Chalenged\\Downloads\\Smash Bros. WiiU Music Preview.mp4");
                try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                media.mediaPlayer().paused(media.mediaPlayer().getMediaPlayer());
                
                media.playMedia("C:\\Users\\Public\\Videos\\Sample Videos\\Wildlife.wmv");
                
            }
        });
	}
	
	public Media() {
		init();
        System.out.println("success1");
	}
}


import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;



public class Media {
	public static final int TIMESTAMPMAX = 1000;
	
	private String fileName;
	private MediaPlayerFactory playerFactory; 
	private EmbeddedMediaPlayer player;
	private Canvas videoSurface;
	
	public void init() {
		videoSurface = new Canvas();
		playerFactory = new MediaPlayerFactory();
		player = playerFactory.newEmbeddedMediaPlayer();
		player.setVideoSurface(playerFactory.newVideoSurface(videoSurface));
	}
	
	public Canvas mediaPlayer() {
		return videoSurface;
	}
	
	public void playMedia(String file) {
		fileName = file;
		player.playMedia(file);
	}
	
	public void pause() {
		player.pause();
	}
	
	public void play() {
		player.play();
	}
	
	public int getTimestamp() {
		return (int)(player.getPosition() * TIMESTAMPMAX);
	}
	
	public void setTimeStamp(int pos) {
		player.setPosition((float)pos / TIMESTAMPMAX);
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
                
                
                frame.add(media.mediaPlayer());
                
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
                System.out.println("rewinding...");
                
            }
        });
	}
	
	public Media() {
		init();
        System.out.println("success1");
	}
}

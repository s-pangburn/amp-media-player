
import java.awt.Canvas;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;



public class Media implements MediaPlayerEventListener {
	public static final int TIMESTAMPMAX = 1000;
	
	private String fileName;
	private MediaPlayerFactory playerFactory; 
	private EmbeddedMediaPlayer player;
	private Canvas videoSurface;
	private int loopStart, loopEnd;
	private boolean looping, fastForwarding;
	private long timeScale;	
	private boolean rewinding;
	private JSlider slider;
	
	public void init() {
		videoSurface = new Canvas();
		playerFactory = new MediaPlayerFactory();
		player = playerFactory.newEmbeddedMediaPlayer();
		player.setVideoSurface(playerFactory.newVideoSurface(videoSurface));
		looping = fastForwarding = false;
		loopStart = loopEnd = 0;
		timeScale = 1;
		rewinding = false;
	}
	
	public Canvas mediaPlayer() {
		return videoSurface;
	}
	
	public void playMedia(String file) {
		fileName = file;
		player.playMedia(file);
		looping = fastForwarding = false;
		timeScale = 1;
	}
	
	public void pause() {
		player.pause();
	}
	
	public void play() {
		player.play();
	}
	
	public void setLoop(int s, int e) {
		looping = true;
		loopStart = s;
		loopEnd = e;
	}
	
	public void stopLoop() {
		looping = false;
	}
	
	public long getLoopStart() {
		return loopStart;
	}
	
	public long getLoopEnd() {
		return loopEnd;
	}
	
	public int getTimestamp() {
		return (int)(player.getPosition() * TIMESTAMPMAX);
	}
	
	public void setTimestamp(int pos) {
		player.setPosition((float)pos / TIMESTAMPMAX);
	}
	
	public void setTimeScale(long t) {
		timeScale = t;
		if (fastForwarding) t = t*2;
		player.setRate(t);
	}
	
	public long getTimeScale() {
		return timeScale;
	}
	
	public void startFastForward() {
		fastForwarding = true;
		player.setRate(timeScale * 2);
	}
	
	public void stopFastForward() {
		fastForwarding = false;
		player.setRate(timeScale);
	}
	
	public void startRewind() {
		rewinding = true;
	}
	
	public void stopRewind() {
		rewinding = false;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setSlider(JSlider s) {
		slider = s;
	}
	
	/*
	public void update() {
		
		if (rewinding) {
			player.skip(-41);
		}
	}
	*/
	
	
	public static void main(final String[] args) {
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
                
            }
        });
	}
	
	public Media() {

        NativeLibrary.addSearchPath(
            RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC"
        );
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		init();
	}

	@Override
	public void backward(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buffering(MediaPlayer arg0, float arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endOfSubItems(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finished(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forward(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lengthChanged(MediaPlayer arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaChanged(MediaPlayer arg0, libvlc_media_t arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaDurationChanged(MediaPlayer arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaFreed(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaMetaChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaParsedChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaStateChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer arg0, libvlc_media_t arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newMedia(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opening(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pausableChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paused(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playing(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionChanged(MediaPlayer arg0, float arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seekableChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void snapshotTaken(MediaPlayer arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopped(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subItemFinished(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subItemPlayed(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeChanged(MediaPlayer arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void titleChanged(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void videoOutput(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}

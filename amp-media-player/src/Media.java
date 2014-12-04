
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;



public class Media extends MediaPlayerEventAdapter {
	public static final double TIMESTAMPMAX = 1000.0;
	
	private String fileName;
	private MediaPlayerFactory playerFactory; 
	private EmbeddedMediaPlayer player;
	private Canvas videoSurface, fullscreenCanvas;
	private int loopStart, loopEnd;
	private boolean looping, fastForwarding;
	private boolean paused = false;
	private long timeScale;	
	private boolean rewinding;
	private boolean skipNext;
	private JFrame fullscreen;
	private long toPause = -1; 
	private boolean lockFullscreen = false;

	
	public void init() {
		videoSurface = new Canvas();
		fullscreenCanvas = new Canvas();
		playerFactory = new MediaPlayerFactory();
		
		fullscreen = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //gets screen size for fullscreen
		int width = (int)screenSize.getWidth();
		int height = (int)screenSize.getHeight();
		fullscreen.setSize(width, height);
		fullscreen.setUndecorated(true); 
		fullscreenCanvas.addKeyListener(new KeyListener() { //keylisteners for fullscreen

			@Override
			public void keyPressed(KeyEvent key) {
				switch (key.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					setFullscreen(false);
					break;
				case KeyEvent.VK_SPACE:
					playPause();
					break;
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		fullscreenCanvas.addMouseListener(new MouseListener() { //mouse listener (NOTE: DOESN'T WORK)

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				//playPause();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
  				playPause();
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		player = playerFactory.newEmbeddedMediaPlayer(new DefaultFullScreenStrategy(fullscreen)); //creates the media player
		looping = fastForwarding = false;
		loopStart = loopEnd = 0;
		timeScale = 1;
		rewinding = false;
		player.addMediaPlayerEventListener(this); //adds the event listeners for things such as update
	}
	
	public void setCanvas(Canvas c) {
		player.setVideoSurface(playerFactory.newVideoSurface(c)); //sets video on the canvas
		videoSurface = c; 
	}
	
	public EmbeddedMediaPlayer mediaPlayer() {
		return player;
	}
	
	public void playMedia(String file) {
		fileName = file;
		player.playMedia(file);
		looping = fastForwarding = false;
		timeScale = 1;
	}
	
	public void pause() {
		player.pause();
		paused = true;
	}
	
	public void play() {
		player.play();
		paused = false;
	}
	
	public void playPause() { //toggles between play/pause
		if (player.isPlaying()) {
			pause();
			paused = true;
		}
		else {
			play();
			paused = false;
		}
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
		return (int)(player.getPosition() * TIMESTAMPMAX); //returns position from 0 to 1000 (default)
	}
	
	public void setTimestamp(int pos) {
		player.setPosition((float)(pos / TIMESTAMPMAX)); //sets position on scale from 0 to 1000 (default)
	}
	
	public void setTimeScale(long t) {
		timeScale = t;
		if (fastForwarding) t = t*2; //fastforwarding is twice as fast
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
	
	public long getCurrentTime() {
		return player.getTime();
	}
	
	public boolean shouldSkip() {
		return skipNext;
	}
	
	public void setSkipToFalse() {
		skipNext = false;
	}
	
	public void toggleFullscreen() {
		setFullscreen(!fullscreenCanvas.isVisible()); 
	}
	
	public void setFullscreen(boolean f) {
		if (fileName != null) { //ensures something is playing
//		if (f) {
//			toPause = !player.isPlaying();
			if (lockFullscreen) return; //prevents fullscreen from happening too quickly to give VLC time to set up
			lockFullscreen = true;
			if (player.isPlaying()) toPause = -1; 
			else {
				toPause = player.getTime(); //sets to pause to the time the video is at
				player.mute(true); //mutes to make pausing not play sound when switching between fullscreen
			}
			long t = player.getTime(); //t is to reset time after changing video to fullscreen
			player.stop(); //stops the player so it can switch to/from fullscreen window
			fullscreenCanvas.setVisible(f);
			player.setVideoSurface(playerFactory.newVideoSurface((f) ? fullscreenCanvas : videoSurface)); //sets the surface to appropriate canvas
			if (f) fullscreen.add(fullscreenCanvas);
			fullscreen.setAlwaysOnTop(f);
			fullscreen.setVisible(f);
			if (f) fullscreenCanvas.requestFocus(); //gives canvas focus for the keylisteners
			player.play();
			player.setTime(t); //resets the time to where it was
			if (paused) {
				player.pause();
			}
		}
//		} else {
//			long t = player.getTime();
//			player.stop();
//			fullscreenCanvas.setVisible(false);
//			player.setVideoSurface(playerFactory.newVideoSurface(videoSurface));
//			fullscreen.setAlwaysOnTop(false);
//			fullscreen.setVisible(false);
//			player.play();
//			player.setTime(t);
//		}
	}
	
	
	
	public void update() {
		if (looping) {
			if (getTimestamp() > loopEnd) { //if past timestamp set back to beginning
				setTimestamp(loopStart);
			}
			else if (getTimestamp() < loopStart) { //if before timestamp set to beginning
				setTimestamp(loopStart);
			}
		}
		if (toPause > -1) { //video won't pause right after loading, so this is a workaround
			pause();
			player.setTime(toPause);
			player.mute(false);
			toPause = -1;
		}
		lockFullscreen = false; //allows fullscreen to happen
	}
	
	public long getLength() {
		return player.getLength();
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	
	public static void main(final String[] args) { //test main
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final Media media = new Media();
                JFrame frame = new JFrame("test");
                
                Canvas canvas = new Canvas();
        		frame.addKeyListener(new KeyListener() {

        			@Override
        			public void keyPressed(KeyEvent key) {
        				switch (key.getKeyCode()) {
        				case KeyEvent.VK_F:
        					media.setFullscreen(true);
        					break;
        				case KeyEvent.VK_SPACE:
        					media.playPause();
        					break;
        				}
        			}
        			@Override
        			public void keyReleased(KeyEvent arg0) {
        			}
        			@Override
        			public void keyTyped(KeyEvent arg0) {
        			}
        		});
                canvas.setVisible(true);
                
                
                media.setCanvas(canvas);
                
                frame.add(canvas);
                //frame.add(media.mediaPlayer());
                
        		
                frame.setLocation(100,100);
                frame.setSize(1050,600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);		
                
                media.playMedia("C:\\Users\\Chalenged\\Downloads\\Advanced Warfare In Depth- Gung-Ho Perk (Shotgun Penalty, Bugs, & Accuracy).mp4");
//                try {
//                	Thread.sleep(1000);
//                } catch(InterruptedException ex) {} 
//                media.pause();
                
////                
//                try {
//                	Thread.sleep(5000);
//                } catch(InterruptedException ex) {} 
//                media.setFullscreen(true);
//
//                try {
//                	Thread.sleep(5000);
//                } catch(InterruptedException ex) {} 
//                media.setFullscreen(false);
//                media.setLoop(490, 510);
//                media.setTimestamp(800);
            }
        });
	}
	
	public Media() {

        NativeLibrary.addSearchPath( //searchpath for VLC libraries (32 bit only)
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
		skipNext = true; //used for GUI 
		player.playMedia(fileName); //replays media
		toPause = 0; //pauses at beginning
		//player.setPosition(temp);
//		player.pause();
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
		//player.pause();
		
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
		update(); //calls update function often
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


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalSliderUI;

public class GUI extends JFrame {
	
	private Playlist playlist;
	public Media media;
	private JSlider slider;
	Icon play = new ImageIcon("play.png");
	Icon pause = new ImageIcon("pause.png");
	Icon next = new ImageIcon("next.png");
	Icon prev = new ImageIcon("prev.png");
	Icon shuffle = new ImageIcon("shuffle.png");
	Icon repeat = new ImageIcon("repeat.png");
	Icon savelist = new ImageIcon("save.png");
	final JButton playButton = new JButton(play);
	
	private class ToolBar extends JToolBar {
		@Override
		protected void paintComponent(Graphics g){
		    // Do nothing
		}
	}
	
	public GUI() {
        playlist = new Playlist();
        media = new Media();
        init();
    }
	
	
	private void init() {
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());

		drawControls(pane);
		drawMenu(pane);
		drawPlaylist(pane);
		
		pack();
		
		setTitle("AMP Media Player");
		setMinimumSize(new Dimension(650, 420));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Timer timer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				slider.setValue(media.getTimestamp());
			}
		});
		timer.setInitialDelay(1);
		timer.start(); 
	}
	
	private void drawMenu(Container pane) {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		JMenuBar menu = new JMenuBar();
		//ImageIcon icon = new ImageIcon("XXXX.png");
		
		JMenu file = new JMenu("File");
		
		JMenuItem open = new JMenuItem("Open");
		open.setToolTipText("Open a file");
		
		JMenu edit = new JMenu("Edit");
		
		JMenuItem nothing = new JMenuItem("Nothing yet");
		nothing.setToolTipText("This won't do anything");
		
		JMenu view = new JMenu("View");
		
		JMenuItem nothing2 = new JMenuItem("Also nothing");
		nothing2.setToolTipText("This also won't do anything");
		
		JMenu filter = new JMenu("Filter");
		
		JMenuItem nothing3 = new JMenuItem("Nope");
		nothing3.setToolTipText("This definitely won't do anything");
		
		file.add(open);
		edit.add(nothing);
		view.add(nothing2);
		filter.add(nothing3);
		
		menu.add(file);
		menu.add(edit);
		menu.add(view);
		menu.add(filter);
		
		setJMenuBar(menu);

		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				openFile();
				playButton.setIcon(pause);
			}
		});
	}

	
	private void drawControls(Container pane) {
		GridBagConstraints params = new GridBagConstraints();
		
		Canvas display = new Canvas();
		display.setBackground(Color.black);
		setConstraints(params, 0, 0, 3, 5, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 2, 10);
//		display.add(media.mediaPlayer());
		pane.add(display, params);
		media.setCanvas(display);
		
		slider = new JSlider(0, 1000, 0);
		setConstraints(params, 0, 3, 1, 5, 1, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(2, 10, 10, 10);
		initSlider(slider);
		pane.add(slider, params);
		
		JLabel blank1 = new JLabel("");
		setConstraints(params, 0, 4, 1, 1, 1, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		pane.add(blank1, params);
		
		JButton prevButton = new JButton(prev);
		setConstraints(params, 1, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(5, 5, 5, 5);
		pane.add(prevButton, params);
		
		//Playbutton
		playButton.setIcon(play);
		setConstraints(params, 2, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		pane.add(playButton, params);
		
		JButton skipButton = new JButton(next);
		setConstraints(params, 3, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		pane.add(skipButton, params);
		
		JLabel blank2 = new JLabel("");
		setConstraints(params, 4, 4, 1, 1, 1, 0, GridBagConstraints.LAST_LINE_END, GridBagConstraints.BOTH);
		pane.add(blank2, params);

		
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Go to previous media");
			}
		});
		
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (playButton.getIcon() == play) {
					playButton.setIcon(pause);
					media.play();
				} else {
					playButton.setIcon(play);
					media.pause();
				}
			}
		});
		
		skipButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Go to next media");
			}
		});
		
		
		//Test code

	}
	
	
	private void drawPlaylist(Container pane) {
		GridBagConstraints params = new GridBagConstraints();
		
		JLabel listTitle = new JLabel("Playlist Title");
		setConstraints(params, 5, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 0, 0);
		pane.add(listTitle, params);

		JLabel blank1 = new JLabel("");
		setConstraints(params, 6, 0, 1, 1, 0.1, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		pane.add(blank1, params);
		
		JButton save = new JButton(savelist);
		save.setToolTipText("Save Playlist");
		setConstraints(params, 7, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 0, 11);
		pane.add(save, params);
		
		ToolBar toolbar = new ToolBar();
		setConstraints(params, 5, 1, 1, 3, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 9, 0, 10);
		JToggleButton shuffleButton = new JToggleButton(shuffle);
		shuffleButton.setToolTipText("Shuffle");
		toolbar.add(shuffleButton);
		JToggleButton repeatButton = new JToggleButton(repeat);
		repeatButton.setToolTipText("Repeat");
		toolbar.add(repeatButton);
		toolbar.add(Box.createHorizontalGlue());
		JButton newPlaylistButton = new JButton("New");
		toolbar.add(newPlaylistButton);
		JButton loadPlaylistButton = new JButton("Load");
		toolbar.add(loadPlaylistButton);
		JButton addPlaylistButton = new JButton("Add");
		toolbar.add(addPlaylistButton);
		JButton delPlaylistButton = new JButton("Del");
		toolbar.add(delPlaylistButton);
		toolbar.setFloatable(false);
		pane.add(toolbar, params);
		
		String[] data = new String[playlist.getPlaylist().size()];
		populateArrayFromList(data, playlist.getPlaylist());
		
		JList<String> plist = new JList<String>(data);
		setConstraints(params, 5, 2, 3, 3, 0, 1, GridBagConstraints.LINE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 10, 10, 10);
		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(plist);
		pane.add(scroll, params);
		
	}
	
	private <T> void populateArrayFromList(T[] arr, ArrayList<T> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			arr[i] = arrayList.get(i);
		}
    }


	private void initSlider(JSlider slider) {
		/*
		 * Sets the slider to jump to a position on-click
		 */
		slider.setUI(new MetalSliderUI() {
			  protected TrackListener createTrackListener(JSlider slider) {
				    return new TrackListener() {
				    	public int value;
				      @Override public void mousePressed(MouseEvent e) {
				        JSlider slider = (JSlider)e.getSource();
				        switch (slider.getOrientation()) {
				          case JSlider.VERTICAL:
				            slider.setValue(valueForYPosition(e.getY()));
				            value = slider.getValue();
				            break;
				          case JSlider.HORIZONTAL:
				            slider.setValue(valueForXPosition(e.getX()));
				            value = slider.getValue();
				            break;
				        }
				        super.mousePressed(e); //isDragging = true;
				        super.mouseDragged(e);
				      }
				      @Override public void mouseDragged(MouseEvent e) {
					      super.mouseDragged(e);
					      JSlider slider = (JSlider)e.getSource();
					      value = slider.getValue();
				      }
				      @Override public void mouseReleased(MouseEvent e) {
					      JSlider slider = (JSlider)e.getSource();
				    	  media.setTimestamp(value);
				    	  super.mouseReleased(e);
				      }
				      /*@Override public boolean shouldScroll(int direction) {
				        return false;
				      }*/
				    };
				  }
		});
	}


	private void setConstraints(GridBagConstraints params, int x, int y, int height, 
								int width, double weightx, double weighty) {
		params.gridx = x;
		params.gridy = y;
		params.gridheight = height;
		params.gridwidth = width;
		params.weightx = weightx;
		params.weighty = weighty;
		params.anchor = GridBagConstraints.CENTER;
		params.fill = GridBagConstraints.NONE;
	}
	
	private void setConstraints(GridBagConstraints params, int x, int y, int height, 
								int width, double weightx, double weighty, int anchor, int fill) {
		params.gridx = x;
		params.gridy = y;
		params.gridheight = height;
		params.gridwidth = width;
		params.weightx = weightx;
		params.weighty = weighty;
		params.anchor = anchor;
		params.fill = fill;
	}
	
	private void openFile() {
		JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("mp4 files", "mp4");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(new JPanel(), "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = fileopen.getSelectedFile().toString();
            media.playMedia(file);
        }
	}
 
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI gui = new GUI();
                gui.setVisible(true);
            }
        });
    }
}
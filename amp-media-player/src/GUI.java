
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
	final Container pane = getContentPane();
	JList<String> plist;
	final JButton playButton = new JButton(play);
	
	/*
	 * TODO: File formats
	 * TODO: Implement Delete
	 * TODO: Implement rearrange
	 * TODO: FullScreen
	 * TODO: Looping
	 */
	
	private class ToolBar extends JToolBar {
		@Override
		protected void paintComponent(Graphics g){
		    // Do nothing (removes gradient from JToolBar component)
		}
	}
	
	public GUI() {
        playlist = new Playlist();
        media = new Media();
        init();
    }
	
	
	private void init() {
		pane.setLayout(new GridBagLayout());

		drawControls();
		drawMenu();
		drawPlaylist();
		
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
	
	
	private void drawMenu() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);  // Remedies overlapping glitch
		JMenuBar menu = new JMenuBar();
		
		JMenu file = new JMenu("File");
		
		JMenuItem open = new JMenuItem("Open");
		open.setToolTipText("Open a file");
		JMenuItem openPList = new JMenuItem("Open Playlist");
		open.setToolTipText("Open a Playlist.xml file");
		
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
				String file = openFile();
				if (isValidFileType(file)) {
					media.playMedia(file);
					playButton.setIcon(pause);
				} else {
					JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			
		});
		
		openPList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				openPlaylist();
			}
		});
	}

	
	private void drawControls() {
		GridBagConstraints params = new GridBagConstraints();
		
		//Media screen
		Canvas display = new Canvas();
		display.setBackground(Color.black);
		setConstraints(params, 0, 0, 3, 6, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 2, 10);
		pane.add(display, params);
		media.setCanvas(display);
		
		//Timestamp slider
		slider = new JSlider(0, 1000, 0);
		setConstraints(params, 0, 3, 1, 6, 1, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(2, 10, 10, 10);
		initSlider(slider);
		pane.add(slider, params);
		
		//Speed control
		String[] speeds = new String[] {"0.25x", "0.5x", "1x", "2x", "4x"};
		final JComboBox<String> box = new JComboBox<String>(speeds);
		setConstraints(params, 0, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(5, 5, 5, 5);
		box.setSelectedItem("1x");
		//pane.add(box,  params);
		
		//Whitespace
		JLabel blank1 = new JLabel("");
		setConstraints(params, 1, 4, 1, 1, 1, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		pane.add(blank1, params);
		
		//Skip back
		JButton prevButton = new JButton(prev);
		setConstraints(params, 2, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(5, 5, 5, 5);
		pane.add(prevButton, params);
		
		//Play/Pause
		playButton.setIcon(play);
		setConstraints(params, 3, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		pane.add(playButton, params);
		
		//Skip forward
		JButton skipButton = new JButton(next);
		setConstraints(params, 4, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		pane.add(skipButton, params);
		
		//Whitespace
		JLabel blank2 = new JLabel("");
		setConstraints(params, 5, 4, 1, 1, 1, 0, GridBagConstraints.LAST_LINE_END, GridBagConstraints.BOTH);
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

		box.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
	            if (box.getSelectedItem() == "0.25x") {
	            	media.setTimeScale((long) 0.25);
	            }
	            if (box.getSelectedItem() == "0.5x") {
	            	media.setTimeScale((long) 0.5);
	            }
	            if (box.getSelectedItem() == "1x") {
	            	media.setTimeScale((long) 1);
	            }
	            if (box.getSelectedItem() == "2x") {
	            	media.setTimeScale((long) 2);
	            }
	            if (box.getSelectedItem() == "4x") {
	            	media.setTimeScale((long) 4);
	            }
	        }
		});

	}
	
	
	private void drawPlaylist() {
		GridBagConstraints params = new GridBagConstraints();
		
		//Playlist name
		JLabel listTitle = new JLabel("New Playlist");
		setConstraints(params, 6, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 0, 0);
		pane.add(listTitle, params);

		//Whitespace
		JLabel blank1 = new JLabel("");
		setConstraints(params, 7, 0, 1, 1, 0.1, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		pane.add(blank1, params);
		
		//Save Button
		JButton save = new JButton(savelist);
		save.setToolTipText("Save Playlist");
		setConstraints(params, 8, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 0, 11);
		pane.add(save, params);
		
		//New, Load, Add, Del, Shuffle, Repeat bar
		ToolBar toolbar = new ToolBar();
		setConstraints(params, 6, 1, 1, 3, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 9, 0, 10);
		initToolBar(toolbar, listTitle);
		pane.add(toolbar, params);
		
		//Collect playlist data
		String[] data = new String[playlist.getPlaylist().size()];
		populateArrayFromList(data, playlist.getPlaylist());
		
		//Playlist display
		plist = new JList<String>(data);
		setConstraints(params, 6, 2, 3, 3, 0, 1, GridBagConstraints.LINE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 10, 10, 10);
		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(plist);
		pane.add(scroll, params);
	}


	private void initToolBar(ToolBar toolbar, final JLabel title) {
		JToggleButton shuffleButton = new JToggleButton(shuffle);
		shuffleButton.setToolTipText("Shuffle");
		toolbar.add(shuffleButton);
		
		JToggleButton repeatButton = new JToggleButton(repeat);
		repeatButton.setToolTipText("Repeat");
		toolbar.add(repeatButton);
		
		toolbar.add(Box.createHorizontalGlue());	// Whitespace
		
		JButton newPlaylistButton = new JButton("New");
		toolbar.add(newPlaylistButton);
		
		JButton loadPlaylistButton = new JButton("Load");
		toolbar.add(loadPlaylistButton);
		
		JButton addPlaylistButton = new JButton("Add");
		toolbar.add(addPlaylistButton);
		
		JButton delPlaylistButton = new JButton("Del");
		toolbar.add(delPlaylistButton);
		
		toolbar.setFloatable(false);	//Prevent dragging
		
		
		newPlaylistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playlist.clearPlaylist();
				title.setText("New Playlist");
				refreshPlaylist();
			}
		});
		
		
		loadPlaylistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				openPlaylist();
				refreshPlaylist();
			}
		});
		
		
		addPlaylistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String file = openFile();
				if (isValidFileType(file)) {
					playlist.addMedia(file);
					refreshPlaylist();
				} else {
					JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		
		delPlaylistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//TODO: Implement
				refreshPlaylist();
			}
		});
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

	
	private void refreshPlaylist() {
		String[] data = new String[playlist.getPlaylist().size()];
		populateArrayFromList(data, playlist.getPlaylist());
		plist = new JList<String>(data);
	}
	
	
	private <T> void populateArrayFromList(T[] arr, ArrayList<T> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			arr[i] = arrayList.get(i);
		}
    }



//	private void setConstraints(GridBagConstraints params, int x, int y, int height, 
//								int width, double weightx, double weighty) {
//		params.gridx = x;
//		params.gridy = y;
//		params.gridheight = height;
//		params.gridwidth = width;
//		params.weightx = weightx;
//		params.weighty = weighty;
//		params.anchor = GridBagConstraints.CENTER;
//		params.fill = GridBagConstraints.NONE;
//	}
	
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
	
	private String openFile() {
		JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("mp4 files", "mp4");
        fileopen.addChoosableFileFilter(filter);
        fileopen.setFileFilter(filter);
        filter = new FileNameExtensionFilter("mp3 files", "mp3");
        fileopen.addChoosableFileFilter(filter);
        filter = new FileNameExtensionFilter("avi files", "avi");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(new JPanel(), "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = fileopen.getSelectedFile().toString();
            return file;
        }
        return "-1";
	}
	
	
	private void openPlaylist() {
		JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        fileopen.setFileFilter(filter);

        int ret = fileopen.showDialog(new JPanel(), "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = fileopen.getSelectedFile().toString();
            if (file.endsWith("xml")) {
            	playlist.loadPlaylist(file);
			} else {
				JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
			}
        }
	}
	
	
	private boolean isValidFileType(String file) {
		return file.endsWith("mp4") || file.endsWith("mp3") || file.endsWith("avi");
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


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultListModel;
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
	final Container pane = getContentPane();
	JList<String> plist;
	DefaultListModel list = new DefaultListModel();
	File lastDirectory = null;
	Icon play = new ImageIcon("play.png");
	Icon pause = new ImageIcon("pause.png");
	Icon next = new ImageIcon("next.png");
	Icon prev = new ImageIcon("prev.png");
	Icon shuffle = new ImageIcon("shuffle.png");
	Icon repeat = new ImageIcon("repeat.png");
	Icon savelist = new ImageIcon("save.png");
	final JButton playButton = new JButton(play);
	final JLabel listTitle = new JLabel("New Playlist");
	
	/*
	 * TODO: Supported File formats??
	 * TODO: Implement Delete
	 * TODO: Implement rearrange
	 * TODO: Shuffle
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
//      playlist.addMedia("C:\\Users\\Stephen\\Videos\\ArmchairTheatre-TheCriminals.mp4");
//		playlist.addMedia("C:\\Users\\Stephen\\Videos\\The Sign of Three.mp4");
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
		
		JMenuItem open = new JMenuItem("Open Media");
		open.setToolTipText("Open a file");
		JMenuItem newPList = new JMenuItem("New Playlist");
		newPList.setToolTipText("Create a new playlist");
		JMenuItem openPList = new JMenuItem("Open Playlist");
		openPList.setToolTipText("Open a Playlist.xml file");
		JMenuItem savePList = new JMenuItem("Save Playlist");
		savePList.setToolTipText("Save current playlist as xml");
		JMenuItem quit = new JMenuItem("Exit");
		quit.setToolTipText("Exit program");
		
		JMenu edit = new JMenu("Edit");
		
		JMenuItem addPList = new JMenuItem("Add file to Playlist");
		addPList.setToolTipText("Add a new file to the playlist");
		JMenuItem addPList2 = new JMenuItem("Add current to Playlist");
		addPList2.setToolTipText("Add current media to the playlist");
		
		JMenu view = new JMenu("View");
		
		JMenuItem nothing2 = new JMenuItem("Also nothing");
		nothing2.setToolTipText("This also won't do anything");
		
		JMenu filter = new JMenu("Help");
		
		JMenuItem about = new JMenuItem("About...");
		about.setToolTipText("This doesn't do anything yet");
		
		file.add(open);
		file.add(newPList);
		file.add(openPList);
		file.add(savePList);
		file.add(quit);
		edit.add(addPList);
		edit.add(addPList2);
		view.add(nothing2);
		filter.add(about);
		
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
				} else if (file != "-1") {
					JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			
		});
		
		newPList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playlist.clearPlaylist();
				listTitle.setText("New Playlist");
				list.clear();
			}
		});
		
		openPList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				openPlaylist();
				refreshPlaylist();
			}
		});
		
		savePList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				savePlaylist();
			}
		});
		
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		addPList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String file = openFile();
				if (isValidFileType(file)) {
					playlist.addMedia(file);
					list.clear();
					refreshPlaylist();
				} else if (file != "-1") {
					JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		addPList2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String file = media.getFileName();
				if (media.getFileName() != null) {
					playlist.addMedia(file);
					list.clear();
					refreshPlaylist();
				}
			}
		});
		
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(pane, "AMP Media Player\n\nBy Lucas Jovalis, Matthew Luu, and Stephen Pangburn", 
						"About AMP Media Player", JOptionPane.INFORMATION_MESSAGE);
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
		
		//Time-stamp slider
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
		setConstraints(params, 0, 4, 2, 1, 1, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
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
				String prev = playlist.getPreviousItem(media.getFileName());
				if (prev != "-1") {
					media.playMedia(prev);
					plist.setSelectedIndex(playlist.getIndex(prev));
				}
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
				String next = playlist.getNextItem(media.getFileName());
				if (next != "-1") {
					media.playMedia(next);
					plist.setSelectedIndex(playlist.getIndex(next));
				}
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
		
		//Playlist display
		plist = new JList<String>(list);
		setConstraints(params, 6, 2, 3, 3, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 10, 10, 10);
		plist.setFixedCellWidth(50);
		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(plist);
		pane.add(scroll, params);
		
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				savePlaylist();
			}
		});
		
		
		plist.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        JList list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {
		        	Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex()); 
		        	if (r != null && r.contains(evt.getPoint())) {
		        		int index = list.locationToIndex(evt.getPoint());
		            	media.playMedia(playlist.getPlaylist2().get(index).getFilePath());
						playButton.setIcon(pause);
		        	}
		        }
		    }
		});
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
		
		
		repeatButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange()==ItemEvent.SELECTED){
					playlist.setRepeat(true);
				} else if (ev.getStateChange()==ItemEvent.DESELECTED){
					playlist.setRepeat(false);
				}
			}
		});
		
		newPlaylistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playlist.clearPlaylist();
				title.setText("New Playlist");
				list.clear();
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
					list.clear();
					refreshPlaylist();
				} else if (file != "-1") {
					JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		
		delPlaylistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//TODO: Implement
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

//	
//	private void refreshPlaylist() {
//		String[] data = new String[playlist.getPlaylist2().size()];
//		populateArrayFromList(data, playlist.getPlaylist2());
//		plist = new JList<String>(data);
//		plist.revalidate();
//	}
//	
//	
//	private <T> void populateArrayFromList(String[] data, ArrayList<fileProperty> arrayList) {
//		for (int i = 0; i < arrayList.size(); i++) {
//			data[i] = arrayList.get(i).getTitle();
//		}
//    }



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
        filter = new FileNameExtensionFilter("mp3 files", "mp3");
        fileopen.addChoosableFileFilter(filter);
        filter = new FileNameExtensionFilter("avi files", "avi");
        fileopen.addChoosableFileFilter(filter);
        fileopen.setCurrentDirectory(lastDirectory);

        int ret = fileopen.showDialog(new JPanel(), "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = fileopen.getSelectedFile().toString();
            lastDirectory = fileopen.getSelectedFile();
            if (isValidFileType(file))
            	return file;
        }
        return "-1";
	}
	
	
	private void openPlaylist() {
		playlist.clearPlaylist();
		list.clear();
		
		JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        fileopen.setFileFilter(filter);

        int ret = fileopen.showDialog(new JPanel(), "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = fileopen.getSelectedFile().toString();
            if (file.endsWith("xml")) {
            	playlist.loadPlaylist(file);
                listTitle.setText(fileopen.getSelectedFile().getName().substring(0, fileopen.getSelectedFile().getName().length()-4));
			} else if (file != "-1") {
				JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
			}
        }
	}
	
	
	private void refreshPlaylist() {
		for (int i = 0; i < playlist.getPlaylist2().size(); i++) {
			list.addElement(playlist.getPlaylist2().get(i).getTitle() + " (" + playlist.getPlaylist2().get(i).getLength() + ")");
		}
	}
	
	
	private void savePlaylist() {
		JFileChooser savefile = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("xml file", "xml");
        savefile.addChoosableFileFilter(filter);
        savefile.setFileFilter(filter);
        savefile.setCurrentDirectory(lastDirectory);

        int ret = savefile.showSaveDialog(new JPanel());

        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = savefile.getSelectedFile().toString();
            lastDirectory = savefile.getSelectedFile();
            playlist.savePlaylist(file);
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

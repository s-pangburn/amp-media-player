
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import java.sql.Timestamp;
import java.text.NumberFormat;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalSliderUI;

public class GUI extends JFrame {

	public Media media;
	private Playlist playlist;
	private JSlider slider;
	private RangeSlider loopSlider;
	private JToggleButton loopButton;
	private final Container pane = getContentPane();
	private final JLabel listTitle = new JLabel("New Playlist");
	private JList<String> plist;
	private DefaultListModel list = new DefaultListModel();
	private File lastDirectory = null;
	private Timestamp currentTime = new Timestamp((long) 0.0);
	private Timestamp totalTime = new Timestamp ((long) 0.0);
	private JLabel timeDisplay;
	
	private Icon play = new ImageIcon("play.png");
	private Icon pause = new ImageIcon("pause.png");
	private Icon next = new ImageIcon("next.png");
	private Icon prev = new ImageIcon("prev.png");
	private Icon shuffle = new ImageIcon("shuffle.png");
	private Icon repeat = new ImageIcon("repeat.png");
	private Icon savelist = new ImageIcon("save.png");
	private Icon loop = new ImageIcon("loop.png");
	private Icon fullscreen = new ImageIcon("fullscreen.png");
	private final JButton playButton = new JButton(play);
	
	/*
	 * TODO: Implement rearrange (ME, MATT)
	 * TODO: FullScreen (LUCAS, ME)
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
		
		
		setTitle("AMP Media Player");
		setMinimumSize(new Dimension(650, 420));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		pack();
		
		Timer timer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				slider.setValue(media.getTimestamp());
				updateTimestamp();
				if (media.shouldSkip()) {
					skip(playlist.getNextItem(media.getFileName()));
					media.setSkipToFalse();
				}
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
		JMenuItem openPList = new JMenuItem("Load Playlist");
		openPList.setToolTipText("Load a playlist from xml file");
		JMenuItem savePList = new JMenuItem("Save Playlist");
		savePList.setToolTipText("Save current playlist as xml");
		JMenuItem quit = new JMenuItem("Exit");
		quit.setToolTipText("Exit program");
		
		JMenu edit = new JMenu("Edit");
		
		JMenuItem addPList = new JMenuItem("Add file to Playlist");
		addPList.setToolTipText("Add a new file to the playlist");
		JMenuItem addPList2 = new JMenuItem("Add current to Playlist");
		addPList2.setToolTipText("Add current media to the playlist");
		JMenuItem delPList = new JMenuItem("Delete from Playlist");
		addPList2.setToolTipText("Delete selected item from Playlist");
		
		JMenu view = new JMenu("View");
		
		JMenuItem fullscreen = new JMenuItem("Fullscreen (Not functional)");
		fullscreen.setToolTipText("This won't do anything");
		
		JMenu help = new JMenu("Help");
		
		JMenuItem about = new JMenuItem("About...");
		about.setToolTipText("About AMP Media Player");
		
		file.add(open);
		file.add(newPList);
		file.add(openPList);
		file.add(savePList);
		file.add(quit);
		edit.add(addPList);
		edit.add(addPList2);
		edit.add(delPList);
		view.add(fullscreen);
		help.add(about);
		
		menu.add(file);
		menu.add(edit);
		menu.add(view);
		menu.add(help);
		
		setJMenuBar(menu);

		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String file = openFile();
				if (isValidFileType(file)) {
					media.playMedia(file);
	            	setTimeDisplay();
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
		
		delPList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int item = plist.getSelectedIndex();
				list.remove(item);
				playlist.deleteItem(item); 
				refreshPlaylist();
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
		
		fullscreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				media.setFullscreen(true);
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
		setConstraints(params, 0, 0, 3, 7, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 2, 10);
		pane.add(display, params);
		media.setCanvas(display);
		
		//Time-stamp slider
		slider = new JSlider(0, 1000, 0);
		setConstraints(params, 0, 3, 1, 6, 0, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(2, 10, 10, 2);
		initSlider(slider);
		pane.add(slider, params);

		//Looping slider
		loopSlider = new RangeSlider(0, 1000);
		setConstraints(params, 0, 3, 1, 6, 1, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(2, 10, 10, 2);
		loopSlider.setValue(0);
        loopSlider.setUpperValue(0);
		
		//Timestamp label
		timeDisplay = new JLabel("0:00/0:00");
		setConstraints(params, 6, 3, 1, 1, 0, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		Font font = new Font("Arial", Font.PLAIN, 10);
		timeDisplay.setFont(font);
		pane.add(timeDisplay, params);
		
		//Loop toggle
		loopButton = new JToggleButton(loop);
		setConstraints(params, 0, 4, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(5, 5, 5, 5);
		loopButton.setToolTipText("Start a new loop");
		loopButton.setPreferredSize(new Dimension(45, 25));
		loopButton.setMaximumSize(new Dimension(45, 25));
		loopButton.setMinimumSize(new Dimension(45, 25));
		pane.add(loopButton,  params);
		
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
		
		//Fullscreen button
		JButton fullscreenButton = new JButton(fullscreen);
		setConstraints(params, 6, 4, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_END, GridBagConstraints.VERTICAL);
		params.insets = new Insets(5, 5, 5, 5);
		fullscreenButton.setToolTipText("Fullscreen mode");
		fullscreenButton.setPreferredSize(new Dimension(45, 25));
		fullscreenButton.setMaximumSize(new Dimension(45, 30));
		fullscreenButton.setMinimumSize(new Dimension(45, 30));
		pane.add(fullscreenButton,  params);

		
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (media.getCurrentTime() < 2000) {
					skip(playlist.getPreviousItem(media.getFileName()));
				} else {
					media.setTimestamp(0);
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
				skip(playlist.getNextItem(media.getFileName()));
			}
			
		});
		
		fullscreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				media.setFullscreen(true);
			}
			
		});

		loopButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange()==ItemEvent.SELECTED){
					GridBagConstraints params = new GridBagConstraints();
					setConstraints(params, 0, 3, 1, 6, 1, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
					params.insets = new Insets(2, 10, 10, 2);
					if (loopSlider.getValue() == 0 && loopSlider.getUpperValue() == 0) {
				        loopSlider.setValue(media.getTimestamp());
				        loopSlider.setUpperValue(media.getTimestamp() + 300);
					}
			        loopSlider.setPreferredSize(new Dimension(100, 25));
					pane.remove(slider);
					pane.add(loopSlider, params);
					pane.revalidate();
	                media.setLoop(loopSlider.getValue(), loopSlider.getUpperValue());
				} else if (ev.getStateChange()==ItemEvent.DESELECTED){
					GridBagConstraints params = new GridBagConstraints();
					setConstraints(params, 0, 3, 1, 6, 1, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
					params.insets = new Insets(2, 10, 10, 2);
					pane.remove(loopSlider);
					pane.add(slider, params);
					pane.revalidate();
					media.stopLoop();
				}
			}
		});
		
		loopSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RangeSlider s = (RangeSlider) e.getSource();
                media.setLoop(s.getValue(), s.getUpperValue());
                if (media.getTimestamp() < s.getValue() || media.getTimestamp() > s.getUpperValue()) {
                	media.setTimestamp(s.getValue());
                } else {
                	media.setTimestamp(media.getTimestamp());
                }
            }
        });

	}
	
	
	private void drawPlaylist() {
		GridBagConstraints params = new GridBagConstraints();
		
		//Playlist name
		Font font = new Font("Arial", Font.BOLD, 16);
		listTitle.setFont(font);
		listTitle.setPreferredSize(new Dimension(95, 16));
		listTitle.setMaximumSize(new Dimension(95, 16));
		listTitle.setMinimumSize(new Dimension(95, 16));
		setConstraints(params, 7, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 0, 0);
		pane.add(listTitle, params);

		//Whitespace
		JLabel blank1 = new JLabel("");
		setConstraints(params, 8, 0, 1, 1, 0.1, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		pane.add(blank1, params);
		
		//Rearrange buttons
		setConstraints(params, 9, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 0, 0, 0);
		
//		JButton shuffleButton = new JButton(shuffle);
//		shuffleButton.setToolTipText("Shift item up");
//		pane.add(shuffleButton);
//		
//		JButton repeatButton = new JButton(repeat);
//		repeatButton.setToolTipText("Shift item down");
//		pane.add(repeatButton);
		
		//Save Button
		JButton save = new JButton(savelist);
		save.setToolTipText("Save Playlist");
		setConstraints(params, 10, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 0, 11);
		pane.add(save, params);
		
		//New, Load, Add, Del, Shuffle, Repeat bar
		ToolBar toolbar = new ToolBar();
		setConstraints(params, 7, 1, 1, 4, 0, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 9, 0, 10);
		initToolBar(toolbar, listTitle);
		pane.add(toolbar, params);
		
		//Playlist display
		plist = new JList<String>(list);
		setConstraints(params, 7, 2, 3, 4, 0, 0, GridBagConstraints.LAST_LINE_END, GridBagConstraints.BOTH);
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
		            	setTimeDisplay();
						playButton.setIcon(pause);
						loopButton.setSelected(false);
						playlist.shuffle();
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
		
		
		shuffleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange()==ItemEvent.SELECTED){
					playlist.setShuffle(true);
					playlist.shuffle();
				} else if (ev.getStateChange()==ItemEvent.DESELECTED){
					playlist.setShuffle(false);
				}
			}
		});
		
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
				playlist.shuffle();
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
				int item = plist.getSelectedIndex();
				list.remove(item);
				playlist.deleteItem(item); 
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
	

	private void skip(String next) {
		if (next != "-1") {
			media.playMedia(next);
        	setTimeDisplay();
			plist.setSelectedIndex(playlist.getIndex(next));
			loopButton.setSelected(false);
			playButton.setIcon(pause);
		}
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
	
	private String openFile() {
		JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("mp4 files", "mp4");
        fileopen.addChoosableFileFilter(filter);
        filter = new FileNameExtensionFilter("mp3 files", "mp3");
        fileopen.addChoosableFileFilter(filter);
        filter = new FileNameExtensionFilter("avi files", "avi");
        fileopen.addChoosableFileFilter(filter);
        filter = new FileNameExtensionFilter("m4a files", "m4a");
        fileopen.addChoosableFileFilter(filter);
        fileopen.setCurrentDirectory(lastDirectory);

        int ret = fileopen.showDialog(new JPanel(), "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            String file = fileopen.getSelectedFile().toString();
            lastDirectory = fileopen.getSelectedFile();
            if (isValidFileType(file)) {
            	loopButton.setSelected(false);
            	return file;
            } else {
            	JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        		playlist.clearPlaylist();
        		list.clear();
            	playlist.loadPlaylist(file);
            	playlist.shuffle();
                listTitle.setText(fileopen.getSelectedFile().getName().substring(0, fileopen.getSelectedFile().getName().length()-4));
			} else if (file != "-1") {
				JOptionPane.showMessageDialog(pane, "Unrecognized file type", "Error", JOptionPane.ERROR_MESSAGE);
			}
        }
	}
	
	
	private void refreshPlaylist() {
		list.clear();
		for (int i = 0; i < playlist.getPlaylist2().size(); i++) {
			list.addElement(playlist.getPlaylist2().get(i).getTitle() + " (" + playlist.getPlaylist2().get(i).getLength() + ")");
		}
		playlist.shuffle();
	}
	
	
	private void setTimeDisplay() {
		currentTime.setTime((long) 0.0);
	}
	
	
	private void updateTimestamp() {
		if (media.getFileName() != null) {
			currentTime.setTime(media.getCurrentTime());
			totalTime.setTime(media.getLength() + 1000);
			NumberFormat format = NumberFormat.getInstance();
			format.setMinimumIntegerDigits(2);
			if (media.getLength() > 3600000) {
				timeDisplay.setText((currentTime.getHours()-16) + ":" + format.format(currentTime.getMinutes()) + ":" + format.format(currentTime.getSeconds()) + 
						"/" + (totalTime.getHours()-16) + ":" + format.format(totalTime.getMinutes()) + ":" + format.format(totalTime.getSeconds()));
			} else {
				timeDisplay.setText(currentTime.getMinutes() + ":" + format.format(currentTime.getSeconds()) + 
						"/" + totalTime.getMinutes() + ":" + format.format(totalTime.getSeconds()));
			}
			
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
		return file.endsWith("mp4") || file.endsWith("mp3") || file.endsWith("avi") || file.endsWith("m4a");
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

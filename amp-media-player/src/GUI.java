
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
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.plaf.metal.MetalSliderUI;

public class GUI extends JFrame {
	
	private Playlist playlist;
	private Media media;
	private JSlider slider;
	
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

		drawMenu(pane);
		drawControls(pane);
		drawPlaylist(pane);
		
		pack();
		
		setTitle("AMP Media Player");
		setMinimumSize(new Dimension(650, 420));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void drawMenu(Container pane) {
		JMenuBar menu = new JMenuBar();
		//ImageIcon icon = new ImageIcon("XXXX.png");
		
		JMenu file = new JMenu("File");
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setToolTipText("Exits the application");
		
		JMenu edit = new JMenu("Edit");
		
		JMenuItem nothing = new JMenuItem("Nothing yet");
		nothing.setToolTipText("This won't do anything");
		
		JMenu view = new JMenu("View");
		
		JMenuItem nothing2 = new JMenuItem("Also nothing");
		nothing2.setToolTipText("This also won't do anything");
		
		JMenu filter = new JMenu("Filter");
		
		JMenuItem nothing3 = new JMenuItem("Nope");
		nothing3.setToolTipText("This definitely won't do anything");
		
		file.add(exit);
		edit.add(nothing);
		view.add(nothing2);
		filter.add(nothing3);
		
		menu.add(file);
		menu.add(edit);
		menu.add(view);
		menu.add(filter);
		
		setJMenuBar(menu);

		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
	}

	
	private void drawControls(Container pane) {
		GridBagConstraints params = new GridBagConstraints();
		
		/*JPanel display = new JPanel();
		display.setBackground(Color.black);
		setConstraints(params, 0, 0, 3, 5, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 2, 10);
		pane.add(display, params);*/
		
		//JFrame display = new JFrame();
		//display.setBackground(Color.black);
		//setConstraints(params, 0, 0, 3, 5, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		//params.insets = new Insets(10, 10, 2, 10);
		pane.add(media.mediaPlayer());
		//pane.add(display, params);
		
		slider = new JSlider(0, 1000, 0);
		setConstraints(params, 0, 3, 1, 5, 1, 0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(2, 10, 10, 10);
		initSlider(slider);
		pane.add(slider, params);
		
		JLabel blank1 = new JLabel("");
		setConstraints(params, 0, 4, 1, 1, 1, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		pane.add(blank1, params);
		
		JButton prevButton = new JButton("Prev");
		setConstraints(params, 1, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		params.insets = new Insets(5, 5, 5, 5);
		pane.add(prevButton, params);
		
		final JButton playButton = new JButton("Play");
		setConstraints(params, 2, 4, 1, 1, 0, 0, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH);
		pane.add(playButton, params);
		
		JButton skipButton = new JButton("Next");
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
				if (playButton.getText() == "Play") {
					playButton.setText("Pause");
					System.out.println("Play media");
				} else {
					playButton.setText("Play");
					System.out.println("Pause media");
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
		media.playMedia("C:\\Users\\Chalenged\\Downloads\\Smash Bros. WiiU Music Preview.mp4");
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
		
		JButton save = new JButton("Save");
		setConstraints(params, 7, 0, 1, 1, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(10, 10, 0, 11);
		pane.add(save, params);
		
		ToolBar toolbar = new ToolBar();
		setConstraints(params, 5, 1, 1, 3, 0, 0, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH);
		params.insets = new Insets(0, 9, 0, 10);
		JToggleButton shuffleButton = new JToggleButton("S");
		toolbar.add(shuffleButton);
		JToggleButton repeatButton = new JToggleButton("R");
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
		media.setSlider(slider);
		slider.setUI(new MetalSliderUI() {
			  protected TrackListener createTrackListener(JSlider slider) {
				    return new TrackListener() {
				      @Override public void mousePressed(MouseEvent e) {
				        JSlider slider = (JSlider)e.getSource();
				        switch (slider.getOrientation()) {
				          case JSlider.VERTICAL:
				            slider.setValue(valueForYPosition(e.getY()));
				            break;
				          case JSlider.HORIZONTAL:
				            slider.setValue(valueForXPosition(e.getX()));
				            break;
				        }
				        super.mousePressed(e); //isDragging = true;
				        super.mouseDragged(e);
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
 
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI gui = new GUI();
                gui.setVisible(true);
            }
        });
    }
}
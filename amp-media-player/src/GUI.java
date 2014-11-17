
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.BorderLayout;

import javax.swing.*;

public class GUI extends JFrame {
	
	/*
	 * WARNING: MESSY-ASS CODE AHEAD
	 * 
	 * Mostly proof-of-concept. Will clean up later as I get familiar with Swing.'
	 * 
	 */
	
	public GUI() {
        init();
    }
	
	
	private void init() {
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());

		drawMenu(pane);
		drawDisplay(pane);
		drawControls(pane);
		
		pack();
		
		setTitle("AMP Media Player");
		setMinimumSize(new Dimension(460, 420));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void drawDisplay(Container pane) {
	}
	
	private void drawMenu(Container pane) {
		JMenuBar menu = new JMenuBar();
		//ImageIcon icon = new ImageIcon("XXXX.png");
		
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem exit = new JMenuItem("Exit");//, icon);
		exit.setToolTipText("Exits the application");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		JMenu edit = new JMenu("Edit");
		file.setMnemonic(KeyEvent.VK_E);
		
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
	}

	
	/*
	 * I plan to clean this up later, just trying to hash things out
	 */
	private void drawControls(Container pane) {
		GridBagConstraints params = new GridBagConstraints();
		
		JPanel display = new JPanel();
		display.setBackground(Color.black);
		params.gridx = 0;
		params.gridy = 0;
		params.gridheight = 1;
		params.gridwidth = 3;
		params.insets = new Insets(10, 10, 10, 10);
		params.anchor = GridBagConstraints.PAGE_START;
		params.fill = GridBagConstraints.BOTH;
		params.weightx = 1;
		params.weighty = 1;
		pane.add(display, params);
		
		JSlider slider = new JSlider(0, 150, 0);
		params.gridx = 0;
		params.gridy = 1;
		params.weighty = 0;
		params.gridwidth = 3;
		params.fill = GridBagConstraints.BOTH;
		pane.add(slider, params);
		
		JButton prevButton = new JButton("Prev");
		params.gridx = 0;
		params.gridy = 2;
		params.insets = new Insets(5, 5, 5, 5);
		params.gridheight = 1;
		params.gridwidth = 1;
		params.fill = GridBagConstraints.HORIZONTAL;
		params.weightx = 0.5;
		params.weighty = 0;
		params.anchor = GridBagConstraints.PAGE_END;
		params.fill = GridBagConstraints.BOTH;
		params.ipady = 0;
		pane.add(prevButton, params);
		
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Go to previous media");
			}
		});
		
		JButton playButton = new JButton("Play");
		params.gridx = 1;
		params.gridy = 2;
		params.weightx = 0.5;
		pane.add(playButton, params);
		
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Play media");
			}
		});
		
		JButton skipButton = new JButton("Next");
		params.gridx = 2;
		params.gridy = 2;
		params.weightx = 0.5;
		pane.add(skipButton, params);
		
		skipButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Go to next media");
			}
		});
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
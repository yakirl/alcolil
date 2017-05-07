package org.gitprof.alcolil.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/*
 *  --------------------------------------
 *  |			main menu				 |
 *  |____________________________________|
 *  |         |                          |
 *  |         | 						 |
 *  | subMenu |     Graphs / Tables      |
 *  |         |					         |
 *  --------------------------------------
 * 
 * 
 * 
 */
public class GUI implements Runnable {

	GController gctrlr;
	JFrame window;
	JPanel container;
	JPanel upperPanel, bottomPanel;
	JPanel mainMenu;
	JPanel subMenu;
	JPanel dataSection;
	
	Timer refreshTimer;
	private final static int REFRESH_TIME = 4000;
	
	public GUI(GController controller) {
		initialize();
	}
	
	
	@Override
	public void run() {	
		showGUI();
	}
	
	public void showGUI() {
		window.setVisible(true);
		refreshTimer.start();
	}
	
	private void initialize() {
		setWindow("Window name");
		refreshTimer = new Timer(REFRESH_TIME, new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					refreshGUI();
				}
		});
	}
	
	private void refreshGUI() {
		updateBarChart();
	}
	
	private void setWindow(String frameName) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		window = new JFrame(frameName);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setContainer(window);      
        //CandlestickChart candlestickChart = new CandlestickChart();
        //frame.setContentPane(candlestickChart);   
        setTabs(window);
        
        window.setResizable(false);
        window.pack();
        
	}
	
	private void setContainer(JFrame window) {
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		setSubPanels(container);
		window.getContentPane().add(container);	
	}
	
	private void setSubPanels(JPanel panel) {
		upperPanel = new JPanel();
		setMainMenu(upperPanel);
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		setBottomPanel(bottomPanel);
		panel.add(upperPanel);
		panel.add(bottomPanel);
	}
	
	private void setMainMenu(JPanel menuPanel) {
		mainMenu = new JPanel();
		// add buttons
		JButton backtestButton = new JButton("Backtest");
		JButton updateDBButton = new JButton("UpdateDB");
		backtestButton.setActionCommand("Backtest");
		updateDBButton.setActionCommand("UpdateDB");
		backtestButton.addActionListener(new ButtonListener());
		updateDBButton.addActionListener(new ButtonListener());
		mainMenu.add(backtestButton);
		menuPanel.add(mainMenu);
	}
	
	private class ButtonListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			
		}
		
	}
	
	private void setBottomPanel(JPanel panel) {
		subMenu = new JPanel();
		dataSection = new JPanel();
		setSubMenu(subMenu);
		setDataSection(dataSection);
		panel.add(subMenu);
		panel.add(dataSection);
	}
	
	private void setSubMenu(JPanel panel) {
		
	}
	
	private void setDataSection(JPanel panel) {
		
	}
	
	private void setTabs(Container pane) {     
        JPanel comboBoxPane = new JPanel(); //use FlowLayout
        String comboBoxItems[] = { "buttons", "text" };
        JComboBox<String> cb = new JComboBox<String>(comboBoxItems);
        cb.setEditable(false); 
        comboBoxPane.add(cb);
        JPanel card1 = new JPanel();
        card1.add(new JButton("Button 1"));
        JPanel card2 = new JPanel();
        card2.add(new JTextField("TextField", 20));
        JPanel cards = new JPanel(new CardLayout());
        cards.add(card1, "buttons");
        cards.add(card2, "text");     
        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
	}
	
	private void showCandlestickChart() {
		
	}

	private void updateBarChart() {
		
	}
}

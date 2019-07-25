package org.yakirl.alcolil.ui;

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

	GUIHandlers handler;
	JFrame window;
	JPanel container;
	Timer refreshTimer;
	private final static int REFRESH_TIME = 4000;
	
	public interface CandleUpdater {
		public void update(long time, double o, double h, double l, double c, long v);
	}
	
	public GUI(GUIHandlers handler) {
		this.handler = handler;
		initialize();
	}
	
	private void initialize() {
		setWindow("Trading System");
		refreshTimer = new Timer(REFRESH_TIME, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				refreshGUI();
			}
		});
	}
	
	@Override
	public void run() {	
		showGUI();
	}
	
	public void showGUI() {
		window.setVisible(true);
		refreshTimer.start();
	}
	
	private void refreshGUI() {
		
	}
	
	private void setWindow(String frameName) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		window = new JFrame(frameName);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setContainer(window);      
        //CandlestickChart candlestickChart = new CandlestickChart();
        //frame.setContentPane(candlestickChart);   
          
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);      
	}
	
	private void setContainer(JFrame window) {
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		setSubPanels(container);
		window.getContentPane().add(container);	
	}
	
	private void setSubPanels(JPanel panel) {
		JPanel bottomPanel = new JPanel();
		setBottomPanel(bottomPanel);
		JPanel upperPanel = new JPanel();
		setMainMenu(upperPanel, bottomPanel);
		panel.add(upperPanel, BorderLayout.NORTH);
		panel.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void setMainMenu(JPanel menuPanel, JPanel bottomPanel) {
		JButton backtestButton = new JButton("Backtest");
		JButton updateDBButton = new JButton("UpdateDB");
		backtestButton.setActionCommand("Backtest");
		updateDBButton.setActionCommand("UpdateDB");
		
		backtestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cardLayout = (CardLayout) bottomPanel.getLayout();
				cardLayout.show(bottomPanel, "backtest");
			}
		});
		updateDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cardLayout = (CardLayout) bottomPanel.getLayout();
				cardLayout.show(bottomPanel, "update_db");
			}
		});				
		menuPanel.add(backtestButton);
		menuPanel.add(updateDBButton);
	}		
	
	private void setBottomPanel(JPanel panel) {
		// panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setLayout(new CardLayout());
		JPanel backtestPanel = new JPanel();
		setBacktestPanel(backtestPanel);
		panel.add(backtestPanel, "backtest");
		JPanel updateDBPanel = new JPanel();
		setUpdateDBPanel(updateDBPanel);
		panel.add(updateDBPanel, "update_db");
	}
	
	private void setBacktestPanel(JPanel panel) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		// DataSection - CardLayout, toggle between Chart and Input
		JPanel dataSection = new JPanel();
		dataSection.setLayout(new CardLayout());
		CandlestickChart chart = new CandlestickChart("CandleChart");
		JPanel input = new JPanel();
		JTextField symbols = new JTextField("symbols");
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chart.reset("New CandleChart");
				handler.backtest(true, symbols.getText(), (timeMillis, o, h, l, c, v) -> chart.addCandle(timeMillis, o, h, l, c, v));
			}
		});
		input.add(symbols);
		input.add(start);
		dataSection.add(chart, "chart");
		dataSection.add(input, "input");
		
		// SubMenu - set actions for buttons, to toggle between chart and input
		JPanel subMenu = new JPanel();
		subMenu.setLayout(new BoxLayout(subMenu, BoxLayout.Y_AXIS));
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cardLayout = (CardLayout) dataSection.getLayout();
				cardLayout.show(dataSection, "input");
			}
		});
		JButton watchButton = new JButton("Watch");
		watchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cardLayout = (CardLayout) dataSection.getLayout();
				cardLayout.show(dataSection, "chart");
			}
		});
		subMenu.add(runButton);
		subMenu.add(watchButton);
		
		// Add all to backtest panel
		panel.add(subMenu);
		panel.add(dataSection);
	}
	
	private void setUpdateDBPanel(JPanel panel) {
	}
}
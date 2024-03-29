package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import constants.SchedulingAlgorithm;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import queues.FCFSQueue;
import queues.NonPQueue;
import queues.PQueue;
import queues.Queue;
import queues.RoundRobin;
import queues.SJFQueue;
import queues.SRTFQueue;
import scheduler.Main;

public class GanttChart extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int ONE_LEVEL = 1, TWO_LEVEL = 2, THREE_LEVEL = 3, FOUR_LEVEL = 4;
	private static int level = 1;
	
	private static JPanel panel1, panel2, panel3, panel4;	
	private static JPanel timePanel1, timePanel2, timePanel3, timePanel4;
	private static JPanel pcbPanel;
	private static JPanel pcbIdPanel;	
	private static JPanel pcbArrivalPanel;
	private static JPanel pcbBurstPanel;
	private static JPanel pcbPriorityPanel;	
	
	private static JPanel turnaroundTimePanel;
	private static JPanel timesPanel;
	private static JPanel waitTimePanel;
	private static JPanel responseTimePanel;
	private static JPanel timesIdPanel;
	
	private static JPanel avgTimeTable;
	private static JPanel avgResponseTime;
	private static JPanel avgWaitTime;
	private static JPanel avgTurnaroundTime;
	private static JPanel avgTimeLblPanel;	
	private static JPanel mlfqPanel; 
	private static JLabel[] timeLabel = new JLabel[100000];
	
	private static JMenuBar menuBar;
	private static JMenu dataSets, levelOptions;

	private static JButton startButton;	
	private static JSpinner setQuantum1, setQuantum2, setQuantum3, setQuantum4;
	
	private ArrayList<Integer> PID = new ArrayList<Integer>();
	private ArrayList<Integer> arrivalTime = new ArrayList<Integer>();
	private ArrayList<Integer> burstTime = new ArrayList<Integer>();
	private ArrayList<Integer> priority = new ArrayList<Integer>();
	private ArrayList<Integer> iOBoundFlag = new ArrayList<Integer>();
	
	private static int algorithm1, algorithm2, algorithm3, algorithm4;
	private static int procYOffset;
	private static int timesYOffset;
	private static int processCount = 0;		
	
	private static int pcbPanelHeight = 350;
	private static int timesPanelHeight = 350;
	private static int panelWidth = 1150;
	
	private static int yOffset = -1;			
	private static int xOffset = -1;
	private static int prevBurstLength = -1;
	private static int timeCounter = 0;
	private static int timesEntry;
	private static int quantum = 2;
	private static int Offset = -2;
	private static int prevEndTime = 0;
	private static boolean initFlag = true;
	private static int quantum1 = 2, 
			quantum2 = 2, 
			quantum3 = 2, 
			quantum4 = 2;
	
	private static String fileChosen;
	private static String[] algorithms = {"FCFS", "SJF", "SRTF", "NP-PRIO", "P-PRIO", "RR"};
	
	private static Main scheduler = new Main();;
	private static Font font = new Font("Helvetica", Font.BOLD, 20);
	private static Font timeLabelFont = new Font("Helvetica", Font.BOLD, 12);
	private static Color darkBlue = new Color(0, 46, 70);
	private static Border border = BorderFactory.createLineBorder(darkBlue);
	private static Container con;
	
	private static ArrayList<CPUBoundProcess> processes = new ArrayList<CPUBoundProcess>();
	
	JComboBox<String> algoList1 = new JComboBox<String>(algorithms);
	JComboBox<String> algoList2 = new JComboBox<String>(algorithms);
	JComboBox<String> algoList3 = new JComboBox<String>(algorithms);
	JComboBox<String> algoList4 = new JComboBox<String>(algorithms);
	
	private JScrollPane timesScrollPane;
	private JScrollPane pcbScrollPane;	
	private JScrollPane scrollPane4;
	private JScrollPane scrollPane3;
	private JScrollPane scrollPane2;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane;
	
	private JLabel arrivingProcesses;
	private JLabel qLabel1;
	private JLabel qLabel2;
	private JLabel qLabel3;
	private JLabel qLabel4;
	private JLabel fileLabl;
	private JLabel execTime;
	private JPanel avgTimeTable2;
	private JPanel avgTimeLblPanel2;
	private static JPanel avgResponseTime2;
	private static JPanel avgWaitTime2;
	private static JPanel avgTurnaroundTime2;	
	
	public GanttChart(){
		super("CPU Scheduling Gantt Chart");		
		setExtendedState(MAXIMIZED_BOTH);
		con = getContentPane();
		con.setBackground(Color.WHITE);
		con.setLayout(null);		
	}
		
	public void init(){
		
		JMenuItem importFile;				
		JMenuItem randomize;
		
		menuBar = new JMenuBar();		
		
		dataSets = new JMenu("Data set");
		dataSets.setEnabled(true);
		
		levelOptions = new JMenu("Levels");
		
		mlfqPanel = new JPanel();
		mlfqPanel.setPreferredSize(new Dimension(con.getWidth(), con.getHeight() + Offset));
		mlfqPanel.setBackground(Color.WHITE);
		mlfqPanel.setLayout(null);
			
		scrollPane = new JScrollPane(mlfqPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(0, 0, con.getWidth(), con.getHeight());
			
		add(scrollPane);
		
		initStartButton(1100, 25);
		GanttChart g = this;
		randomize = new JMenuItem("Randomize");
		randomize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {				
				DataGenerator dg = new DataGenerator(g);
			}
		});
		
		importFile = new JMenuItem("Import file");
		importFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				PID.removeAll(PID);
				arrivalTime.removeAll(arrivalTime);
				burstTime.removeAll(burstTime);
				
				JFileChooser fileChooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Text files", "txt");
			    fileChooser.setFileFilter(filter);
			    int returnVal = fileChooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {				       
			        fileChosen = fileChooser.getSelectedFile().getAbsolutePath();
			        
			        fileLabl.setText("File: " + fileChooser.getSelectedFile().getName());
			        resetGanttChart();
					resetTimesInformation();
					resetArrivedTable();
					resetTimeAverages();			        
			    }

			    if(fileChosen != null) {
			    	readFile(fileChosen);
			    }
			    			    
			    Queue.threadStopped = false;
				Queue.processList.removeAll(Queue.processList);
				Queue.clockTime = 0;							
				
				con.repaint();
				con.revalidate();
			}
		});
		
		dataSets.add(importFile);
		dataSets.add(randomize);
				
		JMenuItem mlfqOneLevel, mlfqTwoLevel, mlfqThreeLevel, mlfqFourLevel;
		mlfqOneLevel = new JMenuItem("1-Level");
		mlfqOneLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Offset = -2;			
				initFlag = false;
				resetNewLevel();
				reset();
				initOneLevel();				
			}
		});
		
		mlfqTwoLevel = new JMenuItem("2-Level");
		mlfqTwoLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Offset = 150;								
				initFlag = false;
				resetNewLevel();
				reset();	
				initTwoLevel();
			}
		});
		
		mlfqThreeLevel = new JMenuItem("3-Level");
		mlfqThreeLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {				
				Offset = 270;
				initFlag = false;
				resetNewLevel();
				reset();				
				initThreeLevel();				
			}
		});
		
		mlfqFourLevel = new JMenuItem("4-Level");
		mlfqFourLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Offset = 450;
				initFlag = false;
				resetNewLevel();
				reset();
				initFourLevel();				
			}
		});
		
		if(initFlag) {
			initOneLevel();
			initFlag = false;
		}
		
		levelOptions.add(mlfqOneLevel);
		levelOptions.add(mlfqTwoLevel);
		levelOptions.add(mlfqThreeLevel);
		levelOptions.add(mlfqFourLevel);
		
		new JMenuItem("Set");		
		
		if(mlfqOneLevel.isSelected()) {
			initOneLevel();
		}
		
		menuBar.add(dataSets);
		menuBar.add(levelOptions);
		
		setJMenuBar(menuBar);
			
		execTime = new JLabel("Expected execution time: --");
		execTime.setBounds(70, 5, 500, 50);
		
		fileLabl = new JLabel("File: " + fileChosen);
		fileLabl.setBounds(70, 20, 500, 50);
		
		mlfqPanel.add(fileLabl);
		mlfqPanel.add(execTime);
		
		con.repaint();
		con.revalidate();
		
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
	}
	
	protected void resetNewLevel() {		
		if(scrollPane1 != null) mlfqPanel.remove(scrollPane1);
		if(scrollPane2 != null) mlfqPanel.remove(scrollPane2);
		if(scrollPane3 != null) mlfqPanel.remove(scrollPane3);
		if(scrollPane4 != null) mlfqPanel.remove(scrollPane4);
		
		if(setQuantum1 != null) mlfqPanel.remove(setQuantum1);
		if(setQuantum2 != null) mlfqPanel.remove(setQuantum2);
		if(setQuantum3 != null) mlfqPanel.remove(setQuantum3);
		if(setQuantum4 != null) mlfqPanel.remove(setQuantum4);
		
		if(qLabel1 != null) mlfqPanel.remove(qLabel1);
		if(qLabel2 != null) mlfqPanel.remove(qLabel2);
		if(qLabel3 != null) mlfqPanel.remove(qLabel3);
		if(qLabel4 != null) mlfqPanel.remove(qLabel4);
		
		mlfqPanel.remove(algoList1);
		mlfqPanel.remove(algoList2);
		mlfqPanel.remove(algoList3);
		mlfqPanel.remove(algoList4);
		
		mlfqPanel.remove(arrivingProcesses);
		mlfqPanel.remove(pcbScrollPane);
		mlfqPanel.remove(timesScrollPane);	
		mlfqPanel.remove(avgTimeTable);
		mlfqPanel.remove(avgTimeTable2);
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
	}

	public void readData(ArrayList<int[]> data, int execTime) {
		PID.removeAll(PID);
		arrivalTime.removeAll(arrivalTime);
		burstTime.removeAll(burstTime);
		priority.removeAll(priority);
		iOBoundFlag.removeAll(iOBoundFlag);
		for(int i = 0; i < data.size(); i++) {
			int[] arr = data.get(i);
			for(int j = 0; j < arr.length; j++) {
				if(i == 0) {
					PID.add(arr[j]);
				}else if(i == 1) {
					arrivalTime.add(arr[j]);
				}else if(i == 2) {
					burstTime.add(arr[j]);
				}else if(i == 3) {
					priority.add(arr[j]);
				}else if(i == 4) {
					iOBoundFlag.add(arr[j]);
				}
			}			
		}
		
		this.execTime.setText("Expected execution time: " + execTime);
		startButton.setEnabled(true);
		startButton.setToolTipText("Start simulation!");
		
		resetGanttChart();
		resetTimesInformation();
		resetArrivedTable();
		resetTimeAverages();			        
    
	    Queue.threadStopped = false;
		Queue.processList.removeAll(Queue.processList);
		Queue.clockTime = 0;							
		
		con.repaint();
		con.revalidate();
	}
	
	protected void readFile(String fileChosen) {
		PID.removeAll(PID);
		arrivalTime.removeAll(arrivalTime);
		burstTime.removeAll(burstTime);
		priority.removeAll(priority);
		iOBoundFlag.removeAll(iOBoundFlag);
		
		int totalExecTime = 0;
		
		try(BufferedReader in = new BufferedReader(new FileReader(fileChosen));){	        	
	        	String line;
				while((line = in.readLine()) != null){
				    System.out.println(line);
				    String[] token = line.split(" ");
				    
				    PID.add(Integer.parseInt(token[0]));				    
				    arrivalTime.add(Integer.parseInt(token[1]));
				    totalExecTime += Integer.parseInt(token[2]);
				    burstTime.add(Integer.parseInt(token[2]));
				    priority.add(Integer.parseInt(token[3]));
				    iOBoundFlag.add(Integer.parseInt(token[4]));
				}
				in.close();								
				startButton.setEnabled(true);
				startButton.setToolTipText("Start simulation!");
				execTime.setText("Expected execution time: " + totalExecTime);
				
	        } catch (NumberFormatException nfe) { 
	        	JOptionPane.showMessageDialog(null, "An exception has occurred while parsing the data. " +
        				"\nPlease make sure to follow the correct format of the contents of the file.", "Error", 
	        				JOptionPane.ERROR_MESSAGE);
	        	startButton.setEnabled(false);
	        } catch (IOException e) {
	        	JOptionPane.showMessageDialog(null, "An error has occurred while reading the file.", "Error", 
        				JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
	}

	private void initGanttChart(int x, int y, int level /*JComboBox cb,*/) {
				
		if(level == 1) {
			panel1 = new JPanel();
			panel1.setLayout(null);
			panel1.setBackground(Color.LIGHT_GRAY);
			panel1.setBorder(border);
			panel1.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel1 = new JPanel();
			timePanel1.setLayout(null);
			timePanel1.setBackground(darkBlue);
			timePanel1.setBounds(1, 51, 1145, 20);																								
			panel1.add(timePanel1);
			
			scrollPane1 = new JScrollPane(panel1);
			scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.setBounds(x, y, panelWidth, 85);
			
			mlfqPanel.add(scrollPane1);
			
		}else if (level == 2) {
			
			panel2 = new JPanel();
			panel2.setLayout(null);
			panel2.setBackground(Color.LIGHT_GRAY);
			panel2.setBorder(border);
			panel2.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel2 = new JPanel();
			timePanel2.setLayout(null);
			timePanel2.setBackground(darkBlue);
			timePanel2.setBounds(1, 51, 1145, 20);																								
			panel2.add(timePanel2);
			
			scrollPane2 = new JScrollPane(panel2);
			scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane2.setBounds(x, y, panelWidth, 85);
			
			mlfqPanel.add(scrollPane2);
			
		}else if (level == 3) {
			
			panel3 = new JPanel();
			panel3.setLayout(null);
			panel3.setBackground(Color.LIGHT_GRAY);
			panel3.setBorder(border);
			panel3.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel3 = new JPanel();
			timePanel3.setLayout(null);
			timePanel3.setBackground(darkBlue);
			timePanel3.setBounds(1, 51, 1145, 20);
			panel3.add(timePanel3);
			
			scrollPane3 = new JScrollPane(panel3);
			scrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane3.setBounds(x, y, panelWidth, 85);
			
			mlfqPanel.add(scrollPane3);
			
		}else if (level == 4) {
			
			panel4 = new JPanel();
			panel4.setLayout(null);
			panel4.setBackground(Color.LIGHT_GRAY);
			panel4.setBorder(border);
			panel4.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel4 = new JPanel();
			timePanel4.setLayout(null);
			timePanel4.setBackground(darkBlue);
			timePanel4.setBounds(1, 51, 1145, 20);
			panel4.add(timePanel4);			
			
			scrollPane4 = new JScrollPane(panel4);
			scrollPane4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane4.setBounds(x, y, panelWidth, 85);
			
			mlfqPanel.add(scrollPane4);
		}
	}
	
	private void initOneLevel() {
		level = ONE_LEVEL;
		
		initOneLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initProcessTable(270, 200, 150, 250);
		initTimesTable(300);
		initAvgTimeTable(650, 560);
		
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
		con.repaint();
		con.revalidate();
	}

	private void initTwoLevel() {
		level = TWO_LEVEL;
		
		initOneLevelComboBox();
		initTwoLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initGanttChart(60, 225, 2);
		initProcessTable(270, 320, 150, 380);
		initTimesTable(400);
		initAvgTimeTable(650, 660);
		
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
		con.repaint();
		con.revalidate();
	}
	
	private void initThreeLevel() {
		level = THREE_LEVEL;
		
		initOneLevelComboBox();
		initTwoLevelComboBox();
		initThreeLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initGanttChart(60, 225, 2);
		initGanttChart(60, 360, 3);
		
		initProcessTable(270, 460, 150, 520);
		initTimesTable(550);
		initAvgTimeTable(650, 820);
	}
	
	private void initFourLevel() {
		level = FOUR_LEVEL;
		
		initOneLevelComboBox();
		initTwoLevelComboBox();
		initThreeLevelComboBox();
		initFourLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initGanttChart(60, 225, 2);
		initGanttChart(60, 360, 3);
		initGanttChart(60, 500, 4);
		
		initProcessTable(270, 620, 150, 680);
		initTimesTable(700);
		initAvgTimeTable(650, 970);
	}
	
	private void initOneLevelComboBox() {		
		addSetQuantum(160, 62, 1);
		
		mlfqPanel.remove(algoList1);
		algoList1.setSelectedIndex(0);
		algoList1.setBounds(70, 65, 80, 20);		
		algoList1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("rawtypes")
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();				
				Queue.threadStopped = false;
				Queue.processList.removeAll(Queue.processList);
				Queue.clockTime = 0;
				
				resetGanttChart();
				resetTimesInformation();
				resetArrivedTable();
				resetTimeAverages();
				
				con.repaint();
				con.revalidate();
				
				if (selected.equals("SJF")) {
					algorithm1 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm1 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm1 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm1 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm1 = SchedulingAlgorithm.RR;						
				}else {
					algorithm1 = SchedulingAlgorithm.FCFS;					
				}
								
				if(selected.equals("RR")){					
					enableQuantum(1);
				}else{
					setQuantum1.setEnabled(false);
				}
			}
			
		});				
		mlfqPanel.add(algoList1);
	}
	
	private void initTwoLevelComboBox() {
		addSetQuantum(160, 200, 2);
		algoList2.setSelectedIndex(0);
		algoList2.setBounds(70, 200, 80, 20);		
		algoList2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("rawtypes")
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();
				
				Queue.threadStopped = false;
				Queue.processList.removeAll(Queue.processList);
				Queue.clockTime = 0;
				
				resetGanttChart();
				resetTimesInformation();
				resetArrivedTable();
				resetTimeAverages();
				
				con.repaint();
				con.revalidate();
				
				if (selected.equals("SJF")) {
					algorithm2 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm2 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm2 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm2 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm2 = SchedulingAlgorithm.RR;
				}else {
					algorithm2 = SchedulingAlgorithm.FCFS;
				}
				
				if(selected.equals("RR")){					
					enableQuantum(2);
				}else{
					setQuantum2.setEnabled(false);
				}
			}
			
		});		
		mlfqPanel.add(algoList2);
	}
	
	private void initThreeLevelComboBox() {
		addSetQuantum(160, 335, 3);
		algoList3.setSelectedIndex(0);
		algoList3.setBounds(70, 335, 80, 20);		
		algoList3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("rawtypes")
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();
				
				Queue.threadStopped = false;
				Queue.processList.removeAll(Queue.processList);
				Queue.clockTime = 0;
				
				resetGanttChart();
				resetTimesInformation();
				resetArrivedTable();
				resetTimeAverages();
				
				con.repaint();
				con.revalidate();
				
				if (selected.equals("SJF")) {
					algorithm3 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm3 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm3 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm3 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm3 = SchedulingAlgorithm.RR;
				}else {
					algorithm3 = SchedulingAlgorithm.FCFS;
				}
				
				if(selected.equals("RR")){					
					enableQuantum(3);
				}else{
					setQuantum3.setEnabled(false);
				}
			}
			
		});		
		mlfqPanel.add(algoList3);
	}
	
	private void initFourLevelComboBox() {
		addSetQuantum(160, 475, 4);
		algoList4.setSelectedIndex(0);
		algoList4.setBounds(70, 475, 80, 20);		
		algoList4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("rawtypes")
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();
				
				Queue.threadStopped = false;
				Queue.processList.removeAll(Queue.processList);
				Queue.clockTime = 0;
				
				resetGanttChart();
				resetTimesInformation();
				resetArrivedTable();
				resetTimeAverages();
				
				con.repaint();
				con.revalidate();
				
				if (selected.equals("SJF")) {
					algorithm4 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm4 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm4 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm4 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm4 = SchedulingAlgorithm.RR;
				}else {
					algorithm4 = SchedulingAlgorithm.FCFS;
				}
				
				if(selected.equals("RR")){					
					enableQuantum(4);
				}else{
					setQuantum4.setEnabled(false);
				}
			}
			
		});
		
		mlfqPanel.add(algoList4);
	}

	protected void addSetQuantum(int x, int y, int level) {
		JSpinner spinner = null;
		SpinnerModel model = new SpinnerNumberModel(2, 2, 100, 1);				
		
		if(level == 1) {			
			
			qLabel1 = new JLabel("Quantum: ");
			qLabel1.setBounds(x, y, 100, 22);
			mlfqPanel.add(qLabel1);
			
			setQuantum1 = new JSpinner(model);			
			setQuantum1.setBounds(x + 60, y, 50, 22);
			setQuantum1.setEnabled(false);
			((DefaultEditor)setQuantum1.getEditor()).getTextField().setEditable(false);
			setQuantum1.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {					
					quantum1 = (int) ((JSpinner)e.getSource()).getValue();
					System.out.println("**************************** quantum1 = " + quantum1);
				}
			});

			spinner = setQuantum1;		
		}
		
		if(level == 2) {
			
			qLabel2 = new JLabel("Quantum: ");
			qLabel2.setBounds(x, y, 100, 22);
			mlfqPanel.add(qLabel2);
			
			setQuantum2 = new JSpinner(model);			
			setQuantum2.setBounds(x + 60, y, 50, 22);
			setQuantum2.setEnabled(false);
			((DefaultEditor)setQuantum2.getEditor()).getTextField().setEditable(false);
			setQuantum2.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					quantum2 = (int) ((JSpinner)e.getSource()).getValue();
					System.out.println("**************************** quantum1 = " + quantum1);
				}
			});
			
			spinner = setQuantum2;	
		}		
		
		if(level == 3) {
			
			qLabel3 = new JLabel("Quantum: ");
			qLabel3.setBounds(x, y, 100, 22);
			mlfqPanel.add(qLabel3);
			
			setQuantum3 = new JSpinner(model);			
			setQuantum3.setBounds(x + 60, y, 50, 22);
			setQuantum3.setEnabled(false);
			((DefaultEditor)setQuantum3.getEditor()).getTextField().setEditable(false);
			setQuantum3.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					quantum3 = (int) ((JSpinner)e.getSource()).getValue();
				}
			});
			
			spinner = setQuantum3;
		}
		
		if(level == 4) {
			
			qLabel4 = new JLabel("Quantum: ");
			qLabel4.setBounds(x, y, 100, 22);
			mlfqPanel.add(qLabel4);
			
			setQuantum4 = new JSpinner(model);			
			setQuantum4.setBounds(x + 60, y, 50, 22);
			setQuantum4.setEnabled(false);
			((DefaultEditor)setQuantum4.getEditor()).getTextField().setEditable(false);
			setQuantum4.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					quantum4 = (int) ((JSpinner)e.getSource()).getValue();
				}
			});
			
			spinner = setQuantum4;
		}
		
		mlfqPanel.add(spinner);
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
	}

	private void initStartButton(int xOffset, int yOffset) {
		startButton = new JButton("START");
		startButton.setBounds(xOffset, yOffset, 100, 50);
		
		if(processes.size() == 0){
			startButton.setEnabled(false);
			startButton.setToolTipText("Import a data set first!");
		}else{
			startButton.setEnabled(true);
			startButton.setToolTipText("Start simulation");
		}
		
		startButton.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
									
					Queue.threadStopped = false;
					Queue.processList.removeAll(Queue.processList);
					Queue.clockTime = 0;
					Queue.prevTimeQuantum = 0;
					
					resetGanttChart();
					resetTimesInformation();
					resetArrivedTable();
					resetTimeAverages();
					
					int queues_num = level;
					processes.removeAll(processes);
					
					int size = PID.size();
					for(int i = 0; i < size; i++){
						if(iOBoundFlag.get(i) == 1) {
							processes.add(new IOBoundProcess(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i)));
						} else {
							processes.add(new CPUBoundProcess(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i)));
						}
					}
					
					scheduler.initProcesses(queues_num, processes);
					
					int[] algorithms = {algorithm1, algorithm2, algorithm3, algorithm4};
					int[] quanta = {((setQuantum1 == null)? 0 : (int) setQuantum1.getValue()), 
									((setQuantum2 == null)? 0 : (int) setQuantum2.getValue()),
									((setQuantum3 == null)? 0 : (int) setQuantum3.getValue()), 
									((setQuantum4 == null)? 0 : (int) setQuantum4.getValue())};
					scheduler.generateQueues(algorithms, quanta);							
			}			
		});
		mlfqPanel.add(startButton);
	}

	private void initProcessTable(int lbl_x, int lbl_y, int scrl_x, int scrl_y) {
		arrivingProcesses = new JLabel("PROCESSES");
		arrivingProcesses.setFont(font);
		arrivingProcesses.setBounds(lbl_x, lbl_y, 500, 50);
		mlfqPanel.add(arrivingProcesses);
		
		pcbPanel = new JPanel(null);
		pcbPanel.setBorder(border);		
		pcbPanel.setPreferredSize(new Dimension(400, 350));		
		
		pcbScrollPane = new JScrollPane(pcbPanel);
		pcbScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pcbScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pcbScrollPane.setBounds(scrl_x, scrl_y, 405, 355);
		mlfqPanel.add(pcbScrollPane);
		
		pcbIdPanel = new JPanel(null);
		pcbIdPanel.setBorder(border);		
		pcbIdPanel.setBounds(1, 1, 50, 350);
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(0, 0, 50, 25);
		idLabelPanel.setBorder(border);
		idLabelPanel.add(new JLabel("PID"));
		pcbIdPanel.add(idLabelPanel);		
		pcbPanel.add(pcbIdPanel);		
		
		pcbArrivalPanel = new JPanel(null);
		pcbArrivalPanel.setBorder(border);		
		pcbArrivalPanel.setBounds(51, 1, 120, 350);		
		
		JPanel arrivalLabelPanel = new JPanel();
		arrivalLabelPanel.setBounds(0, 0, 120, 25);
		arrivalLabelPanel.setBorder(border);
		arrivalLabelPanel.add(new JLabel("ARRIVAL TIME"));
		pcbArrivalPanel.add(arrivalLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbArrivalPanel);
		
		pcbBurstPanel = new JPanel(null);
		pcbBurstPanel.setBorder(border);		
		pcbBurstPanel.setBounds(171, 1, 120, 350);
		pcbPanel.add(pcbBurstPanel);
		
		JPanel burstLabelPanel = new JPanel();
		burstLabelPanel.setBounds(0, 0, 120, 25);
		burstLabelPanel.setBorder(border);
		burstLabelPanel.add(new JLabel("BURST TIME"));
		pcbBurstPanel.add(burstLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbBurstPanel);
		
		pcbPriorityPanel = new JPanel(null);
		pcbPriorityPanel.setBorder(border);		
		pcbPriorityPanel.setBounds(291, 1, 110, 350);
		pcbPanel.add(pcbPriorityPanel);		
		
		JPanel priorityLabelPanel = new JPanel();
		priorityLabelPanel.setBounds(0, 0, 110, 25);
		priorityLabelPanel.setBorder(border);
		priorityLabelPanel.add(new JLabel("PRIORITY"));
		pcbPriorityPanel.add(priorityLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbPriorityPanel);
	}
	
	private void initTimesTable(int offset) {
		timesPanel = new JPanel(null);
		timesPanel.setBorder(border);		
		timesPanel.setPreferredSize(new Dimension(575, 250));		
		
		timesScrollPane = new JScrollPane(timesPanel);
		timesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		timesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		timesScrollPane.setBounds(650, offset, 435, 255);
		mlfqPanel.add(timesScrollPane);
		
		timesIdPanel = new JPanel(null);
		timesIdPanel.setBorder(border);		
		timesIdPanel.setBounds(1, 1, 50, 250);
		
		JPanel timeIdLabelPanel = new JPanel();
		timeIdLabelPanel.setBounds(0, 0, 50, 25);
		timeIdLabelPanel.setBorder(border);
		timeIdLabelPanel.add(new JLabel("PID"));
		timesIdPanel.add(timeIdLabelPanel);		
		timesPanel.add(timesIdPanel);			
		
		responseTimePanel = new JPanel(null);
		responseTimePanel.setBorder(border);		
		responseTimePanel.setBounds(51, 1, 120, 250);		
		
		JPanel responseTimeLabelPanel = new JPanel();
		responseTimeLabelPanel.setBounds(0, 0, 120, 25);
		responseTimeLabelPanel.setBorder(border);
		responseTimeLabelPanel.add(new JLabel("RESPONSE TIME"));
		responseTimePanel.add(responseTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(responseTimePanel);
		
		waitTimePanel = new JPanel(null);
		waitTimePanel.setBorder(border);		
		waitTimePanel.setBounds(171, 1, 120, 250);
		timesPanel.add(waitTimePanel);
		
		JPanel waitTimeLabelPanel = new JPanel();
		waitTimeLabelPanel.setBounds(0, 0, 120, 25);
		waitTimeLabelPanel.setBorder(border);
		waitTimeLabelPanel.add(new JLabel("WAIT TIME"));
		waitTimePanel.add(waitTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(waitTimePanel);
		
		turnaroundTimePanel = new JPanel(null);
		turnaroundTimePanel.setBorder(border);		
		turnaroundTimePanel.setBounds(291, 1, 141, 250);
		timesPanel.add(turnaroundTimePanel);		
		
		JPanel turnaroundTimeLabelPanel = new JPanel();
		turnaroundTimeLabelPanel.setBounds(0, 0, 141, 25);
		turnaroundTimeLabelPanel.setBorder(border);
		turnaroundTimeLabelPanel.add(new JLabel("TURNAROUND TIME"));
		turnaroundTimePanel.add(turnaroundTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(turnaroundTimePanel);
	}
	
	private void initAvgTimeTable(int xOffset, int yOffset) {
		avgTimeTable = new JPanel(null);
		avgTimeTable.setBorder(border);		
		avgTimeTable.setBounds(xOffset, yOffset, 433, 26);		
		mlfqPanel.add(avgTimeTable);
		
		avgTimeLblPanel = new JPanel(null);
		avgTimeLblPanel.setBorder(border);		
		avgTimeLblPanel.setBounds(1, 1, 50, 25);
		
		JPanel avgTimeLbl = new JPanel();
		avgTimeLbl.setBounds(0, 0, 50, 25);
		avgTimeLbl.setBorder(border);
		avgTimeLbl.add(new JLabel("Avg:"));
		avgTimeLblPanel.add(avgTimeLbl);		
		avgTimeTable.add(avgTimeLblPanel);			
		
		avgResponseTime = new JPanel(null);
		avgResponseTime.setBorder(border);		
		avgResponseTime.setBounds(51, 1, 120, 25);		
		avgTimeTable.add(avgResponseTime);
		
		avgWaitTime = new JPanel(null);
		avgWaitTime.setBorder(border);		
		avgWaitTime.setBounds(171, 1, 120, 25);
		avgTimeTable.add(avgWaitTime);
		
		avgTurnaroundTime = new JPanel(null);
		avgTurnaroundTime.setBorder(border);		
		avgTurnaroundTime.setBounds(291, 1, 141, 25);
		avgTimeTable.add(avgTurnaroundTime);
		
		avgTimeTable2 = new JPanel(null);
		avgTimeTable2.setBorder(border);		
		avgTimeTable2.setBounds(xOffset-10, yOffset + 30, 442, 26);		
		mlfqPanel.add(avgTimeTable2);
		
		avgTimeLblPanel2 = new JPanel(null);
		avgTimeLblPanel2.setBorder(border);		
		avgTimeLblPanel2.setBounds(1, 1, 60, 25);
		
		JPanel avgTimeLbl2 = new JPanel();
		avgTimeLbl2.setBounds(0, 0, 60, 25);
		avgTimeLbl2.setBorder(border);
		avgTimeLbl2.add(new JLabel("Avg - I/O:"));
		avgTimeLblPanel2.add(avgTimeLbl2);		
		avgTimeTable2.add(avgTimeLblPanel2);			
		
		avgResponseTime2 = new JPanel(null);
		avgResponseTime2.setBorder(border);		
		avgResponseTime2.setBounds(61, 1, 120, 25);		
		avgTimeTable2.add(avgResponseTime2);
		
		avgWaitTime2 = new JPanel(null);
		avgWaitTime2.setBorder(border);		
		avgWaitTime2.setBounds(181, 1, 120, 25);
		avgTimeTable2.add(avgWaitTime2);
		
		avgTurnaroundTime2 = new JPanel(null);
		avgTurnaroundTime2.setBorder(border);		
		avgTurnaroundTime2.setBounds(301, 1, 140, 25);
		avgTimeTable2.add(avgTurnaroundTime2);
		
	}
	
	public static void addExecutingProcess(byte level, int processId, int executionTime, int timeNow) {
							
		Container container = null;		
		String processName = "p" + processId;		
		
		Border border = BorderFactory.createLineBorder(darkBlue);
		JPanel comp = new JPanel(null);			
		comp.setBorder(border);
		
		JLabel label = new JLabel(processName);
		label.setBounds(18, 18, 30, 15);
		comp.add(label);
		
		JPanel panel = null, timePanel = null;
				
		if(level == 0) {
			container = panel1;
			panel = panel1;
			timePanel = timePanel1;
		}else if(level == 1) {
			container = panel2;
			panel = panel2;
			timePanel = timePanel2;
		}else if(level == 2) {
			container = panel3;
			panel = panel3;
			timePanel = timePanel3;
		}else if(level == 3) {
			container = panel4;
			panel = panel4;
			timePanel = timePanel4;
		}
			
		if(timeCounter > 21){
			panel.setPreferredSize(new Dimension(panelWidth += 100, 73));
			timePanel.setSize(new Dimension(panelWidth, 73));
		}
		
		if(prevBurstLength < 0){
			xOffset = 0;
			yOffset = 0;				
		}else{							
			xOffset += prevBurstLength;											
		}
		
		/*System.out.println("==========[GC] prevEndTime: " + prevEndTime + " timeNow: " + timeNow + " executionTime: " + executionTime);*/
		if(prevEndTime < timeNow-executionTime) {
			
			addGap();		
			timeLabel[timeCounter] = new JLabel("" + (timeNow-executionTime));
			timeLabel[timeCounter].setFont(timeLabelFont);
			timeLabel[timeCounter].setForeground(Color.WHITE);
			timeLabel[timeCounter].setBounds(xOffset + 1, 2, 30, 15);
			
			timePanel.add(timeLabel[timeCounter++]);			
		}
		
		if(prevEndTime == 0) {
			timeLabel[timeCounter] = new JLabel("" + 0);
			timeLabel[timeCounter].setFont(timeLabelFont);
			timeLabel[timeCounter].setForeground(Color.WHITE);
			timeLabel[timeCounter].setBounds(1, 2, 30, 15);
			
			timePanel.add(timeLabel[timeCounter++]);			
		}
					
		timeLabel[timeCounter] = new JLabel("" + timeNow);
		timeLabel[timeCounter].setFont(timeLabelFont);
		timeLabel[timeCounter].setForeground(Color.WHITE);
		timeLabel[timeCounter].setBounds(xOffset + 37, 2, 30, 15);
		
		timePanel.add(timeLabel[timeCounter++]);
		prevBurstLength = 50;	
				
		prevEndTime = timeNow;	
		comp.setBounds(xOffset, yOffset, 50, 51);
		timePanel.repaint();
		timePanel.revalidate();
										
		container.add(comp);
		container.repaint();
		con.repaint();
		con.revalidate();
	}

	public static void addGap() {
		xOffset += 10;
	}
	
	private void reset(){
		Queue.threadStopped = false;
		Queue.processList.removeAll(Queue.processList);
		Queue.clockTime = 0;
		
		resetGanttChart();
		resetTimesInformation();
		resetArrivedTable();
		resetTimeAverages();
		
		con = this.getContentPane();
		mlfqPanel.setPreferredSize(new Dimension(con.getWidth(), con.getHeight() + Offset));
								
		con.repaint();
		con.revalidate();		
	}
		
	public static void addNewArrivedProcess(int processId, int arrivalTime, int burstTime, int priority){							
				
		processCount++;
		
		if(processCount > 12){
			pcbPanelHeight += 25;
			pcbPanel.setPreferredSize(new Dimension(575, pcbPanelHeight));
			pcbIdPanel.setSize(new Dimension(pcbIdPanel.getWidth(), pcbPanelHeight));
			pcbArrivalPanel.setSize(new Dimension(pcbArrivalPanel.getWidth(), pcbPanelHeight));
			pcbBurstPanel.setSize(new Dimension(pcbBurstPanel.getWidth(), pcbPanelHeight));
			pcbPriorityPanel.setSize(new Dimension(pcbPriorityPanel.getWidth(), pcbPanelHeight));
		}
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(1, procYOffset+=25, 40, 25);
		idLabelPanel.add(new JLabel(""+processId));
		pcbIdPanel.add(idLabelPanel);		

		JPanel arrLabelPanel = new JPanel();
		arrLabelPanel.setBounds(1, procYOffset, 110, 25);
		arrLabelPanel.add(new JLabel(""+arrivalTime));		
		pcbArrivalPanel.add(arrLabelPanel);
		
		JPanel burstLabelPanel = new JPanel();
		burstLabelPanel.setBounds(1, procYOffset, 110, 25);
		burstLabelPanel.add(new JLabel(""+burstTime));		
		pcbBurstPanel.add(burstLabelPanel);				
		
		JPanel priorityLabelPanel = new JPanel();
		priorityLabelPanel.setBounds(1, procYOffset, 108, 25);
		priorityLabelPanel.add(new JLabel(""+priority));		
		pcbPriorityPanel.add(priorityLabelPanel);
		
		pcbIdPanel.repaint();
		pcbIdPanel.revalidate();	
		pcbArrivalPanel.repaint();
		pcbArrivalPanel.revalidate();
		pcbBurstPanel.repaint();
		pcbBurstPanel.revalidate();
		pcbPriorityPanel.repaint();
		pcbPriorityPanel.revalidate();
	}

	public static void addTimesInformation(int processId, long responseTime, long waitTime, long turnaroundTime) {
		timesEntry++;
		if(timesEntry > 9){
			timesPanelHeight += 22;
			timesPanel.setPreferredSize(new Dimension(575, timesPanelHeight));
			timesIdPanel.setSize(new Dimension(timesIdPanel.getWidth(), timesPanelHeight));
			responseTimePanel.setSize(new Dimension(responseTimePanel.getWidth(), timesPanelHeight));
			waitTimePanel.setSize(new Dimension(waitTimePanel.getWidth(), timesPanelHeight));
			turnaroundTimePanel.setSize(new Dimension(turnaroundTimePanel.getWidth(), timesPanelHeight));
		}
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(1, timesYOffset+=25, 40, 25);
		idLabelPanel.add(new JLabel("" + processId));		
		timesIdPanel.add(idLabelPanel);		

		JPanel resLabelPanel = new JPanel();
		resLabelPanel.setBounds(1, timesYOffset, 110, 25);
		resLabelPanel.add(new JLabel("" + responseTime));		
		responseTimePanel.add(resLabelPanel);
		
		JPanel waitLabelPanel = new JPanel();
		waitLabelPanel.setBounds(1, timesYOffset, 110, 25);
		waitLabelPanel.add(new JLabel("" + waitTime));		
		waitTimePanel.add(waitLabelPanel);				
		
		JPanel turnaroundLabelPanel = new JPanel();
		turnaroundLabelPanel.setBounds(1, timesYOffset, 131, 25);
		turnaroundLabelPanel.add(new JLabel("" + turnaroundTime));		
		turnaroundTimePanel.add(turnaroundLabelPanel);
		
		timesIdPanel.repaint();
		timesIdPanel.revalidate();
		timesPanel.add(responseTimePanel);
		timesPanel.add(waitTimePanel);
		timesPanel.add(turnaroundTimePanel);
	
		con.repaint();
		con.revalidate();
	}
	
	public static void addTimeAverages(double avgResponse, double avgWait, double avgTurnaround, 
			double avgResponse2, double avgWait2, double avgTurnaround2) {
		JPanel resLabelPanel = new JPanel();
		resLabelPanel.setBounds(1, 1, 120, 23);
		resLabelPanel.add(new JLabel("" + String.format("%.2f", avgResponse)));		
		avgResponseTime.add(resLabelPanel);
		
		JPanel waitLabelPanel = new JPanel();
		waitLabelPanel.setBounds(1, 1, 120, 23);
		waitLabelPanel.add(new JLabel("" + String.format("%.2f", avgWait)));		
		avgWaitTime.add(waitLabelPanel);				
		
		JPanel turnaroundLabelPanel = new JPanel();
		turnaroundLabelPanel.setBounds(1, 1, 141, 23);
		turnaroundLabelPanel.add(new JLabel("" + String.format("%.2f", avgTurnaround)));		
		avgTurnaroundTime.add(turnaroundLabelPanel);
		
		JPanel resLabelPanel2 = new JPanel();
		resLabelPanel2.setBounds(1, 1, 120, 23);
		resLabelPanel2.add(new JLabel("" + String.format("%.2f", avgResponse2)));		
		avgResponseTime2.add(resLabelPanel2);
		
		JPanel waitLabelPanel2 = new JPanel();
		waitLabelPanel2.setBounds(1, 1, 120, 23);
		waitLabelPanel2.add(new JLabel("" + String.format("%.2f", avgWait2)));		
		avgWaitTime2.add(waitLabelPanel2);				
		
		JPanel turnaroundLabelPanel2 = new JPanel();
		turnaroundLabelPanel2.setBounds(1, 1, 141, 23);
		turnaroundLabelPanel2.add(new JLabel("" + String.format("%.2f", avgTurnaround2)));		
		avgTurnaroundTime2.add(turnaroundLabelPanel2);
				
		resLabelPanel.repaint();
		waitLabelPanel.repaint();
		turnaroundLabelPanel.repaint();
		resLabelPanel2.repaint();
		waitLabelPanel2.repaint();
		turnaroundLabelPanel2.repaint();
		resLabelPanel.revalidate();
		waitLabelPanel.revalidate();
		turnaroundLabelPanel.revalidate();
		resLabelPanel2.revalidate();
		waitLabelPanel2.revalidate();
		turnaroundLabelPanel2.revalidate();
	}
	
	private void resetGanttChart() {
		timeCounter = 0;
		xOffset = -1;
		prevBurstLength = -1;
		prevEndTime = 0;
		panelWidth = 1150;
		quantum1 = quantum2 = quantum3 = quantum4 = 2;
		
		if(level == 1) {
			if(panel1 == null || timePanel1 == null)
				return;
			
			panel1.removeAll();
			timePanel1.removeAll();
			
			panel1.add(timePanel1);
			
			panel1.repaint();
			panel1.revalidate();
			
			panel1.setSize(new Dimension(panelWidth, 73));
			panel1.setPreferredSize(new Dimension(panelWidth-5, 73));
			timePanel1.setSize(new Dimension(panelWidth, 20));
			
		}else if (level == 2) {
			if(panel1 == null || timePanel1 == null || 
					panel2 == null || timePanel2 == null)
				return;
			
			panel1.removeAll();
			panel2.removeAll();
			
			timePanel1.removeAll();
			timePanel2.removeAll();
			
			panel1.add(timePanel1);
			panel2.add(timePanel2);
			
			panel1.repaint();
			panel2.repaint();
			
			panel1.revalidate();
			panel2.revalidate();
			
		}else if (level == 3) {
			if(panel1 == null || timePanel1 == null || 
					panel2 == null || timePanel2 == null ||
						panel3 == null || timePanel3 == null)
				return;
			
			panel1.removeAll();
			panel2.removeAll();
			panel3.removeAll();
			
			timePanel1.removeAll();
			timePanel2.removeAll();
			timePanel3.removeAll();
			
			panel1.add(timePanel1);
			panel2.add(timePanel2);
			panel3.add(timePanel3);
			
			panel1.repaint();
			panel2.repaint();
			panel3.repaint();
			
			panel1.revalidate();
			panel2.revalidate();
			panel3.revalidate();
			
		}else if (level == 4) {			
			if(panel1 == null || timePanel1 == null || 
					panel2 == null || timePanel2 == null ||
						panel3 == null || timePanel3 == null ||
							panel4 == null || timePanel4 == null)
				return;
			
			panel1.removeAll();
			panel2.removeAll();
			panel3.removeAll();
			panel4.removeAll();
			
			timePanel1.removeAll();
			timePanel2.removeAll();
			timePanel3.removeAll();
			timePanel4.removeAll();
			
			panel1.add(timePanel1);
			panel2.add(timePanel2);
			panel3.add(timePanel3);			
			panel4.add(timePanel4);
			
			panel1.repaint();
			panel2.repaint();
			panel3.repaint();
			panel4.repaint();
			
			panel1.revalidate();
			panel2.revalidate();
			panel3.revalidate();
			panel4.revalidate();			
		}
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
	}
	
	private void resetArrivedTable() {
		procYOffset = 0;
		processCount = 0;
		pcbPanelHeight = 350;		
		
		if(pcbIdPanel == null || pcbPanel == null || 
				pcbArrivalPanel == null || pcbBurstPanel == null || 
					pcbPriorityPanel == null)
			return;
		
		pcbIdPanel.removeAll();
		pcbIdPanel.setBorder(border);		
		pcbIdPanel.setBounds(1, 1, 50, 350);
		
		pcbPanel.setPreferredSize(new Dimension(575, pcbPanelHeight));
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(0, 0, 50, 25);
		idLabelPanel.setBorder(border);
		idLabelPanel.add(new JLabel("PID"));
		pcbIdPanel.add(idLabelPanel);		
		pcbPanel.add(pcbIdPanel);		
		
		pcbArrivalPanel.removeAll();;
		pcbArrivalPanel.setBorder(border);		
		pcbArrivalPanel.setBounds(51, 1, 120, 350);		
		
		JPanel arrivalLabelPanel = new JPanel();
		arrivalLabelPanel.setBounds(0, 0, 120, 25);
		arrivalLabelPanel.setBorder(border);
		arrivalLabelPanel.add(new JLabel("ARRIVAL TIME"));
		pcbArrivalPanel.add(arrivalLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbArrivalPanel);
		
		pcbBurstPanel.removeAll();
		pcbBurstPanel.setBorder(border);		
		pcbBurstPanel.setBounds(171, 1, 120, 350);
		pcbPanel.add(pcbBurstPanel);
		
		JPanel burstLabelPanel = new JPanel();
		burstLabelPanel.setBounds(0, 0, 120, 25);
		burstLabelPanel.setBorder(border);
		burstLabelPanel.add(new JLabel("BURST TIME"));
		pcbBurstPanel.add(burstLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbBurstPanel);
		
		pcbPriorityPanel.removeAll();
		pcbPriorityPanel.setBorder(border);		
		pcbPriorityPanel.setBounds(291, 1, 110, 350);
		pcbPanel.add(pcbPriorityPanel);		
		
		JPanel priorityLabelPanel = new JPanel();
		priorityLabelPanel.setBounds(0, 0, 110, 25);
		priorityLabelPanel.setBorder(border);
		priorityLabelPanel.add(new JLabel("PRIORITY"));
		pcbPriorityPanel.add(priorityLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbPriorityPanel);
		
		pcbIdPanel.repaint();
		pcbArrivalPanel.repaint();
		pcbBurstPanel.repaint();
		pcbPriorityPanel.repaint();
		
		pcbIdPanel.revalidate();
		pcbArrivalPanel.revalidate();
		pcbBurstPanel.revalidate();
		pcbPriorityPanel.revalidate();
	}
	
	public static void resetTimesInformation() {
		timesEntry = 0;
		timesPanelHeight = 350;
		timesYOffset = 0;
		
		if(timesPanel == null)
			return;
		
		timesPanel.setPreferredSize(new Dimension(575, 250));
		
		timesIdPanel.removeAll();		
		JPanel timeIdLabelPanel = new JPanel();
		timeIdLabelPanel.setBounds(0, 0, 50, 25);
		timeIdLabelPanel.setBorder(border);
		timeIdLabelPanel.add(new JLabel("PID"));
		timesIdPanel.add(timeIdLabelPanel);		
		timesPanel.add(timesIdPanel);			
		
		responseTimePanel.removeAll();				
		JPanel responseTimeLabelPanel = new JPanel();
		responseTimeLabelPanel.setBounds(0, 0, 120, 25);
		responseTimeLabelPanel.setBorder(border);
		responseTimeLabelPanel.add(new JLabel("RESPONSE TIME"));
		responseTimePanel.add(responseTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(responseTimePanel);
		
		waitTimePanel.removeAll();		
		JPanel waitTimeLabelPanel = new JPanel();
		waitTimeLabelPanel.setBounds(0, 0, 120, 25);
		waitTimeLabelPanel.setBorder(border);
		waitTimeLabelPanel.add(new JLabel("WAIT TIME"));
		waitTimePanel.add(waitTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(waitTimePanel);
		
		turnaroundTimePanel.removeAll();
		JPanel turnaroundTimeLabelPanel = new JPanel();
		turnaroundTimeLabelPanel.setBounds(0, 0, 141, 25);
		turnaroundTimeLabelPanel.setBorder(border);
		turnaroundTimeLabelPanel.add(new JLabel("TURNAROUND TIME"));
		turnaroundTimePanel.add(turnaroundTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(turnaroundTimePanel);
		
		timesIdPanel.repaint();
		timesIdPanel.revalidate();
		responseTimePanel.repaint();
		responseTimePanel.revalidate();
		waitTimePanel.repaint();
		waitTimePanel.revalidate();
		turnaroundTimePanel.repaint();
		turnaroundTimePanel.revalidate();
	}
		
	public static void resetTimeAverages() {
		if(avgResponseTime != null)
			avgResponseTime.removeAll();
		if(avgWaitTime != null)
			avgWaitTime.removeAll();
		if(avgTurnaroundTime != null)
			avgTurnaroundTime.removeAll();
		if(avgResponseTime2 != null)
			avgResponseTime2.removeAll();
		if(avgWaitTime2 != null)
			avgWaitTime2.removeAll();
		if(avgTurnaroundTime2 != null)
			avgTurnaroundTime2.removeAll();
	}
	
	private void enableQuantum(int level) {
		if(level == 1) {
			setQuantum1.setEnabled(true);
		}else if(level == 2) {
			setQuantum2.setEnabled(true);
		}else if(level == 3) {
			setQuantum3.setEnabled(true);
		}else if(level == 4) {
			setQuantum4.setEnabled(true);
		}		
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
	}
	
	public void simulationDone(Object queue) {				
		
		if(queue instanceof FCFSQueue){
			((FCFSQueue) queue).stopThread();
		}else if(queue instanceof SJFQueue){
			((SJFQueue) queue).stopThread();
		}else if(queue instanceof SRTFQueue){
			((SRTFQueue) queue).stopThread();
		}else if(queue instanceof PQueue){
			((PQueue) queue).stopThread();
		}else if(queue instanceof NonPQueue){			
			((NonPQueue) queue).stopThread();
		}else if(queue instanceof RoundRobin){
			((RoundRobin) queue).stopThread();
		}
		
	}
}
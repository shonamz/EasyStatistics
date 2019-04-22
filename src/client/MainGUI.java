package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import frame.EasyFrame;

public class MainGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	public static JTabbedPane tabbedPane;
	public static JMenuItem newFileOption;
	public static JMenuItem importFromFileOption;
	public static JMenuItem openProjectAreaOption;
	public static JMenuItem signinOption;
	public static JMenuItem signoutOption;
	public static JMenuItem connectOption;
	public static JMenuItem closeOption;
	public static JMenuItem hostOption;
	public static JMenuItem exportCSVOption;
	public static JMenuItem saveProjectAreaOption;
	public static JMenu openHelpMenu;
	public static JMenu openSettingsMenu;
	public static JMenu networkMenu;
	public static HelpMenuGUI helpMenuGUI;
	public static GlobalOptionsGUI settingsGUI;

	public MainGUI() {
		super("Easy Statistics Software");
		setSize(900, 600);
		setLocationRelativeTo(null);
		initTabbedPane();
		initMenu();
		helpMenuGUI = new HelpMenuGUI(this);
		settingsGUI = new GlobalOptionsGUI(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initTabbedPane() {
		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// menu options
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		// file menu options
		newFileOption = new JMenuItem("New");
		fileMenu.add(newFileOption);
		newFileOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		newFileOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				new ProjectArea();
			}
		});
		
		closeOption = new JMenuItem("Close"); 
		closeOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,KeyEvent.CTRL_DOWN_MASK));
		closeOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(tabbedPane.getTabCount() > 0) {
					tabbedPane.remove(tabbedPane.getSelectedIndex());
				}
			}
			
		});
		
		fileMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {
				if(tabbedPane.getTabCount() > 0) {
					closeOption.setEnabled(true);
					exportCSVOption.setEnabled(true);
				} else {
					closeOption.setEnabled(false);
					exportCSVOption.setEnabled(false);
				}
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
				if(tabbedPane.getTabCount() > 0) {
					closeOption.setEnabled(true);
					exportCSVOption.setEnabled(true);
				} else {
					closeOption.setEnabled(false);
					exportCSVOption.setEnabled(false);
				}
			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				if(tabbedPane.getTabCount() > 0) {
					closeOption.setEnabled(true);
					exportCSVOption.setEnabled(true);
				} else {
					closeOption.setEnabled(false);
					exportCSVOption.setEnabled(false);
				}
			}
			
		});
		
		// import into project option
		importFromFileOption = new JMenuItem("Import from CSV");
		fileMenu.add(importFromFileOption);
		importFromFileOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
		importFromFileOption.addActionListener(new ImportCSVFromFileAction(null));
		
		// export to csv option
		exportCSVOption = new JMenuItem("Export to CSV");
		exportCSVOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,KeyEvent.CTRL_DOWN_MASK));
		fileMenu.add(exportCSVOption);
		
		fileMenu.add(closeOption);

		
		// open help menu option
		openHelpMenu = new JMenu("Commands");
		menuBar.add(openHelpMenu);
		
		openHelpMenu.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				helpMenuGUI.displayMenu();
			}
			
		});
		
		// networking option
		networkMenu = new JMenu("Network");
		menuBar.add(networkMenu);
		networkMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {
				if(tabbedPane.getTabCount() > 0) {
					hostOption.setEnabled(true);
					saveProjectAreaOption.setEnabled(true);
				} else {
					hostOption.setEnabled(false);
					saveProjectAreaOption.setEnabled(false);
				}
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
				if(tabbedPane.getTabCount() > 0) {
					hostOption.setEnabled(true);
					saveProjectAreaOption.setEnabled(true);
				} else {
					hostOption.setEnabled(false);
					saveProjectAreaOption.setEnabled(false);
				}
			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				if(tabbedPane.getTabCount() > 0) {
					hostOption.setEnabled(true);
					saveProjectAreaOption.setEnabled(true);
				} else {
					hostOption.setEnabled(false);
					saveProjectAreaOption.setEnabled(false);
				}
			}
			
		});
		
		// Connect to project area item
		connectOption = new JMenuItem("Connect to Project Area");
		
		// Host current project area item
		hostOption = new JMenuItem("Host Project Area");
		
		// import whole project area option
		openProjectAreaOption = new JMenuItem("Open Project Area");
		openProjectAreaOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		
		// save project area option
		
		saveProjectAreaOption = new JMenuItem("Save Project Area");
		saveProjectAreaOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		
		
		// Log in (if not logged in)
		signinOption = new JMenuItem("Sign in...");
		
		// Log out (if logged in)
		signoutOption = new JMenuItem("Sign out...");
		
		
		networkMenu.add(signinOption);
		networkMenu.add(signoutOption);
		networkMenu.add(connectOption);
		networkMenu.add(hostOption);
		networkMenu.add(openProjectAreaOption);
		networkMenu.add(saveProjectAreaOption);

		
		// open settings menu option
		openSettingsMenu = new JMenu("Settings");
		menuBar.add(openSettingsMenu);
		
		openSettingsMenu.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				settingsGUI.displayMenu();
			}
			
		});

		


	}

	public static void main(String[] args) {
		
		 
 {
			 SwingUtilities.invokeLater(new Runnable() {
			        public void run() {
			          MainGUI app = new MainGUI();
			          app.setVisible(true);
			        }
			      });
		}
	    
	}

}

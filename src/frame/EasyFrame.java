package frame;

 
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import client.HelpMenuGUI;
import client.MainGUI;
 
public class EasyFrame extends JFrame implements Navigator{
	public static MainGUI MainGUI;

	private static final long serialVersionUID = 9183816558021947333L;

	{
		setTitle("Statistics Demo");
		setSize(640,480);
		setMinimumSize(new Dimension(640,480));
 		getContentPane().add(new MainMenu(this));
 
		setLocationRelativeTo(null);
 
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
 
	
	public static void main(String[] args) {
		
		try {// Set the UI to cross platform for better portability
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			Font font;
 
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf"));
				Font styledAndSized = font.deriveFont(Font.BOLD, 15f);
				UIManager.put("Button.font", styledAndSized);
				Font styledAndSized2 = font.deriveFont(Font.BOLD, 17f);
				Font styledAndSized3 = font.deriveFont(Font.BOLD, 13f);

				UIManager.put("Label.foreground",Color.blue);

				UIManager.put("Label.font",styledAndSized2);

				UIManager.put("Button.foreground",Color.BLUE);
 				UIManager.put("Button.background",Color.gray);
 				UIManager.put("Panel.background",new Color(160, 250, 250));
 				UIManager.put("Menu.font",styledAndSized3);
 				UIManager.put("Menu.background",new Color(160, 250, 250));
 				UIManager.put("Menu.foreground",Color.blue);
 				 UIManager.put("TabbedPane.background",new Color(160, 250, 250));
 			    UIManager.put("TabbedPane.foreground",new Color(60, 50, 250));
				 UIManager.put("TabbedPane.font",styledAndSized3);

 				UIManager.put("TextPane.selectionBackground", new Color(160, 250, 250));
 				
 				UIManager.put("MenuItem.background",  Color.gray);
 				UIManager.put("MenuItem.font", styledAndSized3);
 				UIManager.put("MenuItem.foreground",  new Color(60, 250, 250));
 				UIManager.put("MenuItem.acceleratorFont",styledAndSized3);

 				UIManager.put("TabbedPane.borderColor", new Color(160, 250, 250));
 				UIManager.put("TabbedPane.darkShadow", new Color(160, 250, 250));
 				UIManager.put("TabbedPane.light", new Color(160, 250, 250));
 				UIManager.put("TabbedPane.highlight",new Color(160, 250, 250));
 				 
 				UIManager.put("TabbedPane.borderHightlightColor", new Color(160, 250, 250));
 				
 				UIManager.put("MenuBar.font",styledAndSized3);
 				UIManager.put("MenuBar.foreground",Color.BLUE);
 				UIManager.put("MenuBar.highlight", Color.BLUE);
 				UIManager.put("MenuBar.margin", Color.blue);
 				UIManager.put("MenuBar.selectedBackgroundPainter", Color.blue);
 				UIManager.put("MenuBar.selectionBackground", Color.blue);
 				UIManager.put("MenuBar.shadow", Color.BLUE);					
 				UIManager.put("MenuBar.selectionForeground", Color.blue);
 				 
 				UIManager.put("ToggleButton.font", styledAndSized3);
 				UIManager.put("ToggleButton.foreground",new Color(160, 250, 250) );
 			 
 		 		
 		 		UIManager.put("List.font",styledAndSized3);
 		 		UIManager.put("List.foreground", Color.blue);
 		 		
 		 		UIManager.put("TitledBorder.font",styledAndSized3);
 		 		UIManager.put("TitledBorder.titleColor", Color.blue);
 		 		UIManager.put("ToggleButton.background", Color.blue);
 				
 		 		UIManager.put("List.foreground", Color.blue);
 		 		
 		 		UIManager.put("List.font", styledAndSized3);
 
 		 		UIManager.put("ComboBox.font",styledAndSized3);
 		 		UIManager.put("ComboBox.foreground", Color.blue);
 		 		UIManager.put("ComboBox.background", Color.white);


			} catch (FontFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		 
		 
			 
 

		} catch (Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		} finally {
 			SwingUtilities.invokeLater(() -> {
 				EasyFrame of = new EasyFrame();
				 
  				of.setVisible(true);
  				});
		}
	}
				 
	

	@Override
	public void toMain() {
		getContentPane().removeAll();
		dispose();
		MainGUI= new MainGUI();
		MainGUI.setVisible(true);
		revalidate();
		repaint();
	}

	@Override
	public void toLogin() {
		
		getContentPane().removeAll();
		getContentPane().add(new Login(this));
		revalidate();
		repaint();
	}

	@Override
	public void toSignup() {
		getContentPane().removeAll();
		getContentPane().add(new SignUp(this));
		revalidate();
		repaint();
	}
}

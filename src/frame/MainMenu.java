package frame;

 
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

 

public class MainMenu extends JPanel {
	private static final long serialVersionUID = -2085019378387937271L;
	
 	private static final String mTitle = "Welcome to Statistics Demo";
	
	private final Navigator mNav;
	
 	
	JButton mLoginButton;
	JButton mSignupButton;
	JButton mOfflineButton;
	
	{
		setLayout(new GridBagLayout());
 		mLoginButton = new JButton("Login");
		mLoginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mNav.toLogin();
			}
		});
		mSignupButton = new JButton("Signup");
		mSignupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mNav.toSignup();
			}
		});
		mOfflineButton = new JButton("Offline");
		mOfflineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mNav.toMain();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3,3,3,3);
		add(mLoginButton,c);
		add(mSignupButton,c);
		
		c.gridwidth = 2;
		c.gridy = 2;
		add(mOfflineButton,c);
	}

	public MainMenu(Navigator inNav) {
		mNav = inNav;
	}

	protected void paintComponent(Graphics g) {
		ImageIcon gif = new ImageIcon("green.png");
		Image image = gif.getImage();
 		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.blue);  
		Font font;
		 
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf"));
			Font styledAndSized = font.deriveFont(Font.BOLD, 27f);
			g.setFont(styledAndSized);
			FontMetrics metrics = g.getFontMetrics(styledAndSized);
			int heightc = metrics.getHeight()/2;
			int widthc = metrics.stringWidth(mTitle)/2;
			g.drawString(mTitle, (getWidth()/2) - widthc, (getHeight()/3) - heightc);



		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		 
	}
}

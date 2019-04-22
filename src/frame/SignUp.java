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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

 

public class SignUp extends JPanel{
	private static final long serialVersionUID = -3504372201286353694L;
	
 	private static final String mTitle = "Statistics Demo";
	private Navigator mNav;

 	
	 
	
	private final JLabel mUsernameLabel;
	private final JTextField mUsernameField;
	private final JLabel mPasswordLabel;
	private final JPasswordField mPasswordField;
	private final JLabel mPasswordConfirmationLabel;
	private final JPasswordField mPasswordConfirmationField;
	private final JButton mSignupButton;
	
	
	{
		setLayout(new GridBagLayout());
		
		mSignupButton = new JButton("Signup");
		mSignupButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mNav.toMain();

			}
			
		 
		});
		
		mUsernameLabel = new JLabel("Username:");
		mUsernameField = new JTextField(10);
		mPasswordLabel = new JLabel("Password:");
		mPasswordField = new JPasswordField(10);
		mPasswordConfirmationLabel = new JLabel("  Repeat:");
		mPasswordConfirmationField = new JPasswordField(10);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3,3,3,3);
		add(mUsernameLabel,c);
		add(mUsernameField,c);
		c.gridy = 2;
		add(mPasswordLabel,c);
		add(mPasswordField,c);
		c.gridy = 3;
		add(mPasswordConfirmationLabel,c);
		add(mPasswordConfirmationField,c);
		c.gridy = 4;
		c.gridwidth = 3;
		add(mSignupButton,c);
	}

	 
	public SignUp(Navigator inNav) {
		mNav = inNav;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		ImageIcon gif = new ImageIcon("green.png");
		Image image = gif.getImage();
 		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
 		g.setColor(Color.blue);
		Font font;
		 
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf"));
			Font styledAndSized = font.deriveFont(Font.BOLD, 30f);
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

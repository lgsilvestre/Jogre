package org.jogre.client.awt;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import info.clearthought.layout.TableLayout;

public class JogreNewUserDialog extends JogreDialog {
	
	JPanel panel;
    JLabel newUserNameLabel, newPasswordLabel, repeatNewPasswordLabel, portLabel, serverLabel;
    JPasswordField newPassword, repeatNewPassword;
    JTextField newUserName, portTextField, serverTextField;
    JButton createNewUser, cancel;
    
    public JogreNewUserDialog() {
    	setUpGUI();
    }

	private void setUpGUI() {
		
        double pref = TableLayout.PREFERRED, space = 5;
        double [][] sizes = new double [][] {{space, pref, space, pref, space},
                                 {space, pref, space, pref, space, pref, space, pref, space, pref, space, pref}};
        JogrePanel panel = new JogrePanel (sizes);
        portLabel = new JLabel("Port: ");
        serverLabel = new JLabel("Server: ");
        portTextField = new JTextField(4);
        serverTextField = new JTextField(20);
		newUserNameLabel = new JLabel("Username: ");
		newPasswordLabel = new JLabel("Password: ");
		repeatNewPasswordLabel = new JLabel("Repeat Password: ");
		newUserName = new JTextField(12);
		newPassword = new JPasswordField(12);
		repeatNewPassword = new JPasswordField(12);
		createNewUser = new JButton("Create");
		cancel = new JButton("Cancel");
		
        panel.add (newUserNameLabel,     "1,1,r,c");
        panel.add (newPasswordLabel,     "1,3,r,c");
        panel.add (repeatNewPasswordLabel,       "1,5,r,c");
        panel.add (serverLabel, "1,7,r,c");
        panel.add (portLabel, "1,9,r,c");
        panel.add (createNewUser,         "1,11,r,c");
        panel.add (newUserName, "3,1,l,c");
        panel.add (newPassword, "3,3,l,c");
        panel.add (repeatNewPassword,   "3,5,l,c");
        panel.add (serverTextField, "3,7,1,c");
        panel.add (portTextField, "3,9,1,c");
        panel.add (cancel,     "3,11,l,c");
        
        addListeners();

		add(panel);
	}
    
	private void addListeners() {
		
		createNewUser.addActionListener(
			new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						
					}
				}
		);
		cancel.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						close();
					}
				}				
		);
		
	}
	

	private void close() {
		this.setVisible(false);
	}
	
}

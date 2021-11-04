package org.briarjar.briarjar.gui;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LoginGridPane extends GridPane
{
	private Image         imgWelcome;
	private Text          txtWelcome;
	private TextField     tfUsername;
	private PasswordField passwordField;
	private Button        btSignInRegister;

	public LoginGridPane()
	{
		initComponents();
		addComponents();
		addHandlers();
	}
	

	private void initComponents()
	{
		setBackground(new Background(new BackgroundFill(
		                             Paint.valueOf("#ffffff"), null, getInsets())));
		
		setHgap(10);
		setVgap(10);
		setAlignment(Pos.CENTER);
		
		try 
		{
			//imgWelcome = new Image(getClass().getResource("briar-logo.png").toExternalForm());
			//imgViewWelcome = new ImageView(imgWelcome);
		} catch (Exception e) {
			MainGUI.showAlert(AlertType.ERROR, "Configured welcome image not found.");
		}
		
		txtWelcome = new Text("Welcome to Briar");
			txtWelcome.setFont(Font.font(20));
		setHalignment(txtWelcome, HPos.CENTER);
		
		
		tfUsername = new TextField();
			tfUsername.setPromptText("Enter a Nickname");
		passwordField = new PasswordField();
			passwordField.setPromptText("Enter Passphrase");
		
		btSignInRegister = new Button("Register");

		/*
		if (...) { // isRegistered() logic
			tfUsername.setVisible(false);
			btSignInRegister.setText("Sign In");
		}

		 */
		
	}
	
	
	private void addComponents()
	{
		// TODO: don't skip rows, instead correct the heights!
		//add(imgViewWelcome,   0, 0);
		add(txtWelcome,       0, 2);
		add(tfUsername,       0, 4);
		add(passwordField,    0, 5);
		add(btSignInRegister, 0, 6);
	}
	
	
	private void addHandlers()
	{
		tfUsername.setOnKeyReleased(e -> switchToPassphrase(e));
	}

	// ============================ getters/setters ============================

	public Button getBtSignInRegister()
	{
		return btSignInRegister;
	}
	
	public String getUsername()
	{
		return tfUsername.getText().trim();
		//TODO trim maybe should not be done, since the user is not aware about!
	}
	
	public String getPassphrase()
	{
		String passphrase = passwordField.getText();
		passwordField.clear();
		
		return passphrase;
	}
	
	public PasswordField getPassphraseField()
	{
		return passwordField;
	}
	
	public TextField getUsernameField()
	{
		return tfUsername;
	}

	// ============================ logic ============================
	
	private void switchToPassphrase(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER)
		{
			passwordField.requestFocus();
		}
	}
	
//	private void clickedtfUsername()
//	{
//		tfUsername.clear();
//	}
}

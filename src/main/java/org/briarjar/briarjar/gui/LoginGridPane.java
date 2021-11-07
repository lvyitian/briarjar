package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.LoginViewModel;

import java.text.DecimalFormat;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginGridPane extends GridPane
{
	private ImageView imgWelcome;

	private Text          txtWelcome;
	private TextField     tfUsername;
	private PasswordField passwordField;
	private Button        btSignInRegister;
	private Text          passwordStrength;

	private LoginViewModel loginViewModel;
	private RootBorderPane rootBorderPane;

	public LoginGridPane(LoginViewModel loginViewModel, RootBorderPane rootBorderPane)
	{
		this.loginViewModel = loginViewModel;
		this.rootBorderPane = rootBorderPane;

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
			// imgWelcome = new ImageView(new Image(getClass().getResource("briar-logo.png").toExternalForm()));
		} catch (Exception e) {
			MainGUI.showAlert(AlertType.ERROR, "Configured welcome image not found.");
		}
		
		txtWelcome = new Text("Welcome to Briar");
			txtWelcome.setFont(Font.font("System", FontWeight.LIGHT, 20));
		setHalignment(txtWelcome, HPos.CENTER);
		
		
		tfUsername = new TextField();
			tfUsername.setPromptText("Enter a Nickname");
		passwordField = new PasswordField();
			passwordField.setPromptText("Enter Passphrase");

		passwordStrength = new Text("0.00");

		btSignInRegister = new Button("Register");

		// TODO: architectural change -> split in different login / registration "scene"
		if (loginViewModel.isRegistered()) {
			tfUsername.setVisible(false);
			btSignInRegister.setText("Sign In");
		}
		
	}
	
	
	private void addComponents()
	{
		// TODO: don't skip rows, instead correct the heights!
		//add(imgView, 0, 0);
		add(txtWelcome,       0, 2);
		add(tfUsername,       0, 4);
		add(passwordField,    0, 5);
		add(passwordStrength, 1, 5);
		add(btSignInRegister, 0, 6);
	}
	
	
	private void addHandlers()
	{
		tfUsername.setOnKeyReleased(e -> switchToPassphrase(e));
		passwordField.setOnKeyTyped(e -> passwordStrength());
		btSignInRegister.setOnAction(e -> loginOrRegister());
	}

	// ============================ logic ============================

	public void loginOrRegister()
	{
		loginViewModel.setUsername(tfUsername.getText());
		loginViewModel.setPassword(passwordField.getText());

		try {
			if (loginViewModel.isRegistered())
				loginViewModel.signIn();
			else
				loginViewModel.register();
		}
		catch (Exception e)	{
			MainGUI.showAlert(AlertType.ERROR, "Login/SignUp Error: " + e.getMessage());
		}

		try {
			loginViewModel.start();
		}
		catch (Exception e)	{
			MainGUI.showAlert(AlertType.ERROR, "Startup Error: " + e.getMessage());
		}

		rootBorderPane.switchToMainScene();
	}

	private void passwordStrength()
	{
		loginViewModel.setPassword(passwordField.getText());
		DecimalFormat df = new DecimalFormat("#.##");
		passwordStrength.setText(df.format(loginViewModel.getPasswordStrength()));
	}

	// ============================ others ============================
	
	private void switchToPassphrase(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER)
			passwordField.requestFocus();
	}
}

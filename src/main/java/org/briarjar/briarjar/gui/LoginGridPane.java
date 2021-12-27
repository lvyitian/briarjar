package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.crypto.DecryptionException;

import java.text.DecimalFormat;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

@Singleton
public class LoginGridPane extends GridPane {

	private final LoginViewModel lvm;
	private final RootBorderPane rootBorderPane;
	private ImageView imgWelcome;
	private Text txtWelcome;
	private TextField tfUsername;
	private PasswordField passphraseField;
	private Button btSignInRegister;
	private Text passphraseStrength;

	@Inject
	public LoginGridPane(
			RootBorderPane rootBorderPane,
			LoginViewModel lvm)
	{
		this.lvm = lvm;
		this.rootBorderPane = rootBorderPane;

		initComponents();
		addComponents();
		addHandlers();

		prepareSignInSignUpMask();
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
			imgWelcome = null;
			// imgWelcome = new ImageView(new Image(getClass().getResource("briar-logo.png").toExternalForm()));
		} catch (Exception e)
		{
			MainGUI.showAlert(AlertType.ERROR,
					"Configured welcome image not found.");
		}

		txtWelcome = new Text("Welcome to Briar");
		txtWelcome.setFont(Font.font("System", FontWeight.LIGHT, 20));
		setHalignment(txtWelcome, HPos.CENTER);


		tfUsername = new TextField();
		tfUsername.setPromptText("Enter a Nickname");
		passphraseField = new PasswordField();
		passphraseField.setPromptText("Enter Passphrase");

		passphraseStrength = new Text("0.00");

		btSignInRegister = new Button("Sign Up");

		// TODO: architectural change -> split in different login / registration "scene"
		if (lvm.accountExists())
		{
			tfUsername.setVisible(false);
			btSignInRegister.setText("Sign In");
		}

	}


	private void addComponents()
	{
		// TODO: don't skip rows, instead correct the heights!
		//add(imgView, 0, 0);
		add(txtWelcome, 0, 2);
		add(tfUsername, 0, 4);
		add(passphraseField, 0, 5);
		add(passphraseStrength, 1, 5);
		add(btSignInRegister, 0, 6);
	}


	private void addHandlers()
	{
		tfUsername.setOnKeyReleased(e -> switchToPassphrase(e));
		passphraseField.setOnKeyTyped(e -> passphraseStrength());
		//btSignInRegister.setOnAction(e -> btSignInRegister());
	}

	// ============================ logic ============================

	//todo 4k prüfung vorab ob acc existiert
	private void prepareSignInSignUpMask()
	{
		if (lvm.accountExists())
			;// show 1x passphrase
		else
			;// show 1x username, 2x passphrase
	}

	private void signIn()
	{
		try
		{
			lvm.signIn(passphraseField.getText());

			//todo 4k offline mode possible? // if (...
			lvm.start();

		} catch (DecryptionException e)
		{
			MainGUI.showAlert(AlertType.ERROR, "Could not decrypt " +
					"database - wrong passphrase entered?\n(" + e.getMessage() +
					")");
		} catch (InterruptedException e)
		{
			MainGUI.showAlert(AlertType.ERROR,
					"Startup Error: " + e.getMessage());
		}

		rootBorderPane.switchToMainScene();
	}

	private void signUp()
	{
		try
		{
			lvm.signUp(tfUsername.getText(), passphraseField.getText());

			//todo 4k offline mode possible? // if (...
			lvm.start();

		} catch (InterruptedException e)
		{
			MainGUI.showAlert(AlertType.ERROR,
					"Startup Error: " + e.getMessage());
		}

		rootBorderPane.switchToMainScene();
	}

	private void passphraseStrength()
	{
		lvm.setPassphrase(passphraseField.getText());
		DecimalFormat df = new DecimalFormat("#.##");
		passphraseStrength.setText(df.format(lvm.getPassphraseStrength()));
	}

	// ============================ others ============================

	private void switchToPassphrase(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
			passphraseField.requestFocus();
	}
}

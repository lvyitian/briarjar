package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.crypto.DecryptionException;

import javax.inject.Inject;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
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

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

public class SignInGridPane extends GridPane {

	private final LoginViewModel lvm;
	private ImageView imgWelcome;
	private Text txtWelcome;
	private Text txtSubtext;
	private PasswordField passphraseField;
	private Button btSignIn;
	private GUIUtils guiUtils;

	@Inject
	public SignInGridPane(
			LoginViewModel lvm)
	{
		this.lvm = lvm;
	}

	public void create()
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
			imgWelcome = null;
			// imgWelcome = new ImageView(new Image(getClass().getResource("briar-logo.png").toExternalForm()));
		} catch (Exception e)
		{
			showAlert(Alert.AlertType.ERROR,
					"Configured welcome image not found.");
		}

		txtWelcome = new Text("Welcome to BriarJar!");
		txtWelcome.setFont(Font.font("System", FontWeight.LIGHT, 20));
		txtSubtext = new Text("Please Sign In with your Account");
		txtSubtext.setFont(Font.font("System", FontWeight.LIGHT, 15));
		setHalignment(txtWelcome, HPos.CENTER);

		passphraseField = new PasswordField();
		passphraseField.setPromptText("Enter your Passphrase");


		btSignIn = new Button("Sign In");
	}

	private void addComponents()
	{
		// TODO: don't skip rows, instead correct the heights!
		//add(imgView, 0, 0);
		add(txtWelcome, 0, 2);
		add(txtSubtext, 0, 3);
		add(passphraseField, 0, 5);
		add(btSignIn, 0, 6);
	}


	private void addHandlers()
	{
		btSignIn.setOnAction(e -> signIn());
		passphraseField.setOnKeyReleased(this::switchToMain);
	}

	private void signIn()
	{
		try
		{
			lvm.signIn(passphraseField.getText());
			lvm.start();

			btSignIn.setDisable(true);
			guiUtils.switchToMain();
		} catch (DecryptionException e)
		{
			showAlert(Alert.AlertType.ERROR, "Could not decrypt " +
					"database - wrong passphrase entered?\n(" + e.getDecryptionResult() +
					")");
		} catch (InterruptedException e)
		{
			showAlert(Alert.AlertType.ERROR,
					"Startup Error: " + e.getMessage());
		}
	}

	private void switchToMain(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
		{
			signIn();
		}
	}

	public void setGUIUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

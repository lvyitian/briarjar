package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import java.text.DecimalFormat;
import java.util.Objects;

import javax.inject.Inject;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
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

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

public class SignUpGridPane extends GridPane {

	private final LoginViewModel lvm;
	private ImageView imgWelcome;
	private Text txtWelcome;
	private Text txtSubtext;
	private TextField tfUsername;
	private PasswordField passphraseField;
	private Text passphraseStrength;
	private Button btSignUp;
	private GUIUtils guiUtils;

	@Inject
	public SignUpGridPane(LoginViewModel lvm)
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
			String obj = Objects.requireNonNull(
					getClass().getResource("/briar-icon.png")).toExternalForm();
			imgWelcome = new ImageView(new Image(obj));
		} catch (Exception e)
		{
			showAlert(Alert.AlertType.ERROR,
					"Configured welcome image not found.");
		}

		txtWelcome = new Text("Welcome to BriarJar!");
		txtWelcome.setFont(Font.font("System", FontWeight.LIGHT, 20));
		txtSubtext = new Text("Please Create an Account.");
		txtSubtext.setFont(Font.font("System", FontWeight.LIGHT, 15));
		setHalignment(txtWelcome, HPos.CENTER);

		tfUsername = new TextField("");
		tfUsername.setPromptText("Enter a Username");
		passphraseField = new PasswordField();
		passphraseField.setPromptText("Enter a Passphrase");

		passphraseStrength = new Text("0.00");

		btSignUp = new Button("Sign Up");
	}

	private void addComponents()
	{
		// TODO: don't skip rows, instead correct the heights!
		add(imgWelcome, 0, 0);
		add(txtWelcome, 0, 2);
		add(txtSubtext, 0, 3);
		add(tfUsername, 0, 4);
		add(passphraseField, 0, 5);
		add(passphraseStrength, 1, 5);
		add(btSignUp, 0, 6);
	}


	private void addHandlers()
	{
		btSignUp.setOnAction(e -> signUp());
		passphraseField.setOnKeyTyped(e -> passphraseStrength());
		tfUsername.setOnKeyReleased(this::switchToPassphrase);
		passphraseField.setOnKeyReleased(this::switchToMain);

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
			showAlert(Alert.AlertType.ERROR,
					"Startup Error: " + e.getMessage());
		}

		btSignUp.setDisable(true);
		guiUtils.switchToMain();
	}

	private void passphraseStrength()
	{
		lvm.setPassphrase(passphraseField.getText());
		DecimalFormat df = new DecimalFormat("#.##");
		passphraseStrength.setText(df.format(lvm.getPassphraseStrength()));
	}

	private void switchToPassphrase(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
			passphraseField.requestFocus();
	}

	private void switchToMain(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
		{
			signUp();
		}
	}

	public void setGUIUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

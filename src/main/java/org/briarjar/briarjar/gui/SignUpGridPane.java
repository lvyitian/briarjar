package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import java.text.DecimalFormat;
import java.util.Objects;

import javax.inject.Inject;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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


public class SignUpGridPane extends GridPane {

	private final LoginViewModel lvm;
	private final LifeCycleViewModel lifeCycleViewModel;

	private ImageView imgWelcome;
	private Text txtWelcome;
	private Text txtSubtext;
	private JFXTextField tfUsername;
	private JFXPasswordField passphraseField;
	private Text passphraseStrength;
	private Button btSignUp;
	private GUIUtils guiUtils;

	@Inject
	public SignUpGridPane( LoginViewModel     lvm,
	                       LifeCycleViewModel lifeCycleViewModel )
	{
		this.lvm = lvm;
		this.lifeCycleViewModel = lifeCycleViewModel;
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
					getClass().getResource("/images/briar-icon.png")).toExternalForm();
			imgWelcome = new ImageView(new Image(obj));
		} catch (Exception e)
		{
			guiUtils.showMaterialDialog("Image not found.", e.getMessage());
		}

		txtWelcome = new Text("Welcome to BriarJar!");
		txtWelcome.setFont(Font.font("Arial", FontWeight.LIGHT, 20));
		txtSubtext = new Text("Please create an account.");
		txtSubtext.setFont(Font.font("Arial", FontWeight.LIGHT, 15));
		setHalignment(txtWelcome, HPos.CENTER);

		tfUsername = new JFXTextField("");
		tfUsername.setPromptText("Enter a username");
		passphraseField = new JFXPasswordField();
		passphraseField.setPromptText("Enter a passphrase");

		passphraseStrength = new Text("0.00");

		btSignUp = new Button("Sign up");
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
			lifeCycleViewModel.start();
		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
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

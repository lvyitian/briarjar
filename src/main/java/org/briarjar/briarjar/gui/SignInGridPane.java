package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import java.util.Objects;

import javax.inject.Inject;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
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

public class SignInGridPane extends GridPane {

	private final LoginViewModel lvm;
	private final LifeCycleViewModel lifeCycleViewModel;
	private ImageView imgWelcome;
	private Text txtWelcome;
	private Text txtSubtext;
	private JFXPasswordField passphraseField;
	private JFXButton btSignIn;
	private GUIUtils guiUtils;

	@Inject
	public SignInGridPane( LoginViewModel     lvm,
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
		txtSubtext = new Text("Please Sign In with your account");
		txtSubtext.setFont(Font.font("Arial", FontWeight.LIGHT, 15));
		setHalignment(txtWelcome, HPos.CENTER);

		passphraseField = new JFXPasswordField();
		passphraseField.requestFocus();
		passphraseField.setLabelFloat(true);
		passphraseField.setPromptText("Enter your passphrase");


		btSignIn = new JFXButton("Sign in");
	}

	private void addComponents()
	{
		// TODO: don't skip rows, instead correct the heights!
		add(imgWelcome, 0, 0);
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
			btSignIn.setDisable(true);
			lifeCycleViewModel.start();
			guiUtils.switchToMain();

		} catch ( GeneralException e )
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
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

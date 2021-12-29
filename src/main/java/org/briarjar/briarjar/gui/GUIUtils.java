package org.briarjar.briarjar.gui;

import javax.inject.Inject;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class GUIUtils {

	private final RootBorderPane rootBorderPane;
	private final MessagesBorderPane messagesBorderPane;
	private final SignInGridPane signInGridPane;
	private final SignUpGridPane signUpGridPane;

	@Inject
	public GUIUtils(RootBorderPane rootBorderPane,
	                SignInGridPane signInGridPane,
	                SignUpGridPane signUpGridPane,
	                MessagesBorderPane messagesBorderPane)
	{
		this.messagesBorderPane = messagesBorderPane;
			messagesBorderPane.setGUIUtils(this);
			messagesBorderPane.create();
		this.signInGridPane = signInGridPane;
			signInGridPane.setGUIUtils(this);
			signInGridPane.create();
		this.signUpGridPane = signUpGridPane;
			signUpGridPane.setGUIUtils(this);
			signUpGridPane.create();
		this.rootBorderPane = rootBorderPane;
			rootBorderPane.setGUIUtils(this);
			rootBorderPane.create();
	}

	public static void showAlert(Alert.AlertType alertType, String message)
	{
		System.out.println(message);
		Alert alert = new Alert(alertType, message, ButtonType.OK);
		alert.setHeaderText(null);
		alert.setHeight(350);
		alert.setTitle("BriarJar Message");
		alert.showAndWait();
	}

	// Getters

	public RootBorderPane getRootBorderPane()
	{
		return rootBorderPane;
	}

	public SignInGridPane getSignInGridPane()
	{
		return signInGridPane;
	}

	public SignUpGridPane getSignUpGridPane()
	{
		return signUpGridPane;
	}

	public MessagesBorderPane getMessagesBorderPane()
	{
		return messagesBorderPane;
	}

	public void switchToMain()
	{
		rootBorderPane.disableComponents(false);
		rootBorderPane.setCenter(messagesBorderPane);
	}

	public void switchToSignUp()
	{
		rootBorderPane.disableComponents(false);
		rootBorderPane.setCenter(signUpGridPane);
	}

	public void switchToSignIn()
	{
		rootBorderPane.disableComponents(false);
		rootBorderPane.setCenter(signInGridPane);
	}
}

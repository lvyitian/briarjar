package org.briarjar.briarjar.gui;

import javax.inject.Inject;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class GUIUtils {

	private RootBorderPane rootBorderPane;
	private LoginGridPane loginGridPane;
	private MessagesBorderPane messagesBorderPane;

	@Inject
	public GUIUtils(RootBorderPane rootBorderPane, LoginGridPane loginGridPane, MessagesBorderPane messagesBorderPane)
	{
		this.messagesBorderPane = messagesBorderPane;
			messagesBorderPane.setGUIUtils(this);
			messagesBorderPane.create();
		this.loginGridPane = loginGridPane;
			loginGridPane.setGUIUtils(this);
			loginGridPane.create();
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

	public LoginGridPane getLoginGridPane()
	{
		return loginGridPane;
	}

	public MessagesBorderPane getMessagesBorderPane()
	{
		return messagesBorderPane;
	}

	public void switchToMainScene()
	{
		rootBorderPane.disableComponents(false);
		rootBorderPane.setCenter(messagesBorderPane);
	}
}

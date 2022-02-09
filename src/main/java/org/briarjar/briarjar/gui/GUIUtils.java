package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;

import org.briarjar.briarjar.Main;

import javax.inject.Inject;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;

public class GUIUtils {

	private final RootStackPane rootStackPane;
	private final RootBorderPane rootBorderPane;
	private final MessagesBorderPane messagesBorderPane;
	private final SignInGridPane signInGridPane;
	private final SignUpGridPane signUpGridPane;
	private final AddContactDialog addContactDialog;
	private final MessageListView messageListView;


	@Inject
	public GUIUtils(RootBorderPane rootBorderPane,
	                SignInGridPane signInGridPane,
	                SignUpGridPane signUpGridPane,
	                MessagesBorderPane messagesBorderPane,
	                AddContactDialog addContactDialog,
					MessageListView messageListView)
	{

		// no create() call
		this.messageListView = messageListView;
		messageListView.setGuiUtils(this);

		this.addContactDialog = addContactDialog;
		addContactDialog.setGuiUtils(this);

		this.messagesBorderPane = messagesBorderPane;
		messagesBorderPane.setGUIUtils(this);

		// create() call needed!
		this.signInGridPane = signInGridPane;
		signInGridPane.setGUIUtils(this);
		signInGridPane.create();
		this.signUpGridPane = signUpGridPane;
		signUpGridPane.setGUIUtils(this);
		signUpGridPane.create();
		this.rootBorderPane = rootBorderPane;
		rootBorderPane.setGUIUtils(this);
		rootBorderPane.create();

		this.rootStackPane = new RootStackPane(rootBorderPane);
	}

	// Getters

	public RootBorderPane getRootBorderPane()
	{
		return rootBorderPane;
	}

	public RootStackPane getRootStackPane() { return rootStackPane; }

	public SignInGridPane getSignInGridPane()
	{
		return signInGridPane;
	}

	public SignUpGridPane getSignUpGridPane()
	{
		return signUpGridPane;
	}

	public AddContactDialog getAddContactDialog() { return addContactDialog; }

	public MessagesBorderPane getMessagesBorderPane() { return messagesBorderPane; }

	public MessageListView getMessageListView() { return messageListView; }

	public void switchToMain()
	{
		rootBorderPane.disableComponents(false);
		messagesBorderPane.create();
		rootBorderPane.setCenter(messagesBorderPane);
	}

	public void switchToSignUp()
	{
		rootBorderPane.disableComponents(false);
		rootBorderPane.setCenter(signUpGridPane);
		relaunchApp();
	}

	public void switchToSignIn()
	{
		rootBorderPane.disableComponents(false);
		rootBorderPane.setCenter(signInGridPane);
		relaunchApp();
	}

	private void relaunchApp()
	{
		// relaunch app TODO is there a better solution
		var briarJarApp = Main.launchApp();
		var mainGUI = briarJarApp.getMainGUI();
		mainGUI.init();
		mainGUI.start(MainGUI.getPrimaryStage());
	}

	public void showMaterialDialog(String header, String body) {
		showMaterialDialog(rootStackPane, rootBorderPane, header, body);
	}

	public void showMaterialDialog(StackPane root, Node nodeToBlur, String header, String body) {
		BoxBlur blur = new BoxBlur(3, 3, 3);

		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.TOP);
		JFXButton button = new JFXButton("Okay");

		button.setOnAction(e -> dialog.close());
		dialogLayout.setActions(button);
		dialogLayout.setHeading(new Label(header));
		dialogLayout.setBody(new Label(body));
		dialog.show();


		dialog.setOnDialogClosed((JFXDialogEvent event1) -> nodeToBlur.setEffect(null));
		nodeToBlur.setEffect(blur);
	}
}

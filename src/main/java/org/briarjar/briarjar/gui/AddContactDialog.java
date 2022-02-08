package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;

import java.util.Objects;

import javax.inject.Inject;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AddContactDialog extends Stage {

	private StackPane rootStackPane;
	private GridPane gridPane;
	private Scene scene;
	private Clipboard clipboard;
	private Label lbOwnLink;
	private JFXTextField peerHandshakeLink, peerAlias;
	private JFXButton btCopyOwnLink, btPasteLinkOfFriend, btStartHandshake;

	private GUIUtils guiUtils;

	private final ContactViewModel cvm;

	@Inject
	public AddContactDialog(ContactViewModel cvm)
	{
		this.cvm = cvm;
	}

	public void create()
	{
		initComponents();
		addComponents();
		addHandlers();
	}


	private void initComponents()
	{
		String obj = Objects.requireNonNull(
				getClass().getResource("/images/briar-icon.png")).toExternalForm();
		getIcons().add(new Image(obj));

		rootStackPane = new StackPane();
		gridPane = new GridPane();

		gridPane.setBackground(new Background(new BackgroundFill(
				Paint.valueOf("#ffffff"), null, gridPane.getInsets())));
		gridPane.setHgap(10);
		gridPane.setVgap(16);
		gridPane.setAlignment(Pos.CENTER);

		try
		{
			lbOwnLink = new Label(cvm.getHandshakeLink());
			lbOwnLink.setFont(Font.font("Arial", FontWeight.LIGHT, 14));
		} catch (GeneralException e)
		{
			lbOwnLink = new Label("Errored... " + e.getMessage());
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}

		peerHandshakeLink = new JFXTextField();
		peerHandshakeLink.setLabelFloat(true);
		peerHandshakeLink.setPromptText("Link starts with briar://");
		peerAlias = new JFXTextField();
		peerAlias.setLabelFloat(true);
		peerAlias.setPromptText("Choose an alias for your friend here");

		btCopyOwnLink = new JFXButton("Copy your link");
		btPasteLinkOfFriend = new JFXButton("Paste friends link");
		btStartHandshake = new JFXButton("Start handshaking");

		rootStackPane.getChildren().add(gridPane);
		scene = new Scene(rootStackPane, 800, 350);
		clipboard = Clipboard.getSystemClipboard();
	}

	private void addComponents()
	{
		setTitle("Add a new contact");

		gridPane.add(lbOwnLink, 0, 0, 2, 1);
		gridPane.add(btCopyOwnLink, 4, 0, 1, 1);

		gridPane.add(peerHandshakeLink, 0, 1, 2, 1);
		gridPane.add(btPasteLinkOfFriend, 4, 1, 1, 1);

		gridPane.add(peerAlias, 0, 2, 2, 1);
		gridPane.add(btStartHandshake, 4, 2, 1, 1);

		setScene(scene);
	}

	private void addHandlers()
	{
		btCopyOwnLink.setOnAction(e -> copyOwnLink());
		btPasteLinkOfFriend.setOnAction(e -> pasteLinkOfFriend());
		btStartHandshake.setOnAction(e -> startHandshake());
	}

	private void startHandshake()
	{
		try
		{
			cvm.addPendingContact(peerHandshakeLink.getText(), peerAlias.getText());
			close();
		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(rootStackPane, gridPane, e.getTitle(), e.getMessage());
		}
	}

	private void pasteLinkOfFriend()
	{
		if(clipboard.hasString())
		{
			String s = clipboard.getString();
			if(s.startsWith("briar://"))
				peerHandshakeLink.setText(s);
			else
				guiUtils.showMaterialDialog(rootStackPane, gridPane, "Clipboard Error", "Not a briar handshake link.");
		}
		else
			guiUtils.showMaterialDialog(rootStackPane, gridPane, "Clipboard Error", "No text in clipboard.");
	}

	private void copyOwnLink()
	{
		ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(lbOwnLink.getText());
		clipboard.setContent(clipboardContent);
	}

	public void setGuiUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.db.DbException;

import java.security.GeneralSecurityException;
import java.util.Objects;

import javax.inject.Inject;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

public class AddContactDialog extends Stage {

	private GridPane gridPane;
	private Scene scene;
	private Clipboard clipboard;
	private Label lbOwnLink;
	private TextField handshakeLinkOfFriend, aliasOfFriend;
	private Button btCopyOwnLink, btPasteLinkOfFriend, btStartHandshake;

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
				getClass().getResource("/briar-icon.png")).toExternalForm();
		getIcons().add(new Image(obj));

		gridPane = new GridPane();

		gridPane.setBackground(new Background(new BackgroundFill(
				Paint.valueOf("#ffffff"), null, gridPane.getInsets())));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setAlignment(Pos.CENTER);

		try
		{
			lbOwnLink = new Label(cvm.getHandshakeLink());
			lbOwnLink.setFont(Font.font("System", FontWeight.LIGHT, 14));
		} catch (DbException e)
		{
			lbOwnLink = new Label("Errored... " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, e.getMessage());
		}

		handshakeLinkOfFriend = new TextField();
		handshakeLinkOfFriend.setPromptText("Starts with briar://");
		aliasOfFriend = new TextField();
		aliasOfFriend.setPromptText("Enter Alias/Nickname of Friend here");

		btCopyOwnLink = new Button("Copy your Link");
		btPasteLinkOfFriend = new Button("Paste Friends Link");
		btStartHandshake = new Button("Start Handshaking!");
		scene = new Scene(gridPane, 800, 350);
		clipboard = Clipboard.getSystemClipboard();
	}

	private void addComponents()
	{
		setTitle("Add a new Contact");

		gridPane.add(lbOwnLink, 0, 0, 2, 1);
		gridPane.add(btCopyOwnLink, 4, 0, 1, 1);

		gridPane.add(handshakeLinkOfFriend, 0, 1, 2, 1);
		gridPane.add(btPasteLinkOfFriend, 4, 1, 1, 1);

		gridPane.add(aliasOfFriend, 0, 2, 2, 1);
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
			cvm.addPendingContact(handshakeLinkOfFriend.getText(), aliasOfFriend.getText());
			close();
		} catch (GeneralSecurityException | FormatException | DbException | GeneralException e)
		{
			showAlert(Alert.AlertType.ERROR, e.getClass() + " - " + e.getMessage());
		}
	}

	private void pasteLinkOfFriend()
	{
		if(clipboard.hasString())
		{
			String s = clipboard.getString();
			if(s.startsWith("briar://"))
				handshakeLinkOfFriend.setText(s);
			else
				showAlert(Alert.AlertType.ERROR, "Not a briar:// Link!");
		}
		else
			showAlert(Alert.AlertType.ERROR, "No String in Clipboard");
	}

	private void copyOwnLink()
	{
		ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(lbOwnLink.getText());
		clipboard.setContent(clipboardContent);
	}
}

package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@Singleton
public class MessagesBorderPane extends BorderPane {

	private final ContactViewModel cvm;

	private TextArea messageBox;
	private MessageListView messageListView;
	private VBox contactList;
	private boolean isContactListVisible;
	private GUIUtils guiUtils;

	@Inject
	public MessagesBorderPane(ContactViewModel cvm)
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
		messageBox = new TextArea();
		messageBox.setPromptText("Type in your private message here...");
		messageBox.setPrefHeight(35);
		messageBox.setMinHeight(35);

		contactList = new VBox();
		contactList.setPrefWidth(110);
		isContactListVisible = false;

		// statusText = new Label("Select a contact to show status.");
	}


	private void addComponents()
	{
		setCenter(messageListView);
		setBottom(messageBox);
	}


	private void addHandlers()
	{

	}

	// ============================ getters/setters ============================

	public boolean isContactListVisible()
	{
		return isContactListVisible;
	}

	// ============================ logic ============================

	/*
	 * This method will be used in the RootBorderPane.
	 */
	public void showContactList()

	{
		// TODO:
		// 1. "translate" all Contacts into Buttons/Items of a ListView<String>
		// 2. add all contacts to contactList

		try
		{
			Collection<Contact> contacts =
					// todo 4k: getPendingContacts()
					cvm.getAcceptedContacts(); //.getContacts();

			for (Contact c : contacts)
			{
				Button b = new Button(c.getAlias());
				b.setPrefWidth(contactList.getPrefWidth());
				b.setOnAction(e -> loadMessages(c.getId()));
				contactList.getChildren().setAll(new Button(c.getAlias()));
			}
		} catch (DbException e)
		{
			e.printStackTrace();
		}

		// test
		Button alice = new Button("<alice>");
		alice.setPrefWidth(contactList.getPrefWidth());
		contactList.getChildren().setAll(alice); // for testing only...

		setLeft(contactList);
		isContactListVisible = true;
	}

	private void loadMessages(ContactId id)
	{
		messageListView = guiUtils.getMessageListView();
		messageListView.update(id);
	}

	public void hideContactList()
	{
		setLeft(null);
		isContactListVisible = false;
	}

	public void setGUIUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

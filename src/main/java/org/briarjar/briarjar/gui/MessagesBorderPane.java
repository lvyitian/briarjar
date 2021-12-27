package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.ViewModelProvider;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;

import java.util.Collection;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MessagesBorderPane extends BorderPane
{

	private TextArea messageBox;
	private MessageListView messageListView;
	private VBox contactList;
	
	private boolean isContactListVisible;

	private final ViewModelProvider viewModelProvider;
	
	public MessagesBorderPane(ViewModelProvider viewModelProvider) {
		this.viewModelProvider = viewModelProvider;
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
		isContactListVisible = false; // TODO: Get this property from a (possibly JSON) settings file!
		
		messageListView = new MessageListView();
		
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

		try {
			Collection<Contact> contacts = viewModelProvider.getContactManager().getContacts();

			for(Contact c : contacts)
				contactList.getChildren().setAll(new Button(c.getAlias()));

		} catch (DbException e) {
			e.printStackTrace();
		}
		//contactList.getChildren().setAll(new Button("<alice>")); // for testing only...

		// contactList.getChildren().addAll(...)
		setLeft(contactList);
		isContactListVisible = true;
	}
	
	public void hideContactList()
	{
		setLeft(null);
		isContactListVisible = false;
	}
}

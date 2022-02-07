package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.sync.InvalidMessageException;

import javax.inject.Inject;

import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

public class MessageListView extends ListView<String>
{

	private Contact contact;
	private final ConversationViewModel cvm;

	@Inject
	public MessageListView (ConversationViewModel cvm)
	{
		this.cvm = cvm;
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	public void update()
	{
		getItems().clear();
		try
		{
			var headers =  cvm.getMessageHeaders(contact.getId());
			for(var header : headers)
			{
				String message = header.isLocal() ? "<- " + cvm.getMessageText(header.getId()) : "-> " + cvm.getMessageText(header.getId());

				// TODO add metadata
				getItems().add(message);
			}

			getSelectionModel().selectLast();
			scrollTo(getSelectionModel().getSelectedIndex());

		} catch (GeneralException e)
		{
			showAlert(Alert.AlertType.ERROR, e.getMessage());
		}
	}

	public void sendMessage(String messageText)
	{
		try
		{
			cvm.write(contact.getId(), System.currentTimeMillis(), messageText);
		} catch (GeneralException e)
		{
			e.printStackTrace();
		}
	}


	public void setContact(Contact contact)
	{
		this.contact = contact;
	}
}

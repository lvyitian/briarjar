package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.PrivateMessage;

import java.util.List;

import javax.inject.Inject;

import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

public class MessageListView extends ListView<ConversationMessageHeader>
{

	private final ConversationViewModel cvm;

	@Inject
	public MessageListView (ConversationViewModel cvm)
	{
		this.cvm = cvm;
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	public void update(ContactId id)
	{
		try
		{
			getItems().setAll(cvm.getMessageHeaders(id));
		} catch (DbException e)
		{
			showAlert(Alert.AlertType.ERROR, e.getMessage());
		}
	}
}

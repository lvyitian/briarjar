package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;

import java.util.List;

import javax.inject.Inject;

import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

public class MessageListView extends ListView<String>
{

	private Contact contact;
	private final ConversationViewModel cvm;
	private List<ConversationMessageHeader> headers;

	@Inject
	public MessageListView (ConversationViewModel cvm)
	{
		this.cvm = cvm;
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	public void initListView()
	{
		if(contact != null)
		{
			getItems().clear();
			try
			{
				// init headers
				headers = cvm.getMessageHeaders(contact.getId()).stream().toList();

				// add messages
				for (var header : headers)
				{
					addMessageToListView(header);
				}

				// set selected
				getSelectionModel().selectLast();
				scrollTo(getSelectionModel().getSelectedIndex());

			} catch (GeneralException e)
			{
				showAlert(Alert.AlertType.ERROR, e.getMessage());
			}
		}
	}

	public void updateOnMessageAdded()
	{
		if(contact != null)
		{
			try
			{
				var updatedHeader = cvm.getMessageHeaders(contact.getId()).stream().toList();
				// starting index = last index of header
				int headersLastIndex = headers.size()-1;
				for(int i = headersLastIndex; i < updatedHeader.size(); i++)
				{
					// TODO ideally, there shouldn't be this 'if' - but without it, past messages occur duplicated
					if(updatedHeader.get(i).getTimestamp() > headers.get(headersLastIndex).getTimestamp())
						addMessageToListView(updatedHeader.get(i));
				}

				// update headers
				this.headers = updatedHeader;

				// set selected
				getSelectionModel().selectLast();
				scrollTo(getSelectionModel().getSelectedIndex());
			} catch (GeneralException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void updateOnMessageReceived(PrivateMessageHeader header)
	{
		if(contact != null)
		{
			try
			{
				// update headers
				this.headers = cvm.getMessageHeaders(contact.getId()).stream().toList();

				// add message
				addMessageToListView(header);

				// set selected
				getSelectionModel().selectLast();
				scrollTo(getSelectionModel().getSelectedIndex());
			} catch (GeneralException e)
			{
				showAlert(Alert.AlertType.ERROR, e.getMessage());
			}
		}
	}

	private void addMessageToListView(ConversationMessageHeader header)
	{
		String message = null;
		try
		{
			message = header.isLocal() ?
					"<- " + cvm.getMessageText(header.getId()) :
					"-> " + cvm.getMessageText(header.getId());
		} catch (GeneralException e)
		{
			showAlert(Alert.AlertType.ERROR, e.getMessage());
		}

		// TODO add metadata
		getItems().add(message);
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

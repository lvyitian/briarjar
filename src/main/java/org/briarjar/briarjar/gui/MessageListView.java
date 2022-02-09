package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXListView;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import javafx.scene.control.SelectionMode;


public class MessageListView extends JFXListView<String>
{

	private Contact contact;
	private final ConversationViewModel cvm;
	private List<ConversationMessageHeader> headers;


	private GUIUtils guiUtils;

	@Inject
	public MessageListView (ConversationViewModel cvm)
	{
		this.cvm = cvm;
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	public void initListView()
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
				guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
			}
	}

	public void updateOnMessageAdded()
	{
		try
		{
			var updatedHeader = cvm.getMessageHeaders(contact.getId()).stream().toList();
			// starting index = last index of header
			int headersLastIndex = headers.size()-1;
			if(headersLastIndex >= 0)
			{
				for (int i = headersLastIndex; i < updatedHeader.size(); i++)
				{
					// TODO ideally, there shouldn't be this 'if' - but without it, past messages occur duplicated
					if (updatedHeader.get(i).getTimestamp() >
							headers.get(headersLastIndex).getTimestamp())
						addMessageToListView(updatedHeader.get(i));
				}
			} else
				addMessageToListView(updatedHeader.get(0));

			// update headers
			this.headers = updatedHeader;

			// set selected
			getSelectionModel().selectLast();
			scrollTo(getSelectionModel().getSelectedIndex());
		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	public void updateOnMessageReceived(PrivateMessageHeader header)
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
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	private void addMessageToListView(ConversationMessageHeader header)
	{
		// TODO add metadata

		try
		{
			String time = millisecondsToLocalTime(header.getTimestamp());
			if(header.isLocal())
			{
				getItems().add("[" + time + "] " + " You > " + cvm.getMessageText(header.getId()));
			} else
			{
				getItems().add("[" + time + "] " + "Peer > " + cvm.getMessageText(header.getId()));
			}

		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	public void sendMessage(String messageText)
	{
		try
		{
			cvm.write(contact.getId(), System.currentTimeMillis(), messageText);
		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	public void deleteAllMessages()
	{
		try
		{
			cvm.deleteAllMessages(contact.getId());
		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	private static String millisecondsToLocalTime(long ms) {
		Instant instant = Instant.ofEpochMilli(ms);
		DateTimeFormatter formatter
				= DateTimeFormatter.ofPattern("HH:mm");
		LocalTime time = instant.atZone(ZoneId.systemDefault()).toLocalTime();
		return formatter.format(time);
	}

	public Contact getContact()
	{
		return contact;
	}

	public void setContact(Contact contact)
	{
		this.contact = contact;
	}

	public void setGuiUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

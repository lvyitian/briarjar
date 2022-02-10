package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXListView;

import org.briarjar.briarjar.gui.MessageListView.GUIMessage;
import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MessageListView extends JFXListView<GUIMessage> {

	private Contact contact;
	private final ConversationViewModel cvm;
	private List<ConversationMessageHeader> headers;

	private MessageContextMenu messageContextMenu;


	private GUIUtils guiUtils;

	@Inject
	public MessageListView(ConversationViewModel cvm)
	{
		this.cvm = cvm;
		init();
		addHandlers();
	}

	private void init()
	{
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		messageContextMenu = new MessageContextMenu();

		// wrap text automatically
		setCellFactory(p -> new ListCell<>(){

			@Override
			public void updateItem(GUIMessage item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item==null) {
					setGraphic(null);
					setText(null);
					// other stuff to do...
				}
				else
				{
					// set the width's
					setMinWidth(p.getWidth());
					setMaxWidth(p.getWidth());
					setPrefWidth(p.getWidth());
					// allow wrapping
					setWrapText(true);
					setText(item.toString());
				}
			}
		});
	}

	private void addHandlers()
	{
		setOnMouseClicked(this::checkMouseClick);
	}

	private void checkMouseClick(MouseEvent e)
	{
		if(e.getButton() == MouseButton.SECONDARY)
		{
			System.out.println("show");
			messageContextMenu.show(guiUtils.getRootStackPane(), e.getScreenX(), e.getScreenY());
			System.out.println("show done");

		}
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
			var updatedHeader =
					cvm.getMessageHeaders(contact.getId()).stream().toList();
			// starting index = last index of header
			int headersLastIndex = headers.size() - 1;
			if (headersLastIndex >= 0)
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
			this.headers =
					cvm.getMessageHeaders(contact.getId()).stream().toList();

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
		getItems().add(new GUIMessage(header));
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

	private void deleteSelectedMessages()
	{
		var selectedItems =
				getSelectionModel().getSelectedItems();

		if(!selectedItems.isEmpty())
		{
			Collection<MessageId> messagesToDelete =
					new ArrayList<>(selectedItems.size());
			for (var selectedItem : selectedItems)
			{
				messagesToDelete.add(selectedItem.getHeader().getId());
			}

			try
			{
				cvm.deleteMessages(contact.getId(), messagesToDelete);
				initListView(); // re-init
			} catch (GeneralException e)
			{
				guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
			}
		} else
			guiUtils.showMaterialDialog("Deleting messages", "Please select messages first.");
	}

	private void showMessageInformation()
	{
		var selectedItems =
				getSelectionModel().getSelectedItems();
		if(!selectedItems.isEmpty())
		{
			if(selectedItems.size() == 1)
			{
				var header = selectedItems.get(0).getHeader();
				String metadata;
				if(header.isLocal())
				{
					metadata = "ID: " + header.getId() +
							"\nMessage read: " + header.isRead() +
							"\nMessage sent: " + header.isSent() +
							"\nMessage seen: " + header.isSeen() +
							"\nTimestamp: " + millisecondsToLocalDateTime(header.getTimestamp(), "dd.MM.yyyy HH:mm");
				} else
				{
					metadata = "ID: " + header.getId() +
							"\nTimestamp: " + millisecondsToLocalDateTime(header.getTimestamp(), "dd.MM.yyyy HH:mm");
				}

				guiUtils.showMaterialDialog("Showing metadata", metadata);
			} else
				guiUtils.showMaterialDialog("Showing metadata","Please select only one message.");
		} else
			guiUtils.showMaterialDialog("Showing metadata", "Please select a message first.");
	}

	private static String millisecondsToLocalDateTime(long ms, String pattern)
	{
		Instant instant = Instant.ofEpochMilli(ms);
		DateTimeFormatter formatter
				= DateTimeFormatter.ofPattern(pattern);
		LocalDateTime dateTime =
				instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		return formatter.format(dateTime);
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


	/* GUI MESSAGE AS INNER CLASS */

	public class GUIMessage {

		private final ConversationMessageHeader header;

		public GUIMessage(ConversationMessageHeader header)
		{
			this.header = header;
		}

		public ConversationMessageHeader getHeader()
		{
			return header;
		}

		@Override
		public String toString()
		{
			try
			{
				String time = millisecondsToLocalDateTime(header.getTimestamp(), "HH:mm");
				if (header.isLocal())
				{
					return "[" + time + "] " + " You > " +
							cvm.getMessageText(header.getId());
				} else
				{
					return "[" + time + "] " + "Peer > " +
							cvm.getMessageText(header.getId());
				}


			} catch (GeneralException e)
			{
				guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
				return "Message could not be read. " + e.getMessage();
			}
		}
	}


	/* CONTEXT MENU AS INNER CLASS */

	class MessageContextMenu extends ContextMenu {

		public MessageContextMenu()
		{
			initAndAddComponents();
		}

		private void initAndAddComponents()
		{
			MenuItem miInfo = new MenuItem("More information");
			MenuItem miDelete = new MenuItem("Delete message");

			miInfo.setOnAction(e -> showMessageInformation());
			miDelete.setOnAction(e -> deleteSelectedMessages());

			getItems().addAll(miInfo, miDelete);
		}
	}

}



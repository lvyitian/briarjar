package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.event.MessageAddedEvent;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import static com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog;

public class Conversation extends EventListenerViewModel {

	private Panel contentPanel, chatBoxPanel, newMessagePanel;
	private TextBox newMessage;
	private ActionListBox chatBox;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	private final ConversationViewModel cvm;
	private Contact contact;

	List<ConversationMessageHeader> headers;

	@Inject
	public Conversation(EventBus eventBus,
	                    ConversationViewModel cvm)
	{
		super(eventBus);
		super.onInit();

		this.cvm = cvm;

		init();
	}

	/* INIT */

	private void init()
	{
		contentPanel = new Panel(new BorderLayout());
		chatBoxPanel = new Panel(new LinearLayout(Direction.VERTICAL));
		newMessagePanel = new Panel(new BorderLayout());

		newMessage = new TextBox("", TextBox.Style.MULTI_LINE);

		chatBox = new ActionListBox(new TerminalSize(48, 18));

		window = new BasicWindow("Chat with a friend");
		window.setFixedSize(new TerminalSize(50, 20));
		window.setComponent(contentPanel.withBorder(Borders.singleLine()));
	}

	/* CREATE WINDOW */

	private void createWindow()
	{
		removeAllComponents();
		initChatBox();

		window.setTitle("Chat with " + contact.getAlias());

		newMessagePanel.addComponent(newMessage.setLayoutData(BorderLayout.Location.CENTER));
		newMessagePanel.addComponent(
				new Button("Send", () -> {
					if(!newMessage.getText().isEmpty())
					{
						try
						{
							cvm.write(contact.getId(), System.currentTimeMillis(), newMessage.getText());
							newMessage.setText("");     // clear the newMessage text field
						} catch (GeneralException e)
						{
							tuiUtils.show(e);
						}
					}
					else
						showMessageDialog(textGUI, "Empty message", "Please write a message",
									MessageDialogButton.OK);
				}).setLayoutData(BorderLayout.Location.RIGHT));

		newMessagePanel.addComponent(
				new Button("Back", () -> {
					try
					{
						contact = null;
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}).setLayoutData(BorderLayout.Location.LEFT));

		// contentPanel.addComponent(...)
		contentPanel.addComponent(
				newMessagePanel.withBorder(Borders.singleLine("Write your message...")).setLayoutData(BorderLayout.Location.BOTTOM));
		contentPanel.addComponent(
				chatBoxPanel.withBorder(Borders.singleLine("Chat")).setLayoutData(BorderLayout.Location.CENTER));

	}

	/* PANELS REMOVER */

	private void removeAllComponents()
	{
		newMessagePanel.removeAllComponents();
		chatBoxPanel.removeAllComponents();
		contentPanel.removeAllComponents();
	}

	/* RENDER */

	public void render()
	{
		createWindow();
		textGUI.addWindowAndWait(window);
	}

	/* UPDATE CLASS */

	private void updateOnMessageReceived(PrivateMessageHeader header)
	{

		try
		{
			// update headers
			this.headers = cvm.getMessageHeaders(contact.getId()).stream().toList();

			// add message
			addMessageToChatBox(header);

			// set selected
			chatBox.setSelectedIndex(headers.size() - 1);
		} catch (GeneralException e)
		{
			tuiUtils.show(e);
		}
	}

	private void updateOnMessageAdded()
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
					if (updatedHeader.get(i).getTimestamp() >
							headers.get(headersLastIndex).getTimestamp())
						addMessageToChatBox(updatedHeader.get(i));
				}
			} else
				addMessageToChatBox(updatedHeader.get(0));
			// update headers
			this.headers = updatedHeader;

			// set selected
			chatBox.setSelectedIndex(headers.size() - 1);
		} catch (GeneralException e)
		{
			tuiUtils.show(e);
		}
	}

	private void initChatBox()
	{
		chatBox.clearItems();
		if(contact != null)
		{
			try
			{
				headers =
						cvm.getMessageHeaders(contact.getId()).stream()
						   .toList();
			/* loop through all messages and add them to the chatBox */
			for (var header : headers)
			{
				addMessageToChatBox(header);
			}
			} catch (GeneralException e)
			{
				tuiUtils.show(e);
			}
			chatBox.setSelectedIndex(headers.size() - 1);
		}

		chatBoxPanel.addComponent(chatBox);
	}

	private void addMessageToChatBox(ConversationMessageHeader header)
	{
		try {
			String time = millisecondsToLocalDateTime(header.getTimestamp(), "HH:mm");
			String message = header.isLocal() ?
				"[" + time + "] <- " + cvm.getMessageText(header.getId()) :
				"[" + time + "] -> " + cvm.getMessageText(header.getId());

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
			String finalMetaData = metadata;

			chatBox.addItem(message, () ->
					showMessageDialog(textGUI,
							"Message metadata", finalMetaData,
							MessageDialogButton.Close));
		} catch (GeneralException e)
		{
			tuiUtils.show(e);
		}
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

	/* SETTERS */

	public void setTuiUtils(TUIUtils tuiUtils)
	{
		this.tuiUtils = tuiUtils;
	}

	public void setTextGUI(MultiWindowTextGUI textGUI)
	{
		this.textGUI = textGUI;
	}

	public void setContact(Contact contact)
	{
		this.contact = contact;
	}

	/* EVENT HANDLING */

	@Override
	@NotNullByDefault
	public void eventOccurred(Event e)
	{
	/*
		BRAMBLE-API -------------------------

		ContactAddedEvent
		ContactAliasChangedEvent
		ContactRemovedEvent
		ContactVerifiedEvent
		PendingContactAddedEvent
		PendingContactRemovedEvent
		PendingContactStateChangedEvent

		IdentityAddedEvent
		IdentityRemovedEvent

		KeyAgreementAbortedEvent
		KeyAgreementFailedEvent
		KeyAgreementFinishedEvent
		KeyAgreementListeningEvent
		KeyAgreementStartedEvent
		KeyAgreementStoppedListeningEvent
		KeyAgreementWaitingEvent

		NetworkStatusEvent

		ConnectionClosedEvent
		ConnectionOpenedEvent
		ContactConnectedEvent
		ContactDisconnectedEvent

		RendezvousConnectionClosedEvent
		RendezvousConnectionOpenedEvent
		RendezvousPollEvent

		SettingsUpdatedEvent

		MessageAddedEvent
		MessageRequestedEvent
		MessagesAckedEvent
		MessageSharedEvent
		MessagesSentEvent
		MessageStateChangedEvent
		MessageToAckEvent
		MessageToRequestEvent


		BRIAR-API ---------------------------

		ConversationMessagesDeletedEvent

		ConversationMessageReceivedEvent

		IntroductionAbortedEvent
		IntroductionRequestReceivedEvent
		IntroductionResponseReceivedEvent

		AttachmentReceivedEvent
		PrivateMessageReceivedEvent
		*/
		if(contact != null)
		{
			if (e instanceof PrivateMessageReceivedEvent)
			{
				System.out.println("PrivateMessageReceivedEvent...");
				updateOnMessageReceived(
						((PrivateMessageReceivedEvent) e).getMessageHeader());
			} else if (e instanceof MessageAddedEvent)
			{
				System.out.println("MessageAddedEvent...");
				updateOnMessageAdded();
			}
		}
		/* TODO handle these events
		else if (e instanceof MessagesSentEvent)
		{
			System.out.println("MessagesSentEvent...");
			updateOnMessageSent(((MessagesSentEvent) e).getMessageIds());
		} else if (e instanceof MessageStateChangedEvent)
		{
			System.out.println("MessageStateChangedEvent...");
			updateOnMessageStateChanged(((MessageStateChangedEvent) e).getMessageId());
		}
		 */
	}
}

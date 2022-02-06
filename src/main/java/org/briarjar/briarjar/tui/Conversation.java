package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.event.MessageAddedEvent;
import org.briarproject.bramble.api.sync.event.MessageStateChangedEvent;
import org.briarproject.bramble.api.sync.event.MessagesSentEvent;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.event.AttachmentReceivedEvent;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;

import java.util.List;

import javax.inject.Inject;

public class Conversation extends EventListenerViewModel {

	private Panel contentPanel, chatBoxPanel, newMessagePanel;
	private TextBox newMessage;
	private ActionListBox chatBox;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	private final ConversationViewModel cvm;
	private Contact contact;

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
		updateChatBox();

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
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					else
						MessageDialog.showMessageDialog(textGUI, "Empty Messagebox", "Please write a message",
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
				newMessagePanel.withBorder(Borders.singleLine("Write your Message...")).setLayoutData(BorderLayout.Location.BOTTOM));
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

	private void updateChatBox()
	{
		chatBox.clearItems();

		/* loop through all messages and add them to the chatBox */
		try
		{
			List<ConversationMessageHeader> headers = cvm.getMessageHeaders(contact.getId()).stream().toList();
			for(var header : headers)
			{
				try
				{
					String message = header.isLocal() ? "   " + cvm.getMessageText(header.getId()) : "=> " + cvm.getMessageText(header.getId());

					String metaData = "ID: " + header.getId() +
							"\nisRead:" + header.isRead() +
							"\nisLocal: " + header.isLocal() +
							"\nisSent: " + header.isSent() +
							"\nisSeen: " + header.isSeen() +
							"\nTimestamp: " + header.getTimestamp();

					chatBox.addItem(message, () ->
							MessageDialog.showMessageDialog(textGUI, "Message Metadata", metaData,
									MessageDialogButton.Close));
				} catch (GeneralException e)
				{
					e.printStackTrace();
				}
			}
			chatBox.setSelectedIndex(headers.size()-1);
		} catch (GeneralException e)
		{
			e.printStackTrace();
		}

		chatBoxPanel.addComponent(chatBox);
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
		if (e instanceof PrivateMessageReceivedEvent)
		{
			System.out.println("PrivateMessageReceivedEvent...");
			updateChatBox();
		} else if (e instanceof AttachmentReceivedEvent)
		{
			System.out.println("AttachmentReceivedEvent...");
			updateChatBox();
		} else if (e instanceof MessageAddedEvent)
		{
			System.out.println("MessageAddedEvent...");
			updateChatBox();
		} else if (e instanceof MessagesSentEvent)
		{
			System.out.println("MessagesSentEvent...");
			updateChatBox();
		} else if (e instanceof MessageStateChangedEvent)
		{
			System.out.println("MessageStateChangedEvent...");
			updateChatBox();
		}

	}
}

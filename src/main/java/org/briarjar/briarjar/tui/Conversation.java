package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent;
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent;
import org.briarproject.bramble.api.sync.event.MessageAddedEvent;
import org.briarproject.bramble.api.sync.event.MessageStateChangedEvent;
import org.briarproject.bramble.api.sync.event.MessagesSentEvent;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.event.AttachmentReceivedEvent;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class Conversation extends EventListenerViewModel {

	private Panel contentPanel, chatBoxPanel, newMessagePanel;
	private TextBox newMessage;
	private ActionListBox chatBox;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	private final EventBus eventBus;
	private ConversationViewModel cvm;
	private Contact contact;

	@Inject
	public Conversation(EventBus eventBus,
	                    ConversationViewModel cvm)
	{
		super(eventBus);
		super.onInit();

		this.eventBus = eventBus;
		this.cvm = cvm;
	}

	public void createWindow()
	{
		updateChatBox();

		newMessagePanel.addComponent(newMessage.setLayoutData(BorderLayout.Location.CENTER));

		newMessagePanel.addComponent(
				new Button("Send", () -> {
					if(!newMessage.getText().isEmpty())
					{
						try
						{
							cvm.write(contact.getId(), System.currentTimeMillis(), newMessage.getText());
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
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}).setLayoutData(BorderLayout.Location.LEFT));
	}

	public void render()
	{
		contentPanel = new Panel(new BorderLayout());
		chatBoxPanel = new Panel(new LinearLayout(Direction.VERTICAL));
		newMessagePanel = new Panel(new BorderLayout());

		newMessage = new TextBox("", TextBox.Style.MULTI_LINE);

		contentPanel.addComponent(
				newMessagePanel.withBorder(Borders.singleLine("Write your Message...")).setLayoutData(BorderLayout.Location.BOTTOM));
		contentPanel.addComponent(
				chatBoxPanel.withBorder(Borders.singleLine("Chat")).setLayoutData(BorderLayout.Location.CENTER));

		// init instance
		createWindow();

		// render window
		this.window = new BasicWindow("Chat with " + contact.getAlias());
		window.setFixedSize(new TerminalSize(50, 20));
		window.setComponent(contentPanel.withBorder(Borders.singleLine()));

		textGUI.addWindowAndWait(window);
	}

	public void updateChatBox()
	{
		if(chatBox != null)
			chatBoxPanel.removeAllComponents();

		chatBox = new ActionListBox(new TerminalSize(48, 18));
		/* loop through all messages and add them to the chatBox */
		try
		{
			List<ConversationMessageHeader> headers = cvm.getMessageHeaders(contact.getId()).stream().toList();
			for(var header : headers)
			{
				try
				{
					String message = header.isLocal() ? "< " + cvm.getMessageText(header.getId()) : "> " + cvm.getMessageText(header.getId());

					String metaData = new StringBuilder()
							.append("ID: ").append(header.getId())
							.append("\nisRead:").append(header.isRead())
							.append("\nisLocal: ").append(header.isLocal())
							.append("\nisSent: ").append(header.isSent())
							.append("\nisSeen: ").append(header.isSeen())
							.append("\nTimestamp: ").append(header.getTimestamp())
							.toString();

					chatBox.addItem(message, () ->
							MessageDialog.showMessageDialog(textGUI, "Message Metadata", metaData,
									MessageDialogButton.Close));
				} catch (DbException e)
				{
					e.printStackTrace();
				}
			}
		} catch (DbException e)
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

	@Override
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

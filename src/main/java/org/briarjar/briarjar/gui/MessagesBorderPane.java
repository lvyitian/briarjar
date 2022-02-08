package org.briarjar.briarjar.gui;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.TrayNotification;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.event.ContactAddedEvent;
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent;
import org.briarproject.bramble.api.contact.event.PendingContactAddedEvent;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.event.EventListener;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent;
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent;
import org.briarproject.bramble.api.sync.event.MessageAddedEvent;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

@Singleton
public class MessagesBorderPane extends BorderPane implements EventListener {

	private final ContactViewModel cvm;

	private TextArea messageBox;
	private MessageListView messageListView;
	private VBox contactList;
	private boolean isContactListVisible;
	private GUIUtils guiUtils;
	private Image briarLogo;

	private HashMap<ContactId, Boolean > onlineStatusHashMap;

	@Inject
	public MessagesBorderPane(ContactViewModel cvm,
	                          EventBus eventBus)
	{
		eventBus.addListener(this);
		this.cvm = cvm;
	}

	public void create()
	{
		initComponents();
		addComponents();
		addHandlers();
		showContactList();
	}


	private void initComponents()
	{
		onlineStatusHashMap = new HashMap<>();

		messageBox = new TextArea();
		messageBox.setPromptText("Type in your private message here... Press Enter to send");
		messageBox.setPrefHeight(40);
		messageBox.setMinHeight(40);
		messageBox.setDisable(true);

		contactList = new VBox();
		contactList.setPrefWidth(110);
		isContactListVisible = false;

		messageListView = guiUtils.getMessageListView();

		String obj = Objects.requireNonNull(
				getClass().getResource("/briar-icon.png")).toExternalForm();
		briarLogo = new Image(obj);
		// statusText = new Label("Select a contact to show status.");
	}


	private void addComponents()
	{
		setCenter(messageListView);
		setBottom(messageBox);
	}


	private void addHandlers()
	{
		messageBox.setOnKeyPressed(this::sendMessage);
	}

	private void sendMessage(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER)
		{
			messageListView.sendMessage(messageBox.getText());
			messageBox.clear();
		}
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
		updateContactList();
		setLeft(contactList);
		isContactListVisible = true;
	}

	public void hideContactList()
	{
		setLeft(null);
		isContactListVisible = false;
	}

	private void updateContactList()
	{
		try
		{
			contactList.getChildren().clear();
			Collection<Contact> contacts =	cvm.getAcceptedContacts();
			//.getContacts();

			for (Contact c : contacts)
			{
				Button b = new Button(c.getAlias());
				b.setPrefWidth(contactList.getPrefWidth());
				b.setTextFill(getColorsForList(c.getId()));
				b.setOnAction(e -> {
					messageListView.setContact(c);
					messageListView.initListView();
					messageBox.setDisable(false);
				});
				contactList.getChildren().add(b);
			}
		} catch (GeneralException e)
		{
			e.printStackTrace();
		}
	}

	private Color getColorsForList( ContactId id )
	{
		return onlineStatusHashMap
				.getOrDefault( id, false ) ? Color.LIMEGREEN : Color.DIMGREY;
	}

	private void notifyOnNewMessage(ContactId sender)
	{
		TrayNotification notification = new TrayNotification();
		String alias = "";
		try
		{
			alias = cvm.getContact(sender).getAlias();
		} catch (GeneralException ex)
		{
			ex.printStackTrace();
		}

		notification.setTitle("New message");
		notification.setMessage(alias + " sent you a private message.");
		notification.setImage(briarLogo);
		notification.setAnimation(Animations.FADE);
		notification.showAndDismiss(Duration.seconds(2));
	}

	public void setGUIUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}

	/**
	 * Events in the GUI are handled in the MessageBorderPane.
	 * Since we are only handling two types of Events:
	 * 1. Contact-related
	 * 2. Conversation-related
	 * and both of these types of Event can be easily handled from within the
	 * MessageBorderPane.
	 *
	 * In order to make event handling more cleanly, we'll have to rewrite
	 * the GUI, which we are not going to do due to lacking time resources.
	 */
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

		/* CONTACT MANAGEMENT RELATED EVENTS */

		if (e instanceof ContactAddedEvent)
		{
			System.out.println("ContactAddedEvent...");
			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof ContactRemovedEvent)
		{
			System.out.println("ContactRemovedEvent...");
			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof PendingContactAddedEvent)
		{
			System.out.println("PendingContactAddedEvent...");
			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof ContactConnectedEvent)
		{
			System.out.println("ContactConnectedEvent...");
			onlineStatusHashMap.put(
					((ContactConnectedEvent) e).getContactId(), true );

			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof ContactDisconnectedEvent)
		{
			System.out.println("ContactDisconnectedEvent...");
			onlineStatusHashMap.put(
					((ContactDisconnectedEvent) e).getContactId(), false );
			Platform.runLater(
					this::updateContactList
			);
		}

		/* CONVERSATION RELATED EVENTS */

		if (e instanceof PrivateMessageReceivedEvent)
		{
			System.out.println("PrivateMessageReceivedEvent...");
			Platform.runLater(() -> {
				notifyOnNewMessage(((PrivateMessageReceivedEvent) e).getContactId());
				messageListView.updateOnMessageReceived(((PrivateMessageReceivedEvent) e).getMessageHeader());
			});
		} else if (e instanceof MessageAddedEvent)
		{
			System.out.println("MessageAddedEvent...");
			Platform.runLater(() -> messageListView.updateOnMessageAdded());
		}

		/*
		else if (e instanceof MessagesSentEvent)
		{
			System.out.println("MessagesSentEvent...");
			//Platform.runLater(() -> updateOnMessageSent(((MessagesSentEvent) e).getMessageIds()));
		} else if (e instanceof MessageStateChangedEvent)
		{
			System.out.println("MessageStateChangedEvent...");
			//Platform.runLater(() -> updateOnMessageStateChanged(((MessageStateChangedEvent) e).getMessageId());
		}
		 */
	}
}

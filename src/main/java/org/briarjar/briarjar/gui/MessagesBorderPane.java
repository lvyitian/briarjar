/*
 * BriarJar -- a GUI and TUI prototype for the messenger Briar.
 * Copyright (C) 2022 BriarJar Project Team
 *
 * This file is part of BriarJar.
 *
 * BriarJar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * BriarJar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BriarJar.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarjar.briarjar.gui;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.TrayNotification;
import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;

import org.briarjar.briarjar.GeneralException;
import org.briarjar.briarjar.viewmodel.ContactViewModel;
import org.briarproject.bramble.api.Pair;
import org.briarproject.bramble.api.contact.*;
import org.briarproject.bramble.api.contact.event.ContactAddedEvent;
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent;
import org.briarproject.bramble.api.contact.event.PendingContactAddedEvent;
import org.briarproject.bramble.api.contact.event.PendingContactStateChangedEvent;
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
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BoxBlur;
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

	private JFXTextArea messageBox;
	private MessageListView messageListView;
	private VBox contactList;
	private boolean isContactListVisible, isIncludingPendingContacts;
	private GUIUtils guiUtils;
	private Image briarLogo;
	private Label noContactsSelected;

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
		showContactList(true);
	}


	private void initComponents()
	{

		onlineStatusHashMap = new HashMap<>();

		noContactsSelected = new Label("No contact selected. " +
				"Click on a contact or add a new one from the Contact menu.");

		messageBox = new JFXTextArea();
		messageBox.setLabelFloat(true);
		messageBox.setPromptText("Type in your private message here... (Press enter to send)");
		messageBox.setTooltip(new Tooltip("Click on a user or add a new one from the Chat menu"));
		messageBox.setPrefHeight(50);
		messageBox.setMinHeight(50);
		messageBox.setFocusColor(Color.LIMEGREEN);
		messageBox.setDisable(true);

		contactList = new VBox();
		contactList.setPrefWidth(150);
		contactList.setMaxWidth(Double.MAX_VALUE);
		// contactList.setMinHeight(Region.USE_PREF_SIZE);

		isContactListVisible = true;        // default
		isIncludingPendingContacts = true;

		messageListView = guiUtils.getMessageListView();

		String obj = Objects.requireNonNull(
				getClass().getResource("/images/briar-icon.png")).toExternalForm();
		briarLogo = new Image(obj);
		// statusText = new Label("Select a contact to show status.");
		setPadding(new Insets(7));
	}


	private void addComponents()
	{
		setCenter(noContactsSelected);
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

	public boolean isIncludingPendingContacts() { return isIncludingPendingContacts; }

	// ============================ logic ============================

	/*
	 * This method will be used in the RootBorderPane.
	 */
	public void showContactList(boolean show)
	{
		if(show)
		{
			isContactListVisible = true;
			updateContactList();
			guiUtils.getRootBorderPane().setLeft(contactList);
		}
		else
		{
			isContactListVisible = false;
			guiUtils.getRootBorderPane().setLeft(null);
		}
	}


	public void includePendingContacts(boolean include)
	{
		if(include)
		{
			isIncludingPendingContacts = true;
			updateContactList();
		}
		else
		{
			isIncludingPendingContacts = false;
			updateContactList();
		}
	}


	private void updateContactList()
	{
		try
		{
			contactList.getChildren().clear();
			Collection<Contact> contacts =	cvm.getContacts();
			//.getContacts();

			for (Contact c : contacts)
			{
				String alias = c.getAlias(), author = c.getAuthor().getName();
				String fullButtonText = alias + " (" + author + ")";
				JFXButton b;

				if(alias != null)
					b = new JFXButton(fullButtonText);
				else
					b = new JFXButton(author);

				b.setMaxWidth(Double.MAX_VALUE);
				b.setMinWidth(Control.USE_PREF_SIZE);
				b.setTextFill(getColorsForList(c.getId()));
				b.setRipplerFill(getColorsForList(c.getId()));
				b.setOnAction(e -> {
					messageListView.setContact(c);
					messageListView.initListView();
					messageBox.setDisable(false);

					setBottom(messageBox);
					setCenter(messageListView);
				});

				contactList.getChildren().add(b);
			}

			// pending contacts
			if(isIncludingPendingContacts)
			{
				contactList.getChildren().add(new Separator());

				var pendingContacts = cvm.getPendingContacts();
				for(Pair<PendingContact, PendingContactState> pendingContact : pendingContacts)
				{
					String alias = pendingContact.getFirst().getAlias();
					String state = "";
					switch(pendingContact.getSecond())
					{
						case OFFLINE -> state = " (Offline)";
						case WAITING_FOR_CONNECTION -> state = " (Waiting for connection)";
						case CONNECTING -> state = " (Connecting)";
						case ADDING_CONTACT -> state = " (Adding contact)";
						case FAILED -> state = " (Failed)";
					}

					String fullButtonText = alias + state;
					JFXButton b = new JFXButton(fullButtonText);

					b.setMaxWidth(Double.MAX_VALUE);
					b.setMinWidth(Control.USE_PREF_SIZE);
					b.setTextFill(Color.LIGHTSLATEGREY);
					b.setRipplerFill(Color.ORANGERED);
					b.setTooltip(new JFXTooltip(state));
					b.setOnAction(e -> removePendingContactOffer(pendingContact
															.getFirst().getId()));
					contactList.getChildren().add(b);
				}
			}


		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	private Color getColorsForList( ContactId id )
	{
		return onlineStatusHashMap
				.getOrDefault( id, false ) ? Color.LIMEGREEN : Color.DIMGREY;
	}

	/* DIALOGS */

	/* REMOVE CONTACT */

	public void contactRemovalDialog()
	{
		if(messageListView != null && messageListView.getContact() != null)
		{
			JFXButton remove = new JFXButton("Remove contact");
			JFXDialog dialog = guiUtils.showConfirmationDialog(
					"Removing contact",
					"Are you sure you want to remove " +
							messageListView.getContact().getAlias() + "?",
					remove);
			remove.setOnAction(e -> {
				try
				{
					ContactId contactIdToRemove =
							messageListView.getContact().getId();
					messageListView.setContact(
							null); // prevent NoSuchContactException
					cvm.removeContact(contactIdToRemove);
					updateContactList();
					setCenter(noContactsSelected);
					setBottom(null);
					dialog.close();
				} catch (GeneralException ex)
				{
					guiUtils.showMaterialDialog(ex.getTitle(), ex.getMessage());
				}
			});

			dialog.show();
		} else
			guiUtils.showMaterialDialog("Removing contact", "Please select a contact from the contact list first.");

	}

	/* DELETE ALL MESSAGES */

	public void deleteAllMessagesDialog()
	{
		if(messageListView != null && messageListView.getContact() != null)
		{
			JFXButton wipe = new JFXButton("Wipe");
			JFXDialog dialog = guiUtils.showConfirmationDialog(
					"Wiping chat",
					"You are about to wipe your chat history with " +
							messageListView.getContact().getAlias() +
							". Are you sure?",
					wipe);
			wipe.setOnAction(e -> {
				messageListView.deleteAllMessages();
				messageListView.initListView(); // re-init
				dialog.close();
			});

			dialog.show();
		} else
			guiUtils.showMaterialDialog("Deleting all messages", "Please select a contact from the contact list first.");
	}

	/* CHANGE CONTACT ALIAS */

	public void changeContactAlias()
	{
		if(messageListView != null && messageListView.getContact() != null)
		{
			BoxBlur blur = new BoxBlur(3, 3, 3);
			JFXDialogLayout dialogLayout = new JFXDialogLayout();
			JFXDialog dialog =
					new JFXDialog(guiUtils.getRootStackPane(), dialogLayout,
							JFXDialog.DialogTransition.TOP);
			JFXButton change = new JFXButton("Change");
			JFXButton cancel = new JFXButton("Cancel");

			JFXTextField newAlias = new JFXTextField();
			String oldAlias = messageListView.getContact().getAlias();
			if(oldAlias == null)
				oldAlias = messageListView.getContact().getAuthor().getName();

			newAlias.setPromptText("Change alias of " + oldAlias + " here.");
			newAlias.setLabelFloat(true);

			change.setOnAction(e -> {
				try
				{
					cvm.setContactAlias(messageListView.getContact().getId(), newAlias.getText());
					updateContactList();
					messageListView.setContact(cvm.getContact(messageListView.getContact().getId())); // reset
					dialog.close();
				} catch (GeneralException ex)
				{
					guiUtils.showMaterialDialog(ex.getTitle(), ex.getMessage());
				}
			});

			cancel.setOnAction(e -> dialog.close());

			dialogLayout.setActions(change, cancel);
			dialogLayout.setHeading(new Label("Changing alias"));
			dialogLayout.setBody(newAlias);
			dialog.show();

			dialog.setOnDialogClosed(
					(JFXDialogEvent e) -> guiUtils.getRootBorderPane()
					                                   .setEffect(null));
			dialog.setOnKeyPressed(e -> {
				if(e.getCode().equals(KeyCode.ESCAPE))
					dialog.close();
			});
			guiUtils.getRootBorderPane().setEffect(blur);
		} else
			guiUtils.showMaterialDialog("Changing alias",
					"Please select whose alias you'd like to change.");
	}

	/* REMOVE PENDING CONTACT */

	public void removePendingContactOffer(PendingContactId id)
	{
		JFXButton remove = new JFXButton("Remove pending contact");
		JFXDialog dialog = guiUtils.showConfirmationDialog(
				"Removing pending contact",
				"Are you sure you want to remove this pending contact?",
				remove);
		remove.setOnAction(e -> {
			try
			{
				cvm.removePendingContact(id);
				messageListView.setContact(null);
				updateContactList();
				dialog.close();
			} catch (GeneralException ex)
			{
				guiUtils.showMaterialDialog(ex.getTitle(), ex.getMessage());
			}
		});

		dialog.show();
	}

	/* TRAY NOTIFICATIONS */

	private void notifyOnNewMessage(ContactId sender)
	{
		if( messageListView.getContact() == null ||
				!messageListView.getContact().getId().equals(sender) ||
				!MainGUI.getPrimaryStage().isFocused() )
		{
			TrayNotification notification = new TrayNotification();
			String alias = "";
			try
			{
				alias = cvm.getContact(sender).getAlias();
			} catch (GeneralException e)
			{
				guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
			}

			notification.setTitle("New message");
			notification.setMessage(alias + " sent you a private message.");
			notification.setImage(briarLogo);
			notification.setAnimation(Animations.FADE);
			notification.showAndDismiss(Duration.seconds(1.5));
		}
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
		/* CONTACT MANAGEMENT RELATED EVENTS */

		if (e instanceof ContactAddedEvent)
		{
			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof ContactRemovedEvent)
		{
			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof PendingContactAddedEvent)
		{
			Platform.runLater(
					this::updateContactList
			);
		}

		else if (e instanceof PendingContactStateChangedEvent)
		{
			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof ContactConnectedEvent)
		{
			onlineStatusHashMap.put(
					((ContactConnectedEvent) e).getContactId(), true );

			Platform.runLater(
					this::updateContactList
			);
		}
		else if (e instanceof ContactDisconnectedEvent)
		{
			onlineStatusHashMap.put(
					((ContactDisconnectedEvent) e).getContactId(), false );
			Platform.runLater(
					this::updateContactList
			);
		}

		/* CONVERSATION RELATED EVENTS */

		if (e instanceof PrivateMessageReceivedEvent)
		{
			Platform.runLater(() -> {
				var contactId = ((PrivateMessageReceivedEvent) e).getContactId();
				notifyOnNewMessage(contactId);
					if(messageListView != null &&
							messageListView.getContact() != null &&
							messageListView.getContact().getId().equals(contactId)
					)
						messageListView.updateOnMessageReceived(((PrivateMessageReceivedEvent) e).getMessageHeader());
					});
		} else if (e instanceof MessageAddedEvent)
		{
			if(messageListView != null && messageListView.getContact() != null)
				Platform.runLater(() -> messageListView.updateOnMessageAdded());
		}
	}
}

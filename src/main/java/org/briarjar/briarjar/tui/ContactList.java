package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarproject.bramble.api.Pair;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.PendingContact;
import org.briarproject.bramble.api.contact.PendingContactState;
import org.briarproject.bramble.api.contact.event.*;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent;
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent;

import java.util.HashMap;

import javax.inject.Inject;

public class ContactList extends EventListenerViewModel {

	private final ContactViewModel cvm;

	private Panel contentPanel, buttonPanel;
	private BasicWindow window;
	private WindowBasedTextGUI textGUI;
	private TUIUtils tuiUtils;

	private ActionListBox contactListBox;
	private HashMap< ContactId, Boolean > onlineStatusHashMap;
	private Label noContactsLabel;

	@Inject
	public ContactList( EventBus           eventBus,
	                    ContactViewModel   cvm )
	{
		super(eventBus);
		super.onInit();

		this.cvm = cvm;

		init();
	}

	/* INIT */

	private void init()
	{
		onlineStatusHashMap = new HashMap<>();
		noContactsLabel = new Label("No contacts yet.");

		contentPanel = new Panel(new BorderLayout());
		buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

		window = new BasicWindow("Contact Selection");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Choose your peer or add a new one"))
				.setLayoutData(BorderLayout.Location.CENTER));

	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();
		updateContactList();

		buttonPanel.addComponent(
				new Button("Add new", () ->
						tuiUtils.switchWindow(window, TUIWindow.ADDCONTACT)));

		buttonPanel.addComponent(

				new Button("Change alias", () -> {

						try
						{
							// FIXME this is a very quick solution, this needs to be better
							var contactId = cvm.getContacts().stream().toList()
							                   .get(contactListBox.getSelectedIndex()).getId();

							String oldAlias = cvm.getContact(contactId).getAlias();
							if(oldAlias == null)
								oldAlias = cvm.getContact(contactId).getAuthor().getName();

							String newAlias = TextInputDialog.showDialog(textGUI, "Changing alias",
									"Change alias of " + oldAlias + " here.", oldAlias);
							if(newAlias != null && !newAlias.equals(oldAlias))
							{
								cvm.setContactAlias(contactId, newAlias);
								updateContactList();
							}

						} catch (GeneralException e)
						{
							tuiUtils.show(e);
						}
				})
		);




		/*  FIXME Sign Out functionality is too buggy - For now, it's out of the scope of this prototype
		buttonPanel.addComponent(
				new Button("Sign out", () -> {
					// launches a new instance in a new thread
					var instance = new Thread(() -> {
						var briarJarApp = Main.launchApp();
						briarJarApp.getMainTUI().start();
					});
					instance.start();
					Thread.currentThread().interrupt(); // FIXME causes Tor Plugin Exception
				}));
		 */

		buttonPanel.addComponent( new Button("Exit", () -> System.exit(0)) );


		// contentPanel.addComponent(...)
		contentPanel.addComponent(buttonPanel.withBorder(Borders.singleLine()));
	}

	/* PANELS REMOVER */

	private void removeAllComponents()
	{
		buttonPanel.removeAllComponents();
		contentPanel.removeAllComponents();
	}

	/* RENDER */

	public void render()
	{
		createWindow();
		textGUI.addWindowAndWait(window);
	}

	/* UPDATE CLASS */

	private void updateContactList()
	{
		// prevent null pointer exception
		if(contactListBox != null)
			contentPanel.removeComponent(contactListBox);

		// fixes bug when switching windows
		contactListBox = new ActionListBox();

		try
		{
			if ( cvm.getContacts().size() > 0 )
			{
				for ( Contact c : cvm.getContacts() )
				{
					String  alias = c.getAlias(),
							author = c.getAuthor().getName();

					if(alias != null)
					{
						String fullButtonText = getAliasForList(c.getId()) +
												" (" + author + ")";
						contactListBox.addItem( fullButtonText,
								() -> {
									tuiUtils.getConversation().setContact( c );
									tuiUtils.switchWindow( window,
											TUIWindow.CONVERSATION );
								}
						);
					} else
					{
						contactListBox.addItem( author,
								() -> {
									tuiUtils.getConversation().setContact( c );
									tuiUtils.switchWindow( window,
											TUIWindow.CONVERSATION );
								}
						);
					}
				}
				contentPanel.addComponent(contactListBox.setLayoutData(BorderLayout.Location.CENTER));
			} else
				contentPanel.addComponent(noContactsLabel);


			/* PENDING CONTACTS */
			var pendingContacts = cvm.getPendingContacts();
			if(pendingContacts.size() > 0)
			{
				for (Pair<PendingContact, PendingContactState> pendingContact : pendingContacts)
				{
					String alias = pendingContact.getFirst().getAlias();
					String state = "";
					switch (pendingContact.getSecond())
					{
						case OFFLINE -> state = " [Offline]";
						case WAITING_FOR_CONNECTION -> state =
								" [Waiting for connection]";
						case CONNECTING -> state = " [Connecting]";
						case ADDING_CONTACT -> state = " [Adding contact]";
						case FAILED -> state = " [Failed]";
					}

					contactListBox.addItem("[Pending] " + alias + state,
							() -> {
								MessageDialogButton b =
										MessageDialog.showMessageDialog(textGUI,
												"Remove pending contact",
												"Are you sure you want to remove this pending contact?",
												MessageDialogButton.Yes,
												MessageDialogButton.Cancel);
								if (b == MessageDialogButton.Yes)
								{
									try
									{
										cvm.removePendingContact(
												pendingContact.getFirst()
												              .getId());
									} catch (GeneralException e)
									{
										tuiUtils.show(e);
									}
								}
						}
					);
				}
			}

		}
		catch (GeneralException e)
		{
			tuiUtils.show(e);
		}
	}

	/* LOGIC */

	private String getAliasForList( ContactId id )
	{
		String alias = "Internal Error: ContactID not found";
		String status = onlineStatusHashMap
		                       .getOrDefault( id, false ) ? "[on ] " : "[off] ";
		try
		{
			alias = cvm.getContact( id ).getAlias();
		}
		catch (GeneralException e)
		{
			tuiUtils.show(e);
		}

		return status+alias;
	}

	/* SETTERS */

	public void setTextGUI(MultiWindowTextGUI textGUI)
	{
		this.textGUI = textGUI;
	}

	public void setTuiUtils(TUIUtils tuiUtils)
	{
		this.tuiUtils = tuiUtils;
	}

	/* EVENT HANDLING */

	@Override
	@NotNullByDefault
	public void
	eventOccurred( Event e )
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
		if (e instanceof ContactAddedEvent)
		{
			System.out.println("I: ContactAddedEvent...");
			updateContactList();
		}
		else if (e instanceof ContactRemovedEvent)
		{
			System.out.println("I: ContactRemovedEvent...");
			updateContactList();
		}
		else if (e instanceof PendingContactAddedEvent)
		{
			System.out.println("I: PendingContactAddedEvent...");
			updateContactList();
		}
		else if (e instanceof ContactConnectedEvent)
		{
			System.out.println("I: ContactConnectedEvent...");

			onlineStatusHashMap.put(
			              ((ContactConnectedEvent) e).getContactId(), true );

			updateContactList();
		}
		else if (e instanceof ContactDisconnectedEvent)
		{
			System.out.println("I: ContactDisconnectedEvent...");

			onlineStatusHashMap.put(
			             ((ContactDisconnectedEvent) e).getContactId(), false );
			updateContactList();
		}
	}
}
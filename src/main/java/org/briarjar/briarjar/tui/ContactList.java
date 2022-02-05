package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;

import org.briarjar.briarjar.Main;
import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.event.*;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent;
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.inject.Inject;

public class ContactList extends EventListenerViewModel {

	private final ContactViewModel cvm;
	private final LifeCycleViewModel lifeCycleViewModel;

	private Panel contentPanel, buttonPanel;
	private BasicWindow window;
	private WindowBasedTextGUI textGUI;
	private TUIUtils tuiUtils;

	private ActionListBox contactListBox;
	private HashMap< ContactId, Boolean > onlineStatusHashMap;
	private Label noContactsLabel;

	private ArrayList<ListedContact> listedContactList;

	@Inject
	public ContactList( EventBus           eventBus,
	                    ContactViewModel   cvm,
                        LifeCycleViewModel lifeCycleViewModel )
	{
		super(eventBus);
		super.onInit();

		this.cvm = cvm;
		this.lifeCycleViewModel = lifeCycleViewModel;

		init();
	}

	private void init()
	{
		contactListBox = new ActionListBox();
		onlineStatusHashMap = new HashMap<>();
		noContactsLabel = new Label("No Contacts yet.");
	}

	private void createWindow() {
		updateContactList();

		buttonPanel.addComponent(
				new Button("Add...", () ->
						tuiUtils.switchWindow(window, TUIWindow.ADDCONTACT)));

		buttonPanel.addComponent(
				new Button("Exit", () -> {
					try
					{
						lifeCycleViewModel.stop();
					} catch (GeneralException e)
					{
						e.printStackTrace();
					}

					try
					{
						window.getTextGUI().getScreen().close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}

					// relaunch app TODO is there a better solution?
					var briarJarApp = Main.launchApp();
					briarJarApp.getMainTUI().start();
				}));

	}

	public void render()
	{
		contentPanel = new Panel(new BorderLayout());
		buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

		// init instance
		createWindow();

		contentPanel.addComponent(contactListBox.setLayoutData(BorderLayout.Location.CENTER));
		contentPanel.addComponent(buttonPanel.withBorder(Borders.singleLine()).setLayoutData(BorderLayout.Location.BOTTOM));

		this.window = new BasicWindow("Contact Selection");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Choose your peer or Add a new one")));
		// render the window
		textGUI.addWindowAndWait(window);
	}

	private void updateContactList0()
	{
		if(contactListBox != null)
			contentPanel.removeComponent(contactListBox);

		contactListBox = new ActionListBox();


		try
		{
			Collection<Contact> contactsCollection = cvm.getAcceptedContacts();
			listedContactList = new ArrayList<>(contactsCollection.size());

			if ( contactsCollection.size() > 0 )
			{
				for (Contact c : contactsCollection) {
					ListedContact lc = new ListedContact(c); // Contact --> ListedContact
					listedContactList.add(lc);
					contactListBox.addItem(lc.toString(), () -> {
					//contactListBox.addItem(prepareContactForList(lc), () -> {
						try
						{
							tuiUtils.getConversation().setContact(lc.getContact());
							tuiUtils.switchWindow(window, TUIWindow.CONVERSATION);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					});
				}
				contentPanel.addComponent(contactListBox);
			}
			else
				contentPanel.addComponent(new Label("No Contacts found!"));

		} catch (DbException e)
		{
			e.printStackTrace();
		}
	}


	private void updateContactList()
	{
		contactListBox.clearItems();
		contentPanel.removeComponent( contactListBox );
		contentPanel.removeComponent(noContactsLabel);

		try
		{
			if ( cvm.getAcceptedContacts().size() > 0 )   // TODO accepted only currently
			{
				for ( Contact c : cvm.getAcceptedContacts() )
				{
					contactListBox.addItem( getAliasForList(c.getId()),
					                        () -> {
						        /* TODO this block must be adapted (buggy when
						            switching back from messages) */
								tuiUtils.getConversation().setContact( c );
								tuiUtils.switchWindow( window,
										               TUIWindow.CONVERSATION );
							}
					);
				}
				contentPanel.addComponent( contactListBox );
			} else
				contentPanel.addComponent(noContactsLabel);
		}
		catch (DbException e)
		{
			e.printStackTrace();
		}
}


	private String getAliasForList( ContactId id )
	{
		String alias = "Internal Error: ContactID not found";
		String status = onlineStatusHashMap
		                       .getOrDefault( id, false ) ? "[on ] " : "[off] ";
		try
		{
			alias = cvm.getContact( id ).getAlias();
		}
		catch (DbException e)
		{
			e.printStackTrace(); // TODO
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
			System.out.println("ContactAddedEvent...");
			updateContactList();
		}
		else if (e instanceof ContactRemovedEvent)
		{
			System.out.println("ContactRemovedEvent...");
			updateContactList();
		}
		else if (e instanceof PendingContactAddedEvent)
		{
			System.out.println("PendingContactAddedEvent...");
			updateContactList();
		}
		else if (e instanceof ContactConnectedEvent)
		{
			System.out.println("ContactConnectedEvent...");

			onlineStatusHashMap.put(
			              ((ContactConnectedEvent) e).getContactId(), true );

			updateContactList();
		}
		else if (e instanceof ContactDisconnectedEvent)
		{
			System.out.println("ContactDisconnectedEvent...");

			onlineStatusHashMap.put(
			             ((ContactDisconnectedEvent) e).getContactId(), false );
			updateContactList();
		}
	}
}


/**
	A "Wrapper" for a Contact with two properties
	1. Only containing needed information for the UI
	2. A toString() which returns the contact alias
 */
class ListedContact
{
	private Contact contact;
	public ListedContact(Contact contact)
	{
		this.contact = contact;
	}

	@Override
	public String toString()
	{
		return contact.getAlias();
	}

	public Contact getContact()
	{
		return contact;
	}
}

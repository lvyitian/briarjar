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
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent;
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent;

import java.io.IOException;
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

	/* INIT */

	private void init()
	{
		onlineStatusHashMap = new HashMap<>();
		noContactsLabel = new Label("No Contacts yet.");

		contentPanel = new Panel(new BorderLayout());
		buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

		window = new BasicWindow("Contact Selection");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Choose your peer or Add a new one")));

	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();
		updateContactList();

		buttonPanel.addComponent(
				new Button("Add...", () ->
						tuiUtils.switchWindow(window, TUIWindow.ADDCONTACT)));

		buttonPanel.addComponent(
				new Button("Sign Out", () -> {
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

		buttonPanel.addComponent( new Button("Exit", () -> System.exit(0)) );


		// contentPanel.addComponent(...)
		contentPanel.addComponent(buttonPanel.withBorder(Borders.singleLine()).setLayoutData(BorderLayout.Location.BOTTOM));
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
			if ( cvm.getAcceptedContacts().size() > 0 )   // TODO accepted only currently
			{
				for ( Contact c : cvm.getAcceptedContacts() )
				{
					contactListBox.addItem( getAliasForList(c.getId()),
					                        () -> {
								tuiUtils.getConversation().setContact( c );
								tuiUtils.switchWindow( window,
										               TUIWindow.CONVERSATION );
							}
					);
				}
				contentPanel.addComponent(contactListBox.setLayoutData(BorderLayout.Location.CENTER));
			} else
				contentPanel.addComponent(noContactsLabel);
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
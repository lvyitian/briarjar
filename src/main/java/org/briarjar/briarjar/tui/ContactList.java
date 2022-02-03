package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;

import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.event.*;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;

import javax.inject.Inject;

public class ContactList extends EventListenerViewModel {

	private final ContactViewModel cvm;
	private final LifeCycleViewModel lifeCycleViewModel;

	private Panel contentPanel;
	private BasicWindow window;
	private WindowBasedTextGUI textGUI;
	private TUIUtils tuiUtils;

	private ComboBox<String> contactAliasComboBox;

	@Inject
	public ContactList( EventBus           eventBus,
	                    ContactViewModel   cvm,
                        LifeCycleViewModel lifeCycleViewModel )
	{
		super(eventBus);
		super.onInit();

		this.cvm = cvm;
		this.lifeCycleViewModel = lifeCycleViewModel;
	}

	private void createWindow() {
		TUIUtils.addTitle("Select or Add a Friend", contentPanel);

		contentPanel.addComponent(
				new Button("Add a new Contact", () ->
						tuiUtils.switchWindow(window, TUIWindow.ADDCONTACT)));

		TUIUtils.addHorizontalSeparator(contentPanel);

		try {
			if(cvm.getAcceptedContacts().size() != 0) {
				for (Contact c : cvm.getAcceptedContacts()
						) {
					contactAliasComboBox.addItem(c.getAlias());
				}
				contentPanel.addComponent(contactAliasComboBox);
			}
			else
				contentPanel.addComponent(new Label("No Contacts found!"));
		} catch (DbException e) {
			e.printStackTrace();
		}

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Sign Out", () -> {
					try
					{
						lifeCycleViewModel.stop();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace(); //TODO
					}
					tuiUtils.switchWindow(window, TUIWindow.SIGNIN);
				}));
	}

	public void render()
	{
		this.contactAliasComboBox = new ComboBox<>();
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();

		this.window = new BasicWindow("Select or Add a Contact");
		window.setComponent(contentPanel);
		// render the window
		textGUI.addWindowAndWait(window);
	}

	public void setTextGUI(MultiWindowTextGUI textGUI)
	{
		this.textGUI = textGUI;
	}

	public void setTuiUtils(TUIUtils tuiUtils)
	{
		this.tuiUtils = tuiUtils;
	}


	@Override
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
        }
		else if (e instanceof ContactRemovedEvent)
        {
            System.out.println("ContactRemovedEvent...");
        }
        else if (e instanceof PendingContactAddedEvent)
        {
            System.out.println("PendingContactAddedEvent...");
        }
	}
}

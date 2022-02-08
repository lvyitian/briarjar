package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.inject.Inject;

import static com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog;

public class SignIn extends EventListenerViewModel {

	private final LoginViewModel lvm;
	private final LifeCycleViewModel lifeCycleViewModel;

	private Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	@Inject
	public SignIn( EventBus           eventBus,
	               LoginViewModel     lvm,
	               LifeCycleViewModel lifeCycleViewModel )
	{
		super(eventBus);

		this.lvm = lvm;
		this.lifeCycleViewModel = lifeCycleViewModel;

		init();
	}

	/* INIT */

	private void init()
	{
		contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

		window = new BasicWindow("Welcome back to BriarJar TUI (development mode)");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Please Sign In with your Account")));
	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();

		enterPassphrase();

		/* TODO uncomment after delete logic is implemented
		contentPanel.addComponent(
				new Button("Delete Account", () -> {
					lvm.deleteAccount();
					tuiUtils.switchWindow(window, TUIWindow.SIGNUP);
				})
		);
		 */
	}

	/* PANELS REMOVER */

	private void removeAllComponents()
	{
		contentPanel.removeAllComponents();
	}

	/* RENDER */

	public void render()
	{
		createWindow();
		textGUI.addWindowAndWait(window);
	}

	/* PASSPHRASE DIALOG */

	private void enterPassphrase()
	{
		boolean repeatDialog = true;

		while(repeatDialog)
		{
			String passphrase = TextInputDialog.showPasswordDialog(textGUI,
					"Enter Passphrase",
					"Enter your Account Passphrase", "");

			if ( passphrase == null ) {
				System.out.println("STOPPING BriarJar TUI …");
				System.exit(0);
			}

			try {
				lvm.signIn( passphrase );
				lifeCycleViewModel.start();
				repeatDialog = false;
				tuiUtils.switchWindow( TUIWindow.CONTACTLIST );

			} catch ( GeneralException e )
			{
				showMessageDialog( textGUI,
						e.getTitle(),
						e.getMessage(),
						MessageDialogButton.OK );
			}
		}
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
	}
}
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

	private Button btnGoToPassphrase;
	private Button btnSignIn;

	private String passphrase;


	@Inject
	public SignIn( EventBus           eventBus,
	               LoginViewModel     lvm,
	               LifeCycleViewModel lifeCycleViewModel )
	{
		super(eventBus);

		this.lvm = lvm;
		this.lifeCycleViewModel = lifeCycleViewModel;
	}









	private void createWindow() {

		btnGoToPassphrase = new Button( "Enter Passphrase", () ->
			passphrase = TextInputDialog.showPasswordDialog(
			                                textGUI,
			                                "Enter Passphrase",
			                                "Enter your account passphrase", "")
		).addTo( contentPanel );


		TUIUtils.addHorizontalSeparator( contentPanel );


		btnSignIn = new Button( "Sign In", () ->
		{
			try {
				lvm.signIn( passphrase );
				lifeCycleViewModel.start();
				tuiUtils.switchWindow( window, TUIWindow.CONTACTLIST );

			} catch ( GeneralException e )
			{
				showMessageDialog( textGUI,
						           e.getTitle(),
						           e.getMessage(),
						           MessageDialogButton.OK );
			}
		}).addTo( contentPanel );

		/*
		contentPanel.addComponent(
				new Button("Delete Account", () -> {
					lvm.deleteAccount();
					tuiUtils.switchWindow(window, TUIWindow.SIGNUP);
				})
		);
		 */
	}

	public void render()
	{
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();

		this.window = new BasicWindow("Welcome back to BriarJar TUI (development mode)");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Please Sign In with your Account")));
		// render the window
		textGUI.addWindowAndWait(window);
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
	}
}

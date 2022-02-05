package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState;

import javax.inject.Inject;

public class SignUp extends EventListenerViewModel {

	private final EventBus eventBus;
	private final LoginViewModel lvm;
	private final LifeCycleViewModel lifeCycleViewModel;

	private Panel contentPanel;
	private TUIUtils tuiUtils;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private String username;
	private String passphrase;

	@Inject
	public SignUp ( EventBus           eventBus,
			        LoginViewModel     lvm,
			        LifeCycleViewModel lifeCycleViewModel )
	{
		super(eventBus);
		super.onInit();

		this.eventBus = eventBus;
		this.lvm = lvm;
		this.lifeCycleViewModel = lifeCycleViewModel;
	}

	private void createWindow() {

		contentPanel.addComponent(
				new Button("Enter Username", () ->
						username = TextInputDialog.showDialog(textGUI, "Choose a Username", "No account has been found, please choose a username.", "alice")
				));

		contentPanel.addComponent(
				new Button("Enter Passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Enter Passphrase", "Choose a strong passphrase, which will be used to decrypt your account", "")
		));

		// passphraseStrength = lvm.calcPassphraseStrength(passphrase);

		contentPanel.addComponent(
				new Button("Sign Up", () -> {
					if(username != null && !username.isEmpty())
					{
						if(passphrase != null && !passphrase.isEmpty())
						{
							try {
								lvm.signUp(username, passphrase);}
							catch (GeneralException e) {
								MessageDialog.showMessageDialog(textGUI, "InterruptedException occurred", e.getMessage(), MessageDialogButton.OK);
							}
							try {
								lifeCycleViewModel.start();
							} catch (GeneralException e) {
								MessageDialog.showMessageDialog(textGUI, "InterruptedException occurred", e.getMessage(), MessageDialogButton.OK);
							}
						} else
							MessageDialog.showMessageDialog(textGUI, "Empty Passphrase", "Please enter a valid passphrase.",
									MessageDialogButton.OK);
					} else
						MessageDialog.showMessageDialog(textGUI, "Empty Username", "Please choose a username.",
								MessageDialogButton.OK);


					if(lifeCycleViewModel.getLifeCycleState() == LifecycleState.RUNNING)
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
				}));
		TUIUtils.addHorizontalSeparator(contentPanel);
	}

	public void render()
	{
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();

		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Please Create an Account")));
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

		System.out.println("EvListConfigured@TUISignUp   ====> "+e.getClass().getSimpleName());
	}
}

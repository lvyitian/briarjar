package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.viewmodels.EventListenerViewModel;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState;

import javax.inject.Inject;

public class SignIn extends EventListenerViewModel {

	private final EventBus eventBus;
	private final LoginViewModel lvm;
	private final LifeCycleViewModel lifeCycleViewModel;

	private Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	private String passphrase;

	@Inject
	public SignIn( EventBus           eventBus,
	               LoginViewModel     lvm,
	               LifeCycleViewModel lifeCycleViewModel )
	{
		super(eventBus);
		this.eventBus = eventBus;

		this.lvm = lvm;
		this.lifeCycleViewModel = lifeCycleViewModel;
	}


	private void createWindow() {
		TUIUtils.addTitle("Please Sign In with your Account", contentPanel);
		contentPanel.addComponent(
				new Button("Enter Passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Enter Passphrase", "Enter your account passphrase", ""))
		);

		contentPanel.addComponent(
				new Button("Sign In", () -> {

					try {
						lvm.signIn(passphrase);

						System.out.println("pre start: " +lifeCycleViewModel.getLifeCycleState());
						lifeCycleViewModel.start();
						System.out.println("post start: " +lifeCycleViewModel.getLifeCycleState());
					} catch (DecryptionException e) {
						MessageDialog.showMessageDialog(textGUI, "DecryptionException occurred", e.getDecryptionResult().toString(), MessageDialogButton.OK);
					} catch (InterruptedException e) {
						MessageDialog.showMessageDialog(textGUI, "InterruptedException occurred", e.getMessage(), MessageDialogButton.OK);
					}
					if(lifeCycleViewModel.getLifeCycleState() == LifecycleState.RUNNING)
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
				}));

		TUIUtils.addHorizontalSeparator(contentPanel);

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
	}
}

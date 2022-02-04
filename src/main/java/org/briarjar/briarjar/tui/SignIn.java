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
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

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


		// contentPanel.addComponent(...)
		contentPanel.addComponent(
				new Button("Enter Passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Enter Passphrase", "Enter your account passphrase", ""))
		);
		contentPanel.addComponent(
				new Button("Sign In", () -> {

					if(passphrase != null && !passphrase.isEmpty())
					{
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
					} else
						MessageDialog.showMessageDialog(textGUI, "Empty Passphrase", "Please enter a passphrase",
								MessageDialogButton.OK);
					}));

		TUIUtils.addHorizontalSeparator(contentPanel);

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
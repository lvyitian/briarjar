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
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.inject.Inject;

import static com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog;

public class SignUp extends EventListenerViewModel {

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

		this.lvm = lvm;
		this.lifeCycleViewModel = lifeCycleViewModel;

		init();
	}

	/* INIT */

	private void init()
	{
		contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		window.setComponent(
				contentPanel.withBorder(Borders.singleLine("Please create an account")));
	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();

		// contentPanel.addComponent(...)
		contentPanel.addComponent(
				new Button("Choose username", () ->
						username = TextInputDialog.showDialog(textGUI, "Choose a username", "No account has been found, please choose a username.", "alice")
				));

		contentPanel.addComponent(
				new Button("Choose passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Choose passphrase", "Choose a strong passphrase, which will be used to decrypt your account.", "")
		));

		// passphraseStrength = lvm.calcPassphraseStrength(passphrase);

		contentPanel.addComponent(
				new Button("Sign up", () -> {
					if(username != null && !username.isEmpty())
					{
						if(passphrase != null && !passphrase.isEmpty())
						{
							try {
								lvm.signUp(username, passphrase);}
							catch (GeneralException e) {
								showMessageDialog(textGUI, "InterruptedException occurred", e.getMessage(), MessageDialogButton.OK);
							}
							try {
								lifeCycleViewModel.start();
							} catch (GeneralException e) {
								tuiUtils.show(e);
							}
						} else
							showMessageDialog(textGUI, "Empty passphrase", "Please enter a valid passphrase.",
									MessageDialogButton.OK);
					} else
						showMessageDialog(textGUI, "Empty username", "Please choose a username.",
								MessageDialogButton.OK);


					if(lifeCycleViewModel.getLifeCycleState() == LifecycleState.RUNNING)
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
				}));
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

		System.out.println("EvListConfigured@TUISignUp   ====> "+e.getClass().getSimpleName());
	}
}

package org.briarjar.briarjar.tui;

import org.briarjar.briarjar.BriarJarUi;
import org.briarjar.briarjar.model.LoginViewModel;
import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.messaging.MessagingManager;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Immutable
@Singleton
public class BriarJarTuiImpl implements BriarJarUi
{/*
	private RegistrationViewModel registrationViewModel;
	private LoginViewModel loginViewModel;
	private ContactsViewModel contactsViewModel;*/
	private LoginViewModel loginViewModel;
	private AccountManager accountManager;
	private ContactManager contactManager;
	private ConversationManager conversationManager;
	private IdentityManager identityManager;
	private MessagingManager messagingManager;
	private LifecycleManager lifecycleManager;

	@Inject
	public BriarJarTuiImpl(//RegistrationViewModel registrationViewModel,
						  //ContactsViewModel contactsViewModel,
			              LoginViewModel loginViewModel,
						  AccountManager accountManager,
						  ContactManager contactManager,
						  ConversationManager conversationManager,
						  IdentityManager identityManager,
						  MessagingManager messagingManager,
						  LifecycleManager lifecycleManager) {
		/*this.registrationViewModel = registrationViewModel;
		this.contactsViewModel = contactsViewModel;*/
		this.loginViewModel = loginViewModel;
		this.accountManager = accountManager;
		this.contactManager = contactManager;
		this.conversationManager = conversationManager;
		this.identityManager = identityManager;
		this.messagingManager = messagingManager;
		this.lifecycleManager = lifecycleManager;
	}

	@Override
	public void start()
	{
		MainTUI mainTUI = new MainTUI(loginViewModel);

		/*
		final MainGUI mainGUI = new MainGUI(loginViewModel, contactManager);
		mainGUI.init();

		Platform.startup(() -> {
			Stage stage = new Stage();
			mainGUI.start(stage);
		});
		 */

		/*  FIXME - TO BE REMOVED
			SecretKey dbKey = accountManager.getDatabaseKey();

		System.out.println("Start LifeServiceManager Services...");
		lifecycleManager.startServices(dbKey);
		try {
			System.out.println("Waiting for startup...");
			lifecycleManager.waitForStartup();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Startup wait done!");

		System.out.println("Let's try to find out your Handshake link...");
		try {
			System.out.println(contactManager.getHandshakeLink());
		} catch (DbException e) {
			e.getMessage();
		}
	 */

		Runtime.getRuntime().addShutdownHook(new Thread(Thread.currentThread()::stop));

	}

	@Override
	public void stop() {
		loginViewModel.stop();
	}
}

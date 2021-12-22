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
{/**
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
		/**this.registrationViewModel = registrationViewModel;
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
		MainTUI mainTUI = new MainTUI();
		mainTUI.start(loginViewModel);
		Runtime.getRuntime().addShutdownHook(new Thread(Thread.currentThread()::stop));
	}

	@Override
	public void stop() {
		loginViewModel.stop();
	}
}

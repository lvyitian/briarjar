package org.briarjar.briarjar.tui;

import org.briarjar.briarjar.BriarJarUi;
import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.db.DbException;
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
	private AccountManager accountManager;
	private ContactManager contactManager;
	private ConversationManager conversationManager;
	private IdentityManager identityManager;
	private MessagingManager messagingManager;
	private LifecycleManager lifecycleManager;

	@Inject
	public BriarJarTuiImpl(/*RegistrationViewModel registrationViewModel,
						  LoginViewModel loginViewModel,
						  ContactsViewModel contactsViewModel,*/
						  AccountManager accountManager,
						  ContactManager contactManager,
						  ConversationManager conversationManager,
						  IdentityManager identityManager,
						  MessagingManager messagingManager,
						  LifecycleManager lifecycleManager) {
		/*this.registrationViewModel = registrationViewModel;
		this.loginViewModel = loginViewModel;
		this.contactsViewModel = contactsViewModel;*/
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
		System.out.println("I'm starting BriarService.");

		if(!accountManager.accountExists())
			createAccount();
		// TODO login procedure

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

		Runtime.getRuntime().addShutdownHook(new Thread(Thread.currentThread()::stop));


		System.out.println("Let's try to find out your Handshake link...");
		try {
			System.out.println(contactManager.getHandshakeLink());
		} catch (DbException e) {
			e.getMessage();
		}
	}

	@Override
	public void stop() {
		System.out.println("Stopping Services....");
		lifecycleManager.stopServices();
		try {
			System.out.println("Waiting for Shutdown...");
			lifecycleManager.waitForShutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Shutdown wait done!");
	}

	private void createAccount()
	{
		String nickname, password;
		System.out.println("Creating account... w/ testname and testpass122345");

		// TODO passwordEstimator logic

		nickname = "testname";
		password = "testpass122345";

		accountManager.createAccount(nickname, password);
		System.out.println("Account created!");
	}
}

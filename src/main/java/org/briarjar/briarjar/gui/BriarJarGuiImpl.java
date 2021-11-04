package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.BriarJarUi;
import org.briarjar.briarjar.Main;
import org.briarjar.briarjar.model.LoginViewModel;
import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.messaging.MessagingManager;
/*
import org.briarjar.briarjar.contact.ContactsViewModel;
import org.briarjar.briarjar.login.Login;
import org.briarjar.briarjar.login.LoginViewModel;
import org.briarjar.briarjar.login.Registration;
import org.briarjar.briarjar.login.RegistrationViewModel;
import org.briarjar.briarjar.theme.BriarTheme;
import java.awt.Dimension;
import java.util.logging.Logger;
*/


import org.briarproject.bramble.api.crypto.SecretKey; // needed here??
import org.briarproject.bramble.api.db.DbException; // needed here??

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Immutable
@Singleton
public class BriarJarGuiImpl implements BriarJarUi
{
	private LoginViewModel loginViewModel;
	/*private LoginViewModel loginViewModel;
	private ContactsViewModel contactsViewModel;*/
	private AccountManager accountManager;
	private ContactManager contactManager;
	private ConversationManager conversationManager;
	private IdentityManager identityManager;
	private MessagingManager messagingManager;
	private LifecycleManager lifecycleManager;

	@Inject
	public BriarJarGuiImpl(
			/*
			LoginViewModel loginViewModel,
			ContactsViewModel contactsViewModel,*/
			LoginViewModel loginViewModel,
			AccountManager accountManager,
			ContactManager contactManager,
			ConversationManager conversationManager,
			IdentityManager identityManager,
			MessagingManager messagingManager,
			LifecycleManager lifecycleManager) {

		this.loginViewModel = loginViewModel;
		/*
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
		MainGUI.startGUI();

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
}
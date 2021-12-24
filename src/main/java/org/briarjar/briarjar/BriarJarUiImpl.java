package org.briarjar.briarjar;

import org.briarjar.briarjar.gui.MainGUI;
import org.briarjar.briarjar.model.LoginViewModel;
import org.briarjar.briarjar.model.UserInterface;
import org.briarjar.briarjar.model.ViewModelProvider;
import org.briarjar.briarjar.tui.MainTUI;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.application.Platform;
import javafx.stage.Stage;

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

@Immutable
@Singleton
public class BriarJarUiImpl implements BriarJarUi
{
	private ViewModelProvider viewModelProvider;
	private LoginViewModel loginViewModel;
	private UserInterface userInterface;

	@Inject
	public BriarJarUiImpl(
			ViewModelProvider viewModelProvider,
			UserInterface userInterface) {
		this.viewModelProvider = viewModelProvider;
		this.userInterface = userInterface;
	}

	@Override
	public void start()
	{
		if(userInterface == UserInterface.GRAPHICAL)
		{
			final MainGUI mainGUI = new MainGUI(viewModelProvider);
			mainGUI.init();

			Platform.startup(() -> {
				Stage stage = new Stage();
				mainGUI.start(stage);
			});
		}
		else
		{
			if(userInterface == UserInterface.TERMINAL)
			{
				MainTUI mainTUI = new MainTUI(viewModelProvider);
				mainTUI.start();
			}
		}

		/**  FIXME - TO BE REMOVED
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
package org.briarjar.briarjar;

import org.briarjar.briarjar.gui.MainGUI;
import org.briarjar.briarjar.model.utils.UserInterface;
import org.briarjar.briarjar.tui.MainTUI;
import org.briarproject.bramble.BrambleCoreEagerSingletons;
import org.briarproject.briar.BriarCoreEagerSingletons;

import java.util.Arrays;

import javafx.application.Platform;
import javafx.stage.Stage;

public class Main {

	public static void main(String[] args)
	{
		UserInterface ui;
		if ( Arrays.stream(args).anyMatch(s -> s.equals("--tui") ||
			                                        s.equals("tui")      ) )
				ui = UserInterface.TERMINAL;
			else
				ui = UserInterface.GRAPHICAL;

			// testing for terminal
			// ui = UserInterface.TERMINAL;

			var briarJarApp = launchApp();

			if ( ui.equals(UserInterface.TERMINAL) )
			{
				MainTUI mainTUI = briarJarApp.getMainTUI();
				mainTUI.start();
				System.out.println("STOPPING BriarJar TUI …");
				System.exit(0);

			} else
			{
				Platform.startup( () -> {
					MainGUI mainGUI = briarJarApp.getMainGUI();
					mainGUI.init();

					Stage stage = new Stage();
					mainGUI.start( stage );
				} );
			}
	}

	/**
	 *  Will be called on application start and re-login.
	 */
	public static BriarJarApp launchApp()
	{
		var briarJarApp = DaggerBriarJarApp.builder().
		                                   briarJarModule(new BriarJarModule()).build();
		BrambleCoreEagerSingletons.Helper.injectEagerSingletons(briarJarApp);
		BriarCoreEagerSingletons.Helper.injectEagerSingletons(briarJarApp);
		return briarJarApp;
	}
}

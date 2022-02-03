package org.briarjar.briarjar;

import org.briarjar.briarjar.gui.MainGUI;
import org.briarjar.briarjar.model.utils.UserInterface;
import org.briarjar.briarjar.tui.MainTUI;
import org.briarproject.bramble.BrambleCoreEagerSingletons;
import org.briarproject.briar.BriarCoreEagerSingletons;

import java.io.File;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.stage.Stage;

public class Main {

	private static UserInterface ui;

	private static MainGUI mainGUI;
	private static MainTUI mainTUI;

	public static void main(String[] args)
	{
		if (Arrays.stream(args).anyMatch(s -> s.equals("--tui") || s.equals("tui")))
			ui = UserInterface.TERMINAL;
		else
			ui = UserInterface.GRAPHICAL;
		// testing
		//ui = UserInterface.GRAPHICAL;
		ui = UserInterface.TERMINAL;

		var briarJarApp = launchApp();

		if (ui.equals(UserInterface.GRAPHICAL))
		{
			Platform.startup(() -> {
				mainGUI = briarJarApp.getMainGUI();
				mainGUI.init();

				Stage stage = new Stage();
				mainGUI.start(stage);
			});
		} else if (ui.equals(UserInterface.TERMINAL))
		{
			mainTUI = briarJarApp.getMainTUI();
			mainTUI.start();
		}

		// todo 4k: stop is deprecated
		Runtime.getRuntime()
		       .addShutdownHook(new Thread(Thread.currentThread()::stop));
	}

	public static BriarJarApp launchApp()
	{
		var briarJarApp = DaggerBriarJarApp.builder().
		                                   briarJarModule(new BriarJarModule()).build();
		BrambleCoreEagerSingletons.Helper.injectEagerSingletons(briarJarApp);
		BriarCoreEagerSingletons.Helper.injectEagerSingletons(briarJarApp);
		return briarJarApp;
	}

	public static File getDataDir()
	{//todo
		return new File(System.getProperty("user.home") + "/.briar");
	}

}

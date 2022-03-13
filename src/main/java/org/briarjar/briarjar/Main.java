package org.briarjar.briarjar;

import org.briarjar.briarjar.gui.MainGUI;
import org.briarjar.briarjar.model.utils.UserInterface;
import org.briarjar.briarjar.tui.MainTUI;
import org.briarproject.bramble.BrambleCoreEagerSingletons;
import org.briarproject.briar.BriarCoreEagerSingletons;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javafx.application.Platform;
import javafx.stage.Stage;

public class Main {

	public static void main(String[] args)
	{
		// Disable Logs by default (keep UnixTerminal clean)
		Level level = Level.OFF;
		UserInterface ui;
		if ( Arrays.stream(args).anyMatch(s -> s.equals("--tui") || s.equals("-t")))
				ui = UserInterface.TERMINAL;
			else
				ui = UserInterface.GRAPHICAL;


		if ( Arrays.stream(args).anyMatch(s -> s.equals("--help") || s.equals("-h")))
			printHelp();

		if ( Arrays.stream(args).anyMatch(s -> s.equals("--verbose") || s.equals("-v")))
			level = Level.ALL;

			LogManager.getLogManager().getLogger("").setLevel(level);

			// testing for terminal
			// ui = UserInterface.TERMINAL;

			var briarJarApp = launchApp();

			if ( ui.equals(UserInterface.TERMINAL) )
			{
				MainTUI mainTUI = briarJarApp.getMainTUI();
				mainTUI.start();
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

	private static void printHelp()
	{
		System.out.println("""
                           BriarJar version 1.00 Copyright (C) 2022 BriarJar Project Team
                           
                           Options:
                           -t, --tui        Start in TUI mode (default is GUI)
                           
                           -h, --help       Show (this) help menu
                           -v, --verbose    Show all logs (floods the TUI, discouraged)
                           """);
		System.exit(0);
	}
}

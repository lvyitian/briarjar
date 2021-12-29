package org.briarjar.briarjar;

//import org.briarproject.bramble.BrambleCoreEagerSingletons;
//import org.briarproject.briar.BriarCoreEagerSingletons;

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

		if (Arrays.stream(args)
		          .anyMatch(s -> s.equals("--tui") || s.equals("tui")))
			ui = UserInterface.TERMINAL;
		else
			ui = UserInterface.GRAPHICAL;


		// testing
		//ui = UserInterface.GRAPHICAL;
		ui = UserInterface.TERMINAL;

		if (ui.equals(UserInterface.GRAPHICAL))
		{
			Platform.startup(() -> {
				mainGUI = DaggerBriarJarApp.builder().build().getMainGUI();
				mainGUI.init();

				Stage stage = new Stage();
				mainGUI.start(stage);
			});
		} else if (ui.equals(UserInterface.TERMINAL))
		{
			mainTUI = DaggerBriarJarApp.builder().build().getMainTUI();
			mainTUI.start();
		}

		// legacy/reminder
		/* Maybe not needed in this form currently, since it's for testing?
		BrambleCoreEagerSingletons.Helper.injectEagerSingletons(
				(BrambleCoreEagerSingletons) mainTUI);
		BriarCoreEagerSingletons.Helper.injectEagerSingletons(
				(BriarCoreEagerSingletons) mainTUI);
		*/
		//System.out.println("Starting briarJarGuiApp.getBriarJarUi().start()");
		//briarJarApp.getBriarJarUi().start();

		// todo 4k: stop is deprecated
		Runtime.getRuntime()
		       .addShutdownHook(new Thread(Thread.currentThread()::stop));
	}

	public static File getDataDir()
	{//todo
		return new File(System.getProperty("user.home") + "/.briar");
	}

}

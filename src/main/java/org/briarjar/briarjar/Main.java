package org.briarjar.briarjar;

//import org.briarproject.bramble.BrambleCoreEagerSingletons;
//import org.briarproject.briar.BriarCoreEagerSingletons;

import org.briarjar.briarjar.model.UserInterface;

import java.io.File;
import java.util.Arrays;

public class Main
{

	public static void main(String[] args) {
		UserInterface ui;

		if (Arrays.stream(args).anyMatch(s -> s.equals("--tui") || s.equals("tui")))
			ui = UserInterface.TERMINAL;
		else
			ui = UserInterface.GRAPHICAL;


		// testing
		//ui = UserInterface.TERMINAL;
		var briarJarUiApp =
				DaggerBriarJarUiApp.builder().briarJarUiModule(
						new BriarJarUiModule(getDataDir(), ui)).build();

			/* Maybe not needed in this form currently, since it's for testing?
			BrambleCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);
			BriarCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);
			*/
			System.out.println("Starting briarJarGuiApp.getBriarJarUi().start()");
			briarJarUiApp.getBriarJarUi().start();
	}

	private static File getDataDir()
	{
		return new File(System.getProperty("user.home") + "/.briar");
	}

}

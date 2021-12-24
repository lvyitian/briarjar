package org.briarjar.briarjar;

//import org.briarproject.bramble.BrambleCoreEagerSingletons;
//import org.briarproject.briar.BriarCoreEagerSingletons;

import org.briarjar.briarjar.model.UserInterface;

import java.io.File;

public class Main
{
	private static UserInterface ui;

	public static void main(String[] args) {

		if (args == null)
			ui = UserInterface.GRAPHICAL;
		else
			if(args.length == 1 && args[0].equals("gui"))
				ui = UserInterface.GRAPHICAL;
			else
				if(args.length == 1 && args[0].equals("tui"))
					ui = UserInterface.TERMINAL;

		// testing
		ui = UserInterface.GRAPHICAL;
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

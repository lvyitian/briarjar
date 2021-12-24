package org.briarjar.briarjar;

//import org.briarproject.bramble.BrambleCoreEagerSingletons;
//import org.briarproject.briar.BriarCoreEagerSingletons;

import org.briarjar.briarjar.model.UserInterface;

import java.io.File;

public class Main
{

	private static String DEFAULT_DATA_DIR = System.getProperty("user.home") + "/.briar";
	private static boolean startGUI;  // true = GUI start - false = TUI starts
	private static UserInterface ui;

	public static void main(String[] args) {

		if (args == null)
			ui = ui.GRAPHICAL;
		else
			if(args.length == 1 && args[0].equals("gui"))
				ui = ui.GRAPHICAL;
			else
				if(args.length == 1 && args[0].equals("tui"))
					ui = ui.TERMINAL;


		// testing
		ui = ui.GRAPHICAL;
		var briarJarGuiApp =
				DaggerBriarJarUiApp.builder().briarJarUiModule(
						new BriarJarUiModule(getDataDir(), ui)).build();

			/* Maybe not needed in this form currently, since it's for testing?
			BrambleCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);
			BriarCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);
			*/
			System.out.println("Starting briarJarGuiApp.getBriarJarUi().start()");
			briarJarGuiApp.getBriarJarUi().start();
		/* FIXME - TO BE REMOVED
		// experiment with Dagger
		System.out.println("1");
		DependsComponent dependsComponent = DaggerDependsComponent.create();
		System.out.println("2");
		Depends depends = dependsComponent.getDepends();
		System.out.println("3");
		depends.method();
		*/

	}

	private static File getDataDir()
	{
		// TODO implement
		return new File(DEFAULT_DATA_DIR);
	}

}

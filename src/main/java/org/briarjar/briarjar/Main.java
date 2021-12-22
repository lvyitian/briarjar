package org.briarjar.briarjar;

//import org.briarproject.bramble.BrambleCoreEagerSingletons;
//import org.briarproject.briar.BriarCoreEagerSingletons;

import java.io.File;

public class Main
{

	private static String DEFAULT_DATA_DIR = System.getProperty("user.home") + "/.briar";
	private static boolean startGUI;  // true = GUI start - false = TUI starts

	public static void main(String[] args) {


		if (args == null)
			startGUI = true;
		else
			if(args.length == 1 && args[0].equals("gui"))
				startGUI = true;
			else
				if(args.length == 1 && args[0].equals("tui"))
					startGUI = false;


		// testing
		startGUI = false;
		if (startGUI) {
			var briarJarGuiApp =
					DaggerBriarJarGuiApp.builder().briarJarGuiModule(
							new BriarJarGuiModule(getDataDir())).build();

			/* Maybe not needed in this form currently, since it's for testing?
			BrambleCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);
			BriarCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);
			*/
			System.out.println("Starting briarJarGuiApp.getBriarJarUi().start()");
			briarJarGuiApp.getBriarJarUi().start();
		}
		else
		{
			var briarJarTuiApp =
					DaggerBriarJarTuiApp.builder().briarJarTuiModule(
							new BriarJarTuiModule(getDataDir())).build();
			System.out.println("Starting briarJarTuiApp.getBriarJarUi().start()");
			briarJarTuiApp.getBriarJarUi().start();
		}

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

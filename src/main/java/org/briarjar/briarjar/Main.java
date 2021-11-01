package org.briarjar.briarjar;

import org.briarproject.bramble.BrambleCoreEagerSingletons;
import org.briarproject.briar.BriarCoreEagerSingletons;

import java.io.File;

public class Main
{

	private static String DEFAULT_DATA_DIR = System.getProperty("user.home") + "/.briar";

	public static void main(String[] args) {
		//MainGUI.main(args);
		// runs GUI

		/*
		val dataDir = getDataDir()
		val app =
				DaggerBriarDesktopApp.builder().desktopModule( DesktopModule(dataDir) ).build()
		// We need to load the eager singletons directly after making the
		// dependency graphs
		BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
		BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

		app.getUI().startBriar();

		 */
/*
		// experiment with Dagger
		System.out.println("1");
		DependsComponent dependsComponent = DaggerDependsComponent.create();
		System.out.println("2");
		Depends depends = dependsComponent.getDepends();
		System.out.println("3");
		depends.method();
*/


		var dataDir = getDataDir();


		var briarJarGuiApp =
				DaggerBriarJarGuiApp.builder().briarJarGuiModule(new BriarJarGuiModule(dataDir)).build();

		BrambleCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);
		BriarCoreEagerSingletons.Helper.injectEagerSingletons(briarJarGuiApp);

		briarJarGuiApp.getBriarJarUi().start();
	}

	private static File getDataDir()
	{
		// TODO implement
		return new File(DEFAULT_DATA_DIR);
	}

}

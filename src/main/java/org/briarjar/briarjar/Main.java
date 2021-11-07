package org.briarjar.briarjar;

//import org.briarproject.bramble.BrambleCoreEagerSingletons;
//import org.briarproject.briar.BriarCoreEagerSingletons;

import java.io.File;

public class Main
{

	private static String DEFAULT_DATA_DIR = System.getProperty("user.home") + "/.briar";

	public static void main(String[] args) {

		var dataDir = getDataDir();

		var briarJarGuiApp =
				DaggerBriarJarGuiApp.builder().briarJarGuiModule(new BriarJarGuiModule(dataDir)).build();

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

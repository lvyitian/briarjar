package org.briarjar.briarjar;

import org.briarjar.briarjar.gui.MainGUI;
import org.briarjar.briarjar.tui.MainTUI;
import org.briarproject.bramble.BrambleCoreEagerSingletons;
import org.briarproject.bramble.BrambleCoreModule;
import org.briarproject.briar.BriarCoreEagerSingletons;
import org.briarproject.briar.BriarCoreModule;

import javax.inject.Singleton;

import dagger.Component;


/* "the injector" */
@Singleton
@Component(modules = {
		BrambleCoreModule.class,
		BriarCoreModule.class,
		BriarJarModule.class
})
public interface BriarJarApp extends BrambleCoreEagerSingletons,
                                     BriarCoreEagerSingletons
{

	/*
	 * Define which classes should be directly provided via
	 *  DaggerBriarJarApp.builder().build().get…()
	 */
	MainTUI getMainTUI();

	MainGUI getMainGUI();

}

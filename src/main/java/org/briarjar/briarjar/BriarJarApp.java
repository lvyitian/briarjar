package org.briarjar.briarjar;

import org.briarjar.briarjar.gui.MainGUI;
import org.briarjar.briarjar.tui.MainTUI;
import org.briarproject.bramble.BrambleCoreModule;
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
public interface BriarJarApp
		/* Maybe not needed in this form currently, since it's for testing?
		extends BrambleCoreEagerSingletons,
		BriarCoreEagerSingletons
		*/ {

	/*
	 * Define which classes should be directly provided via
	 *  DaggerBriarJarApp.builder().build().get…()
	 */
	MainTUI getMainTUI();

	MainGUI getMainGUI();

}

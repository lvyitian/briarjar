package org.briarjar.briarjar;

import dagger.Component;
import javax.inject.Singleton;

import org.briarproject.bramble.BrambleCoreModule;
import org.briarproject.briar.BriarCoreModule;

/* also called: the injector */

@Singleton
@Component(modules = {
		BrambleCoreModule.class,
		BriarCoreModule.class,
		BriarJarUiModule.class
})

public interface BriarJarUiApp
		/* Maybe not needed in this form currently, since it's for testing?
		extends BrambleCoreEagerSingletons,
		BriarCoreEagerSingletons
		*/
{
	BriarJarUi getBriarJarUi();
}

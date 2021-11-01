package org.briarjar.briarjar;

import dagger.Component;
import javax.inject.Singleton;

import org.briarproject.bramble.BrambleCoreEagerSingletons;
import org.briarproject.bramble.BrambleCoreModule;
import org.briarproject.briar.BriarCoreEagerSingletons;
import org.briarproject.briar.BriarCoreModule;

import java.security.SecureRandom;

/* also called: the injector */

@Singleton
@Component(modules = {
		BrambleCoreModule.class,
		BriarCoreModule.class,
		BriarJarGuiModule.class
})

public interface BriarJarGuiApp extends BrambleCoreEagerSingletons,
		BriarCoreEagerSingletons
{
	// TODO add a method to make UI call necessary like
	BriarJarUi getBriarJarUi();

	SecureRandom getSecureRandom();
}

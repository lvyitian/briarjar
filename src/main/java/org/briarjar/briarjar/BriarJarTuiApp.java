package org.briarjar.briarjar;

import dagger.Component;
import org.briarproject.bramble.BrambleCoreEagerSingletons;
import org.briarproject.bramble.BrambleCoreModule;
import org.briarproject.briar.BriarCoreEagerSingletons;
import org.briarproject.briar.BriarCoreModule;

import javax.inject.Singleton;
import java.security.SecureRandom;

/* also called: the injector */

@Singleton
@Component(modules = {
		BrambleCoreModule.class,
		BriarCoreModule.class,
		BriarJarTuiModule.class
})

public interface BriarJarTuiApp extends BrambleCoreEagerSingletons,
		BriarCoreEagerSingletons
{
	// TODO add a method to make UI call necessary like
	BriarJarUi getBriarJarUi();

	SecureRandom getSecureRandom();
}

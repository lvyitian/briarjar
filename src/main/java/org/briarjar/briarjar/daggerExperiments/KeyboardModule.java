package org.briarjar.briarjar.daggerExperiments;

import dagger.Module;
import dagger.Provides;


@Module
public abstract class KeyboardModule {

	@Provides
	public static Boolean provideNumPad() {
		return true;
	}

	@Provides
	public static String provideLayout() {
		return "en";
	}
}

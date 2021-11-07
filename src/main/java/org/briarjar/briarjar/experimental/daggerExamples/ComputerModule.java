package org.briarjar.briarjar.experimental.daggerExamples;
import javax.inject.Named;
import dagger.Module;
import dagger.Provides;

/**
 * A module ("dependency provider") is only necessary when
 * - an implementation class has to be bound (@Binds) to an interface
 * - a class ownership is not given (no possibility to add f.e. @Inject to its constructor)
 * - a class needs configuration during instantiation or changes are desired afterwards
 *
 * The methods should be preferable static and the class abstract.
 *
 * @Named() methods & parameters in constructors will show Dagger2 the
 * corresponding objects.
 */

@Module (includes = KeyboardModule.class)
public abstract class ComputerModule {

	@Provides
	@Named("CPU")
	public static String provideCpu() {
		return "amd";
	}

	@Provides
	@Named("HDD")
	public static String provideHdd() {
		return "hgst";
	}
}

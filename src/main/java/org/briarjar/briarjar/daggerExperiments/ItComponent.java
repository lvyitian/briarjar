package org.briarjar.briarjar.daggerExperiments;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * If one class or @Provides is @Singleton, the Component has to be it too.
 * But other classes/modules which are included in a component can still be
 * not @Singleton.
 * */
@Singleton
@Component (modules = ComputerModule.class)
public interface ItComponent {
	/**
	 * Specify in a @Component interface which dependencies resp. objects should
	 * be made available (built) in this place
	 */


	Computer getComputer();
	Keyboard getKeyboard();

	/**
	 * Offer an original (not modified) CPU through a build ItComponent.
	 *
	 * There would be also 2 other ways to get an original or modified one, both
	 * require a getCpu() in Computer:
	 *
	 * 1. Simply use computer1.getCpu() to get computer1's (maybe modified) CPU.
	 *
	 * 2. Call itc.getComputer().getCpu(), this will create NEW INSTANCES as long
	 *    as Computer and therefore also ItComponent are not annotated with
	 *    @Singleton. If they are @Singleton and those single created Computer
	 *    instance's CPU is modified, it will offer of course the same modified
	 *    CPU at every time it is called via itc.getComputer().getCpu()
	 */
	@Named("CPU") String getCpu();
	// ...

}

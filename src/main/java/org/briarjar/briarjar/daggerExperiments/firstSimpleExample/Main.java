package org.briarjar.briarjar.daggerExperiments.firstSimpleExample;

public class Main {

	public static void main(String[] args) {

		// This object is desired
		HasDependencies hasDependencies;

		// This component offers it
		ExampleOfDependenciesComponent exampleOfDependenciesComponent;


		// Creation of component (create() used for this simple example)
		exampleOfDependenciesComponent = DaggerExampleOfDependenciesComponent.create();

		hasDependencies = exampleOfDependenciesComponent.getHasDependencies();
		hasDependencies.method();

	}
}

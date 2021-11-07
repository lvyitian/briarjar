package org.briarjar.briarjar.experimental.daggerExamples.firstSimpleExample;

import dagger.Component;

/**
 * A @Component offers ready-to-use dependencies, it could also be used with modules.
 */
@Component
public interface ExampleOfDependenciesComponent {
    HasDependencies getHasDependencies();
}

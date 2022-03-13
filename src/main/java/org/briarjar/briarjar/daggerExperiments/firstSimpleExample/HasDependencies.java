package org.briarjar.briarjar.daggerExperiments.firstSimpleExample;

import javax.inject.Inject;

public class HasDependencies {

    SomeDependency someDependency;

    /**
     * @Inject is needed on both sides (here to specify WHERE injection is desired)
     */
    @Inject
    public HasDependencies(SomeDependency someDependency) {
        this.someDependency = someDependency;
    }

    public void method() {
        System.out.println("I'm the HasDependencies class and I got my " +
                "dependency injected via constructor (recommended way). " +
                "someDependency has a getMessage() which I have access to:\n" +
                someDependency.getMessage());
    }
}

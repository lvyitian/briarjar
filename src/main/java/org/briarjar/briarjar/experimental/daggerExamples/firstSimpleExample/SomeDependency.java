package org.briarjar.briarjar.experimental.daggerExamples.firstSimpleExample;

import javax.inject.Inject;

public class SomeDependency {

    /**
     * @Inject is needed on both sides (here to specify: THIS is desired to inject)
     */
    @Inject
    public SomeDependency() {
        System.out.println("I'm the SomeDependency class and I get currently instantiated.");
    }

    public String getMessage() {
        return "I'm the getMessage() from the already instantiated SomeDependency class.";
    }

}

package org.briarjar.briarjar.experimental;

import javax.inject.Inject;

public class DummyDependency {

    // @Inject needed on both sides, here for specifying that it's used to inject
    @Inject
    public DummyDependency() {
        System.out.println("I'm the DummyDependency and I get currently initialised :D");
    }

    public String getMessage() {
        return "I'm a dependency...";
    }

}

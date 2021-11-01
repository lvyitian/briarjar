package org.briarjar.briarjar.experimental;

import javax.inject.Inject;

public class Depends {

    DummyDependency dummyDependency;

    // @Inject needed on both sides, here for specifying where to inject
    @Inject
    public Depends(DummyDependency dummyDependency) {
        this.dummyDependency = dummyDependency;
    }

    public void method() {
        System.out.println("I'm the Depends.method() and I have access to a DummyDependency instance" +
                " it has a method getMessage(), here it is:\n"+dummyDependency.getMessage());
    }
}

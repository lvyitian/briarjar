package org.briarjar.briarjar.experimental;

import dagger.Component;

@Component
public interface DependsComponent {
    Depends getDepends();
}

package org.briarjar.briarjar.model.viewmodels;

import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.event.EventListener;


public abstract class EventListenerViewModel implements EventListener {

	EventBus eventBus;


	public EventListenerViewModel( EventBus eventBus )
	{
		this.eventBus = eventBus;
	}



	public void onInit()
	{
		eventBus.addListener( this );
	}

	/* currently, not implemented

	public void onCleared()
	{
		eventBus.removeListener(this);
	}*/
}

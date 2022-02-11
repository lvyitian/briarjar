package org.briarjar.briarjar.model.viewmodels;

import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.event.EventListener;


/**
 * Provides a modest API for event management.
 * <ul><li>
 * Intends to be used by a simple UI implementation
 * <li>
 * Related events extend the superclass {@link org.briarproject.bramble.api.event.Event}
 * </ul>
 * Implements an {@link org.briarproject.bramble.api.event.EventListener}.
 *<p>
 * //TODO @version 1.0, 2021-mm-dd hh:mm
 */
public abstract class EventListenerViewModel implements EventListener {

	EventBus eventBus;


	/**
	 * Constructs an EventListenerViewModel.
	 *
	 * @param eventBus  an {@link org.briarproject.bramble.api.event.EventBus} implementation
	 *
	 * @since 1.0
	 */
	public EventListenerViewModel( EventBus eventBus )
	{
		this.eventBus = eventBus;
	}


	/**
	 * Subscribe as a listener to be notified about events until
	 * {@link #onCleared()} is called
	 *
	 * @see org.briarproject.bramble.api.event.EventBus#addListener
	 *
	 * @since 1.0
	 */
	public void onInit()
	{
		eventBus.addListener( this );
	}


	/**
	 * Unsubscribe as a listener from an earlier {@link #onInit()} call
	 *
	 * @see org.briarproject.bramble.api.event.EventBus#removeListener
	 *
	 * @since 1.0
	 */
	public void onCleared()
	{
		eventBus.removeListener(this);
	}
}

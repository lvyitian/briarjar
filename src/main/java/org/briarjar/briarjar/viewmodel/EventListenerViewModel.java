/*
 * BriarJar -- a GUI and TUI prototype for the messenger Briar.
 * Copyright (C) 2022 BriarJar Project Team
 *
 * This file is part of BriarJar.
 *
 * BriarJar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * BriarJar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BriarJar.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarjar.briarjar.viewmodel;

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
 *
 * @since 1.00
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

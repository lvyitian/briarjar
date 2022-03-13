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


import org.briarjar.briarjar.GeneralException;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult;
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Provides a modest API for lifecycle management.
 * <ul><li>
 * Intends to be used by a simple UI implementation
 * <b>mainly via {@link LoginViewModel}</b>, less to be called directly.
 * <li>
 * Underlying exceptions are caught and wrapped into {@link GeneralException}s.
 * GeneralExceptions are constructed with additional information where
 * appropriate.
 * <li>
 * Adds a shutdown hook to gratefully stop Briar before exiting the application.
 * </ul>
 * Depends mainly on an implementation of {@link org.briarproject.bramble.api.lifecycle.LifecycleManager}.
 *
 * @since 1.00
 */
@Singleton
@NotNullByDefault
public class LifeCycleViewModel {

	private final LifecycleManager lifecycleManager;
	private @Nullable Thread shutdownThread;

	/**
	 * Constructs a LifeCycleViewModel.
	 *
	 * @param lifecycleManager  a {@link org.briarproject.bramble.api.lifecycle.LifecycleManager} implementation
	 *
	 * @since 1.0
	 */
	@Inject
	public LifeCycleViewModel( LifecycleManager lifecycleManager )
	{
		this.lifecycleManager = lifecycleManager;

		addShutdownThread();
	}






// public ======================================================================


	/**
	 * Starts Briar (all lifecycle services) after successfully sign-up /
	 * sign-in with the provided database key. This method <b>should not</b> be
	 * called by the programmer, instead it is prepared to be called by
	 * {@link LoginViewModel}'s {@link LoginViewModel#signUp signUp} /
	 * {@link LoginViewModel#signIn signIn} method.
	 *
	 * @param dbKey  the {@link org.briarproject.bramble.api.crypto.SecretKey},
	 *               can be null under
	 *               {@link org.briarproject.bramble.api.account.AccountManager#getDatabaseKey
	 *               certain conditions}
	 *
	 * @throws GeneralException if the {@code dbKey} is null or another start-up
	 *                          problem occurs
	 *
	 * @see org.briarproject.bramble.api.lifecycle.LifecycleManager#startServices(SecretKey)
	 * @see org.briarproject.bramble.api.lifecycle.LifecycleManager#waitForStartup
	 *
	 * @since 1.0
	 */
	public void
	       start( @Nullable SecretKey dbKey )
	throws GeneralException
	{
		// System.out.println( "STARTING LifecycleManager Services …" );

		String exTitle = "Attempting to start services";

		if ( dbKey == null )
			throw new GeneralException( "Can not start services since no " +
			                       "dbKey is provided at the moment", exTitle );


		/* Startup LifeCycle */
		StartResult result = lifecycleManager.startServices( dbKey );

		if ( result.equals(StartResult.CLOCK_ERROR) )
			throw new GeneralException( "Unreasonable system clock, please " +
			                            "set the correct time first", exTitle );


		/* Wait for finishing Lifecycle Startup */
		try {
			lifecycleManager.waitForStartup();
		}
		catch ( InterruptedException e ) {
			throw new GeneralException(
	                          "Services' startup got interrupted while waiting",
					          exTitle, e, true );
		}

		// System.out.println("STARTING LifecycleManager Services … done");
	}


	/**
	 * Stops Briar (all running lifecycle services) to exit the application
	 * gratefully. {@link LoginViewModel}'s {@link LoginViewModel#signUp signUp}
	 * / {@link LoginViewModel#signIn signIn} should be used to
	 * {@link #start(SecretKey) start} Briar again.
	 *
	 * @throws GeneralException  if an error occurs during stopping
	 *
	 * @see org.briarproject.bramble.api.lifecycle.LifecycleManager#stopServices
	 * @see org.briarproject.bramble.api.lifecycle.LifecycleManager#waitForShutdown
	 *
	 * @since 1.0
	 */
	public void
	       stop()
	throws GeneralException
	{
		try {
			if ( lifecycleManager.getLifecycleState()
			                     .isAfter(LifecycleState.STARTING) ) {
				// System.out.println( "STOPPING LifecycleManager Services …" );
				lifecycleManager.stopServices();
				lifecycleManager.waitForShutdown();
			  //System.out.println("STOPPING LifecycleManager Services … done");
			} /*else
				System.out.println( "LifecycleManager doesn't need to STOP " +
						"services since none are running" );
			  */
		}
		catch ( InterruptedException e ) {
			throw new GeneralException(
			                 "Services' shutdown got interrupted while waiting",
			                 "Attempting to stop services" );
		}
	}


	/**
	 * Calls {@link #stop()} and removes the shutdown hook on success to be
	 * prepared to delete the existing user account and exit the application
	 * gratefully immediately afterwards.
	 * <p>This method <b>should not</b> be  called by the programmer, instead it
	 * is prepared to be called by {@link LoginViewModel}'s
	 * {@link LoginViewModel#deleteAccount()} method.
	 *
	 * @throws GeneralException if a problem occurs during {@link #stop()}
	 *
	 * @since 1.0
	 */
	public void
	       stopForAccountDeletion()
	throws GeneralException
	{
		stop();
		Runtime.getRuntime().removeShutdownHook( shutdownThread );
	}





// private======================================================================

	/**
	 * Adds a shutdown thread to gracefully {@link #stop} Briar before the
	 * application gets exited. If an error occurs when stopping Briar, both the
	 * standard output and error output will be used to raise attention.
	 *
	 * @since 1.0
	 */
	private void addShutdownThread()
	{
		shutdownThread = new Thread(() -> {
			try {
				stop();
			}
			catch (GeneralException e) { /*
				System.out.println( "\n"+e+"The throwable and its backtrace " +
				     "will now get printed to the standard error stream\n" );*/
				e.printStackTrace();
			}
		});
		Runtime.getRuntime().addShutdownHook( shutdownThread );
	}
}

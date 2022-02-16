package org.briarjar.briarjar.model.viewmodels;


import org.briarjar.briarjar.model.exceptions.GeneralException;
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
 *<p>
 * //TODO @version 1.0, 2021-mm-dd hh:mm
 */
@Singleton
@NotNullByDefault
public class LifeCycleViewModel {

	private final LifecycleManager lifecycleManager;

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
		System.out.println( "STARTING LifecycleManager Services …" );

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

		System.out.println("STARTING LifecycleManager Services … done");
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
				System.out.println( "STOPPING LifecycleManager Services …" );
				lifecycleManager.stopServices();
				lifecycleManager.waitForShutdown();
				System.out.println("STOPPING LifecycleManager Services … done");
			} else
				System.out.println( "LifecycleManager doesn't need to STOP " +
				                    "services since none are running" );
		}
		catch ( InterruptedException e ) {
			throw new GeneralException(
			                 "Services' shutdown got interrupted while waiting",
			                 "Attempting to stop services" );
		}
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
		Thread shutdownThread = new Thread(() -> {
			try {
				stop();
			}
			catch (GeneralException e) {
				System.out.println( "\n"+e+"The throwable and its backtrace " +
				        "will now get printed to the standard error stream\n" );
				e.printStackTrace();
			}
		});
		Runtime.getRuntime().addShutdownHook( shutdownThread );
	}
}

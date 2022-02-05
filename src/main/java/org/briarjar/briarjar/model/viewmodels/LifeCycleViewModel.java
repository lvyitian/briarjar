package org.briarjar.briarjar.model.viewmodels;


import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult;
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;



@Singleton
@NotNullByDefault
public class LifeCycleViewModel {

	private final LifecycleManager lifecycleManager;
	private final AccountManager accountManager;

	@Inject
	public LifeCycleViewModel( LifecycleManager lifecycleManager,
	                           AccountManager   accountManager    )
	{
		this.lifecycleManager = lifecycleManager;
		this.accountManager   = accountManager;
	}






// public ======================================================================

	public LifecycleState getLifeCycleState()
	{
		return lifecycleManager.getLifecycleState();
	}


	public void
	       start()
	throws GeneralException
	{
		System.out.println( "Starting LifecycleManager Services..." );


		/* Get needed Database Key for LifeCycle Startup */
		SecretKey dbKey = accountManager.getDatabaseKey();

		if ( dbKey == null )
			throw new GeneralException( "Internal Error",
			                        "Can not start services since no dbKey "+
			                        "is provided at the moment."              );


		/* Startup LifeCycle */
		StartResult result = lifecycleManager.startServices( dbKey );

		if ( result.equals(StartResult.CLOCK_ERROR) )
			throw new GeneralException( "Unreasonable System Clock",
			                            "Please set the correct system clock" );


		/* Wait for finished LifeCycle Startup */
		try
		{
			lifecycleManager.waitForStartup();

		} catch ( InterruptedException e )
		{
			throw new GeneralException( "Process interrupted",
	                            "Services' startup interrupted while waiting" );
		}


		System.out.println("Starting LifecycleManager Services... done.");
	}


	public void
	       stop()
	throws GeneralException
	{
		System.out.println( "Stopping LifecycleManager Services..." );

		try
		{
			lifecycleManager.stopServices();
			lifecycleManager.waitForShutdown();

		} catch ( InterruptedException e )
		{
			throw new GeneralException( "Process interrupted",
					           "Services' shutdown interrupted while waiting" );
		}

		System.out.println( "Stopping LifecycleManager Services... done." );
	}






// private======================================================================

}

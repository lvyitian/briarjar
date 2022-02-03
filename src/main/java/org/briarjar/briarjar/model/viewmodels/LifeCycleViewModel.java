package org.briarjar.briarjar.model.viewmodels;


import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;
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
	throws InterruptedException
	{
		System.out.println("Starting LifecycleManager Services...");

		SecretKey dbKey = accountManager.getDatabaseKey();

		if ( dbKey == null )
			throw new AssertionError( "Can not start services since no dbKey "+
			                          "is provided at the moment." );

		lifecycleManager.startServices( dbKey );
		lifecycleManager.waitForStartup();

		System.out.println("Starting LifecycleManager Services... done.");
	}


	public void
	       stop()
	throws InterruptedException
	{
		System.out.println("Stopping LifecycleManager Services...");

		lifecycleManager.stopServices();
		lifecycleManager.waitForShutdown();

		System.out.println("Stopping LifecycleManager Services... done.");
	}






// private======================================================================

}

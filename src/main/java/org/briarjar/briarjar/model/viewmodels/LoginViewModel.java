package org.briarjar.briarjar.model.viewmodels;

import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/*
	This Class is for Login and Registration Logic.
 */
@Singleton
public class LoginViewModel {

	private final AccountManager accountManager;
	private final LifecycleManager lifecycleManager;
	private final PasswordStrengthEstimator passphraseStrengthEstimator;

	private String passphrase;

	@Inject
	public LoginViewModel(
			AccountManager accountManager,
			LifecycleManager lifecycleManager,
			PasswordStrengthEstimator passphraseStrengthEstimator
	)
	{
		this.accountManager = accountManager;
		this.lifecycleManager = lifecycleManager;
		this.passphraseStrengthEstimator = passphraseStrengthEstimator;
	}


	// ============================ setters ============================


	public void setPassphrase(String passphrase)
	{
		this.passphrase = passphrase;
	}


	// ============================ logic ============================


	public float getPassphraseStrength()
	{
		return passphraseStrengthEstimator.estimateStrength(passphrase);
	}

	public void signUp(String username, String passphrase)
			throws InterruptedException
	{
		accountManager.createAccount(username, passphrase);
	}

	public void signIn(String passphrase)
			throws DecryptionException
	{
		accountManager.signIn(passphrase);
	}

	public Boolean accountExists()
	{
		return accountManager.accountExists();
	}

	public void deleteAccount()
	{
		accountManager.deleteAccount();
	}

	//todo 4k maybe startBriar or similar?
	public void start()
			throws InterruptedException
	{
		SecretKey dbKey = accountManager.getDatabaseKey();
		assert dbKey != null;
		lifecycleManager.startServices(dbKey);
		lifecycleManager.waitForStartup();
	}

	public void stop()
	{
		System.out.println("Stopping LifecycleManager Services...");

		lifecycleManager.stopServices();
		try
		{
			lifecycleManager.waitForShutdown();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("Stopped LifecycleManager Services.");
	}

	public LifecycleManager.LifecycleState getLifeCycleState()
	{
		return lifecycleManager.getLifecycleState();
	}

	public Boolean hasDbKey()
	{
		return accountManager.hasDatabaseKey();
	}
}
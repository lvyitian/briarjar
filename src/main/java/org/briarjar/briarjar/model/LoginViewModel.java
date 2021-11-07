package org.briarjar.briarjar.model;

import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;

import javax.inject.Inject;

/*
	This Class is for Login and Registration Logic.
 */
public class LoginViewModel {
	private AccountManager accountManager;
	private LifecycleManager lifecycleManager;
	private PasswordStrengthEstimator passwordStrengthEstimator;

	private String username;
	private String password;

	@Inject
	public LoginViewModel(
			AccountManager accountManager,
			LifecycleManager lifecycleManager,
			PasswordStrengthEstimator passwordStrengthEstimator
	)
	{
		this.accountManager = accountManager;
		this.lifecycleManager = lifecycleManager;
		this.passwordStrengthEstimator = passwordStrengthEstimator;
	}


	// ============================ setters ============================


	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	// ============================ logic ============================


	public float getPasswordStrength()
	{
		return passwordStrengthEstimator.estimateStrength(password);
	}

	public void register()
	{
		if(!accountManager.accountExists())
		{
			accountManager.createAccount(username, password);
		}
			// TODO exception handling 'account exist' or ask for acc. deletion?
	}

	public void signIn()
	{
		try {
			accountManager.signIn(password);
		} catch (DecryptionException e) {
			// TODO exception handling
		}
	}

	public Boolean isRegistered()
	{
		return accountManager.accountExists();
	}

	public void deleteAccount()
	{
		try {
			accountManager.deleteAccount();
		} catch (Exception e)
		{
			// TODO exception handling
		}

	}

	public void start()
	{
		SecretKey dbKey = accountManager.getDatabaseKey();
		lifecycleManager.startServices(dbKey);
		try {
			lifecycleManager.waitForStartup();
		} catch (InterruptedException e) {
			// TODO exception handling
		}
	}

	public void stop() {
		System.out.println("Stopping LifecycleManager Services...");

		lifecycleManager.stopServices();
		try {
			lifecycleManager.waitForShutdown();
		} catch (InterruptedException e) {
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
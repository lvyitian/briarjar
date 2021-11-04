package org.briarjar.briarjar.model;

import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;

import javax.inject.Inject;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

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
		SecretKey dbKey = accountManager.getDatabaseKey();
		lifecycleManager.startServices(dbKey);
		try {
			lifecycleManager.waitForStartup();
		} catch (InterruptedException e) {
			// TODO exception handling
		}
	}
}

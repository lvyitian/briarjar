package org.briarjar.briarjar.model.viewmodels;

import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;

import javax.inject.Inject;
import javax.inject.Singleton;

/*
	This Class is for Login and Registration Logic.
 */
@Singleton
public class LoginViewModel {

	private final AccountManager accountManager;
	private final PasswordStrengthEstimator pwStrengthEstimator;

	private String passphrase;

	@Inject
	public LoginViewModel( AccountManager            accountManager,
	                       PasswordStrengthEstimator pwStrengthEstimator )
	{
		this.accountManager = accountManager;
		this.pwStrengthEstimator = pwStrengthEstimator;
	}




	// ============================ setters ============================

	public void
	       setPassphrase( String passphrase )
	{
		this.passphrase = passphrase;
	}




	// ============================ logic ==============================

	public Boolean
	       accountExists()
	{
		return accountManager.accountExists();
	}


	public void
	       deleteAccount()
	{
		accountManager.deleteAccount();
	}


	public float
	       getPassphraseStrength()
	{
		return pwStrengthEstimator.estimateStrength( passphrase );
	}



	public Boolean
	       hasDbKey()
	{
		return accountManager.hasDatabaseKey();
	}


	public void
	       signIn( String passphrase )
	throws DecryptionException
	{
		accountManager.signIn( passphrase );
	}


	public void
	       signUp( String username,
	               String passphrase )
	throws InterruptedException
	{
		accountManager.createAccount( username, passphrase );
	}
}
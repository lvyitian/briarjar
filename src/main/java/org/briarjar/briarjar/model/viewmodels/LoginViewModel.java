package org.briarjar.briarjar.model.viewmodels;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.briarproject.bramble.api.crypto.DecryptionResult.INVALID_PASSWORD;


@Singleton
public class LoginViewModel {

	private final AccountManager accountManager;
	private final PasswordStrengthEstimator pwStrengthEstimator;

	int MIN_PASSPHRASE_LENGTH = 1; //TODO set higher value in production

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
		// TODO check null behaviour
		return pwStrengthEstimator.estimateStrength( passphrase );
	}


	public Boolean
	       hasDbKey()
	{
		return accountManager.hasDatabaseKey();
	}


	public void
	       signIn( String passphrase )
	throws GeneralException
	{
		try
		{
			accountManager.signIn( checkPassphraseLength(passphrase, false) );

		} catch ( DecryptionException e )
		{
			if ( e.getDecryptionResult().equals(INVALID_PASSWORD) )
			{
				throw new GeneralException( "Passphrase invalid",
				                            "Please try again", e );
			} else
			{
				throw new GeneralException( e.getDecryptionResult().name(),
						            "There seems something wrong...\n"+
		                            "May you want to try it again anyway?", e );
			}
		}
	}


	public void
	       signUp( String username,
	               String passphrase )
	throws GeneralException
	{
		accountManager.createAccount( username,
				                      checkPassphraseLength(passphrase, true) );
	}





// private =====================================================================

	private String
	        checkPassphraseLength( String  passphrase,
	                               boolean isSignUp    )
	throws GeneralException
	{
		if ( isSignUp ) {

			if ( passphrase == null ||
			     passphrase.length() < MIN_PASSPHRASE_LENGTH )
				throw new GeneralException( "Sorry",
			                        "Passphrase must be at least "+
			                         MIN_PASSPHRASE_LENGTH+" characters long" );
		} else {

			if ( passphrase == null || passphrase.length() <= 0 )
				throw new GeneralException( "Empty Passphrase",
				                            "Please enter your passphrase" );
		}
		return passphrase;
	}
}
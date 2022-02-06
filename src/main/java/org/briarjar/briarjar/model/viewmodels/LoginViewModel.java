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
		this.passphrase = passphrase; //TODO currently not implemented / useful
	}




	// ============================ logic ==============================

	public Boolean
	       accountExists()
	{
		return accountManager.accountExists();
	}


	public void
	       deleteAccount()
	{ //TODO maybe ask beforehand for reading unseen messages
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
		try {
			accountManager.signIn( checkPassphraseLength(passphrase, false) );
		}
		catch ( DecryptionException e ) {
			if ( e.getDecryptionResult().equals(INVALID_PASSWORD) )
				throw new GeneralException( "Invalid passphrase entered",
				                            "Checking passphrase", e, false );
			else
				throw new GeneralException( e, true, "Attempting to sign in" );
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
			     passphrase.length() < MIN_PASSPHRASE_LENGTH ) {

				String m = "Passphrase must be at least "+MIN_PASSPHRASE_LENGTH+
				           " characters long";
				throw new GeneralException( m, "Setting passphrase" );
			}

		} else {

			if ( passphrase == null || passphrase.length() <= 0 )
				throw new GeneralException( "Passphrase can not be empty",
					                        "Checking passphrase" );
		}
		return passphrase;
	}
}
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
import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;
import org.briarproject.bramble.api.crypto.SecretKey;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.briarproject.bramble.api.crypto.DecryptionResult.INVALID_PASSWORD;

/**
 * Provides a modest API for local user account management.
 * <ul><li>
 * Intends to be directly used by a simple UI implementation.
 * <li>
 * Underlying exceptions are caught and wrapped into {@link GeneralException}s.
 * GeneralExceptions are constructed with additional information where
 * appropriate.
 * <li>
 * Additionally checks for plausibility.
 * </ul>
 * Depends mainly on an implementations of {@link org.briarproject.bramble.api.account.AccountManager}
 * and {@link org.briarproject.bramble.api.crypto.PasswordStrengthEstimator},
 * and the {@link LifeCycleViewModel}.
 *
 * @since 1.00
 */
@Singleton
public class LoginViewModel {

	private final AccountManager accountManager;
	private final LifeCycleViewModel lifeCycleViewModel;
	private final PasswordStrengthEstimator pwStrengthEstimator;

	public static final int MIN_USERNAME_LENGTH = 1;
	public static final int MAX_USERNAME_LENGTH = 50;

	public static final int MIN_PASSPHRASE_LENGTH = 15;
	public static final int MAX_PASSPHRASE_LENGTH = 200;


	/**
	 * Constructs a LoginViewModel
	 *
	 * @param accountManager       an {@link org.briarproject.bramble.api.account.AccountManager} implementation
	 * @param lifeCycleViewModel   the {@link org.briarjar.briarjar.viewmodel.LifeCycleViewModel}
	 * @param pwStrengthEstimator  a {@link org.briarproject.bramble.api.crypto.PasswordStrengthEstimator} implementation
	 *
	 * @since 1.0
	 */
	@Inject
	public LoginViewModel( AccountManager            accountManager,
						   LifeCycleViewModel        lifeCycleViewModel,
	                       PasswordStrengthEstimator pwStrengthEstimator )
	{
		this.accountManager = accountManager;
		this.lifeCycleViewModel = lifeCycleViewModel;
		this.pwStrengthEstimator = pwStrengthEstimator;
	}





// public ======================================================================

	/**
	 * Returns if a local user account exists by trying to load the encrypted
	 * database from disk.
	 *
	 * @return {@code true} on success otherwise {@code false}
	 *
	 * @since  1.0
	 *
	 * @see org.briarproject.bramble.api.account.AccountManager#accountExists
	 */
	public Boolean
	       accountExists()
	{
		return accountManager.accountExists();
	}


	/**
	 * Deletes a local existing user account completely (contacts are not
	 * getting notified about) and exits the application gratefully.
	 *
	 * @throws GeneralException if a problem occurs during
	 *                          {@link LifeCycleViewModel#stopForAccountDeletion()}
	 *
	 * @since 1.0
	 *
	 * @see org.briarproject.bramble.api.account.AccountManager#deleteAccount
	 */
	public void
	       deleteAccount()
	throws GeneralException
	{
		lifeCycleViewModel.stopForAccountDeletion();
		accountManager.deleteAccount();
		System.exit(0);
	}


	/**
	 * Returns the estimated strength of the {@code passphrase} provided to
	 * assess its ability to withstand attacks.
	 *
	 * @param passphrase a string to assess
	 *
	 * @return a {@code float} between 0 (weakest) and 1 (strongest), inclusive
	 *
	 * @since 1.0
	 *
	 * @see org.briarproject.bramble.api.crypto.PasswordStrengthEstimator#estimateStrength(String) 
	 */
	public float
	       getPassphraseStrength( String passphrase )
	{
		return pwStrengthEstimator
		              .estimateStrength( passphrase != null ? passphrase : "" );
	}


	/**
	 * Returns if signed-in actions can be performed currently, which is only
	 * possible while the {@link org.briarproject.bramble.api.account.AccountManager}
	 * has the database key.
	 *
	 * @return  {@code true} after successful {@link #signUp signUp} or
	 *          {@link #signIn signIn} until {@link #deleteAccount()} or
	 *          {@link LifeCycleViewModel#stop()} is called or the process exits
	 *          for another reason.
	 *
	 * @see     org.briarproject.bramble.api.account.AccountManager#hasDatabaseKey
	 *
	 * @since   1.0
	 */
	public Boolean
	       hasDbKey()
	{
		return accountManager.hasDatabaseKey();
	}


	/**
	 * Signs in to the local existing user account when provided the correct
	 * {@code passphrase} and starts Briar.
	 *
	 * @param   passphrase the {@code string} used for account creation
	 *
	 * @throws  GeneralException when {@code passphrase} is not complying to
	 *          {@link #checkPassphrase checkPassphrase} or database decryption 
	 *          fails (usually the wrong {@code passphrase} provided)
	 *
	 * @see     org.briarproject.bramble.api.account.AccountManager#signIn(String) 
	 *
	 * @since   1.0
	 */
	public void
	       signIn( String passphrase )
	throws GeneralException
	{
		try {
			accountManager.signIn( checkPassphrase(passphrase) );
		}
		catch ( DecryptionException e ) {
			if ( e.getDecryptionResult().equals(INVALID_PASSWORD) )
				throw new GeneralException( "Invalid passphrase entered",
				                            "Checking passphrase", e, false );
			else
				throw new GeneralException( e, true, "Attempting to sign in" );
		}

		lifeCycleViewModel.start( accountManager.getDatabaseKey() );
	}


	/**
	 * Creates a required, local stored user account with the provided
	 * credentials and starts Briar (no extra {@link #signIn signIn} call is needed).
	 *<p>
	 * The freely chosen {@code username} is transmitted / displayed to contacts
	 * and may be locally extended by them with an alias.
	 *<p>
	 * There is no possibility to restore / reset the {@code passphrase} but
	 * {@link #deleteAccount() deleting} the whole user account and sign-up for
	 * a new one.
	 *
	 * @param   username a string, complying to {@link #checkUsername checkUsername}
	 *
	 * @param   passphrase a string, complying to {@link #checkPassphrase checkPassphrase}
	 * 	                   <p><b>Note:</b> Currently, the {@code passphrase}
	 * 	                   can not be altered after account creation since it is
	 * 	                   out of scope for this implementation.</p>
	 *
	 * @throws  GeneralException if compliance is not met or signing up is not
	 * 	                         possible for another reason
	 *
	 * @see     org.briarproject.bramble.api.account.AccountManager#createAccount(String, String) 
	 * @see     LifeCycleViewModel#start(SecretKey) 
	 *
	 * @since   1.0
	 */
	public void
	       signUp( String username,
	               String passphrase )
	throws GeneralException
	{
		accountManager.createAccount( checkUsername(username),
				                      checkPassphrase(passphrase) );
		lifeCycleViewModel.start( accountManager.getDatabaseKey() );
	}





// private =====================================================================


	/**
	 * Checks the provided {@code username} for compliance.
	 *
	 * @param   username a {@code string}, not null, not empty, not blank,
	 *          within boundaries {@link #MIN_USERNAME_LENGTH} and {@link #MAX_USERNAME_LENGTH}
	 *
	 * @return  the provided {@code username} if compliance is met
	 *
	 * @throws  GeneralException if compliance is not met
	 *
	 * @see     #checkPassphrase(String) 
	 *
	 * @since   1.0
	 *
	 */
	private String
	        checkUsername( String username )
	throws GeneralException
	{
		if ( username == null ||
		     username.isBlank() ||
		     username.length() < MIN_USERNAME_LENGTH ||
		     username.length() > MAX_USERNAME_LENGTH    ) {

			String msg = "Username must be between "+MIN_USERNAME_LENGTH+" and "
			             +MAX_USERNAME_LENGTH+" characters long and not blank";

			throw new GeneralException( new IllegalArgumentException(msg),
			                            false, "Checking username" );
		}
		return username;
	}


	/**
	 * Checks the provided {@code passphrase} for compliance.
	 *
	 * @param   passphrase a {@code string}, not null, not empty, within
	 *          boundaries {@link #MIN_PASSPHRASE_LENGTH} and {@link #MAX_PASSPHRASE_LENGTH}
	 *
	 * @return  the provided {@code passphrase} if compliance is met
	 *
	 * @throws  GeneralException if compliance is not met
	 *
	 * @see     #checkUsername(String) 
	 *
	 * @since   1.0
	 *
	 */
	private String
	        checkPassphrase( String passphrase )
	throws GeneralException
	{
		if ( passphrase == null ||
		     passphrase.length() < MIN_PASSPHRASE_LENGTH ||
		     passphrase.length() > MAX_PASSPHRASE_LENGTH    ) {

				String msg = "Passphrase must be between "+MIN_PASSPHRASE_LENGTH
				             +" and "+MAX_PASSPHRASE_LENGTH+" characters long";

				throw new GeneralException( new IllegalArgumentException(msg),
				                            false, "Checking passphrase" );
		}
		return passphrase;
	}
}
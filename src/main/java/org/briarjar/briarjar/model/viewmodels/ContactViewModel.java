package org.briarjar.briarjar.model.viewmodels;


import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.Pair;
import org.briarproject.bramble.api.contact.*;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import java.security.GeneralSecurityException;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.briarjar.briarjar.model.utils.Checker.throwOnNullParam;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;


/**
 * Provides a modest API for contact management.
 * <ul><li>
 * Intends to be directly used by a simple UI implementation.
 * <li>
 * Underlying exceptions are caught and wrapped into {@link GeneralException}s.
 * GeneralExceptions are constructed with additional information where
 * appropriate.
 * <li>
 * Additionally checks for plausibility.
 * </ul>
 * Depends mainly on an implementation of {@link org.briarproject.bramble.api.contact.ContactManager}.
 *<p>
 * //TODO @version 1.0, 2021-mm-dd hh:mm
 */
@Singleton
@NotNullByDefault
public class ContactViewModel {

	private final ContactManager contactManager;

	/**
	 * Constructs a ContactViewModel
	 *
	 * @param contactManager  a {@link org.briarproject.bramble.api.contact.ContactManager} implementation
	 *
	 * @since 1.0
	 */
	@Inject
	public ContactViewModel( ContactManager contactManager )
	{
		this.contactManager = contactManager;
	}






// public ======================================================================

	/**
	 * Adds a new pending (yet not mutually accepted) contact for communication.
	 * The alias (shown contact name) can also be changed later on if desired.
	 *
	 * @param link  a string, not null, not empty, not blank
	 *
	 * @param alias  a string, not null, not empty, not blank
	 *
	 * @throws GeneralException if compliance is not met or adding is not
	 *                          possible for another reason
	 * @see org.briarproject.bramble.api.contact.ContactManager#addPendingContact(String, String) 
	 *
	 * @since 1.0
	 */
	public void
	       addPendingContact( String link,
	                          String alias )
	throws GeneralException
	{
		try {
			throwOnNullParam("Handshake Link", link);
			if ( link.isBlank() )
				throw new IllegalArgumentException(
				                         "The handshake-link can not be blank");

			if ( link.equals( contactManager.getHandshakeLink() ) )
				throw new IllegalArgumentException( "You entered your own " +
				     "handshake-link, but the link of your contact is needed" );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true, "Checking handshake-link" );
		}


		String exTitle = "Attempting to add contact";
		try {
			contactManager.addPendingContact( link, checkAlias(alias) );
		}
		catch (DbException | GeneralSecurityException e) {
			throw new GeneralException( e, true, exTitle );
		}
		catch (FormatException e) {
			throw new GeneralException( "The link format seems to be invalid",
			                            exTitle, e, true );
		}
	}


	/**
	 * Returns all mutually accepted (not pending any more) contacts. Useful to
	 * acquire further information.
	 *
	 * @return  a collection of {@link org.briarproject.bramble.api.contact.Contact}s
	 *
	 * @throws GeneralException if returning is not possible for an underlying
	 *                          reason
	 * @see org.briarproject.bramble.api.contact.ContactManager#getPendingContacts
	 *
	 * @since 1.0
	 */
	public Collection< Contact >
	       getContacts()
	throws GeneralException
	{
		try	{
			return contactManager.getContacts();
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
					                    "Attempting to get accepted contacts" );
		}
	}


	/**
	 * Returns the mutually accepted (not pending any more) contact with the
	 * provided {@link org.briarproject.bramble.api.contact.ContactId}
	 * . Useful to acquire further information.
	 *
	 * @param contactId  contact's {@link org.briarproject.bramble.api.contact.ContactId}
	 *                   , not null
	 * @return the desired {@link org.briarproject.bramble.api.contact.Contact}
	 *
	 * @throws GeneralException if compliance is not met or returning is not
	 *                          possible for another reason
	 *
	 * @see org.briarproject.bramble.api.contact.ContactManager#getContact(ContactId)
	 *
	 * @since 1.0
	 */
	public Contact
	       getContact( ContactId contactId )
	throws GeneralException
	{
		try {
			throwOnNullParam( "ContactId", contactId );
			return contactManager.getContact( contactId );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true, "Attempting to get contact" );
		}
	}


	/**
	 * Returns the own handshake link which is needed to be mutually shared with
	 * a new contact in order to get connected to each other for communication.
	 *
	 * @return a string of the own handshake link
	 *
	 * @throws GeneralException if returning is not possible for an underlying
	 *                          reason
	 * @see org.briarproject.bramble.api.contact.ContactManager#getContact(ContactId) 
	 *
	 * @since 1.0
	 */
	public String
	       getHandshakeLink()
	throws GeneralException
	{
		try {
			return contactManager.getHandshakeLink();
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
			                            "Attempting to get handshake-link" );
		}
	}


	/**
	 * Returns all pending (yet not mutually accepted) contacts and their
	 * current state.
	 *
	 * @return  a collection of {@link org.briarproject.bramble.api.Pair}s
	 *          of a {@link org.briarproject.bramble.api.contact.PendingContact}
	 * 	        and the current {@link org.briarproject.bramble.api.contact.PendingContactState}
	 *
	 * @throws GeneralException if returning is not possible for an underlying
	 *                          reason
	 * @see org.briarproject.bramble.api.contact.ContactManager#getPendingContacts
	 *
	 * @since 1.0
	 */
	public Collection< Pair<PendingContact, PendingContactState> >
	       getPendingContacts()
	throws GeneralException
	{
		try {
			return contactManager.getPendingContacts();
		}
		catch (DbException e) {
			throw new GeneralException( e, true, "Attempting to get pending "+
			                                     "contacts and their state"   );
		}
	}


	/**
	 * Removes a mutually accepted (not pending any more) contact. The contact
	 * is not getting notified about.
	 *
	 * @param c  contact's {@link org.briarproject.bramble.api.contact.ContactId},
	 *           not null
	 *
	 * @throws GeneralException if compliance is not met or removing is not
	 *                          possible for another reason
	 *
	 * @see org.briarproject.bramble.api.contact.ContactManager#removeContact(ContactId) 
	 *
	 * @since 1.0
	 */
	public void
	       removeContact(ContactId c )
	throws GeneralException
	{
	    try {
			throwOnNullParam( "ContactId", c );
		    contactManager.removeContact( c );
	    }
		catch ( DbException | IllegalArgumentException e ) {
		    throw new GeneralException( e, true,
		                               "Attempting to remove accepted contact");
	    }
    }


	/**
	 * Removes an {@link #addPendingContact added} but still pending (yet not
	 * mutually accepted) contact.
	 *
	 * @param p  contact's {@link org.briarproject.bramble.api.contact.PendingContactId}
	 *           , not null
	 *
	 * @throws GeneralException if compliance is not met or removing is not
	 *                          possible for another reason
	 *
	 * @see org.briarproject.bramble.api.contact.ContactManager#removePendingContact(PendingContactId) 
	 *
	 * @since 1.0
	 */
	public void
	       removePendingContact( PendingContactId p )
	throws GeneralException
	{
		try {
			throwOnNullParam( "PendingContactId", p );
			contactManager.removePendingContact( p );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true,
			                            "Attempting to remove pending contact");
		}
	}


	/**
	 * Sets the provided alias for the contact with the provided
	 * {@link org.briarproject.bramble.api.contact.ContactId}.
	 *
	 * @param contactId  contact's {@link org.briarproject.bramble.api.contact.ContactId}
	 *                   , not null
	 *
	 * @param alias  a string, not null, not empty, not blank
	 *
	 * @throws GeneralException if compliance is not met or setting is not
	 *                          possible for another reason
	 *
	 * @see org.briarproject.bramble.api.contact.ContactManager#setContactAlias(ContactId, String) 
	 *
	 * @since 1.0
	 */
	public void
	       setContactAlias( ContactId contactId,
	                        String    alias      )
	throws GeneralException
	{
		try {
			throwOnNullParam( "ContactId", contactId );
			contactManager.setContactAlias( contactId, checkAlias(alias) );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true,
			                            "Attempting to set contact's alias" );
		}
	}






// private =====================================================================

	/**
	 * Checks the provided alias for compliance.
	 *
	 * @param alias  a string, not null, not empty, not blank,
	 *               with maximum {@link org.briarproject.bramble.api.identity.AuthorConstants#MAX_AUTHOR_NAME_LENGTH MAX_AUTHOR_NAME_LENGTH}
	 *
	 * @return the provided {@code alias} if compliance is met
	 *
	 * @throws GeneralException if compliance is not met
	 *
	 * @implNote Briar allows an alias to be unset but that is out of scope for
	 *           this implementation so far
	 * @since 1.0
	 */
	private String
	        checkAlias( String alias )
	throws GeneralException
	{
		if ( alias == null ||
		     alias.isBlank() ||
		     alias.length() > MAX_AUTHOR_NAME_LENGTH )
		{
			String msg = "Alias length must not be blank and max. "+
                          MAX_AUTHOR_NAME_LENGTH+" (not "+alias.length()+
			             ") characters long";

			throw new GeneralException( new IllegalArgumentException(msg),
			                            false, "Checking Alias" );
		}
		return alias;
	}
}

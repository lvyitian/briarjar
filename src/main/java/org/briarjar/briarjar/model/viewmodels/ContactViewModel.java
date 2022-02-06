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

import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;


@Singleton
@NotNullByDefault
public class ContactViewModel {

	private final ContactManager contactManager;

	@Inject
	public ContactViewModel( ContactManager contactManager )
	{
		this.contactManager = contactManager;
	}






// public ======================================================================

	public void
	       addPendingContact( String link,
	                          String alias )
	throws GeneralException
	{
		String exTitle = "Checking handshake-link";

		if ( link == null || link.isBlank() )
			throw new GeneralException( "The handshake-link can not be empty",
			                            exTitle );
		try {
			if ( link.equals( contactManager.getHandshakeLink() ) ) {
				String m = "You entered your own handshake-link, but the link" +
				           " of your contact is needed";
				throw new GeneralException( m, exTitle );
			}
		}
		catch (DbException e) {
			throw new GeneralException( e, true, exTitle );
		}


		exTitle = "Attempting to add contact";
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


	public Collection< Contact >
	       getAcceptedContacts()
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


	public Contact
	       getContact( ContactId contactId )
	throws GeneralException
	{
		try {
			return contactManager.getContact( contactId );
		}
		catch (DbException e) {
			throw new GeneralException( e, true, "Attempting to get contact" );
		}
	}


	public Collection< Contact >
	       getContacts()
	throws GeneralException
	{
		try {
			return contactManager.getContacts();
		}
		catch (DbException e) {
			throw new GeneralException( e, true, "Attempting to get contacts" );
		}
	}


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


	public void
	       removeAcceptedContact( ContactId c )
	throws GeneralException
	{
	    try {
		    contactManager.removeContact( c );
	    }
		catch ( DbException e ) {
		    throw new GeneralException( e, true,
		                               "Attempting to remove accepted contact");
	    }
    }


	public void
	       removePendingContact( PendingContactId p )
	throws GeneralException
	{
		try {
			contactManager.removePendingContact( p );
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
			                            "Attempting to remove pending contact");
		}
	}


	public void
	       setContactAlias( ContactId contactId,
	                        String    alias      )
	throws GeneralException
	{
		try {
			contactManager.setContactAlias( contactId, checkAlias(alias) );
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
			                            "Attempting to set contact's alias" );
		}
	}






// private =====================================================================

	private String
	        checkAlias( String alias ) //TODO it's allowed to be empty at briar
	throws GeneralException
	{
		if ( alias.isBlank() ||
		     alias.length() > MAX_AUTHOR_NAME_LENGTH )
		{
			String msg = "Alias length must not be blank and max. "+
                          MAX_AUTHOR_NAME_LENGTH+" (not "+alias.length()+
			             ") characters long";
			throw new GeneralException( msg, "Checking Alias" );
		}
		return alias;
	}
}

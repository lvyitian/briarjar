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
	throws DbException,
	       FormatException,
	       GeneralException,
	       GeneralSecurityException
	{
		contactManager.addPendingContact( link, checkAlias(alias) );
	}


	public Collection< Contact >
	       getAcceptedContacts()
	throws DbException
	{
		return contactManager.getContacts();
	}


	public Contact
	       getContact( ContactId contactId )
	throws DbException
	{
		return contactManager.getContact( contactId );
	}


	public Collection< Contact >
	       getContacts()
	throws DbException
	{
		return contactManager.getContacts();
	}


	public String
	       getHandshakeLink()
	throws DbException
	{
		return contactManager.getHandshakeLink();
	}


	public Collection< Pair<PendingContact, PendingContactState> >
	       getPendingContacts()
	throws DbException
	{
		return contactManager.getPendingContacts();
	}


	public void
	       removeAcceptedContact( ContactId c )
    throws DbException
    {
        contactManager.removeContact( c );
    }


	public void
	       removePendingContact( PendingContactId p )
	throws DbException
	{
		contactManager.removePendingContact( p );
	}


	public void
	       setContactAlias( ContactId contactId,
	                        String    alias      )
	throws DbException,
	       GeneralException

	{
		contactManager.setContactAlias( contactId, checkAlias(alias) );
	}






// private =====================================================================

	private String
	        checkAlias( String alias )
	throws GeneralException
	{
		// TODO further checks? (fe does alias already exist?)
		if ( alias.isBlank() ||
		     alias.length() > MAX_AUTHOR_NAME_LENGTH )
		{
			throw new GeneralException( "Alias length must be not blank and " +
				"max. "+MAX_AUTHOR_NAME_LENGTH+" not "+alias.length()+" long" );
		}
		return alias;
	}

}

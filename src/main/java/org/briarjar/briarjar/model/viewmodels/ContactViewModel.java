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
	public ContactViewModel(ContactManager contactManager)
	{
		this.contactManager = contactManager;
	}


	public void addPendingContact(String link, String alias)
			throws GeneralSecurityException, FormatException, DbException,
			       GeneralException
	{
		checkAliasLength(alias);
		contactManager.addPendingContact(link, alias);
	}

	private void checkAliasLength(String alias)
			throws GeneralException
	{
		if (alias.isBlank() || alias.length() > MAX_AUTHOR_NAME_LENGTH)
		{
			throw new GeneralException("Invalid alias length (must be shorter" +
					" then " + MAX_AUTHOR_NAME_LENGTH +
					" characters and not be blank)");
		}
	}

	public Collection<Contact> getAcceptedContacts()
			throws DbException
	{
		return contactManager.getContacts();
	}


	public String getLink()
			throws DbException
	{
		return contactManager.getHandshakeLink();
	}


	public Collection<Pair<PendingContact, PendingContactState>> getPendingContacts()
			throws DbException
	{
		return contactManager.getPendingContacts();
	}

	public void removeAcceptedContact(ContactId c)
			throws DbException
	{
		contactManager.removeContact(c);
	}

	public void removePendingContact(PendingContactId p)
			throws DbException
	{
		contactManager.removePendingContact(p);
	}

	public void setContactAlias(ContactId contactId, String alias)
			throws GeneralException, DbException
	{
		checkAliasLength(alias);
		contactManager.setContactAlias(contactId, alias);
	}

	// todo a: events
}

package org.briarjar.briarjar.model.viewmodels;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.Pair;
import org.briarproject.bramble.api.contact.*;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.contact.event.*;
import java.security.GeneralSecurityException;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;


@Singleton
@NotNullByDefault
public class ContactViewModel extends EventListenerViewModel {

	private final ContactManager contactManager;

	@Inject
	public ContactViewModel(EventBus eventBus,
	                        ContactManager contactManager)
	{
		super(eventBus);
		super.onInit();

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

	// todo
	public void loadContacts(){
		try
		{
			var acceptedContacts = getAcceptedContacts();
			var pendingContacts = getPendingContacts();

			System.out.println("accepted contacts:");
			for (Contact c : acceptedContacts) {
				System.out.println(c);
			}

			System.out.println("pending contacts:");
			for (Pair p : pendingContacts) {
				System.out.println(p.getFirst()+" / "+p.getSecond());
			}
		} catch (DbException e)
		{
			e.printStackTrace();
		}
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

	// todo
	@Override
	public void eventOccurred(Event e)
	{
		if (e instanceof ContactRemovedEvent)
		{
			System.out.println("ContactRemovedEvent... load contacts...");
			loadContacts();
		}
		else if (e instanceof ContactAddedEvent)
		{
			System.out.println("ContactAddedEvent... load contacts...");
			loadContacts();
		}
		else if (e instanceof PendingContactAddedEvent)
		{
			System.out.println("PendingContactAddedEvent... load contacts...");
			loadContacts();
		}
	}
}

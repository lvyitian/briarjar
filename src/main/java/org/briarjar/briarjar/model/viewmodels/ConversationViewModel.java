package org.briarjar.briarjar.model.viewmodels;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.MessageDeletedException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.briar.api.client.MessageTracker.GroupCount;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.conversation.DeletionResult;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageFactory;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.briarjar.briarjar.model.utils.Checker.throwOnNullParam;

/**
 * Provides a modest API for conversation management.
 * <ul><li>
 * Intends to be directly used by a simple UI implementation.
 * <li>
 * Underlying exceptions are caught and wrapped into {@link GeneralException}s.
 * GeneralExceptions are constructed with additional information where
 * appropriate.
 * <li>
 * Additionally checks for plausibility.
 * </ul>
 * Depends mainly on an implementations of {@link org.briarproject.briar.api.conversation.ConversationManager},
 * {@link org.briarproject.briar.api.messaging.MessagingManager} and
 * {@link org.briarproject.briar.api.messaging.PrivateMessageFactory}.
 *<p>
 * //TODO @version 1.0, 2021-mm-dd hh:mm
 */
@Singleton
@NotNullByDefault
public class ConversationViewModel {

	private final ConversationManager conversationManager;
	private final MessagingManager messagingManager;
	private final PrivateMessageFactory pmFactory;

	/**
	 * Constructs a ConversationViewModel
	 *
	 * @param conversationManager    a {@link org.briarproject.briar.api.conversation.ConversationManager} implementation
	 * @param messagingManager       a {@link org.briarproject.briar.api.messaging.MessagingManager} implementation
	 * @param privateMessageFactory  a {@link org.briarproject.briar.api.messaging.PrivateMessageFactory} implementation
	 *
	 * @since 1.0
	 */
	@Inject
	public ConversationViewModel( ConversationManager   conversationManager,
			                      MessagingManager      messagingManager,
			                      PrivateMessageFactory privateMessageFactory )
	{
		this.conversationManager = conversationManager;
		this.messagingManager = messagingManager;
		this.pmFactory = privateMessageFactory;
	}





// public ======================================================================

	/**
	 * Deletes all private messages from the chat with the provided
	 * {@link org.briarproject.bramble.api.contact.ContactId}.
	 *
	 * @param c  contact's {@link org.briarproject.bramble.api.contact.ContactId}
	 *           , not null
	 *
	 * @throws GeneralException  if compliance is not met or deleting is
	 *                           currently not possible
	 *
	 * @see org.briarproject.briar.api.conversation.ConversationManager#deleteAllMessages(ContactId) 
	 *
	 * @since 1.0
	 */
	public void
	       deleteAllMessages( ContactId c )
	throws GeneralException
	{
	    try {
		    throwOnNullParam( "ContactId", c );
		    DeletionResult result = conversationManager.deleteAllMessages( c );

			if ( !result.allDeleted() )
				throw new Exception( "Can not delete messages during an " +
				       "introduction / invitation progress with this contact" );
	    }
		catch (Exception e) {
		    throw new GeneralException( e, true,
		                                "Attempting to delete all messages" );
	    }
    }

	/**
	 * Deletes multiple chosen private messages from the chat with the provided
	 * {@link org.briarproject.bramble.api.contact.ContactId}.
	 *
	 * @param c  contact's {@link org.briarproject.bramble.api.contact.ContactId}
	 *           , not null
	 * @param toDelete  a {@link Collection} of {@link org.briarproject.bramble.api.sync.MessageId}
	 *                  s, not null
	 *
	 * @throws GeneralException if compliance is not met or deleting is
	 *                          currently not possible
	 *
	 * @see org.briarproject.briar.api.conversation.ConversationManager#deleteMessages(ContactId, Collection) 
	 *
	 * @since 1.0
	 */
    public void
           deleteMessages( ContactId             c,
                           Collection<MessageId> toDelete )
    throws GeneralException
    {
	    try {
		    throwOnNullParam( "ContactId", c );
		    throwOnNullParam( "Collection<MessageId>", c );
			DeletionResult r = conversationManager.deleteMessages( c, toDelete);

		    if ( !r.allDeleted() )
			    throw new Exception( "Can not delete messages during an " +
				       "introduction / invitation progress with this contact" );
	    }
		catch (Exception e) {
			throw new GeneralException( e, true,
			                         "Attempting to delete specific messages" );
	    }
    }


	/**
	 * Returns the {@link org.briarproject.bramble.api.contact.ContactId} of the
	 * provided {@link org.briarproject.bramble.api.sync.GroupId} (private
	 * conversation ID).
	 *
	 * @param g  contact's {@link org.briarproject.bramble.api.sync.GroupId}
	 *           , not null
	 * @return the related {@link org.briarproject.bramble.api.contact.ContactId}
	 *
	 * @throws GeneralException if compliance is not met or returning is not
	 *                          possible for another reason
	 *
	 * @see #getConversationId(ContactId)
	 * @see org.briarproject.briar.api.messaging.MessagingManager#getContactId(GroupId)
	 *
	 * @since 1.0
	 */
	public ContactId
	       getContactId( GroupId g )
	throws GeneralException
	{
		try {
			throwOnNullParam( "GroupId", g );
			return messagingManager.getContactId( g );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true,
			                        "Attempting to get contact's internal ID" );
		}
	}


	/**
	 * Returns the {@link org.briarproject.bramble.api.sync.GroupId} (private
	 * conversation ID) of the provided {@link org.briarproject.bramble.api.contact.ContactId}
	 * , useful for instance to mark read messages by calling {@link #setReadFlag setReadFlag}.
	 *
	 * @param c  contact's {@link org.briarproject.bramble.api.contact.ContactId}
	 *           , not null
	 * @return the related {@link org.briarproject.bramble.api.sync.GroupId}
	 *
	 * @throws GeneralException if compliance is not met or returning is not
	 *                          possible for another reason
	 *
	 * @see #getContactId(GroupId)
	 * @see org.briarproject.briar.api.messaging.MessagingManager#getConversationId(ContactId)
	 *
	 * @since 1.0
	 */
	public GroupId
	       getConversationId( ContactId c )
	throws GeneralException
	{
		try {
			throwOnNullParam( "ContactId", c );
			return messagingManager.getConversationId( c );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true,
			           "Attempting to get contact's internal conversation ID" );
		}
	}


// TODO
	/**
	 * @deprecated Will only be completely implemented if UI makes use of it
	 *         since it is not required (out of scope) for this diploma project.
	 */
	public GroupCount            // currently: msgCount, unreadCount, latestTime
	       getGroupCount( ContactId contactId )
    throws GeneralException
    {
	    try {
		    return conversationManager.getGroupCount( contactId );
	    }
		catch (DbException e) {
		    throw new GeneralException( e, true,
		                            "Attempting to get contact's group count" );
	    }
    }


	/**
	 * Gets a collection of private message headers exchanged with the passed
	 * {@link org.briarproject.bramble.api.contact.ContactId}, useful for
	 * calling {@link #getMessageText(MessageId)}.
	 *
	 * @param   c  contact's {@link org.briarproject.bramble.api.contact.ContactId}
	 *             , not null
	 * @return  a {@link Collection} of {@link org.briarproject.briar.api.conversation.ConversationMessageHeader}s
	 *
	 * @throws GeneralException if compliance is not met or returning is not
	 * 	                        possible for another reason
	 *
	 * @see org.briarproject.briar.api.conversation.ConversationManager#getMessageHeaders(ContactId) 
	 *
	 * @since 1.0
	 */
	public Collection< ConversationMessageHeader >
	       getMessageHeaders( ContactId c )
	throws GeneralException
	{
		try {
			throwOnNullParam( "ContactId", c );
			return conversationManager.getMessageHeaders( c );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true,
			                            "Attempting to get message headers" );
		}
	}


	/**
	 * Gets the private message text for a passed {@link org.briarproject.bramble.api.sync.MessageId}
	 * or a placeholder under certain conditions.
	 *
	 * @param m  the {@link org.briarproject.bramble.api.sync.MessageId} of
	 *           interest, not null
	 * @return  a string of the stored message text or a placeholder if
	 *          the message text is {@code null} or has been deleted before
	 *
	 * @throws GeneralException  if the requested message text can not be
	 *                           fetched for another reason
	 *
	 * @see org.briarproject.briar.api.messaging.MessagingManager#getMessageText(MessageId) 
	 *
	 * @since 1.0
	 */
	public String
	       getMessageText( MessageId m )
    throws GeneralException
    {
		if ( m == null )
			return "<Can not get message text from a null message ID>";

	    try {
			String text = messagingManager.getMessageText( m );
			if ( text == null )
				text = "<This message ID has no text stored, hm>";
			return text;
	    }
		catch (DbException e) {
			if ( e.getClass().equals(MessageDeletedException.class) )
				return
				 "<This message text is not available on your client any more>";
		    else
				throw new GeneralException( e, true,
				                            "Attempting to get message text" );
	    }
    }


//TODO
	/**
	 * @deprecated Will only be completely implemented if UI makes use of it
	 *         since it is not required (out of scope) for this diploma project.
	 */
	public void
	       setReadFlag( GroupId   g,
	                    MessageId m,
	                    boolean   read )
    throws GeneralException
    {
	    try {
		    conversationManager.setReadFlag( g, m, read );
	    }
		catch (DbException e) {
		    throw new GeneralException( e, true, "Attempting to set read flag");
	    }
    }


	/**
	 * Writes a legacy {@link org.briarproject.briar.api.messaging.PrivateMessageFormat#TEXT_ONLY TEXT_ONLY}
	 * private message to a contact, stores and send it.
	 *
	 * @param contactId  the {@link org.briarproject.bramble.api.contact.ContactId}
	 *                   of the receiver, not null
	 * @param text       a {@code string} to be sent, not null, not blank
	 *
	 * @throws GeneralException  if compliance is not met or writing is not
	 * 	                         possible for another reason
	 *
	 * @see org.briarproject.briar.api.messaging.PrivateMessageFactory#createLegacyPrivateMessage(GroupId, long, String) 
	 * @see org.briarproject.briar.api.messaging.MessagingManager#addLocalMessage(PrivateMessage)
	 * @see System#currentTimeMillis()
	 *
	 * @since 1.0
	 */
	public void
	       write( ContactId contactId,
				  String    text       )
	throws GeneralException
	{
		if ( text == null || text.isBlank() ) {
			throw new GeneralException(
			      new IllegalArgumentException("Message text can not be blank"),
			      true, "Checking if text input is not blank" );
		}

		GroupId groupId;
		try {
			throwOnNullParam( "ContactId", contactId );
			groupId = messagingManager.getConversationId( contactId );
		}
		catch (DbException | IllegalArgumentException e) {
			throw new GeneralException( e, true,
			           "Attempting to get contact's internal conversation ID" );
		}

		PrivateMessage pm;
		try {
			pm = pmFactory.createLegacyPrivateMessage(
			                         groupId, System.currentTimeMillis(), text);
		}
		catch (FormatException e) {
			throw new GeneralException("The message format seems to be invalid",
			                           "Attempting to create message", e, true);
		}

		try {
			messagingManager.addLocalMessage( pm );
		}
		catch (DbException e) {
			throw new GeneralException( e, true, "Attempting to send message" );
		}
	}






// private =====================================================================


}

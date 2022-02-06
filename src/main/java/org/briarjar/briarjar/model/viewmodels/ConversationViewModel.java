package org.briarjar.briarjar.model.viewmodels;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.InvalidMessageException;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.briar.api.client.MessageTracker.GroupCount;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.conversation.DeletionResult;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageFactory;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
@NotNullByDefault
public class ConversationViewModel {

	private final ConversationManager conversationManager;
	private final MessagingManager messagingManager;
	private final PrivateMessageFactory pmFactory;

	@Inject
	public ConversationViewModel( ConversationManager   conversationManager,
			                      MessagingManager      messagingManager,
			                      PrivateMessageFactory privateMessageFactory )
	{
		this.conversationManager = conversationManager;
		this.messagingManager = messagingManager;
		this.pmFactory = privateMessageFactory;

		//messagesQueryAmount = 25;
	}





// public ======================================================================

	public DeletionResult
           deleteAllMessages( ContactId c )
	throws GeneralException
	{
	    try {
		    return conversationManager.deleteAllMessages( c );
	    }
		catch (DbException e) {
		    throw new GeneralException( e, true,
		                                "Attempting to delete all messages" );
	    }
    }


    public DeletionResult
           deleteMessages( ContactId             c,
                           Collection<MessageId> toDelete )
    throws GeneralException
    {
	    try {
		    return conversationManager.deleteMessages( c, toDelete );
	    }
		catch (DbException e) {
			throw new GeneralException( e, true,
			                         "Attempting to delete specific messages" );
	    }
    }


	public ContactId
	       getContactId( GroupId g )
	throws GeneralException
	{
		try {
			return messagingManager.getContactId( g );
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
			                        "Attempting to get contact's internal ID" );
		}
	}


	public GroupId
	       getConversationId( ContactId c )
	throws GeneralException
	{
		try {
			return messagingManager.getConversationId( c );
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
			           "Attempting to get contact's internal conversation ID" );
		}
	}


	public GroupCount
	       getGroupCount( ContactId contactId ) // TODO what's this exactly?
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


	public Collection< ConversationMessageHeader >
	       getMessageHeaders( ContactId c )
	throws GeneralException
	{
		try {
			return conversationManager.getMessageHeaders( c );
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
			                            "Attempting to get message headers" );
		}
	}


	@Nullable
	public String
	       getMessageText( MessageId m )
    throws GeneralException
    {
	    try {
		    return messagingManager.getMessageText( m );
	    }
		catch (DbException e) {
		    throw new GeneralException(e,true,"Attempting to get message text");
	    }
    }


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


	public void
	       write( ContactId contactId,
				  long      timestamp,
				  String    text       )
	throws GeneralException
	{
		if ( text == null || text.isBlank() ) {
			throw new GeneralException(
			       new InvalidMessageException("Message text can not be blank"),
			       true, "Checking if text input is not blank" );
		}

		GroupId groupId;
		try {
			groupId = messagingManager.getConversationId( contactId );
		}
		catch (DbException e) {
			throw new GeneralException( e, true,
			           "Attempting to get contact's internal conversation ID" );
		}

		PrivateMessage pm;
		try {
			pm = pmFactory.createLegacyPrivateMessage(groupId, timestamp, text);
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


	/*
	 * TODO: move to related classes
	 *
	@Override
	public void
	       eventOccurred( Event e )
	{
		BRAMBLE-API -------------------------

		ContactAddedEvent
		ContactAliasChangedEvent
		*/
	/*
		ContactRemovedEvent
		ContactVerifiedEvent
		PendingContactAddedEvent
		PendingContactRemovedEvent
		PendingContactStateChangedEvent

		IdentityAddedEvent
		IdentityRemovedEvent

		KeyAgreementAbortedEvent
		KeyAgreementFailedEvent
		KeyAgreementFinishedEvent
		KeyAgreementListeningEvent
		KeyAgreementStartedEvent
		KeyAgreementStoppedListeningEvent
		KeyAgreementWaitingEvent

		NetworkStatusEvent

		ConnectionClosedEvent
		ConnectionOpenedEvent
		ContactConnectedEvent
		ContactDisconnectedEvent

		RendezvousConnectionClosedEvent
		RendezvousConnectionOpenedEvent
		RendezvousPollEvent

		SettingsUpdatedEvent

		MessageAddedEvent
		MessageRequestedEvent
		MessagesAckedEvent
		MessageSharedEvent
		MessagesSentEvent
		MessageStateChangedEvent
		MessageToAckEvent
		MessageToRequestEvent


		BRIAR-API ---------------------------

		ConversationMessagesDeletedEvent

		ConversationMessageReceivedEvent

		IntroductionAbortedEvent
		IntroductionRequestReceivedEvent
		IntroductionResponseReceivedEvent

		AttachmentReceivedEvent
		PrivateMessageReceivedEvent



		if (e instanceof MessageAddedEvent)
		{
			System.out.println("MessageAddedEvent");

		}
		else if (e instanceof MessageRequestedEvent)
		{
			System.out.println("MessageRequestedEvent");

		}
		else if (e instanceof MessagesAckedEvent)
		{
			System.out.println("MessagesAckedEvent");

		}
		else if (e instanceof MessageSharedEvent)
		{
			System.out.println("MessageSharedEvent");

		}
		else if (e instanceof MessagesSentEvent)
		{
			System.out.println("MessagesSentEvent");

		}
		else if (e instanceof MessageStateChangedEvent)
		{
			System.out.println("MessageStateChangedEvent");

		}
		else if (e instanceof MessageToAckEvent)
		{
			System.out.println("MessageToAckEvent");

		}
		else if (e instanceof MessageToRequestEvent)
		{
			System.out.println("MessageToRequestEvent");

		}

		else if (e instanceof PrivateMessageReceivedEvent)
        {
            System.out.println("PrivateMessageReceivedEvent");

            try
            {
				// TODO
                ContactId cId = new ContactId(1);
                var mH = getMessageHeaders( cId ).stream().findFirst().get();
                MessageId mId = mH.getId();
                System.out.println( "MessageHeader... "+mH+", ...");
                System.out.println( getMessageText( mId ) );
            }
            catch (DbException ex)
            {
                System.out.println(ex.getMessage());
            }
        }

}*/






// private =====================================================================







	//todo
	/*
	 * such feature maybe later on, not yet
     *
    private int messagesQueryAmount;

	@Experimental
	public Stream<ConversationMessageHeader> getMoreMessages(ContactId contactId)
			throws DbException
	{
		// todo a: implement further messages loading; sorting?
		return getMessageHeaders(contactId).stream().limit(messagesQueryAmount);
	}

	public void setMessagesQueryAmount(int amount)
			throws Exception
	{
		int min = 4;
		int max = 50;
		if (amount >= min && amount <= max)
			this.messagesQueryAmount = amount;
		else
			throw new Exception("setAmountOfMessagesPerGet must be between " +
					min + " and " + max + "!");
	}
	*/


}

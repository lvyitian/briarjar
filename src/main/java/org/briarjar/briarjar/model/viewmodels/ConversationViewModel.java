package org.briarjar.briarjar.model.viewmodels;

import org.briarjar.briarjar.model.utils.Experimental;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.InvalidMessageException;
import org.briarproject.bramble.api.sync.event.*;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageFactory;

import java.util.Collection;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
@NotNullByDefault
public class ConversationViewModel extends EventListenerViewModel {

	private final ConversationManager conversationManager;
	private final MessagingManager messagingManager;
	private final PrivateMessageFactory pmFactory;

	private int messagesQueryAmount;

	@Inject
	public ConversationViewModel(
			EventBus eventBus,
			ConversationManager conversationManager,
			MessagingManager messagingManager,
			PrivateMessageFactory privateMessageFactory
	)
	{
		super(eventBus);
		// todo a: right to call it in each
		// ...ViewModel extends EventListenerViewModel class?
		super.onInit();

		this.conversationManager = conversationManager;
		this.messagingManager = messagingManager;
		this.pmFactory = privateMessageFactory;

		messagesQueryAmount = 25;
	}


	public Collection<ConversationMessageHeader> getMessageHeaders(ContactId contactId)
			throws DbException
	{
		return conversationManager.getMessageHeaders(contactId);
	}

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

	public void write(ContactId contactId, String text)
			throws DbException, FormatException, InvalidMessageException
	{
		if (!text.isBlank())
		{
			GroupId groupId = messagingManager.getConversationId(contactId);

			PrivateMessage pm = pmFactory.createLegacyPrivateMessage(
					groupId,
					System.currentTimeMillis(),
					text);
			messagingManager.addLocalMessage(pm);
			// todo how to get/implement its status for displaying it in the messages area?
		} else
			throw new InvalidMessageException("Message text can't be blank");
	}

	// todo
	@Override
	public void eventOccurred(Event e)
	{
		if (e instanceof CloseSyncConnectionsEvent)
		{
			System.out.println("CloseSyncConnectionsEvent");
		}
		else if (e instanceof GroupAddedEvent)
		{
			System.out.println("GroupAddedEvent");

		}
		else if (e instanceof GroupRemovedEvent)
		{
			System.out.println("GroupRemovedEvent");

		}
		else if (e instanceof GroupVisibilityUpdatedEvent)
		{
			System.out.println("GroupVisibilityUpdatedEvent");

		}
		else if (e instanceof MessageAddedEvent)
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
		else if (e instanceof SyncVersionsUpdatedEvent)
		{
			System.out.println("SyncVersionsUpdatedEvent");

		}
	}
}

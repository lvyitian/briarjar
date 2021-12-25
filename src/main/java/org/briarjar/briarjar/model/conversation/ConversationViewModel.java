package org.briarjar.briarjar.model.conversation;

import org.briarjar.briarjar.Experimental;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.InvalidMessageException;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageFactory;

import java.util.Collection;
import java.util.stream.Stream;

import javax.inject.Inject;


@NotNullByDefault
public class ConversationViewModel {

	private final ConversationManager conversationManager;
	private final MessagingManager messagingManager;
	private final PrivateMessageFactory pmFactory;

	private int messagesQueryAmount;

	@Inject
	public ConversationViewModel(
			ConversationManager conversationManager,
			MessagingManager messagingManager,
			PrivateMessageFactory privateMessageFactory
	) {
		this.conversationManager = conversationManager;
		this.messagingManager = messagingManager;
		this.pmFactory = privateMessageFactory;

		messagesQueryAmount = 25;
	}





	public Collection<ConversationMessageHeader> getMessageHeaders(
			ContactId contactId) throws DbException
	{
		 return conversationManager.getMessageHeaders(contactId);
	}

	@Experimental
	public Stream<ConversationMessageHeader>
			getMoreMessages(ContactId contactId) throws DbException {
		// todo a: implement further messages loading; sorting?
		return getMessageHeaders(contactId).stream().limit(messagesQueryAmount);
	}

	public void setMessagesQueryAmount(int amount) throws Exception {
		int min = 4;
		int max = 50;
		if (amount >= min && amount <= max)
			this.messagesQueryAmount = amount;
		else
			throw new Exception("setAmountOfMessagesPerGet must be between "+
					min+" and "+max+"!");
	}

	public void write(ContactId contactId, String text)
			throws DbException, FormatException, InvalidMessageException {
		if (!text.isBlank()) {
			GroupId groupId = messagingManager.getConversationId(contactId);

			PrivateMessage pm =	pmFactory.createLegacyPrivateMessage(
									groupId,
									System.currentTimeMillis(),
									text);
			messagingManager.addLocalMessage(pm);
			// todo how to get/implement its status for displaying it in the messages area?
		} else
			throw new InvalidMessageException("Message text can't be blank");
	}
}

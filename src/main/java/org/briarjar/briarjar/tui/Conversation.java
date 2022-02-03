package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;

import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;

import javax.inject.Inject;

import okhttp3.internal.annotations.EverythingIsNonNull;

public class PrivateChat {

	private Panel contentPanel, chatPanel, messageBoxPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	private ConversationViewModel cvm;
	private Contact contact;

	@Inject
	public PrivateChat(ConversationViewModel cvm)
	{
		this.cvm = cvm;
	}

	public void createWindow()
	{
		TUIUtils.addTitle("Chat with " + contact.getAlias(), contentPanel);



			/* loop through all messages and add them to the chatPanel
			for(ConversationMessageHeader cmh : cvm.getMessageHeaders(contact.getId()))
			{
				chatPanel.addComponent(cmh);
			}
			 */
	}

	public void render()
	{
		contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
		chatPanel = new Panel();
		contentPanel.addComponent(chatPanel.withBorder(Borders.singleLine("Chat")));

		messageBoxPanel = new Panel();
		contentPanel.addComponent(messageBoxPanel.withBorder(Borders.singleLine("Write your Message...")));

		LinearLayout linearLayout = (LinearLayout) contentPanel.getLayoutManager();
		linearLayout.setSpacing(2);

		// init instance
		createWindow();

		this.window = new BasicWindow();
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Private Chat")));
		// render the window
		textGUI.addWindowAndWait(window);
	}

	/* SETTERS */

	public void setTuiUtils(TUIUtils tuiUtils)
	{
		this.tuiUtils = tuiUtils;
	}

	public void setTextGUI(MultiWindowTextGUI textGUI)
	{
		this.textGUI = textGUI;
	}

	public void setContact(Contact contact)
	{
		this.contact = contact;
	}
}

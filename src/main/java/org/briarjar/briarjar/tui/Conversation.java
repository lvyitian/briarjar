package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import org.briarjar.briarjar.model.viewmodels.ConversationViewModel;
import org.briarproject.bramble.api.contact.Contact;

import javax.inject.Inject;

public class Conversation {

	private Panel contentPanel, chatPanel, messageBoxPanel;
	private TextBox messageBox;
	private ActionListBox chatBox;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	private ConversationViewModel cvm;
	private Contact contact;

	@Inject
	public Conversation(ConversationViewModel cvm)
	{
		this.cvm = cvm;
	}

	public void createWindow()
	{
		TUIUtils.addTitle("Chat with " + contact.getAlias(), contentPanel);

		chatBox.addItem("> Hey there", () ->
				MessageDialog.showMessageDialog(textGUI, "Message MetaData", "TODO",
				MessageDialogButton.Close));
		chatBox.addItem("< How are you?", () ->
				MessageDialog.showMessageDialog(textGUI, "Message MetaData", "TODO",
						MessageDialogButton.Close));

		/* loop through all messages and add them to the chatPanel
			for(ConversationMessageHeader cmh : cvm.getMessageHeaders(contact.getId()))
			{
				chatPanel.addComponent(cmh);
			}
		*/


		chatPanel.addComponent(chatBox);
		messageBoxPanel.addComponent(messageBox.setLayoutData(BorderLayout.Location.CENTER));

		messageBoxPanel.addComponent(
				new Button("Send", () -> {
					if(!messageBox.getText().isEmpty())
					{
						try
						{
							cvm.write(contact.getId(), messageBox.getText());
							render();
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					else
						MessageDialog.showMessageDialog(textGUI, "Empty Messagebox", "Please write a message",
									MessageDialogButton.OK);
				}).setLayoutData(BorderLayout.Location.RIGHT));

		messageBoxPanel.addComponent(
				new Button("Back", () -> {
					try
					{
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}).setLayoutData(BorderLayout.Location.LEFT));
	}

	public void render()
	{
		contentPanel = new Panel(new BorderLayout());
		chatPanel = new Panel(new LinearLayout(Direction.VERTICAL));
		messageBoxPanel = new Panel(new BorderLayout());

		messageBox = new TextBox("");
		chatBox = new ActionListBox();

		contentPanel.addComponent(messageBoxPanel.withBorder(Borders.singleLine("Write your Message...")).setLayoutData(BorderLayout.Location.BOTTOM));
		contentPanel.addComponent(chatPanel.withBorder(Borders.singleLine("Chat")).setLayoutData(BorderLayout.Location.CENTER));

		// init instance
		createWindow();

		this.window = new BasicWindow("Private Conversation");
		window.setFixedSize(new TerminalSize(50, 20));
		window.setComponent(contentPanel.withBorder(Borders.singleLine()));

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

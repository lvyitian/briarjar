package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.ViewModelProvider;
import org.briarproject.bramble.api.db.DbException;

public class AddContact {

	private final ViewModelProvider viewModelProvider;
	private final Panel contentPanel;
	private final BasicWindow window;
	private Label errors;
	private final MultiWindowTextGUI textGUI;

	private String handshakeLinkOfFriend;

	public AddContact(ViewModelProvider viewModelProvider, MultiWindowTextGUI textGUI)
	{
		this.viewModelProvider = viewModelProvider;
		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		this.errors = new Label("");
		this.handshakeLinkOfFriend = "";
		this.textGUI = textGUI;
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();
	}

	private void createWindow() {
		TUIUtils.addTitle("Add a new Contact", contentPanel);

		try {
		String	link = viewModelProvider.getContactManager().getHandshakeLink();
		contentPanel.addComponent(
				new Button("Get your own Handshake-Link", () ->
						MessageDialog.showMessageDialog(textGUI, "Share your Handshanke-Link", link, MessageDialogButton.OK)
				));
		} catch (DbException e) {
			e.printStackTrace();
		}
		contentPanel.addComponent(
				new Button("Enter your friends Handshake-Link", () ->
						handshakeLinkOfFriend = TextInputDialog.showDialog(textGUI, "Enter Handshake-Link", "The Handshake-Link starts with briar://...", "")
		));

		TUIUtils.addHorizontalSeparator(contentPanel);
		contentPanel.addComponent(
				new Button("Add Contact", () -> {
					//viewModelProvider.getContactManager().addContact();
					TUIUtils.switchWindow(window,viewModelProvider,TUIWindow.CONTACTLIST);
				}));
		TUIUtils.addHorizontalSeparator(contentPanel);
		contentPanel.addComponent(errors);
		window.setComponent(contentPanel);
	}

	public void render()
	{
		// render the window
		textGUI.addWindowAndWait(window);
	}

}

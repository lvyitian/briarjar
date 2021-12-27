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
	private final MultiWindowTextGUI textGUI;
	private Label errors;

	private String handshakeLinkOfFriend;

	public AddContact(ViewModelProvider viewModelProvider, MultiWindowTextGUI textGUI)
	{
		this.viewModelProvider = viewModelProvider;
		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		this.errors = new Label("");
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
		String link = viewModelProvider.getContactManager().getHandshakeLink();
		contentPanel.addComponent(
				new Button("Get your own Handshake-Link", () ->
						MessageDialog.showMessageDialog(textGUI, "Share your Handshake-Link", link, MessageDialogButton.OK)
				));
		} catch (DbException e) {
			e.printStackTrace();
		}

		contentPanel.addComponent(
				new Button("Enter your friends Handshake-Link", () ->
						handshakeLinkOfFriend = TextInputDialog.showDialog(textGUI, "Enter Handshake-Link", "The Handshake-Link starts with briar://...", "")
		));

		contentPanel.addComponent(
				new Button("Start Handshake Process", () -> {
					// viewModelProvider.getContactManager().addContact();
					TUIUtils.switchWindow(window,viewModelProvider,TUIWindow.CONTACTLIST);
				}));

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Cancel", () -> {
					// viewModelProvider.getContactManager().addContact();
					TUIUtils.switchWindow(window,viewModelProvider,TUIWindow.CONTACTLIST);
				}));

		contentPanel.addComponent(errors);
		window.setComponent(contentPanel);
	}

	public void render()
	{
		// render the window
		textGUI.addWindowAndWait(window);
	}

}

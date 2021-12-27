package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarproject.bramble.api.db.DbException;

import javax.inject.Inject;

public class AddContact {

	private final Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private final ContactViewModel cvm;
	private TUIUtils tuiUtils;
	private Label errors;

	private String handshakeLinkOfFriend;

	@Inject
	public AddContact(ContactViewModel cvm)
	{
		this.cvm = cvm;
		this.errors = new Label("");
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();
	}

	private void createWindow() {
		TUIUtils.addTitle("Add a new Contact", contentPanel);

		try {
		String link = "TODO"; // cvm.getLink();         // FIXME exception w/ dagger @Injection of cvm
		contentPanel.addComponent(
				new Button("Get your own Handshake-Link", () ->
						MessageDialog.showMessageDialog(textGUI, "Share your Handshake-Link", link, MessageDialogButton.OK)
				));
		} catch (/*Db*/Exception e) {
			e.printStackTrace();
		}

		contentPanel.addComponent(
				new Button("Enter your friends Handshake-Link", () ->
						handshakeLinkOfFriend = TextInputDialog.showDialog(textGUI, "Enter Handshake-Link", "The Handshake-Link starts with briar://...", "")
		));

		contentPanel.addComponent(
				new Button("Start Handshake Process", () -> {
					// viewModelProvider.getContactManager().addContact();
					tuiUtils.switchWindow(window,TUIWindow.CONTACTLIST);
				}));

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Cancel", () -> {
					// viewModelProvider.getContactManager().addContact();
					tuiUtils.switchWindow(window,TUIWindow.CONTACTLIST);
				}));

		contentPanel.addComponent(errors);
	}

	public void render()
	{
		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		window.setComponent(contentPanel);
		// render the window
		textGUI.addWindowAndWait(window);
	}

	public void setTextGUI(MultiWindowTextGUI textGUI)
	{
		this.textGUI = textGUI;
	}

	public void setTuiUtils(TUIUtils tuiUtils)
	{
		this.tuiUtils = tuiUtils;
	}

}

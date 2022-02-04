package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.db.DbException;

import java.security.GeneralSecurityException;

import javax.inject.Inject;

public class AddContact {

	private Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private final ContactViewModel cvm;
	private TUIUtils tuiUtils;

	private String handshakeLinkOfFriend;
	private String aliasOfFriend;

	@Inject
	public AddContact(ContactViewModel cvm)
	{
		this.cvm = cvm;
		init();
	}

	/* INIT */

	private void init()
	{
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		handshakeLinkOfFriend = "";
		aliasOfFriend = "Bob";

		window = new BasicWindow("Contact Manager");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Add a new Contact")));
	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();

		try {
		String ownLink = cvm.getHandshakeLink();

		// contentPanel.addComponent(...)
		contentPanel.addComponent(
				new Button("Get your own Handshake-Link", () ->
						MessageDialog.showMessageDialog(textGUI, "Share your Handshake-Link", ownLink, MessageDialogButton.OK)
				));
			System.out.println(ownLink);
		} catch (/*Db*/Exception e) {
			e.printStackTrace();
		}

		contentPanel.addComponent(
				new Button("Enter your friends Handshake-Link", () ->
						handshakeLinkOfFriend = TextInputDialog.showDialog(textGUI, "Enter Handshake-Link", "The Handshake-Link starts with briar://...", "")
		));

		contentPanel.addComponent(
				new Button("Enter your friends Alias", () ->
						aliasOfFriend = TextInputDialog.showDialog(textGUI, "Enter Alias of Friend", "Choose how you want your Friend to be referred to", "Bob")
		));

		contentPanel.addComponent(
				new Button("Start Handshake Process", () -> {
					try
					{
						cvm.addPendingContact(handshakeLinkOfFriend, aliasOfFriend);
					} catch (GeneralSecurityException | FormatException | DbException | GeneralException e)
					{
						MessageDialog.showMessageDialog(textGUI,
								e.getClass().toString(), e.getMessage(),
								MessageDialogButton.OK);
					}
					tuiUtils.switchWindow(window,TUIWindow.CONTACTLIST);
				}));

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Cancel", () -> tuiUtils.switchWindow(window,TUIWindow.CONTACTLIST)));
	}

	/* PANELS REMOVER */

	private void removeAllComponents()
	{
		contentPanel.removeAllComponents();
	}

	/* RENDER */

	public void render()
	{
		createWindow();
		textGUI.addWindowAndWait(window);
	}

	/* SETTERS */

	public void setTextGUI(MultiWindowTextGUI textGUI)
	{
		this.textGUI = textGUI;
	}

	public void setTuiUtils(TUIUtils tuiUtils)
	{
		this.tuiUtils = tuiUtils;
	}

}

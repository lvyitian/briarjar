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

	private final ContactViewModel cvm;

	private Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	private String handshakeLinkOfFriend;

	@Inject
	public AddContact(ContactViewModel cvm)
	{
		this.cvm = cvm;
	}

	private void createWindow() {
		TUIUtils.addTitle("Add a new Contact", contentPanel);

		try {
			String link = cvm.getLink();
			contentPanel.addComponent(
					new Button("Get your own Handshake-Link", () ->
							MessageDialog.
									showMessageDialog(
											textGUI,
											"Share your Handshake-Link",
											link,
											MessageDialogButton.OK)
					));

			System.out.println("\nOwn Handshake Link: "+link+"\n");

		} catch (/*Db*/Exception e) {
			e.printStackTrace();
		}

		contentPanel.addComponent(
				new Button("Enter your friends Handshake-Link", () ->
						handshakeLinkOfFriend = TextInputDialog.showDialog(textGUI, "Enter Handshake-Link", "The Handshake-Link starts with briar://...", "")
		));

		contentPanel.addComponent(
				new Button("Start Handshake Process", () -> {
					try
					{
						cvm.addPendingContact(handshakeLinkOfFriend, "Bob-Test");
					} catch (GeneralSecurityException e)
					{
						e.printStackTrace();
					} catch (FormatException e)
					{
						e.printStackTrace();
					} catch (DbException e)
					{
						e.printStackTrace();
					} catch (GeneralException e)
					{
						e.printStackTrace();
					}
					tuiUtils.switchWindow(window,TUIWindow.CONTACTLIST);
				}));

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Cancel", () -> {
					// viewModelProvider.getContactManager().addContact();
					tuiUtils.switchWindow(window,TUIWindow.CONTACTLIST);
				}));
	}

	public void render()
	{
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();

		this.window = new BasicWindow("Add a new Contact");
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

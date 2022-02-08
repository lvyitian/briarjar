package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.inject.Inject;

public class AddContact {

	private Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private final ContactViewModel cvm;
	private TUIUtils tuiUtils;

	private String peerHandshakeLink;
	private String peerAlias;

	@Inject
	public AddContact(ContactViewModel cvm)
	{
		this.cvm = cvm;
		init();
	}

	/* INIT */

	private void init()
	{
		contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

		peerHandshakeLink = "";
		peerAlias = "Bob";

		window = new BasicWindow("Contact Manager");
		window.setComponent(contentPanel.withBorder(Borders.singleLine("Add a new contact")));
	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();

		try {
		String ownLink = cvm.getHandshakeLink();

		// contentPanel.addComponent(...)
		contentPanel.addComponent(
				new Button("Copy your handshake-link to clipboard", () ->
				{
					StringSelection selection = new StringSelection(ownLink);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(selection, selection);
					MessageDialog.showMessageDialog(textGUI, "Your handshake-link is in the clipboard", ownLink, MessageDialogButton.OK);
				}));

			System.out.println(ownLink);
		} catch (GeneralException e) {
			tuiUtils.show(e);
		}

		contentPanel.addComponent(
				new Button("Enter your friends handshake-link", () ->
						peerHandshakeLink = TextInputDialog.showDialog(textGUI, "Enter handshake-link", "The Handshake-Link starts with briar://...", "")
		));

		contentPanel.addComponent(
				new Button("Choose an alias for your friends", () ->
						peerAlias = TextInputDialog.showDialog(textGUI, "Enter alias of Friend", "Choose how you want your friend to be referred to", "Bob")
		));

		contentPanel.addComponent(
				new Button("Start handshaking", () -> {
					try
					{
						cvm.addPendingContact(peerHandshakeLink, peerAlias);
						tuiUtils.switchWindow(window,TUIWindow.CONTACTLIST);
					}
					catch (GeneralException e)
					{
						tuiUtils.show(e);
					}
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

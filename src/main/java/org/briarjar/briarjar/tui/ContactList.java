package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;

import org.briarjar.briarjar.Main;
import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;

public class ContactList {

	private final ContactViewModel cvm;
	private final LoginViewModel lvm;

	private Panel contentPanel;
	private BasicWindow window;
	private WindowBasedTextGUI textGUI;
	private TUIUtils tuiUtils;

	private ComboBox<ListedContact> contactAliasComboBox;

	@Inject
	public ContactList(LoginViewModel lvm,
	                   ContactViewModel cvm)
	{
		this.lvm = lvm;
		this.cvm = cvm;
	}

	private void createWindow() {
		TUIUtils.addTitle("Contact Selection", contentPanel);

		contentPanel.addComponent(
				new Button("Add a new Contact", () ->
						tuiUtils.switchWindow(window, TUIWindow.ADDCONTACT)));

		TUIUtils.addHorizontalSeparator(contentPanel);

		try {
			Collection<Contact> contactsCollection = cvm.getAcceptedContacts();
			ArrayList<ListedContact> listedContactList = new ArrayList<>(contactsCollection.size());
			if(contactsCollection.size() != 0) {
				for (Contact c : contactsCollection) {
					ListedContact lc = new ListedContact(c); // Contact --> ListedContact
					listedContactList.add(lc);
					contactAliasComboBox.addItem(lc);
				}
				contentPanel.addComponent(contactAliasComboBox);

				TUIUtils.addHorizontalSeparator(contentPanel);

				contentPanel.addComponent(
						new Button("Chat with selected Contact", () -> {
							try
							{
								tuiUtils.getConversation().setContact(contactAliasComboBox.getSelectedItem().getContact());
								tuiUtils.switchWindow(window, TUIWindow.CONVERSATION);
							} catch (Exception e)
							{
								e.printStackTrace();
							}
						}));

			}
			else
				contentPanel.addComponent(new Label("No Contacts found!"));
		} catch (DbException e) {
			e.printStackTrace();
		}

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Log Out", () -> {
					lvm.stop();

					try
					{
						window.getTextGUI().getScreen().close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}

					// relaunch app TODO is there a better solution?
					var briarJarApp = Main.launchApp();
					briarJarApp.getMainTUI().start();
				}));
	}

	public void render()
	{
		this.contactAliasComboBox = new ComboBox<>();
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();

		this.window = new BasicWindow("Select or Add a Contact");
		window.setComponent(contentPanel.withBorder(Borders.singleLine()));
		// render the window
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


/**
	A "Wrapper" for a Contact with two properties
	1. Only containing needed information for the UI
	2. A toString() which returns the contact alias
 */
class ListedContact
{
	private Contact contact;
	public ListedContact(Contact contact)
	{
		this.contact = contact;
	}

	@Override
	public String toString()
	{
		return contact.getAlias();
	}

	public Contact getContact()
	{
		return contact;
	}
}

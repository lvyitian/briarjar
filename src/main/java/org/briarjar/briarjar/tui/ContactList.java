package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;

import org.briarjar.briarjar.model.viewmodels.ContactViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;

import javax.inject.Inject;

public class ContactList {

	private Panel contentPanel;
	private BasicWindow window;
	private WindowBasedTextGUI textGUI;
	private final LoginViewModel lvm;
	private TUIUtils tuiUtils;
	private final ContactViewModel cvm;

	private ComboBox<String> contactAliasComboBox;

	@Inject
	public ContactList(LoginViewModel lvm, ContactViewModel cvm)
	{
		this.lvm = lvm;
		this.cvm = cvm;
	}

	private void createWindow() {
		TUIUtils.addTitle("Select or Add a Friend", contentPanel);

		contentPanel.addComponent(
				new Button("Add a new Contact", () ->
						tuiUtils.switchWindow(window, TUIWindow.ADDCONTACT)));

		TUIUtils.addHorizontalSeparator(contentPanel);

		try {
			if(cvm.getAcceptedContacts().size() != 0) {
				for (Contact c : cvm.getAcceptedContacts()
						) {
					contactAliasComboBox.addItem(c.getAlias());
				}
				contentPanel.addComponent(contactAliasComboBox);
			}
			else
				contentPanel.addComponent(new Label("No Contacts found!"));
		} catch (DbException e) {
			e.printStackTrace();
		}

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Log Out", () -> {
					System.out.println("pre stop: "+ lvm.getLifeCycleState());
					lvm.stop();
					System.out.println("post stop: " +lvm.getLifeCycleState());
					tuiUtils.switchWindow(window, TUIWindow.SIGNIN);
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

package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;

import org.briarjar.briarjar.model.ViewModelProvider;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;

public class ContactList {

	private final ViewModelProvider viewModelProvider;
	private final Panel contentPanel;
	private final BasicWindow window;
	private final WindowBasedTextGUI textGUI;
	private Label errors;

	private final ComboBox<String> contactAliasComboBox;

	public ContactList(ViewModelProvider viewModelProvider, MultiWindowTextGUI textGUI)
	{
		this.viewModelProvider = viewModelProvider;
		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		this.textGUI = textGUI;
		this.errors = new Label("");
		this.contactAliasComboBox = new ComboBox<>();

		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();
	}

	private void createWindow() {
		TUIUtils.addTitle("Select or Add a Friend", contentPanel);

		contentPanel.addComponent(
				new Button("Add a new Contact", () ->
						TUIUtils.switchWindow(window, viewModelProvider, TUIWindow.ADDCONTACT)));

		TUIUtils.addHorizontalSeparator(contentPanel);

		try {
			if(viewModelProvider.getContactManager().getContacts().size() != 0) {
				for (Contact c : viewModelProvider.getContactManager()
						.getContacts()) {
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
					viewModelProvider.getLoginViewModel().stop();
					TUIUtils.switchWindow(window, viewModelProvider, TUIWindow.SIGNIN);
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

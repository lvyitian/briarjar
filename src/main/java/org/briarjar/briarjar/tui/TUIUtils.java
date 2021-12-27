package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.*;

import org.briarjar.briarjar.model.ViewModelProvider;

public class TUIUtils {

	public static void addTitle(String title, Panel contentPanel) {
		Label label = new Label(title).addStyle(SGR.BOLD);
		contentPanel.addComponent(label);
		addHorizontalSeparator(contentPanel);
	}

	public static void addHorizontalSeparator(Panel contentPanel) {
		contentPanel.addComponent(
				new Separator(Direction.HORIZONTAL)
						.setLayoutData(
								GridLayout.createHorizontallyFilledLayoutData(1)));


	}

	public static void switchWindow(Window window, ViewModelProvider viewModelProvider, TUIWindow tw)
	{
		MultiWindowTextGUI textGUI = (MultiWindowTextGUI) window.getTextGUI();
		window.close();
		switch (tw)
		{
			case CONTACTLIST -> {
				ContactList contactList =
						new ContactList(viewModelProvider, textGUI);
				contactList.render();
			}
			case SIGNIN -> {
				SignIn signIn =
						new SignIn(viewModelProvider, textGUI);
				signIn.render();
			}
			case SIGNUP -> {
				SignUp signUp =
						new SignUp(viewModelProvider, textGUI);
				signUp.render();
			}
			case ADDCONTACT -> {
				AddContact addContact =
						new AddContact(viewModelProvider, textGUI);
				addContact.render();
			}
		}
	}
}

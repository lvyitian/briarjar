package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.*;

import javax.inject.Inject;

public class TUIUtils {

	private final SignIn signIn;
	private final SignUp signUp;
	private final ContactList contactList;
	private final AddContact addContact;

	@Inject
	public TUIUtils(SignIn signIn, SignUp signUp, ContactList contactList, AddContact addContact)
	{
		this.signIn = signIn;
			signIn.setTuiUtils(this);
		this.signUp = signUp;
			signUp.setTuiUtils(this);
		this.contactList = contactList;
			contactList.setTuiUtils(this);
		this.addContact = addContact;
			addContact.setTuiUtils(this);
	}

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

	public static void clearScreen(Window window)
	{
		window.getTextGUI().getScreen().clear();
	}

	/**
		Closes current window and opens another.
	 */
	public void switchWindow(Window currentWindow, TUIWindow tw)
	{
		// MultiWindowTextGUI textGUI = (MultiWindowTextGUI) window.getTextGUI();
		clearScreen(currentWindow);
		currentWindow.close();
		switch (tw)
		{
			case CONTACTLIST -> contactList.render();
			case SIGNIN -> signIn.render();
			case SIGNUP -> signUp.render();
			case ADDCONTACT -> addContact.render();
		}
	}

	/**
		Open a new window without closing current one (used in MainTUI).
	 */
	public void switchWindow(TUIWindow tw)
	{
		switch (tw)
		{
			case CONTACTLIST -> contactList.render();
			case SIGNIN -> signIn.render();
			case SIGNUP -> signUp.render();
			case ADDCONTACT -> addContact.render();
		}
	}

	// Getters

	public SignIn getSignIn()
	{
		return signIn;
	}

	public SignUp getSignUp()
	{
		return signUp;
	}

	public ContactList getContactList()
	{
		return contactList;
	}

	public AddContact getAddContact()
	{
		return addContact;
	}

}

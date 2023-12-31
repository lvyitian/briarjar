/*
 * BriarJar -- a GUI and TUI prototype for the messenger Briar.
 * Copyright (C) 2022 BriarJar Project Team
 *
 * This file is part of BriarJar.
 *
 * BriarJar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * BriarJar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BriarJar.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import org.briarjar.briarjar.GeneralException;

import javax.inject.Inject;

public class TUIUtils {

	private MultiWindowTextGUI textGUI;
	private TUIMessageDialog tuiMessageDialog;

	private final SignIn signIn;
	private final SignUp signUp;
	private final ContactList contactList;
	private final AddContact addContact;
	private final Conversation conversation;

	/**
	 * TUIUtils uses constructor injection by Dagger2 to instantiate
	 * the TUI window classes.
	 *
	 * TuiUtils will also be used in every window class to switch between windows.
	 *
	 * Since Dagger2 already creates these windows and the TUIUtils class, we cannot
	 * constructor-inject this instance of TUIUtils in those classes (cyclic DI graph).
	 *
	 * A simple setter is used instead - maybe this is a workaround-ish solution,
	 * but for now it suffices.
	 *
	 */
	@Inject
	public TUIUtils(SignIn signIn,
	                SignUp signUp,
	                ContactList contactList,
	                AddContact addContact,
	                Conversation conversation) // TODO use Dagger to make it shorter
	{
		this.signIn = signIn;
			signIn.setTuiUtils(this);
		this.signUp = signUp;
			signUp.setTuiUtils(this);
		this.contactList = contactList;
			contactList.setTuiUtils(this);
		this.addContact = addContact;
			addContact.setTuiUtils(this);
		this.conversation = conversation;
			conversation.setTuiUtils(this);
	}

	public void setTextGUI( MultiWindowTextGUI textGUI )
	{
		this.textGUI = textGUI;
		setAllTextGui();
	}

	private void setAllTextGui()
	{
		//TODO not that beautiful here, but in the current structure it's a possibility
		tuiMessageDialog = new TUIMessageDialog( textGUI );
		signIn.setTextGUI(textGUI);
		signUp.setTextGUI(textGUI);
		addContact.setTextGUI(textGUI);
		contactList.setTextGUI(textGUI);
		conversation.setTextGUI(textGUI);
	}

	public static void addHorizontalSeparator(Panel contentPanel) {
		contentPanel.addComponent(
				new Separator(Direction.HORIZONTAL)
						.setLayoutData(
								GridLayout.createHorizontallyFilledLayoutData(1)));
	}

	/**
	 *  Quick-fix to clear log-spam from UnixTerminal.
	 * @param window The window to clear.
	 * @implNote TO BE DELETED.
	 */
	public static void clearScreen(Window window)
	{
		window.getTextGUI().getScreen().clear();
	}

	/**
	 * Closes current window and opens another.
	 * @param currentWindow     The Window reference of the current window.
	 * @param tuiWindowToSwitch The TUIWindow enum of the desired window to switch.
	 */
	public void switchWindow(Window currentWindow, TUIWindow tuiWindowToSwitch)
	{
		// TODO remove clearScreen(..) when log spam is fixed
		// clears logs spam
		clearScreen(currentWindow);
		// remove currentWindow from textGui
		currentWindow.getTextGUI().removeWindow(currentWindow);
		// switch to desired window
		switchWindow(tuiWindowToSwitch);
	}

	/**
	 *
	 * Open a new window without closing current one (used in MainTUI).
	 * @param tuiWindowToSwitch  The TUIWindow enum of the desired window to switch.
	 */
	public void switchWindow(TUIWindow tuiWindowToSwitch)
	{
		/*
		 *
		 * Every window class has a render() method.
		 *
		 * render() methods have the job to
		 * 1. create a window
		 * 2. add it to the textGui and wait
		 *
		 *  render() methods are used to switch windows.
		 */
		switch (tuiWindowToSwitch)
		{
			case SIGNUP -> signUp.render();
			case SIGNIN -> signIn.render();
			case CONTACTLIST -> contactList.render();
			case ADDCONTACT -> addContact.render();
			case CONVERSATION -> conversation.render();
		}
	}

	public void show( GeneralException e, MessageDialogButton... buttons )
	{
		tuiMessageDialog.show( e, buttons );
	}


	/* GETTERS */

	/**
	 * TUIUtils offers us getters to the windows!
	 */

	// public SignIn getSignIn() { return signIn; }

	public SignUp getSignUp()
	{
		return signUp;
	}

	public ContactList getContactList()
	{
		return contactList;
	}

	// public AddContact getAddContact() { return addContact; }

	public Conversation getConversation() { return conversation; }

}

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
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.GeneralException;
import org.briarjar.briarjar.viewmodel.EventListenerViewModel;
import org.briarjar.briarjar.viewmodel.LoginViewModel;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.inject.Inject;

import static com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog;

public class SignIn extends EventListenerViewModel {

	private final LoginViewModel lvm;

	private Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private TUIUtils tuiUtils;

	@Inject
	public SignIn( EventBus           eventBus,
	               LoginViewModel     lvm )
	{
		super(eventBus);

		this.lvm = lvm;

		init();
	}

	/* INIT */

	private void init()
	{
		contentPanel = new Panel();
		window = new BasicWindow();
		window.setComponent(contentPanel);
	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();

		enterPassphrase();

		/* TODO uncomment after delete logic is implemented
		contentPanel.addComponent(
				new Button("Delete Account", () -> {
					lvm.deleteAccount();
					tuiUtils.switchWindow(window, TUIWindow.SIGNUP);
				})
		);
		 */
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

	/* PASSPHRASE DIALOG */

	private void enterPassphrase()
	{
		boolean repeatDialog = true;

		while(repeatDialog)
		{
			String passphrase = TextInputDialog.showPasswordDialog(textGUI,
					"Enter passphrase",
					"Enter your account passphrase", "");

			if ( passphrase == null ) {
				System.exit(0);
			}

			try {
				lvm.signIn( passphrase );
				repeatDialog = false;
				tuiUtils.switchWindow( TUIWindow.CONTACTLIST );

			} catch ( GeneralException e )
			{
				showMessageDialog( textGUI,
						e.getTitle(),
						e.getMessage(),
						MessageDialogButton.OK );
			}
		}
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

	/* EVENT HANDLING */

	@Override
	@NotNullByDefault
	public void
	       eventOccurred( Event e )
	{
		/*
		BRAMBLE-API -------------------------

		ContactAddedEvent
		ContactAliasChangedEvent
		ContactRemovedEvent
		ContactVerifiedEvent
		PendingContactAddedEvent
		PendingContactRemovedEvent
		PendingContactStateChangedEvent

		IdentityAddedEvent
		IdentityRemovedEvent

		KeyAgreementAbortedEvent
		KeyAgreementFailedEvent
		KeyAgreementFinishedEvent
		KeyAgreementListeningEvent
		KeyAgreementStartedEvent
		KeyAgreementStoppedListeningEvent
		KeyAgreementWaitingEvent

		NetworkStatusEvent

		ConnectionClosedEvent
		ConnectionOpenedEvent
		ContactConnectedEvent
		ContactDisconnectedEvent

		RendezvousConnectionClosedEvent
		RendezvousConnectionOpenedEvent
		RendezvousPollEvent

		SettingsUpdatedEvent

		MessageAddedEvent
		MessageRequestedEvent
		MessagesAckedEvent
		MessageSharedEvent
		MessagesSentEvent
		MessageStateChangedEvent
		MessageToAckEvent
		MessageToRequestEvent


		BRIAR-API ---------------------------

		ConversationMessagesDeletedEvent

		ConversationMessageReceivedEvent

		IntroductionAbortedEvent
		IntroductionRequestReceivedEvent
		IntroductionResponseReceivedEvent

		AttachmentReceivedEvent
		PrivateMessageReceivedEvent
		*/

	}
}
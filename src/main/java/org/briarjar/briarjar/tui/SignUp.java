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
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.GeneralException;
import org.briarjar.briarjar.viewmodel.EventListenerViewModel;
import org.briarjar.briarjar.viewmodel.LoginViewModel;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.inject.Inject;


public class SignUp extends EventListenerViewModel {

	private final LoginViewModel lvm;

	private Panel contentPanel;
	private TUIUtils tuiUtils;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private String username;
	private String passphrase;

	@Inject
	public SignUp ( EventBus           eventBus,
			        LoginViewModel     lvm )
	{
		super(eventBus);

		this.lvm = lvm;

		init();
	}

	/* INIT */

	private void init()
	{
		contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		window.setComponent(
				contentPanel.withBorder(Borders.singleLine("Please create an account")));
	}

	/* CREATE WINDOW */

	private void createWindow() {
		removeAllComponents();

		// contentPanel.addComponent(...)
		contentPanel.addComponent(
				new Button("Choose username", () ->
						username = TextInputDialog.showDialog(textGUI, "Choose a username", "No account has been found, please choose a username.", "alice")
				));

		contentPanel.addComponent(
				new Button("Choose passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Choose passphrase", "Choose a strong passphrase, which will be used to decrypt your account.", "")
		));

		// passphraseStrength = lvm.calcPassphraseStrength(passphrase);

		contentPanel.addComponent(
				new Button("Sign up", () -> {
					try {
						lvm.signUp(username, passphrase);
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
					} catch (GeneralException e) {
						tuiUtils.show(e);
					}
				}));
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

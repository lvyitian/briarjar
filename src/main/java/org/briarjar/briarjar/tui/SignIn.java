package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.ViewModelProvider;
import org.briarproject.bramble.api.crypto.DecryptionException;

public class SignIn {

	private final ViewModelProvider viewModelProvider;
	private final Panel contentPanel;
	private final BasicWindow window;
	private final MultiWindowTextGUI textGUI;
	private Label errors;

	private String passphrase;

	public SignIn(ViewModelProvider viewModelProvider, MultiWindowTextGUI textGUI)
	{
		this.viewModelProvider = viewModelProvider;
		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
		this.errors = new Label("");
		this.textGUI = textGUI;

		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();
	}

	private void createWindow() {
		TUIUtils.addTitle("Please Sign In with your Account", contentPanel);
		contentPanel.addComponent(
				new Button("Enter Passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Enter Passphrase", "Enter your account passphrase", ""))
		);

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Sign In", () -> {

					try {
						viewModelProvider.getLoginViewModel().signIn(passphrase);
					} catch (DecryptionException e) {
						errors = new Label(e.getMessage());
					}
					try {
						viewModelProvider.getLoginViewModel().start();
					} catch (InterruptedException e) {
						errors = new Label(e.getMessage());
					}
					TUIUtils.switchWindow(window, viewModelProvider, TUIWindow.CONTACTLIST);
				}));

		TUIUtils.addHorizontalSeparator(contentPanel);

		contentPanel.addComponent(
				new Button("Delete Account", () -> {
					// TODO delete account !!
					TUIUtils.switchWindow(window, viewModelProvider, TUIWindow.SIGNUP);
				})
		);

		contentPanel.addComponent(errors);
		window.setComponent(contentPanel);
	}

	public void render()
	{
		// render the window
		textGUI.addWindowAndWait(window);
	}
}

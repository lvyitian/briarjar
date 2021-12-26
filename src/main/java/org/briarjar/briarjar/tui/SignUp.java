package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.ViewModelProvider;

public class SignUp {

	private final ViewModelProvider viewModelProvider;
	private final Panel contentPanel;
	private final BasicWindow window;
	private Label errors;
	private final MultiWindowTextGUI textGUI;

	private String username;
	private String passphrase;

	public SignUp(ViewModelProvider viewModelProvider, MultiWindowTextGUI textGUI)
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
		TUIUtils.addTitle("Please Create an Account", contentPanel);

		contentPanel.addComponent(
				new Button("Enter Username", () ->
						username = TextInputDialog.showDialog(textGUI, "Choose a Username", "No account has been found, please choose a username.", "alice")
				));

		contentPanel.addComponent(
				new Button("Enter Passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Enter Passphrase", "Choose a strong passphrase, which will be used to decrypt your account", "")
		));

		// passphraseStrength = viewModelProvider.getLoginViewModel().calcPassphraseStrength(passphrase);

		contentPanel.addComponent(
				new Button("Sign Up", () -> {
					try {
						viewModelProvider.getLoginViewModel().signUp(username, passphrase);}
					catch (InterruptedException e) {
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
		contentPanel.addComponent(errors);
		window.setComponent(contentPanel);
	}

	public void render()
	{
		// render the window
		textGUI.addWindowAndWait(window);
	}
}

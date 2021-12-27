package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import javax.inject.Inject;

public class SignUp {

	private final Panel contentPanel;
	private TUIUtils tuiUtils;
	private BasicWindow window;
	private Label errors;
	private MultiWindowTextGUI textGUI;
	private final LoginViewModel lvm;
	private String username;
	private String passphrase;

	@Inject
	public SignUp(LoginViewModel lvm)
	{
		this.lvm = lvm;
		this.errors = new Label("");
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

		// passphraseStrength = lvm.calcPassphraseStrength(passphrase);

		contentPanel.addComponent(
				new Button("Sign Up", () -> {
					try {
						lvm.signUp(username, passphrase);}
					catch (InterruptedException e) {
						errors = new Label(e.getMessage());
					}
					try {
						lvm.start();
					} catch (InterruptedException e) {
						errors = new Label(e.getMessage());
					}
					tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
				}));
		TUIUtils.addHorizontalSeparator(contentPanel);
		contentPanel.addComponent(errors);
	}

	public void render()
	{
		this.window = new BasicWindow("Welcome to BriarJar TUI (development mode)");
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

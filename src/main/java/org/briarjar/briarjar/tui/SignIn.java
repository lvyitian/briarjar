package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.crypto.DecryptionException;

import javax.inject.Inject;

public class SignIn {

	private final Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private final LoginViewModel lvm;
	private TUIUtils tuiUtils;
	private Label errors;

	private String passphrase;

	@Inject
	public SignIn(LoginViewModel lvm)
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
		TUIUtils.addTitle("Please Sign In with your Account", contentPanel);
		contentPanel.addComponent(
				new Button("Enter Passphrase", () ->
						passphrase = TextInputDialog.showPasswordDialog(textGUI, "Enter Passphrase", "Enter your account passphrase", ""))
		);

		contentPanel.addComponent(
				new Button("Sign In", () -> {

					try {
						lvm.signIn(passphrase);
					} catch (DecryptionException e) {
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

		contentPanel.addComponent(
				new Button("Delete Account", () -> {
					// TODO delete account !!
					tuiUtils.switchWindow(window, TUIWindow.SIGNUP);
				})
		);

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

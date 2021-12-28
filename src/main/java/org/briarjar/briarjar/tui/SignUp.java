package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;

import javax.inject.Inject;

public class SignUp {

	private Panel contentPanel;
	private TUIUtils tuiUtils;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private final LoginViewModel lvm;
	private String username;
	private String passphrase;

	@Inject
	public SignUp(LoginViewModel lvm)
	{
		this.lvm = lvm;
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
						MessageDialog.showMessageDialog(textGUI, "InterruptedException occurred", e.getMessage(), MessageDialogButton.OK);
					}
					try {
						lvm.start();
					} catch (InterruptedException e) {
						MessageDialog.showMessageDialog(textGUI, "InterruptedException occurred", e.getMessage(), MessageDialogButton.OK);
					}
					if(lvm.getLifeCycleState() == LifecycleManager.LifecycleState.RUNNING)
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
				}));
		TUIUtils.addHorizontalSeparator(contentPanel);
	}

	public void render()
	{
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();

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

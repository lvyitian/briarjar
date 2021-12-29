package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;
import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;

import javax.inject.Inject;

public class SignIn {

	private Panel contentPanel;
	private BasicWindow window;
	private MultiWindowTextGUI textGUI;
	private final LoginViewModel lvm;
	private TUIUtils tuiUtils;

	private String passphrase;

	@Inject
	public SignIn(LoginViewModel lvm)
	{
		this.lvm = lvm;
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

						System.out.println("pre start: " +lvm.getLifeCycleState());
						lvm.start();
						System.out.println("post start: " +lvm.getLifeCycleState());
					} catch (DecryptionException e) {
						MessageDialog.showMessageDialog(textGUI, "DecryptionException occurred", e.getDecryptionResult().toString(), MessageDialogButton.OK);
					} catch (InterruptedException e) {
						MessageDialog.showMessageDialog(textGUI, "InterruptedException occurred", e.getMessage(), MessageDialogButton.OK);
					}
					if(lvm.getLifeCycleState() == LifecycleManager.LifecycleState.RUNNING)
						tuiUtils.switchWindow(window, TUIWindow.CONTACTLIST);
				}));

		TUIUtils.addHorizontalSeparator(contentPanel);

		/*
		contentPanel.addComponent(
				new Button("Delete Account", () -> {
					lvm.deleteAccount();
					tuiUtils.switchWindow(window, TUIWindow.SIGNUP);
				})
		);
		 */
	}

	public void render()
	{
		contentPanel = new Panel(new GridLayout(1));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(2);

		// init instance
		createWindow();

		this.window = new BasicWindow("Welcome back to BriarJar TUI (development mode)");
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

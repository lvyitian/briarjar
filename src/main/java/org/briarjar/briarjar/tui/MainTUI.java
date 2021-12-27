package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainTUI {

	private final LoginViewModel lvm;

	@Inject
	public MainTUI(LoginViewModel lvm)
	{
		this.lvm = lvm;
		init();
	}

	public void start()
	{
		// Setup terminal and screen layers
		Terminal terminal;
		final TextBox tbUsername;
		try
		{
			terminal = new DefaultTerminalFactory().createTerminal();
			Screen screen = new TerminalScreen(terminal);
			screen.startScreen();

			// Create panel to hold components
			Panel panel = new Panel();
			panel.setLayoutManager(new GridLayout(2));


			final Label lblOutput = new Label("");

			if (!lvm.accountExists())
			{
				panel.addComponent(new Label("Username: "));
				tbUsername = new TextBox().addTo(panel);
			} else
				tbUsername = null;
			panel.addComponent(new Label("Passphrase: "));
			final TextBox tbPassphrase = new TextBox().addTo(panel);

			panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

			if (!lvm.accountExists())
			{
				new Button("Sign Up", () -> {
					try
					{
						lvm.signUp(tbUsername.getText(),tbPassphrase.getText());
					} catch (Exception e)
					{
						// TODO implement, check getText() for NULL
						System.out.println(e.getMessage());
					}

					/*
					loginViewModel.start();
					lblOutput.setText("Logging in...");
					 */
				}).addTo(panel);
			} else
			{
				new Button("Login", () -> {
					try
					{
						lvm.signIn(tbPassphrase.getText());
					} catch (Exception e)
					{
						// TODO implement, check getText() for NULL
						System.out.println(e.getMessage());
					}
					/*
					loginViewModel.start();
					lblOutput.setText("Logging in...");
					 */
				}).addTo(panel);
			}
			panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
			panel.addComponent(lblOutput);

			// Create window to hold the panel
			BasicWindow window = new BasicWindow();
			window.setTitle("BriarJar TUI");
			window.setComponent(panel);

			// Create "gui" with tui and start
			MultiWindowTextGUI gui =
					new MultiWindowTextGUI(screen,
					                       new DefaultWindowManager(),
					                       new EmptySpace(TextColor.ANSI.GREEN));
			gui.addWindowAndWait(window);

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void init()
	{
		System.out.println("===== BriarJar TUI Mode (development version) =====");
		System.out.println("JDK Version (java.version): "+System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): "+System.getProperty("java.runtime.version"));
		System.out.println("Operating System (os.name): " + System.getProperty("os.name"));
		System.out.println("==========================================");
	}

	public void stop()
	{
		lvm.stop();
	}
}

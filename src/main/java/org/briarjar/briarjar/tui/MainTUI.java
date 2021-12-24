package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import org.briarjar.briarjar.model.LoginViewModel;

import java.io.IOException;

public class MainTUI {

	public MainTUI()
	{
		init();
	}
	public void start(LoginViewModel loginViewModel)
	{
		// Setup terminal and screen layers
		Terminal terminal = null;
		final TextBox tbUsername;
		try {
			terminal = new DefaultTerminalFactory().createTerminal();
			Screen screen = new TerminalScreen(terminal);
			screen.startScreen();

			// Create panel to hold components
			Panel panel = new Panel();
			panel.setLayoutManager(new GridLayout(2));


			final Label lblOutput = new Label("");

			if(!loginViewModel.accountExists()) {
				panel.addComponent(new Label("Username: "));
				tbUsername = new TextBox().addTo(panel);
			}
			else
				tbUsername = null;
			panel.addComponent(new Label("Passphrase: "));
			final TextBox tbPassphrase = new TextBox().addTo(panel);

			panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

			if(!loginViewModel.accountExists()) {
				new Button("Sign Up", new Runnable() {
					@Override
					public void run() {
						loginViewModel.setUsername(tbUsername.getText());
						loginViewModel.setPassphrase(tbPassphrase.getText());
						loginViewModel.signUp();


						/**
						loginViewModel.start();
						lblOutput.setText("Logging in...");
						 */
					}
				}).addTo(panel);
			} else
			{
				new Button("Login", new Runnable() {
					@Override
					public void run() {
						loginViewModel.setPassphrase(tbPassphrase.getText());
						loginViewModel.signIn();

						/**
						loginViewModel.start();
						lblOutput.setText("Logging in...");
						 */
					}
				}).addTo(panel);
			}
			panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
			panel.addComponent(lblOutput);

			// Create window to hold the panel
			BasicWindow window = new BasicWindow();
			window.setTitle("BriarJar TUI");
			window.setComponent(panel);

			// Create "gui" with tui and start
			MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(
					TextColor.ANSI.GREEN));
			gui.addWindowAndWait(window);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init()
	{
		System.out.println("===== BriarJar TUI Mode (development version) =====");
		System.out.println("JDK Version (java.version): " + System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): " + System.getProperty("java.runtime.version"));
		System.out.println("Operating System (os.name): " + System.getProperty("os.name"));
		System.out.println("==========================================");
	}

	public void stop()
	{

	}
}

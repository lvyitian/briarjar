package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import org.briarjar.briarjar.model.ViewModelProvider;

import java.io.Closeable;
import java.io.IOException;

public class MainTUI implements Closeable {

	private Screen screen;

	private final ViewModelProvider viewModelProvider;

	public MainTUI(ViewModelProvider viewModelProvider) {
		init();
		this.viewModelProvider = viewModelProvider;
	}

	public void start() {
		DefaultTerminalFactory defaultTerminalFactory =
				new DefaultTerminalFactory();
		try {
			Screen screen = defaultTerminalFactory.createScreen();
			screen.startScreen();
		this.screen = screen;

		final MultiWindowTextGUI textGUI =
				new MultiWindowTextGUI(this.screen, new DefaultWindowManager(),
						new EmptySpace(
								TextColor.ANSI.GREEN_BRIGHT));

		// login or register
		if (viewModelProvider.getLoginViewModel().accountExists()) {
			SignIn signIn = new SignIn(viewModelProvider, textGUI);
			signIn.render();
		} else {
			SignUp signUp = new SignUp(viewModelProvider, textGUI);
			signUp.render();
		}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		System.out.println(
				"===== BriarJar TUI Mode (development version) =====");
		System.out.println("JDK Version (java.version): " +
				System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): " +
				System.getProperty("java.runtime.version"));
		System.out.println(
				"Operating System (os.name): " + System.getProperty("os.name"));
		System.out.println("==========================================");
	}

	public void stop() {
		viewModelProvider.getLoginViewModel().stop();
	}

	@Override
	public void close() throws IOException {
		stop();
		screen.stopScreen();
	}
}
package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainTUI {

	private final LoginViewModel lvm;
	private MultiWindowTextGUI textGUI;
	private final TUIUtils tuiUtils;

	private Screen screen;

	@Inject
	public MainTUI( LoginViewModel     lvm,
	                TUIUtils           tuiUtils            )
	{
		this.lvm = lvm;
		this.tuiUtils = tuiUtils;
		init();
	}

	public void start() {
		DefaultTerminalFactory defaultTerminalFactory =
				new DefaultTerminalFactory();

		try {
			Screen screen = defaultTerminalFactory.createScreen();

			screen.startScreen();
			this.screen = screen;

			textGUI = new MultiWindowTextGUI(this.screen, new DefaultWindowManager(),
							new EmptySpace(TextColor.ANSI.GREEN_BRIGHT));

			setAllTextGUI();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// login or register
		if (lvm.accountExists()) {
			tuiUtils.switchWindow(TUIWindow.SIGNIN);
		} else {
			tuiUtils.switchWindow(TUIWindow.SIGNUP);
		}
	}

	// TODO is there a better solution
	private void setAllTextGUI()
	{
		tuiUtils.setTextGUI(textGUI);
		tuiUtils.getSignIn().setTextGUI(textGUI);
		tuiUtils.getSignUp().setTextGUI(textGUI);
		tuiUtils.getAddContact().setTextGUI(textGUI);
		tuiUtils.getContactList().setTextGUI(textGUI);
		tuiUtils.getConversation().setTextGUI(textGUI);
	}

	public void init() {
		System.out.println("===== BriarJar TUI Mode (development version) =====");
		System.out.println("JDK Version (java.version): "+System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): "+System.getProperty("java.runtime.version"));
		System.out.println("Operating System (os.name): "+System.getProperty("os.name"));
		System.out.println("==========================================");
	}
}

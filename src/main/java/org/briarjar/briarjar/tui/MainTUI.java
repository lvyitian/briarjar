/*
 * BriarJar -- a GUI and TUI prototype for the messenger Briar.
 * Copyright (C) 2022 BriarJar Project Team
 *
 * This file is part of BriarJar.
 *
 * BriarJar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * BriarJar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BriarJar.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import org.briarjar.briarjar.viewmodel.LoginViewModel;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainTUI {

	private final LoginViewModel lvm;
	private MultiWindowTextGUI textGUI;
	private final TUIUtils tuiUtils;

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

			textGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
							new EmptySpace(TextColor.ANSI.GREEN_BRIGHT));
			setTextGUI();
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

	private void setTextGUI()
	{
		tuiUtils.setTextGUI(textGUI);
	}

	public void init() {
		System.out.println("===== BriarJar TUI Mode =====");
		System.out.println("JDK Version (java.version): "+System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): "+System.getProperty("java.runtime.version"));
		System.out.println("Operating System (os.name): "+System.getProperty("os.name"));
		System.out.println("==========================================");
	}
}

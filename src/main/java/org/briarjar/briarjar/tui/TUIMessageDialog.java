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

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import org.briarjar.briarjar.GeneralException;

@SuppressWarnings("ClassCanBeRecord")
public class TUIMessageDialog {

	private final MultiWindowTextGUI textGUI;


	public TUIMessageDialog( MultiWindowTextGUI textGUI )
	{
		this.textGUI = textGUI;
	}


	public void show( GeneralException       e,
	                  MessageDialogButton... buttons )
	{
		MessageDialogBuilder builder = new MessageDialogBuilder();

		if ( buttons.length == 0 )
			builder.addButton( MessageDialogButton.OK );

		for ( MessageDialogButton button: buttons )
			builder.addButton( button );

		builder.setTitle( e.getTitle() )
		       .setText( e.getMessage() )
		       .build()
		       .showDialog( textGUI );
	}
}

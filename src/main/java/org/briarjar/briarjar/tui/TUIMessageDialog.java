package org.briarjar.briarjar.tui;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import org.briarjar.briarjar.model.exceptions.GeneralException;

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

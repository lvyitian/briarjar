package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

@Singleton
public class RootBorderPane extends BorderPane
{
	private MenuBar menuBar;
	private Menu    mBriar, mChat, mContact, mInfo;
	private MenuItem
				miSignOut, miDeleteAccount, miExit, 		// mBriar
				miShowContactList, miIncludePendingContacts, miDeleteAllMessages, 						// mChat
				miAddContact, miRemoveContact, miChangeContactAlias, 	//mContact
				miAbout;				// miInfo
	
	private SignInGridPane signInGridPane;
	private SignUpGridPane signUpGridPane;
	private MessagesBorderPane    messagesBorderPane; // same again

	private final LoginViewModel lvm;
	private final LifeCycleViewModel lifeCycleViewModel;

	private GUIUtils guiUtils;

	@Inject
	public RootBorderPane( LoginViewModel lvm,
	                       LifeCycleViewModel lifeCycleViewModel )
	{
		this.lvm = lvm;
		this.lifeCycleViewModel = lifeCycleViewModel;
	}

	public void create()
	{
		initComponents();
		addComponents();
		addHandlers();
		disableComponents(true);
	}

	private void initComponents()
	{
		menuBar 			= new MenuBar();

		mBriar 				= new Menu("Briar");
		mChat 				= new Menu("Chat");
		mContact 			= new Menu("Contact");
		mInfo 				= new Menu("Info");

		miSignOut           = new MenuItem("Sign out");
		miDeleteAccount		= new MenuItem("Delete account");
		miExit 				= new MenuItem("Exit");
		miShowContactList 	= new MenuItem("Hide contact list"); // default
		miIncludePendingContacts = new MenuItem("Exclude pending contacts");
		miDeleteAllMessages = new MenuItem("Delete all messages");
		miAddContact        = new MenuItem("Add a new contact");
		miRemoveContact 	= new MenuItem("Remove this contact");
		miChangeContactAlias = new MenuItem("Change contact alias");
		miAbout 			= new MenuItem("About");

		signInGridPane      	= guiUtils.getSignInGridPane();
		signUpGridPane          = guiUtils.getSignUpGridPane();
		messagesBorderPane      = guiUtils.getMessagesBorderPane();
	}

	private void addComponents()
	{
		menuBar.getMenus().addAll(mBriar, mChat, mContact, mInfo);

								// TODO uncomment if sign out bug is fixed
		mBriar.getItems().addAll(/*miSignOut, */miDeleteAccount, miExit);
		mChat.getItems().addAll(miShowContactList, miIncludePendingContacts,
								new SeparatorMenuItem(), miDeleteAllMessages);
		mContact.getItems().addAll(miAddContact, miRemoveContact,
				miChangeContactAlias);
		mInfo.getItems().addAll(miAbout);


		setTop(menuBar);

		// login or register view
		if (lvm.accountExists())
			setCenter(signInGridPane);
		 else
			 setCenter(signUpGridPane);
	}
	
	private void addHandlers()
	{
		// menu: mBriar
		miSignOut.setOnAction(e -> signOut());
		miDeleteAccount.setOnAction(e -> accountDeletionDialog());
		miExit.setOnAction( event -> {
			System.out.println("STOPPING BriarJar GUI …");
			System.exit(0);
		} );

		// menu: mChat
		miShowContactList.setOnAction(e -> showContactList());
		miIncludePendingContacts.setOnAction(e -> includePendingContacts());
		miDeleteAllMessages.setOnAction(e -> messagesBorderPane.deleteAllMessagesDialog());
		
		// menu: mContact
		miAddContact.setOnAction(e -> addContact());
		miRemoveContact.setOnAction(e -> messagesBorderPane.contactRemovalDialog());
		miChangeContactAlias.setOnAction(e -> messagesBorderPane.changeContactAlias());
		
		// menu: mInfo
		miAbout.setOnAction(e -> about());

	}

	public void disableComponents(boolean disable)
	{
		miSignOut.setDisable(disable);
		mContact.setDisable(disable);
		mChat.setDisable(disable);
	}

	// ============================ menu: mBriar ============================


	// FIXME Sign Out functionality is too buggy - For now, it's out of the scope of this prototype
	private void signOut()
	{
		// this might be dangerous!
		try {
			lifeCycleViewModel.stop();
			guiUtils.switchToSignIn();
		} catch (GeneralException e) {
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}


	private void accountDeletionDialog()
	{
		JFXButton delete = new JFXButton("Delete account");
		JFXDialog dialog = guiUtils.showConfirmationDialog(
				"Deleting account",
				"Are you sure that you want to delete your account?\n\n" +
				"A deleted account can not be recovered without any " +
				"kind of file-system based backup or recovery approach.\n\n" +
				"When finished, BriarJar exits automatically and can be " +
				"started again manually",
						delete);
		delete.setOnAction(e -> {
			try
			{
				lvm.deleteAccount();
			} catch (Exception ex)
			{
				guiUtils.showMaterialDialog("Deleting account", ex.getMessage());
			}
		});

		dialog.show();
	}

	// ============================ menu: mChat ============================


	private void showContactList()
	{
		if(messagesBorderPane.isContactListVisible())
		{
			messagesBorderPane.showContactList(false);
			miShowContactList.setText("Show contact list");
		}
		else
		{
			messagesBorderPane.showContactList(true);
			miShowContactList.setText("Hide contact list");
		}
	}


	private void includePendingContacts()
	{
		if(messagesBorderPane.isIncludingPendingContacts())
		{
			messagesBorderPane.includePendingContacts(false);
			miIncludePendingContacts.setText("Include pending contacts");
		}
		else
		{
			messagesBorderPane.includePendingContacts(true);
			miIncludePendingContacts.setText("Exclude pending contacts");
		}
	}



	// ============================ menu: mContact ============================

	private void addContact()
	{
		AddContactDialog addContactDialog = guiUtils.getAddContactDialog();
		addContactDialog.create();      // !!
		addContactDialog.showAndWait();
	}
	
	// ============================ menu: mInfo ============================
	
	private void about()
	{
		guiUtils.showMaterialDialog("BriarJar GUI",
				"""
						This development build is the GUI prototype.
						Try out the TUI mode by starting the application with --tui or tui option.
						
						Briar (briarproject.org) is licensed under GPLv3.
						
						This project uses the following third-party libraries:
						- JFoenix (Apache v2, jfoenix.com)
						- TrayNotification by PlusHaze (MIT, github.com/PlusHaze)
						"""
						);
	}
	
	public void setGUIUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

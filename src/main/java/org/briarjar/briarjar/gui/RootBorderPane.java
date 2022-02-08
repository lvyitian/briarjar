package org.briarjar.briarjar.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;

import org.briarjar.briarjar.model.exceptions.GeneralException;
import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;
import org.briarjar.briarjar.model.viewmodels.LoginViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.BorderPane;

@Singleton
public class RootBorderPane extends BorderPane
{
	private MenuBar menuBar;
	private Menu    mBriar, mChat, mContact, mInfo;
	private MenuItem
				miSignOut, miDeleteAccount, miExit, 		// mBriar
				miShowContactList, miDeleteAllMessages, 						// mChat
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
		
		mBriar.getItems().addAll(miSignOut, miDeleteAccount, miExit);
		mChat.getItems().addAll(miShowContactList, new SeparatorMenuItem(),
								miDeleteAllMessages);
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
		miDeleteAccount.setDisable(disable);
	}

	// ============================ menu: mBriar ============================


	private void signOut()
	{
		// TODO check this - might be dangerous!
		try {
			lifeCycleViewModel.stop();
			guiUtils.switchToSignIn();
		} catch (GeneralException e) {
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	private void accountDeletionDialog()
	{
		BoxBlur blur = new BoxBlur(3, 3, 3);
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXDialog dialog =
				new JFXDialog(guiUtils.getRootStackPane(), dialogLayout,
						JFXDialog.DialogTransition.TOP);
		JFXButton remove = new JFXButton("Delete account");
		JFXButton cancel = new JFXButton("Cancel");

		remove.setOnAction(e -> {
			try
			{
				lvm.deleteAccount();
				guiUtils.switchToSignUp();
				dialog.close();
			} catch (Exception ex)
			{
				guiUtils.showMaterialDialog("Removing account", ex.getMessage());
			}
		});

		cancel.setOnAction(e -> dialog.close());

		dialogLayout.setActions(remove, cancel);
		dialogLayout.setHeading(new Label("Removing account"));
		dialogLayout.setBody(
				new Label("Are you sure you want to remove your account? This is permanent and can't be recovered."));
		dialog.show();

		dialog.setOnDialogClosed(
				(JFXDialogEvent event1) -> guiUtils.getRootBorderPane()
				                                   .setEffect(null));
		guiUtils.getRootBorderPane().setEffect(blur);
	}

	// ============================ menu: mChat ============================


	private void showContactList()
	{
		if(!messagesBorderPane.isContactListVisible())
		{
			messagesBorderPane.showContactList();
			miShowContactList.setText("Hide contact list");
		}
		else
		{
			messagesBorderPane.hideContactList();
			miShowContactList.setText("Show contact list");
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
						This development build is a GUI prototype.Try out the TUI Mode with --tui or tui option.
						Briar is licensed unser GPLv3.
						This project uses the following third-party libraries:
						- JFoenix (Apache V2, jfoenix.com)
						- TrayNotification by PlusHaze (MIT, github.com/PlusHaze)
						"""
						);
	}
	
	public void setGUIUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.Main;
import org.briarjar.briarjar.model.LoginViewModel;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class RootBorderPane extends BorderPane
{

	private MenuBar menuBar;
	private Menu    mBriar, mChat, mContact, mInfo;
	private MenuItem 	miToggleOnline, miDeleteAccount, miExit, 		// mBriar
				miShowContactList, 						// mChat
				miRemoveContact, miChangeContactDisplayName, 	//mContact
				miCheckForUpdates, miAbout;				// miInfo
	private ToolBar statusBar; 		// the bar at the bottom
	
	private LoginGridPane loginGridPane;      // maybe the wrong place (better/right in Main via Main(login)??)
	private MessagesBorderPane    messagesBorderPane; // same again

	private LoginViewModel loginViewModel;
	private ContactManager contactManager;
	
	public RootBorderPane(LoginViewModel loginViewModel, ContactManager contactManager)
	{
		this.loginViewModel = loginViewModel;
		this.contactManager = contactManager;

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


		miToggleOnline 		= new MenuItem("Go Offline");
		miDeleteAccount		= new MenuItem("Delete Account");
		miExit 				= new MenuItem("Exit");
		miShowContactList 	= new MenuItem("Show Contact List");
		miRemoveContact 	= new MenuItem("Remove this Contact");
		miChangeContactDisplayName = new MenuItem("Change Contact Display-Name");
		miCheckForUpdates 	= new MenuItem("Check for Updates");
		miAbout 			= new MenuItem("About");
		
		statusBar 			= new ToolBar();

		loginGridPane      	= new LoginGridPane(loginViewModel, this);
		messagesBorderPane = new MessagesBorderPane();


	}
	
	private void addComponents()
	{
		menuBar.getMenus().addAll(mBriar, mChat, mContact, mInfo);
		
		mBriar.getItems().addAll(miToggleOnline, miDeleteAccount, miExit);
		mChat.getItems().addAll(miShowContactList);
		mContact.getItems().addAll(miRemoveContact, miChangeContactDisplayName);
		mInfo.getItems().addAll(miCheckForUpdates, miAbout);
		
		statusBar.getItems().setAll(new Label("Ready to chat!"));
		
		setTop(menuBar);
		setCenter(loginGridPane);
		setBottom(statusBar);
	}
	
	private void addHandlers()
	{
		// menu: mBriar
		miToggleOnline.setOnAction(e -> toggleOnline());
		miDeleteAccount.setOnAction(e -> deleteAccount());
		miExit.setOnAction(e -> exit());
		
		// menu: mChat
		miShowContactList.setOnAction(e -> showContactList());
		
		// menu: mContact
		miRemoveContact.setOnAction(e -> removeContact());
		miChangeContactDisplayName.setOnAction(e -> changeContactDisplayName());
		
		// menu: mInfo
		miCheckForUpdates.setOnAction(e -> checkForUpdates());
		miAbout.setOnAction(e -> about());

	}
	
	private void disableComponents(boolean disable)
	{
		miToggleOnline.setDisable(disable);
		mContact.setDisable(disable);
		mChat.setDisable(disable);
		miDeleteAccount.setDisable(disable);
		/*
		if(loginViewModel.isRegistered())
			miDeleteAccount.setDisable(false);
		else
			miDeleteAccount.setDisable(true);
		 */
	}
	
	// ============================ logic ============================
	
	private void unimplemented()
	{
		MainGUI.showAlert(AlertType.INFORMATION, "This feature is not part of the prototype and unimplemented!");
	}

	public void switchToMainScene()
	{
		disableComponents(false);
		setCenter(messagesBorderPane);
	}

	public void switchToLogin()
	{
		disableComponents(true);
		setCenter(loginGridPane);
	}

	// ============================ menu: mBriar ============================


	private void toggleOnline()
	{
		// TODO check this - might be dangerous!
		try {
			if (loginViewModel.hasDbKey()) {
				loginViewModel.stop();
				miToggleOnline.setText("Go Online");
			}
			else
				loginViewModel.start();
				miToggleOnline.setText("Go Offline");
		} catch (Exception e) {
			MainGUI.showAlert(AlertType.ERROR, e.getMessage());
		}
	}

	private void deleteAccount()
	{
		Alert deletionAlert = new Alert(AlertType.WARNING, "Deleting an account is permanent. You will lose all contacts, messages, etc. forever! Are you sure?", ButtonType.YES, ButtonType.CANCEL);
		deletionAlert.setTitle("Delete account?");
		deletionAlert.setHeaderText(null);
		deletionAlert.showAndWait();
		
		if(deletionAlert.getResult() == ButtonType.YES)
		{
			if(loginViewModel.hasDbKey()) {
				loginViewModel.deleteAccount();
				loginGridPane = new LoginGridPane(loginViewModel,
						this); // find better way?
				switchToLogin();
			}
			else
				MainGUI.showAlert(AlertType.ERROR, "No DbKey");
			// TODO architectural changes... maybe remove the delete feat. completely?
		}
	}
	
	public void exit()
	{
		if(loginViewModel.hasDbKey()) {
			try {
				loginViewModel.stop();
			} catch (Exception e) {
				MainGUI.showAlert(AlertType.ERROR, e.getMessage());
			}
		}
		Platform.exit();
	}
	
	// ============================ menu: mChat ============================
	
	private void showContactList()
	{
		if(!messagesBorderPane.isContactListVisible())
		{
			messagesBorderPane.showContactList(contactManager);
			miShowContactList.setText("Hide Contact List");
		}
		else
		{
			messagesBorderPane.hideContactList();
			miShowContactList.setText("Show Contact List");
		}
	}
	
	// ============================ menu: mContact ============================
	
	private void removeContact()
	{
		unimplemented();
	}
	
	private void changeContactDisplayName()
	{
		unimplemented();
	}
	
	// ============================ menu: mInfo ============================

	private void checkForUpdates()
	{
		unimplemented();
	}
	
	private void about()
	{
		// showAlert(AlertType.INFORMATION, "Briar Desktop. This development build is a GUI prototype. Functionality is highly experimental.");
	}
}

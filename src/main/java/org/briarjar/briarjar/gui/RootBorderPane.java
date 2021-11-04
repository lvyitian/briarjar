package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.LoginViewModel;

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
	private MenuItem 	miLockApp, miToggleOnline, miDeleteAccount, miExit, 		// mBriar
				miShowContactList, 						// mChat
				miRemoveContact, miChangeContactDisplayName, 	//mContact
				miCheckForUpdates, miAbout;				// miInfo
	private ToolBar statusBar; 		// the bar at the bottom
	
	private LoginGridPane loginGridPane;      // maybe the wrong place (better/right in Main via Main(login)??)
	private MessagesBorderPane    messagesBorderPane; // same again

	private LoginViewModel loginViewModel;
	
	public RootBorderPane(LoginViewModel loginViewModel)
	{
		initComponents();
		addComponents();
		addHandlers();
		disableCompontents(true);
	}
	
	private void initComponents()
	{
		menuBar 			= new MenuBar();


		mBriar 				= new Menu("Briar");
		mChat 				= new Menu("Chat");
		mContact 			= new Menu("Contact");
		mInfo 				= new Menu("Info");


		miLockApp 			= new MenuItem("Lock App");
		miToggleOnline 		= new MenuItem("Go Offline");
		miDeleteAccount		= new MenuItem("Delete Account");
		miExit 				= new MenuItem("Exit");
		miShowContactList 	= new MenuItem("Show Contact List");
		miRemoveContact 	= new MenuItem("Remove this Contact");
		miChangeContactDisplayName = new MenuItem("Change Contact Display-Name");
		miCheckForUpdates 	= new MenuItem("Check for Updates");
		miAbout 			= new MenuItem("About");
		
		statusBar 			= new ToolBar();

		loginGridPane      	= new LoginGridPane();
		messagesBorderPane = new MessagesBorderPane();


	}
	
	private void addComponents()
	{
		menuBar.getMenus().addAll(mBriar, mChat, mContact, mInfo);
		
		mBriar.getItems().addAll(miLockApp, miToggleOnline, miDeleteAccount, miExit);
		mChat.getItems().addAll(miShowContactList);
		mContact.getItems().addAll(miRemoveContact, miChangeContactDisplayName);
		mInfo.getItems().addAll(miCheckForUpdates, miAbout);
		
		statusBar.getItems().setAll(new Label("Please log in!"));
		
		setTop(menuBar);
		setCenter(loginGridPane);
		setBottom(statusBar);
	}
	
	private void addHandlers()
	{
		// menu: mBriar
		miLockApp.setOnAction(e -> lockApp());
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
		
		// other handlers...
		loginGridPane.getPassphraseField().setOnKeyReleased(e -> enterPressed(e));
		loginGridPane.getBtSignInRegister().setOnAction(e -> login());

	}
	
	private void disableCompontents(boolean disable)
	{
		miLockApp.setDisable(disable);
		miToggleOnline.setDisable(disable);
		mContact.setDisable(disable);
		mChat.setDisable(disable);
		//if(isRegistered())	// disable ONLY if isRegistered
		//	miDeleteAccount.setDisable(true);
	}
	
	// ============================ logic ============================
	

	
	private Boolean login() {
		/*
		login logic
		*/
		return true;
	}
	
	private void switchToMessages() // TODO maybe this procedure should be part of the logingridpane?
	{
		// switch to MessagesBorderPane
	}
	
	/*
	 * SwitchToMessages if Enter Key is pressed
	 */
	private void enterPressed(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER)
			login();
	}
	
	// TODO Find better solution...
	private void reloadLoginGridPane()
	{
		// re-init
		// loginGridPane = new LoginGridPane(loginViewModel); // FIXME this has to be deleted
		// reload handlers too...
		loginGridPane.getPassphraseField().setOnKeyReleased(e -> enterPressed(e));
		loginGridPane.getBtSignInRegister().setOnAction(e -> switchToMessages());
		setCenter(loginGridPane);
	}
	
	private void unimplemented()
	{
		MainGUI.showAlert(AlertType.INFORMATION, "This feature is not part of the prototype and unimplemented!");
	}
	
	// ============================ menu: mBriar ============================
	
	private void lockApp()
	{
		disableCompontents(true);
		miDeleteAccount.setDisable(false);
		reloadLoginGridPane(); 		// test case: start briar, register and then "lock app"
		// TODO stop the lifecycle manager here
		setCenter(loginGridPane);

	}
	
	private void toggleOnline()
	{
		// toggles online state
		// also changes to text of the button miToggleOnline
	}
	
	private void deleteAccount()
	{
		Alert deletionAlert = new Alert(AlertType.WARNING, "Deleting an account is permanent. You will lose all contacts, messages, etc. forever! Are you sure?", ButtonType.YES, ButtonType.CANCEL);
		deletionAlert.setTitle("Delete account?");
		deletionAlert.setHeaderText(null);
		deletionAlert.showAndWait();
		
		if(deletionAlert.getResult() == ButtonType.YES)
		{
			// account deletion procedure
		}
	}
	
	public void exit()
	{
		// stop the lifecycle manager too
		Platform.exit();
	}
	
	// ============================ menu: mChat ============================
	
	private void showContactList()
	{
		if(!messagesBorderPane.isContactListVisible())
		{
			messagesBorderPane.showContactList();
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
		MainGUI.showAlert(AlertType.INFORMATION, "Briar Desktop. This development build is a GUI prototype. Functionality is highly experimental.");
	}
}

package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.LoginViewModel;

import javax.inject.Inject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class MainGUI extends Application
{
	private RootBorderPane rootBorderPane;
	private LoginViewModel loginViewModel;

	@Override
	public void start(Stage primaryStage) {
		try
		{
			rootBorderPane = new RootBorderPane(loginViewModel);
			System.out.println("RootBorderPane done");
			Scene sceneRoot = new Scene(rootBorderPane, 850, 560);
			System.out.println("SceneRoot done");
			//sceneRoot.getStylesheets().add(getClass().getResource("briar.css").toExternalForm());
			primaryStage.setScene(sceneRoot);
			System.out.println("setScene() done");
			primaryStage.setTitle("BriarJar (Development Version)");
			System.out.println("setTitle() done");
			//primaryStage.getIcons().add(new Image(getClass().getResource("briar-logo.png").toExternalForm()));

			primaryStage.show();
		}
		catch (Exception e)
		{
			MainGUI.showAlert(AlertType.ERROR, e.getMessage());
		}
	}

	public static void startGUI() {
		launch();
	}
	
	public static void showAlert(AlertType alertType, String message) {
		System.out.println(message);
		Alert alert = new  Alert(alertType, message, ButtonType.OK);
		alert.setHeaderText(null);
		alert.setHeight(350);
		alert.setTitle("Alert!");
		alert.showAndWait();
	}
	
	public void init()
	{

		// system test
		String systemVersion = System.getProperty("java.version"), requiredVersion = "16.0.1";
		String systemOS = System.getProperty("os.name");
		System.out.println("===== BriarJar (development version) =====");
		System.out.println("JDK Version (java.version): " + systemVersion);
		System.out.println("JRE Version (java.runtime.version): " + System.getProperty("java.runtime.version"));
	}

}

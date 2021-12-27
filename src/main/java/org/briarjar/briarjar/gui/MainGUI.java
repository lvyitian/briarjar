package org.briarjar.briarjar.gui;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

@Singleton
public class MainGUI extends Application {

	private final RootBorderPane rootBorderPane;

	@Inject
	public MainGUI(RootBorderPane rootBorderPane)
	{
		super();
		this.rootBorderPane = rootBorderPane;
	}

	public static void showAlert(AlertType alertType, String message)
	{
		System.out.println(message);
		Alert alert = new Alert(alertType, message, ButtonType.OK);
		alert.setHeaderText(null);
		alert.setHeight(350);
		alert.setTitle("BriarJar Message");
		alert.showAndWait();
	}

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			Scene sceneRoot = new Scene(rootBorderPane, 850, 560);
			// sceneRoot.getStylesheets().add(getClass().getResource("briar.css").toExternalForm());
			primaryStage.setScene(sceneRoot);
			primaryStage.setTitle("BriarJar (development version)");
			// primaryStage.getIcons().add(new Image(getClass().getResource("briar-logo.png").toExternalForm()));
			primaryStage.show();
		} catch (Exception e)
		{
			showAlert(AlertType.ERROR, e.getMessage());
		}
	}

	public void init()
	{
		System.out.println("===== BriarJar (development version) =====");
		System.out.println("JDK Version (java.version): "+System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): "+System.getProperty("java.runtime.version"));
		System.out.println("Operating System (os.name): "+System.getProperty("os.name"));
		System.out.println("==========================================");
	}

	@Override
	public void stop()
	{
		rootBorderPane.exit();
	}

}

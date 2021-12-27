package org.briarjar.briarjar.gui;
import org.briarjar.briarjar.model.ViewModelProvider;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

public class MainGUI extends Application
{
	private RootBorderPane rootBorderPane;

	final private ViewModelProvider viewModelProvider;

	public MainGUI (ViewModelProvider viewModelProvider)
	{
		super();
		this.viewModelProvider = viewModelProvider;
	}

	@Override
	public void start(Stage primaryStage) {
		try
		{
			rootBorderPane = new RootBorderPane(viewModelProvider);
			Scene sceneRoot = new Scene(rootBorderPane, 850, 560);
			// sceneRoot.getStylesheets().add(getClass().getResource("briar.css").toExternalForm());
			primaryStage.setScene(sceneRoot);
			primaryStage.setTitle("BriarJar (development version)");
			// primaryStage.getIcons().add(new Image(getClass().getResource("briar-logo.png").toExternalForm()));
			primaryStage.show();
		}
		catch (Exception e)
		{
			showAlert(AlertType.ERROR, e.getMessage());
		}
	}



	public void init()
	{
		System.out.println("===== BriarJar (development version) =====");
		System.out.println("JDK Version (java.version): " + System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): " + System.getProperty("java.runtime.version"));
		System.out.println("Operating System (os.name): " + System.getProperty("os.name"));
		System.out.println("==========================================");
	}

	@Override
	public void stop()
	{
		rootBorderPane.exit();
	}

}

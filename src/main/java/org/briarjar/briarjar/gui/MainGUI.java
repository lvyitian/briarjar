package org.briarjar.briarjar.gui;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static org.briarjar.briarjar.gui.GUIUtils.showAlert;

@Singleton
public class MainGUI extends Application {

	private final RootBorderPane rootBorderPane;
	private final GUIUtils guiUtils;

	@Inject
	public MainGUI(GUIUtils guiUtils)
	{
		super();
		this.guiUtils = guiUtils;
		this.rootBorderPane = guiUtils.getRootBorderPane();
	}

	@Override
	public void start(Stage primaryStage)
	{
		try
		{

			Scene sceneRoot = new Scene(rootBorderPane, 850, 560);
			// sceneRoot.getStylesheets().add(getClass().getResource("briar.css").toExternalForm());
			primaryStage.setScene(sceneRoot);
			primaryStage.setTitle("BriarJar GUI Mode (development version)");
			String obj = Objects.requireNonNull(
					getClass().getResource("/briar-icon.png")).toExternalForm();
			primaryStage.getIcons().add(new Image(obj));
			primaryStage.show();
		} catch (Exception e)
		{
			showAlert(AlertType.ERROR, e.getMessage());
		}
	}

	public void init()
	{
		System.out.println("===== BriarJar GUI Mode (development version) GUI mode =====");
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

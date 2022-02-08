package org.briarjar.briarjar.gui;

import org.briarjar.briarjar.model.viewmodels.LifeCycleViewModel;

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

	private static Stage primaryStage;
	private final GUIUtils guiUtils;

	@Inject
	public MainGUI(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			primaryStage.setOnCloseRequest( event -> stop() );

			MainGUI.primaryStage = primaryStage;
			Scene sceneRoot = new Scene(guiUtils.getRootBorderPane(), 850, 560);
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
		System.out.println("STOPPING BriarJar GUI …");
		System.exit(0);
	}

	public static Stage getPrimaryStage()
	{
		return primaryStage;
	}
}

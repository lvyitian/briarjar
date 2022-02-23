package org.briarjar.briarjar.gui;

import com.sun.javafx.runtime.VersionInfo;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
			Scene sceneRoot = new Scene(guiUtils.getRootStackPane(), 850, 560);
			primaryStage.setScene(sceneRoot);
			primaryStage.setTitle("BriarJar GUI Mode (development version)");
			String img = Objects.requireNonNull(
					getClass().getResource("/images/briar-icon.png")).toExternalForm();
			primaryStage.getIcons().add(new Image(img));
			String css = Objects.requireNonNull(
					getClass().getResource("/application.css")).toExternalForm();
			sceneRoot.getStylesheets().add(css);
			primaryStage.show();

		} catch (Exception e)
		{
			guiUtils.showMaterialDialog("Error", e.getMessage());
		}
	}

	public void init()
	{
		System.out.println("===== BriarJar GUI Mode (development version) GUI mode =====");
		System.out.println("JDK Version (java.version): "+ System.getProperty("java.version"));
		System.out.println("JRE Version (java.runtime.version): "+ System.getProperty("java.runtime.version"));
		System.out.println(("JavaFX Version: " + VersionInfo.getVersion()));
		System.out.println(("JavaFX Runtime Version: " + VersionInfo.getRuntimeVersion()));
		System.out.println("Operating System (os.name): "+ System.getProperty("os.name"));
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

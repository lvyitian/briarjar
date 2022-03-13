/*
 * BriarJar -- a GUI and TUI prototype for the messenger Briar.
 * Copyright (C) 2022 BriarJar Project Team
 *
 * This file is part of BriarJar.
 *
 * BriarJar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * BriarJar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BriarJar.  If not, see <https://www.gnu.org/licenses/>.
 */

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
			primaryStage.setTitle("BriarJar GUI Mode");
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
		System.out.println("===== BriarJar GUI Mode =====");
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
		System.exit(0);
	}

	public static Stage getPrimaryStage()
	{
		return primaryStage;
	}
}

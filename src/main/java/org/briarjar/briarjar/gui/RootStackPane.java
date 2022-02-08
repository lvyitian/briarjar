package org.briarjar.briarjar.gui;

import javafx.scene.layout.StackPane;

public class RootStackPane extends StackPane {
	public RootStackPane(RootBorderPane rootBorderPane)
	{
		getChildren().add(rootBorderPane);
	}
}

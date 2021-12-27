package org.briarjar.briarjar.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class GUIUtils {
	public static void showAlert(Alert.AlertType alertType, String message) {
		System.out.println(message);
		Alert alert = new  Alert(alertType, message, ButtonType.OK);
		alert.setHeaderText(null);
		alert.setHeight(350);
		alert.setTitle("BriarJar Message");
		alert.showAndWait();
	}
}

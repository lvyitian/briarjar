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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import org.briarjar.briarjar.GeneralException;
import org.briarjar.briarjar.viewmodel.LoginViewModel;

import javax.inject.Inject;

import javafx.geometry.HPos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class SignUpGridPane extends GridPane {

	private final LoginViewModel lvm;

	private ImageView imgWelcome;
	private Text txtWelcome;
	private Text txtSubtext;
	private JFXTextField tfUsername;
	private JFXPasswordField passphraseField;
	private JFXButton btSignUp;
	private GUIUtils guiUtils;

	@Inject
	public SignUpGridPane( LoginViewModel     lvm )
	{
		this.lvm = lvm;
	}

	public void create()
	{
		initComponents();
		addComponents();
		addHandlers();
	}

	private void initComponents()
	{
		guiUtils.initWelcomeGridPane(this);
		imgWelcome = guiUtils.initBriarLogo();

		txtWelcome = new Text("Welcome to BriarJar!");
		txtWelcome.setFont(Font.font("Arial", FontWeight.LIGHT, 20));
		txtSubtext = new Text("Please create an account.");
		txtSubtext.setFont(Font.font("Arial", FontWeight.LIGHT, 15));
		setHalignment(txtWelcome, HPos.CENTER);

		tfUsername = new JFXTextField("");
		tfUsername.setPromptText("Enter a username");
		passphraseField = new JFXPasswordField();
		passphraseField.setMinSize(235d, USE_COMPUTED_SIZE);
		passphraseField.setPromptText("Enter a passphrase  (min. 15 chars)");

		btSignUp = new JFXButton("Sign up");
		btSignUp.setDisable(true);
	}

	private void addComponents()
	{
		// TODO: don't skip rows, instead correct the heights!
		add(imgWelcome, 0, 0);
		setHalignment(imgWelcome, HPos.CENTER);
		add(txtWelcome, 0, 2);
		add(txtSubtext, 0, 3);
		add(tfUsername, 0, 4);
		add(passphraseField, 0, 5);
		add(btSignUp, 0, 6);
	}


	private void addHandlers()
	{
		btSignUp.setOnAction(e -> signUp());
		passphraseField.setOnKeyTyped(e -> passphraseStrength());
		tfUsername.setOnKeyReleased(this::switchToPassphrase);
		passphraseField.setOnKeyReleased(this::switchToMain);

	}

	private void signUp()
	{
		try
		{
			lvm.signUp(tfUsername.getText(), passphraseField.getText());
			btSignUp.setDisable(true);
			guiUtils.switchToMain();
		} catch (GeneralException e)
		{
			guiUtils.showMaterialDialog(e.getTitle(), e.getMessage());
		}
	}

	private void passphraseStrength()
	{
		String pw = passphraseField.getText();
		float strength = lvm.getPassphraseStrength(pw);
		if(strength < 0.25)
			passphraseField.setFocusColor(Color.DARKRED);
		else if (strength < 0.5)
			passphraseField.setFocusColor(Color.RED);
		else if (strength < 0.75)
			passphraseField.setFocusColor(Color.ORANGE);
		else if (strength < 1)
			passphraseField.setFocusColor(Color.YELLOW);
		else
			passphraseField.setFocusColor(Color.LIMEGREEN);

		btSignUp.setDisable(pw.length() < LoginViewModel.MIN_PASSPHRASE_LENGTH);
	}

	private void switchToPassphrase(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
			passphraseField.requestFocus();
	}

	private void switchToMain(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
		{
			signUp();
		}
	}

	public void setGUIUtils(GUIUtils guiUtils)
	{
		this.guiUtils = guiUtils;
	}
}

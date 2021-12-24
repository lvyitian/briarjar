package org.briarjar.briarjar.model;

import org.briarproject.bramble.api.contact.ContactManager;

import javax.inject.Inject;

public class ViewModelProvider {

	private LoginViewModel loginViewModel;
	private ContactManager contactManager;
	@Inject
	public ViewModelProvider (LoginViewModel loginViewModel, ContactManager contactManager)
	{
		this.loginViewModel = loginViewModel;
		this.contactManager = contactManager;
	}

	public LoginViewModel getLoginViewModel() {
		return loginViewModel;
	}

	public ContactManager getContactManager()
	{
		return  contactManager;
	}
}
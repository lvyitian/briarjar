package org.briarjar.briarjar.model.exceptions;


public class GeneralException extends Exception {

	private String title;


	public GeneralException(String message)
	{
		super(message);
	}

	public GeneralException(String title, String message)
	{
		super(message);
		this.title = title;
	}

	public GeneralException(String title, String message, Throwable cause)
	{
		super(message, cause);
		this.title = title;
	}

	public GeneralException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GeneralException(Throwable cause)
	{
		super(cause);
	}






	public String getTitle()
	{
		return title;
	}

	@Override
	public String toString()
	{
		return  getClass().getSimpleName()                           +"; "+
		       (title        != null ? title        : "(no title)"  )+"; "+
		       (getMessage() != null ? getMessage() : "(no message)")+"; "+
			   "Preceding Cause: "+
		       (getCause()   != null ? getCause()   : "(none)"      );
	}
}
